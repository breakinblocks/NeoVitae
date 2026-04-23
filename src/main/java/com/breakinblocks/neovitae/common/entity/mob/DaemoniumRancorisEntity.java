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

public class DaemoniumRancorisEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Integer> ATTACK_STATE =
            SynchedEntityData.defineId(DaemoniumRancorisEntity.class, EntityDataSerializers.INT);

    public static final int ATTACK_NONE = 0;
    public static final int ATTACK_BOLT = 1;    // Spectral bolt: single ranged shot
    public static final int ATTACK_BURST = 2;   // Ectoplasmic explosion: AoE burst at target

    private static final RawAnimation SPAWN_ANIM = RawAnimation.begin().thenPlayAndHold("animation.daemonium_rancoris.spawn");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_rancoris.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_rancoris.walk");
    private static final RawAnimation BOLT_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_rancoris.spectral_bolt");
    private static final RawAnimation BURST_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_rancoris.ectoplasmic_explosion");
    private static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlayAndHold("animation.daemonium_rancoris.death");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int attackCooldown = 0;
    private int attackAnimTimer = 0;
    private boolean hasSpawned = false;
    private int spawnTimer = 0;

    private int deathTimer = 0;
    private boolean deathParticlesPlayed = false;

    public DaemoniumRancorisEntity(EntityType<? extends DaemoniumRancorisEntity> type, Level level) {
        super(type, level);
        this.xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 45.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 25.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new DaemoniumRancorisAttackGoal(this));
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
    public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
        if (!hasSpawned) {
            return true;
        }
        return super.isInvulnerableTo(level, source);
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
        if (random.nextInt(4) == 0) {
            double x = getX() + (random.nextDouble() - 0.5) * 0.8;
            double y = getY() + 0.5 + random.nextDouble() * 1.5;
            double z = getZ() + (random.nextDouble() - 0.5) * 0.8;
            level().addParticle(ParticleTypes.SOUL, x, y, z, 0, 0.02, 0);
        }
        if (random.nextInt(6) == 0) {
            double x = getX() + (random.nextDouble() - 0.5) * 0.6;
            double y = getY() + 0.8 + random.nextDouble() * 1.2;
            double z = getZ() + (random.nextDouble() - 0.5) * 0.6;
            level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0, 0.01, 0);
        }
    }

    // ---- Attack: Spectral Bolt (single ranged shot) ----
    public void performBoltAttack(LivingEntity target) {
        if (level().isClientSide()) return;
        setAttackState(ATTACK_BOLT);
        attackAnimTimer = 35;
        attackCooldown = 70;

        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
        target.hurtServer((ServerLevel) target.level(), damageSources().mobAttack(this), damage);
        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 0));

        spawnBoltParticles(target);
        playLayeredSound(SoundEvents.WITHER_SHOOT, 0.4F, 0.6F);
        playLayeredSound(SoundEvents.PHANTOM_AMBIENT, 0.8F, 0.8F);
    }

    // ---- Attack: Ectoplasmic Burst (AoE at target location) ----
    public void performBurstAttack(LivingEntity target) {
        if (level().isClientSide()) return;
        setAttackState(ATTACK_BURST);
        attackAnimTimer = 37;
        attackCooldown = 100;

        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5F;

        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
                target.getBoundingBox().inflate(3.0), e -> e != this && e instanceof Player)) {
            entity.hurtServer((ServerLevel) entity.level(), damageSources().mobAttack(this), damage);
            entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 1));
        }

        spawnBurstParticles(target);
        playLayeredSound(SoundEvents.WITHER_SHOOT, 0.4F, 0.4F);
        playLayeredSound(SoundEvents.WITHER_HURT, 0.7F, 0.7F);
    }

    // ---- Particles ----
    private void spawnBoltParticles(LivingEntity target) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    15, 0.5, 0.5, 0.5, 0.05);
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    8, 0.3, 0.3, 0.3, 0.02);
        }
    }

    private void spawnBurstParticles(LivingEntity target) {
        if (level() instanceof ServerLevel serverLevel) {
            for (int r = 1; r <= 4; r++) {
                serverLevel.sendParticles(ParticleTypes.SOUL,
                        target.getX(), target.getY() + 0.5, target.getZ(),
                        15 * r, r, 0.5, r, 0.02);
            }
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    20, 2.0, 0.8, 2.0, 0.05);
        }
    }

    private void spawnDeathParticles() {
        if (level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 20; i++) {
                serverLevel.sendParticles(ParticleTypes.SOUL,
                        getX(), getY() + 0.2, getZ(),
                        30, 1.2, 0.2, 1.2, 0.03);
            }
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    getX(), getY() + 0.5, getZ(),
                    40, 0.8, 0.4, 0.8, 0.05);
        }
    }

    // ---- Sounds ----
    private void playSpawnSounds() {
        if (level().isClientSide()) return;
        playLayeredSound(SoundEvents.PHANTOM_AMBIENT, 1.0F, 0.6F);
        playLayeredSound(SoundEvents.SOUL_ESCAPE.value(), 1.0F, 0.8F);
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
        playLayeredSound(SoundEvents.PHANTOM_AMBIENT, 0.75F, 0.8F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Override
    public void playHurtSound(DamageSource source) {
        playLayeredSound(SoundEvents.PHANTOM_HURT, 0.7F, 1.2F);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    public void die(DamageSource source) {
        playLayeredSound(SoundEvents.PHANTOM_DEATH, 1.0F, 1.0F);
        playLayeredSound(SoundEvents.SOUL_ESCAPE.value(), 0.8F, 0.6F);
        super.die(source);
    }

    private void playLayeredSound(SoundEvent sound, float volume, float pitch) {
        level().playSound(null, getX(), getY(), getZ(), sound, SoundSource.HOSTILE, volume, pitch);
    }

    public boolean canAttack() {
        return attackCooldown <= 0 && hasSpawned && !isAttacking();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("HasSpawned", hasSpawned);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
        hasSpawned = tag.getBooleanOr("HasSpawned", false);
        if (hasSpawned) {
            spawnTimer = 50;
        }
    }

    // ---- GeckoLib animation ----
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<DaemoniumRancorisEntity>("body", 10, state -> {
            if (isDeadOrDying()) {
                return state.setAndContinue(DEATH_ANIM);
            }
            if (!hasSpawned) {
                return state.setAndContinue(SPAWN_ANIM);
            }

            int attackState = getAttackState();
            if (attackState == ATTACK_BOLT) {
                return state.setAndContinue(BOLT_ANIM);
            } else if (attackState == ATTACK_BURST) {
                return state.setAndContinue(BURST_ANIM);
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
