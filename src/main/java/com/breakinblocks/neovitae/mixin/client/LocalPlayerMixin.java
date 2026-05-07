package com.breakinblocks.neovitae.mixin.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.breakinblocks.neovitae.common.item.soul.LexVitaeItem;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

    @Shadow
    private ItemStack useItem;

    @Inject(method = "itemUseSpeedMultiplier", at = @At("HEAD"), cancellable = true)
    private void neovitae$lexVitaeNoSlowdown(CallbackInfoReturnable<Float> cir) {
        if (useItem != null && useItem.getItem() instanceof LexVitaeItem && LexVitaeItem.isActive(useItem)) {
            cir.setReturnValue(1.0F);
        }
    }
}
