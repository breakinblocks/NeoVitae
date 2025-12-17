package com.breakinblocks.neovitae.common.blockentity.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import com.breakinblocks.neovitae.common.blockentity.BMTiles;
import com.breakinblocks.neovitae.common.item.routing.IItemFilterProvider;
import com.breakinblocks.neovitae.common.menu.RoutingNodeMenu;
import com.breakinblocks.neovitae.common.routing.IItemFilter;
import com.breakinblocks.neovitae.common.routing.IOutputItemRoutingNode;
import com.breakinblocks.neovitae.util.Utils;

/**
 * Output routing node - pushes items to connected inventories.
 */
public class OutputRoutingNodeTile extends FilteredRoutingNodeTile implements IOutputItemRoutingNode, MenuProvider {

    public OutputRoutingNodeTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, 6, pos, state);
    }

    public OutputRoutingNodeTile(BlockPos pos, BlockState state) {
        this(BMTiles.OUTPUT_ROUTING_NODE_TYPE.get(), pos, state);
    }

    @Override
    public boolean isOutput(Direction side) {
        return true;
    }

    @Override
    public IItemFilter getOutputFilterForSide(Direction side) {
        BlockEntity tile = getLevel().getBlockEntity(worldPosition.relative(side));
        if (tile != null) {
            IItemHandler handler = Utils.getInventory(tile, side.getOpposite());
            if (handler != null) {
                ItemStack filterStack = this.getFilterStack(side);

                if (filterStack.isEmpty() || !(filterStack.getItem() instanceof IItemFilterProvider filter)) {
                    return null;
                }

                return filter.getOutputItemFilter(filterStack, tile, handler);
            }
        }
        return null;
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new RoutingNodeMenu(containerId, playerInventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.neovitae.output_routing_node");
    }
}
