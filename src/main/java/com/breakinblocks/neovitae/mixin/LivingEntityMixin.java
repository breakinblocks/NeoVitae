package com.breakinblocks.neovitae.mixin;

import com.breakinblocks.neovitae.common.sentient.SentientEffectComponents;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    private boolean neovitae$canWalkOnLava() {
        return (Object) this instanceof Player player
                && !player.isShiftKeyDown() && player.hasEffect(MobEffects.FIRE_RESISTANCE)
                && SentientHelper.hasFullSet(player)
                && SentientHelper.has(player, SentientEffectComponents.LAVA_STRIDER.get());
    }

    @Inject(method = "canStandOnFluid", at = @At("RETURN"), cancellable = true)
    private void neovitae$lavaStrider(FluidState fluid, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && fluid.is(FluidTags.LAVA) && neovitae$canWalkOnLava()) {
            cir.setReturnValue(true);
        }
    }
}
