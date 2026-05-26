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

    private boolean isAllowed(int slot) {
        if (side == null) return true;
        return config.isAllowed(slot, side);
    }
}
