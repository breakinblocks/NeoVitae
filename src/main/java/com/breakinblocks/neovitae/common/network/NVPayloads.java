package com.breakinblocks.neovitae.common.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.breakinblocks.neovitae.common.blockentity.routing.FilteredRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;
import net.minecraft.network.chat.Component;
import com.breakinblocks.neovitae.common.item.ItemRitualDiviner;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.sigil.ItemSigilHolding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.menu.SigilHoldingMenu;
import com.breakinblocks.neovitae.util.helper.BloodLightHelper;
import com.breakinblocks.neovitae.will.WorldSpiritusHandler;

public class NVPayloads {

    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");

        registrar.playToServer(
                SigilHoldingSelectionPayload.TYPE,
                SigilHoldingSelectionPayload.STREAM_CODEC,
                NVPayloads::handleSigilHoldingSelection
        );

        registrar.playToServer(
                RoutingNodePayload.TYPE,
                RoutingNodePayload.STREAM_CODEC,
                NVPayloads::handleRoutingNode
        );

        registrar.playToServer(
                RoutingNodeSetGhostPayload.TYPE,
                RoutingNodeSetGhostPayload.STREAM_CODEC,
                NVPayloads::handleRoutingNodeSetGhost
        );

        registrar.playToServer(
                RoutingNodeSetFluidGhostPayload.TYPE,
                RoutingNodeSetFluidGhostPayload.STREAM_CODEC,
                NVPayloads::handleRoutingNodeSetFluidGhost
        );

        registrar.playToServer(
                MasterRoutingNodeEnergyRatePayload.TYPE,
                MasterRoutingNodeEnergyRatePayload.STREAM_CODEC,
                NVPayloads::handleMasterRoutingNodeEnergyRate
        );

        registrar.playToServer(
                RitualDivinerCyclePayload.TYPE,
                RitualDivinerCyclePayload.STREAM_CODEC,
                NVPayloads::handleRitualDivinerCycle
        );

        registrar.playToServer(
                SigilHoldingCyclePayload.TYPE,
                SigilHoldingCyclePayload.STREAM_CODEC,
                NVPayloads::handleSigilHoldingCycle
        );

        registrar.playToServer(
                BloodLightCyclePayload.TYPE,
                BloodLightCyclePayload.STREAM_CODEC,
                NVPayloads::handleBloodLightCycle
        );

        registrar.playToServer(
                LexCycleRadiusPayload.TYPE,
                LexCycleRadiusPayload.STREAM_CODEC,
                NVPayloads::handleLexCycleRadius
        );

        registrar.playToClient(
                SpiritusSyncPayload.TYPE,
                SpiritusSyncPayload.STREAM_CODEC,
                NVPayloads::handleSpiritusChunkSync
        );

        registrar.playToClient(
                SetClientVelocityPayload.TYPE,
                SetClientVelocityPayload.STREAM_CODEC,
                NVPayloads::handleSetClientVelocity
        );

        registrar.playToClient(
                StreamPayload.TYPE,
                StreamPayload.STREAM_CODEC,
                NVPayloads::handleStreamFX
        );
    }

    private static void handleSetClientVelocity(SetClientVelocityPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            context.player().setDeltaMovement(payload.motionX(), payload.motionY(), payload.motionZ());
        });
    }

    private static void handleStreamFX(StreamPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            com.breakinblocks.neovitae.client.render.stream.StreamManager.getInstance()
                    .addStream(payload.effect());
        });
    }

    private static void handleSpiritusChunkSync(SpiritusSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            com.breakinblocks.neovitae.client.ClientSpiritusCache.update(
                    payload.chunkX(),
                    payload.chunkZ(),
                    payload.toSpiritusChunk()
            );
        });
    }

    private static void handleRoutingNode(RoutingNodePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.distanceToSqr(payload.pos().getX() + 0.5, payload.pos().getY() + 0.5, payload.pos().getZ() + 0.5) > 64.0) {
                return;
            }
            BlockEntity be = player.level().getBlockEntity(payload.pos());
            if (be instanceof FilteredRoutingNodeBlockEntity tile) {
                if (player.containerMenu instanceof com.breakinblocks.neovitae.common.menu.RoutingNodeMenu menu && menu.tile == tile) {
                    int currentSide = tile.getCurrentActiveSlot();
                    switch (payload.action()) {
                        case RoutingNodePayload.ACTION_SELECT_SLOT -> tile.swapFilters(payload.value());
                        case RoutingNodePayload.ACTION_INCREMENT_PRIORITY -> tile.incrementCurrentPriorityToMaximum(10);
                        case RoutingNodePayload.ACTION_DECREMENT_PRIORITY -> tile.decrementCurrentPriority();
                        case RoutingNodePayload.ACTION_SWAP_PRIORITY -> tile.swapPriorityWith(payload.value());
                        case RoutingNodePayload.ACTION_TOGGLE_SIDE_ENABLED -> {
                            var cfg = tile.getSideFilter(currentSide);
                            tile.setSideEnabled(currentSide, !cfg.isEnabled());
                        }
                        case RoutingNodePayload.ACTION_TOGGLE_SIDE_ITEM_MODE -> tile.toggleSideItemMode(currentSide);
                        case RoutingNodePayload.ACTION_CLEAR_ITEM_GHOST -> tile.clearItemGhost(currentSide, payload.value());
                        case RoutingNodePayload.ACTION_TOGGLE_SIDE_FLUID_MODE -> tile.toggleSideFluidMode(currentSide);
                        case RoutingNodePayload.ACTION_CLEAR_FLUID_GHOST -> tile.clearFluidGhost(currentSide, payload.value());
                    }
                }
            }
        });
    }

    private static void handleRoutingNodeSetGhost(RoutingNodeSetGhostPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.distanceToSqr(payload.pos().getX() + 0.5, payload.pos().getY() + 0.5, payload.pos().getZ() + 0.5) > 64.0) {
                return;
            }
            BlockEntity be = player.level().getBlockEntity(payload.pos());
            if (be instanceof FilteredRoutingNodeBlockEntity tile) {
                if (player.containerMenu instanceof com.breakinblocks.neovitae.common.menu.RoutingNodeMenu menu && menu.tile == tile) {
                    int currentSide = tile.getCurrentActiveSlot();
                    tile.setItemGhost(currentSide, payload.ghostSlot(), payload.stack());
                }
            }
        });
    }

    private static void handleRoutingNodeSetFluidGhost(RoutingNodeSetFluidGhostPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.distanceToSqr(payload.pos().getX() + 0.5, payload.pos().getY() + 0.5, payload.pos().getZ() + 0.5) > 64.0) {
                return;
            }
            BlockEntity be = player.level().getBlockEntity(payload.pos());
            if (be instanceof FilteredRoutingNodeBlockEntity tile) {
                if (player.containerMenu instanceof com.breakinblocks.neovitae.common.menu.RoutingNodeMenu menu && menu.tile == tile) {
                    int currentSide = tile.getCurrentActiveSlot();
                    tile.setFluidGhost(currentSide, payload.ghostSlot(), payload.stack());
                }
            }
        });
    }

    private static void handleMasterRoutingNodeEnergyRate(MasterRoutingNodeEnergyRatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.distanceToSqr(payload.pos().getX() + 0.5, payload.pos().getY() + 0.5, payload.pos().getZ() + 0.5) > 64.0) {
                return;
            }
            BlockEntity be = player.level().getBlockEntity(payload.pos());
            if (be instanceof MasterRoutingNodeBlockEntity tile) {
                if (player.containerMenu instanceof com.breakinblocks.neovitae.common.menu.MasterRoutingNodeMenu menu && menu.tile == tile) {
                    tile.setConfiguredEnergyRate(payload.rate());
                }
            }
        });
    }

    private static void handleSigilHoldingCycle(SigilHoldingCyclePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            int slot = payload.slot();
            if (slot < 0 || slot > 8) return;

            ItemStack itemStack = player.getInventory().getItem(slot);
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemSigilHolding) {
                ItemSigilHolding.cycleToNextSigil(itemStack, payload.direction());
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

    private static void handleBloodLightCycle(BloodLightCyclePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack held = player.getItemInHand(hand);
                if (held.is(NVItems.SIGIL_BLOOD_LIGHT.get())) {
                    int current = held.getOrDefault(NVDataComponents.BLOOD_LIGHT_BRIGHTNESS.get(), 15);
                    int newBrightness = BloodLightHelper.cycleBrightness(current, payload.reverse());
                    held.set(NVDataComponents.BLOOD_LIGHT_BRIGHTNESS.get(), newBrightness);
                    player.displayClientMessage(Component.translatable("message.neovitae.sigil.blood_light.brightness", newBrightness), true);
                    return;
                }
            }
        });
    }

    private static void handleLexCycleRadius(LexCycleRadiusPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack held = player.getItemInHand(hand);
                if (held.getItem() instanceof com.breakinblocks.neovitae.common.item.soul.LexVitaeItem) {
                    int current = held.getOrDefault(NVDataComponents.LEX_RADIUS.get(), 0);
                    int next = ((current + Integer.signum(payload.direction())) % 3 + 3) % 3;
                    held.set(NVDataComponents.LEX_RADIUS.get(), next);
                    int side = next == 0 ? 1 : (next == 1 ? 3 : 5);
                    player.displayClientMessage(Component.translatable("message.neovitae.lex_vitae.radius", side, side), true);
                    return;
                }
            }
        });
    }

    private static void handleRitualDivinerCycle(RitualDivinerCyclePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack held = player.getItemInHand(hand);
                if (held.getItem() instanceof ItemRitualDiviner diviner) {
                    diviner.cycleRitual(held, player, payload.reverse());
                    return;
                }
            }
        });
    }

    public static void sendToServer(Object payload) {
        PacketDistributor.sendToServer((net.minecraft.network.protocol.common.custom.CustomPacketPayload) payload);
    }

    public static void sendToPlayer(ServerPlayer player, Object payload) {
        PacketDistributor.sendToPlayer(player, (net.minecraft.network.protocol.common.custom.CustomPacketPayload) payload);
    }

    /**
     * Send a payload to all players within a radius of a block position.
     */
    public static void sendToNearby(net.minecraft.server.level.ServerLevel level, net.minecraft.core.BlockPos pos,
                                    double radius, Object payload) {
        net.minecraft.network.protocol.common.custom.CustomPacketPayload p =
                (net.minecraft.network.protocol.common.custom.CustomPacketPayload) payload;
        double radiusSq = radius * radius;
        double cx = pos.getX() + 0.5, cy = pos.getY() + 0.5, cz = pos.getZ() + 0.5;
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(cx, cy, cz) <= radiusSq) {
                PacketDistributor.sendToPlayer(player, p);
            }
        }
    }
}
