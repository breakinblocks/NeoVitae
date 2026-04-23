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

public class DaemoniumFervidisEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Integer> ATTACK_STATE =
            SynchedEntityData.defineId(DaemoniumFervidisEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_RUNNING =
            SynchedEntityData.defineId(DaemoniumFervidisEntity.class, EntityDataSerializers.BOOLEAN);

    public static final int ATTACK_NONE = 0;
    public static final int ATTACK_SWING = 1;    // Decay swing: melee sweep
    public static final int ATTACK_SMASH = 2;    // Revenant smash: leap slam
    public static final int ATTACK_BEAR = 3;     // Undying resilience: defensive stance

    private static final RawAnimation SPAWN_ANIM = RawAnimation.begin().thenPlayAndHold("animation.daemonium_fervidis.spawn");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_fervidis.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_fervidis.walk");
    private static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_fervidis.run");
    private static final RawAnimation SWING_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_fervidis.decay_swing_left");
    private static final RawAnimation SMASH_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_fervidis.revenant_smash");
    private static final RawAnimation BEAR_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_fervidis.bear");
    private static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlayAndHold("animation.daemonium_fervidis.death");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int attackCooldown = 0;
    private int attackAnimTimer = 0;
    private boolean hasSpawned = false;
    private int spawnTimer = 0;

    private int bearTimer = 0;
    private int bearCooldown = 0;
    private int stanceSwitchTimer = 0;

    private int deathTimer = 0;
    private boolean deathParticlesPlayed = false;

    public DaemoniumFervidisEntity(EntityType<? extends DaemoniumFervidisEntity> type, Level level) {
        super(type, level);
        this.xpReward = 30;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 150.0D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 30.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.21D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new DaemoniumFervidisAttackGoal(this));
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
        builder.define(IS_RUNNING, false);
    }

    public int getAttackState() {
        return this.entityData.get(ATTACK_STATE);
    }

    public void setAttackState(int state) {
        this.entityData.set(ATTACK_STATE, state);
    }

    public boolean isRunning() {
        return this.entityData.get(IS_RUNNING);
    }

    public void setRunning(boolean running) {
        this.entityData.set(IS_RUNNING, running);
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
        if (!hasSpawned) {
            return true;
        }
        return super.isInvulnerableTo(level, source);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        boolean hurt = super.hurtServer(level, source, amount);
        if (hurt && !level().isClientSide() && hasSpawned) {
            // Undying Resilience: 25% chance on damage, if off cooldown and not already in bear
            if (bearCooldown <= 0 && bearTimer <= 0 && random.nextFloat() < 0.25F) {
                activateBearStance();
            }
            // Heal during bear stance
            if (bearTimer > 0) {
                heal(5.0F);
                if (level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.GLOW,
                            getX(), getY() + 1.2, getZ(), 20, 0.85, 0.7, 0.85, 0.1);
                }
            }
        }
        return hurt;
    }

    private void activateBearStance() {
        setAttackState(ATTACK_BEAR);
        bearTimer = 50;
        bearCooldown = 240; // 12 seconds
        attackAnimTimer = 50;
        attackCooldown = 55;

        addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 50, 2));

        playLayeredSound(SoundEvents.ZOGLIN_AMBIENT, 0.8F, 0.5F);
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

        if (attackCooldown > 0) attackCooldown--;
        if (bearCooldown > 0) bearCooldown--;
        if (bearTimer > 0) bearTimer--;

        // Stance switching
        if (!level().isClientSide() && hasSpawned && !isAttacking()) {
            if (--stanceSwitchTimer <= 0) {
                stanceSwitchTimer = 60 + random.nextInt(60);
                setRunning(random.nextFloat() < 0.3F);
            }
        }

        if (isDeadOrDying()) {
            deathTimer++;
            if (!deathParticlesPlayed && deathTimer >= 35) {
                deathParticlesPlayed = true;
                spawnDeathParticles();
            }
        }

        if (level().isClientSide()) {
            spawnAmbientParticles();
        }
    }

    private void spawnAmbientParticles() {
        if (isRunning() && random.nextInt(2) == 0) {
            double x = getX() + (random.nextDouble() - 0.5) * 0.6;
            double z = getZ() + (random.nextDouble() - 0.5) * 0.6;
            level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, getY() + 0.1, z, 0, 0.05, 0);
        }
    }

    // ---- Attack: Decay Swing ----
    public void performSwingAttack(LivingEntity target) {
        if (level().isClientSide()) return;
        setAttackState(ATTACK_SWING);
        attackAnimTimer = 30;
        attackCooldown = 60;

        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
        Vec3 forward = getForwardHitPos(1.5);

        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(3.0), e -> e != this && e instanceof Player)) {
            if (entity.distanceToSqr(forward.x, forward.y, forward.z) <= 3.0 * 3.0) {
                entity.hurtServer((ServerLevel) entity.level(), damageSources().mobAttack(this), damage);
                entity.invulnerableTime = 0;
                Vec3 knockback = entity.position().subtract(position()).normalize().scale(1.2).add(0, 0.3, 0);
                entity.setDeltaMovement(entity.getDeltaMovement().add(knockback));
            }
        }

        playLayeredSound(SoundEvents.WITCH_THROW, 0.9F, 0.1F);
        playLayeredSound(SoundEvents.ZOGLIN_ATTACK, 0.8F, 0.8F);

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    forward.x, forward.y, forward.z, 8, 0.5, 0.3, 0.5, 0.05);
        }
    }

    // ---- Attack: Revenant Smash (leap + ground slam) ----
    public void performSmashAttack(LivingEntity target) {
        if (level().isClientSide()) return;
        setAttackState(ATTACK_SMASH);
        attackAnimTimer = 40;
        attackCooldown = 75;

        // Lunge toward target
        Vec3 dir = target.position().subtract(position()).normalize();
        setDeltaMovement(dir.scale(0.9).add(0, 0.35, 0));

        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5F;

        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(5.0), e -> e != this && e instanceof Player)) {
            entity.hurtServer((ServerLevel) entity.level(), damageSources().mobAttack(this), damage);
            entity.invulnerableTime = 0;
            Vec3 knockback = entity.position().subtract(position()).normalize().scale(0.8).add(0, 1.0, 0);
            entity.setDeltaMovement(entity.getDeltaMovement().add(knockback));
        }

        playLayeredSound(SoundEvents.ZOGLIN_STEP, 0.9F, 0.6F);
        playLayeredSound(SoundEvents.BOAT_PADDLE_LAND, 0.9F, 0.4F);
        playLayeredSound(SoundEvents.ANCIENT_DEBRIS_BREAK, 0.9F, 0.5F);
        playLayeredSound(SoundEvents.ARMOR_STAND_BREAK, 0.9F, 0.6F);

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    getX(), getY() + 0.1, getZ(), 10, 0.85, 0.15, 0.85, 0.1);
        }
    }

    private Vec3 getForwardHitPos(double forward) {
        Vec3 look = getViewVector(1.0F);
        return position().add(look.x * forward, getBbHeight() * 0.5, look.z * forward);
    }

    private void spawnDeathParticles() {
        if (level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 20; i++) {
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                        getX(), getY() + 0.2, getZ(),
                        30, 1.5, 0.3, 1.5, 0.03);
            }
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    getX(), getY() + 0.5, getZ(),
                    40, 1.0, 0.5, 1.0, 0.05);
        }
    }

    // ---- Sounds ----
    private void playSpawnSounds() {
        if (level().isClientSide()) return;
        playLayeredSound(SoundEvents.HEAVY_CORE_PLACE, 1.0F, 0.8F);
        playLayeredSound(SoundEvents.ZOGLIN_AMBIENT, 0.8F, 0.65F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 140;
    }

    @Override
    public void playAmbientSound() {
        if (!hasSpawned) return;
        if (random.nextFloat() > 0.35F) return;
        playLayeredSound(SoundEvents.ZOGLIN_AMBIENT, 0.75F, 0.65F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Override
    public void playHurtSound(DamageSource source) {
        playLayeredSound(SoundEvents.HUSK_HURT, 0.8F, 0.7F);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    public void die(DamageSource source) {
        playLayeredSound(SoundEvents.ZOGLIN_DEATH, 1.0F, 0.65F);
        super.die(source);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (isRunning()) {
            playLayeredSound(SoundEvents.ZOGLIN_STEP, 1.0F, 0.4F);
        } else {
            playLayeredSound(SoundEvents.ZOGLIN_STEP, 1.0F, 0.6F);
        }
    }

    private void playLayeredSound(SoundEvent sound, float volume, float pitch) {
        level().playSound(null, getX(), getY(), getZ(), sound, SoundSource.HOSTILE, volume, pitch);
    }

    public boolean canAttack() {
        return attackCooldown <= 0 && hasSpawned && !isAttacking();
    }

    @Override
    public float getSpeed() {
        return isRunning() ? super.getSpeed() * 1.6F : super.getSpeed();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("HasSpawned", hasSpawned);
        tag.putBoolean("IsRunning", isRunning());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
        hasSpawned = tag.getBooleanOr("HasSpawned", false);
        if (hasSpawned) spawnTimer = 50;
        setRunning(tag.getBooleanOr("IsRunning", false));
    }

    // ---- GeckoLib animation ----
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<DaemoniumFervidisEntity>("body", 10, state -> {
            if (isDeadOrDying()) {
                return state.setAndContinue(DEATH_ANIM);
            }
            if (!hasSpawned) {
                return state.setAndContinue(SPAWN_ANIM);
            }

            int attackState = getAttackState();
            if (attackState == ATTACK_SWING) return state.setAndContinue(SWING_ANIM);
            if (attackState == ATTACK_SMASH) return state.setAndContinue(SMASH_ANIM);
            if (attackState == ATTACK_BEAR) return state.setAndContinue(BEAR_ANIM);

            if (state.isMoving()) {
                return state.setAndContinue(isRunning() ? RUN_ANIM : WALK_ANIM);
            }
            return state.setAndContinue(IDLE_ANIM);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
