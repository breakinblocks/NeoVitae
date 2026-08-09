// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

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

    public ItemDungeonKey(String keyType, String... resourceKeys) {
        super(new Properties().stacksTo(16));
        this.keyType = keyType;
        this.resourceKeys = resourceKeys;
    }

    public List<ResourceLocation> getValidResourceLocations(List<ResourceLocation> roomPools) {
        if (roomPools == null || roomPools.isEmpty()) {
            return List.of();
        }

        List<ResourceLocation> matchingPools = roomPools.stream()
                .filter(pool -> Arrays.stream(resourceKeys)
                        .anyMatch(key -> getPoolName(pool).equals(key)))
                .collect(Collectors.toList());

        Collections.shuffle(matchingPools);
        return matchingPools;
    }

    public boolean canOpenDoor(List<ResourceLocation> roomPools) {
        if (roomPools == null || roomPools.isEmpty()) {
            return false;
        }

        return roomPools.stream()
                .anyMatch(pool -> Arrays.stream(resourceKeys)
                        .anyMatch(key -> getPoolName(pool).equals(key)));
    }

    private static String getPoolName(ResourceLocation pool) {
        String path = pool.getPath();
        int lastSlash = path.lastIndexOf('/');
        return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
    }

    public String[] getResourceKeys() {
        return resourceKeys.clone();
    }

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
