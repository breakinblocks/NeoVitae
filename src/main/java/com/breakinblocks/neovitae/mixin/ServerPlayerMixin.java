package com.breakinblocks.neovitae.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.breakinblocks.neovitae.common.world.BoundTreasureLeases;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"
            )
    )
    private boolean neovitae$keepBoundTreasureOpen(boolean original) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        return original || BoundTreasureLeases.keepOpen(self, self.containerMenu);
    }
}
