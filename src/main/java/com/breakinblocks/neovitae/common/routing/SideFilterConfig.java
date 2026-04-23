package com.breakinblocks.neovitae.common.routing;

import com.mojang.serialization.DataResult;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Per-direction filter configuration stored on a routing node. Default state is
 * disabled with empty item/fluid ghosts; a fresh side routes nothing. Item mode
 * defaults to WHITELIST, fluid mode to AUTO_MATCH (mirror the neighbor tank).
 * Energy is gated only by the enabled flag.
 */
public final class SideFilterConfig {
    public static final int GHOST_SLOTS = 9;

    private boolean enabled;
    private FilterMode itemMode;
    private final NonNullList<ItemStack> itemGhosts;
    private FilterMode fluidMode;
    private final List<FluidStack> fluidGhosts;

    public SideFilterConfig() {
        this.enabled = false;
        this.itemMode = FilterMode.WHITELIST;
        this.itemGhosts = NonNullList.withSize(GHOST_SLOTS, ItemStack.EMPTY);
        this.fluidMode = FilterMode.AUTO_MATCH;
        this.fluidGhosts = new ArrayList<>(GHOST_SLOTS);
        for (int i = 0; i < GHOST_SLOTS; i++) {
            this.fluidGhosts.add(FluidStack.EMPTY);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public FilterMode getItemMode() {
        return itemMode;
    }

    public void setItemMode(FilterMode mode) {
        this.itemMode = (mode == FilterMode.AUTO_MATCH) ? FilterMode.WHITELIST : mode;
    }

    public NonNullList<ItemStack> getItemGhosts() {
        return itemGhosts;
    }

    public ItemStack getItemGhost(int slot) {
        return (slot >= 0 && slot < GHOST_SLOTS) ? itemGhosts.get(slot) : ItemStack.EMPTY;
    }

    public void setItemGhost(int slot, ItemStack stack) {
        if (slot >= 0 && slot < GHOST_SLOTS) {
            itemGhosts.set(slot, stack);
        }
    }

    public void clearItemGhosts() {
        for (int i = 0; i < GHOST_SLOTS; i++) {
            itemGhosts.set(i, ItemStack.EMPTY);
        }
    }

    public FilterMode getFluidMode() {
        return fluidMode;
    }

    public void setFluidMode(FilterMode mode) {
        this.fluidMode = mode;
    }

    public List<FluidStack> getFluidGhosts() {
        return fluidGhosts;
    }

    public FluidStack getFluidGhost(int slot) {
        return (slot >= 0 && slot < GHOST_SLOTS) ? fluidGhosts.get(slot) : FluidStack.EMPTY;
    }

    public void setFluidGhost(int slot, FluidStack stack) {
        if (slot >= 0 && slot < GHOST_SLOTS) {
            fluidGhosts.set(slot, stack == null ? FluidStack.EMPTY : stack.copy());
        }
    }

    public void clearFluidGhosts() {
        for (int i = 0; i < GHOST_SLOTS; i++) {
            fluidGhosts.set(i, FluidStack.EMPTY);
        }
    }

    public void save(ValueOutput out) {
        out.putBoolean("enabled", enabled);
        out.putString("itemMode", itemMode.name());
        ContainerHelper.saveAllItems(out.child("itemGhosts"), itemGhosts);
        out.putString("fluidMode", fluidMode.name());
        out.store("fluidGhosts", FluidStack.OPTIONAL_CODEC.listOf(), fluidGhosts);
    }

    public void load(ValueInput in) {
        enabled = in.getBooleanOr("enabled", false);
        in.getString("itemMode").ifPresent(s -> { try { itemMode = FilterMode.valueOf(s); } catch (IllegalArgumentException ignored) {} });
        in.child("itemGhosts").ifPresent(child -> ContainerHelper.loadAllItems(child, itemGhosts));
        in.getString("fluidMode").ifPresent(s -> { try { fluidMode = FilterMode.valueOf(s); } catch (IllegalArgumentException ignored) {} });
        in.read("fluidGhosts", FluidStack.OPTIONAL_CODEC.listOf()).ifPresent(list -> {
            for (int i = 0; i < GHOST_SLOTS; i++) {
                fluidGhosts.set(i, i < list.size() ? list.get(i) : FluidStack.EMPTY);
            }
        });
    }
}
