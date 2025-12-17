package com.breakinblocks.neovitae.common.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Data component for storing nested filters in a composite filter.
 * Stores up to 4 filter item stacks.
 */
public record NestedFilterInventory(List<ItemStack> filters) {
    public static final int SIZE = 4;

    public static final Codec<NestedFilterInventory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("filters").forGetter(NestedFilterInventory::filters)
    ).apply(instance, NestedFilterInventory::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, NestedFilterInventory> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_LIST_STREAM_CODEC, NestedFilterInventory::filters,
            NestedFilterInventory::new
    );

    public static NestedFilterInventory empty() {
        List<ItemStack> filters = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            filters.add(ItemStack.EMPTY);
        }
        return new NestedFilterInventory(filters);
    }

    public ItemStack getFilter(int slot) {
        if (slot < 0 || slot >= filters.size()) {
            return ItemStack.EMPTY;
        }
        return filters.get(slot);
    }

    public NestedFilterInventory setFilter(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SIZE) {
            return this;
        }
        List<ItemStack> newFilters = new ArrayList<>(filters);
        newFilters.set(slot, stack);
        return new NestedFilterInventory(newFilters);
    }

    public List<ItemStack> getNonEmptyFilters() {
        List<ItemStack> nonEmpty = new ArrayList<>();
        for (ItemStack stack : filters) {
            if (!stack.isEmpty()) {
                nonEmpty.add(stack);
            }
        }
        return nonEmpty;
    }

    public int getFirstEmptySlot() {
        for (int i = 0; i < filters.size(); i++) {
            if (filters.get(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public boolean containsFilterType(ItemStack filterStack) {
        if (filterStack.isEmpty()) {
            return false;
        }
        for (ItemStack stack : filters) {
            if (!stack.isEmpty() && stack.getItem().equals(filterStack.getItem())) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        for (ItemStack stack : filters) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
