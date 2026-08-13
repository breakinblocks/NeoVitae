package com.breakinblocks.neovitae.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.breakinblocks.neovitae.common.world.BoundTreasureLeases;

@Mixin(Player.class)
public class PlayerMixin {

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"
            )
    )
    private boolean neovitae$keepBoundTreasureOpen(AbstractContainerMenu menu, Player player) {
        return BoundTreasureLeases.keepOpen(player, menu) || menu.stillValid(player);
    }
}
