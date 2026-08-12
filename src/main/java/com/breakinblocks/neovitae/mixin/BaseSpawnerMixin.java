package com.breakinblocks.neovitae.mixin;

import com.breakinblocks.neovitae.ritual.SpawnerSuppression;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseSpawner.class)
public class BaseSpawnerMixin {

    @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true)
    private void neovitae$freezeSuppressedSpawner(ServerLevel level, BlockPos pos, CallbackInfo ci) {
        if (SpawnerSuppression.isSuppressed(level, pos)) {
            ci.cancel();
        }
    }
}
