// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.structures.DungeonSynthesizer;

public class ItemDungeonTester extends Item {
    public ItemDungeonTester() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            DungeonSynthesizer dungeon = new DungeonSynthesizer();
            ResourceLocation initialType = NeoVitae.rl("room_pools/entrances/mini_dungeon_entrances");

            BlockPos targetPos = player.blockPosition().relative(player.getDirection(), 2);
            BlockPos[] result = dungeon.generateInitialRoom(initialType, serverLevel.random, serverLevel, targetPos);

            if (result != null && result.length > 0) {
                player.displayClientMessage(Component.literal(
                        "Generated dungeon room at " + targetPos.toShortString()), false);
            } else {
                player.displayClientMessage(Component.literal(
                        "Failed to generate dungeon room"), false);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
