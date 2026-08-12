package com.breakinblocks.neovitae.mixin;

import com.breakinblocks.neovitae.common.fluid.EssentiaLogging;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

    @Shadow
    private FluidState fluidState;

    @Inject(method = "initCache", at = @At("RETURN"))
    private void neovitae$cacheLoggedFluid(CallbackInfo ci) {
        if (EssentiaLogging.isLogged((BlockState) (Object) this)) {
            this.fluidState = EssentiaLogging.loggedFluidState();
        }
    }
}
