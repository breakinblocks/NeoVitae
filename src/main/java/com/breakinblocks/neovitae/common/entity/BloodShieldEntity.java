package com.breakinblocks.neovitae.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import com.breakinblocks.neovitae.api.NeoVitaeAPI;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.api.soul.IAnima;
import com.breakinblocks.neovitae.common.item.BloodOrbItem;

import javax.annotation.Nullable;
import java.util.UUID;

public class BloodShieldEntity extends Entity implements GeoEntity {

    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(BloodShieldEntity.class, EntityDataSerializers.INT);
    private static final float SHIELD_DISTANCE = 2.5f;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Nullable
    private UUID ownerUUID;

    public BloodShieldEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public BloodShieldEntity(Level level, Player owner) {
        this(NVEntities.BLOOD_SHIELD.get(), level);
        this.ownerUUID = owner.getUUID();
        this.entityData.set(OWNER_ID, owner.getId());
        updatePosition(owner);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER_ID, -1);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) {
            ownerUUID = tag.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (ownerUUID != null) {
            tag.putUUID("Owner", ownerUUID);
        }
    }

    @Nullable
    public Player getOwner() {
        int ownerId = entityData.get(OWNER_ID);
        if (ownerId >= 0) {
            Entity e = level().getEntity(ownerId);
            if (e instanceof Player p) return p;
        }
        return null;
    }

    public void updatePosition(Player owner) {
        float yawRad = (float) Math.toRadians(owner.getYRot());
        double dx = -Math.sin(yawRad) * SHIELD_DISTANCE;
        double dz = Math.cos(yawRad) * SHIELD_DISTANCE;
        double px = owner.getX() + dx;
        double py = owner.getY() + owner.getBbHeight() * 0.5;
        double pz = owner.getZ() + dz;
        this.setPos(px, py, pz);
        this.setYRot(owner.getYRot());
        this.setXRot(0);
    }

    public static void raise(Player player) {
        Level level = player.level();
        if (level.isClientSide()) return;
        boolean exists = !level.getEntitiesOfClass(BloodShieldEntity.class, player.getBoundingBox().inflate(8.0),
                e -> player.getUUID().equals(e.ownerUUID)).isEmpty();
        if (exists) return;
        level.addFreshEntity(new BloodShieldEntity(level, player));
    }

    @Override
    public void tick() {
        super.tick();
        Player owner = getOwner();
        if (owner == null || !owner.isAlive()
                || !BloodOrbItem.isShieldActive(owner)
                || !(owner.getOffhandItem().getItem() instanceof BloodOrbItem)) {
            discard();
            return;
        }
        if (!level().isClientSide() && owner.tickCount % 20 == 0) {
            IAnima network = NeoVitaeAPI.getInstance().getAnima(owner.getUUID());
            if (network == null || network.getCurrentEV() < BloodOrbItem.getShieldDrain()) {
                BloodOrbItem.setShieldActive(owner, false);
                discard();
                return;
            }
            network.syphon(AnimaTicket.create(BloodOrbItem.getShieldDrain()));
        }
        updatePosition(owner);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 128 * 128;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
