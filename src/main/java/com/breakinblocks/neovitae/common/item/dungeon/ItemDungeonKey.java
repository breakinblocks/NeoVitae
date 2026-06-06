// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item.dungeon;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * Dungeon Key item used to open sealed doors in procedural dungeons.
 * Each key type is associated with specific room pool identifiers.
 */
public class ItemDungeonKey extends Item {

    private final String[] resourceKeys;
    private final String keyType;

    public ItemDungeonKey(Item.Properties props, String keyType, String... resourceKeys) {
        super(props.stacksTo(16));
        this.keyType = keyType;
        this.resourceKeys = resourceKeys;
    }

    public Identifier getValidResourceLocation(List<Identifier> roomPools) {
        if (roomPools == null || roomPools.isEmpty()) {
            return null;
        }

        List<Identifier> matchingPools = roomPools.stream()
                .filter(pool -> Arrays.stream(resourceKeys)
                        .anyMatch(key -> getPoolName(pool).equals(key)))
                .collect(Collectors.toList());

        if (matchingPools.isEmpty()) {
            return null;
        }

        Collections.shuffle(matchingPools);
        return matchingPools.get(0);
    }

    public boolean canOpenDoor(List<Identifier> roomPools) {
        if (roomPools == null || roomPools.isEmpty()) {
            return false;
        }

        return roomPools.stream()
                .anyMatch(pool -> Arrays.stream(resourceKeys)
                        .anyMatch(key -> getPoolName(pool).equals(key)));
    }

    private static String getPoolName(Identifier pool) {
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
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.neovitae.dungeon_key.type", keyType)
                .withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.neovitae.dungeon_key.desc")
                .withStyle(ChatFormatting.DARK_GRAY));}
}
