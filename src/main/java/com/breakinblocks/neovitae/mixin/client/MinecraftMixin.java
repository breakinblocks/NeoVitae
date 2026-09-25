package com.breakinblocks.neovitae.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow
    @Nullable
    public LocalPlayer player;

    @ModifyReturnValue(method = "shouldEntityAppearGlowing", at = @At("RETURN"))
    private boolean neovitae$spectralSight(boolean original, Entity entity) {
        if (original || player == null || entity == player
                || !(entity instanceof LivingEntity) || entity instanceof ArmorStand) {
            return original;
        }
        MobEffectInstance sight = player.getEffect(NVMobEffects.SPECTRAL_SIGHT);
        if (sight == null) {
            return false;
        }
        double range = 24 + 32 * sight.getAmplifier();
        return entity.distanceToSqr(player) <= range * range;
    }
}
