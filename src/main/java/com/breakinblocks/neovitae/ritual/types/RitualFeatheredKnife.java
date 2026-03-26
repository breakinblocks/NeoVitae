package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.damagesource.NVDamageSources;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.common.living.LivingHelper;
import com.breakinblocks.neovitae.incense.IncenseHelper;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.api.will.SpiritusState;
import com.breakinblocks.neovitae.util.AltarUtil;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual of the Feathered Knife - damages nearby players to generate LP for a Ara Vitae.
 *
 * <p>Spiritus effects:
 * <ul>
 *   <li><b>Raw (Default)</b> - Reduces refresh time to 10 ticks, lowers health threshold to 30%</li>
 *   <li><b>Steadfast</b> - Raises health threshold to 70% (safer for players)</li>
 *   <li><b>Vengeful</b> - Lowers health threshold to 10% (more aggressive)</li>
 *   <li><b>Corrosive</b> - Incense mode: consumes incense for massive LP, applies Soul Fray</li>
 *   <li><b>Destructive</b> - LP multiplier based on will amount</li>
 * </ul>
 */
public class RitualFeatheredKnife extends Ritual {

    public static final String SACRIFICE_RANGE = "sacrificeRange";
    public static final String ALTAR_RANGE = "altarRange";

    private static final double MIN_WILL = 0.5;
    private static final double CORROSIVE_WILL_PER_USE = 5.0;
    private static final double DESTRUCTIVE_WILL_PER_USE = 0.5;

    /** Cached altar offset (relative to master pos). Persisted in NBT. */
    private BlockPos altarOffsetPos = null;

    /** Dynamic refresh time — 20 normally, 10 with raw Spiritus. */
    private int refreshTime = 20;

    public RitualFeatheredKnife() {
        super("feathered_knife", 0, 25000, "ritual." + NeoVitae.MODID + ".feathered_knife");
        addBlockRange(SACRIFICE_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -5, -5), 11, 11, 11));
        addBlockRange(ALTAR_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -10, -5), 11, 20, 11));
        setMaximumVolumeAndDistanceOfRange(SACRIFICE_RANGE, 0, 10, 10);
        setMaximumVolumeAndDistanceOfRange(ALTAR_RANGE, 0, 15, 15);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        BlockPos masterPos = ctx.masterPos();

        SpiritusState will = RitualHelper.queryWill(ctx.level(), masterPos, MIN_WILL);

        refreshTime = will.hasDefault() ? 10 : 20;

        // Find the altar (use cached position first, fallback to search)
        AraVitaeTile altar = findAltar(ctx);
        if (altar == null) return;

        // Determine health threshold based on will types
        // Steadfast = safer (keep 70% health), Vengeful = aggressive (keep only 10%)
        // Default without will: keep 30%
        float healthThreshold;
        if (will.hasSteadfast()) {
            healthThreshold = 0.7F;
        } else if (will.hasVengeful()) {
            healthThreshold = 0.1F;
        } else if (will.hasDefault()) {
            healthThreshold = 0.3F;
        } else {
            healthThreshold = 0.3F;
        }

        // LP multiplier from destructive will
        double lpMultiplier = 1.0;
        if (will.hasDestructive()) {
            lpMultiplier = 1.0 + will.getDestructive() * 0.2 / 100.0;
        }

        List<Player> players = RitualHelper.getEntitiesInRange(ctx, this, SACRIFICE_RANGE, Player.class,
                player -> player.isAlive() && !player.isCreative() && !player.isSpectator());

        int totalEV = 0;
        double corrosiveUsed = 0;
        double destructiveUsed = 0;

        for (Player player : players) {
            float health = player.getHealth();
            float maxHealth = player.getMaxHealth();
            float threshold = maxHealth * healthThreshold;

            if (health <= threshold) continue;

            // CORROSIVE: Incense mode — consume accumulated incense for massive self-sacrifice
            if (will.hasCorrosive() && (will.getCorrosive() - corrosiveUsed) >= CORROSIVE_WILL_PER_USE) {
                double incense = IncenseHelper.getCurrentIncense(player);
                if (incense > 0) {
                    // Sacrifice more health (down to threshold)
                    float damage = health - threshold;
                    if (damage > 0) {
                        player.hurt(ctx.level().damageSources().source(NVDamageSources.SELF_SACRIFICE, player), damage);
                        if (player.getHealth() < health) {
                            int healthLost = (int) (health - player.getHealth());
                            int lp = AltarUtil.calculateSelfSacrificeLP(player, healthLost, incense);
                            lp = (int) (lp * lpMultiplier);
                            totalEV += lp;
                        }
                        // Apply Soul Fray to prevent immediate re-sacrifice with incense
                        player.addEffect(new MobEffectInstance(NVMobEffects.SOUL_FRAY, 400, 0, true, true));
                        IncenseHelper.clearIncense(player);
                        corrosiveUsed += CORROSIVE_WILL_PER_USE;
                    }
                    continue;
                }
            }

            // Normal sacrifice — take 1 HP down to threshold
            float damage = Math.min(1.0F, health - threshold);
            if (damage <= 0) continue;

            player.hurt(ctx.level().damageSources().source(NVDamageSources.SELF_SACRIFICE, player), damage);

            if (player.getHealth() < health) {
                int healthLost = (int) Math.ceil(health - player.getHealth());
                int lp = AltarUtil.calculateSelfSacrificeLP(player, healthLost);

                // Living armor self-sacrifice bonus
                if (LivingHelper.hasFullSet(player)) {
                    lp = (int) (lp * 1.1);
                }

                // Destructive will LP multiplier
                if (will.hasDestructive() && (will.getDestructive() - destructiveUsed) >= DESTRUCTIVE_WILL_PER_USE) {
                    lp = (int) (lp * lpMultiplier);
                    destructiveUsed += DESTRUCTIVE_WILL_PER_USE;
                }

                totalEV += lp;
            }
        }

        if (totalEV > 0) {
            ctx.syphon(getRefreshCost());
            // Feed LP directly to the altar
            altar.addSacrificeEV(totalEV, false);
        }

        will.use(SpiritusType.CORROSIVE, corrosiveUsed);
        will.use(SpiritusType.DESTRUCTIVE, destructiveUsed);
        will.drain(ctx.level(), masterPos);
    }

    private AraVitaeTile findAltar(RitualContext ctx) {
        RitualHelper.AltarSearchResult result = RitualHelper.findAltar(ctx, this, ALTAR_RANGE, altarOffsetPos);
        altarOffsetPos = result.offset();
        return result.altar();
    }

    @Override
    public int getRefreshTime() {
        return refreshTime;
    }

    @Override
    public int getRefreshCost() {
        return 20;
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        altarOffsetPos = RitualHelper.readAltarOffset(tag);
        refreshTime = tag.getInt("refreshTime");
        if (refreshTime == 0) refreshTime = 20;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        RitualHelper.writeAltarOffset(tag, altarOffsetPos);
        tag.putInt("refreshTime", refreshTime);
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addParallelRunes(components, 1, 0, EnumRuneType.DUSK);
        addCornerRunes(components, 1, 0, EnumRuneType.AIR);
        addParallelRunes(components, 2, 0, EnumRuneType.AIR);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualFeatheredKnife();
    }
}
