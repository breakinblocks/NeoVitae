package com.breakinblocks.neovitae.api.sigil.effects;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;

import java.util.function.Supplier;

/**
 * Air Sigil effect - launches the player in the direction they are looking.
 * Speed effect boosts horizontal velocity, Jump Boost effect boosts vertical velocity.
 */
public record AirSigilEffect() implements SigilEffect {
    public static final MapCodec<AirSigilEffect> CODEC = MapCodec.unit(AirSigilEffect::new);

    public static final Supplier<MapCodec<AirSigilEffect>> REGISTRATION =
            SigilEffectRegistry.SIGIL_EFFECT_TYPES.register("air", () -> CODEC);

    @Override
    public MapCodec<? extends SigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        if (level.isClientSide) {
            Vec3 vec = player.getLookAngle();
            double wantedVelocity = 1.7;

            // Speed effect boosts horizontal velocity
            if (player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                int amplifier = player.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier();
                wantedVelocity += 0.3 * (amplifier + 1);
            }

            // Jump Boost effect boosts vertical velocity
            double verticalBoost = 0;
            if (player.hasEffect(MobEffects.JUMP)) {
                int amplifier = player.getEffect(MobEffects.JUMP).getAmplifier();
                verticalBoost = 0.2 * (amplifier + 1);
            }

            player.setDeltaMovement(vec.x * wantedVelocity, vec.y * wantedVelocity + verticalBoost, vec.z * wantedVelocity);
        } else {
            player.fallDistance = 0;
        }

        return true;
    }
}
