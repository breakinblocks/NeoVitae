package com.breakinblocks.neovitae.common.entity.projectile;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.breakinblocks.neovitae.common.attribute.NVAttributes;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.will.PlayerSpiritusHandler;
import net.minecraft.world.entity.monster.Enemy;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.core.component.DataComponents;

/**
 * Base class for throwing dagger projectiles.
 * Handles arrow-like behavior with potion effects and spiritus collection.
 */
public abstract class AbstractEntityThrowingDagger extends ThrowableItemProjectile implements ItemSupplier {

    private static final EntityDataAccessor<Integer> ID_EFFECT_COLOR = SynchedEntityData.defineId(
            AbstractEntityThrowingDagger.class, EntityDataSerializers.INT);

    @Nullable
    private BlockState inBlockState;
    protected boolean inGround;
    protected int timeInGround;
    public AbstractArrow.Pickup pickupStatus = AbstractArrow.Pickup.ALLOWED;
    public int arrowShake;
    private int ticksInGround;
    private double damage = 2.0D;
    private int knockbackStrength;
    private SoundEvent hitSound = this.getHitEntitySound();
    private IntOpenHashSet piercedEntities;
    private List<Entity> hitEntities;

    private double willDrop = 0;
    private SpiritusType spiritusType = SpiritusType.RAW;

    private final Set<MobEffectInstance> effects = Sets.newHashSet();

    public AbstractEntityThrowingDagger(EntityType<? extends AbstractEntityThrowingDagger> type, Level level) {
        super(type, level);
    }

    public AbstractEntityThrowingDagger(EntityType<? extends AbstractEntityThrowingDagger> type, ItemStack stack,
                                        Level level, LivingEntity thrower) {
        super(type, thrower, level, stack);
        if (thrower instanceof Player) {
            this.pickupStatus = AbstractArrow.Pickup.ALLOWED;
        }
    }

    public AbstractEntityThrowingDagger(EntityType<? extends AbstractEntityThrowingDagger> type, ItemStack stack,
                                        Level level, double x, double y, double z) {
        super(type, x, y, z, level, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return NVItems.THROWING_DAGGER.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_EFFECT_COLOR, -1);
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setWillDrop(double willDrop) {
        this.willDrop = willDrop;
    }

    public double getWillDropForMobHealth(double hp) {
        return this.willDrop * hp / 20D;
    }

    public void setWillType(SpiritusType type) {
        this.spiritusType = type;
    }

    public int getColor() {
        return this.entityData.get(ID_EFFECT_COLOR);
    }

    public void addEffect(MobEffectInstance effect) {
        this.effects.add(effect);
        updateColor();
    }

    public void setEffectsFromItem(ItemStack stack) {
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents != null) {
            for (MobEffectInstance effect : contents.getAllEffects()) {
                this.addEffect(new MobEffectInstance(effect));
            }
        }
    }

    private void updateColor() {
        if (this.effects.isEmpty()) {
            this.entityData.set(ID_EFFECT_COLOR, -1);
        } else {
            this.entityData.set(ID_EFFECT_COLOR, computeEffectColor(this.effects));
        }
    }

    private static int computeEffectColor(Collection<MobEffectInstance> effects) {
        float r = 0f, g = 0f, b = 0f;
        int total = 0;
        for (MobEffectInstance e : effects) {
            if (!e.isVisible()) continue;
            int color = e.getEffect().value().getColor();
            int amp = e.getAmplifier() + 1;
            r += ((color >> 16) & 0xFF) / 255.0F * amp;
            g += ((color >> 8) & 0xFF) / 255.0F * amp;
            b += (color & 0xFF) / 255.0F * amp;
            total += amp;
        }
        if (total == 0) return 0;
        int ri = Mth.floor(r / total * 255.0F);
        int gi = Mth.floor(g / total * 255.0F);
        int bi = Mth.floor(b / total * 255.0F);
        return (ri << 16) | (gi << 8) | bi;
    }

    @Override
    public void tick() {
        // Use baseTick() instead of super.tick() to avoid ThrowableItemProjectile physics
        this.baseTick();

        boolean noClip = this.noPhysics;
        Vec3 movement = this.getDeltaMovement();

        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = movement.horizontalDistance();
            this.setYRot((float) (Mth.atan2(movement.x, movement.z) * (180F / (float) Math.PI)));
            this.setXRot((float) (Mth.atan2(movement.y, d0) * (180F / (float) Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level().getBlockState(blockpos);
        if (!blockstate.isAir() && !noClip) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level(), blockpos);
            if (!voxelshape.isEmpty()) {
                Vec3 pos = this.position();
                for (AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockpos).contains(pos)) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        if (this.arrowShake > 0) {
            --this.arrowShake;
        }

        if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW)) {
            this.clearFire();
        }

        if (this.inGround && !noClip) {
            if (this.inBlockState != blockstate && this.shouldFall()) {
                this.startFalling();
            } else if (!this.level().isClientSide()) {
                this.tickDespawn();
            }
            ++this.timeInGround;
        } else {
            this.timeInGround = 0;
            Vec3 currentPos = this.position();
            Vec3 nextPos = currentPos.add(movement);

            HitResult hitresult = this.level().clip(new ClipContext(currentPos, nextPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hitresult.getType() != HitResult.Type.MISS) {
                nextPos = hitresult.getLocation();
            }

            while (!this.isRemoved()) {
                EntityHitResult entityhitresult = this.rayTraceEntities(currentPos, nextPos);
                if (entityhitresult != null) {
                    hitresult = entityhitresult;
                }

                if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
                    Entity entity = ((EntityHitResult) hitresult).getEntity();
                    Entity owner = this.getOwner();
                    if (entity instanceof Player && owner instanceof Player && !((Player) owner).canHarmPlayer((Player) entity)) {
                        hitresult = null;
                        entityhitresult = null;
                    }
                }

                if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !noClip) {
                    this.onHit(hitresult);
                }

                if (entityhitresult == null || this.getPierceLevel() <= 0) {
                    break;
                }

                hitresult = null;
            }

            movement = this.getDeltaMovement();
            double dx = movement.x;
            double dy = movement.y;
            double dz = movement.z;

            double newX = this.getX() + dx;
            double newY = this.getY() + dy;
            double newZ = this.getZ() + dz;
            double horizontalDist = movement.horizontalDistance();

            if (noClip) {
                this.setYRot((float) (Mth.atan2(-dx, -dz) * (180F / (float) Math.PI)));
            } else {
                this.setYRot((float) (Mth.atan2(dx, dz) * (180F / (float) Math.PI)));
            }

            this.setXRot((float) (Mth.atan2(dy, horizontalDist) * (180F / (float) Math.PI)));
            this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
            this.setYRot(lerpRotation(this.yRotO, this.getYRot()));

            float drag = 0.99F;
            if (this.isInWater()) {
                drag = this.getWaterDrag();
            }

            this.setDeltaMovement(movement.scale(drag));

            if (!this.isNoGravity() && !noClip) {
                Vec3 vel = this.getDeltaMovement();
                this.setDeltaMovement(vel.x, vel.y - 0.05F, vel.z);
            }

            this.setPos(newX, newY, newZ);
        }
    }

    @Override
    public void move(MoverType type, Vec3 pos) {
        super.move(type, pos);
        if (type != MoverType.SELF && this.shouldFall()) {
            this.startFalling();
        }
    }

    private void startFalling() {
        this.inGround = false;
        Vec3 movement = this.getDeltaMovement();
        this.setDeltaMovement(movement.multiply(
                this.random.nextFloat() * 0.2F,
                this.random.nextFloat() * 0.2F,
                this.random.nextFloat() * 0.2F));
        this.ticksInGround = 0;
    }

    private boolean shouldFall() {
        return this.inGround && this.level().noCollision(
                new AABB(this.position(), this.position()).inflate(0.06D));
    }

    protected void tickDespawn() {
        ++this.ticksInGround;
        if (this.ticksInGround >= 1200) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        int dmg = Mth.ceil(Mth.clamp(this.damage, 0.0D, 2.147483647E9D));

        Entity owner = this.getOwner();
        DamageSource damageSource;
        if (owner == null) {
            damageSource = entity.damageSources().thrown(this, this);
        } else {
            damageSource = entity.damageSources().thrown(this, owner);
            if (owner instanceof LivingEntity) {
                ((LivingEntity) owner).setLastHurtMob(entity);
            }
        }

        int fireTicks = entity.getRemainingFireTicks();
        if (this.isOnFire()) {
            entity.igniteForSeconds(5);
        }

        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (entity instanceof LivingEntity snareTarget && entity instanceof Enemy) {
            snareTarget.addEffect(new MobEffectInstance(NVMobEffects.SPIRITUS_SNARE, 1200, 0, false, true));
        }

        if (entity.hurtServer(serverLevel, damageSource, (float) dmg)) {
            if (!entity.isAlive() && owner instanceof Player playerOwner && entity instanceof LivingEntity living) {
                double spiritusAmount = this.getWillDropForMobHealth(living.getMaxHealth());
                double bonusSpiritus = playerOwner.getAttributeValue(NVAttributes.BONUS_SPIRITUS);
                if (bonusSpiritus > 0) {
                    spiritusAmount *= (1 + bonusSpiritus / 100);
                }
                if (spiritusAmount > 0) {
                    PlayerSpiritusHandler.addSpiritus(spiritusType, playerOwner, spiritusAmount);
                }
            }

            if (entity instanceof LivingEntity living) {
                if (this.knockbackStrength > 0) {
                    Vec3 knockback = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D)
                            .normalize().scale(this.knockbackStrength * 0.6D);
                    if (knockback.lengthSqr() > 0.0D) {
                        living.push(knockback.x, 0.1D, knockback.z);
                    }
                }

                this.daggerHit(living);
            }

            this.playSound(this.hitSound, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            this.discard();
        } else {
            entity.setRemainingFireTicks(fireTicks);
            this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
            this.setYRot(this.getYRot() + 180.0F);
            this.yRotO += 180.0F;
            if (this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                if (this.pickupStatus == AbstractArrow.Pickup.ALLOWED) {
                    this.spawnAtLocation(serverLevel, this.getArrowStack(), 0.1F);
                }
                this.discard();
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        this.inBlockState = this.level().getBlockState(result.getBlockPos());
        super.onHitBlock(result);
        Vec3 hitVec = result.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(hitVec);
        Vec3 offset = hitVec.normalize().scale(0.05F);
        this.setPosRaw(this.getX() - offset.x, this.getY() - offset.y, this.getZ() - offset.z);
        this.playSound(this.getHitGroundSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.inGround = true;
        this.arrowShake = 7;
        this.setHitSound(SoundEvents.ARROW_HIT);
        this.resetPiercedEntities();
    }

    @Override
    public void playerTouch(Player player) {
        if (!this.level().isClientSide() && (this.inGround || this.noPhysics) && this.arrowShake <= 0) {
            boolean canPickup = this.pickupStatus == AbstractArrow.Pickup.ALLOWED ||
                    (this.pickupStatus == AbstractArrow.Pickup.CREATIVE_ONLY && player.getAbilities().instabuild) ||
                    (this.noPhysics && this.getOwner() != null && this.getOwner().getUUID().equals(player.getUUID()));

            if (this.pickupStatus == AbstractArrow.Pickup.ALLOWED &&
                    !player.getInventory().add(this.getArrowStack())) {
                canPickup = false;
            }

            if (canPickup) {
                level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
                        SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                        ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                this.discard();
            }
        }
    }

    protected void daggerHit(LivingEntity living) {
        Entity source = this.getEffectSource();
        for (MobEffectInstance effect : this.effects) {
            living.addEffect(new MobEffectInstance(effect.getEffect(),
                    Math.max(effect.getDuration() / 8, 1),
                    effect.getAmplifier(), effect.isAmbient(), effect.isVisible()), source);
        }
    }

    protected ItemStack getArrowStack() {
        return getItem();
    }

    protected SoundEvent getHitEntitySound() {
        return SoundEvents.ARROW_HIT;
    }

    protected SoundEvent getHitGroundSound() {
        return this.hitSound;
    }

    public void setHitSound(SoundEvent sound) {
        this.hitSound = sound;
    }

    public byte getPierceLevel() {
        return 0;
    }

    protected float getWaterDrag() {
        return 0.6F;
    }

    @Nullable
    protected EntityHitResult rayTraceEntities(Vec3 startVec, Vec3 endVec) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, startVec, endVec,
                this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) &&
                (this.piercedEntities == null || !this.piercedEntities.contains(entity.getId()));
    }

    private void resetPiercedEntities() {
        if (this.hitEntities != null) {
            this.hitEntities.clear();
        }
        if (this.piercedEntities != null) {
            this.piercedEntities.clear();
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput out) {
        super.addAdditionalSaveData(out);
        out.putShort("life", (short) this.ticksInGround);
        if (this.inBlockState != null) {
            out.store("inBlockState", BlockState.CODEC, this.inBlockState);
        }
        out.putByte("shake", (byte) this.arrowShake);
        out.putBoolean("inGround", this.inGround);
        out.putByte("pickup", (byte) this.pickupStatus.ordinal());
        out.putDouble("damage", this.damage);
        out.putDouble("willDrop", willDrop);
        out.putString("spiritusType", this.spiritusType.getSerializedName());

        if (!this.effects.isEmpty()) {
            out.store("CustomPotionEffects", MobEffectInstance.CODEC.listOf(), List.copyOf(this.effects));
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput in) {
        super.readAdditionalSaveData(in);
        this.ticksInGround = in.getShortOr("life", (short) 0);
        in.read("inBlockState", BlockState.CODEC).ifPresent(state -> this.inBlockState = state);
        this.arrowShake = in.getByteOr("shake", (byte) 0) & 255;
        this.inGround = in.getBooleanOr("inGround", false);
        this.damage = in.getDoubleOr("damage", this.damage);
        this.pickupStatus = AbstractArrow.Pickup.byOrdinal(in.getByteOr("pickup", (byte) 0));
        this.willDrop = in.getDoubleOr("willDrop", 0d);
        String willTypeName = in.getStringOr("spiritusType", "");
        this.spiritusType = willTypeName.isEmpty() ? SpiritusType.RAW : SpiritusType.valueOf(willTypeName.toUpperCase());

        in.read("CustomPotionEffects", MobEffectInstance.CODEC.listOf()).ifPresent(list -> {
            for (MobEffectInstance effect : list) {
                this.addEffect(effect);
            }
        });
    }

    private ParticleOptions makeParticle() {
        ItemStack stack = this.getItem();
        return stack.isEmpty() ? ParticleTypes.CRIT : new ItemParticleOption(ParticleTypes.ITEM, stack.getItem());
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ParticleOptions particle = this.makeParticle();
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
