package com.breakinblocks.neovitae.common.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import com.breakinblocks.neovitae.common.blockentity.BloodAltarTile;
import com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe;

/**
 * Events for Blood Altar crafting.
 * Allows mods to hook into altar crafting operations.
 */
public abstract class BloodAltarCraftEvent extends Event {
    private final BloodAltarTile altar;
    private final BloodAltarRecipe recipe;
    private final ItemStack input;
    private ItemStack output;

    public BloodAltarCraftEvent(BloodAltarTile altar, BloodAltarRecipe recipe, ItemStack input, ItemStack output) {
        this.altar = altar;
        this.recipe = recipe;
        this.input = input.copy();
        this.output = output.copy();
    }

    public BloodAltarTile getAltar() {
        return altar;
    }

    public BloodAltarRecipe getRecipe() {
        return recipe;
    }

    public ItemStack getInput() {
        return input.copy();
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public void setOutput(ItemStack output) {
        this.output = output.copy();
    }

    public Level getLevel() {
        return altar.getLevel();
    }

    public BlockPos getPos() {
        return altar.getBlockPos();
    }

    public int getTier() {
        return altar.getTier();
    }

    /**
     * Fired when the altar is about to complete a craft.
     * Cancel to prevent the craft from completing (LP is still consumed).
     * Modify output to change what is produced.
     */
    public static class Crafting extends BloodAltarCraftEvent implements ICancellableEvent {
        public Crafting(BloodAltarTile altar, BloodAltarRecipe recipe, ItemStack input, ItemStack output) {
            super(altar, recipe, input, output);
        }
    }

    /**
     * Fired after a craft has been completed successfully.
     * Not cancellable - use for notification purposes only.
     */
    public static class Crafted extends BloodAltarCraftEvent {
        public Crafted(BloodAltarTile altar, BloodAltarRecipe recipe, ItemStack input, ItemStack output) {
            super(altar, recipe, input, output);
        }
    }
}
