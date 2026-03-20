package com.breakinblocks.neovitae.common.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForgeMod;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Flight effect - grants creative-style flight while active.
 * Uses the NeoForge CREATIVE_FLIGHT attribute to enable flight.
 * Also resets fall distance every tick and scales flying speed by amplifier.
 *
 * <p>Cleanup on removal (milk, /clear, expiry) is handled by
 * {@link com.breakinblocks.neovitae.common.event.CommonEventHandler}.</p>
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

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // Reset fall distance while flight effect is active
        entity.fallDistance = 0;

        if (entity instanceof Player player) {
            // Scale flying speed by amplifier level
            float targetSpeed = 0.05F * (amplifier + 1);
            if (player.getAbilities().getFlyingSpeed() != targetSpeed) {
                player.getAbilities().setFlyingSpeed(targetSpeed);
                player.onUpdateAbilities();
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            player.getAbilities().setFlyingSpeed(0.05F * (amplifier + 1));
            player.onUpdateAbilities();
        }
    }
}
