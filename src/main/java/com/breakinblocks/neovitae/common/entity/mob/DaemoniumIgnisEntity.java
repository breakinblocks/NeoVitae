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
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DaemoniumIgnisEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Integer> ATTACK_STATE =
            SynchedEntityData.defineId(DaemoniumIgnisEntity.class, EntityDataSerializers.INT);

    public static final int ATTACK_NONE = 0;
    public static final int ATTACK_FIREBALL = 1;   // MM attack_1: ranged fireball
    public static final int ATTACK_SLAM = 2;        // MM attack_2: close-range ground slam AoE
    public static final int ATTACK_SWORD = 3;        // MM attack_3: melee sword slash multi-hit

    private static final RawAnimation SPAWN_ANIM = RawAnimation.begin().thenPlayAndHold("animation.daemonium_ignis.spawn");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_ignis.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_ignis.walk");
    private static final RawAnimation ATTACK_1_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_ignis.attack_1");
    private static final RawAnimation ATTACK_2_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_ignis.attack_2");
    private static final RawAnimation ATTACK_3_ANIM = RawAnimation.begin()
            .thenPlayAndHold("animation.daemonium_ignis.attack_3_1")
            .thenPlay("animation.daemonium_ignis.attack_3_2");
    private static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlayAndHold("animation.daemonium_ignis.death");
    private static final RawAnimation SPIN_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_ignis.spin");
    private static final RawAnimation SWORD_SPIN_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_ignis.sword_spin");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int attackCooldown = 0;
    private int attackAnimTimer = 0;
    private boolean hasSpawned = false;
    private int spawnTimer = 0;

    // Sword slash multi-hit tracking
    private int swordHitsRemaining = 0;
    private int swordHitTimer = 0;
    private LivingEntity swordTarget = null;

    // Death particle timing
    private int deathTimer = 0;
    private boolean deathParticlesPlayed = false;

    public DaemoniumIgnisEntity(EntityType<? extends DaemoniumIgnisEntity> type, Level level) {
        super(type, level);
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ARMOR, 7.0D)
                .add(Attributes.FOLLOW_RANGE, 25.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.FLYING_SPEED, 0.23D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new DaemoniumIgnisAttackGoal(this));
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
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)) {
            return true;
        }
        if (!hasSpawned) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    public boolean isAttacking() {
        return attackAnimTimer > 0;
    }

    @Override
    public void tick() {
        super.tick();

        // Spawn phase
        if (!hasSpawned) {
            spawnTimer++;
            if (spawnTimer == 1) {
                playSpawnSounds();
            }
            if (spawnTimer >= 50) {
                hasSpawned = true;
            }
        }

        // Attack animation timer + self-stun
        if (attackAnimTimer > 0) {
            attackAnimTimer--;
            // Freeze movement during attack animations
            setDeltaMovement(Vec3.ZERO);
            if (attackAnimTimer <= 0) {
                setAttackState(ATTACK_NONE);
            }
        }

        if (attackCooldown > 0) {
            attackCooldown--;
        }

        // Sword slash multi-hit
        if (swordHitsRemaining > 0 && swordTarget != null && swordTarget.isAlive()) {
            swordHitTimer--;
            if (swordHitTimer <= 0) {
                swordHitTimer = 4; // 4 ticks between hits
                swordHitsRemaining--;
                float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
                swordTarget.hurt(damageSources().mobAttack(this), damage);
                swordTarget.setDeltaMovement(Vec3.ZERO);
                swordTarget.invulnerableTime = 0; // Reset i-frames for multi-hit
                swordTarget.setRemainingFireTicks(100);
                spawnSwordHitParticles(swordTarget);
                playSwordHitSounds();
                if (swordHitsRemaining <= 0) {
                    swordTarget = null;
                }
            }
        }

        // Slow falling - keeps mob floating gently
        if (!level().isClientSide && hasSpawned && !onGround()) {
            if (!hasEffect(MobEffects.SLOW_FALLING)) {
                addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 0, false, false));
            }
        }

        // Death particles (delayed smoke burst)
        if (isDeadOrDying()) {
            deathTimer++;
            if (!deathParticlesPlayed && deathTimer >= 35) {
                deathParticlesPlayed = true;
                spawnDeathParticles();
            }
        }

        // Ambient particles (client-side)
        if (level().isClientSide) {
            spawnAmbientParticles();
        }
    }

    // ---- Ambient particles ----

    private void spawnAmbientParticles() {
        double x = getX();
        double y = getY() + 1.2;
        double z = getZ();
        // Smoke cloud around body (~20 particles per 6 ticks from MM)
        for (int i = 0; i < 3; i++) {
            double ox = (random.nextDouble() - 0.5) * 0.8;
            double oy = (random.nextDouble() - 0.5) * 1.2;
            double oz = (random.nextDouble() - 0.5) * 0.8;
            level().addParticle(ParticleTypes.SMOKE, x + ox, y + oy, z + oz, 0, 0.013, 0);
        }
        if (random.nextInt(3) == 0) {
            level().addParticle(ParticleTypes.FLAME,
                    x + (random.nextDouble() - 0.5) * 0.5,
                    y + (random.nextDouble() - 0.5) * 0.5,
                    z + (random.nextDouble() - 0.5) * 0.5,
                    0, 0.02, 0);
        }
    }

    // ---- Attack: Fireball (MM attack_1) ----

    public void performFireballAttack(LivingEntity target) {
        if (level().isClientSide) return;

        setAttackState(ATTACK_FIREBALL);
        attackAnimTimer = 65;
        attackCooldown = 65;

        Vec3 origin = position().add(0, getBbHeight() * 0.7, 0);
        Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
        Vec3 direction = targetPos.subtract(origin).normalize();

        Vec3 velocity = direction.scale(1.5);
        LargeFireball fireball = new LargeFireball(level(), this, velocity, 1);
        fireball.setPos(origin.add(direction));
        level().addFreshEntity(fireball);

        playFireballSounds();
    }

    private void playFireballSounds() {
        playLayeredSound(SoundEvents.BLAZE_SHOOT, 1.0F, 0.85F + random.nextFloat() * 0.15F);
        playLayeredSound(SoundEvents.BREEZE_SHOOT, 0.6F, 1.25F);
        playLayeredSound(SoundEvents.SHULKER_SHOOT, 1.0F, 1.75F);
        playLayeredSound(SoundEvents.WITHER_SHOOT, 4.0F, 1.70F);
    }

    // ---- Attack: Ground Slam (MM attack_2) - close range AoE ----

    public void performSlamAttack(LivingEntity target) {
        if (level().isClientSide) return;

        setAttackState(ATTACK_SLAM);
        attackAnimTimer = 50;
        attackCooldown = 50;

        // Delayed damage at impact point - schedule via tick counting
        // For now, apply damage immediately to all nearby entities
        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
                target.getBoundingBox().inflate(2.5), e -> e != this && e instanceof Player)) {
            entity.hurt(damageSources().mobAttack(this), damage);
            entity.invulnerableTime = 0;
            entity.setRemainingFireTicks(100);
        }

        spawnSlamParticles(target);
        playSlamSounds();
    }

    private void spawnSlamParticles(LivingEntity target) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLAME,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    13, 0.35, 0.4, 0.35, 0.1);
            serverLevel.sendParticles(ParticleTypes.LAVA,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    13, 0.35, 0.4, 0.35, 0.1);
        }
    }

    private void playSlamSounds() {
        playLayeredSound(SoundEvents.PLAYER_ATTACK_STRONG, 0.5F, 1.50F);
        playLayeredSound(SoundEvents.PLAYER_ATTACK_NODAMAGE, 1.0F, 0.55F);
        playLayeredSound(SoundEvents.PLAYER_ATTACK_WEAK, 0.8F, 1.42F);
        playLayeredSound(SoundEvents.FIRE_EXTINGUISH, 0.4F, 1.00F);
        playLayeredSound(SoundEvents.FIRECHARGE_USE, 0.5F, 0.75F);
        playLayeredSound(SoundEvents.BLAZE_SHOOT, 0.3F, 1.00F);
        playLayeredSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.00F);
        playLayeredSound(SoundEvents.TRIDENT_RETURN, 1.0F, 1.58F);
    }

    // ---- Attack: Sword Slash (MM attack_3) - melee multi-hit ----

    public void performSwordAttack(LivingEntity target) {
        if (level().isClientSide) return;

        setAttackState(ATTACK_SWORD);
        attackAnimTimer = 65;
        attackCooldown = 65;

        // Apply slowness IV to target (3 seconds = 60 ticks)
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 4));

        // Start multi-hit sequence (4 hits like MM sword totem)
        swordHitsRemaining = 4;
        swordHitTimer = 8; // First hit after brief delay
        swordTarget = target;

        playSwordStartSounds();
    }

    private void playSwordStartSounds() {
        playLayeredSound(SoundEvents.DROWNED_SHOOT, 0.3F, 1.00F);
        playLayeredSound(SoundEvents.BREEZE_LAND, 1.0F, 1.30F);
        playLayeredSound(SoundEvents.BREEZE_SHOOT, 0.3F, 1.50F);
        playLayeredSound(SoundEvents.PLAYER_ATTACK_WEAK, 1.0F, 1.00F);
    }

    private void playSwordHitSounds() {
        playLayeredSound(SoundEvents.WITHER_SHOOT, 0.2F, 1.70F);
        playLayeredSound(SoundEvents.TRIDENT_RETURN, 0.6F, 1.58F);
        playLayeredSound(SoundEvents.IRON_GOLEM_HURT, 0.6F, 1.70F);
        playLayeredSound(SoundEvents.FIRECHARGE_USE, 1.0F, 1.38F);
        playLayeredSound(SoundEvents.BREEZE_SHOOT, 1.0F, 2.00F);
    }

    private void spawnSwordHitParticles(LivingEntity target) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLAME,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    13, 0.35, 0.4, 0.35, 0.1);
            serverLevel.sendParticles(ParticleTypes.LAVA,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    13, 0.35, 0.4, 0.35, 0.1);
        }
    }

    // ---- Death particles ----

    private void spawnDeathParticles() {
        if (level() instanceof ServerLevel serverLevel) {
            // Smoke burst on death (MM: repeat=24, interval=1)
            for (int i = 0; i < 24; i++) {
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                        getX(), getY() + 0.2, getZ(),
                        35, 1.5, 0.2, 1.5, 0.025);
            }
        }
    }

    // ---- Spawn sounds ----

    private void playSpawnSounds() {
        if (level().isClientSide) return;
        playLayeredSound(SoundEvents.FROGSPAWN_BREAK, 0.5F, 0.77F);
        playLayeredSound(SoundEvents.TRIAL_SPAWNER_AMBIENT_OMINOUS, 0.6F, 0.75F);
        playLayeredSound(SoundEvents.BLAZE_AMBIENT, 0.9F, 0.80F);
        playLayeredSound(SoundEvents.WARDEN_HEARTBEAT, 0.8F, 0.80F);
        playLayeredSound(SoundEvents.WITHER_SPAWN, 0.4F, 1.50F);
    }

    // ---- Ambient sounds (layered, 75% chance every ~60 ticks) ----

    @Override
    protected SoundEvent getAmbientSound() {
        // Return null - we handle ambient sounds manually for layering
        return null;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 60;
    }

    @Override
    public void playAmbientSound() {
        if (random.nextFloat() > 0.75F) return;
        playLayeredSound(SoundEvents.BLAZE_AMBIENT, 1.0F, 0.80F);
        playLayeredSound(SoundEvents.SOUL_ESCAPE.value(), 1.0F, 0.90F);
        playLayeredSound(SoundEvents.BREEZE_INHALE, 1.0F, 0.65F);
    }

    // ---- Hurt sounds (layered) ----

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        // Return null - we handle hurt sounds manually for layering
        return null;
    }

    @Override
    public void playHurtSound(DamageSource source) {
        playLayeredSound(SoundEvents.BLAZE_HURT, 1.0F, 0.90F);
        playLayeredSound(SoundEvents.BREEZE_HURT, 0.5F, 1.60F);
        playLayeredSound(SoundEvents.GUARDIAN_HURT, 0.6F, 1.00F);
        playLayeredSound(SoundEvents.IRON_GOLEM_HURT, 0.4F, 1.16F);
    }

    // ---- Death sounds (layered) ----

    @Override
    protected SoundEvent getDeathSound() {
        // Return null - we handle death sounds manually for layering
        return null;
    }

    @Override
    public void die(DamageSource source) {
        playLayeredSound(SoundEvents.BLAZE_DEATH, 1.0F, 0.80F);
        playLayeredSound(SoundEvents.HUSK_DEATH, 1.0F, 0.70F);
        playLayeredSound(SoundEvents.IRON_GOLEM_DEATH, 0.4F, 0.80F);
        playLayeredSound(SoundEvents.PHANTOM_DEATH, 0.6F, 0.80F);
        super.die(source);
    }

    // ---- Utility ----

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

    // ---- Geckolib animation ----

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Main body controller - handles spawn, idle, walk, attacks, death
        controllers.add(new AnimationController<>(this, "body", 10, state -> {
            if (isDeadOrDying()) {
                return state.setAndContinue(DEATH_ANIM);
            }
            if (!hasSpawned) {
                return state.setAndContinue(SPAWN_ANIM);
            }

            int attackState = getAttackState();
            if (attackState == ATTACK_FIREBALL) {
                return state.setAndContinue(ATTACK_1_ANIM);
            } else if (attackState == ATTACK_SLAM) {
                return state.setAndContinue(ATTACK_2_ANIM);
            } else if (attackState == ATTACK_SWORD) {
                return state.setAndContinue(ATTACK_3_ANIM);
            }

            if (state.isMoving()) {
                return state.setAndContinue(WALK_ANIM);
            }
            return state.setAndContinue(IDLE_ANIM);
        }));

        // Blaze rod spin controller (always active, independent)
        controllers.add(new AnimationController<>(this, "spin", 0, state ->
                state.setAndContinue(SPIN_ANIM)));

        // Sword orbit spin controller (always active, independent)
        controllers.add(new AnimationController<>(this, "sword_spin", 0, state ->
                state.setAndContinue(SWORD_SPIN_ANIM)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
