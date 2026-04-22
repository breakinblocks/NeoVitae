package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DaemoniumPestisEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Integer> ATTACK_STATE =
            SynchedEntityData.defineId(DaemoniumPestisEntity.class, EntityDataSerializers.INT);

    public static final int ATTACK_NONE = 0;
    public static final int ATTACK_FANG = 1;    // Bite attack
    public static final int ATTACK_LUNGE = 2;   // Shadow dash + bite

    private static final RawAnimation SPAWN_ANIM = RawAnimation.begin().thenPlayAndHold("animation.daemonium_pestis.spawn");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_pestis.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_pestis.walk");
    private static final RawAnimation FANG_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_pestis.attack");
    private static final RawAnimation LUNGE_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_pestis.attack2");
    private static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlayAndHold("animation.daemonium_pestis.death");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int attackCooldown = 0;
    private int attackAnimTimer = 0;
    private boolean hasSpawned = false;
    private int spawnTimer = 0;

    private int deathTimer = 0;
    private boolean deathParticlesPlayed = false;

    public DaemoniumPestisEntity(EntityType<? extends DaemoniumPestisEntity> type, Level level) {
        super(type, level);
        this.xpReward = 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new DaemoniumPestisAttackGoal(this));
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
    public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
        if (!hasSpawned) return true;
        return super.isInvulnerableTo(level, source);
    }

    public boolean isAttacking() { return attackAnimTimer > 0; }

    @Override
    public void tick() {
        super.tick();

        if (!hasSpawned) {
            spawnTimer++;
            if (spawnTimer == 1) playSpawnSounds();
            if (spawnTimer >= 40) hasSpawned = true;
        }

        if (attackAnimTimer > 0) {
            attackAnimTimer--;
            setDeltaMovement(Vec3.ZERO);
            if (attackAnimTimer <= 0) setAttackState(ATTACK_NONE);
        }

        if (attackCooldown > 0) attackCooldown--;

        if (isDeadOrDying()) {
            deathTimer++;
            if (!deathParticlesPlayed && deathTimer >= 15) {
                deathParticlesPlayed = true;
                spawnDeathParticles();
            }
        }

        if (level().isClientSide()) spawnAmbientParticles();
    }

    private void spawnAmbientParticles() {
        if (random.nextInt(8) == 0) {
            double x = getX() + (random.nextDouble() - 0.5) * 0.6;
            double y = getY() + random.nextDouble() * 0.5;
            double z = getZ() + (random.nextDouble() - 0.5) * 0.6;
            level().addParticle(ParticleTypes.SQUID_INK, x, y, z, 0, 0.005, 0);
        }
    }

    // ---- Attack: Fang Bite ----
    public void performFangAttack(LivingEntity target) {
        if (level().isClientSide()) return;
        setAttackState(ATTACK_FANG);
        attackAnimTimer = 26;
        attackCooldown = 26;

        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
        Vec3 forward = getForwardHitPos(1.0);

        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(2.0), e -> e != this && e instanceof Player)) {
            if (entity.distanceToSqr(forward.x, forward.y, forward.z) <= 2.0 * 2.0) {
                entity.hurt(damageSources().mobAttack(this), damage);
                entity.invulnerableTime = 0;
                entity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
                Vec3 kb = entity.position().subtract(position()).normalize().scale(0.4);
                entity.setDeltaMovement(entity.getDeltaMovement().add(kb));
            }
        }

        playLayeredSound(SoundEvents.LLAMA_SPIT, 1.0F, 0.8F);
    }

    // ---- Attack: Shadow Lunge ----
    public void performLungeAttack(LivingEntity target) {
        if (level().isClientSide()) return;
        setAttackState(ATTACK_LUNGE);
        attackAnimTimer = 40;
        attackCooldown = 40;

        // Dash toward target
        Vec3 dir = target.position().subtract(position()).normalize();
        setDeltaMovement(dir.scale(0.8).add(0, 0.1, 0));

        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(2.0), e -> e != this && e instanceof Player)) {
            entity.hurt(damageSources().mobAttack(this), damage);
            entity.invulnerableTime = 0;
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
            Vec3 kb = entity.position().subtract(position()).normalize().scale(0.5);
            entity.setDeltaMovement(entity.getDeltaMovement().add(kb));
        }

        playLayeredSound(SoundEvents.BREEZE_JUMP, 1.0F, 0.8F);

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SQUID_INK,
                    getX(), getY() + 0.3, getZ(), 15, 0.5, 0.3, 0.5, 0.1);
        }
    }

    private Vec3 getForwardHitPos(double forward) {
        Vec3 look = getViewVector(1.0F);
        return position().add(look.x * forward, getBbHeight() * 0.5, look.z * forward);
    }

    private void spawnDeathParticles() {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SQUID_INK,
                    getX(), getY() + 0.3, getZ(), 25, 0.5, 0.3, 0.5, 0.05);
        }
    }

    // ---- Sounds ----
    private void playSpawnSounds() {
        if (level().isClientSide()) return;
        playLayeredSound(SoundEvents.SPIDER_AMBIENT, 1.0F, 0.7F);
    }

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.SPIDER_AMBIENT; }

    @Override
    public int getAmbientSoundInterval() { return 160; }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.SPIDER_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.SPIDER_DEATH; }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playSound(SoundEvents.SPIDER_STEP, 0.8F, 0.9F);
    }

    private void playLayeredSound(SoundEvent sound, float volume, float pitch) {
        level().playSound(null, getX(), getY(), getZ(), sound, SoundSource.HOSTILE, volume, pitch);
    }

    public boolean canAttack() { return attackCooldown <= 0 && hasSpawned && !isAttacking(); }

    @Override
    protected void addAdditionalSaveData(ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("HasSpawned", hasSpawned);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
        hasSpawned = tag.getBooleanOr("HasSpawned", false);
        if (hasSpawned) spawnTimer = 40;
    }

    // ---- GeckoLib ----
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<DaemoniumPestisEntity>("body", 5, state -> {
            if (isDeadOrDying()) return state.setAndContinue(DEATH_ANIM);
            if (!hasSpawned) return state.setAndContinue(SPAWN_ANIM);

            int attackState = getAttackState();
            if (attackState == ATTACK_FANG) return state.setAndContinue(FANG_ANIM);
            if (attackState == ATTACK_LUNGE) return state.setAndContinue(LUNGE_ANIM);

            if (state.isMoving()) return state.setAndContinue(WALK_ANIM);
            return state.setAndContinue(IDLE_ANIM);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
