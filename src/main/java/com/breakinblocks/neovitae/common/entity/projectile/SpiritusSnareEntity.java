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
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.common.entity.NVEntities;
import com.breakinblocks.neovitae.common.item.NVItems;

/**
 * Soul Snare projectile - marks hostile mobs for spiritus drops on death.
 */
public class SpiritusSnareEntity extends ThrowableItemProjectile {

    public SpiritusSnareEntity(EntityType<? extends SpiritusSnareEntity> type, Level level) {
        super(type, level);
    }

    public SpiritusSnareEntity(Level level, LivingEntity shooter) {
        super(NVEntities.SPIRITUS_SNARE.get(), shooter, level);
    }

    public SpiritusSnareEntity(Level level, double x, double y, double z) {
        super(NVEntities.SPIRITUS_SNARE.get(), x, y, z, level);
    }

    @Override
    protected Item getDefaultItem() {
        return NVItems.SPIRITUS_SNARE.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        if (!level().isClientSide() && result.getEntity() instanceof LivingEntity target) {
            if (target instanceof Enemy) {
                target.addEffect(new MobEffectInstance(NVMobEffects.SPIRITUS_SNARE, 1200, 0, false, true));

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
