// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.api.spiritus.SpiritusState;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Ritual of Lava - generates lava in a configurable area, or fills a fluid tank
 * placed directly above the Master Ritual Stone with one bucket of lava per cycle.
 *
 * <p>Tank-fill mode is automatic: if a fluid handler is detected within the tank
 * range (the block above the master stone by default), the ritual pipes lava into
 * it instead of placing source blocks in the world. When the tank has no room for
 * another bucket, the ritual enters an exponential backoff, skipping an increasing
 * number of invocations to avoid repeatedly probing a full container. A successful
 * fill immediately resets the backoff to the ritual's normal cadence.
 *
 * <p>Spiritus effects:
 * <ul>
 *   <li><b>Raw (Default)</b> - Reduced EV cost for lava placement and tank filling</li>
 *   <li><b>Vengeful</b> - Apply Fire Fuse to non-player mobs in fire range</li>
 *   <li><b>Steadfast</b> - Apply Fire Resistance to players in fire range</li>
 *   <li><b>Corrosive</b> - Deal fire damage to entities (skip fire-immune) in fire range</li>
 * </ul>
 */
public class RitualLava extends Ritual {

    public static final String LAVA_RANGE = "lavaRange";
    public static final String TANK_RANGE = "tankRange";
    public static final String FIRE_RANGE = "fireRange";

    private static final double MIN_SPIRITUS = 0.5;
    private static final double VENGEFUL_WILL_PER_MOB = 1.0;
    private static final double CORROSIVE_WILL_PER_HIT = 1.0;
    private static final double STEADFAST_WILL_PER_PLAYER = 0.5;
    private static final int BASE_LAVA_COST = 500;
    private static final float CORROSIVE_FIRE_DAMAGE = 2.0F;

    // Backoff configuration: number of performRitual invocations to skip per level.
    // Level 0 = normal cadence (no skip). Capped so full-tank polling stays cheap.
    private static final int MAX_BACKOFF_LEVEL = 5;
    private static final int[] BACKOFF_SKIPS = {0, 1, 3, 7, 15, 31};

    // Persisted backoff state (saved with the ritual via read/writeToNBT).
    private int tankBackoffLevel = 0;
    private int tankBackoffRemaining = 0;

    public RitualLava() {
        super("lava", 0, 10000, "ritual." + NeoVitae.MODID + ".lava");
        addBlockRange(LAVA_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        addBlockRange(TANK_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        addBlockRange(FIRE_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -5, -5), 11, 11, 11));

        setMaximumVolumeAndDistanceOfRange(LAVA_RANGE, 9, 3, 3);
        setMaximumVolumeAndDistanceOfRange(TANK_RANGE, 1, 10, 10);
        setMaximumVolumeAndDistanceOfRange(FIRE_RANGE, 0, 10, 10);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        BlockPos masterPos = ctx.masterPos();
        UUID owner = ctx.master().getOwner();

        SpiritusState will = RitualHelper.querySpiritus(ctx.level(), masterPos, MIN_SPIRITUS);

        // EV cost per operation: cheaper with more raw Spiritus.
        int lavaCost = will.hasDefault() ? Math.max(1, BASE_LAVA_COST - (int) will.getDefault()) : BASE_LAVA_COST;

        int maxEffects = ctx.maxOperations(lavaCost);
        int totalEffects = 0;

        double rawUsed = 0;
        double corrosiveUsed = 0;
        double vengefulUsed = 0;
        double steadfastUsed = 0;

        // --- TANK / LAVA PLACEMENT ---
        // If a fluid handler is present in the tank range, switch to tank-fill mode.
        // Otherwise fall back to placing lava source blocks in the lava range.
        boolean tankPresent = false;

        if (tankBackoffRemaining > 0) {
            // Currently backing off: don't probe the tank this cycle, but remember
            // that tank mode is active so we skip world placement.
            tankBackoffRemaining--;
            tankPresent = true;
        } else {
            List<BlockPos> tankPositions = RitualHelper.getRangePositions(ctx.master(), this, TANK_RANGE, masterPos);
            IFluidHandler availableTank = null;
            for (BlockPos tankPos : tankPositions) {
                IFluidHandler handler = ctx.level().getCapability(
                        Capabilities.FluidHandler.BLOCK, tankPos, null);
                if (handler != null) {
                    tankPresent = true;
                    FluidStack lavaStack = new FluidStack(Fluids.LAVA, 1000);
                    int simulated = handler.fill(lavaStack, IFluidHandler.FluidAction.SIMULATE);
                    if (simulated >= 1000) {
                        availableTank = handler;
                        break;
                    }
                }
            }

            if (availableTank != null && totalEffects < maxEffects) {
                availableTank.fill(new FluidStack(Fluids.LAVA, 1000), IFluidHandler.FluidAction.EXECUTE);
                totalEffects++;
                // Successful fill: return to normal cadence.
                tankBackoffLevel = 0;
                tankBackoffRemaining = 0;
            } else if (tankPresent) {
                // Tank exists but is full (or maxEffects exhausted). Back off so we
                // don't keep probing the capability every cycle.
                if (tankBackoffLevel < MAX_BACKOFF_LEVEL) {
                    tankBackoffLevel++;
                }
                tankBackoffRemaining = BACKOFF_SKIPS[tankBackoffLevel];
            }
        }

        // Only place lava in the world when no tank is present at all.
        if (!tankPresent) {
            List<BlockPos> positions = RitualHelper.getRangePositions(ctx.master(), this, LAVA_RANGE, masterPos);
            for (BlockPos pos : positions) {
                if (totalEffects >= maxEffects) break;

                if (ctx.level().isEmptyBlock(pos)) {
                    if (BlockProtectionHelper.tryPlaceBlock(ctx.level(), pos, Blocks.LAVA.defaultBlockState(), owner)) {
                        totalEffects++;
                    }
                }
            }
        }

        // --- FIRE EFFECTS: Apply effects to entities in fire range ---

        // Vengeful: Fire Fuse on non-player mobs
        if (will.hasVengeful()) {
            List<LivingEntity> mobs = RitualHelper.getAliveMobsInRange(ctx, this, FIRE_RANGE);
            for (LivingEntity mob : mobs) {
                if ((will.getVengeful() - vengefulUsed) < VENGEFUL_WILL_PER_MOB) break;
                mob.addEffect(new MobEffectInstance(NVMobEffects.FIRE_FUSE, 100, 0, true, true));
                vengefulUsed += VENGEFUL_WILL_PER_MOB;
            }
        }

        // Steadfast: Fire Resistance on players
        if (will.hasSteadfast()) {
            List<Player> players = RitualHelper.getAlivePlayersInRange(ctx, this, FIRE_RANGE);
            for (Player player : players) {
                if ((will.getSteadfast() - steadfastUsed) < STEADFAST_WILL_PER_PLAYER) break;
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0, true, false));
                steadfastUsed += STEADFAST_WILL_PER_PLAYER;
            }
        }

        // Corrosive: Fire damage to entities (skip fire-immune)
        if (will.hasCorrosive()) {
            List<LivingEntity> entities = RitualHelper.getEntitiesInRange(ctx, this, FIRE_RANGE, LivingEntity.class,
                    entity -> entity.isAlive() && !entity.fireImmune());
            for (LivingEntity entity : entities) {
                if ((will.getCorrosive() - corrosiveUsed) < CORROSIVE_WILL_PER_HIT) break;
                entity.hurt(ctx.level().damageSources().onFire(), CORROSIVE_FIRE_DAMAGE);
                entity.setRemainingFireTicks(60);
                corrosiveUsed += CORROSIVE_WILL_PER_HIT;
            }
        }

        if (totalEffects > 0) {
            ctx.syphon(lavaCost * totalEffects);
        }

        RitualHelper.drainSpiritus(will, ctx.level(), masterPos,
                rawUsed, corrosiveUsed, 0, vengefulUsed, steadfastUsed);
    }



    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualLava();
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        tankBackoffLevel = Math.min(Math.max(tag.getInt("tankBackoffLevel"), 0), MAX_BACKOFF_LEVEL);
        tankBackoffRemaining = Math.max(tag.getInt("tankBackoffRemaining"), 0);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.putInt("tankBackoffLevel", tankBackoffLevel);
        tag.putInt("tankBackoffRemaining", tankBackoffRemaining);
    }
}
