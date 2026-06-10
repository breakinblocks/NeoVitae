// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.routing;

import com.mojang.serialization.Codec;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Per-direction filter configuration stored on a routing node. Item and fluid
 * filters grow on demand: each holds as many ghost entries as the player has set,
 * rendered nine-wide across as many pages as are needed. Default state is disabled
 * with empty filters; a fresh side routes nothing. Item mode defaults to WHITELIST,
 * fluid mode to AUTO_MATCH (mirror the neighbor tank).
 */
public final class SideFilterConfig {
    public static final int PAGE_COLUMNS = 9;
    public static final int PAGE_ROWS = 3;
    public static final int PAGE_SIZE = PAGE_COLUMNS * PAGE_ROWS;

    private boolean enabled;
    private FilterMode itemMode;
    private final List<ItemStack> itemGhosts = new ArrayList<>();
    private final List<Integer> itemAmounts = new ArrayList<>();
    private final List<Set<Identifier>> itemComponents = new ArrayList<>();
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

    public Set<Identifier> getItemComponents(int slot) {
        return (slot >= 0 && slot < itemComponents.size()) ? itemComponents.get(slot) : Set.of();
    }

    public void setItemComponents(int slot, Set<Identifier> components) {
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
                if (i < itemComponents.size()) itemComponents.remove(i);
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

    public void save(ValueOutput out) {
        out.putBoolean("enabled", enabled);
        out.putString("itemMode", itemMode.name());
        out.putString("fluidMode", fluidMode.name());
        out.store("items", ItemStack.OPTIONAL_CODEC.listOf(), itemGhosts);
        out.store("itemAmounts", Codec.INT.listOf(), itemAmounts);
        out.store("itemComponents", Codec.STRING.listOf().listOf(), componentsToStrings());
        out.store("fluids", FluidStack.OPTIONAL_CODEC.listOf(), fluidGhosts);
        out.store("fluidAmounts", Codec.INT.listOf(), fluidAmounts);
    }

    public void load(ValueInput in) {
        enabled = in.getBooleanOr("enabled", false);
        in.getString("itemMode").ifPresent(s -> {
            try {
                itemMode = FilterMode.valueOf(s);
            } catch (IllegalArgumentException ignored) {
            }
        });
        if (itemMode == FilterMode.AUTO_MATCH) {
            itemMode = FilterMode.WHITELIST;
        }
        in.getString("fluidMode").ifPresent(s -> {
            try {
                fluidMode = FilterMode.valueOf(s);
            } catch (IllegalArgumentException ignored) {
            }
        });

        itemGhosts.clear();
        itemAmounts.clear();
        itemComponents.clear();
        fluidGhosts.clear();
        fluidAmounts.clear();

        if (in.read("items", ItemStack.OPTIONAL_CODEC.listOf()).map(list -> {
            itemGhosts.addAll(list);
            return true;
        }).orElse(false)) {
            in.read("itemAmounts", Codec.INT.listOf()).ifPresent(itemAmounts::addAll);
            in.read("itemComponents", Codec.STRING.listOf().listOf()).ifPresent(this::stringsToComponents);
            in.read("fluids", FluidStack.OPTIONAL_CODEC.listOf()).ifPresent(fluidGhosts::addAll);
            in.read("fluidAmounts", Codec.INT.listOf()).ifPresent(fluidAmounts::addAll);
        } else {
            NonNullList<ItemStack> legacy = NonNullList.withSize(PAGE_SIZE, ItemStack.EMPTY);
            in.child("itemGhosts").ifPresent(child -> ContainerHelper.loadAllItems(child, legacy));
            itemGhosts.addAll(legacy);
            in.read("fluidGhosts", FluidStack.OPTIONAL_CODEC.listOf()).ifPresent(fluidGhosts::addAll);
        }

        while (itemAmounts.size() < itemGhosts.size()) itemAmounts.add(0);
        while (itemComponents.size() < itemGhosts.size()) itemComponents.add(Set.of());
        while (fluidAmounts.size() < fluidGhosts.size()) fluidAmounts.add(0);
        trimTrailingItems();
        trimTrailingFluid();
    }

    private List<List<String>> componentsToStrings() {
        List<List<String>> result = new ArrayList<>();
        for (Set<Identifier> set : itemComponents) {
            List<String> ids = new ArrayList<>();
            for (Identifier id : set) ids.add(id.toString());
            result.add(ids);
        }
        return result;
    }

    private void stringsToComponents(List<List<String>> data) {
        itemComponents.clear();
        for (List<String> ids : data) {
            Set<Identifier> set = new LinkedHashSet<>();
            for (String s : ids) {
                Identifier id = Identifier.tryParse(s);
                if (id != null) set.add(id);
            }
            itemComponents.add(set);
        }
    }
}
