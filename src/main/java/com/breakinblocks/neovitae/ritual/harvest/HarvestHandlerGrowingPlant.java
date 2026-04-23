package com.breakinblocks.neovitae.ritual.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class HarvestHandlerGrowingPlant implements IHarvestHandler {

    @Override
    public boolean harvest(Level level, BlockPos pos, BlockState state, List<ItemStack> drops, @Nullable UUID ownerUUID) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        if (!BlockProtectionHelper.tryBreakBlockNoDrops(level, pos, ownerUUID)) {
            return false;
        }
        drops.addAll(HarvestHelper.getDropsAt(serverLevel, pos, state, HarvestHelper.mockHoe()));
        return true;
    }

    @Override
    public boolean test(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof GrowingPlantHeadBlock) {
            // Check above and below since kelp grows up, weeping vines grow down
            BlockState above = level.getBlockState(pos.above());
            BlockState below = level.getBlockState(pos.below());

            return above.getBlock() instanceof GrowingPlantBodyBlock
                    || below.getBlock() instanceof GrowingPlantBodyBlock;
        }
        return false;
    }
}
