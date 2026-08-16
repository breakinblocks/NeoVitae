package com.breakinblocks.neovitae.common.blockentity.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.MenuProvider;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.api.routing.IInputFluidRoutingNode;
import com.breakinblocks.neovitae.api.routing.IInputItemRoutingNode;
import com.breakinblocks.neovitae.api.routing.IOutputFluidRoutingNode;
import com.breakinblocks.neovitae.api.routing.IOutputItemRoutingNode;
import com.breakinblocks.neovitae.api.routing.ISpiritusExportNode;
import com.breakinblocks.neovitae.api.routing.IFluidFilter;
import com.breakinblocks.neovitae.api.routing.IItemFilter;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.menu.RoutingNodeMenu;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.common.routing.FaceDirection;
import com.breakinblocks.neovitae.common.routing.RoutingFilterFactory;
import com.breakinblocks.neovitae.common.routing.SideFilterConfig;
import com.breakinblocks.neovitae.util.Utils;

public class OmniRoutingNodeBlockEntity extends FilteredRoutingNodeBlockEntity
        implements IInputItemRoutingNode, IOutputItemRoutingNode,
        IInputFluidRoutingNode, IOutputFluidRoutingNode, ISpiritusExportNode, MenuProvider {

    private static final int PARTICLE_INTERVAL = 20;
    private static final int PULL_COLOR = 0x4488FF;
    private static final int PUSH_COLOR = 0xFF8744;

    @Nullable
    private SpiritusType spiritusExportType;
    private int spiritusStockTarget;
    private int particleTimer;

    public OmniRoutingNodeBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.OMNI_ROUTING_NODE_TYPE.get(), pos, state);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        super.tick(level, pos, state);
        if (level.isClientSide()) return;
        if (++particleTimer < PARTICLE_INTERVAL) return;
        particleTimer = 0;
        emitFaceParticles(level, pos);
    }

    private void emitFaceParticles(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel) || getMasterPos().equals(BlockPos.ZERO)) return;

        for (Direction side : Direction.values()) {
            FaceDirection direction = getSideFilter(side).getDirection();
            if (direction == FaceDirection.OFF || !hasRoutableNeighbor(level, pos, side)) continue;

            int color = direction == FaceDirection.OUTPUT ? PUSH_COLOR : PULL_COLOR;
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), color, true),
                    pos.getX() + 0.5 + side.getStepX() * 0.6,
                    pos.getY() + 0.5 + side.getStepY() * 0.6,
                    pos.getZ() + 0.5 + side.getStepZ() * 0.6,
                    1, 0.08, 0.08, 0.08, 0.0);
        }
    }

    private static boolean hasRoutableNeighbor(Level level, BlockPos pos, Direction side) {
        BlockPos neighbor = pos.relative(side);
        Direction face = side.getOpposite();
        return level.getCapability(Capabilities.Item.BLOCK, neighbor, face) != null
                || level.getCapability(Capabilities.Fluid.BLOCK, neighbor, face) != null
                || level.getCapability(Capabilities.Energy.BLOCK, neighbor, face) != null;
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
        if (level == null) return null;

        var tile = level.getBlockEntity(worldPosition.relative(side));
        if (tile == null) return null;

        ResourceHandler<ItemResource> handler = Utils.getInventory(tile, side.getOpposite());
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
        if (level == null) return null;

        ResourceHandler<FluidResource> handler = level.getCapability(
                Capabilities.Fluid.BLOCK, worldPosition.relative(side), side.getOpposite());
        if (handler == null) return null;

        return RoutingFilterFactory.createFluidFilter(cfg, level.getBlockEntity(worldPosition.relative(side)), handler, output);
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
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putString("spiritusExportType", spiritusExportType == null ? "" : spiritusExportType.getSerializedName());
        tag.putInt("spiritusStockTarget", spiritusStockTarget);
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        String typeStr = tag.getStringOr("spiritusExportType", "");
        spiritusExportType = null;
        if (!typeStr.isEmpty()) {
            for (SpiritusType type : SpiritusType.values()) {
                if (type.getSerializedName().equals(typeStr)) {
                    spiritusExportType = type;
                    break;
                }
            }
        }
        spiritusStockTarget = tag.getIntOr("spiritusStockTarget", 0);
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
