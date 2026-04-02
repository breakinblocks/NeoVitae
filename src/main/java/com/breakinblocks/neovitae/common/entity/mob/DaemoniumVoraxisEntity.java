package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DaemoniumVoraxisEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Integer> ATTACK_STATE =
            SynchedEntityData.defineId(DaemoniumVoraxisEntity.class, EntityDataSerializers.INT);

    public static final int ATTACK_NONE = 0;
    public static final int ATTACK_SLASH_L = 1;
    public static final int ATTACK_SLASH_R = 2;

    private static final RawAnimation SPAWN_ANIM = RawAnimation.begin().thenPlayAndHold("animation.daemonium_voraxis.spawn");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_voraxis.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_voraxis.walk");
    private static final RawAnimation SLASH_L_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_voraxis.slash_left");
    private static final RawAnimation SLASH_R_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_voraxis.slash_right");
    private static final RawAnimation HIT_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_voraxis.hit");
    private static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlayAndHold("animation.daemonium_voraxis.death");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int attackCooldown = 0;
    private int attackAnimTimer = 0;
    private boolean hasSpawned = false;
    private int spawnTimer = 0;

    private int deathTimer = 0;
    private boolean deathParticlesPlayed = false;

    public DaemoniumVoraxisEntity(EntityType<? extends DaemoniumVoraxisEntity> type, Level level) {
        super(type, level);
        this.xpReward = 12;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 25.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new DaemoniumVoraxisAttackGoal(this));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_STATE, ATTACK_NONE);
    }

    public int getAttackState() { return this.entityData.get(ATTACK_STATE); }
    public void setAttackState(int state) { this.entityData.set(ATTACK_STATE, state); }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (!hasSpawned) return true;
        return super.isInvulnerableTo(source);
    }

    public boolean isAttacking() { return attackAnimTimer > 0; }

    @Override
    public void tick() {
        super.tick();

        if (!hasSpawned) {
            spawnTimer++;
            if (spawnTimer == 1) playSpawnSounds();
            if (spawnTimer >= 25) hasSpawned = true;
        }

        if (attackAnimTimer > 0) {
            attackAnimTimer--;
            setDeltaMovement(Vec3.ZERO);
            if (attackAnimTimer <= 0) setAttackState(ATTACK_NONE);
        }

        if (attackCooldown > 0) attackCooldown--;

        if (isDeadOrDying()) {
            deathTimer++;
            if (!deathParticlesPlayed && deathTimer >= 35) {
                deathParticlesPlayed = true;
                spawnDeathParticles();
            }
        }

        if (level().isClientSide) spawnAmbientParticles();
    }

    private void spawnAmbientParticles() {
        if (random.nextInt(5) == 0) {
            double x = getX() + (random.nextDouble() - 0.5) * 0.8;
            double y = getY() + 0.3 + random.nextDouble() * 1.2;
            double z = getZ() + (random.nextDouble() - 0.5) * 0.8;
            level().addParticle(ParticleTypes.SQUID_INK, x, y, z, 0, 0.01, 0);
        }
    }

    // ---- Attack: Slash Left ----
    public void performSlashLeft(LivingEntity target) {
        if (level().isClientSide) return;
        setAttackState(ATTACK_SLASH_L);
        attackAnimTimer = 25;
        attackCooldown = 30;
        performSlashDamage(target);
    }

    // ---- Attack: Slash Right ----
    public void performSlashRight(LivingEntity target) {
        if (level().isClientSide) return;
        setAttackState(ATTACK_SLASH_R);
        attackAnimTimer = 25;
        attackCooldown = 30;
        performSlashDamage(target);
    }

    private void performSlashDamage(LivingEntity target) {
        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
        Vec3 forward = getForwardHitPos(1.5);

        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(2.5), e -> e != this && e instanceof Player)) {
            if (entity.distanceToSqr(forward.x, forward.y, forward.z) <= 2.5 * 2.5) {
                entity.hurt(damageSources().mobAttack(this), damage);
                entity.invulnerableTime = 0;
                entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 1));
                Vec3 kb = entity.position().subtract(position()).normalize().scale(0.5).add(0, 0.2, 0);
                entity.setDeltaMovement(entity.getDeltaMovement().add(kb));
            }
        }

        // Life drain: heal on hit
        heal(3.0F);

        playLayeredSound(SoundEvents.PLAYER_ATTACK_CRIT, 1.0F, 0.3F);

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.DRAGON_BREATH,
                    forward.x, forward.y, forward.z, 8, 0.4, 0.3, 0.4, 0.02);
        }
    }

    private Vec3 getForwardHitPos(double forward) {
        Vec3 look = getViewVector(1.0F);
        return position().add(look.x * forward, getBbHeight() * 0.5, look.z * forward);
    }

    private void spawnDeathParticles() {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SQUID_INK,
                    getX(), getY() + 0.5, getZ(), 30, 0.8, 0.4, 0.8, 0.05);
            serverLevel.sendParticles(ParticleTypes.DRAGON_BREATH,
                    getX(), getY() + 0.3, getZ(), 20, 0.6, 0.3, 0.6, 0.03);
        }
    }

    // ---- Sounds ----
    private void playSpawnSounds() {
        if (level().isClientSide) return;
        playLayeredSound(SoundEvents.PHANTOM_AMBIENT, 0.8F, 0.4F);
    }

    @Override
    protected SoundEvent getAmbientSound() { return null; }

    @Override
    public int getAmbientSoundInterval() { return 160; }

    @Override
    public void playAmbientSound() {
        if (!hasSpawned) return;
        playLayeredSound(SoundEvents.PHANTOM_AMBIENT, 0.7F, random.nextFloat() * 0.4F + 0.2F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return null; }

    @Override
    public void playHurtSound(DamageSource source) {
        playLayeredSound(SoundEvents.PHANTOM_HURT, 0.7F, random.nextFloat() * 0.4F + 0.2F);
    }

    @Override
    protected SoundEvent getDeathSound() { return null; }

    @Override
    public void die(DamageSource source) {
        playLayeredSound(SoundEvents.PHANTOM_DEATH, 1.0F, 0.4F);
        super.die(source);
    }

    private void playLayeredSound(SoundEvent sound, float volume, float pitch) {
        level().playSound(null, getX(), getY(), getZ(), sound, SoundSource.HOSTILE, volume, pitch);
    }

    public boolean canAttack() { return attackCooldown <= 0 && hasSpawned && !isAttacking(); }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("HasSpawned", hasSpawned);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        hasSpawned = tag.getBoolean("HasSpawned");
        if (hasSpawned) spawnTimer = 25;
    }

    // ---- GeckoLib ----
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "body", 10, state -> {
            if (isDeadOrDying()) return state.setAndContinue(DEATH_ANIM);
            if (!hasSpawned) return state.setAndContinue(SPAWN_ANIM);

            int attackState = getAttackState();
            if (attackState == ATTACK_SLASH_L) return state.setAndContinue(SLASH_L_ANIM);
            if (attackState == ATTACK_SLASH_R) return state.setAndContinue(SLASH_R_ANIM);

            if (state.isMoving()) return state.setAndContinue(WALK_ANIM);
            return state.setAndContinue(IDLE_ANIM);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
