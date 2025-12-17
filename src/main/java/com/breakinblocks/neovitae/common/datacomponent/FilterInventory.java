package com.breakinblocks.neovitae.common.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Data component for storing filter inventory contents.
 * Stores up to 9 ghost item stacks for item routing filters.
 */
public record FilterInventory(List<ItemStack> items, List<Integer> tagIndices) {
    public static final int SIZE = 9;

    public static final Codec<FilterInventory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("items").forGetter(FilterInventory::items),
            Codec.INT.listOf().fieldOf("tag_indices").forGetter(FilterInventory::tagIndices)
    ).apply(instance, FilterInventory::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FilterInventory> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_LIST_STREAM_CODEC, FilterInventory::items,
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), FilterInventory::tagIndices,
            FilterInventory::new
    );

    public static FilterInventory empty() {
        List<ItemStack> items = new ArrayList<>(SIZE);
        List<Integer> tagIndices = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            items.add(ItemStack.EMPTY);
            tagIndices.add(0);
        }
        return new FilterInventory(items, tagIndices);
    }

    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= items.size()) {
            return ItemStack.EMPTY;
        }
        return items.get(slot);
    }

    public FilterInventory setItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SIZE) {
            return this;
        }
        List<ItemStack> newItems = new ArrayList<>(items);
        newItems.set(slot, stack);
        return new FilterInventory(newItems, tagIndices);
    }

    public int getTagIndex(int slot) {
        if (slot < 0 || slot >= tagIndices.size()) {
            return 0;
        }
        return tagIndices.get(slot);
    }

    public FilterInventory setTagIndex(int slot, int index) {
        if (slot < 0 || slot >= SIZE) {
            return this;
        }
        List<Integer> newIndices = new ArrayList<>(tagIndices);
        newIndices.set(slot, index);
        return new FilterInventory(items, newIndices);
    }

    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
