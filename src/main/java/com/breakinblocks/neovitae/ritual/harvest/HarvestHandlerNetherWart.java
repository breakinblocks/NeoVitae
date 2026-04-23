package com.breakinblocks.neovitae.ritual.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class HarvestHandlerNetherWart implements IHarvestHandler {

    @Override
    public boolean harvest(Level level, BlockPos pos, BlockState state, List<ItemStack> drops, @Nullable UUID ownerUUID) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        List<ItemStack> blockDrops = HarvestHelper.getDropsAt(serverLevel, pos, state, HarvestHelper.mockHoe());

        boolean foundSeed = false;
        for (ItemStack stack : blockDrops) {
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof BlockItem blockItem
                    && blockItem.getBlock() == state.getBlock()) {
                stack.shrink(1);
                foundSeed = true;
                break;
            }
        }

        if (foundSeed) {
            BlockState newState = state.getBlock().defaultBlockState();
            if (!BlockProtectionHelper.tryReplaceBlock(level, pos, newState, ownerUUID)) {
                return false;
            }
            level.levelEvent(2001, pos, Block.getId(state));

            for (ItemStack stack : blockDrops) {
                if (!stack.isEmpty()) {
                    drops.add(stack);
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean test(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof NetherWartBlock) {
            return state.getValue(NetherWartBlock.AGE) >= 3;
        }
        return false;
    }
}
