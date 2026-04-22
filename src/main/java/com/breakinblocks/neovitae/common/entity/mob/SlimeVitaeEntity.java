package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.entity.NVEntities;
import com.breakinblocks.neovitae.common.particle.NVParticles;

import java.util.List;
import net.minecraft.world.entity.EntitySpawnReason;

/**
 * Slime of Vitae: a blood-red slime that applies slowness on hit,
 * explodes with instant harm on death, is immune to magic/harm damage,
 * and merges 4 small variants into a larger one.
 */
public class SlimeVitaeEntity extends Slime {

    private static final int MERGE_CHECK_INTERVAL = 40;
    private static final int MERGE_COUNT = 4;
    private static final double MERGE_RANGE = 4.0;

    public SlimeVitaeEntity(EntityType<? extends SlimeVitaeEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void playerTouch(Player player) {
        if (this.isDealsDamage()) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1));
        }
        super.playerTouch(player);
    }

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity target) {
        boolean result = super.doHurtTarget(serverLevel, target);
        if (result && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1));
        }
        return result;
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
        if (source.is(DamageTypes.MAGIC) || source.is(DamageTypes.INDIRECT_MAGIC)) {
            return true;
        }
        return super.isInvulnerableTo(level, source);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level().isClientSide() && level() instanceof ServerLevel serverLevel) {
            int size = getSize();
            float radius = size * 1.5f;
            List<LivingEntity> nearby = serverLevel.getEntitiesOfClass(LivingEntity.class,
                    getBoundingBox().inflate(radius), e -> e != this && e.isAlive());
            for (LivingEntity entity : nearby) {
                if (!(entity instanceof SlimeVitaeEntity)) {
                    entity.addEffect(new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 1, 1));
                }
            }
            serverLevel.sendParticles(ParticleTypes.CRIMSON_SPORE,
                    getX(), getY() + 0.5, getZ(), 30 * size, radius * 0.5, 0.5, radius * 0.5, 0.05);
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0xAA0000),
                    getX(), getY() + 0.5, getZ(), 15 * size, radius * 0.3, 0.3, radius * 0.3, 0.02);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide() && level() instanceof ServerLevel serverLevel
                && tickCount % MERGE_CHECK_INTERVAL == 0 && getSize() == 1) {
            tryMerge(serverLevel);
        }
    }

    private void tryMerge(ServerLevel level) {
        AABB searchBox = getBoundingBox().inflate(MERGE_RANGE);
        List<SlimeVitaeEntity> nearbySmall = level.getEntitiesOfClass(SlimeVitaeEntity.class, searchBox,
                e -> e != this && e.isAlive() && e.getSize() == 1);

        if (nearbySmall.size() >= MERGE_COUNT - 1) {
            double cx = getX();
            double cy = getY();
            double cz = getZ();
            int removed = 0;
            for (SlimeVitaeEntity other : nearbySmall) {
                if (removed >= MERGE_COUNT - 1) break;
                cx += other.getX();
                cy += other.getY();
                cz += other.getZ();
                other.discard();
                removed++;
            }
            cx /= (removed + 1);
            cy /= (removed + 1);
            cz /= (removed + 1);

            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                    cx, cy + 0.5, cz, 40, 1.0, 0.5, 1.0, 0.08);
            level.sendParticles(ParticleTypes.ENCHANT,
                    cx, cy + 1.0, cz, 20, 0.5, 0.5, 0.5, 0.1);

            SlimeVitaeEntity merged = NVEntities.SLIME_VITAE.get().create(level, EntitySpawnReason.MOB_SUMMONED);
            if (merged != null) {
                merged.setSize(2, true);
                merged.snapTo(cx, cy, cz, random.nextFloat() * 360, 0);
                level.addFreshEntity(merged);
            }

            this.discard();
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        int size = this.getSize();
        if (!this.isRemoved() && !this.level().isClientSide() && size > 1 && this.isDeadOrDying()) {
            int count = 2 + this.random.nextInt(3);
            for (int i = 0; i < count; i++) {
                float offset = ((float) (i % 2) - 0.5F) * (float) size / 4.0F;
                float offsetZ = ((float) (i / 2) - 0.5F) * (float) size / 4.0F;
                SlimeVitaeEntity baby = NVEntities.SLIME_VITAE.get().create(this.level(), EntitySpawnReason.MOB_SUMMONED);
                if (baby != null) {
                    if (this.isPersistenceRequired()) {
                        baby.setPersistenceRequired();
                    }
                    baby.setSize(size / 2, true);
                    baby.snapTo(this.getX() + offset, this.getY() + 0.5, this.getZ() + offsetZ,
                            this.random.nextFloat() * 360.0F, 0.0F);
                    if (this.level() instanceof ServerLevel sl) {
                        sl.addFreshEntity(baby);
                    }
                }
            }
        }
        super.remove(reason);
    }
}
