package com.breakinblocks.neovitae.common.blockentity.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;

/**
 * Concrete routing conduit block entity. The conduit carries no inventory or
 * filters; it is purely a graph edge that lets Input, Output and Master nodes
 * find each other across distance. All the transport-graph bookkeeping lives
 * in {@link RoutingNodeBlockEntity}, the shared base class.
 *
 * <p>Historically this role was filled by {@code RoutingNodeBlockEntity}
 * directly, which also doubled as the base class for filtered/input/output
 * nodes. Splitting the concrete relay out keeps the base class abstract and
 * removes the misleading {@code item_routing_node} naming from the registry.
 */
public class RoutingConduitBlockEntity extends RoutingNodeBlockEntity {

    public RoutingConduitBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.ROUTING_CONDUIT_TYPE.get(), pos, state);
    }
}
