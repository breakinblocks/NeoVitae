// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.routing;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Per-direction filter configuration stored on a routing node. Item and fluid
 * filters grow on demand: each holds as many ghost entries as the player has set,
 * rendered nine-wide across as many pages as are needed. Default state is disabled
 * with empty filters; a fresh side routes nothing.
 */
public final class SideFilterConfig {
    public static final int PAGE_COLUMNS = 9;
    public static final int PAGE_ROWS = 3;
    public static final int PAGE_SIZE = PAGE_COLUMNS * PAGE_ROWS;

    private boolean enabled;
    private FilterMode itemMode;
    private final List<ItemStack> itemGhosts = new ArrayList<>();
    private final List<Integer> itemAmounts = new ArrayList<>();
    private final List<Set<ResourceLocation>> itemComponents = new ArrayList<>();
    private FilterMode fluidMode;
    private final List<FluidStack> fluidGhosts = new ArrayList<>();
    private final List<Integer> fluidAmounts = new ArrayList<>();

    public SideFilterConfig() {
        this.enabled = false;
        this.itemMode = FilterMode.WHITELIST;
        this.fluidMode = FilterMode.AUTO_MATCH;
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

    public int getItemSlotCount() {
        return itemGhosts.size();
    }

    public ItemStack getItemGhost(int slot) {
        return (slot >= 0 && slot < itemGhosts.size()) ? itemGhosts.get(slot) : ItemStack.EMPTY;
    }

    public void setItemGhost(int slot, ItemStack stack) {
        if (slot < 0) return;
        ensureItemSize(slot + 1);
        itemGhosts.set(slot, stack == null ? ItemStack.EMPTY : stack);
        itemAmounts.set(slot, 0);
        itemComponents.set(slot, Set.of());
        trimTrailingItems();
    }

    public Set<ResourceLocation> getItemComponents(int slot) {
        return (slot >= 0 && slot < itemComponents.size()) ? itemComponents.get(slot) : Set.of();
    }

    public void setItemComponents(int slot, Set<ResourceLocation> components) {
        if (slot >= 0 && slot < itemComponents.size()) {
            itemComponents.set(slot, (components == null || components.isEmpty())
                    ? Set.of() : new LinkedHashSet<>(components));
        }
    }

    public void clearItemGhosts() {
        itemGhosts.clear();
        itemAmounts.clear();
        itemComponents.clear();
    }

    public int getItemAmount(int slot) {
        return (slot >= 0 && slot < itemAmounts.size()) ? itemAmounts.get(slot) : 0;
    }

    public void setItemAmount(int slot, int amount) {
        if (slot >= 0 && slot < itemAmounts.size()) {
            itemAmounts.set(slot, Math.max(0, amount));
        }
    }

    public FilterMode getFluidMode() {
        return fluidMode;
    }

    public void setFluidMode(FilterMode mode) {
        this.fluidMode = mode;
    }

    public int getFluidSlotCount() {
        return fluidGhosts.size();
    }

    public FluidStack getFluidGhost(int slot) {
        return (slot >= 0 && slot < fluidGhosts.size()) ? fluidGhosts.get(slot) : FluidStack.EMPTY;
    }

    public void setFluidGhost(int slot, FluidStack stack) {
        if (slot < 0) return;
        ensureFluidSize(slot + 1);
        fluidGhosts.set(slot, stack == null ? FluidStack.EMPTY : stack.copy());
        fluidAmounts.set(slot, 0);
        trimTrailingFluid();
    }

    public void clearFluidGhosts() {
        fluidGhosts.clear();
        fluidAmounts.clear();
    }

    public int getFluidAmount(int slot) {
        return (slot >= 0 && slot < fluidAmounts.size()) ? fluidAmounts.get(slot) : 0;
    }

    public void setFluidAmount(int slot, int amount) {
        if (slot >= 0 && slot < fluidAmounts.size()) {
            fluidAmounts.set(slot, Math.max(0, amount));
        }
    }

    /** Number of nine-wide pages to display, always leaving one empty page to grow into. */
    public int getPageCount() {
        int used = Math.max(itemGhosts.size(), fluidGhosts.size());
        int pages = (used + PAGE_SIZE - 1) / PAGE_SIZE;
        if (used > 0 && used % PAGE_SIZE == 0) {
            pages += 1;
        }
        return Math.max(1, pages);
    }

    private void ensureItemSize(int size) {
        while (itemGhosts.size() < size) {
            itemGhosts.add(ItemStack.EMPTY);
            itemAmounts.add(0);
            itemComponents.add(Set.of());
        }
    }

    private void ensureFluidSize(int size) {
        while (fluidGhosts.size() < size) {
            fluidGhosts.add(FluidStack.EMPTY);
            fluidAmounts.add(0);
        }
    }

    private void trimTrailingItems() {
        for (int i = itemGhosts.size() - 1; i >= 0; i--) {
            if (itemGhosts.get(i).isEmpty()) {
                itemGhosts.remove(i);
                itemAmounts.remove(i);
                itemComponents.remove(i);
            } else {
                break;
            }
        }
    }

    private void trimTrailingFluid() {
        for (int i = fluidGhosts.size() - 1; i >= 0; i--) {
            if (fluidGhosts.get(i).isEmpty()) {
                fluidGhosts.remove(i);
                fluidAmounts.remove(i);
            } else {
                break;
            }
        }
    }

    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("enabled", enabled);
        tag.putByte("itemMode", (byte) itemMode.ordinal());
        tag.putByte("fluidMode", (byte) fluidMode.ordinal());

        RegistryOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);

        ListTag items = new ListTag();
        for (int i = 0; i < itemGhosts.size(); i++) {
            ItemStack ghost = itemGhosts.get(i);
            if (ghost.isEmpty()) continue;
            CompoundTag entry = new CompoundTag();
            entry.putInt("i", i);
            ItemStack.OPTIONAL_CODEC.encodeStart(ops, ghost).result().ifPresent(t -> entry.put("item", t));
            int amt = itemAmounts.get(i);
            if (amt > 0) entry.putInt("amt", amt);
            Set<ResourceLocation> comps = itemComponents.get(i);
            if (!comps.isEmpty()) {
                ListTag compList = new ListTag();
                for (ResourceLocation id : comps) compList.add(StringTag.valueOf(id.toString()));
                entry.put("comps", compList);
            }
            items.add(entry);
        }
        tag.put("ItemEntries", items);

        ListTag fluids = new ListTag();
        for (int i = 0; i < fluidGhosts.size(); i++) {
            FluidStack ghost = fluidGhosts.get(i);
            if (ghost.isEmpty()) continue;
            CompoundTag entry = new CompoundTag();
            entry.putInt("i", i);
            FluidStack.OPTIONAL_CODEC.encodeStart(ops, ghost).result().ifPresent(t -> entry.put("fluid", t));
            int amt = fluidAmounts.get(i);
            if (amt > 0) entry.putInt("amt", amt);
            fluids.add(entry);
        }
        tag.put("FluidEntries", fluids);

        return tag;
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        this.enabled = tag.getBoolean("enabled");
        FilterMode[] values = FilterMode.values();

        int itemModeIdx = tag.contains("itemMode", Tag.TAG_BYTE)
                ? tag.getByte("itemMode")
                : tag.getByte("mode");
        this.itemMode = (itemModeIdx >= 0 && itemModeIdx < values.length) ? values[itemModeIdx] : FilterMode.WHITELIST;
        if (this.itemMode == FilterMode.AUTO_MATCH) {
            this.itemMode = FilterMode.WHITELIST;
        }

        int fluidModeIdx = tag.contains("fluidMode", Tag.TAG_BYTE) ? tag.getByte("fluidMode") : FilterMode.AUTO_MATCH.ordinal();
        this.fluidMode = (fluidModeIdx >= 0 && fluidModeIdx < values.length) ? values[fluidModeIdx] : FilterMode.AUTO_MATCH;

        itemGhosts.clear();
        itemAmounts.clear();
        fluidGhosts.clear();
        fluidAmounts.clear();

        RegistryOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);

        if (tag.contains("ItemEntries", Tag.TAG_LIST) || tag.contains("FluidEntries", Tag.TAG_LIST)) {
            loadEntries(tag, ops);
        } else {
            loadLegacy(tag, registries, ops);
        }

        trimTrailingItems();
        trimTrailingFluid();
    }

    private void loadEntries(CompoundTag tag, RegistryOps<Tag> ops) {
        ListTag items = tag.getList("ItemEntries", Tag.TAG_COMPOUND);
        for (int e = 0; e < items.size(); e++) {
            CompoundTag entry = items.getCompound(e);
            int i = entry.getInt("i");
            if (i < 0) continue;
            ItemStack ghost = ItemStack.OPTIONAL_CODEC.parse(ops, entry.getCompound("item")).result().orElse(ItemStack.EMPTY);
            if (ghost.isEmpty()) continue;
            ensureItemSize(i + 1);
            itemGhosts.set(i, ghost);
            itemAmounts.set(i, Math.max(0, entry.getInt("amt")));
            if (entry.contains("comps", Tag.TAG_LIST)) {
                ListTag compList = entry.getList("comps", Tag.TAG_STRING);
                Set<ResourceLocation> comps = new LinkedHashSet<>();
                for (int c = 0; c < compList.size(); c++) {
                    ResourceLocation id = ResourceLocation.tryParse(compList.getString(c));
                    if (id != null) comps.add(id);
                }
                itemComponents.set(i, comps);
            }
        }

        ListTag fluids = tag.getList("FluidEntries", Tag.TAG_COMPOUND);
        for (int e = 0; e < fluids.size(); e++) {
            CompoundTag entry = fluids.getCompound(e);
            int i = entry.getInt("i");
            if (i < 0) continue;
            FluidStack ghost = FluidStack.OPTIONAL_CODEC.parse(ops, entry.getCompound("fluid")).result().orElse(FluidStack.EMPTY);
            if (ghost.isEmpty()) continue;
            ensureFluidSize(i + 1);
            fluidGhosts.set(i, ghost);
            fluidAmounts.set(i, Math.max(0, entry.getInt("amt")));
        }
    }

    private void loadLegacy(CompoundTag tag, HolderLookup.Provider registries, RegistryOps<Tag> ops) {
        NonNullList<ItemStack> legacyItems = NonNullList.withSize(PAGE_SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, legacyItems, registries);
        int[] legacyItemAmounts = tag.getIntArray("ItemAmounts");
        for (int i = 0; i < legacyItems.size(); i++) {
            ItemStack ghost = legacyItems.get(i);
            if (ghost.isEmpty()) continue;
            ensureItemSize(i + 1);
            itemGhosts.set(i, ghost);
            itemAmounts.set(i, (i < legacyItemAmounts.length) ? Math.max(0, legacyItemAmounts[i]) : 0);
        }

        if (tag.contains("FluidGhosts", Tag.TAG_LIST)) {
            ListTag fluidList = tag.getList("FluidGhosts", Tag.TAG_COMPOUND);
            int[] legacyFluidAmounts = tag.getIntArray("FluidAmounts");
            for (int i = 0; i < fluidList.size(); i++) {
                FluidStack ghost = FluidStack.OPTIONAL_CODEC.parse(ops, fluidList.getCompound(i)).result().orElse(FluidStack.EMPTY);
                if (ghost.isEmpty()) continue;
                ensureFluidSize(i + 1);
                fluidGhosts.set(i, ghost);
                fluidAmounts.set(i, (i < legacyFluidAmounts.length) ? Math.max(0, legacyFluidAmounts[i]) : 0);
            }
        }
    }
}
