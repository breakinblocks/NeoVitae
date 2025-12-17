package com.breakinblocks.neovitae.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import com.breakinblocks.neovitae.common.effect.BMMobEffects;
import com.breakinblocks.neovitae.common.entity.BMEntities;
import com.breakinblocks.neovitae.common.item.BMItems;

/**
 * Soul Snare projectile - marks hostile mobs for demon will drops on death.
 */
public class SoulSnareEntity extends ThrowableItemProjectile {

    public SoulSnareEntity(EntityType<? extends SoulSnareEntity> type, Level level) {
        super(type, level);
    }

    public SoulSnareEntity(Level level, LivingEntity shooter) {
        super(BMEntities.SOUL_SNARE.get(), shooter, level);
    }

    public SoulSnareEntity(Level level, double x, double y, double z) {
        super(BMEntities.SOUL_SNARE.get(), x, y, z, level);
    }

    @Override
    protected Item getDefaultItem() {
        return BMItems.SOUL_SNARE.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        if (!level().isClientSide() && result.getEntity() instanceof LivingEntity target) {
            // Only affect hostile mobs
            if (target instanceof Enemy) {
                // Apply the soul snare effect
                target.addEffect(new MobEffectInstance(BMMobEffects.SOUL_SNARE, 1200, 0, false, true));

                // Spawn particles to indicate success
                for (int i = 0; i < 8; i++) {
                    level().addParticle(ParticleTypes.ENCHANT,
                            target.getX() + (random.nextDouble() - 0.5) * target.getBbWidth(),
                            target.getY() + random.nextDouble() * target.getBbHeight(),
                            target.getZ() + (random.nextDouble() - 0.5) * target.getBbWidth(),
                            0, 0.1, 0);
                }
            }
            discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide()) {
            discard();
        }
    }

    @Override
    public void tick() {
        super.tick();

        // Spawn trailing particles
        if (level().isClientSide()) {
            level().addParticle(ParticleTypes.ENCHANT,
                    getX(), getY(), getZ(),
                    0, 0, 0);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }
}
