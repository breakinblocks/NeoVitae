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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
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

public class DaemoniumCruorisEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Integer> ATTACK_STATE =
            SynchedEntityData.defineId(DaemoniumCruorisEntity.class, EntityDataSerializers.INT);

    public static final int ATTACK_NONE = 0;
    public static final int ATTACK_CLAW_LEFT = 1;
    public static final int ATTACK_CLAW_RIGHT = 2;
    public static final int ATTACK_LEAP = 3;

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_cruoris.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_cruoris.walk");
    private static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_cruoris.run");
    private static final RawAnimation CLAW_LEFT_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_cruoris.necrotic_claws_left");
    private static final RawAnimation CLAW_RIGHT_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_cruoris.necrotic_claws_right");
    private static final RawAnimation LEAP_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_cruoris.grave_leap");
    private static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlayAndHold("animation.daemonium_cruoris.death");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int attackCooldown = 0;
    private int attackAnimTimer = 0;
    private boolean isRunning = false;
    private int leapCooldown = 0;

    // Leap tracking
    private boolean leaping = false;
    private int leapTicks = 0;

    // Death particle timing
    private int deathTimer = 0;
    private boolean deathParticlesPlayed = false;

    public DaemoniumCruorisEntity(EntityType<? extends DaemoniumCruorisEntity> type, Level level) {
        super(type, level);
        this.xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 25.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new DaemoniumCruorisAttackGoal(this));
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

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isAttacking() {
        return attackAnimTimer > 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (attackAnimTimer > 0) {
            attackAnimTimer--;
            if (!leaping) {
                setDeltaMovement(Vec3.ZERO);
            }
            if (attackAnimTimer <= 0) {
                setAttackState(ATTACK_NONE);
                leaping = false;
            }
        }

        if (attackCooldown > 0) attackCooldown--;
        if (leapCooldown > 0) leapCooldown--;

        // Leap movement
        if (leaping) {
            leapTicks++;
            if (leapTicks == 5) {
                LivingEntity target = getTarget();
                if (target != null) {
                    Vec3 dir = target.position().subtract(position()).normalize();
                    setDeltaMovement(dir.scale(0.7).add(0, 0.12, 0));
                }
            }
            if (leapTicks >= 15 && onGround()) {
                performLeapLanding();
                leaping = false;
            }
        }

        // Random walk/run switching (like MM Ghoul_Dash)
        if (!level().isClientSide() && getTarget() != null && tickCount % 40 == 0) {
            isRunning = random.nextFloat() < 0.25F;
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

    // ---- Ambient particles: blood drips ----

    private void spawnAmbientParticles() {
        if (random.nextInt(6) == 0) {
            double x = getX() + (random.nextDouble() - 0.5) * 0.6;
            double y = getY() + random.nextDouble() * 0.8;
            double z = getZ() + (random.nextDouble() - 0.5) * 0.6;
            level().addParticle(ParticleTypes.FALLING_DRIPSTONE_LAVA, x, y, z, 0, 0, 0);
        }
    }

    // ---- Attack: Necrotic Claw ----

    public void performClawAttack(LivingEntity target, boolean leftHand) {
        if (level().isClientSide()) return;

        setAttackState(leftHand ? ATTACK_CLAW_LEFT : ATTACK_CLAW_RIGHT);
        attackAnimTimer = 34;
        attackCooldown = 34;

        Vec3 forward = getForwardHitPos(1.3);
        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);

        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(2.5), e -> e != this && e instanceof Player)) {
            if (entity.distanceToSqr(forward.x, forward.y, forward.z) <= 2.5 * 2.5) {
                entity.hurtServer((ServerLevel) entity.level(), damageSources().mobAttack(this), damage);
                entity.invulnerableTime = 0;
            }
        }

        spawnClawParticles(forward);
        playClawSounds();
    }

    private void spawnClawParticles(Vec3 pos) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FALLING_DRIPSTONE_LAVA,
                    pos.x, pos.y, pos.z, 6, 0.3, 0.3, 0.3, 0.01);
        }
    }

    private void playClawSounds() {
        playLayeredSound(SoundEvents.WITCH_THROW, 0.9F, 0.2F);
    }

    // ---- Attack: Grave Leap ----

    public void performLeapAttack(LivingEntity target) {
        if (level().isClientSide()) return;

        setAttackState(ATTACK_LEAP);
        attackAnimTimer = 75;
        attackCooldown = 75;
        leapCooldown = 140; // 7 second cooldown
        leaping = true;
        leapTicks = 0;
    }

    private void performLeapLanding() {
        if (level().isClientSide()) return;

        Vec3 forward = getForwardHitPos(1.3);
        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5F;

        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(2.5), e -> e != this && e instanceof Player)) {
            if (entity.distanceToSqr(forward.x, forward.y, forward.z) <= 2.5 * 2.5) {
                entity.hurtServer((ServerLevel) entity.level(), damageSources().mobAttack(this), damage);
                entity.invulnerableTime = 0;
            }
        }

        spawnLeapParticles(forward);
        playLeapSounds();
    }

    private void spawnLeapParticles(Vec3 pos) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FALLING_DRIPSTONE_LAVA,
                    pos.x, pos.y, pos.z, 12, 0.5, 0.2, 0.5, 0.02);
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    pos.x, pos.y + 0.1, pos.z, 6, 0.6, 0.1, 0.6, 0.1);
        }
    }

    private void playLeapSounds() {
        playLayeredSound(SoundEvents.WITCH_THROW, 0.9F, 0.7F);
        playLayeredSound(SoundEvents.PANDA_BITE, 0.9F, 1.3F);
    }

    // ---- Death particles ----

    private void spawnDeathParticles() {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FALLING_DRIPSTONE_LAVA,
                    getX(), getY() + 0.5, getZ(), 30, 0.8, 0.4, 0.8, 0.02);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    getX(), getY() + 0.2, getZ(), 20, 0.6, 0.2, 0.6, 0.03);
        }
    }

    // ---- Sounds ----

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
        if (random.nextFloat() > 0.35F) return;
        playLayeredSound(SoundEvents.HUSK_AMBIENT, 0.75F, 0.8F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Override
    public void playHurtSound(DamageSource source) {
        playLayeredSound(SoundEvents.HUSK_HURT, 0.8F, 1.3F);
        playLayeredSound(SoundEvents.PLAYER_ATTACK_CRIT, 0.55F, 0.33F);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    public void die(DamageSource source) {
        playLayeredSound(SoundEvents.HUSK_AMBIENT, 0.75F, 1.0F);
        super.die(source);
    }

    private void playLayeredSound(SoundEvent sound, float volume, float pitch) {
        level().playSound(null, getX(), getY(), getZ(), sound, SoundSource.HOSTILE, volume, pitch);
    }

    // ---- Utility ----

    private Vec3 getForwardHitPos(double forward) {
        Vec3 look = getViewVector(1.0F);
        return position().add(look.x * forward, getBbHeight() * 0.5, look.z * forward);
    }

    public boolean canAttack() {
        return attackCooldown <= 0 && !isAttacking();
    }

    public boolean canLeap() {
        return leapCooldown <= 0 && isRunning;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput tag) {
        super.addAdditionalSaveData(tag);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
    }

    // ---- GeckoLib animation ----

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<DaemoniumCruorisEntity>("body", 5, state -> {
            if (isDeadOrDying()) {
                return state.setAndContinue(DEATH_ANIM);
            }

            int attackState = getAttackState();
            if (attackState == ATTACK_CLAW_LEFT) {
                return state.setAndContinue(CLAW_LEFT_ANIM);
            } else if (attackState == ATTACK_CLAW_RIGHT) {
                return state.setAndContinue(CLAW_RIGHT_ANIM);
            } else if (attackState == ATTACK_LEAP) {
                return state.setAndContinue(LEAP_ANIM);
            }

            if (state.isMoving()) {
                return state.setAndContinue(isRunning ? RUN_ANIM : WALK_ANIM);
            }
            return state.setAndContinue(IDLE_ANIM);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
