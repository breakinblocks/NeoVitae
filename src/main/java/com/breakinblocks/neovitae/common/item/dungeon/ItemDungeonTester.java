package com.breakinblocks.neovitae.common.item.dungeon;

import net.minecraft.core.BlockPos;
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

/**
 * Developer/debug item for testing dungeon generation.
 * Right-click to generate a test dungeon room at the player's position.
 */
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

            // Generate a test dungeon room 2 blocks in front of the player
            BlockPos targetPos = player.blockPosition().relative(player.getDirection(), 2);
            BlockPos[] result = dungeon.generateInitialRoom(initialType, serverLevel.random, serverLevel, targetPos);

            if (result != null && result.length > 0) {
                player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                        "Generated dungeon room at " + targetPos.toShortString()), false);
            } else {
                player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                        "Failed to generate dungeon room"), false);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
