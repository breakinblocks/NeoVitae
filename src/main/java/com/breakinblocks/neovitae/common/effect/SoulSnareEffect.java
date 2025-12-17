package com.breakinblocks.neovitae.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Soul Snare effect - marks entities for soul capture on death.
 * The actual soul capture logic is handled in event handlers.
 */
public class SoulSnareEffect extends MobEffect {

    public SoulSnareEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
