package com.breakinblocks.neovitae.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.SignalGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.breakinblocks.neovitae.common.world.AlternatorLinks;

@Mixin(SignalGetter.class)
public interface SignalGetterMixin {

    @Inject(method = "getSignal", at = @At("HEAD"), cancellable = true)
    private void neovitae$linkedWeakSignal(BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
        if (AlternatorLinks.isPowered(this, pos)) {
            cir.setReturnValue(15);
        }
    }

    @Inject(method = "getDirectSignal", at = @At("HEAD"), cancellable = true)
    private void neovitae$linkedDirectSignal(BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
        if (AlternatorLinks.isPowered(this, pos)) {
            cir.setReturnValue(15);
        }
    }

    @Inject(method = "hasNeighborSignal", at = @At("HEAD"), cancellable = true)
    private void neovitae$linkedNeighborSignal(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (AlternatorLinks.isPowered(this, pos)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getBestNeighborSignal", at = @At("HEAD"), cancellable = true)
    private void neovitae$linkedBestNeighborSignal(BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (AlternatorLinks.isPowered(this, pos)) {
            cir.setReturnValue(15);
        }
    }
}
