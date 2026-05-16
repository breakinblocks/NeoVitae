package com.breakinblocks.neovitae.common.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForgeMod;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.particle.NVParticles;

public class HeavyHeartEffect extends MobEffect {

    private static final ResourceLocation HEAVY_HEART_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "effect.heavy_heart");

    public HeavyHeartEffect(MobEffectCategory category, int color) {
        super(category, color);
        // Flight cancellation is handled in applyEffectTick and onEffectAdded
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide && entity.tickCount % 4 == 0) {
            double x = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * 0.6;
            double y = entity.getY() + entity.getRandom().nextDouble() * 0.3;
            double z = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * 0.6;
            entity.level().addParticle(
                    new ColoredParticleOptions(
                            NVParticles.BLOOD_FLAME.get(), 0x8B0000),
                    x, y, z, 0, -0.04, 0);
        }

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
