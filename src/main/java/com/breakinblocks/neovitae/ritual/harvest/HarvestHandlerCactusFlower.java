package com.breakinblocks.neovitae.ritual.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Harvest handler for the Cactus Flower that grows atop mature cacti (and on the ground).
 * Breaks the flower in place and collects its drop, leaving any cactus beneath untouched.
 */
public class HarvestHandlerCactusFlower implements IHarvestHandler {

    @Override
    public boolean harvest(Level level, BlockPos pos, BlockState state, List<ItemStack> drops, @Nullable UUID ownerUUID) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        List<ItemStack> blockDrops = HarvestHelper.getDropsAt(serverLevel, pos, state, HarvestHelper.mockHoe());
        if (!BlockProtectionHelper.tryBreakBlockNoDrops(level, pos, ownerUUID)) {
            return false;
        }
        drops.addAll(blockDrops);
        return true;
    }

    @Override
    public boolean test(Level level, BlockPos pos, BlockState state) {
        return state.is(Blocks.CACTUS_FLOWER);
    }
}
