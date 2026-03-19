package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.common.network.NVPayloads;
import com.breakinblocks.neovitae.common.network.SetClientVelocityPayload;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.will.WorldDemonWillHandler;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual of Speed - propels entities in the direction the master ritual stone faces.
 *
 * <p>Demon Will effects:
 * <ul>
 *   <li><b>Raw (Default)</b> - Increased speed scaling: horizontal=3+rawWill/40, vertical=1.2+rawWill/200</li>
 *   <li><b>Corrosive</b> - Additional horizontal speed bonus: corrosiveWill/40</li>
 *   <li><b>Destructive</b> - Only transport baby entities (skip adults)</li>
 *   <li><b>Vengeful</b> - Only transport adult entities (skip babies)</li>
 *   <li><b>Steadfast</b> - Apply Soft Fall to launched entities</li>
 * </ul>
 *
 * <p>If both destructive and vengeful are present, only players pass through
 * (they are neither "baby" nor "adult" in mob terms).</p>
 */
public class RitualSpeed extends Ritual {

    public static final String SPEED_RANGE = "speedRange";

    private static final double MIN_WILL = 0.5;
    private static final double WILL_PER_ENTITY = 0.1;

    public RitualSpeed() {
        super("speed", 0, 500, "ritual." + NeoVitae.MODID + ".speed");
        addBlockRange(SPEED_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -5, -5), 11, 11, 11));
        setMaximumVolumeAndDistanceOfRange(SPEED_RANGE, 2000, 15, 15);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        BlockPos masterPos = ctx.masterPos();
        Direction facing = masterRitualStone.getDirection();

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

        // Calculate speed based on will
        double horizontalSpeed;
        double verticalSpeed;
        if (hasRawWill) {
            horizontalSpeed = 3.0 + rawWill / 40.0;
            verticalSpeed = 1.2 + rawWill / 200.0;
        } else {
            horizontalSpeed = 1.5;
            verticalSpeed = 1.0;
        }

        // Corrosive: additional horizontal speed
        if (hasCorrosive) {
            horizontalSpeed += corrosiveWill / 40.0;
        }

        List<LivingEntity> entities = RitualHelper.getEntitiesInRange(ctx, this, SPEED_RANGE, LivingEntity.class,
                entity -> entity.isAlive() && !entity.isShiftKeyDown());

        int cost = 0;
        double rawUsed = 0;
        double corrosiveUsed = 0;
        double destructiveUsed = 0;
        double vengefulUsed = 0;
        double steadfastUsed = 0;

        for (LivingEntity entity : entities) {
            if (cost + getRefreshCost() > ctx.currentEssence()) break;

            // Entity filtering based on destructive/vengeful will
            boolean isBaby = entity.isBaby();
            boolean isPlayer = entity instanceof net.minecraft.world.entity.player.Player;

            if (hasDestructive && hasVengeful) {
                // Both present: only players pass through (neither baby nor adult in mob terms)
                if (!isPlayer) continue;
            } else if (hasDestructive) {
                // Only transport baby entities
                if (!isBaby && !isPlayer) continue;
            } else if (hasVengeful) {
                // Only transport adult entities
                if (isBaby) continue;
            }

            // Calculate velocity based on direction
            double motionX = 0;
            double motionY = 0;
            double motionZ = 0;

            switch (facing) {
                case NORTH -> motionZ = -horizontalSpeed;
                case SOUTH -> motionZ = horizontalSpeed;
                case WEST -> motionX = -horizontalSpeed;
                case EAST -> motionX = horizontalSpeed;
                case UP -> motionY = verticalSpeed;
                case DOWN -> motionY = -verticalSpeed;
            }

            // For horizontal directions, add vertical lift
            if (facing.getAxis().isHorizontal()) {
                motionY = verticalSpeed * 0.5;
            }

            // Apply velocity
            entity.setDeltaMovement(motionX, motionY, motionZ);
            entity.hurtMarked = true;
            entity.fallDistance = 0;

            // For server players, send velocity payload for client sync
            if (entity instanceof ServerPlayer serverPlayer) {
                NVPayloads.sendToPlayer(serverPlayer, new SetClientVelocityPayload(motionX, motionY, motionZ));
            }

            // Steadfast: apply Soft Fall
            if (hasSteadfast) {
                entity.addEffect(new MobEffectInstance(NVMobEffects.SOFT_FALL, 100, 0, true, false));
                steadfastUsed += WILL_PER_ENTITY;
            }

            cost += getRefreshCost();

            // Track will usage
            if (hasRawWill) rawUsed += WILL_PER_ENTITY;
            if (hasCorrosive) corrosiveUsed += WILL_PER_ENTITY;
            if (hasDestructive) destructiveUsed += WILL_PER_ENTITY;
            if (hasVengeful) vengefulUsed += WILL_PER_ENTITY;
        }

        if (cost > 0) {
            ctx.syphon(cost);
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
        return 5;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.AIR);
        addParallelRunes(components, 2, 0, EnumRuneType.AIR);
        addCornerRunes(components, 2, 0, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualSpeed();
    }
}
