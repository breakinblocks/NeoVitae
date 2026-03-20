package com.breakinblocks.neovitae.common.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForgeMod;
import com.breakinblocks.neovitae.NeoVitae;

public class HeavyHeartEffect extends MobEffect {

    private static final ResourceLocation HEAVY_HEART_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "effect.heavy_heart");

    public HeavyHeartEffect(MobEffectCategory category, int color) {
        super(category, color);
        // MULTIPLY_TOTAL with 0 zeroes the attribute, forcibly disabling flight
        addAttributeModifier(
                NeoForgeMod.CREATIVE_FLIGHT,
                HEAVY_HEART_MODIFIER_ID,
                0.0,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.onGround() && entity.getDeltaMovement().y > -1.0) {
            double downwardForce = 0.05 * (amplifier + 1);
            entity.setDeltaMovement(entity.getDeltaMovement().add(0, -downwardForce, 0));
        }

        // Attribute disables mayfly, but we must also cancel active flight mid-air
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
        return true;
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player && !player.isCreative() && !player.isSpectator()) {
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }
}
