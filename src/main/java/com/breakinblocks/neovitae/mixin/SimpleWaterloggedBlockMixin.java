package com.breakinblocks.neovitae.mixin;

import com.breakinblocks.neovitae.common.fluid.EssentiaLogging;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleWaterloggedBlock.class)
public interface SimpleWaterloggedBlockMixin {

    @Inject(method = "canPlaceLiquid", at = @At("HEAD"), cancellable = true)
    private void neovitae$canPlaceEssentiaVitae(LivingEntity user, BlockGetter level, BlockPos pos, BlockState state,
                                          Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (EssentiaLogging.isLogged(state)) {
            cir.setReturnValue(false);
        } else if (EssentiaLogging.isEssentiaVitae(fluid)) {
            cir.setReturnValue(EssentiaLogging.canLog(state));
        }
    }

    @Inject(method = "placeLiquid", at = @At("HEAD"), cancellable = true)
    private void neovitae$placeEssentiaVitae(LevelAccessor level, BlockPos pos, BlockState state,
                                       FluidState fluidState, CallbackInfoReturnable<Boolean> cir) {
        if (!EssentiaLogging.isEssentiaVitae(fluidState.getType())) {
            return;
        }
        if (!EssentiaLogging.canLog(state)) {
            cir.setReturnValue(false);
            return;
        }
        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(EssentiaLogging.ESSENTIA_LOGGED, true), 3);
            level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
        }
        cir.setReturnValue(true);
    }

    @Inject(method = "pickupBlock", at = @At("HEAD"), cancellable = true)
    private void neovitae$pickupEssentiaVitae(LivingEntity user, LevelAccessor level, BlockPos pos, BlockState state,
                                        CallbackInfoReturnable<ItemStack> cir) {
        if (!EssentiaLogging.isLogged(state)) {
            return;
        }
        level.setBlock(pos, state.setValue(EssentiaLogging.ESSENTIA_LOGGED, false), 3);
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
        cir.setReturnValue(new ItemStack(NVFluids.ESSENTIA_VITAE_BUCKET.get()));
    }
}
