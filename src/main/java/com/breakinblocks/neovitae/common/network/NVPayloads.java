package com.breakinblocks.neovitae.common.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.breakinblocks.neovitae.client.ClientSpiritusCache;
import com.breakinblocks.neovitae.client.ClipboardClientHelper;
import com.breakinblocks.neovitae.client.render.stream.StreamManager;
import com.breakinblocks.neovitae.common.blockentity.routing.FilteredRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.OutputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;
import net.minecraft.network.chat.Component;
import com.breakinblocks.neovitae.common.item.ExperienceTomeItem;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.TrainerItem;
import com.breakinblocks.neovitae.common.item.sigil.ItemSigilHolding;
import com.breakinblocks.neovitae.common.item.soul.LexVitaeItem;
import com.breakinblocks.neovitae.common.item.BloodOrbItem;
import com.breakinblocks.neovitae.common.entity.BloodShieldEntity;
import com.breakinblocks.neovitae.api.NeoVitaeAPI;
import com.breakinblocks.neovitae.api.soul.IAnima;
import com.breakinblocks.neovitae.compat.curios.CuriosCompat;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.menu.AbstractBlockEntityMenu;
import com.breakinblocks.neovitae.common.menu.MasterRoutingNodeMenu;
import com.breakinblocks.neovitae.common.menu.RoutingNodeMenu;
import com.breakinblocks.neovitae.common.menu.ExperienceTomeMenu;
import com.breakinblocks.neovitae.common.menu.SigilHoldingMenu;
import com.breakinblocks.neovitae.common.sideconfig.SideConfigurable;
import net.minecraft.core.Direction;
import com.breakinblocks.neovitae.util.helper.BloodLightHelper;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;

import java.util.LinkedHashSet;

public class NVPayloads {

    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");

        registrar.playToServer(
                SigilHoldingSelectionPayload.TYPE,
                SigilHoldingSelectionPayload.STREAM_CODEC,
                NVPayloads::handleSigilHoldingSelection
        );


        registrar.playToServer(

                ExperienceTomeTransferPayload.TYPE,

                ExperienceTomeTransferPayload.STREAM_CODEC,

                NVPayloads::handleExperienceTomeTransfer

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
                RoutingNodeSetAmountPayload.TYPE,
                RoutingNodeSetAmountPayload.STREAM_CODEC,
                NVPayloads::handleRoutingNodeSetAmount
        );

        registrar.playToServer(
                RoutingNodeSetComponentsPayload.TYPE,
                RoutingNodeSetComponentsPayload.STREAM_CODEC,
                NVPayloads::handleRoutingNodeSetComponents
        );

        registrar.playToServer(
                MasterRoutingNodeEnergyRatePayload.TYPE,
                MasterRoutingNodeEnergyRatePayload.STREAM_CODEC,
                NVPayloads::handleMasterRoutingNodeEnergyRate
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

        registrar.playToClient(
                RitualCodePayload.TYPE,
                RitualCodePayload.STREAM_CODEC,
                NVPayloads::handleRitualCode
        );

        registrar.playToServer(
                OpenTrainerFromCurioPayload.TYPE,
                OpenTrainerFromCurioPayload.STREAM_CODEC,
                NVPayloads::handleOpenTrainerFromCurio
        );

        registrar.playToServer(
                SetSideConfigPayload.TYPE,
                SetSideConfigPayload.STREAM_CODEC,
                NVPayloads::handleSetSideConfig
        );

        registrar.playToServer(
                LexBeamPayload.TYPE,
                LexBeamPayload.STREAM_CODEC,
                NVPayloads::handleLexBeam
        );

        registrar.playToServer(
                ShieldKeyPayload.TYPE,
                ShieldKeyPayload.STREAM_CODEC,
                NVPayloads::handleShieldKey
        );

        registrar.playToServer(
                LexModeCyclePayload.TYPE,
                LexModeCyclePayload.STREAM_CODEC,
                NVPayloads::handleLexModeCycle
        );
    }

    private static void handleLexModeCycle(LexModeCyclePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() == null) return;
            ItemStack held = context.player().getMainHandItem();
            if (held.getItem() instanceof LexVitaeItem && LexVitaeItem.isActive(held)) {
                LexVitaeItem.cycleMode(held);
            }
        });
    }

    private static void handleLexBeam(LexBeamPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() != null) {
                LexVitaeItem.setBeaming(context.player(), payload.firing());
            }
        });
    }

    private static void handleShieldKey(ShieldKeyPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            if (payload.active() && player.getOffhandItem().getItem() instanceof BloodOrbItem) {
                IAnima network = NeoVitaeAPI.getInstance().getAnima(player.getUUID());
                if (network != null && network.getCurrentEV() >= BloodOrbItem.getShieldMinEV()) {
                    BloodOrbItem.setShieldActive(player, true);
                    BloodShieldEntity.raise(player);
                    return;
                }
            }
            BloodOrbItem.setShieldActive(player, false);
        });
    }

    private static void handleSetSideConfig(SetSideConfigPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.distanceToSqr(payload.pos().getX() + 0.5, payload.pos().getY() + 0.5, payload.pos().getZ() + 0.5) > 64.0) {
                return;
            }
            if (!(player.containerMenu instanceof AbstractBlockEntityMenu<?> menu)) return;
            BlockEntity be = menu.getTile();
            if (be == null || !be.getBlockPos().equals(payload.pos())) return;
            if (!(be instanceof SideConfigurable configurable)) return;
            if (payload.direction() < 0 || payload.direction() >= Direction.values().length) return;
            configurable.getSideConfig().setAllowed(payload.slot(), Direction.from3DDataValue(payload.direction()), payload.enabled());
            be.setChanged();
        });
    }

    private static void handleOpenTrainerFromCurio(OpenTrainerFromCurioPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) return;
            if (!CuriosCompat.isCuriosLoaded()) return;
            ItemStack bracelet = new ItemStack(NVItems.TRAINING_BRACELET.get());
            if (!CuriosCompat.isEquippedCurio(serverPlayer, bracelet)) return;
            TrainerItem.openTrainer(serverPlayer, serverPlayer.getInventory().getSelectedSlot());
        });
    }

    private static void handleRitualCode(RitualCodePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClipboardClientHelper.setClipboard(payload.code()));
    }

    private static void handleSetClientVelocity(SetClientVelocityPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            context.player().setDeltaMovement(payload.motionX(), payload.motionY(), payload.motionZ());
        });
    }

    private static void handleStreamFX(StreamPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            StreamManager.getInstance()
                    .addStream(payload.effect());
        });
    }

    private static void handleSpiritusChunkSync(SpiritusSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientSpiritusCache.update(
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
                if (player.containerMenu instanceof RoutingNodeMenu menu && menu.tile == tile) {
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
                        case RoutingNodePayload.ACTION_CYCLE_SPIRITUS_TYPE -> {
                            if (tile instanceof OutputRoutingNodeBlockEntity output) output.cycleSpiritusType(payload.value());
                        }
                        case RoutingNodePayload.ACTION_ADJUST_SPIRITUS_STOCK -> {
                            if (tile instanceof OutputRoutingNodeBlockEntity output) output.adjustSpiritusStock(payload.value());
                        }
                    }
                }
            }
        });
    }

    private static void handleRoutingNodeSetComponents(RoutingNodeSetComponentsPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.distanceToSqr(payload.pos().getX() + 0.5, payload.pos().getY() + 0.5, payload.pos().getZ() + 0.5) > 64.0) {
                return;
            }
            BlockEntity be = player.level().getBlockEntity(payload.pos());
            if (be instanceof FilteredRoutingNodeBlockEntity tile) {
                if (player.containerMenu instanceof RoutingNodeMenu menu && menu.tile == tile) {
                    int currentSide = tile.getCurrentActiveSlot();
                    tile.setItemComponents(currentSide, payload.ghostSlot(), new LinkedHashSet<>(payload.components()));
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
                if (player.containerMenu instanceof RoutingNodeMenu menu && menu.tile == tile) {
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
                if (player.containerMenu instanceof RoutingNodeMenu menu && menu.tile == tile) {
                    int currentSide = tile.getCurrentActiveSlot();
                    tile.setFluidGhost(currentSide, payload.ghostSlot(), payload.stack());
                }
            }
        });
    }

    private static void handleRoutingNodeSetAmount(RoutingNodeSetAmountPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.distanceToSqr(payload.pos().getX() + 0.5, payload.pos().getY() + 0.5, payload.pos().getZ() + 0.5) > 64.0) {
                return;
            }
            BlockEntity be = player.level().getBlockEntity(payload.pos());
            if (be instanceof FilteredRoutingNodeBlockEntity tile) {
                if (player.containerMenu instanceof RoutingNodeMenu menu && menu.tile == tile) {
                    int currentSide = tile.getCurrentActiveSlot();
                    if (payload.fluid()) {
                        tile.setFluidAmount(currentSide, payload.ghostSlot(), payload.amount());
                    } else {
                        tile.setItemAmount(currentSide, payload.ghostSlot(), payload.amount());
                    }
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
                if (player.containerMenu instanceof MasterRoutingNodeMenu menu && menu.tile == tile) {
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

    private static void handleExperienceTomeTransfer(ExperienceTomeTransferPayload payload, IPayloadContext context) {

        context.enqueueWork(() -> {

            Player player = context.player();

            if (!(player.containerMenu instanceof ExperienceTomeMenu menu)) {

                return;

            }

            ItemStack tome = menu.getTome(player);

            if (!(tome.getItem() instanceof ExperienceTomeItem)) {

                return;

            }

            if (payload.deposit()) {

                ExperienceTomeItem.depositLevels(player, tome, payload.levels());

            } else {

                ExperienceTomeItem.withdrawLevels(player, tome, payload.levels());

            }

            if (player instanceof ServerPlayer serverPlayer) {

                serverPlayer.inventoryMenu.broadcastChanges();

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
                    player.sendOverlayMessage(Component.translatable("message.neovitae.sigil.blood_light.brightness", newBrightness));
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
                if (held.getItem() instanceof LexVitaeItem) {
                    int current = held.getOrDefault(NVDataComponents.LEX_RADIUS.get(), 0);
                    int next = ((current + Integer.signum(payload.direction())) % 3 + 3) % 3;
                    held.set(NVDataComponents.LEX_RADIUS.get(), next);
                    int side = next == 0 ? 1 : (next == 1 ? 3 : 5);
                    player.sendOverlayMessage(Component.translatable("message.neovitae.lex_vitae.radius", side, side));
                    return;
                }
            }
        });
    }

    public static void sendToPlayer(ServerPlayer player, Object payload) {
        PacketDistributor.sendToPlayer(player, (CustomPacketPayload) payload);
    }

    /**
     * Send a payload to all players within a radius of a block position.
     */
    public static void sendToNearby(ServerLevel level, BlockPos pos,
                                    double radius, Object payload) {
        CustomPacketPayload p =
                (CustomPacketPayload) payload;
        double radiusSq = radius * radius;
        double cx = pos.getX() + 0.5, cy = pos.getY() + 0.5, cz = pos.getZ() + 0.5;
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(cx, cy, cz) <= radiusSq) {
                PacketDistributor.sendToPlayer(player, p);
            }
        }
    }
}
