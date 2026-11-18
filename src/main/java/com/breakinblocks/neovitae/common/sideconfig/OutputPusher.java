package com.breakinblocks.neovitae.common.sideconfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

public final class OutputPusher {

    public static final int PUSH_INTERVAL = 5;
    private static final String KEY = "outputFace";

    private OutputPusher() {}

    public static boolean shouldPush(Level level) {
        return level.getGameTime() % PUSH_INTERVAL == 0;
    }

    public static void push(Level level, BlockPos pos, @Nullable Direction face,
                            IItemHandler source, int firstSlot, int lastSlot) {
        if (face == null || isEmpty(source, firstSlot, lastSlot)) return;
        IItemHandler target = level.getCapability(Capabilities.ItemHandler.BLOCK, pos.relative(face), face.getOpposite());
        if (target == null) return;
        for (int slot = firstSlot; slot <= lastSlot; slot++) {
            ItemStack available = source.extractItem(slot, Integer.MAX_VALUE, true);
            if (available.isEmpty()) continue;
            ItemStack leftover = ItemHandlerHelper.insertItemStacked(target, available, true);
            int movable = available.getCount() - leftover.getCount();
            if (movable <= 0) continue;
            ItemStack extracted = source.extractItem(slot, movable, false);
            ItemStack rejected = ItemHandlerHelper.insertItemStacked(target, extracted, false);
            if (!rejected.isEmpty()) {
                source.insertItem(slot, rejected, false);
            }
        }
    }

    private static boolean isEmpty(IItemHandler source, int firstSlot, int lastSlot) {
        for (int slot = firstSlot; slot <= lastSlot; slot++) {
            if (!source.getStackInSlot(slot).isEmpty()) return false;
        }
        return true;
    }

    public static void save(CompoundTag tag, @Nullable Direction face) {
        tag.putInt(KEY, face == null ? -1 : face.get3DDataValue());
    }

    @Nullable
    public static Direction load(CompoundTag tag) {
        int value = tag.contains(KEY) ? tag.getInt(KEY) : -1;
        return value < 0 || value >= Direction.values().length ? null : Direction.from3DDataValue(value);
    }
}
