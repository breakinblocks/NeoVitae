package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DaemoniumAnimarisEntity extends Vex {

    private boolean hasSpawned = false;
    private int spawnTimer = 0;

    private int deathTimer = 0;
    private boolean deathParticlesPlayed = false;

    public DaemoniumAnimarisEntity(EntityType<? extends Vex> type, Level level) {
        super(type, level);
        this.xpReward = 15;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 25.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2D);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        // No equipment; this is a spectral entity, not a sword-wielding one
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (!hasSpawned) {
            return true;
        }
        return super.isInvulnerableTo(source);
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

    private void spawnAmbientParticles() {
        if (random.nextInt(5) == 0) {
            double x = getX() + (random.nextDouble() - 0.5) * 0.6;
            double y = getY() + random.nextDouble() * 0.8;
            double z = getZ() + (random.nextDouble() - 0.5) * 0.6;
            level().addParticle(ParticleTypes.SOUL, x, y, z, 0, 0.02, 0);
        }
    }

    private void spawnDeathParticles() {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL,
                    getX(), getY() + 0.3, getZ(),
                    20, 0.5, 0.3, 0.5, 0.05);
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    getX(), getY() + 0.3, getZ(),
                    15, 0.4, 0.2, 0.4, 0.03);
        }
    }

    // ---- Sounds ----
    private void playSpawnSounds() {
        if (level().isClientSide) return;
        playLayeredSound(SoundEvents.PHANTOM_AMBIENT, 0.8F, 0.6F);
        playLayeredSound(SoundEvents.SOUL_ESCAPE.value(), 0.8F, 1.0F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PHANTOM_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PHANTOM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PHANTOM_DEATH;
    }

    @Override
    public void die(DamageSource source) {
        playLayeredSound(SoundEvents.SOUL_ESCAPE.value(), 0.8F, 0.6F);
        super.die(source);
    }

    private void playLayeredSound(SoundEvent sound, float volume, float pitch) {
        level().playSound(null, getX(), getY(), getZ(), sound, SoundSource.HOSTILE, volume, pitch);
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
}
