package com.breakinblocks.neovitae.common.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.NeoForgeMod;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Flight effect - grants creative-style flight while active.
 * Uses the NeoForge CREATIVE_FLIGHT attribute to enable flight.
 */
public class FlightEffect extends MobEffect {

    private static final ResourceLocation FLIGHT_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "effect.flight");

    public FlightEffect(MobEffectCategory category, int color) {
        super(category, color);
        // Add attribute modifier to enable creative flight
        // Any value > 0 enables flight according to NeoForge docs
        addAttributeModifier(
                NeoForgeMod.CREATIVE_FLIGHT,
                FLIGHT_MODIFIER_ID,
                1.0,
                AttributeModifier.Operation.ADD_VALUE
        );
    }
}
