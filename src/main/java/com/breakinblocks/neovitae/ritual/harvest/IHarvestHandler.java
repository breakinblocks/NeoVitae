package com.breakinblocks.neovitae.ritual.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Used to define a HarvestHandler for the Harvest Ritual.
 */
public interface IHarvestHandler {

    /**
     * Called whenever the Harvest Ritual attempts to harvest a block.
     * Use this to break the block and plant a new one.
     * Add the items to be dropped to the drops list.
     *
     * @param level     - The world
     * @param pos       - The position of the BlockState being checked
     * @param state     - The BlockState being checked
     * @param drops     - The items to be dropped
     * @param ownerUUID - The UUID of the ritual owner for protection checks
     * @return If the block was successfully harvested.
     */
    boolean harvest(Level level, BlockPos pos, BlockState state, List<ItemStack> drops, @Nullable UUID ownerUUID);

    /**
     * Tests to see if the block is valid for harvest.
     *
     * @param level The world
     * @param pos   The position in the world of the BlockState being checked
     * @param state The BlockState being checked
     * @return if this block is valid for harvest.
     */
    boolean test(Level level, BlockPos pos, BlockState state);
}
