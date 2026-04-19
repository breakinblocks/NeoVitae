package com.breakinblocks.neovitae.datagen.content.datamap;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.datamap.BloodOrb;
import com.breakinblocks.neovitae.common.item.NVItems;

import java.util.function.Function;

public class BloodOrbStats {
        public static void bootstrap(Function<DataMapType<Item, BloodOrb>, DataMapProvider.Builder<BloodOrb, Item>> setup) {
                setup.apply(NVDataMaps.BLOOD_ORB_STATS)
                        .add(NVItems.ORB_WEAK, new BloodOrb(0, 4_000, 5_000, 2), false)
                        .add(NVItems.ORB_APPRENTICE, new BloodOrb(1, 6_000, 25_000, 5), false)
                        .add(NVItems.ORB_MAGICIAN, new BloodOrb(2, 8_000, 150_000, 15), false)
                        .add(NVItems.ORB_MASTER, new BloodOrb(3, 10_000, 1_000_000, 25), false)
                        .add(NVItems.ORB_ARCHMAGE, new BloodOrb(4, 12_000, 5_000_000, 50), false)
                        .add(NVItems.ORB_TRANSCENDENT, new BloodOrb(5, 14_000, 10_000_000, 50), false);
        }
}
