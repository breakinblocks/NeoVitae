package com.breakinblocks.neovitae.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.common.entity.NVEntities;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

/**
 * Soul Snare projectile - marks hostile mobs for spiritus drops on death.
 */
public class SpiritusSnareEntity extends ThrowableItemProjectile {

    public SpiritusSnareEntity(EntityType<? extends SpiritusSnareEntity> type, Level level) {
        super(type, level);
    }

    public SpiritusSnareEntity(Level level, LivingEntity shooter) {
        super(NVEntities.SPIRITUS_SNARE.get(), shooter, level, new ItemStack(NVItems.SPIRITUS_SNARE.get()));
    }

    public SpiritusSnareEntity(Level level, double x, double y, double z) {
        super(NVEntities.SPIRITUS_SNARE.get(), x, y, z, level, new ItemStack(NVItems.SPIRITUS_SNARE.get()));
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

                if (level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            new ColoredParticleOptions(
                                    NVParticles.BLOOD_GLOW.get(), 0x88CCFF),
                            target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                            10, target.getBbWidth() * 0.4, target.getBbHeight() * 0.4, target.getBbWidth() * 0.4, 0.02);
                    serverLevel.sendParticles(
                            new ColoredParticleOptions(
                                    NVParticles.BLOOD_FLAME.get(), 0x88CCFF),
                            target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                            6, target.getBbWidth() * 0.3, target.getBbHeight() * 0.3, target.getBbWidth() * 0.3, 0.03);
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
            level().addParticle(
                    new ColoredParticleOptions(
                            NVParticles.BLOOD_FLAME.get(), 0x88CCFF),
                    getX() + (random.nextDouble() - 0.5) * 0.1,
                    getY() + (random.nextDouble() - 0.5) * 0.1,
                    getZ() + (random.nextDouble() - 0.5) * 0.1,
                    0, 0.02, 0);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput tag) {
        super.addAdditionalSaveData(tag);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
    }
}
