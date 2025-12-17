package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Domain of the Filler - Places blocks from an adjacent inventory into the ritual area.
 */
public class RitualPlacer extends Ritual {

    public static final String PLACER_RANGE = "placerRange";
    private BlockPos currentPos = null;

    public RitualPlacer() {
        super("placer", 0, 5000, "ritual." + NeoVitae.MODID + ".placer");
        addBlockRange(PLACER_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, 1, -5), 11, 10, 11));
        setMaximumVolumeAndDistanceOfRange(PLACER_RANGE, 5000, 15, 15);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) {
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            return;
        }

        AreaDescriptor range = RitualHelper.getEffectiveRange(ctx.master(), this, PLACER_RANGE);

        // Find adjacent inventory
        IItemHandler inventory = findAdjacentInventory(ctx.level(), ctx.masterPos());
        if (inventory == null) return;

        // Get block to place from inventory
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

        // Find next air block in range to fill
        UUID owner = ctx.master().getOwner();
        BlockPos placePos = findNextPlaceablePosition(ctx.level(), ctx.masterPos(), range, owner);
        if (placePos == null) return;

        // Place the block
        BlockItem blockItem = (BlockItem) toPlace.getItem();
        BlockState stateToPlace = blockItem.getBlock().defaultBlockState();

        // Check protection before placing
        if (BlockProtectionHelper.tryPlaceBlock(ctx.level(), placePos, stateToPlace, owner)) {
            // Extract item from inventory
            inventory.extractItem(slotIndex, 1, false);
            ctx.syphon(getRefreshCost());
        }
    }

    private IItemHandler findAdjacentInventory(Level level, BlockPos pos) {
        for (BlockPos offset : new BlockPos[]{
            pos.above(), pos.below(), pos.north(), pos.south(), pos.east(), pos.west()
        }) {
            var blockEntity = level.getBlockEntity(offset);
            if (blockEntity != null) {
                IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, offset, null);
                if (handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }

    private BlockPos findNextPlaceablePosition(Level level, BlockPos masterPos, AreaDescriptor range, UUID owner) {
        for (BlockPos checkPos : range.getContainedPositions(masterPos)) {
            if (level.isEmptyBlock(checkPos)) {
                // Only return positions where we can actually place
                if (BlockProtectionHelper.canPlaceBlock(level, checkPos, level.getBlockState(checkPos), owner)) {
                    return checkPos;
                }
            }
        }
        return null;
    }

    @Override
    public int getRefreshTime() {
        return 5;
    }

    @Override
    public int getRefreshCost() {
        return 10;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 0, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualPlacer();
    }
}
