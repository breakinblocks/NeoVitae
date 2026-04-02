package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DaemoniumCorrodisEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Integer> ATTACK_STATE =
            SynchedEntityData.defineId(DaemoniumCorrodisEntity.class, EntityDataSerializers.INT);

    public static final int ATTACK_NONE = 0;
    public static final int ATTACK_SWEEP = 1;       // attack_1: single forward sweep
    public static final int ATTACK_DOUBLE = 2;       // attack_2: double forward sweep
    public static final int ATTACK_SLAM = 3;          // attack_3: two-phase AoE slam

    private static final int WITHER_DURATION = 100;   // 5 seconds
    private static final int WITHER_LEVEL = 1;         // Wither II

    private static final RawAnimation SPAWN_ANIM = RawAnimation.begin().thenPlayAndHold("animation.daemonium_corrodis.spawn");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_corrodis.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_corrodis.walk");
    private static final RawAnimation ATTACK_1_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_corrodis.attack_1");
    private static final RawAnimation ATTACK_2_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_corrodis.attack_2");
    private static final RawAnimation ATTACK_3_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_corrodis.attack_3");
    private static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlayAndHold("animation.daemonium_corrodis.death");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int attackCooldown = 0;
    private int attackAnimTimer = 0;
    private boolean hasSpawned = false;
    private int spawnTimer = 0;

    // Attack 3 two-phase tracking
    private int slamPhase2Timer = 0;
    private Vec3 slamOrigin = null;

    // Death particle timing
    private int deathTimer = 0;
    private boolean deathParticlesPlayed = false;

    public DaemoniumCorrodisEntity(EntityType<? extends DaemoniumCorrodisEntity> type, Level level) {
        super(type, level);
        this.xpReward = 25;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.ARMOR, 15.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new DaemoniumCorrodisAttackGoal(this));
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

    public int getAttackState() {
        return this.entityData.get(ATTACK_STATE);
    }

    public void setAttackState(int state) {
        this.entityData.set(ATTACK_STATE, state);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (!hasSpawned) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.WITHER, WITHER_DURATION, WITHER_LEVEL));
        }
        return hit;
    }

    public boolean isAttacking() {
        return attackAnimTimer > 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (!hasSpawned) {
            spawnTimer++;
            if (spawnTimer == 1) {
                playSpawnSounds();
            }
            if (spawnTimer >= 50) {
                hasSpawned = true;
            }
        }

        if (attackAnimTimer > 0) {
            attackAnimTimer--;
            setDeltaMovement(Vec3.ZERO);
            if (attackAnimTimer <= 0) {
                setAttackState(ATTACK_NONE);
            }
        }

        if (attackCooldown > 0) {
            attackCooldown--;
        }

        // Attack 3 phase 2: delayed second AoE
        if (slamPhase2Timer > 0) {
            slamPhase2Timer--;
            if (slamPhase2Timer <= 0 && slamOrigin != null) {
                performSlamPhase2();
                slamOrigin = null;
            }
        }

        if (isDeadOrDying()) {
            deathTimer++;
            if (!deathParticlesPlayed && deathTimer >= 35) {
                deathParticlesPlayed = true;
                spawnDeathParticles();
            }
        }

        if (level().isClientSide) {
            spawnAmbientParticles();
        }
    }

    // ---- Ambient particles: corrosive smoke/mist ----

    private void spawnAmbientParticles() {
        double x = getX();
        double y = getY() + 1.0;
        double z = getZ();
        for (int i = 0; i < 2; i++) {
            double ox = (random.nextDouble() - 0.5) * 0.8;
            double oy = (random.nextDouble() - 0.5) * 1.0;
            double oz = (random.nextDouble() - 0.5) * 0.8;
            level().addParticle(ParticleTypes.SMOKE, x + ox, y + oy, z + oz, 0, 0.013, 0);
        }
        if (random.nextInt(4) == 0) {
            level().addParticle(ParticleTypes.LARGE_SMOKE,
                    x + (random.nextDouble() - 0.5) * 0.6,
                    y + (random.nextDouble() - 0.5) * 0.6,
                    z + (random.nextDouble() - 0.5) * 0.6,
                    0, 0.01, 0);
        }
    }

    // ---- Attack 1: Single forward sweep ----

    public void performSweepAttack(LivingEntity target) {
        if (level().isClientSide) return;

        setAttackState(ATTACK_SWEEP);
        attackAnimTimer = 15;
        attackCooldown = 15;

        Vec3 forward = getForwardHitPos(1.0);
        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);

        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(2.5), e -> e != this && e instanceof Player)) {
            if (entity.distanceToSqr(forward.x, forward.y, forward.z) <= 2.5 * 2.5) {
                entity.hurt(damageSources().mobAttack(this), damage);
                entity.invulnerableTime = 0;
                entity.addEffect(new MobEffectInstance(MobEffects.WITHER, WITHER_DURATION, WITHER_LEVEL));
                applyCorrosiveChance(entity);
                Vec3 knockback = entity.position().subtract(position()).normalize().scale(0.7).add(0, 0.3, 0);
                entity.setDeltaMovement(entity.getDeltaMovement().add(knockback));
            }
        }

        spawnAttackParticles(forward);
        playAttackSounds();
    }

    // ---- Attack 2: Double forward sweep ----

    public void performDoubleAttack(LivingEntity target) {
        if (level().isClientSide) return;

        setAttackState(ATTACK_DOUBLE);
        attackAnimTimer = 15;
        attackCooldown = 15;

        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
        Vec3 forward1 = getForwardHitPos(1.0);
        Vec3 forward2 = getForwardHitPos(2.0);

        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(3.0), e -> e != this && e instanceof Player)) {
            boolean inZone1 = entity.distanceToSqr(forward1.x, forward1.y, forward1.z) <= 1.3 * 1.3;
            boolean inZone2 = entity.distanceToSqr(forward2.x, forward2.y, forward2.z) <= 1.3 * 1.3;
            if (inZone1 || inZone2) {
                entity.hurt(damageSources().mobAttack(this), damage);
                entity.invulnerableTime = 0;
                entity.addEffect(new MobEffectInstance(MobEffects.WITHER, WITHER_DURATION, WITHER_LEVEL));
                applyCorrosiveChance(entity);
                Vec3 knockback = entity.position().subtract(position()).normalize().scale(0.7).add(0, 0.3, 0);
                entity.setDeltaMovement(entity.getDeltaMovement().add(knockback));
            }
        }

        spawnAttackParticles(forward1);
        spawnAttackParticles(forward2);
        playAttackSounds();
    }

    // ---- Attack 3: Two-phase AoE slam ----

    public void performSlamAttack(LivingEntity target) {
        if (level().isClientSide) return;

        setAttackState(ATTACK_SLAM);
        attackAnimTimer = 25;
        attackCooldown = 25;

        Vec3 forward = getForwardHitPos(1.0);
        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);

        // Phase 1: immediate AoE, no knockback
        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(2.5), e -> e != this)) {
            if (entity.distanceToSqr(forward.x, forward.y, forward.z) <= 2.5 * 2.5) {
                entity.hurt(damageSources().mobAttack(this), damage);
                entity.invulnerableTime = 0;
                entity.addEffect(new MobEffectInstance(MobEffects.WITHER, WITHER_DURATION, WITHER_LEVEL));
                applyCorrosiveChance(entity);
            }
        }

        spawnAttackParticles(forward);
        playAttackSounds();

        // Phase 2: delayed AoE with knockback after 10 ticks
        slamPhase2Timer = 10;
        slamOrigin = forward;
    }

    private void performSlamPhase2() {
        if (level().isClientSide || slamOrigin == null) return;

        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);

        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(2.75), e -> e != this && e instanceof Player)) {
            if (entity.distanceToSqr(slamOrigin.x, slamOrigin.y, slamOrigin.z) <= 2.75 * 2.75) {
                entity.hurt(damageSources().mobAttack(this), damage);
                entity.invulnerableTime = 0;
                entity.addEffect(new MobEffectInstance(MobEffects.WITHER, WITHER_DURATION, WITHER_LEVEL));
                applyCorrosiveChance(entity);
                Vec3 knockback = entity.position().subtract(position()).normalize().scale(0.7).add(0, 0.3, 0);
                entity.setDeltaMovement(entity.getDeltaMovement().add(knockback));
            }
        }

        spawnAttackParticles(slamOrigin);
        playAttackSounds();
    }

    // ---- 60% chance to apply Weakness (corrosive weakening) ----

    private void applyCorrosiveChance(LivingEntity target) {
        if (random.nextFloat() < 0.6F) {
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
        }
    }

    // ---- Particle helpers ----

    private Vec3 getForwardHitPos(double forward) {
        Vec3 look = getViewVector(1.0F);
        return position().add(look.x * forward, getBbHeight() * 0.5, look.z * forward);
    }

    private void spawnAttackParticles(Vec3 pos) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    pos.x, pos.y, pos.z,
                    10, 0.4, 0.3, 0.4, 0.02);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    pos.x, pos.y, pos.z,
                    8, 0.35, 0.3, 0.35, 0.04);
        }
    }

    private void spawnDeathParticles() {
        if (level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 20; i++) {
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                        getX(), getY() + 0.2, getZ(),
                        30, 1.2, 0.2, 1.2, 0.03);
            }
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    getX(), getY() + 0.5, getZ(),
                    40, 0.8, 0.4, 0.8, 0.05);
        }
    }

    // ---- Sounds ----

    private void playSpawnSounds() {
        if (level().isClientSide) return;
        playLayeredSound(SoundEvents.HEAVY_CORE_PLACE, 1.0F, 1.0F);
        playLayeredSound(SoundEvents.ARMOR_EQUIP_IRON.value(), 1.0F, 0.69F);
        playLayeredSound(SoundEvents.WITHER_SKELETON_DEATH, 1.0F, 0.80F);
        playLayeredSound(SoundEvents.HOSTILE_SMALL_FALL, 0.3F, 1.0F);
    }

    private void playAttackSounds() {
        playLayeredSound(SoundEvents.PLAYER_ATTACK_SWEEP, 0.6F, 1.0F);
        playLayeredSound(SoundEvents.PLAYER_ATTACK_WEAK, 1.0F, 1.0F);
        playLayeredSound(SoundEvents.WITHER_SKELETON_STEP, 0.3F, 0.80F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 60;
    }

    @Override
    public void playAmbientSound() {
        if (!hasSpawned) return;
        if (random.nextFloat() > 0.75F) return;
        playLayeredSound(SoundEvents.WITHER_SKELETON_AMBIENT, 1.0F, 0.75F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Override
    public void playHurtSound(DamageSource source) {
        playLayeredSound(SoundEvents.WITHER_SKELETON_HURT, 1.0F, 1.0F);
        playLayeredSound(SoundEvents.WITHER_HURT, 0.2F, 2.0F);
        playLayeredSound(SoundEvents.WITHER_BREAK_BLOCK, 0.3F, 1.84F);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    public void die(DamageSource source) {
        playLayeredSound(SoundEvents.WITHER_SKELETON_DEATH, 1.0F, 1.0F);
        playLayeredSound(SoundEvents.WITHER_SHOOT, 0.5F, 0.50F);
        playLayeredSound(SoundEvents.WITHER_BREAK_BLOCK, 0.5F, 0.65F);
        super.die(source);
    }

    private void playLayeredSound(SoundEvent sound, float volume, float pitch) {
        level().playSound(null, getX(), getY(), getZ(), sound, SoundSource.HOSTILE, volume, pitch);
    }

    public boolean canAttack() {
        return attackCooldown <= 0 && hasSpawned && !isAttacking();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("HasSpawned", hasSpawned);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        hasSpawned = tag.getBoolean("HasSpawned");
        if (hasSpawned) {
            spawnTimer = 50;
        }
    }

    // ---- GeckoLib animation ----

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "body", 10, state -> {
            if (isDeadOrDying()) {
                return state.setAndContinue(DEATH_ANIM);
            }
            if (!hasSpawned) {
                return state.setAndContinue(SPAWN_ANIM);
            }

            int attackState = getAttackState();
            if (attackState == ATTACK_SWEEP) {
                return state.setAndContinue(ATTACK_1_ANIM);
            } else if (attackState == ATTACK_DOUBLE) {
                return state.setAndContinue(ATTACK_2_ANIM);
            } else if (attackState == ATTACK_SLAM) {
                return state.setAndContinue(ATTACK_3_ANIM);
            }

            if (state.isMoving()) {
                return state.setAndContinue(WALK_ANIM);
            }
            return state.setAndContinue(IDLE_ANIM);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
