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
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
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

public class DaemoniumGlaciarisEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Integer> ATTACK_STATE =
            SynchedEntityData.defineId(DaemoniumGlaciarisEntity.class, EntityDataSerializers.INT);

    public static final int ATTACK_NONE = 0;
    public static final int ATTACK_BASIC = 1;       // Quick ice projectile
    public static final int ATTACK_ICE_BEAM = 2;    // Channeled beam
    public static final int ATTACK_ROSE_TOSS = 3;   // Ice rose projectile
    public static final int ATTACK_SHARDS = 4;      // Close-range AoE ice burst
    public static final int ATTACK_WALL_SUMMON = 5;  // Summon ice wall + golem
    public static final int ATTACK_MIST = 6;        // Mist phase (invulnerable)

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_glaciaris.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_glaciaris.walk");
    private static final RawAnimation BASIC_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_glaciaris.basic");
    private static final RawAnimation ICE_BEAM_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_glaciaris.ice_beam");
    private static final RawAnimation ROSE_TOSS_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_glaciaris.rose_toss");
    private static final RawAnimation SHARDS_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_glaciaris.shards");
    private static final RawAnimation WALL_SUMMON_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_glaciaris.wall_summon");
    private static final RawAnimation MIST_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_glaciaris.mist");
    private static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlayAndHold("animation.daemonium_glaciaris.death");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int attackCooldown = 0;
    private int attackAnimTimer = 0;

    // Ice beam tracking
    private int beamTicksRemaining = 0;
    private LivingEntity beamTarget = null;

    // Mist phase
    private int mistTicksRemaining = 0;

    public DaemoniumGlaciarisEntity(EntityType<? extends DaemoniumGlaciarisEntity> type, Level level) {
        super(type, level);
        this.xpReward = 30;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 250.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 30.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new DaemoniumGlaciarisAttackGoal(this));
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
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        if (mistTicksRemaining > 0) {
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

        // Ice beam continuous damage
        if (!level().isClientSide && beamTicksRemaining > 0 && beamTarget != null && beamTarget.isAlive()) {
            beamTicksRemaining--;
            if (beamTicksRemaining % 4 == 0) {
                beamTarget.hurt(damageSources().mobAttack(this), 0.25F);
                beamTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2));
            }
            if (beamTicksRemaining <= 0) {
                beamTarget = null;
            }
        }

        // Mist phase countdown
        if (mistTicksRemaining > 0) {
            mistTicksRemaining--;
        }

        if (level().isClientSide) {
            spawnAmbientParticles();
        }
    }

    private void spawnAmbientParticles() {
        if (random.nextInt(3) == 0) {
            double x = getX() + (random.nextDouble() - 0.5) * 0.8;
            double y = getY() + 0.5 + random.nextDouble() * 1.5;
            double z = getZ() + (random.nextDouble() - 0.5) * 0.8;
            level().addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0, 0.02, 0);
        }
        if (mistTicksRemaining > 0) {
            for (int i = 0; i < 5; i++) {
                double x = getX() + (random.nextDouble() - 0.5) * 3;
                double y = getY() + random.nextDouble() * 2;
                double z = getZ() + (random.nextDouble() - 0.5) * 3;
                level().addParticle(ParticleTypes.CLOUD, x, y, z, 0, 0.01, 0);
            }
        }
    }

    // ---- Attack: Basic shot (MM basic) - quick ice projectile ----
    public void performBasicAttack(LivingEntity target) {
        if (level().isClientSide) return;
        setAttackState(ATTACK_BASIC);
        attackAnimTimer = 30;
        attackCooldown = 35;

        target.hurt(damageSources().mobAttack(this), 10.0F);
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));

        playLayeredSound(SoundEvents.TRIDENT_RETURN, 1.0F, 2.0F);
        spawnIceParticles(target);
    }

    // ---- Attack: Ice Beam (MM ice_beam) - channeled damage ----
    public void performIceBeamAttack(LivingEntity target) {
        if (level().isClientSide) return;
        setAttackState(ATTACK_ICE_BEAM);
        attackAnimTimer = 100;
        attackCooldown = 100;

        beamTarget = target;
        beamTicksRemaining = 45;

        playLayeredSound(SoundEvents.ILLUSIONER_CAST_SPELL, 1.0F, 1.2F);
        playLayeredSound(SoundEvents.ALLAY_THROW, 15.0F, 0.1F);
        playLayeredSound(SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, 15.0F, 1.0F);
    }

    // ---- Attack: Rose Toss (MM rose_toss) - ice projectile + thorns ----
    public void performRoseTossAttack(LivingEntity target) {
        if (level().isClientSide) return;
        setAttackState(ATTACK_ROSE_TOSS);
        attackAnimTimer = 90;
        attackCooldown = 90;

        target.hurt(damageSources().mobAttack(this), 10.0F);
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));

        spawnIceParticles(target);
        playLayeredSound(SoundEvents.TRIDENT_RETURN, 1.0F, 2.0F);
    }

    // ---- Attack: Shards (MM shards) - close-range AoE burst ----
    public void performShardsAttack(LivingEntity target) {
        if (level().isClientSide) return;
        setAttackState(ATTACK_SHARDS);
        attackAnimTimer = 70;
        attackCooldown = 70;

        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(4.0), e -> e != this && e instanceof Player)) {
            entity.hurt(damageSources().mobAttack(this), 15.0F);
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3));
        }

        // Layered trident sounds (6x from MM)
        for (int i = 0; i < 6; i++) {
            playLayeredSound(SoundEvents.TRIDENT_RETURN, 1.0F, 2.0F);
        }

        spawnShardParticles();
    }

    // ---- Attack: Wall Summon (MM wall_summon) - defensive ----
    public void performWallSummonAttack(LivingEntity target) {
        if (level().isClientSide) return;
        setAttackState(ATTACK_WALL_SUMMON);
        attackAnimTimer = 90;
        attackCooldown = 600; // 30 second cooldown like MM

        // Heal self during this defensive move
        heal(20.0F);

        playLayeredSound(SoundEvents.GLASS_BREAK, 1.0F, 0.5F);
        playLayeredSound(SoundEvents.ILLUSIONER_CAST_SPELL, 1.0F, 0.8F);

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    getX(), getY() + 1.5, getZ(), 40, 2.0, 1.5, 2.0, 0.05);
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    getX(), getY() + 1.0, getZ(), 20, 1.5, 1.0, 1.5, 0.02);
        }
    }

    // ---- Attack: Mist Phase (MM mist) - invulnerable phase ----
    public void performMistAttack() {
        if (level().isClientSide) return;
        setAttackState(ATTACK_MIST);
        attackAnimTimer = 400;
        attackCooldown = 400;
        mistTicksRemaining = 396;

        playLayeredSound(SoundEvents.ILLUSIONER_CAST_SPELL, 1.0F, 0.6F);

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CLOUD,
                    getX(), getY() + 1.0, getZ(), 50, 2.0, 1.5, 2.0, 0.05);
        }
    }

    // ---- Particles ----
    private void spawnIceParticles(LivingEntity target) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    15, 0.5, 0.5, 0.5, 0.05);
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    5, 0.3, 0.3, 0.3, 0.02);
        }
    }

    private void spawnShardParticles() {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    getX(), getY() + 1.0, getZ(), 30, 3.0, 1.0, 3.0, 0.1);
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    getX(), getY() + 1.0, getZ(), 15, 2.0, 0.8, 2.0, 0.05);
        }
    }

    // ---- Sounds ----
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 100;
    }

    @Override
    public void playAmbientSound() {
        playLayeredSound(SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, 1.0F, 1.2F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Override
    public void playHurtSound(DamageSource source) {
        if (source.is(DamageTypeTags.BYPASSES_ARMOR)) return;
        playLayeredSound(SoundEvents.ALLAY_HURT, 1.0F, 2.0F);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    public void die(DamageSource source) {
        playLayeredSound(SoundEvents.ALLAY_DEATH, 1.0F, 1.0F);
        super.die(source);
    }

    private void playLayeredSound(SoundEvent sound, float volume, float pitch) {
        level().playSound(null, getX(), getY(), getZ(), sound, SoundSource.HOSTILE, volume, pitch);
    }

    public boolean canAttack() {
        return attackCooldown <= 0 && !isAttacking();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MistTicks", mistTicksRemaining);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        mistTicksRemaining = tag.getInt("MistTicks");
    }

    // ---- Geckolib ----
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "body", 10, state -> {
            if (isDeadOrDying()) {
                return state.setAndContinue(DEATH_ANIM);
            }

            int attackState = getAttackState();
            switch (attackState) {
                case ATTACK_BASIC: return state.setAndContinue(BASIC_ANIM);
                case ATTACK_ICE_BEAM: return state.setAndContinue(ICE_BEAM_ANIM);
                case ATTACK_ROSE_TOSS: return state.setAndContinue(ROSE_TOSS_ANIM);
                case ATTACK_SHARDS: return state.setAndContinue(SHARDS_ANIM);
                case ATTACK_WALL_SUMMON: return state.setAndContinue(WALL_SUMMON_ANIM);
                case ATTACK_MIST: return state.setAndContinue(MIST_ANIM);
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
