package com.breakinblocks.neovitae.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class PlantLeechEffect extends MobEffect {

    public PlantLeechEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        NVPotionUtils.damageMobAndGrowSurroundingPlants(entity, 2 + amplifier, 1,
                0.5 * 3 / (amplifier + 3), 25 * (1 + amplifier));
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 10 == 0;
    }
}
