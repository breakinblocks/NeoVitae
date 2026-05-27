package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.common.network.NVPayloads;
import com.breakinblocks.neovitae.common.network.SetClientVelocityPayload;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.api.will.SpiritusState;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.entity.player.Player;

/**
 * Ritual of Speed - propels entities in the direction the master ritual stone faces.
 *
 * <p>Spiritus effects:
 * <ul>
 *   <li><b>Raw (Default)</b> - Increased speed scaling: horizontal=3+rawSpiritus/40, vertical=1.2+rawSpiritus/200</li>
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

    private static final double MIN_SPIRITUS = 0.5;
    private static final double SPIRITUS_PER_ENTITY = 0.1;

    private static final int SPEED_BUFF_DURATION = 36000;
    private static final int SPEED_BUFF_AMPLIFIER = 1;
    private static final int SPEED_BUFF_REFRESH_THRESHOLD = 200;

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

        SpiritusState will = RitualHelper.queryWill(ctx.level(), masterPos, MIN_SPIRITUS);

        boolean hasRawWill = will.hasDefault();
        boolean hasCorrosive = will.hasCorrosive();
        boolean hasDestructive = will.hasDestructive();
        boolean hasVengeful = will.hasVengeful();
        boolean hasSteadfast = will.hasSteadfast();

        double horizontalSpeed;
        double verticalSpeed;
        if (hasRawWill) {
            horizontalSpeed = 3.0 + will.getDefault() / 40.0;
            verticalSpeed = 1.2 + will.getDefault() / 200.0;
        } else {
            horizontalSpeed = 1.5;
            verticalSpeed = 1.0;
        }

        // Corrosive: additional horizontal speed
        if (hasCorrosive) {
            horizontalSpeed += will.getCorrosive() / 40.0;
        }

        List<LivingEntity> entities = RitualHelper.getEntitiesInRange(ctx, this, SPEED_RANGE, LivingEntity.class,
                entity -> entity.isAlive()
                        && !(entity instanceof net.minecraft.world.entity.player.Player player && player.isSpectator()));

        int cost = 0;

        for (LivingEntity entity : entities) {
            if (cost + getRefreshCost() > ctx.currentEV()) break;

            if (entity.isShiftKeyDown()) {
                MobEffectInstance existing = entity.getEffect(MobEffects.MOVEMENT_SPEED);
                if (existing == null
                        || existing.getAmplifier() < SPEED_BUFF_AMPLIFIER
                        || existing.getDuration() < SPEED_BUFF_REFRESH_THRESHOLD) {
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,
                            SPEED_BUFF_DURATION, SPEED_BUFF_AMPLIFIER, true, false));
                    cost += getRefreshCost();
                    if (hasRawWill) will.use(SpiritusType.RAW, SPIRITUS_PER_ENTITY);
                }
                continue;
            }

            // Entity filtering based on destructive/vengeful will
            boolean isBaby = entity.isBaby();
            boolean isPlayer = entity instanceof Player;

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

            entity.setDeltaMovement(motionX, motionY, motionZ);
            entity.hurtMarked = true;
            entity.fallDistance = 0;

            RitualHelper.chanceStream(ctx.level(), 15, () ->
                    StreamPresets.arcaneBolt(ctx.masterPos(), entity.blockPosition()).build()
                            .sendToNearby(ctx.serverLevel(), ctx.masterPos(), 32));

            // For server players, send velocity payload for client sync
            if (entity instanceof ServerPlayer serverPlayer) {
                NVPayloads.sendToPlayer(serverPlayer, new SetClientVelocityPayload(motionX, motionY, motionZ));
            }

            // Steadfast: apply Soft Fall
            if (hasSteadfast) {
                entity.addEffect(new MobEffectInstance(NVMobEffects.SOFT_FALL, 100, 0, true, false));
                will.use(SpiritusType.INVICTUS, SPIRITUS_PER_ENTITY);
            }

            cost += getRefreshCost();

            if (hasRawWill) will.use(SpiritusType.RAW, SPIRITUS_PER_ENTITY);
            if (hasCorrosive) will.use(SpiritusType.RUINA, SPIRITUS_PER_ENTITY);
            if (hasDestructive) will.use(SpiritusType.NIHILUM, SPIRITUS_PER_ENTITY);
            if (hasVengeful) will.use(SpiritusType.VINDICTA, SPIRITUS_PER_ENTITY);
        }

        if (cost > 0) {
            ctx.syphon(cost);
        }

        will.drain(ctx.level(), masterPos);
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
