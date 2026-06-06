// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import com.breakinblocks.neovitae.common.tag.NVTags;

import javax.annotation.Nullable;

public class Utils {

    public static ItemStack insertStackIntoTile(ItemStack stack, BlockEntity tile, Direction dir) {
        IItemHandler handler = tile.getLevel().getCapability(
                net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                tile.getBlockPos(), dir);

        if (handler != null) {
            return insertStackIntoTile(stack, handler);
        } else if (tile instanceof Container container) {
            return insertStackIntoInventory(stack, container, dir);
        }

        return stack;
    }

    public static ItemStack insertStackIntoTile(ItemStack stack, IItemHandler handler) {
        return insertStackIntoTile(stack, handler, false);
    }

    /**
     * @param doCleanly If true, tries to stack with existing items first
     */
    public static ItemStack insertStackIntoTile(ItemStack stack, IItemHandler handler, boolean doCleanly) {
        int numberOfSlots = handler.getSlots();
        ItemStack copyStack = stack.copy();

        if (doCleanly) {
            for (int slot = 0; slot < numberOfSlots; slot++) {
                ItemStack containedStack = handler.getStackInSlot(slot);
                if (ItemStack.isSameItemSameComponents(stack, containedStack)) {
                    copyStack = handler.insertItem(slot, copyStack, false);
                    if (copyStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        for (int slot = 0; slot < numberOfSlots; slot++) {
            copyStack = handler.insertItem(slot, copyStack, false);
            if (copyStack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        return copyStack;
    }

    public static ItemStack insertStackIntoInventory(ItemStack stack, Container inventory, Direction dir) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack copyStack = stack.copy();

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack slotStack = inventory.getItem(slot);

            if (slotStack.isEmpty()) {
                inventory.setItem(slot, copyStack);
                return ItemStack.EMPTY;
            } else if (ItemStack.isSameItemSameComponents(slotStack, copyStack)) {
                int maxSize = Math.min(slotStack.getMaxStackSize(), inventory.getMaxStackSize());
                int space = maxSize - slotStack.getCount();
                if (space > 0) {
                    int toTransfer = Math.min(space, copyStack.getCount());
                    slotStack.grow(toTransfer);
                    copyStack.shrink(toTransfer);
                    if (copyStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        return copyStack;
    }

    public static int getNumberOfFreeSlots(BlockEntity tile, Direction dir) {
        int slots = 0;

        IItemHandler handler = tile.getLevel().getCapability(
                net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                tile.getBlockPos(), dir);

        if (handler != null) {
            for (int i = 0; i < handler.getSlots(); i++) {
                if (handler.getStackInSlot(i).isEmpty()) {
                    slots++;
                }
            }
        } else if (tile instanceof Container container) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                if (container.getItem(i).isEmpty()) {
                    slots++;
                }
            }
        }

        return slots;
    }

    public static void spawnStackAtBlock(Level level, BlockPos pos, Direction dir, ItemStack stack) {
        if (stack.isEmpty() || level.isClientSide) return;

        double x = pos.getX() + 0.5 + dir.getStepX() * 0.6;
        double y = pos.getY() + 0.5 + dir.getStepY() * 0.6;
        double z = pos.getZ() + 0.5 + dir.getStepZ() * 0.6;

        ItemEntity entity = new ItemEntity(level, x, y, z, stack.copy());
        entity.setDeltaMovement(dir.getStepX() * 0.05, dir.getStepY() * 0.05 + 0.1, dir.getStepZ() * 0.05);
        level.addFreshEntity(entity);
    }

    @Nullable
    public static IItemHandler getInventory(BlockEntity tile, @Nullable Direction facing) {
        if (tile == null || tile.getLevel() == null) return null;
        if (facing == null) facing = Direction.DOWN;

        IItemHandler handler = tile.getLevel().getCapability(
                Capabilities.ItemHandler.BLOCK, tile.getBlockPos(), facing);

        if (handler != null) {
            return handler;
        } else if (tile instanceof WorldlyContainer worldly) {
            int[] slots = worldly.getSlotsForFace(facing);
            return slots.length != 0 ? new SidedInvWrapper(worldly, facing) : null;
        } else if (tile instanceof Container container) {
            return new InvWrapper(container);
        }

        return null;
    }

    public static boolean swapLocations(Level initialWorld, BlockPos initialPos, Level finalWorld, BlockPos finalPos, boolean playSound) {
        BlockState initialState = initialWorld.getBlockState(initialPos);
        BlockState finalState = finalWorld.getBlockState(finalPos);

        if ((initialState.is(Blocks.AIR) && finalState.is(Blocks.AIR))
                || initialState.is(NVTags.Blocks.TELEPOSE_BLACKLIST)
                || finalState.is(NVTags.Blocks.TELEPOSE_BLACKLIST)) {
            return false;
        }

        BlockEntity initialTile = initialWorld.getBlockEntity(initialPos);
        BlockEntity finalTile = finalWorld.getBlockEntity(finalPos);
        HolderLookup.Provider initialRegistries = initialWorld.registryAccess();
        HolderLookup.Provider finalRegistries = finalWorld.registryAccess();
        CompoundTag initialTag = initialTile != null ? initialTile.saveWithFullMetadata(initialRegistries) : null;
        CompoundTag finalTag = finalTile != null ? finalTile.saveWithFullMetadata(finalRegistries) : null;

        if (playSound) {
            initialWorld.playSound(null, initialPos.getX(), initialPos.getY(), initialPos.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.AMBIENT, 1.0F, 1.0F);
            finalWorld.playSound(null, finalPos.getX(), finalPos.getY(), finalPos.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.AMBIENT, 1.0F, 1.0F);
        }

        if (finalState.getBlock() instanceof EntityBlock) {
            finalWorld.removeBlockEntity(finalPos);
        }
        if (initialState.getBlock() instanceof EntityBlock) {
            initialWorld.removeBlockEntity(initialPos);
        }

        finalWorld.setBlock(finalPos, initialState, Block.UPDATE_ALL);
        initialWorld.setBlock(initialPos, finalState, Block.UPDATE_ALL);

        if (initialTag != null) {
            BlockEntity newTileAtFinal = finalWorld.getBlockEntity(finalPos);
            if (newTileAtFinal != null) {
                newTileAtFinal.loadWithComponents(initialTag, finalRegistries);
                newTileAtFinal.setChanged();
            }
        }
        if (finalTag != null) {
            BlockEntity newTileAtInitial = initialWorld.getBlockEntity(initialPos);
            if (newTileAtInitial != null) {
                newTileAtInitial.loadWithComponents(finalTag, initialRegistries);
                newTileAtInitial.setChanged();
            }
        }

        initialWorld.updateNeighborsAt(initialPos, finalState.getBlock());
        finalWorld.updateNeighborsAt(finalPos, initialState.getBlock());

        if (initialWorld.getBlockTicks().hasScheduledTick(initialPos, initialState.getBlock())) {
            finalWorld.scheduleTick(finalPos, initialState.getBlock(), 20);
        }
        if (finalWorld.getBlockTicks().hasScheduledTick(finalPos, finalState.getBlock())) {
            initialWorld.scheduleTick(initialPos, finalState.getBlock(), 20);
        }

        return true;
    }
}
