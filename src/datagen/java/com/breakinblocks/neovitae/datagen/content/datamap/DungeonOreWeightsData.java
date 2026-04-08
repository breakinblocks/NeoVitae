package com.breakinblocks.neovitae.datagen.content.datamap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;

import java.util.function.Function;

public class DungeonOreWeightsData {
    public static void bootstrap(Function<DataMapType<Block, Integer>, DataMapProvider.Builder<Integer, Block>> setup) {
        setup.apply(NVDataMaps.DUNGEON_ORE_WEIGHTS)
                .add(key(Blocks.COAL_ORE), 40, false)
                .add(key(Blocks.IRON_ORE), 30, false)
                .add(key(Blocks.COPPER_ORE), 25, false)
                .add(key(Blocks.GOLD_ORE), 15, false)
                .add(key(Blocks.REDSTONE_ORE), 15, false)
                .add(key(Blocks.LAPIS_ORE), 10, false)
                .add(key(Blocks.DIAMOND_ORE), 5, false)
                .add(key(Blocks.EMERALD_ORE), 3, false)
                .build();
    }

    private static ResourceKey<Block> key(Block block) {
        return BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow();
    }
}
