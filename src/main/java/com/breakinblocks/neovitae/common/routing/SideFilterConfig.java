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

    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("enabled", enabled);
        tag.putByte("itemMode", (byte) itemMode.ordinal());
        tag.putByte("fluidMode", (byte) fluidMode.ordinal());
        ContainerHelper.saveAllItems(tag, itemGhosts, registries);

        RegistryOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);
        ListTag fluidList = new ListTag();
        for (int i = 0; i < GHOST_SLOTS; i++) {
            FluidStack fluid = fluidGhosts.get(i);
            DataResult<Tag> encoded = FluidStack.OPTIONAL_CODEC.encodeStart(ops, fluid);
            fluidList.add(encoded.result().orElseGet(CompoundTag::new));
        }
        tag.put("FluidGhosts", fluidList);
        return tag;
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        this.enabled = tag.getBoolean("enabled");
        FilterMode[] values = FilterMode.values();

        int itemModeIdx = tag.contains("itemMode", Tag.TAG_BYTE)
                ? tag.getByte("itemMode")
                : tag.getByte("mode");
        this.itemMode = (itemModeIdx >= 0 && itemModeIdx < values.length)
                ? values[itemModeIdx]
                : FilterMode.WHITELIST;
        if (this.itemMode == FilterMode.AUTO_MATCH) {
            this.itemMode = FilterMode.WHITELIST;
        }

        int fluidModeIdx = tag.contains("fluidMode", Tag.TAG_BYTE) ? tag.getByte("fluidMode") : FilterMode.AUTO_MATCH.ordinal();
        this.fluidMode = (fluidModeIdx >= 0 && fluidModeIdx < values.length)
                ? values[fluidModeIdx]
                : FilterMode.AUTO_MATCH;

        for (int i = 0; i < GHOST_SLOTS; i++) {
            itemGhosts.set(i, ItemStack.EMPTY);
        }
        ContainerHelper.loadAllItems(tag, itemGhosts, registries);

        for (int i = 0; i < GHOST_SLOTS; i++) {
            fluidGhosts.set(i, FluidStack.EMPTY);
        }
        if (tag.contains("FluidGhosts", Tag.TAG_LIST)) {
            RegistryOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);
            ListTag fluidList = tag.getList("FluidGhosts", Tag.TAG_COMPOUND);
            int limit = Math.min(fluidList.size(), GHOST_SLOTS);
            for (int i = 0; i < limit; i++) {
                DataResult<FluidStack> parsed = FluidStack.OPTIONAL_CODEC.parse(ops, fluidList.getCompound(i));
                fluidGhosts.set(i, parsed.result().orElse(FluidStack.EMPTY));
            }
        }
    }
}
