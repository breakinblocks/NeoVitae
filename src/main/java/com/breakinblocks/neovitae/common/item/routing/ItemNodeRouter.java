// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item.routing;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.api.routing.*;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonAlternatorBlockEntity;
import com.breakinblocks.neovitae.common.routing.RoutingLinkHelper;
import com.breakinblocks.neovitae.common.world.AlternatorLinks;
import com.breakinblocks.neovitae.util.Constants;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * Item for connecting routing nodes together.
 */
public class ItemNodeRouter extends Item {

    private static final String NBT_ALTERNATOR_MODE = "alternatorLink";
    private static final String NBT_ALTERNATOR_DIM = "alternatorDim";

    public ItemNodeRouter(Item.Properties props) {
        super(props.stacksTo(1));
    }
    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        BlockPos coords = getBlockPos(stack);
        if (coords != null && !coords.equals(BlockPos.ZERO)) {
            if (isAlternatorMode(stack)) {
                tooltip.accept(Component.translatable("tooltip.neovitae.noderouter.alternator",
                        coords.getX(), coords.getY(), coords.getZ()));
            } else {
                tooltip.accept(Component.translatable("tooltip.neovitae.noderouter.coords",
                        coords.getX(), coords.getY(), coords.getZ()));
            }
        }
        tooltip.accept(Component.translatable("tooltip.neovitae.noderouter.unlink").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.neovitae.noderouter.clear").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.neovitae.noderouter.alternator.hint").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide() || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        BlockPos stored = getBlockPos(stack);
        if (stored == null || stored.equals(BlockPos.ZERO)) {
            player.sendOverlayMessage(Component.translatable("chat.neovitae.routing.noStored"));
            return InteractionResult.SUCCESS;
        }

        setBlockPos(stack, BlockPos.ZERO);
        player.sendOverlayMessage(Component.translatable("chat.neovitae.routing.remove"));
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        if (level.isClientSide()) {
            return player.isShiftKeyDown() ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }

        BlockEntity tileHit = level.getBlockEntity(pos);

        if (player.isShiftKeyDown() && level instanceof ServerLevel serverLevel) {
            if (tileHit instanceof DungeonAlternatorBlockEntity) {
                setAlternatorSource(stack, serverLevel, pos);
                player.sendOverlayMessage(Component.translatable("chat.neovitae.alternator.selected"));
                return InteractionResult.SUCCESS;
            }
            if (!(tileHit instanceof IRoutingNode)) {
                InteractionResult alternatorResult = handleAlternatorTarget(serverLevel, player, stack, pos);
                if (alternatorResult != null) {
                    return alternatorResult;
                }
            }
        }

        if (player.isShiftKeyDown() && tileHit instanceof IRoutingNode selected && pos.equals(getBlockPos(stack))) {
            return unlinkNode(level, player, stack, selected, pos);
        }

        if (!(tileHit instanceof IRoutingNode node)) {
            if (isAlternatorMode(stack)) {
                return InteractionResult.PASS;
            }
            BlockPos containedPos = getBlockPos(stack);
            if (containedPos != null && !containedPos.equals(BlockPos.ZERO)) {
                setBlockPos(stack, BlockPos.ZERO);
                player.sendOverlayMessage(Component.translatable("chat.neovitae.routing.remove"));
                return InteractionResult.FAIL;
            }
            return InteractionResult.FAIL;
        }

        BlockPos containedPos = getBlockPos(stack);
        if (containedPos == null || containedPos.equals(BlockPos.ZERO)) {
            setBlockPos(stack, pos);
            player.sendOverlayMessage(Component.translatable("chat.neovitae.routing.set"));
            return InteractionResult.SUCCESS;
        }

        if (containedPos.distSqr(pos) > 16 * 16) {
            player.sendOverlayMessage(Component.translatable("chat.neovitae.routing.distance"));
            return InteractionResult.SUCCESS;
        }

        if (containedPos.equals(pos)) {
            player.sendOverlayMessage(Component.translatable("chat.neovitae.routing.same"));
            return InteractionResult.SUCCESS;
        }

        BlockEntity pastTile = level.getBlockEntity(containedPos);
        if (!(pastTile instanceof IRoutingNode pastNode)) {
            setBlockPos(stack, BlockPos.ZERO);
            return InteractionResult.FAIL;
        }

        if (pastNode instanceof IMasterRoutingNode master) {
            return connectToMaster(level, player, stack, node, master, pos, containedPos);
        } else if (node instanceof IMasterRoutingNode master) {
            return connectToMaster(level, player, stack, pastNode, master, containedPos, pos);
        }

        return connectNodes(level, player, stack, node, pastNode, pos, containedPos);
    }

    private InteractionResult unlinkNode(Level level, Player player, ItemStack stack,
                                          IRoutingNode node, BlockPos nodePos) {
        BlockPos masterPos = node.getMasterPos();
        boolean isMaster = node instanceof IMasterRoutingNode;

        node.removeAllConnections();
        if (!masterPos.equals(BlockPos.ZERO)) {
            node.removeConnection(masterPos);
        }

        setBlockPos(stack, BlockPos.ZERO);
        level.playSound(null, nodePos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 0.4F, 1.6F);
        player.sendOverlayMessage(Component.translatable(isMaster
                ? "chat.neovitae.routing.unlink.master"
                : "chat.neovitae.routing.unlink"));
        return InteractionResult.SUCCESS;
    }

    private InteractionResult connectToMaster(Level level, Player player, ItemStack stack,
                                               IRoutingNode node, IMasterRoutingNode master,
                                               BlockPos nodePos, BlockPos masterPos) {
        if (!node.isMaster(master)) {
            if (node.getMasterPos().equals(BlockPos.ZERO)) {
                node.connectMasterToRemainingNode(level, new LinkedList<>(), master);
                master.addConnection(nodePos, masterPos);
                master.addNodeToList(node);
                node.addConnection(masterPos);
                RoutingLinkHelper.fireLinkBolt(level, nodePos, masterPos);
                player.sendOverlayMessage(Component.translatable("chat.neovitae.routing.link.master"));
                // Preserve the master in the router so the next right-click binds another
                // node to the same master without re-selecting it.
                setBlockPos(stack, masterPos);
                return InteractionResult.SUCCESS;
            }
        } else {
            master.addConnection(nodePos, masterPos);
            node.addConnection(masterPos);
            RoutingLinkHelper.fireLinkBolt(level, nodePos, masterPos);
            player.sendOverlayMessage(Component.translatable("chat.neovitae.routing.link.master"));
            setBlockPos(stack, masterPos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    private InteractionResult connectNodes(Level level, Player player, ItemStack stack,
                                            IRoutingNode node, IRoutingNode pastNode,
                                            BlockPos pos, BlockPos containedPos) {
        if (pastNode.getMasterPos().equals(node.getMasterPos())) {
            // Both connected to same master (or both unconnected)
            if (!pastNode.getMasterPos().equals(BlockPos.ZERO)) {
                BlockEntity testTile = level.getBlockEntity(pastNode.getMasterPos());
                if (testTile instanceof IMasterRoutingNode master) {
                    master.addConnection(pos, containedPos);
                }
            }
            pastNode.addConnection(pos);
            node.addConnection(containedPos);
            RoutingLinkHelper.fireLinkBolt(level, containedPos, pos);
            player.sendOverlayMessage(Component.translatable("chat.neovitae.routing.link"));
            // Chain-bind: stash the just-clicked node so the next right-click continues the chain
            setBlockPos(stack, pos);
            return InteractionResult.SUCCESS;
        } else if (pastNode.getMasterPos().equals(BlockPos.ZERO)) {
            // pastNode not connected, node is connected
            BlockEntity tile = level.getBlockEntity(node.getMasterPos());
            if (tile instanceof IMasterRoutingNode master) {
                master.addConnection(pos, containedPos);
                master.addNodeToList(pastNode);
                pastNode.connectMasterToRemainingNode(level, new LinkedList<>(), master);
            }
            pastNode.addConnection(pos);
            node.addConnection(containedPos);
            RoutingLinkHelper.fireLinkBolt(level, containedPos, pos);
            player.sendOverlayMessage(Component.translatable("chat.neovitae.routing.link"));
            setBlockPos(stack, pos);
            return InteractionResult.SUCCESS;
        } else if (node.getMasterPos().equals(BlockPos.ZERO)) {
            // node not connected, pastNode is connected
            BlockEntity tile = level.getBlockEntity(pastNode.getMasterPos());
            if (tile instanceof IMasterRoutingNode master) {
                master.addConnection(pos, containedPos);
                master.addNodeToList(node);
                node.connectMasterToRemainingNode(level, new LinkedList<>(), master);
            }
            pastNode.addConnection(pos);
            node.addConnection(containedPos);
            RoutingLinkHelper.fireLinkBolt(level, containedPos, pos);
            player.sendOverlayMessage(Component.translatable("chat.neovitae.routing.link"));
            setBlockPos(stack, pos);
            return InteractionResult.SUCCESS;
        }

        // Both nodes attached to different masters - can't bridge networks, bail.
        setBlockPos(stack, BlockPos.ZERO);
        return InteractionResult.SUCCESS;
    }

    @Nullable
    private InteractionResult handleAlternatorTarget(ServerLevel level, Player player, ItemStack stack, BlockPos pos) {
        boolean alternatorMode = isAlternatorMode(stack);
        BlockPos source = alternatorMode ? alternatorSource(stack, level) : null;
        DungeonAlternatorBlockEntity sourceTile = source != null
                && level.getBlockEntity(source) instanceof DungeonAlternatorBlockEntity alternator ? alternator : null;

        if (alternatorMode && sourceTile == null) {
            setBlockPos(stack, BlockPos.ZERO);
            player.sendOverlayMessage(Component.translatable("chat.neovitae.routing.remove"));
            return InteractionResult.SUCCESS;
        }

        if (sourceTile != null) {
            if (!BlockProtectionHelper.canModifyBlock(level, pos, player)) {
                player.sendOverlayMessage(Component.translatable("chat.neovitae.alternator.protected"));
                return InteractionResult.SUCCESS;
            }
            if (sourceTile.hasReceiver(pos)) {
                sourceTile.removeReceiver(pos);
                player.sendOverlayMessage(Component.translatable("chat.neovitae.alternator.unlink"));
                return InteractionResult.SUCCESS;
            }
            BlockPos otherSource = AlternatorLinks.getSource(level, pos);
            if (otherSource != null) {
                clearLink(level, pos, otherSource);
                player.sendOverlayMessage(Component.translatable("chat.neovitae.alternator.unlink"));
                return InteractionResult.SUCCESS;
            }
            if (source.distSqr(pos) > (double) AlternatorLinks.MAX_RANGE * AlternatorLinks.MAX_RANGE) {
                player.sendOverlayMessage(Component.translatable("chat.neovitae.alternator.distance",
                        AlternatorLinks.MAX_RANGE));
                return InteractionResult.SUCCESS;
            }
            if (sourceTile.getReceivers().size() >= AlternatorLinks.MAX_RECEIVERS) {
                player.sendOverlayMessage(Component.translatable("chat.neovitae.alternator.limit",
                        AlternatorLinks.MAX_RECEIVERS));
                return InteractionResult.SUCCESS;
            }
            sourceTile.addReceiver(pos);
            RoutingLinkHelper.fireLinkBolt(level, source, pos);
            level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.4F, 1.6F);
            player.sendOverlayMessage(Component.translatable("chat.neovitae.alternator.link",
                    sourceTile.getReceivers().size(), AlternatorLinks.MAX_RECEIVERS));
            return InteractionResult.SUCCESS;
        }

        BlockPos otherSource = AlternatorLinks.getSource(level, pos);
        if (otherSource != null) {
            if (!BlockProtectionHelper.canModifyBlock(level, pos, player)) {
                player.sendOverlayMessage(Component.translatable("chat.neovitae.alternator.protected"));
                return InteractionResult.SUCCESS;
            }
            clearLink(level, pos, otherSource);
            player.sendOverlayMessage(Component.translatable("chat.neovitae.alternator.unlink"));
            return InteractionResult.SUCCESS;
        }
        return null;
    }

    private void clearLink(ServerLevel level, BlockPos receiver, BlockPos source) {
        if (level.getBlockEntity(source) instanceof DungeonAlternatorBlockEntity alternator) {
            alternator.removeReceiver(receiver);
        } else {
            AlternatorLinks.unlink(level, receiver);
        }
    }

    public boolean isAlternatorMode(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.copyTag().getBooleanOr(NBT_ALTERNATOR_MODE, false);
    }

    @Nullable
    private BlockPos alternatorSource(ItemStack stack, Level level) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        var tag = data.copyTag();
        if (!tag.getBooleanOr(NBT_ALTERNATOR_MODE, false)) return null;
        if (!level.dimension().identifier().toString().equals(tag.getStringOr(NBT_ALTERNATOR_DIM, ""))) return null;
        BlockPos pos = getBlockPos(stack);
        return pos.equals(BlockPos.ZERO) ? null : pos;
    }

    private void setAlternatorSource(ItemStack stack, Level level, BlockPos pos) {
        String dim = level.dimension().identifier().toString();
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> {
            var tag = data.copyTag();
            tag.putInt(Constants.NBT.X_COORD, pos.getX());
            tag.putInt(Constants.NBT.Y_COORD, pos.getY());
            tag.putInt(Constants.NBT.Z_COORD, pos.getZ());
            tag.putBoolean(NBT_ALTERNATOR_MODE, true);
            tag.putString(NBT_ALTERNATOR_DIM, dim);
            return CustomData.of(tag);
        });
    }

    public BlockPos getBlockPos(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return BlockPos.ZERO;

        var tag = data.copyTag();
        return new BlockPos(
                tag.getIntOr(Constants.NBT.X_COORD, 0),
                tag.getIntOr(Constants.NBT.Y_COORD, 0),
                tag.getIntOr(Constants.NBT.Z_COORD, 0));
    }

    public void setBlockPos(ItemStack stack, BlockPos pos) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> {
            var tag = data.copyTag();
            tag.putInt(Constants.NBT.X_COORD, pos.getX());
            tag.putInt(Constants.NBT.Y_COORD, pos.getY());
            tag.putInt(Constants.NBT.Z_COORD, pos.getZ());
            tag.remove(NBT_ALTERNATOR_MODE);
            tag.remove(NBT_ALTERNATOR_DIM);
            return CustomData.of(tag);
        });
    }
}
