package com.breakinblocks.neovitae.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Base class for simple Blood Magic effects that have no special tick logic.
 */
public class NeoVitaeEffect extends MobEffect {

    public NeoVitaeEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
