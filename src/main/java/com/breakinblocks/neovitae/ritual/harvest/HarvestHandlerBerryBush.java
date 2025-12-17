package com.breakinblocks.neovitae.ritual.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Harvest handler for Sweet Berry Bushes.
 */
public class HarvestHandlerBerryBush implements IHarvestHandler {

    @Override
    public boolean harvest(Level level, BlockPos pos, BlockState state, List<ItemStack> drops, @Nullable UUID ownerUUID) {
        if (test(level, pos, state)) {
            // Check protection before modifying block
            BlockState newState = state.setValue(SweetBerryBushBlock.AGE, 1);
            if (!BlockProtectionHelper.tryReplaceBlock(level, pos, newState, ownerUUID)) {
                return false;
            }
            int berries = 2 + level.random.nextInt(2);
            Block.popResource(level, pos, new ItemStack(Items.SWEET_BERRIES, berries));
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
                    1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            return true;
        }
        return false;
    }

    @Override
    public boolean test(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof SweetBerryBushBlock) {
            return state.getValue(SweetBerryBushBlock.AGE) >= 3;
        }
        return false;
    }
}
