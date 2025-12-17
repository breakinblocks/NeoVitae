package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Yawning of the Void - Destroys items in the area or from adjacent inventories.
 * Can be filtered using item filters placed in a linked chest.
 * This is a Dusk tier ritual.
 */
public class RitualYawningVoid extends Ritual {

    public static final String VOID_RANGE = "voidRange";
    public static final String CHEST_RANGE = "chestRange";

    public RitualYawningVoid() {
        super("yawning_void", 1, 10000, "ritual." + NeoVitae.MODID + ".yawning_void");
        addBlockRange(VOID_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -5, -5), 11, 11, 11));
        addBlockRange(CHEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        setMaximumVolumeAndDistanceOfRange(VOID_RANGE, 5000, 20, 20);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) {
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            return;
        }

        AreaDescriptor range = RitualHelper.getEffectiveRange(ctx.master(), this, VOID_RANGE);
        int totalCost = 0;

        // Void items on the ground
        AABB aabb = range.getAABB(ctx.masterPos());
        List<ItemEntity> items = ctx.level().getEntitiesOfClass(ItemEntity.class, aabb);

        // Get filter from chest above
        BlockPos chestPos = ctx.masterPos().above();
        IItemHandler filterInventory = ctx.level().getCapability(Capabilities.ItemHandler.BLOCK, chestPos, null);

        for (ItemEntity itemEntity : items) {
            if (totalCost + getRefreshCost() > ctx.currentEssence()) break;

            // Check against filter in chest above
            if (!passesFilter(itemEntity.getItem(), filterInventory)) {
                continue;
            }

            itemEntity.discard();
            totalCost += getRefreshCost();
        }

        // Also void items from adjacent inventories
        IItemHandler inventory = findAdjacentInventory(ctx.level(), ctx.masterPos());
        if (inventory != null) {
            for (int i = 0; i < inventory.getSlots(); i++) {
                if (totalCost + getRefreshCost() > ctx.currentEssence()) break;

                ItemStack stack = inventory.extractItem(i, 64, false);
                if (!stack.isEmpty()) {
                    totalCost += getRefreshCost() * stack.getCount() / 64 + 1;
                }
            }
        }

        if (totalCost > 0) {
            ctx.syphon(Math.min(totalCost, ctx.currentEssence()));
        }
    }

    private IItemHandler findAdjacentInventory(Level level, BlockPos pos) {
        // Look for inventory NOT directly above (that's the filter chest)
        for (BlockPos offset : new BlockPos[]{
            pos.below(), pos.north(), pos.south(), pos.east(), pos.west()
        }) {
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, offset, null);
            if (handler != null) {
                return handler;
            }
        }
        return null;
    }

    /**
     * Checks if an item should be voided based on the filter inventory.
     * If filter is null or empty, all items pass.
     * If filter has items, only matching items pass (whitelist).
     */
    private boolean passesFilter(ItemStack stack, IItemHandler filterInventory) {
        if (filterInventory == null) {
            return true; // No filter chest, void everything
        }

        boolean hasFilter = false;
        for (int i = 0; i < filterInventory.getSlots(); i++) {
            ItemStack filterStack = filterInventory.getStackInSlot(i);
            if (!filterStack.isEmpty()) {
                hasFilter = true;
                if (ItemStack.isSameItemSameComponents(stack, filterStack)) {
                    return true; // Matches filter
                }
            }
        }

        // If no items in filter, void everything; otherwise only void matching items
        return !hasFilter;
    }

    @Override
    public int getRefreshTime() {
        return 20;
    }

    @Override
    public int getRefreshCost() {
        return 5;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.DUSK);
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 0, EnumRuneType.AIR);
        addParallelRunes(components, 3, 0, EnumRuneType.DUSK);
        addCornerRunes(components, 3, 0, EnumRuneType.WATER);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualYawningVoid();
    }
}
