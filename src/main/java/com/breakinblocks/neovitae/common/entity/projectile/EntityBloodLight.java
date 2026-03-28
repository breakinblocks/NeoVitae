package com.breakinblocks.neovitae.common.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.util.helper.ColorHelper;
import com.breakinblocks.neovitae.common.entity.NVEntities;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;
import com.breakinblocks.neovitae.util.helper.BloodLightHelper;

import java.util.UUID;

public class EntityBloodLight extends ThrowableProjectile {

    private int maxTicksInAir = 600;
    private UUID ownerUUID = null;
    private int brightness = com.breakinblocks.neovitae.common.block.BloodLightBlock.DEFAULT_BRIGHTNESS;
    private DyeColor color = DyeColor.RED;

    public EntityBloodLight(EntityType<? extends EntityBloodLight> type, Level level) {
        super(type, level);
    }

    public EntityBloodLight(Level level, LivingEntity shooter) {
        super(NVEntities.BLOOD_LIGHT.get(), shooter, level);
        if (shooter != null) {
            this.ownerUUID = shooter.getUUID();
        }
    }

    public EntityBloodLight(Level level, LivingEntity shooter, int brightness, DyeColor color) {
        this(level, shooter);
        this.brightness = brightness;
        this.color = color;
    }

    public EntityBloodLight(Level level, double x, double y, double z) {
        super(NVEntities.BLOOD_LIGHT.get(), x, y, z, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    private void placeLight(BlockPos placePos) {
        if (level().isEmptyBlock(placePos) || level().getBlockState(placePos).canBeReplaced()) {
            BlockState lightState = BloodLightHelper.createBlockState(brightness);
            if (BlockProtectionHelper.tryPlaceBlock(level(), placePos, lightState, ownerUUID)) {
                BloodLightHelper.setBlockEntityColor(level(), placePos, color);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide()) {
            placeLight(result.getBlockPos().relative(result.getDirection()));
            discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (result.getType() == HitResult.Type.ENTITY && !level().isClientSide()) {
            placeLight(blockPosition());
            discard();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (tickCount > maxTicksInAir) {
            if (!level().isClientSide()) {
                placeLight(blockPosition());
            }
            discard();
            return;
        }

        if (level().isClientSide()) {
            int packedColor = ColorHelper.fromDye(color);
            Vec3 motion = getDeltaMovement();
            for (int i = 0; i < 3; i++) {
                double offsetX = (random.nextDouble() - 0.5) * 0.1;
                double offsetY = (random.nextDouble() - 0.5) * 0.1;
                double offsetZ = (random.nextDouble() - 0.5) * 0.1;
                level().addParticle(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), packedColor),
                        getX() + offsetX, getY() + offsetY, getZ() + offsetZ,
                        -motion.x * 0.1, -motion.y * 0.1, -motion.z * 0.1);
            }
        }

        if (!isNoGravity()) {
            Vec3 motion = getDeltaMovement();
            setDeltaMovement(motion.x * 0.99, motion.y * 0.99, motion.z * 0.99);
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("maxTicksInAir", maxTicksInAir);
        tag.putInt("Brightness", brightness);
        tag.putInt("Color", color.getId());
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
        if (tag.contains("Brightness")) {
            brightness = tag.getInt("Brightness");
        }
        if (tag.contains("Color")) {
            color = DyeColor.byId(tag.getInt("Color"));
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
