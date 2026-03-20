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
 * Cleanup on removal (milk, /clear, expiry) is handled by
 * {@link com.breakinblocks.neovitae.common.event.CommonEventHandler}.
 */
public class FlightEffect extends MobEffect {

    private static final ResourceLocation FLIGHT_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "effect.flight");

    public FlightEffect(MobEffectCategory category, int color) {
        super(category, color);
        // Any value > 0 enables flight per NeoForge CREATIVE_FLIGHT attribute docs
        addAttributeModifier(
                NeoForgeMod.CREATIVE_FLIGHT,
                FLIGHT_MODIFIER_ID,
                1.0,
                AttributeModifier.Operation.ADD_VALUE
        );
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.fallDistance = 0;

        if (entity instanceof Player player) {
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
