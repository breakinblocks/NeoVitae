package com.breakinblocks.neovitae.common.command;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.routing.IMasterRoutingNode;
import com.breakinblocks.neovitae.api.routing.IRoutingNode;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.SpiritAccumulatorBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.FilteredRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.OmniRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.OutputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.routing.FaceDirection;
import com.breakinblocks.neovitae.common.routing.FilterMode;
import com.breakinblocks.neovitae.common.routing.RoutingLinkHelper;
import com.breakinblocks.neovitae.common.routing.SideFilterConfig;

import java.util.ArrayList;
import java.util.List;

public final class RoutingShowcase {

    private static final int RIG_WIDTH = 11;
    private static final int RIG_DEPTH = 9;
    private static final int RIG_COLS = 4;

    private static final int LINE_Z = 4;
    private static final int SIGN_Z = 1;

    private RoutingShowcase() {}

    private interface Rig {
        void build(ServerLevel level, int x, int y, int z);
    }

    private record Station(String[] label, Rig rig) {}

    private static List<Station> stations() {
        List<Station> out = new ArrayList<>();
        out.add(new Station(new String[]{"Items", "Pass All", "everything moves"}, RoutingShowcase::rigPassAll));
        out.add(new Station(new String[]{"Items", "Whitelist", "cobble only"}, RoutingShowcase::rigWhitelist));
        out.add(new Station(new String[]{"Items", "Blacklist", "no diamonds"}, RoutingShowcase::rigBlacklist));
        out.add(new Station(new String[]{"Items", "Stock 16", "output stops", "at 16 cobble"}, RoutingShowcase::rigOutputStock));
        out.add(new Station(new String[]{"Items", "Leave 16", "source keeps", "16 cobble"}, RoutingShowcase::rigInputKeep));
        out.add(new Station(new String[]{"Items", "Priority", "near chest", "fills first"}, RoutingShowcase::rigPriority));
        out.add(new Station(new String[]{"Fluid", "Auto Match", "tank to tank"}, RoutingShowcase::rigFluid));
        out.add(new Station(new String[]{"Energy", "Battery", "charged to", "empty"}, RoutingShowcase::rigEnergy));
        out.add(new Station(new String[]{"Omni Node", "pulls west", "pushes down"}, RoutingShowcase::rigOmni));
        out.add(new Station(new String[]{"Conduit", "Relay chain", "two hops to", "the master"}, RoutingShowcase::rigConduit));
        out.add(new Station(new String[]{"Redstone", "Halted", "powered node", "moves nothing"}, RoutingShowcase::rigRedstone));
        out.add(new Station(new String[]{"Upgrades", "Speed + Stack", "drains fast"}, RoutingShowcase::rigUpgrades));
        out.add(new Station(new String[]{"Spiritus", "Chunk Export", "accumulator", "to this chunk"}, RoutingShowcase::rigSpiritus));
        return out;
    }

    public static int sectionWidth() {
        return RIG_COLS * RIG_WIDTH;
    }

    public static int sectionDepth() {
        int rows = (stations().size() + RIG_COLS - 1) / RIG_COLS;
        return rows * RIG_DEPTH;
    }

    public static int place(ServerLevel level, int startX, int baseY, int startZ) {
        List<Station> stations = stations();
        int placed = 0;

        for (int i = 0; i < stations.size(); i++) {
            int x = startX + (i % RIG_COLS) * RIG_WIDTH;
            int z = startZ + (i / RIG_COLS) * RIG_DEPTH;
            Station station = stations.get(i);

            try {
                station.rig().build(level, x, baseY + 1, z);
                level.setBlock(new BlockPos(x + 2, baseY, z + SIGN_Z), Blocks.POLISHED_ANDESITE.defaultBlockState(), 2);
                ShowcaseCommand.placeLabel(level, new BlockPos(x + 2, baseY + 1, z + SIGN_Z), 8, station.label());
                placed++;
            } catch (Exception e) {
                NeoVitae.LOGGER.warn("Failed to build routing showcase rig {}: {}", station.label()[0], e.getMessage());
            }
        }
        return placed;
    }

    // ---------- rigs ----------

    private static void rigPassAll(ServerLevel level, int x, int y, int z) {
        linear(level, x, y, z,
                new ItemStack[]{new ItemStack(Items.COBBLESTONE, 64), new ItemStack(Items.DIAMOND, 16)},
                (in, out) -> {
                    passAll(in, Direction.WEST);
                    passAll(out, Direction.EAST);
                });
    }

    private static void rigWhitelist(ServerLevel level, int x, int y, int z) {
        linear(level, x, y, z,
                new ItemStack[]{new ItemStack(Items.COBBLESTONE, 64), new ItemStack(Items.DIAMOND, 16), new ItemStack(Items.IRON_INGOT, 16)},
                (in, out) -> {
                    passAll(in, Direction.WEST);
                    whitelist(out, Direction.EAST, 0, Items.COBBLESTONE);
                });
    }

    private static void rigBlacklist(ServerLevel level, int x, int y, int z) {
        linear(level, x, y, z,
                new ItemStack[]{new ItemStack(Items.COBBLESTONE, 64), new ItemStack(Items.DIAMOND, 16), new ItemStack(Items.IRON_INGOT, 16)},
                (in, out) -> {
                    passAll(in, Direction.WEST);
                    blacklist(out, Direction.EAST, Items.DIAMOND);
                });
    }

    private static void rigOutputStock(ServerLevel level, int x, int y, int z) {
        linear(level, x, y, z,
                new ItemStack[]{new ItemStack(Items.COBBLESTONE, 64)},
                (in, out) -> {
                    passAll(in, Direction.WEST);
                    whitelist(out, Direction.EAST, 16, Items.COBBLESTONE);
                });
    }

    private static void rigInputKeep(ServerLevel level, int x, int y, int z) {
        linear(level, x, y, z,
                new ItemStack[]{new ItemStack(Items.COBBLESTONE, 64)},
                (in, out) -> {
                    whitelist(in, Direction.WEST, 16, Items.COBBLESTONE);
                    passAll(out, Direction.EAST);
                });
    }

    private static void rigPriority(ServerLevel level, int x, int y, int z) {
        BlockPos srcPos = new BlockPos(x, y, z + LINE_Z);
        BlockPos inPos = new BlockPos(x + 1, y, z + LINE_Z);
        BlockPos masterPos = new BlockPos(x + 2, y, z + LINE_Z);
        BlockPos nearOutPos = new BlockPos(x + 3, y, z + LINE_Z);
        BlockPos nearDstPos = new BlockPos(x + 4, y, z + LINE_Z);
        BlockPos farOutPos = new BlockPos(x + 3, y, z + LINE_Z + 2);
        BlockPos farDstPos = new BlockPos(x + 4, y, z + LINE_Z + 2);

        chest(level, srcPos, new ItemStack(Items.COBBLESTONE, 64));
        chest(level, nearDstPos);
        chest(level, farDstPos);
        node(level, inPos, NVBlocks.INPUT_ROUTING_NODE.block().get());
        node(level, masterPos, NVBlocks.MASTER_ROUTING_NODE.block().get());
        node(level, nearOutPos, NVBlocks.OUTPUT_ROUTING_NODE.block().get());
        node(level, farOutPos, NVBlocks.OUTPUT_ROUTING_NODE.block().get());
        refresh(level, inPos, masterPos, nearOutPos, farOutPos);

        MasterRoutingNodeBlockEntity master = master(level, masterPos);
        FilteredRoutingNodeBlockEntity in = filtered(level, inPos);
        FilteredRoutingNodeBlockEntity near = filtered(level, nearOutPos);
        FilteredRoutingNodeBlockEntity far = filtered(level, farOutPos);
        if (master == null || in == null || near == null || far == null) return;

        passAll(in, Direction.WEST);
        passAll(near, Direction.EAST);
        passAll(far, Direction.EAST);
        near.priorities[Direction.EAST.get3DDataValue()] = 5;

        bind(level, in, inPos, master, masterPos);
        bind(level, near, nearOutPos, master, masterPos);
        bind(level, far, farOutPos, master, masterPos);
    }

    private static void rigFluid(ServerLevel level, int x, int y, int z) {
        BlockPos srcPos = new BlockPos(x, y, z + LINE_Z);
        BlockPos inPos = new BlockPos(x + 1, y, z + LINE_Z);
        BlockPos masterPos = new BlockPos(x + 2, y, z + LINE_Z);
        BlockPos outPos = new BlockPos(x + 3, y, z + LINE_Z);
        BlockPos dstPos = new BlockPos(x + 4, y, z + LINE_Z);

        tank(level, srcPos, 3, 64_000);
        tank(level, dstPos, 3, 0);
        node(level, inPos, NVBlocks.INPUT_ROUTING_NODE.block().get());
        node(level, masterPos, NVBlocks.MASTER_ROUTING_NODE.block().get());
        node(level, outPos, NVBlocks.OUTPUT_ROUTING_NODE.block().get());
        refresh(level, inPos, masterPos, outPos);

        MasterRoutingNodeBlockEntity master = master(level, masterPos);
        FilteredRoutingNodeBlockEntity in = filtered(level, inPos);
        FilteredRoutingNodeBlockEntity out = filtered(level, outPos);
        if (master == null || in == null || out == null) return;

        in.getSideFilter(Direction.WEST).setEnabled(true);
        out.getSideFilter(Direction.EAST).setEnabled(true);
        in.setChanged();
        out.setChanged();

        bind(level, in, inPos, master, masterPos);
        bind(level, out, outPos, master, masterPos);
    }

    private static void rigEnergy(ServerLevel level, int x, int y, int z) {
        BlockPos srcPos = new BlockPos(x, y, z + LINE_Z);
        BlockPos inPos = new BlockPos(x + 1, y, z + LINE_Z);
        BlockPos masterPos = new BlockPos(x + 2, y, z + LINE_Z);
        BlockPos outPos = new BlockPos(x + 3, y, z + LINE_Z);
        BlockPos dstPos = new BlockPos(x + 4, y, z + LINE_Z);

        battery(level, srcPos, 1_000_000);
        battery(level, dstPos, 0);
        node(level, inPos, NVBlocks.INPUT_ROUTING_NODE.block().get());
        node(level, masterPos, NVBlocks.MASTER_ROUTING_NODE.block().get());
        node(level, outPos, NVBlocks.OUTPUT_ROUTING_NODE.block().get());
        refresh(level, inPos, masterPos, outPos);

        MasterRoutingNodeBlockEntity master = master(level, masterPos);
        FilteredRoutingNodeBlockEntity in = filtered(level, inPos);
        FilteredRoutingNodeBlockEntity out = filtered(level, outPos);
        if (master == null || in == null || out == null) return;

        in.getSideFilter(Direction.WEST).setEnabled(true);
        out.getSideFilter(Direction.EAST).setEnabled(true);
        in.setChanged();
        out.setChanged();

        bind(level, in, inPos, master, masterPos);
        bind(level, out, outPos, master, masterPos);
    }

    private static void rigOmni(ServerLevel level, int x, int y, int z) {
        BlockPos westChestPos = new BlockPos(x + 1, y + 1, z + LINE_Z);
        BlockPos omniPos = new BlockPos(x + 2, y + 1, z + LINE_Z);
        BlockPos downChestPos = new BlockPos(x + 2, y, z + LINE_Z);
        BlockPos masterPos = new BlockPos(x + 3, y + 1, z + LINE_Z);

        chest(level, westChestPos, new ItemStack(Items.COBBLESTONE, 64));
        chest(level, downChestPos);
        node(level, omniPos, NVBlocks.OMNI_ROUTING_NODE.block().get());
        node(level, masterPos, NVBlocks.MASTER_ROUTING_NODE.block().get());
        refresh(level, omniPos, masterPos);

        MasterRoutingNodeBlockEntity master = master(level, masterPos);
        if (!(level.getBlockEntity(omniPos) instanceof OmniRoutingNodeBlockEntity omni) || master == null) return;

        omniSide(omni, Direction.WEST, FaceDirection.INPUT, 16, Items.COBBLESTONE);
        omniSide(omni, Direction.DOWN, FaceDirection.OUTPUT, 16, Items.COBBLESTONE);

        bind(level, omni, omniPos, master, masterPos);
    }

    private static void rigConduit(ServerLevel level, int x, int y, int z) {
        BlockPos srcPos = new BlockPos(x, y, z + LINE_Z);
        BlockPos inPos = new BlockPos(x + 1, y, z + LINE_Z);
        BlockPos relayFarPos = new BlockPos(x + 2, y, z + LINE_Z);
        BlockPos relayNearPos = new BlockPos(x + 3, y, z + LINE_Z);
        BlockPos masterPos = new BlockPos(x + 4, y, z + LINE_Z);
        BlockPos outPos = new BlockPos(x + 5, y, z + LINE_Z);
        BlockPos dstPos = new BlockPos(x + 6, y, z + LINE_Z);

        chest(level, srcPos, new ItemStack(Items.COBBLESTONE, 64), new ItemStack(Items.GOLD_INGOT, 16));
        chest(level, dstPos);
        node(level, inPos, NVBlocks.INPUT_ROUTING_NODE.block().get());
        node(level, relayFarPos, NVBlocks.ROUTING_CONDUIT.block().get());
        node(level, relayNearPos, NVBlocks.ROUTING_CONDUIT.block().get());
        node(level, masterPos, NVBlocks.MASTER_ROUTING_NODE.block().get());
        node(level, outPos, NVBlocks.OUTPUT_ROUTING_NODE.block().get());
        refresh(level, inPos, relayFarPos, relayNearPos, masterPos, outPos);

        MasterRoutingNodeBlockEntity master = master(level, masterPos);
        FilteredRoutingNodeBlockEntity in = filtered(level, inPos);
        FilteredRoutingNodeBlockEntity out = filtered(level, outPos);
        if (master == null || in == null || out == null) return;
        if (!(level.getBlockEntity(relayFarPos) instanceof IRoutingNode relayFar)) return;
        if (!(level.getBlockEntity(relayNearPos) instanceof IRoutingNode relayNear)) return;

        passAll(in, Direction.WEST);
        passAll(out, Direction.EAST);

        bind(level, out, outPos, master, masterPos);
        bind(level, relayNear, relayNearPos, master, masterPos);
        RoutingLinkHelper.bindThroughNeighbor(level, relayFar, relayFarPos, relayNear, relayNearPos);
        RoutingLinkHelper.bindThroughNeighbor(level, in, inPos, relayFar, relayFarPos);
    }

    private static void rigRedstone(ServerLevel level, int x, int y, int z) {
        linear(level, x, y, z,
                new ItemStack[]{new ItemStack(Items.COBBLESTONE, 64)},
                (in, out) -> {
                    passAll(in, Direction.WEST);
                    passAll(out, Direction.EAST);
                });
        level.setBlock(new BlockPos(x + 1, y, z + LINE_Z - 1), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
    }

    private static void rigUpgrades(ServerLevel level, int x, int y, int z) {
        linear(level, x, y, z, fullChestOfCobble(),
                (in, out) -> {
                    passAll(in, Direction.WEST);
                    passAll(out, Direction.EAST);
                });

        MasterRoutingNodeBlockEntity master = master(level, new BlockPos(x + 2, y, z + LINE_Z));
        if (master == null) return;
        master.setItem(MasterRoutingNodeBlockEntity.SLOT_SPEED_UPGRADE,
                new ItemStack(NVItems.MASTER_NODE_UPGRADE_SPEED.get(), 19));
        master.setItem(MasterRoutingNodeBlockEntity.SLOT_STACK_UPGRADE,
                new ItemStack(NVItems.MASTER_NODE_UPGRADE.get(), 8));
        master.setChanged();
    }

    private static void rigSpiritus(ServerLevel level, int x, int y, int z) {
        BlockPos accumulatorPos = new BlockPos(x + 1, y, z + LINE_Z);
        BlockPos masterPos = new BlockPos(x + 2, y, z + LINE_Z);
        BlockPos outPos = new BlockPos(x + 3, y, z + LINE_Z);

        node(level, accumulatorPos, NVBlocks.SPIRIT_ACCUMULATOR.block().get());
        node(level, masterPos, NVBlocks.MASTER_ROUTING_NODE.block().get());
        node(level, outPos, NVBlocks.OUTPUT_ROUTING_NODE.block().get());
        refresh(level, masterPos, outPos);

        MasterRoutingNodeBlockEntity master = master(level, masterPos);
        if (master == null) return;
        if (!(level.getBlockEntity(accumulatorPos) instanceof SpiritAccumulatorBlockEntity accumulator)) return;
        if (!(level.getBlockEntity(outPos) instanceof OutputRoutingNodeBlockEntity out)) return;

        accumulator.attuneTo(SpiritusType.RAW);
        accumulator.insert(SpiritusType.RAW, SpiritAccumulatorBlockEntity.CAPACITY, false);
        out.setSpiritusExport(SpiritusType.RAW, 500);

        bind(level, accumulator, accumulatorPos, master, masterPos);
        bind(level, out, outPos, master, masterPos);
    }

    // ---------- rig scaffolding ----------

    private interface LinearConfig {
        void apply(FilteredRoutingNodeBlockEntity input, FilteredRoutingNodeBlockEntity output);
    }

    private static void linear(ServerLevel level, int x, int y, int z, ItemStack[] source, LinearConfig config) {
        BlockPos srcPos = new BlockPos(x, y, z + LINE_Z);
        BlockPos inPos = new BlockPos(x + 1, y, z + LINE_Z);
        BlockPos masterPos = new BlockPos(x + 2, y, z + LINE_Z);
        BlockPos outPos = new BlockPos(x + 3, y, z + LINE_Z);
        BlockPos dstPos = new BlockPos(x + 4, y, z + LINE_Z);

        chest(level, srcPos, source);
        chest(level, dstPos);
        node(level, inPos, NVBlocks.INPUT_ROUTING_NODE.block().get());
        node(level, masterPos, NVBlocks.MASTER_ROUTING_NODE.block().get());
        node(level, outPos, NVBlocks.OUTPUT_ROUTING_NODE.block().get());
        refresh(level, inPos, masterPos, outPos);

        MasterRoutingNodeBlockEntity master = master(level, masterPos);
        FilteredRoutingNodeBlockEntity in = filtered(level, inPos);
        FilteredRoutingNodeBlockEntity out = filtered(level, outPos);
        if (master == null || in == null || out == null) return;

        config.apply(in, out);
        bind(level, in, inPos, master, masterPos);
        bind(level, out, outPos, master, masterPos);
    }

    private static ItemStack[] fullChestOfCobble() {
        ItemStack[] stacks = new ItemStack[27];
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = new ItemStack(Items.COBBLESTONE, 64);
        }
        return stacks;
    }

    // ---------- placement helpers ----------

    private static void node(ServerLevel level, BlockPos pos, Block block) {
        level.setBlock(pos, block.defaultBlockState(), 2);
    }

    private static void refresh(ServerLevel level, BlockPos... positions) {
        for (BlockPos pos : positions) {
            BlockState state = level.getBlockState(pos);
            level.setBlock(pos, Block.updateFromNeighbourShapes(state, level, pos), 2);
        }
    }

    private static void chest(ServerLevel level, BlockPos pos, ItemStack... contents) {
        level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 2);
        if (!(level.getBlockEntity(pos) instanceof Container container)) return;
        for (int i = 0; i < contents.length && i < container.getContainerSize(); i++) {
            container.setItem(i, contents[i].copy());
        }
        container.setChanged();
    }

    private static void tank(ServerLevel level, BlockPos pos, int tier, int amount) {
        level.setBlock(pos, NVBlocks.BLOOD_TANK.block().get().defaultBlockState(), 2);
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) return;

        CompoundTag tag = be.saveWithoutMetadata(level.registryAccess());
        tag.putInt("tier", tier);
        be.loadWithComponents(tag, level.registryAccess());

        if (amount <= 0) return;
        IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
        if (handler != null) {
            handler.fill(new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE.get(), amount), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    private static void battery(ServerLevel level, BlockPos pos, int energy) {
        level.setBlock(pos, NVBlocks.BLOOD_BATTERY.block().get().defaultBlockState(), 2);
        if (energy <= 0) return;
        IEnergyStorage storage = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, null);
        if (storage != null) {
            storage.receiveEnergy(energy, false);
        }
    }

    // ---------- filter helpers ----------

    private static void passAll(FilteredRoutingNodeBlockEntity node, Direction side) {
        blacklist(node, side);
    }

    private static void blacklist(FilteredRoutingNodeBlockEntity node, Direction side, Item... items) {
        SideFilterConfig cfg = node.getSideFilter(side);
        cfg.setEnabled(true);
        cfg.setItemMode(FilterMode.BLACKLIST);
        cfg.clearItemGhosts();
        for (int i = 0; i < items.length; i++) {
            cfg.setItemGhost(i, new ItemStack(items[i]));
        }
        node.setChanged();
    }

    private static void whitelist(FilteredRoutingNodeBlockEntity node, Direction side, int amount, Item... items) {
        SideFilterConfig cfg = node.getSideFilter(side);
        cfg.setEnabled(true);
        cfg.setItemMode(FilterMode.WHITELIST);
        cfg.clearItemGhosts();
        for (int i = 0; i < items.length; i++) {
            cfg.setItemGhost(i, new ItemStack(items[i]));
        }
        for (int i = 0; i < items.length; i++) {
            cfg.setItemAmount(i, amount);
        }
        node.setChanged();
    }

    private static void omniSide(OmniRoutingNodeBlockEntity node, Direction side, FaceDirection direction,
                                 int amount, Item... items) {
        SideFilterConfig cfg = node.getSideFilter(side);
        cfg.setDirection(direction);
        cfg.setItemMode(FilterMode.WHITELIST);
        cfg.clearItemGhosts();
        for (int i = 0; i < items.length; i++) {
            cfg.setItemGhost(i, new ItemStack(items[i]));
        }
        for (int i = 0; i < items.length; i++) {
            cfg.setItemAmount(i, amount);
        }
        node.setChanged();
    }

    // ---------- lookup helpers ----------

    private static MasterRoutingNodeBlockEntity master(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof MasterRoutingNodeBlockEntity be ? be : null;
    }

    private static FilteredRoutingNodeBlockEntity filtered(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof FilteredRoutingNodeBlockEntity be ? be : null;
    }

    private static void bind(ServerLevel level, IRoutingNode node, BlockPos nodePos,
                             IMasterRoutingNode master, BlockPos masterPos) {
        RoutingLinkHelper.bindToMaster(level, node, nodePos, master, masterPos);
    }
}
