package com.breakinblocks.neovitae.datagen.content.datamap;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.item.NVItems;

import java.util.function.Function;

public class SpiritusGemMax {
    public static void bootstrap(Function<DataMapType<Item, Double>, DataMapProvider.Builder<Double, Item>> setup) {
        setup.apply(NVDataMaps.SPIRITUS_GEM_MAX_AMOUNTS)
                .add(NVItems.SPIRITUS_GEM_PETTY.getKey(), 64D, false)
                .add(NVItems.SPIRITUS_GEM_LESSER.getKey(), 256D, false)
                .add(NVItems.SPIRITUS_GEM_COMMON.getKey(), 1024D, false)
                .add(NVItems.SPIRITUS_GEM_GREATER.getKey(), 4096D, false)
                .add(NVItems.SPIRITUS_GEM_GRAND.getKey(), 16384D, false)
                .build();
    }
}
