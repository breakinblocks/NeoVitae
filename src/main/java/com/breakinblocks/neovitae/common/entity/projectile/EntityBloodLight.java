package com.breakinblocks.neovitae.common.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
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
import com.breakinblocks.neovitae.common.block.BloodLightBlock;
import com.breakinblocks.neovitae.util.helper.ColorHelper;
import com.breakinblocks.neovitae.common.entity.NVEntities;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;
import com.breakinblocks.neovitae.util.helper.BloodLightHelper;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.tags.FluidTags;

public class EntityBloodLight extends ThrowableProjectile {

    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(EntityBloodLight.class, EntityDataSerializers.INT);

    private int maxTicksInAir = 600;
    private UUID ownerUUID = null;
    private int brightness = BloodLightBlock.DEFAULT_BRIGHTNESS;
    private DyeColor color = DyeColor.RED;

    public EntityBloodLight(EntityType<? extends EntityBloodLight> type, Level level) {
        super(type, level);
    }

    public EntityBloodLight(Level level, LivingEntity shooter) {
        super(NVEntities.BLOOD_LIGHT.get(), shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ(), level);
        setOwner(shooter);
        if (shooter != null) {
            this.ownerUUID = shooter.getUUID();
        }
    }

    public EntityBloodLight(Level level, LivingEntity shooter, int brightness, DyeColor color) {
        this(level, shooter);
        this.brightness = brightness;
        this.color = color;
        this.entityData.set(DATA_COLOR, color.getId());
    }

    public EntityBloodLight(Level level, double x, double y, double z) {
        super(NVEntities.BLOOD_LIGHT.get(), x, y, z, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_COLOR, DyeColor.RED.getId());
    }

    private void placeLight(BlockPos placePos) {
        BlockState existing = level().getBlockState(placePos);
        if (level().isEmptyBlock(placePos) || existing.canBeReplaced()) {
            boolean waterlogged = existing.getFluidState().is(FluidTags.WATER);
            BlockState lightState = BloodLightHelper.createBlockState(brightness, waterlogged);
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
            int packedColor = ColorHelper.fromDye(DyeColor.byId(this.entityData.get(DATA_COLOR)));
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
    @SuppressWarnings("deprecation")
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    // @Override (removed: not an override in 26.1)
    protected boolean updateInWaterStateAndDoFluidPushing() {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("maxTicksInAir", maxTicksInAir);
        tag.putInt("Brightness", brightness);
        tag.putInt("Color", color.getId());
        if (ownerUUID != null) {
            tag.store("ownerUUID", UUIDUtil.CODEC, ownerUUID);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
        maxTicksInAir = tag.getIntOr("maxTicksInAir", maxTicksInAir);
        brightness = tag.getIntOr("Brightness", brightness);
        color = DyeColor.byId(tag.getIntOr("Color", color.getId()));
        this.entityData.set(DATA_COLOR, color.getId());
        ownerUUID = tag.read("ownerUUID", UUIDUtil.CODEC).orElse(null);
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
