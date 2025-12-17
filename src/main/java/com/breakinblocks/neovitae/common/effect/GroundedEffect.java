package com.breakinblocks.neovitae.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Grounded effect - prevents jumping.
 * Uses a negative jump strength modifier to prevent jumps.
 */
public class GroundedEffect extends MobEffect {

    public GroundedEffect(MobEffectCategory category, int color) {
        super(category, color);
        // Apply a massive negative jump strength modifier
        addAttributeModifier(
                Attributes.JUMP_STRENGTH,
                NeoVitae.rl("effect.grounded"),
                -1.0, // Completely negate jump
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }
}
