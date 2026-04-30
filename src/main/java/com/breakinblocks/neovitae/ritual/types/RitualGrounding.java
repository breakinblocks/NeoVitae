package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.api.will.SpiritusState;

import java.util.List;
import java.util.function.Consumer;

/**
 * The Sinner's Burden - Prevents flight and grounds entities in the area.
 *
 * <p>Spiritus effects:
 * <ul>
 *   <li><b>Raw (Default)</b> - Player-only targeting with Grounded + Gravity effects</li>
 *   <li><b>Destructive</b> - Apply Heavy Heart to ALL entities (including non-players), amplifier 1</li>
 *   <li><b>Corrosive</b> - Apply Suspended (floating) instead of grounding</li>
 *   <li><b>Vengeful</b> - Apply Levitation (amplifier 10) instead of grounding</li>
 *   <li><b>Steadfast</b> - Include boss entities (normally skipped)</li>
 * </ul>
 *
 * <p>Effect priority: Corrosive (Suspended) > Vengeful (Levitation) > Default (Grounded + Gravity)</p>
 */
public class RitualGrounding extends Ritual {

    public static final String GROUNDING_RANGE = "groundingRange";

    private static final double MIN_SPIRITUS = 0.5;
    private static final double SPIRITUS_PER_ENTITY = 0.2;

    public RitualGrounding() {
        super("grounding", 0, 2000, "ritual." + NeoVitae.MODID + ".grounding");
        addBlockRange(GROUNDING_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-15, -15, -15), 31, 31, 31));
        setMaximumVolumeAndDistanceOfRange(GROUNDING_RANGE, 0, 30, 30);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        // Volume-based cost: max(1, volume / 10000)
        AABB aabb = getBlockRange(GROUNDING_RANGE).getAABB(masterRitualStone.getBlockPos());
        int volume = (int) (aabb.getXsize() * aabb.getYsize() * aabb.getZsize());
        int refreshCost = Math.max(1, volume / 10000);

        RitualContext ctx = RitualHelper.createContext(masterRitualStone, refreshCost);
        if (ctx == null) {
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            return;
        }

        BlockPos masterPos = ctx.masterPos();

        SpiritusState will = RitualHelper.queryWill(ctx.level(), masterPos, MIN_SPIRITUS);

        double rawUsed = 0;
        double corrosiveUsed = 0;
        double destructiveUsed = 0;
        double vengefulUsed = 0;
        double steadfastUsed = 0;

        int totalCost = 0;

        if (will.hasDestructive()) {
            // DESTRUCTIVE: Heavy Heart on ALL living entities
            List<LivingEntity> entities = RitualHelper.getAliveLivingEntities(ctx, this, GROUNDING_RANGE);
            boolean hasSteadfast = will.hasSteadfast();

            for (LivingEntity entity : entities) {
                if (entity instanceof Player player && player.isCreative()) continue;

                boolean isBoss = entity.getType().getTags().anyMatch(t -> t.equals(NVTags.Entities.RITUAL_BOSS_BLACKLIST));
                if (isBoss && !hasSteadfast) continue;

                if ((will.getDestructive() - destructiveUsed) < SPIRITUS_PER_ENTITY) break;

                entity.addEffect(new MobEffectInstance(NVMobEffects.HEAVY_HEART, 100, 1, true, true));
                destructiveUsed += SPIRITUS_PER_ENTITY;
                if (isBoss) {
                    steadfastUsed += SPIRITUS_PER_ENTITY;
                }
                totalCost += refreshCost;
                RitualHelper.chanceStream(ctx.level(), 15, () ->
                        StreamPresets.voidTendril(masterPos, entity.blockPosition()).build()
                                .sendToNearby(ctx.serverLevel(), masterPos, 128));
            }
        } else if (will.hasDefault()) {
            // RAW WILL: Player-only targeting with will-based effects
            List<Player> players = RitualHelper.getEntitiesInRange(ctx, this, GROUNDING_RANGE, Player.class,
                    player -> player.isAlive() && !player.isCreative() && !player.isSpectator());

            for (Player player : players) {
                if ((will.getDefault() - rawUsed) < SPIRITUS_PER_ENTITY) break;

                // Effect priority: Corrosive > Vengeful > Default
                if (will.hasCorrosive() && (will.getCorrosive() - corrosiveUsed) >= SPIRITUS_PER_ENTITY) {
                    // Corrosive: Suspended (floating)
                    player.addEffect(new MobEffectInstance(NVMobEffects.SUSPENDED, 20, 0, true, false));
                    corrosiveUsed += SPIRITUS_PER_ENTITY;
                } else if (will.hasVengeful() && (will.getVengeful() - vengefulUsed) >= SPIRITUS_PER_ENTITY) {
                    // Vengeful: Levitation (amplifier 10 for strong upward force)
                    player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20, 10, true, false));
                    vengefulUsed += SPIRITUS_PER_ENTITY;
                } else {
                    // Default: Grounded + Gravity
                    player.addEffect(new MobEffectInstance(NVMobEffects.GROUNDED, 20, 0, true, false));
                    player.addEffect(new MobEffectInstance(NVMobEffects.GRAVITY, 20, 0, true, false));
                }

                rawUsed += SPIRITUS_PER_ENTITY;
                totalCost += refreshCost;
            }
        } else {
            // NO WILL: Basic Heavy Heart on non-owner players (original behavior)
            List<Player> players = RitualHelper.getEntitiesInRange(ctx, this, GROUNDING_RANGE, Player.class);

            for (Player player : players) {
                if (player.getUUID().equals(ctx.master().getOwner()) ||
                        player.isCreative() || player.isSpectator()) {
                    continue;
                }

                player.addEffect(new MobEffectInstance(NVMobEffects.HEAVY_HEART, 30, 0, true, true));
                totalCost += refreshCost;
            }
        }

        if (totalCost > 0) {
            ctx.syphon(Math.min(totalCost, ctx.currentEV()));
        }

        RitualHelper.drainSpiritus(will, ctx.level(), masterPos,
                rawUsed, corrosiveUsed, destructiveUsed, vengefulUsed, steadfastUsed);
    }

    @Override
    public int getRefreshTime() {
        return 1;
    }

    @Override
    public int getRefreshCost() {
        return 10;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 3, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 3, 0, EnumRuneType.EARTH);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualGrounding();
    }
}
