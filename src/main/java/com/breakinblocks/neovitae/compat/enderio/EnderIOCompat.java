package com.breakinblocks.neovitae.compat.enderio;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.ModList;

public final class EnderIOCompat {

    private static final boolean LOADED = ModList.get().isLoaded("enderio");
    private static final Identifier POWERED_SPAWNER = Identifier.fromNamespaceAndPath("enderio", "powered_spawner");

    private EnderIOCompat() {}

    public static boolean isLoaded() {
        return LOADED;
    }

    public static boolean isPoweredSpawner(BlockEntity be) {
        if (!LOADED || be == null) return false;
        return POWERED_SPAWNER.equals(BuiltInRegistries.BLOCK.getKey(be.getBlockState().getBlock()));
    }
}
