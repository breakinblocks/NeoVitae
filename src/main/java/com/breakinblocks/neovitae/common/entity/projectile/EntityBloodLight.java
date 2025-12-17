package com.breakinblocks.neovitae.common.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import com.breakinblocks.neovitae.common.block.BloodLightBlock;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.entity.BMEntities;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.UUID;

/**
 * Blood Light projectile - places a blood light block where it lands.
 */
public class EntityBloodLight extends ThrowableProjectile {

    // Red color for particles
    private static final DustParticleOptions BLOOD_PARTICLE = new DustParticleOptions(new Vector3f(0.8f, 0.0f, 0.0f), 1.0f);

    private int maxTicksInAir = 600; // 30 seconds max flight time
    private UUID ownerUUID = null;

    public EntityBloodLight(EntityType<? extends EntityBloodLight> type, Level level) {
        super(type, level);
    }

    public EntityBloodLight(Level level, LivingEntity shooter) {
        super(BMEntities.BLOOD_LIGHT.get(), shooter, level);
        if (shooter != null) {
            this.ownerUUID = shooter.getUUID();
        }
    }

    public EntityBloodLight(Level level, double x, double y, double z) {
        super(BMEntities.BLOOD_LIGHT.get(), x, y, z, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // No additional synched data needed
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        if (!level().isClientSide()) {
            BlockPos hitPos = result.getBlockPos();
            BlockPos placePos = hitPos.relative(result.getDirection());

            // Try to place the blood light (with protection check)
            if (level().isEmptyBlock(placePos) || level().getBlockState(placePos).canBeReplaced()) {
                BlockState lightState = BMBlocks.BLOOD_LIGHT.get().defaultBlockState()
                        .setValue(BloodLightBlock.LIFESPAN, BloodLightBlock.DEFAULT_LIFESPAN);
                BlockProtectionHelper.tryPlaceBlock(level(), placePos, lightState, ownerUUID);
            }

            discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        // If we hit an entity, try to place light at current position (with protection check)
        if (result.getType() == HitResult.Type.ENTITY && !level().isClientSide()) {
            BlockPos placePos = blockPosition();
            if (level().isEmptyBlock(placePos) || level().getBlockState(placePos).canBeReplaced()) {
                BlockState lightState = BMBlocks.BLOOD_LIGHT.get().defaultBlockState()
                        .setValue(BloodLightBlock.LIFESPAN, BloodLightBlock.DEFAULT_LIFESPAN);
                BlockProtectionHelper.tryPlaceBlock(level(), placePos, lightState, ownerUUID);
            }
            discard();
        }
    }

    @Override
    public void tick() {
        super.tick();

        // Check if we've been in the air too long
        if (tickCount > maxTicksInAir) {
            if (!level().isClientSide()) {
                // Place light at current position before expiring (with protection check)
                BlockPos placePos = blockPosition();
                if (level().isEmptyBlock(placePos) || level().getBlockState(placePos).canBeReplaced()) {
                    BlockState lightState = BMBlocks.BLOOD_LIGHT.get().defaultBlockState()
                            .setValue(BloodLightBlock.LIFESPAN, BloodLightBlock.DEFAULT_LIFESPAN);
                    BlockProtectionHelper.tryPlaceBlock(level(), placePos, lightState, ownerUUID);
                }
            }
            discard();
            return;
        }

        // Spawn trailing particles
        if (level().isClientSide()) {
            Vec3 motion = getDeltaMovement();
            for (int i = 0; i < 3; i++) {
                double offsetX = (random.nextDouble() - 0.5) * 0.1;
                double offsetY = (random.nextDouble() - 0.5) * 0.1;
                double offsetZ = (random.nextDouble() - 0.5) * 0.1;
                level().addParticle(BLOOD_PARTICLE,
                        getX() + offsetX, getY() + offsetY, getZ() + offsetZ,
                        -motion.x * 0.1, -motion.y * 0.1, -motion.z * 0.1);
            }
        }

        // Slow down over time (float gently)
        if (!isNoGravity()) {
            Vec3 motion = getDeltaMovement();
            setDeltaMovement(motion.x * 0.99, motion.y * 0.99, motion.z * 0.99);
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0; // No gravity - blood light floats in place
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("maxTicksInAir", maxTicksInAir);
        if (ownerUUID != null) {
            tag.putUUID("ownerUUID", ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("maxTicksInAir")) {
            maxTicksInAir = tag.getInt("maxTicksInAir");
        }
        if (tag.hasUUID("ownerUUID")) {
            ownerUUID = tag.getUUID("ownerUUID");
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 4096;
    }
}
