package com.breakinblocks.neovitae.mixin;

import com.breakinblocks.neovitae.api.fluid.EssentiaLoggingAPI;
import com.breakinblocks.neovitae.common.fluid.EssentiaLogging;
import com.breakinblocks.neovitae.common.fluid.EssentiaLoggingConfig;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "<init>", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/world/level/block/Block;createBlockStateDefinition(Lnet/minecraft/world/level/block/state/StateDefinition$Builder;)V"))
    private void neovitae$addEssentiaLoggingProperty(BlockBehaviour.Properties properties, CallbackInfo ci,
                                                    @Local StateDefinition.Builder<Block, BlockState> builder) {
        if (EssentiaLoggingConfig.isEnabled() && EssentiaLoggingAPI.isSupported((Block) (Object) this)) {
            builder.add(EssentiaLogging.ESSENTIA_LOGGED);
        }
    }
}
