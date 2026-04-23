package com.breakinblocks.neovitae.ritual.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

final class HarvestHelper {

    private HarvestHelper() {}

    private static ItemStack cachedHoe;
    private static ItemStack cachedShears;

    /** Lazy-init diamond hoe for loot context. Cannot be eager — see project memory on Items.X holder binding. */
    static ItemStack mockHoe() {
        ItemStack cached = cachedHoe;
        if (cached == null) {
            cached = new ItemStack(Items.DIAMOND_HOE, 1);
            cachedHoe = cached;
        }
        return cached;
    }

    static ItemStack mockShears() {
        ItemStack cached = cachedShears;
        if (cached == null) {
            cached = new ItemStack(Items.SHEARS, 1);
            cachedShears = cached;
        }
        return cached;
    }

    static List<ItemStack> getDropsAt(ServerLevel level, BlockPos pos, BlockState state, ItemStack tool) {
        Vec3 center = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        return state.getDrops(new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, center)
                .withParameter(LootContextParams.TOOL, tool));
    }
}
