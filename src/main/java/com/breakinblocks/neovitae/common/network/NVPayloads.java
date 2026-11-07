package com.breakinblocks.neovitae.common.network;

import net.minecraft.resources.ResourceKey;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.breakinblocks.neovitae.client.ClientAnimaCache;
import com.breakinblocks.neovitae.client.ClientSpiritusCache;
import com.breakinblocks.neovitae.client.ClipboardClientHelper;
import com.breakinblocks.neovitae.ClientConfig;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.render.stream.StreamManager;
import com.breakinblocks.neovitae.common.blockentity.routing.FilteredRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.OutputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.SanguineCrafterBlockEntity;
import net.minecraft.network.chat.Component;
import com.breakinblocks.neovitae.common.item.ExperienceTomeItem;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.TrainerItem;
import com.breakinblocks.neovitae.common.item.sigil.ItemSigilHolding;
import com.breakinblocks.neovitae.common.item.soul.LexVitaeItem;
import com.breakinblocks.neovitae.common.item.BloodOrbItem;
import com.breakinblocks.neovitae.common.entity.BloodShieldEntity;
import com.breakinblocks.neovitae.api.NeoVitaeAPI;
import com.breakinblocks.neovitae.api.routing.ISpiritusExportNode;
import com.breakinblocks.neovitae.api.soul.IAnima;
import com.breakinblocks.neovitae.compat.curios.CuriosCompat;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonAlternatorBlockEntity;
import com.breakinblocks.neovitae.common.menu.AbstractBlockEntityMenu;
import com.breakinblocks.neovitae.common.menu.AlternatorMenu;
import com.breakinblocks.neovitae.common.menu.MasterRoutingNodeMenu;
import com.breakinblocks.neovitae.common.menu.RoutingNodeMenu;
import com.breakinblocks.neovitae.common.menu.SanguineCrafterMenu;
import com.breakinblocks.neovitae.common.menu.ExperienceTomeMenu;
import com.breakinblocks.neovitae.common.menu.SigilHoldingMenu;
import com.breakinblocks.neovitae.common.menu.TrainerMenu;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.common.sideconfig.OutputFaceConfigurable;
import com.breakinblocks.neovitae.common.sideconfig.SideConfigurable;
import com.breakinblocks.neovitae.common.sideconfig.SlotSideConfig;
import net.minecraft.core.Direction;
import com.breakinblocks.neovitae.util.helper.BloodLightHelper;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

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
                AnimaSyncPayload.TYPE,
                AnimaSyncPayload.STREAM_CODEC,
                NVPayloads::handleAnimaSync
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
                SiphonStreamPayload.TYPE,
                SiphonStreamPayload.STREAM_CODEC,
                NVPayloads::handleSiphonStreamFX
        );

        registrar.playToClient(
                RitualCodePayload.TYPE,
                RitualCodePayload.STREAM_CODEC,
                NVPayloads::handleRitualCode
        );

        registrar.playToServer(
                SetTrainingCapPayload.TYPE,
                SetTrainingCapPayload.STREAM_CODEC,
                NVPayloads::handleSetTrainingCap
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
                SetOutputFacePayload.TYPE,
                SetOutputFacePayload.STREAM_CODEC,
                NVPayloads::handleSetOutputFace
        );

        registrar.playToServer(
                PasteSideConfigPayload.TYPE,
                PasteSideConfigPayload.STREAM_CODEC,
                NVPayloads::handlePasteSideConfig
        );

        registrar.playToServer(
                SanguineCrafterTemplatePayload.TYPE,
                SanguineCrafterTemplatePayload.STREAM_CODEC,
                NVPayloads::handleSanguineCrafterTemplate
        );

        registrar.playToServer(
                SanguineCrafterSettingPayload.TYPE,
                SanguineCrafterSettingPayload.STREAM_CODEC,
                NVPayloads::handleSanguineCrafterSetting
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

        registrar.playToServer(
                AlternatorConfigPayload.TYPE,
                AlternatorConfigPayload.STREAM_CODEC,
                NVPayloads::handleAlternatorConfig
        );
    }

    private static void handleAlternatorConfig(AlternatorConfigPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.distanceToSqr(payload.pos().getX() + 0.5, payload.pos().getY() + 0.5, payload.pos().getZ() + 0.5) > 64.0) {
                return;
            }
            BlockEntity be = player.level().getBlockEntity(payload.pos());
            if (be instanceof DungeonAlternatorBlockEntity tile
                    && player.containerMenu instanceof AlternatorMenu menu && menu.tile == tile) {
                tile.setDelay(payload.delay());
                tile.setStopOnRedstone(payload.stopOnRedstone());
            }
        });
    }

    private static void handleLexModeCycle(LexModeCyclePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() == null) return;
            ItemStack held = context.player().getMainHandItem();
            if (held.getItem() instanceof LexVitaeItem && LexVitaeItem.isActive(held)) {
                if (context.player().isShiftKeyDown() && LexVitaeItem.canToggleSilkTouch(held)) {
                    LexVitaeItem.toggleSilkTouch(held);
                    context.player().sendOverlayMessage(Component.translatable("message.neovitae.lex_vitae.harvest",
                            Component.translatable(LexVitaeItem.getHarvestTranslationKey(held))));
                    return;
                }
                LexVitaeItem.cycleMode(held);
                context.player().sendOverlayMessage(Component.translatable("message.neovitae.lex_vitae.mode",
                        Component.translatable(LexVitaeItem.getModeTranslationKey(held))));
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
                player.sendOverlayMessage(Component.translatable("message.neovitae.sanguine_ward.not_enough_ev",
                        BloodOrbItem.getShieldMinEV()).withStyle(ChatFormatting.RED));
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

    @Nullable
    private static SanguineCrafterBlockEntity openCrafter(Player player, BlockPos pos) {
        if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64.0) return null;
        if (!(player.containerMenu instanceof SanguineCrafterMenu menu)) return null;
        SanguineCrafterBlockEntity crafter = menu.getTile();
        return crafter != null && crafter.getBlockPos().equals(pos) ? crafter : null;
    }

    private static void handleSanguineCrafterTemplate(SanguineCrafterTemplatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            SanguineCrafterBlockEntity crafter = openCrafter(context.player(), payload.pos());
            if (crafter == null) return;
            crafter.setTemplate(SanguineCrafterBlockEntity.Mode.byIndex(payload.mode()), payload.stacks());
        });
    }

    private static void handleSanguineCrafterSetting(SanguineCrafterSettingPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            SanguineCrafterBlockEntity crafter = openCrafter(context.player(), payload.pos());
            if (crafter == null) return;
            switch (payload.setting()) {
                case SanguineCrafterSettingPayload.MODE -> crafter.setMode(SanguineCrafterBlockEntity.Mode.byIndex(payload.value()));
                case SanguineCrafterSettingPayload.REDSTONE -> crafter.setRedstoneMode(SanguineCrafterBlockEntity.RedstoneMode.byIndex(payload.value()));
                case SanguineCrafterSettingPayload.LIMIT -> crafter.setOutputLimit(payload.value());
                default -> { }
            }
        });
    }

    private static void handleSetOutputFace(SetOutputFacePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.distanceToSqr(payload.pos().getX() + 0.5, payload.pos().getY() + 0.5, payload.pos().getZ() + 0.5) > 64.0) {
                return;
            }
            if (!(player.containerMenu instanceof AbstractBlockEntityMenu<?> menu)) return;
            BlockEntity be = menu.getTile();
            if (be == null || !be.getBlockPos().equals(payload.pos())) return;
            if (!(be instanceof OutputFaceConfigurable configurable)) return;
            if (payload.face() >= Direction.values().length) return;
            configurable.setOutputFace(payload.face() < 0 ? null : Direction.from3DDataValue(payload.face()));
            be.setChanged();
        });
    }

    private static void handlePasteSideConfig(PasteSideConfigPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.distanceToSqr(payload.pos().getX() + 0.5, payload.pos().getY() + 0.5, payload.pos().getZ() + 0.5) > 64.0) {
                return;
            }
            if (!(player.containerMenu instanceof AbstractBlockEntityMenu<?> menu)) return;
            BlockEntity be = menu.getTile();
            if (be == null || !be.getBlockPos().equals(payload.pos())) return;
            if (!(be instanceof SideConfigurable configurable)) return;
            SlotSideConfig config = configurable.getSideConfig();
            Direction[] directions = Direction.values();
            if (payload.mask().length != config.slotCount() * directions.length) return;
            if (payload.face() >= directions.length) return;
            for (int slot = 0; slot < config.slotCount(); slot++) {
                for (Direction dir : directions) {
                    config.setAllowed(slot, dir, payload.mask()[slot * directions.length + dir.get3DDataValue()] != 0);
                }
            }
            if (be instanceof OutputFaceConfigurable outputConfigurable) {
                outputConfigurable.setOutputFace(payload.face() < 0 ? null : Direction.from3DDataValue(payload.face()));
            }
            be.setChanged();
        });
    }

    private static void handleSetTrainingCap(SetTrainingCapPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player.containerMenu instanceof TrainerMenu)) return;
            player.level().registryAccess().lookupOrThrow(NVRegistries.Keys.SENTIENT_UPGRADES)
                    .get(ResourceKey.create(NVRegistries.Keys.SENTIENT_UPGRADES, payload.upgrade()))
                    .filter(holder -> holder.is(NVTags.Sentient.TRAINABLE))
                    .ifPresent(holder -> TrainerItem.setCap(player, holder, payload.level()));
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

    private static void handleSiphonStreamFX(SiphonStreamPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientConfig.SiphonStreams mode = NeoVitae.CLIENT_CONFIG.BLOOD_SIPHON_STREAMS.get();
            if (mode == ClientConfig.SiphonStreams.NEVER) return;
            if (mode == ClientConfig.SiphonStreams.ORB_ONLY && !payload.fromOrb()) return;
            StreamManager.getInstance().addStream(payload.effect());
        });
    }

    private static void handleAnimaSync(AnimaSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientAnimaCache.update(payload.owner(), payload.currentEV()));
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
                        case RoutingNodePayload.ACTION_SET_SIDE_ENERGY_RATE ->
                                tile.setSideEnergyRate(currentSide, payload.value());
                        case RoutingNodePayload.ACTION_CYCLE_SIDE_DIRECTION ->
                                tile.cycleSideDirection(currentSide, payload.value() < 0);
                        case RoutingNodePayload.ACTION_TOGGLE_SIDE_ENERGY -> {
                            var cfg = tile.getSideFilter(currentSide);
                            tile.setSideEnergyEnabled(currentSide, !cfg.isEnergyEnabled());
                        }
                        case RoutingNodePayload.ACTION_TOGGLE_SIDE_ITEM_MODE -> tile.toggleSideItemMode(currentSide);
                        case RoutingNodePayload.ACTION_CLEAR_ITEM_GHOST -> tile.clearItemGhost(currentSide, payload.value());
                        case RoutingNodePayload.ACTION_TOGGLE_SIDE_FLUID_MODE -> tile.toggleSideFluidMode(currentSide);
                        case RoutingNodePayload.ACTION_CLEAR_FLUID_GHOST -> tile.clearFluidGhost(currentSide, payload.value());
                        case RoutingNodePayload.ACTION_CYCLE_SPIRITUS_TYPE -> {
                            if (tile instanceof ISpiritusExportNode output) output.cycleSpiritusType(payload.value());
                        }
                        case RoutingNodePayload.ACTION_ADJUST_SPIRITUS_STOCK -> {
                            if (tile instanceof ISpiritusExportNode output) output.adjustSpiritusStock(payload.value());
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
