package com.breakinblocks.neovitae.common.item.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.breakinblocks.neovitae.common.routing.IMasterRoutingNode;
import com.breakinblocks.neovitae.common.routing.IRoutingNode;
import com.breakinblocks.neovitae.util.Constants;

import java.util.LinkedList;
import java.util.List;

/**
 * Item for connecting routing nodes together.
 */
public class ItemNodeRouter extends Item {

    public ItemNodeRouter() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        BlockPos coords = getBlockPos(stack);
        if (coords != null && !coords.equals(BlockPos.ZERO)) {
            tooltip.add(Component.translatable("tooltip.neovitae.noderouter.coords",
                    coords.getX(), coords.getY(), coords.getZ()));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        if (level.isClientSide) {
            return InteractionResult.PASS;
        }

        BlockEntity tileHit = level.getBlockEntity(pos);

        if (!(tileHit instanceof IRoutingNode node)) {
            BlockPos containedPos = getBlockPos(stack);
            if (containedPos != null && !containedPos.equals(BlockPos.ZERO)) {
                setBlockPos(stack, BlockPos.ZERO);
                player.displayClientMessage(Component.translatable("chat.neovitae.routing.remove"), true);
                return InteractionResult.FAIL;
            }
            return InteractionResult.FAIL;
        }

        BlockPos containedPos = getBlockPos(stack);
        if (containedPos == null || containedPos.equals(BlockPos.ZERO)) {
            setBlockPos(stack, pos);
            player.displayClientMessage(Component.translatable("chat.neovitae.routing.set"), true);
            return InteractionResult.SUCCESS;
        }

        // Check distance
        if (containedPos.distSqr(pos) > 16 * 16) {
            player.displayClientMessage(Component.translatable("chat.neovitae.routing.distance"), true);
            return InteractionResult.SUCCESS;
        }

        if (containedPos.equals(pos)) {
            player.displayClientMessage(Component.translatable("chat.neovitae.routing.same"), true);
            return InteractionResult.SUCCESS;
        }

        BlockEntity pastTile = level.getBlockEntity(containedPos);
        if (!(pastTile instanceof IRoutingNode pastNode)) {
            setBlockPos(stack, BlockPos.ZERO);
            return InteractionResult.FAIL;
        }

        // Handle master node connections
        if (pastNode instanceof IMasterRoutingNode master) {
            return connectToMaster(level, player, stack, node, master, pos, containedPos);
        } else if (node instanceof IMasterRoutingNode master) {
            return connectToMaster(level, player, stack, pastNode, master, containedPos, pos);
        }

        // Both are regular nodes
        return connectNodes(level, player, stack, node, pastNode, pos, containedPos);
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
                player.displayClientMessage(Component.translatable("chat.neovitae.routing.link.master"), true);
                setBlockPos(stack, BlockPos.ZERO);
                return InteractionResult.SUCCESS;
            }
        } else {
            master.addConnection(nodePos, masterPos);
            node.addConnection(masterPos);
            player.displayClientMessage(Component.translatable("chat.neovitae.routing.link.master"), true);
            setBlockPos(stack, BlockPos.ZERO);
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
            player.displayClientMessage(Component.translatable("chat.neovitae.routing.link"), true);
            setBlockPos(stack, BlockPos.ZERO);
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
            player.displayClientMessage(Component.translatable("chat.neovitae.routing.link"), true);
            setBlockPos(stack, BlockPos.ZERO);
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
            player.displayClientMessage(Component.translatable("chat.neovitae.routing.link"), true);
            setBlockPos(stack, BlockPos.ZERO);
            return InteractionResult.SUCCESS;
        }

        setBlockPos(stack, BlockPos.ZERO);
        return InteractionResult.SUCCESS;
    }

    public BlockPos getBlockPos(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return BlockPos.ZERO;

        var tag = data.copyTag();
        return new BlockPos(
                tag.getInt(Constants.NBT.X_COORD),
                tag.getInt(Constants.NBT.Y_COORD),
                tag.getInt(Constants.NBT.Z_COORD));
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
