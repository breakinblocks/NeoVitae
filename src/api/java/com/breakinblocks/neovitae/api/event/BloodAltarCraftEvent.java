package com.breakinblocks.neovitae.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import com.breakinblocks.neovitae.api.altar.IBloodAltar;
import com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe;

/**
 * Events for Blood Altar crafting operations.
 * Allows mods to hook into altar crafting to modify or cancel operations.
 */
public abstract class BloodAltarCraftEvent extends Event {
    private final IBloodAltar altar;
    private final BloodAltarRecipe recipe;
    private final ItemStack input;
    private ItemStack output;

    protected BloodAltarCraftEvent(IBloodAltar altar, BloodAltarRecipe recipe, ItemStack input, ItemStack output) {
        this.altar = altar;
        this.recipe = recipe;
        this.input = input.copy();
        this.output = output.copy();
    }

    /**
     * Gets the altar performing the craft.
     */
    public IBloodAltar getAltar() {
        return altar;
    }

    /**
     * Gets the recipe being crafted.
     */
    public BloodAltarRecipe getRecipe() {
        return recipe;
    }

    /**
     * Gets a copy of the input item.
     */
    public ItemStack getInput() {
        return input.copy();
    }

    /**
     * Gets a copy of the output item.
     */
    public ItemStack getOutput() {
        return output.copy();
    }

    /**
     * Sets the output item.
     */
    public void setOutput(ItemStack output) {
        this.output = output.copy();
    }

    /**
     * Gets the level the altar is in.
     */
    public Level getLevel() {
        return altar.getLevel();
    }

    /**
     * Gets the position of the altar.
     */
    public BlockPos getPos() {
        return altar.getBlockPos();
    }

    /**
     * Gets the tier of the altar.
     */
    public int getTier() {
        return altar.getTier();
    }

    /**
     * Fired when the altar is about to complete a craft.
     * Cancel to prevent the craft from completing (LP is still consumed).
     * Modify output to change what is produced.
     */
    public static class Crafting extends BloodAltarCraftEvent implements ICancellableEvent {
        public Crafting(IBloodAltar altar, BloodAltarRecipe recipe, ItemStack input, ItemStack output) {
            super(altar, recipe, input, output);
        }
    }

    /**
     * Fired after a craft has been completed successfully.
     * Not cancellable - use for notification purposes only.
     */
    public static class Crafted extends BloodAltarCraftEvent {
        public Crafted(IBloodAltar altar, BloodAltarRecipe recipe, ItemStack input, ItemStack output) {
            super(altar, recipe, input, output);
        }
    }
}
