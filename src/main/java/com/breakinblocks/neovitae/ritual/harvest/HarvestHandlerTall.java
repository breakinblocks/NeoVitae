package com.breakinblocks.neovitae.ritual.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Harvest handler for crops that grow vertically such as Sugar Cane and Cactus.
 * Register a new crop for this handler with {@link HarvestRegistry#registerTallCrop(BlockState)}
 */
public class HarvestHandlerTall implements IHarvestHandler {

    public HarvestHandlerTall() {
        HarvestRegistry.registerTallCrop(Blocks.SUGAR_CANE.defaultBlockState().setValue(SugarCaneBlock.AGE, 0));
        HarvestRegistry.registerTallCrop(Blocks.CACTUS.defaultBlockState().setValue(CactusBlock.AGE, 0));
        HarvestRegistry.registerTallCrop(Blocks.BAMBOO.defaultBlockState()
                .setValue(BambooStalkBlock.STAGE, 0)
                .setValue(BambooStalkBlock.AGE, 1)
                .setValue(BambooStalkBlock.LEAVES, BambooLeaves.NONE));
    }

    @Override
    public boolean harvest(Level level, BlockPos pos, BlockState state, List<ItemStack> drops, @Nullable UUID ownerUUID) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        BlockState up = level.getBlockState(pos.above());
        if (up.getBlock() != state.getBlock()) return false;

        if (!BlockProtectionHelper.tryBreakBlockNoDrops(level, pos.above(), ownerUUID)) {
            return false;
        }
        drops.addAll(HarvestHelper.getDropsAt(serverLevel, pos, state, HarvestHelper.mockHoe()));
        return true;
    }

    @Override
    public boolean test(Level level, BlockPos pos, BlockState state) {
        return HarvestRegistry.getTallCrops().contains(state);
    }
}
