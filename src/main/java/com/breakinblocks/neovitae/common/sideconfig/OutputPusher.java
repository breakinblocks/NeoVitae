package com.breakinblocks.neovitae.common.sideconfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public final class OutputPusher {

    public static final int PUSH_INTERVAL = 5;
    private static final String KEY = "outputFace";

    private OutputPusher() {}

    public static boolean shouldPush(Level level) {
        return level.getGameTime() % PUSH_INTERVAL == 0;
    }

    public static void push(Level level, BlockPos pos, @Nullable Direction face,
                            ResourceHandler<ItemResource> source, int firstSlot, int lastSlot) {
        if (face == null || isEmpty(source, firstSlot, lastSlot)) return;
        ResourceHandler<ItemResource> target =
                level.getCapability(Capabilities.Item.BLOCK, pos.relative(face), face.getOpposite());
        if (target == null) return;
        for (int slot = firstSlot; slot <= lastSlot; slot++) {
            ItemResource resource = source.getResource(slot);
            int amount = source.getAmountAsInt(slot);
            if (resource.isEmpty() || amount <= 0) continue;
            try (Transaction tx = Transaction.openRoot()) {
                int inserted = target.insert(resource, amount, tx);
                if (inserted <= 0) continue;
                if (source.extract(slot, resource, inserted, tx) == inserted) {
                    tx.commit();
                }
            }
        }
    }

    private static boolean isEmpty(ResourceHandler<ItemResource> source, int firstSlot, int lastSlot) {
        for (int slot = firstSlot; slot <= lastSlot; slot++) {
            if (!source.getResource(slot).isEmpty()) return false;
        }
        return true;
    }

    public static void save(ValueOutput out, @Nullable Direction face) {
        out.putInt(KEY, face == null ? -1 : face.get3DDataValue());
    }

    @Nullable
    public static Direction load(ValueInput in) {
        int value = in.getIntOr(KEY, -1);
        return value < 0 || value >= Direction.values().length ? null : Direction.from3DDataValue(value);
    }
}
