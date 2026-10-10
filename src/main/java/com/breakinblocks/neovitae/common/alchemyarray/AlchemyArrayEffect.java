// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;

import java.util.List;

public abstract class AlchemyArrayEffect {
    private static final int PROCESSING_GRACE_TICKS = 5;

    private int evCost;

    public static void markProcessing(ItemEntity item) {
        item.setData(NVDataAttachments.ARRAY_PROCESSING_UNTIL, item.level().getGameTime() + PROCESSING_GRACE_TICKS);
    }

    public static boolean isBeingProcessed(ItemEntity item) {
        return item.hasData(NVDataAttachments.ARRAY_PROCESSING_UNTIL)
                && item.getData(NVDataAttachments.ARRAY_PROCESSING_UNTIL) >= item.level().getGameTime();
    }

    public int getEvCost() {
        return evCost;
    }

    public void setEvCost(int evCost) {
        this.evCost = evCost;
    }

    public abstract AlchemyArrayEffect getNewCopy();

    public abstract void readFromNBT(CompoundTag compound);

    public abstract void writeToNBT(CompoundTag compound);

    public abstract boolean update(AlchemyArrayBlockEntity array, int activeCounter);

    public void onEntityCollidedWithBlock(AlchemyArrayBlockEntity tile, Level world, BlockPos pos, BlockState state, Entity entity) {
    }

    /**
     * Called when a block adjacent to the array changes. Effects that maintain
     * a neighbour cache can override this to invalidate it reactively instead
     * of waiting for the next timed rebuild.
     */
    public void onNeighborChanged(AlchemyArrayBlockEntity tile, BlockPos neighborPos) {
    }

    /**
     * Invoked when a player right-clicks an active array. Returning true
     * indicates the effect consumed the interaction; the block will report
     * success and skip its default slot-fill logic.
     */
    public boolean onUse(AlchemyArrayBlockEntity tile, Player player) {
        return false;
    }

    public void onRemoved(AlchemyArrayBlockEntity tile) {
    }

    public List<ItemStack> saveItems() {
        return List.of();
    }

    public void loadItems(List<ItemStack> items) {
    }

    public int getRedstoneSignal(AlchemyArrayBlockEntity tile) {
        return 0;
    }

    public boolean isSignalSource() {
        return false;
    }
}
