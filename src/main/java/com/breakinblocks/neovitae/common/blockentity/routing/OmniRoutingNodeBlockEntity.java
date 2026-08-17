package com.breakinblocks.neovitae.common.blockentity.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.api.routing.IFluidFilter;
import com.breakinblocks.neovitae.api.routing.IInputFluidRoutingNode;
import com.breakinblocks.neovitae.api.routing.IInputItemRoutingNode;
import com.breakinblocks.neovitae.api.routing.IItemFilter;
import com.breakinblocks.neovitae.api.routing.IOutputFluidRoutingNode;
import com.breakinblocks.neovitae.api.routing.IOutputItemRoutingNode;
import com.breakinblocks.neovitae.api.routing.ISpiritusExportNode;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.menu.RoutingNodeMenu;
import com.breakinblocks.neovitae.common.routing.RoutingFilterFactory;
import com.breakinblocks.neovitae.common.routing.SideFilterConfig;
import com.breakinblocks.neovitae.util.Utils;

public class OmniRoutingNodeBlockEntity extends FilteredRoutingNodeBlockEntity
        implements IInputItemRoutingNode, IOutputItemRoutingNode,
        IInputFluidRoutingNode, IOutputFluidRoutingNode, ISpiritusExportNode, MenuProvider {

    @Nullable
    private SpiritusType spiritusExportType;
    private int spiritusStockTarget;

    public OmniRoutingNodeBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.OMNI_ROUTING_NODE_TYPE.get(), pos, state);
    }

    @Override
    public boolean isInput(Direction side) {
        return getSideFilter(side).getDirection().pulls();
    }

    @Override
    public boolean isOutput(Direction side) {
        return getSideFilter(side).getDirection().pushes();
    }

    @Override
    public boolean isFluidInput(Direction side) {
        return isInput(side);
    }

    @Override
    public boolean isFluidOutput(Direction side) {
        return isOutput(side);
    }

    @Override
    public boolean isTankConnectedToSide(Direction side) {
        return true;
    }

    @Override
    public int getFluidPriority(Direction side) {
        return priorities[side.get3DDataValue()];
    }

    @Override
    @Nullable
    public IItemFilter getInputFilterForSide(Direction side) {
        return itemFilter(side, false);
    }

    @Override
    @Nullable
    public IItemFilter getOutputFilterForSide(Direction side) {
        return itemFilter(side, true);
    }

    @Nullable
    private IItemFilter itemFilter(Direction side, boolean output) {
        SideFilterConfig cfg = getSideFilter(side);
        if (output ? !cfg.getDirection().pushes() : !cfg.getDirection().pulls()) return null;
        if (getLevel() == null) return null;

        BlockEntity tile = getLevel().getBlockEntity(worldPosition.relative(side));
        if (tile == null) return null;

        IItemHandler handler = Utils.getInventory(tile, side.getOpposite());
        if (handler == null) return null;

        return RoutingFilterFactory.createItemFilter(cfg, tile, handler, output);
    }

    @Override
    @Nullable
    public IFluidFilter getInputFluidFilterForSide(Direction side) {
        return fluidFilter(side, false);
    }

    @Override
    @Nullable
    public IFluidFilter getOutputFluidFilterForSide(Direction side) {
        return fluidFilter(side, true);
    }

    @Nullable
    private IFluidFilter fluidFilter(Direction side, boolean output) {
        SideFilterConfig cfg = getSideFilter(side);
        if (output ? !cfg.getDirection().pushes() : !cfg.getDirection().pulls()) return null;
        if (getLevel() == null) return null;

        BlockPos neighborPos = worldPosition.relative(side);
        IFluidHandler handler = getLevel().getCapability(Capabilities.FluidHandler.BLOCK, neighborPos, side.getOpposite());
        if (handler == null) return null;

        return RoutingFilterFactory.createFluidFilter(cfg, getLevel().getBlockEntity(neighborPos), handler, output);
    }

    @Override
    @Nullable
    public SpiritusType getSpiritusExportType() {
        return spiritusExportType;
    }

    @Override
    public int getSpiritusStockTarget() {
        return spiritusStockTarget;
    }

    @Override
    public void cycleSpiritusType(int direction) {
        SpiritusType[] types = SpiritusType.values();
        int current = spiritusExportType == null ? 0 : spiritusExportType.ordinal() + 1;
        int next = Math.floorMod(current + (direction < 0 ? -1 : 1), types.length + 1);
        setSpiritusExport(next == 0 ? null : types[next - 1], spiritusStockTarget);
    }

    @Override
    public void adjustSpiritusStock(int delta) {
        setSpiritusExport(spiritusExportType, Math.min(1000, Math.max(0, spiritusStockTarget + delta)));
    }

    @Override
    public void setSpiritusExport(@Nullable SpiritusType type, int stockTarget) {
        this.spiritusExportType = type;
        this.spiritusStockTarget = Math.max(0, stockTarget);
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("spiritusExportType", spiritusExportType == null ? "" : spiritusExportType.getSerializedName());
        tag.putInt("spiritusStockTarget", spiritusStockTarget);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        String typeStr = tag.getString("spiritusExportType");
        spiritusExportType = null;
        if (!typeStr.isEmpty()) {
            for (SpiritusType type : SpiritusType.values()) {
                if (type.getSerializedName().equals(typeStr)) {
                    spiritusExportType = type;
                    break;
                }
            }
        }
        spiritusStockTarget = tag.getInt("spiritusStockTarget");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.neovitae.omni_routing_node");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new RoutingNodeMenu(containerId, inventory, this);
    }
}
