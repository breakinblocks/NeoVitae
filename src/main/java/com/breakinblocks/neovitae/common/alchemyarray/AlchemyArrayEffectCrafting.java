package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayTile;
import com.breakinblocks.neovitae.common.event.AlchemyArrayCraftEvent;

public class AlchemyArrayEffectCrafting extends AlchemyArrayEffect {
    public final ItemStack outputStack;
    public int tickLimit;

    public AlchemyArrayEffectCrafting(ItemStack outputStack) {
        this(outputStack, 200);
    }

    public AlchemyArrayEffectCrafting(ItemStack outputStack, int tickLimit) {
        this.outputStack = outputStack;
        this.tickLimit = tickLimit;
    }

    @Override
    public boolean update(AlchemyArrayTile tile, int ticksActive) {
        if (tile.getLevel().isClientSide) {
            return false;
        }

        if (ticksActive >= tickLimit) {
            BlockPos pos = tile.getBlockPos();
            ItemStack baseInput = tile.getItem(0);
            ItemStack addedInput = tile.getItem(1);
            ItemStack output = outputStack.copy();

            // Fire pre-craft event (cancellable)
            AlchemyArrayCraftEvent.Crafting craftingEvent = new AlchemyArrayCraftEvent.Crafting(
                    tile, baseInput, addedInput, output);
            if (NeoForge.EVENT_BUS.post(craftingEvent).isCanceled()) {
                // Cancelled - return false to reset the array
                return false;
            }

            // Use potentially modified output from event
            ItemStack finalOutput = craftingEvent.getOutput();

            ItemEntity outputEntity = new ItemEntity(tile.getLevel(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, finalOutput);
            tile.getLevel().addFreshEntity(outputEntity);

            // Fire post-craft event (not cancellable)
            NeoForge.EVENT_BUS.post(new AlchemyArrayCraftEvent.Crafted(
                    tile, baseInput, addedInput, finalOutput));

            return true;
        }

        return false;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
    }

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectCrafting(outputStack, tickLimit);
    }
}
