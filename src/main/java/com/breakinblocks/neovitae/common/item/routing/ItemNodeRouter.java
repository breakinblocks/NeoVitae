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
import com.breakinblocks.neovitae.api.routing.*;
import com.breakinblocks.neovitae.common.routing.RoutingLinkHelper;
import com.breakinblocks.neovitae.util.Constants;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * Item for connecting routing nodes together.
 */
public class ItemNodeRouter extends Item {

    public ItemNodeRouter(Item.Properties props) {
        super(props.stacksTo(1));
    }
    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        BlockPos coords = getBlockPos(stack);
        if (coords != null && !coords.equals(BlockPos.ZERO)) {
            tooltip.accept(Component.translatable("tooltip.neovitae.noderouter.coords",
                    coords.getX(), coords.getY(), coords.getZ()));
        }
        tooltip.accept(Component.translatable("tooltip.neovitae.noderouter.unlink").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.neovitae.noderouter.clear").withStyle(ChatFormatting.GRAY));
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
            return InteractionResult.PASS;
        }

        BlockEntity tileHit = level.getBlockEntity(pos);

        if (player.isShiftKeyDown() && tileHit instanceof IRoutingNode selected && pos.equals(getBlockPos(stack))) {
            return unlinkNode(level, player, stack, selected, pos);
        }

        if (!(tileHit instanceof IRoutingNode node)) {
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
            return CustomData.of(tag);
        });
    }
}
