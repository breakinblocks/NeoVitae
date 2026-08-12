package com.breakinblocks.neovitae.mixin;

import com.breakinblocks.neovitae.common.fluid.EssentiaLogging;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(WallBlock.class)
public class WallBlockMixin {

    @Redirect(method = {"getShape", "getCollisionShape"},
            at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object neovitae$shapeForEssentiaLoggedState(Map<BlockState, VoxelShape> shapes, Object key) {
        VoxelShape shape = shapes.get(key);
        if (shape == null && key instanceof BlockState state && EssentiaLogging.isLogged(state)) {
            shape = shapes.get(state.setValue(EssentiaLogging.ESSENTIA_LOGGED, false));
        }
        return shape;
    }
}
