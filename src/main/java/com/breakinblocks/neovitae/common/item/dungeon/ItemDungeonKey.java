package com.breakinblocks.neovitae.common.item.dungeon;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dungeon Key item used to open sealed doors in procedural dungeons.
 * Each key type is associated with specific room pool identifiers.
 */
public class ItemDungeonKey extends Item {

    private final String[] resourceKeys;
    private final String keyType;

    /**
     * Creates a new dungeon key item.
     *
     * @param keyType      Display name type for tooltip
     * @param resourceKeys Room pool identifiers this key can open
     */
    public ItemDungeonKey(String keyType, String... resourceKeys) {
        super(new Properties().stacksTo(16));
        this.keyType = keyType;
        this.resourceKeys = resourceKeys;
    }

    /**
     * Gets a valid resource location from the given list that matches this key.
     * Randomly selects from matching entries.
     * Uses streams for modern Java patterns.
     *
     * @param roomPools List of potential room pool locations
     * @return A matching ResourceLocation, or null if none match
     */
    public ResourceLocation getValidResourceLocation(List<ResourceLocation> roomPools) {
        if (roomPools == null || roomPools.isEmpty()) {
            return null;
        }

        List<ResourceLocation> matchingPools = roomPools.stream()
                .filter(pool -> Arrays.stream(resourceKeys)
                        .anyMatch(key -> pool.toString().contains(key)))
                .collect(Collectors.toList());

        if (matchingPools.isEmpty()) {
            return null;
        }

        Collections.shuffle(matchingPools);
        return matchingPools.get(0);
    }

    /**
     * Checks if this key can open a door with the given room pools.
     * Uses streams for modern Java patterns.
     *
     * @param roomPools List of potential room pool locations
     * @return true if this key matches any of the room pools
     */
    public boolean canOpenDoor(List<ResourceLocation> roomPools) {
        if (roomPools == null || roomPools.isEmpty()) {
            return false;
        }

        return roomPools.stream()
                .anyMatch(pool -> Arrays.stream(resourceKeys)
                        .anyMatch(key -> pool.toString().contains(key)));
    }

    /**
     * Gets the resource key patterns this key matches.
     */
    public String[] getResourceKeys() {
        return resourceKeys.clone(); // Return defensive copy
    }

    /**
     * Gets the key type for display purposes.
     */
    public String getKeyType() {
        return keyType;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae.dungeon_key.type", keyType)
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.neovitae.dungeon_key.desc")
                .withStyle(ChatFormatting.DARK_GRAY));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
