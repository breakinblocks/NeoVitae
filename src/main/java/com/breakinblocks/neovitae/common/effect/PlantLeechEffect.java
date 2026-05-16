package com.breakinblocks.neovitae.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.particle.NVParticles;

public class PlantLeechEffect extends MobEffect {

    public PlantLeechEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) {
            for (int i = 0; i < 2; i++) {
                double x = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * 0.8;
                double y = entity.getY() + entity.getRandom().nextDouble() * entity.getBbHeight();
                double z = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * 0.8;
                entity.level().addParticle(
                        new ColoredParticleOptions(
                                NVParticles.BLOOD_FLAME.get(), 0x22AA22),
                        x, y, z, 0, -0.03, 0);
            }
        }
        NVPotionUtils.damageMobAndGrowSurroundingPlants(entity, 2 + amplifier, 1,
                0.5 * 3 / (amplifier + 3), 25 * (1 + amplifier));
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 10 == 0;
    }
}
