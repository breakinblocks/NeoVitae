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
        if (level.isClientSide()) {
            Vec3 vec = player.getLookAngle();
            double wantedVelocity = 1.7;

            if (player.hasEffect(MobEffects.SPEED)) {
                int amplifier = player.getEffect(MobEffects.SPEED).getAmplifier();
                wantedVelocity += 0.3 * (amplifier + 1);
            }

            double verticalBoost = 0;
            if (player.hasEffect(MobEffects.JUMP_BOOST)) {
                int amplifier = player.getEffect(MobEffects.JUMP_BOOST).getAmplifier();
                verticalBoost = 0.2 * (amplifier + 1);
            }

            player.setDeltaMovement(vec.x * wantedVelocity, vec.y * wantedVelocity + verticalBoost, vec.z * wantedVelocity);
        } else {
            player.fallDistance = 0;
        }

        return true;
    }
}
