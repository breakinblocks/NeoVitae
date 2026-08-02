package com.breakinblocks.neovitae.common.sideconfig;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

public class SideConfigResourceHandler<T extends Resource> extends DelegatingResourceHandler<T> {

    private final SlotSideConfig config;
    @Nullable
    private final Direction side;

    public SideConfigResourceHandler(ResourceHandler<T> delegate, SlotSideConfig config, @Nullable Direction side) {
        super(delegate);
        this.config = config;
        this.side = side;
    }

    @Override
    public boolean isValid(int slot, T resource) {
        return isAllowed(slot) && super.isValid(slot, resource);
    }

    @Override
    public int insert(int slot, T resource, int amount, TransactionContext ctx) {
        if (!isAllowed(slot)) return 0;
        return super.insert(slot, resource, amount, ctx);
    }

    @Override
    public int extract(int slot, T resource, int amount, TransactionContext ctx) {
        if (!isAllowed(slot)) return 0;
        return super.extract(slot, resource, amount, ctx);
    }

    @Override
    public int insert(T resource, int amount, TransactionContext ctx) {
        int inserted = 0;
        for (int slot = 0, size = size(); slot < size && inserted < amount; slot++) {
            inserted += insert(slot, resource, amount - inserted, ctx);
        }
        return inserted;
    }

    @Override
    public int extract(T resource, int amount, TransactionContext ctx) {
        int extracted = 0;
        for (int slot = 0, size = size(); slot < size && extracted < amount; slot++) {
            extracted += extract(slot, resource, amount - extracted, ctx);
        }
        return extracted;
    }

    private boolean isAllowed(int slot) {
        if (side == null) return true;
        return config.isAllowed(slot, side);
    }
}
