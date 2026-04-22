package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.ItemStack;
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
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.level.block.state.BlockState;

public class DaemoniumDolorisEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Integer> ATTACK_STATE =
            SynchedEntityData.defineId(DaemoniumDolorisEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_RUNNING =
            SynchedEntityData.defineId(DaemoniumDolorisEntity.class, EntityDataSerializers.BOOLEAN);

    public static final int ATTACK_NONE = 0;
    public static final int ATTACK_COMBO1 = 1;
    public static final int ATTACK_COMBO2 = 2;
    public static final int ATTACK_COMBO3 = 3;    // P2 only: upward slash + spin
    public static final int ATTACK_LEAP = 4;       // Leap slam
    public static final int ATTACK_HOWL = 5;       // P2 only: defensive howl

    private static final RawAnimation SPAWN_ANIM = RawAnimation.begin().thenPlayAndHold("animation.daemonium_doloris.spawn");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_doloris.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_doloris.walk");
    private static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("animation.daemonium_doloris.run");
    private static final RawAnimation COMBO1_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_doloris.combo1");
    private static final RawAnimation COMBO2_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_doloris.combo2");
    private static final RawAnimation COMBO3_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_doloris.combo3");
    private static final RawAnimation LEAP_ANIM = RawAnimation.begin()
            .thenPlay("animation.daemonium_doloris.leap_up")
            .thenLoop("animation.daemonium_doloris.leap_down_loop");
    private static final RawAnimation SMASH_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_doloris.smash");
    private static final RawAnimation HOWL_ANIM = RawAnimation.begin().thenPlay("animation.daemonium_doloris.howl");
    private static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlayAndHold("animation.daemonium_doloris.death");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossBar = new ServerBossEvent(
            UUID.randomUUID(),
            Component.translatable("entity.neovitae.daemonium_doloris"),
            BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
    private boolean isForeman = false;

    private int attackCooldown = 0;
    private int attackAnimTimer = 0;
    private boolean hasSpawned = false;
    private int spawnTimer = 0;
    private int stanceSwitchTimer = 0;
    private int howlCooldown = 0;
    private int leapCooldown = 0;

    // Leap tracking
    private boolean isLeaping = false;
    private int leapTimer = 0;

    // Howl ghost phase
    private int ghostTimer = 0;

    private int deathTimer = 0;
    private boolean deathParticlesPlayed = false;

    public DaemoniumDolorisEntity(EntityType<? extends DaemoniumDolorisEntity> type, Level level) {
        super(type, level);
        this.xpReward = 25;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 30.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.24D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new DaemoniumDolorisAttackGoal(this));
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

    public int getAttackState() { return this.entityData.get(ATTACK_STATE); }
    public void setAttackState(int state) { this.entityData.set(ATTACK_STATE, state); }
    public boolean isRunning() { return this.entityData.get(IS_RUNNING); }
    public void setRunning(boolean running) { this.entityData.set(IS_RUNNING, running); }

    public boolean isPhase2() { return getHealth() < getMaxHealth() * 0.5F; }

    @Override
    public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
        if (!hasSpawned) return true;
        // Ghost howl: 40% chance to negate non-bypass damage
        if (ghostTimer > 0 && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            if (random.nextFloat() < 0.4F) return true;
        }
        return super.isInvulnerableTo(level, source);
    }

    public boolean isAttacking() { return attackAnimTimer > 0; }

    @Override
    public void tick() {
        super.tick();

        if (!hasSpawned) {
            spawnTimer++;
            if (spawnTimer == 1) playSpawnSounds();
            if (spawnTimer >= 50) hasSpawned = true;
        }

        if (attackAnimTimer > 0) {
            attackAnimTimer--;
            if (!isLeaping) setDeltaMovement(Vec3.ZERO);
            if (attackAnimTimer <= 0) {
                setAttackState(ATTACK_NONE);
                isLeaping = false;
            }
        }

        if (attackCooldown > 0) attackCooldown--;
        if (howlCooldown > 0) howlCooldown--;
        if (leapCooldown > 0) leapCooldown--;
        if (ghostTimer > 0) ghostTimer--;

        // Leap logic: apply ground slam damage on landing
        if (isLeaping) {
            leapTimer++;
            if (leapTimer > 10 && onGround()) {
                performSmashLanding();
                isLeaping = false;
            }
        }

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

        if (level().isClientSide()) spawnAmbientParticles();

        if (!level().isClientSide() && isForeman) {
            bossBar.setProgress(getHealth() / getMaxHealth());
        }
    }

    public void setForeman(boolean foreman) {
        this.isForeman = foreman;
        if (foreman) {
            bossBar.setName(Component.translatable("entity.neovitae.daemonium_doloris.foreman"));
            bossBar.setVisible(true);
            getAttribute(Attributes.MAX_HEALTH).setBaseValue(600.0);
            getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(25.0);
            getAttribute(Attributes.ARMOR).setBaseValue(16.0);
            getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(8.0);
            setHealth(getMaxHealth());
        }
    }

    public boolean isForeman() { return isForeman; }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (isForeman) bossBar.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        if (isForeman) bossBar.removePlayer(player);
    }

    private void spawnAmbientParticles() {
        if (ghostTimer > 0 && random.nextInt(2) == 0) {
            double x = getX() + (random.nextDouble() - 0.5) * 1.5;
            double y = getY() + random.nextDouble() * 2.5;
            double z = getZ() + (random.nextDouble() - 0.5) * 1.5;
            level().addParticle(ParticleTypes.SOUL, x, y, z, 0, 0.02, 0);
        }
        if (isRunning() && random.nextInt(3) == 0) {
            double x = getX() + (random.nextDouble() - 0.5) * 0.6;
            double z = getZ() + (random.nextDouble() - 0.5) * 0.6;
            level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, getY() + 0.1, z, 0, 0.05, 0);
        }
    }

    // ---- Attack: Combo 1 (basic melee) ----
    public void performCombo1(LivingEntity target) {
        if (level().isClientSide()) return;
        setAttackState(ATTACK_COMBO1);
        attackAnimTimer = 40;
        attackCooldown = 80;
        dealMeleeDamage(target, 1.0F, 3.0, 0.7);
        playAttackSounds();
    }

    // ---- Attack: Combo 2 (longer melee) ----
    public void performCombo2(LivingEntity target) {
        if (level().isClientSide()) return;
        setAttackState(ATTACK_COMBO2);
        attackAnimTimer = 52;
        attackCooldown = 100;
        dealMeleeDamage(target, 1.0F, 3.0, 0.7);
        playAttackSounds();
    }

    // ---- Attack: Combo 3 (P2 upward + spin, big knockback) ----
    public void performCombo3(LivingEntity target) {
        if (level().isClientSide()) return;
        setAttackState(ATTACK_COMBO3);
        attackAnimTimer = 50;
        attackCooldown = 100;

        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5F;
        Vec3 forward = getForwardHitPos(2.0);
        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(4.0), e -> e != this && e instanceof Player)) {
            if (entity.distanceToSqr(forward.x, forward.y, forward.z) <= 4.0 * 4.0) {
                entity.hurt(damageSources().mobAttack(this), damage);
                entity.invulnerableTime = 0;
                entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1));
                Vec3 kb = entity.position().subtract(position()).normalize().scale(2.5).add(0, 1.5, 0);
                entity.setDeltaMovement(entity.getDeltaMovement().add(kb));
            }
        }
        playAttackSounds();
        playLayeredSound(SoundEvents.WITHER_SHOOT, 0.6F, 0.8F);
    }

    // ---- Attack: Leap Smash ----
    public void performLeap(LivingEntity target) {
        if (level().isClientSide()) return;
        setAttackState(ATTACK_LEAP);
        attackAnimTimer = 60;
        attackCooldown = 80;
        leapCooldown = 140;
        isLeaping = true;
        leapTimer = 0;

        Vec3 dir = target.position().subtract(position()).normalize();
        setDeltaMovement(dir.scale(1.0).add(0, 0.6, 0));

        playLayeredSound(SoundEvents.POLAR_BEAR_WARNING, 0.8F, 0.6F);
    }

    private void performSmashLanding() {
        if (level().isClientSide()) return;
        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5F;
        if (isPhase2()) damage *= 1.33F;

        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(6.0), e -> e != this && e instanceof Player)) {
            entity.hurt(damageSources().mobAttack(this), damage);
            entity.invulnerableTime = 0;
            Vec3 kb = entity.position().subtract(position()).normalize().scale(1.5).add(0, 1.0, 0);
            entity.setDeltaMovement(entity.getDeltaMovement().add(kb));
        }

        setAttackState(ATTACK_NONE);
        attackAnimTimer = 30;
        setAttackState(ATTACK_COMBO1); // Use smash landing anim in the controller

        playLayeredSound(SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, 0.7F, 1.4F);
        playLayeredSound(SoundEvents.WARDEN_ATTACK_IMPACT, 0.7F, 0.4F);
        playLayeredSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 0.7F, 0.6F);

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    getX(), getY() + 0.1, getZ(), 14, 1.1, 0.2, 1.1, 0.15);
        }
    }

    // ---- Attack: Howl (P2 ghost phase) ----
    public void performHowl() {
        if (level().isClientSide()) return;
        setAttackState(ATTACK_HOWL);
        attackAnimTimer = 66;
        attackCooldown = 60;
        howlCooldown = 500; // 25 seconds
        ghostTimer = 220; // 11 seconds of ghost phase

        playLayeredSound(SoundEvents.WITHER_AMBIENT, 0.9F, 1.3F);
        playLayeredSound(SoundEvents.PHANTOM_AMBIENT, 0.9F, 0.7F);

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL,
                    getX(), getY() + 1.5, getZ(), 30, 2.0, 1.0, 2.0, 0.05);
        }
    }

    // ---- Shared melee damage ----
    private void dealMeleeDamage(LivingEntity target, float mult, double range, double kbScale) {
        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE) * mult;
        if (isPhase2()) damage *= 1.5F;
        Vec3 forward = getForwardHitPos(2.0);
        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(range), e -> e != this && e instanceof Player)) {
            if (entity.distanceToSqr(forward.x, forward.y, forward.z) <= range * range) {
                entity.hurt(damageSources().mobAttack(this), damage);
                entity.invulnerableTime = 0;
                Vec3 kb = entity.position().subtract(position()).normalize().scale(kbScale).add(0, 0.2, 0);
                entity.setDeltaMovement(entity.getDeltaMovement().add(kb));
            }
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
                        getX(), getY() + 0.2, getZ(), 30, 1.5, 0.3, 1.5, 0.03);
            }
            serverLevel.sendParticles(ParticleTypes.SOUL,
                    getX(), getY() + 1.0, getZ(), 25, 1.0, 0.5, 1.0, 0.05);
        }
    }

    // ---- Sounds ----
    private void playSpawnSounds() {
        if (level().isClientSide()) return;
        playLayeredSound(SoundEvents.HEAVY_CORE_PLACE, 1.0F, 0.8F);
        playLayeredSound(SoundEvents.POLAR_BEAR_AMBIENT, 0.8F, 0.6F);
    }

    private void playAttackSounds() {
        playLayeredSound(SoundEvents.WITCH_THROW, 0.9F, 0.1F);
        playLayeredSound(SoundEvents.PLAYER_ATTACK_SWEEP, 0.9F, 0.4F);
        playLayeredSound(SoundEvents.GOAT_RAM_IMPACT, 0.9F, 0.8F);
    }

    @Override
    protected SoundEvent getAmbientSound() { return null; }

    @Override
    public int getAmbientSoundInterval() { return 140; }

    @Override
    public void playAmbientSound() {
        if (!hasSpawned) return;
        if (random.nextFloat() > 0.35F) return;
        playLayeredSound(SoundEvents.POLAR_BEAR_AMBIENT, 0.8F, 0.6F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return null; }

    @Override
    public void playHurtSound(DamageSource source) {
        playLayeredSound(SoundEvents.POLAR_BEAR_HURT, 0.8F, 0.8F);
    }

    @Override
    protected SoundEvent getDeathSound() { return null; }

    @Override
    public void die(DamageSource source) {
        playLayeredSound(SoundEvents.POLAR_BEAR_DEATH, 0.8F, 0.45F);
        if (isForeman && level() instanceof ServerLevel sl) {
            spawnAtLocation(sl, new ItemStack(com.breakinblocks.neovitae.common.item.NVItems.MINE_ENTRANCE_KEY.get()));
            bossBar.removeAllPlayers();
        }
        super.die(source);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playLayeredSound(SoundEvents.ZOGLIN_STEP, 1.0F, isRunning() ? 0.4F : 0.6F);
    }

    private void playLayeredSound(SoundEvent sound, float volume, float pitch) {
        level().playSound(null, getX(), getY(), getZ(), sound, SoundSource.HOSTILE, volume, pitch);
    }

    public boolean canAttack() { return attackCooldown <= 0 && hasSpawned && !isAttacking(); }

    @Override
    public float getSpeed() {
        return isRunning() ? super.getSpeed() * 2.0F : super.getSpeed();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("HasSpawned", hasSpawned);
        tag.putBoolean("IsRunning", isRunning());
        tag.putInt("GhostTimer", ghostTimer);
        tag.putBoolean("IsForeman", isForeman);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
        hasSpawned = tag.getBooleanOr("HasSpawned", false);
        if (hasSpawned) spawnTimer = 50;
        setRunning(tag.getBooleanOr("IsRunning", false));
        ghostTimer = tag.getIntOr("GhostTimer", 0);
        if (tag.getBooleanOr("IsForeman", false)) setForeman(true);
    }

    // ---- GeckoLib ----
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<DaemoniumDolorisEntity>("body", 10, state -> {
            if (isDeadOrDying()) return state.setAndContinue(DEATH_ANIM);
            if (!hasSpawned) return state.setAndContinue(SPAWN_ANIM);

            int attackState = getAttackState();
            switch (attackState) {
                case ATTACK_COMBO1: return state.setAndContinue(COMBO1_ANIM);
                case ATTACK_COMBO2: return state.setAndContinue(COMBO2_ANIM);
                case ATTACK_COMBO3: return state.setAndContinue(COMBO3_ANIM);
                case ATTACK_LEAP:
                    return state.setAndContinue(isLeaping ? LEAP_ANIM : SMASH_ANIM);
                case ATTACK_HOWL: return state.setAndContinue(HOWL_ANIM);
            }

            if (state.isMoving()) return state.setAndContinue(isRunning() ? RUN_ANIM : WALK_ANIM);
            return state.setAndContinue(IDLE_ANIM);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
