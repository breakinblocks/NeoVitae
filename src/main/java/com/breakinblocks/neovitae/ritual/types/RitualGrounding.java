package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.will.WorldDemonWillHandler;

import java.util.List;
import java.util.function.Consumer;

/**
 * The Sinner's Burden - Prevents flight and grounds entities in the area.
 *
 * <p>Demon Will effects:
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

    private static final double MIN_WILL = 0.5;
    private static final double WILL_PER_ENTITY = 0.2;

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

        // Query demon will
        double rawWill = WorldDemonWillHandler.getCurrentWill(ctx.level(), masterPos, EnumWillType.DEFAULT);
        double corrosiveWill = WorldDemonWillHandler.getCurrentWill(ctx.level(), masterPos, EnumWillType.CORROSIVE);
        double destructiveWill = WorldDemonWillHandler.getCurrentWill(ctx.level(), masterPos, EnumWillType.DESTRUCTIVE);
        double vengefulWill = WorldDemonWillHandler.getCurrentWill(ctx.level(), masterPos, EnumWillType.VENGEFUL);
        double steadfastWill = WorldDemonWillHandler.getCurrentWill(ctx.level(), masterPos, EnumWillType.STEADFAST);

        boolean hasRawWill = rawWill >= MIN_WILL;
        boolean hasCorrosive = corrosiveWill >= MIN_WILL;
        boolean hasDestructive = destructiveWill >= MIN_WILL;
        boolean hasVengeful = vengefulWill >= MIN_WILL;
        boolean hasSteadfast = steadfastWill >= MIN_WILL;

        double rawUsed = 0;
        double corrosiveUsed = 0;
        double destructiveUsed = 0;
        double vengefulUsed = 0;
        double steadfastUsed = 0;

        int totalCost = 0;

        if (hasDestructive) {
            // DESTRUCTIVE: Heavy Heart on ALL living entities
            List<LivingEntity> entities = RitualHelper.getEntitiesInRange(ctx, this, GROUNDING_RANGE, LivingEntity.class,
                    entity -> entity.isAlive());

            for (LivingEntity entity : entities) {
                // Skip creative players
                if (entity instanceof Player player && player.isCreative()) continue;

                // Skip boss entities unless steadfast will is present
                if (!hasSteadfast && !entity.canChangeDimensions(ctx.level(), ctx.level())) continue;

                if ((destructiveWill - destructiveUsed) < WILL_PER_ENTITY) break;

                entity.addEffect(new MobEffectInstance(NVMobEffects.HEAVY_HEART, 100, 1, true, true));
                destructiveUsed += WILL_PER_ENTITY;
                if (hasSteadfast && !entity.canChangeDimensions(ctx.level(), ctx.level())) {
                    steadfastUsed += WILL_PER_ENTITY;
                }
                totalCost += refreshCost;
            }
        } else if (hasRawWill) {
            // RAW WILL: Player-only targeting with will-based effects
            List<Player> players = RitualHelper.getEntitiesInRange(ctx, this, GROUNDING_RANGE, Player.class,
                    player -> player.isAlive() && !player.isCreative() && !player.isSpectator());

            for (Player player : players) {
                if ((rawWill - rawUsed) < WILL_PER_ENTITY) break;

                // Effect priority: Corrosive > Vengeful > Default
                if (hasCorrosive && (corrosiveWill - corrosiveUsed) >= WILL_PER_ENTITY) {
                    // Corrosive: Suspended (floating)
                    player.addEffect(new MobEffectInstance(NVMobEffects.SUSPENDED, 20, 0, true, false));
                    corrosiveUsed += WILL_PER_ENTITY;
                } else if (hasVengeful && (vengefulWill - vengefulUsed) >= WILL_PER_ENTITY) {
                    // Vengeful: Levitation (amplifier 10 for strong upward force)
                    player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20, 10, true, false));
                    vengefulUsed += WILL_PER_ENTITY;
                } else {
                    // Default: Grounded + Gravity
                    player.addEffect(new MobEffectInstance(NVMobEffects.GROUNDED, 20, 0, true, false));
                    player.addEffect(new MobEffectInstance(NVMobEffects.GRAVITY, 20, 0, true, false));
                }

                rawUsed += WILL_PER_ENTITY;
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
            ctx.syphon(Math.min(totalCost, ctx.currentEssence()));
        }

        // Drain consumed will
        if (rawUsed > 0) {
            WorldDemonWillHandler.drainWillFromChunk(ctx.level(), masterPos, EnumWillType.DEFAULT, rawUsed);
        }
        if (corrosiveUsed > 0) {
            WorldDemonWillHandler.drainWillFromChunk(ctx.level(), masterPos, EnumWillType.CORROSIVE, corrosiveUsed);
        }
        if (destructiveUsed > 0) {
            WorldDemonWillHandler.drainWillFromChunk(ctx.level(), masterPos, EnumWillType.DESTRUCTIVE, destructiveUsed);
        }
        if (vengefulUsed > 0) {
            WorldDemonWillHandler.drainWillFromChunk(ctx.level(), masterPos, EnumWillType.VENGEFUL, vengefulUsed);
        }
        if (steadfastUsed > 0) {
            WorldDemonWillHandler.drainWillFromChunk(ctx.level(), masterPos, EnumWillType.STEADFAST, steadfastUsed);
        }
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
