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
 * Harvest handler for vines (jungle vines).
 * Only harvests vines that have another vine directly above them,
 * preserving the top of the vine to allow regrowth.
 */
public class HarvestHandlerVines implements IHarvestHandler {

    @Override
    public boolean harvest(Level level, BlockPos pos, BlockState state, List<ItemStack> drops, @Nullable UUID ownerUUID) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        if (!BlockProtectionHelper.tryBreakBlockNoDrops(level, pos, ownerUUID)) {
            return false;
        }
        // Vines require shears to drop
        drops.addAll(HarvestHelper.getDropsAt(serverLevel, pos, state, HarvestHelper.mockShears()));
        return true;
    }

    @Override
    public boolean test(Level level, BlockPos pos, BlockState state) {
        // Only harvest vines with another vine above to preserve regrowth
        if (state.is(Blocks.VINE)) {
            BlockState above = level.getBlockState(pos.above());
            return above.is(Blocks.VINE);
        }
        return false;
    }
}
