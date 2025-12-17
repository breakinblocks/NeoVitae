package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Focus of the Ellipsoid - Builds ellipsoid/dome shapes using blocks from inventory.
 * This is a Dusk tier ritual for building complex shapes.
 */
public class RitualEllipse extends Ritual {

    public static final String ELLIPSE_RANGE = "ellipseRange";
    private int currentIndex = 0;
    private List<BlockPos> buildPositions = null;

    public RitualEllipse() {
        super("ellipse", 1, 25000, "ritual." + NeoVitae.MODID + ".ellipse");
        addBlockRange(ELLIPSE_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-10, 0, -10), 21, 15, 21));
        setMaximumVolumeAndDistanceOfRange(ELLIPSE_RANGE, 10000, 25, 25);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) {
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            return;
        }

        AreaDescriptor range = RitualHelper.getEffectiveRange(ctx.master(), this, ELLIPSE_RANGE);

        // Generate build positions if needed
        if (buildPositions == null || buildPositions.isEmpty()) {
            buildPositions = generateEllipsoidPositions(ctx.masterPos(), range);
            currentIndex = 0;
        }

        if (currentIndex >= buildPositions.size()) {
            // Building complete
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            buildPositions = null;
            return;
        }

        // Find adjacent inventory
        IItemHandler inventory = findAdjacentInventory(ctx.level(), ctx.masterPos());
        if (inventory == null) return;

        UUID owner = ctx.master().getOwner();

        // Place blocks
        int blocksPlaced = 0;
        int maxBlocksPerTick = 5;

        while (blocksPlaced < maxBlocksPerTick && currentIndex < buildPositions.size()) {
            BlockPos placePos = buildPositions.get(currentIndex);
            currentIndex++;

            if (!ctx.level().isEmptyBlock(placePos)) continue;

            // Get block from inventory
            ItemStack toPlace = ItemStack.EMPTY;
            int slotIndex = -1;
            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                    toPlace = stack;
                    slotIndex = i;
                    break;
                }
            }

            if (toPlace.isEmpty()) return;

            BlockItem blockItem = (BlockItem) toPlace.getItem();
            BlockState stateToPlace = blockItem.getBlock().defaultBlockState();

            // Check protection before placing block
            if (BlockProtectionHelper.tryPlaceBlock(ctx.level(), placePos, stateToPlace, owner)) {
                inventory.extractItem(slotIndex, 1, false);
                blocksPlaced++;
            }
        }

        ctx.syphon(getRefreshCost() * blocksPlaced);
    }

    private List<BlockPos> generateEllipsoidPositions(BlockPos masterPos, AreaDescriptor range) {
        List<BlockPos> positions = new ArrayList<>();

        AABB aabb = range.getAABB(masterPos);
        int minX = (int) aabb.minX;
        int minY = (int) aabb.minY;
        int minZ = (int) aabb.minZ;
        int maxX = (int) aabb.maxX - 1;
        int maxY = (int) aabb.maxY - 1;
        int maxZ = (int) aabb.maxZ - 1;

        double radiusX = (maxX - minX) / 2.0;
        double radiusY = (maxY - minY) / 2.0;
        double radiusZ = (maxZ - minZ) / 2.0;

        int centerX = (minX + maxX) / 2;
        int centerY = minY;
        int centerZ = (minZ + maxZ) / 2;

        // Generate dome (half ellipsoid)
        for (int y = 0; y <= radiusY; y++) {
            for (int x = (int) -radiusX; x <= radiusX; x++) {
                for (int z = (int) -radiusZ; z <= radiusZ; z++) {
                    double normalX = x / radiusX;
                    double normalY = y / radiusY;
                    double normalZ = z / radiusZ;

                    double distance = normalX * normalX + normalY * normalY + normalZ * normalZ;

                    // Shell of the ellipsoid (between 0.9 and 1.0)
                    if (distance >= 0.85 && distance <= 1.0) {
                        positions.add(new BlockPos(centerX + x, centerY + y, centerZ + z));
                    }
                }
            }
        }

        return positions;
    }

    private IItemHandler findAdjacentInventory(Level level, BlockPos pos) {
        for (BlockPos offset : new BlockPos[]{
            pos.above(), pos.below(), pos.north(), pos.south(), pos.east(), pos.west()
        }) {
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, offset, null);
            if (handler != null) {
                return handler;
            }
        }
        return null;
    }

    @Override
    public int getRefreshTime() {
        return 1;
    }

    @Override
    public int getRefreshCost() {
        return 25;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 3, 0, EnumRuneType.DUSK);
        addCornerRunes(components, 3, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 4, 0, EnumRuneType.EARTH);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualEllipse();
    }
}
