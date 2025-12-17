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
 * Heavy Heart effect - drags the target down, prevents flight.
 * Affected entities fall faster and cannot fly.
 * Uses CREATIVE_FLIGHT attribute with MULTIPLY_TOTAL 0 to disable flight.
 */
public class HeavyHeartEffect extends MobEffect {

    private static final ResourceLocation HEAVY_HEART_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "effect.heavy_heart");

    public HeavyHeartEffect(MobEffectCategory category, int color) {
        super(category, color);
        // Use MULTIPLY_TOTAL with 0 to forcibly disable creative flight
        addAttributeModifier(
                NeoForgeMod.CREATIVE_FLIGHT,
                HEAVY_HEART_MODIFIER_ID,
                0.0,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // Apply downward force
        if (!entity.onGround() && entity.getDeltaMovement().y > -1.0) {
            double downwardForce = 0.05 * (amplifier + 1);
            entity.setDeltaMovement(entity.getDeltaMovement().add(0, -downwardForce, 0));
        }

        // Stop active flying for players (attribute handles the mayfly permission)
        if (entity instanceof Player player && !player.isCreative() && !player.isSpectator()) {
            if (player.getAbilities().flying) {
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // Apply every tick
        return true;
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        // Stop active flying when effect is added
        if (entity instanceof Player player && !player.isCreative() && !player.isSpectator()) {
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }
}
