package com.breakinblocks.neovitae.common.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayTile;

/**
 * Events for Alchemy Array crafting.
 * Allows mods to hook into alchemy array crafting operations.
 */
public abstract class AlchemyArrayCraftEvent extends Event {
    private final AlchemyArrayTile array;
    private final ItemStack baseInput;
    private final ItemStack addedInput;
    private ItemStack output;

    public AlchemyArrayCraftEvent(AlchemyArrayTile array, ItemStack baseInput, ItemStack addedInput, ItemStack output) {
        this.array = array;
        this.baseInput = baseInput.copy();
        this.addedInput = addedInput.copy();
        this.output = output.copy();
    }

    public AlchemyArrayTile getArray() {
        return array;
    }

    public ItemStack getBaseInput() {
        return baseInput.copy();
    }

    public ItemStack getAddedInput() {
        return addedInput.copy();
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public void setOutput(ItemStack output) {
        this.output = output.copy();
    }

    public Level getLevel() {
        return array.getLevel();
    }

    public BlockPos getPos() {
        return array.getBlockPos();
    }

    /**
     * Fired when the alchemy array is about to complete a craft.
     * Cancel to prevent the craft from completing (array will reset).
     * Modify output to change what is produced.
     */
    public static class Crafting extends AlchemyArrayCraftEvent implements ICancellableEvent {
        public Crafting(AlchemyArrayTile array, ItemStack baseInput, ItemStack addedInput, ItemStack output) {
            super(array, baseInput, addedInput, output);
        }
    }

    /**
     * Fired after a craft has been completed successfully.
     * Not cancellable - use for notification purposes only.
     */
    public static class Crafted extends AlchemyArrayCraftEvent {
        public Crafted(AlchemyArrayTile array, ItemStack baseInput, ItemStack addedInput, ItemStack output) {
            super(array, baseInput, addedInput, output);
        }
    }
}
