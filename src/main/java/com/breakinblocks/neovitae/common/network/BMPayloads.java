package com.breakinblocks.neovitae.common.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.breakinblocks.neovitae.common.blockentity.routing.FilteredRoutingNodeTile;
import com.breakinblocks.neovitae.common.datacomponent.FilterInventory;
import com.breakinblocks.neovitae.common.item.ItemRitualDiviner;
import com.breakinblocks.neovitae.common.item.routing.ItemRouterFilter;
import com.breakinblocks.neovitae.common.menu.FilterMenu;
import com.breakinblocks.neovitae.common.menu.SigilHoldingMenu;
import com.breakinblocks.neovitae.will.WorldDemonWillHandler;

/**
 * Handles registration and processing of network payloads.
 */
public class BMPayloads {

    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");

        // Client -> Server
        registrar.playToServer(
                SigilHoldingSelectionPayload.TYPE,
                SigilHoldingSelectionPayload.STREAM_CODEC,
                BMPayloads::handleSigilHoldingSelection
        );

        registrar.playToServer(
                RoutingNodePayload.TYPE,
                RoutingNodePayload.STREAM_CODEC,
                BMPayloads::handleRoutingNode
        );

        registrar.playToServer(
                RitualDivinerCyclePayload.TYPE,
                RitualDivinerCyclePayload.STREAM_CODEC,
                BMPayloads::handleRitualDivinerCycle
        );

        registrar.playToServer(
                FilterGhostSlotPayload.TYPE,
                FilterGhostSlotPayload.STREAM_CODEC,
                BMPayloads::handleFilterGhostSlot
        );

        // Server -> Client
        registrar.playToClient(
                WillChunkSyncPayload.TYPE,
                WillChunkSyncPayload.STREAM_CODEC,
                BMPayloads::handleWillChunkSync
        );
    }

    private static void handleWillChunkSync(WillChunkSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            WorldDemonWillHandler.updateClientCache(
                    payload.chunkX(),
                    payload.chunkZ(),
                    payload.toWillChunk()
            );
        });
    }

    private static void handleRoutingNode(RoutingNodePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            // Security check: verify player is within interaction range of the block
            if (player.distanceToSqr(payload.pos().getX() + 0.5, payload.pos().getY() + 0.5, payload.pos().getZ() + 0.5) > 64.0) {
                return; // Player too far away (> 8 blocks)
            }
            BlockEntity be = player.level().getBlockEntity(payload.pos());
            if (be instanceof FilteredRoutingNodeTile tile) {
                // Verify player has the menu open for this tile
                if (player.containerMenu instanceof com.breakinblocks.neovitae.common.menu.RoutingNodeMenu menu && menu.tile == tile) {
                    switch (payload.action()) {
                        case RoutingNodePayload.ACTION_SELECT_SLOT -> tile.swapFilters(payload.value());
                        case RoutingNodePayload.ACTION_INCREMENT_PRIORITY -> tile.incrementCurrentPriorityToMaximum(10);
                        case RoutingNodePayload.ACTION_DECREMENT_PRIORITY -> tile.decrementCurrentPriority();
                        case RoutingNodePayload.ACTION_SWAP_PRIORITY -> tile.swapPriorityWith(payload.value());
                    }
                }
            }
        });
    }

    private static void handleSigilHoldingSelection(SigilHoldingSelectionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.containerMenu instanceof SigilHoldingMenu menu) {
                menu.setSelectedSlot(payload.selectedSlot());
            }
        });
    }

    private static void handleRitualDivinerCycle(RitualDivinerCyclePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            // Check both hands for ritual diviner
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack held = player.getItemInHand(hand);
                if (held.getItem() instanceof ItemRitualDiviner diviner) {
                    diviner.cycleRitual(held, player, payload.reverse());
                    return;
                }
            }
        });
    }

    private static void handleFilterGhostSlot(FilterGhostSlotPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player.containerMenu instanceof FilterMenu menu)) {
                return;
            }

            ItemStack filterStack = player.getMainHandItem();
            if (!(filterStack.getItem() instanceof ItemRouterFilter)) {
                return;
            }

            int slot = payload.ghostSlot();
            if (slot < 0 || slot >= ItemRouterFilter.INVENTORY_SIZE) {
                return;
            }

            // Update the ghost item in the filter inventory
            FilterInventory inv = ItemRouterFilter.getFilterInventory(filterStack);
            inv = inv.setItem(slot, payload.stack());
            ItemRouterFilter.setFilterInventory(filterStack, inv);

            // Also update the menu's inventory
            menu.filterInventory.setStackInSlot(slot, payload.stack());
        });
    }

    /**
     * Sends a payload to the server.
     */
    public static void sendToServer(Object payload) {
        PacketDistributor.sendToServer((net.minecraft.network.protocol.common.custom.CustomPacketPayload) payload);
    }

    /**
     * Sends a payload to a specific player.
     */
    public static void sendToPlayer(ServerPlayer player, Object payload) {
        PacketDistributor.sendToPlayer(player, (net.minecraft.network.protocol.common.custom.CustomPacketPayload) payload);
    }
}
