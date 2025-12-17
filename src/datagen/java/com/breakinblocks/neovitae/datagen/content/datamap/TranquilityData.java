package com.breakinblocks.neovitae.datagen.content.datamap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import com.breakinblocks.neovitae.common.datamap.BMDataMaps;
import com.breakinblocks.neovitae.common.datamap.TranquilityValue;
import com.breakinblocks.neovitae.common.tag.BMTags;
import com.breakinblocks.neovitae.incense.EnumTranquilityType;

import java.util.function.Function;

/**
 * Generates default block tranquility values for the Incense Altar system.
 *
 * <p>Tranquility values determine how much each block type contributes to the
 * overall tranquility score around an Incense Altar, which affects the
 * incense bonus multiplier.</p>
 *
 * <h2>Tag Priority</h2>
 * <p>When a block matches multiple tags, the entry with the highest value wins.
 * This is handled by the custom merger in {@link BMDataMaps#TRANQUILITY_MERGER}.</p>
 *
 * <p>Values can be overridden via datapacks at:
 * {@code data/<namespace>/data_maps/block/tranquility.json}</p>
 *
 * <h2>Block Tags</h2>
 * <p>Datapacks can use block tags for bulk configuration:</p>
 * <pre>{@code
 * {
 *   "values": {
 *     "#minecraft:logs": { "type": "tree", "value": 1.0 },
 *     "#neovitae:tranquility/plant": { "type": "plant", "value": 1.0 },
 *     "mymod:special_flower": { "type": "plant", "value": 2.0 }
 *   }
 * }
 * }</pre>
 */
public class TranquilityData {

    // Standard tranquility value for most blocks
    public static final double STANDARD_VALUE = 1.0;

    // Lower value for common earthen blocks
    public static final double EARTHEN_VALUE = 0.5;

    public static void bootstrap(Function<DataMapType<Block, TranquilityValue>, DataMapProvider.Builder<TranquilityValue, Block>> setup) {
        var builder = setup.apply(BMDataMaps.TRANQUILITY);

        // ===== Blood Magic Tranquility Tags =====
        // These are the primary tags for modpack/datapack customization
        builder
            .add(BMTags.Blocks.TRANQUILITY_PLANT, TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BMTags.Blocks.TRANQUILITY_CROP, TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BMTags.Blocks.TRANQUILITY_TREE, TranquilityValue.of(EnumTranquilityType.TREE, STANDARD_VALUE), false)
            .add(BMTags.Blocks.TRANQUILITY_EARTHEN, TranquilityValue.of(EnumTranquilityType.EARTHEN, STANDARD_VALUE), false)
            .add(BMTags.Blocks.TRANQUILITY_WATER, TranquilityValue.of(EnumTranquilityType.WATER, STANDARD_VALUE), false)
            .add(BMTags.Blocks.TRANQUILITY_FIRE, TranquilityValue.of(EnumTranquilityType.FIRE, STANDARD_VALUE), false)
            .add(BMTags.Blocks.TRANQUILITY_LAVA, TranquilityValue.of(EnumTranquilityType.LAVA, STANDARD_VALUE), false);

        // ===== Vanilla Tags for Tree Blocks =====
        builder
            .add(BlockTags.LOGS, TranquilityValue.of(EnumTranquilityType.TREE, STANDARD_VALUE), false)
            .add(BlockTags.LEAVES, TranquilityValue.of(EnumTranquilityType.TREE, STANDARD_VALUE), false);

        // ===== Vanilla Tags for Plant Blocks =====
        builder
            .add(BlockTags.FLOWERS, TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BlockTags.SMALL_FLOWERS, TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BlockTags.TALL_FLOWERS, TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false);

        // ===== Vanilla Tags for Earthen Blocks =====
        builder
            .add(BlockTags.DIRT, TranquilityValue.of(EnumTranquilityType.EARTHEN, EARTHEN_VALUE), false);

        // ===== Individual Plant Blocks =====
        // Grass and ferns
        builder
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.SHORT_GRASS), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.TALL_GRASS), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.FERN), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.LARGE_FERN), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.SEAGRASS), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.TALL_SEAGRASS), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false);

        // Vines and moss
        builder
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.VINE), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.GLOW_LICHEN), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.MOSS_BLOCK), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.MOSS_CARPET), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.HANGING_ROOTS), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false);

        // Cave vegetation
        builder
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.CAVE_VINES), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.CAVE_VINES_PLANT), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.SPORE_BLOSSOM), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.BIG_DRIPLEAF), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.BIG_DRIPLEAF_STEM), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.SMALL_DRIPLEAF), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false);

        // Mushrooms and fungi
        builder
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.BROWN_MUSHROOM), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.RED_MUSHROOM), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.BROWN_MUSHROOM_BLOCK), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.RED_MUSHROOM_BLOCK), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.MUSHROOM_STEM), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false);

        // Nether plants (also considered plants for tranquility)
        builder
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.CRIMSON_FUNGUS), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.WARPED_FUNGUS), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.CRIMSON_ROOTS), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.WARPED_ROOTS), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.NETHER_SPROUTS), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.WEEPING_VINES), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.WEEPING_VINES_PLANT), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.TWISTING_VINES), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.TWISTING_VINES_PLANT), TranquilityValue.of(EnumTranquilityType.PLANT, STANDARD_VALUE), false);

        // ===== Crop Blocks =====
        builder
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.WHEAT), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.CARROTS), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.POTATOES), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.BEETROOTS), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.MELON_STEM), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.PUMPKIN_STEM), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.ATTACHED_MELON_STEM), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.ATTACHED_PUMPKIN_STEM), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.MELON), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.PUMPKIN), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.NETHER_WART), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.SWEET_BERRY_BUSH), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.COCOA), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.SUGAR_CANE), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.BAMBOO), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.BAMBOO_SAPLING), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.KELP), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.KELP_PLANT), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.TORCHFLOWER_CROP), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.PITCHER_CROP), TranquilityValue.of(EnumTranquilityType.CROP, STANDARD_VALUE), false);

        // ===== Earthen Blocks =====
        // Individual blocks not in the #dirt tag
        builder
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.GRASS_BLOCK), TranquilityValue.of(EnumTranquilityType.EARTHEN, EARTHEN_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.PODZOL), TranquilityValue.of(EnumTranquilityType.EARTHEN, EARTHEN_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.MYCELIUM), TranquilityValue.of(EnumTranquilityType.EARTHEN, EARTHEN_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.SAND), TranquilityValue.of(EnumTranquilityType.EARTHEN, EARTHEN_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.RED_SAND), TranquilityValue.of(EnumTranquilityType.EARTHEN, EARTHEN_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.GRAVEL), TranquilityValue.of(EnumTranquilityType.EARTHEN, EARTHEN_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.CLAY), TranquilityValue.of(EnumTranquilityType.EARTHEN, EARTHEN_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.MUD), TranquilityValue.of(EnumTranquilityType.EARTHEN, EARTHEN_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.SOUL_SAND), TranquilityValue.of(EnumTranquilityType.EARTHEN, EARTHEN_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.SOUL_SOIL), TranquilityValue.of(EnumTranquilityType.EARTHEN, EARTHEN_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.FARMLAND), TranquilityValue.of(EnumTranquilityType.EARTHEN, EARTHEN_VALUE), false);

        // ===== Fire Blocks =====
        builder
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.FIRE), TranquilityValue.of(EnumTranquilityType.FIRE, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.SOUL_FIRE), TranquilityValue.of(EnumTranquilityType.FIRE, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.CAMPFIRE), TranquilityValue.of(EnumTranquilityType.FIRE, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.SOUL_CAMPFIRE), TranquilityValue.of(EnumTranquilityType.FIRE, STANDARD_VALUE), false);

        // ===== Water Blocks =====
        // Water source block provides water tranquility
        builder
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.WATER), TranquilityValue.of(EnumTranquilityType.WATER, STANDARD_VALUE), false);

        // ===== Lava Blocks =====
        builder
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.LAVA), TranquilityValue.of(EnumTranquilityType.LAVA, STANDARD_VALUE), false);

        // ===== Saplings (Tree type) =====
        builder
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.OAK_SAPLING), TranquilityValue.of(EnumTranquilityType.TREE, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.SPRUCE_SAPLING), TranquilityValue.of(EnumTranquilityType.TREE, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.BIRCH_SAPLING), TranquilityValue.of(EnumTranquilityType.TREE, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.JUNGLE_SAPLING), TranquilityValue.of(EnumTranquilityType.TREE, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.ACACIA_SAPLING), TranquilityValue.of(EnumTranquilityType.TREE, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.DARK_OAK_SAPLING), TranquilityValue.of(EnumTranquilityType.TREE, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.CHERRY_SAPLING), TranquilityValue.of(EnumTranquilityType.TREE, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.MANGROVE_PROPAGULE), TranquilityValue.of(EnumTranquilityType.TREE, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.AZALEA), TranquilityValue.of(EnumTranquilityType.TREE, STANDARD_VALUE), false)
            .add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.FLOWERING_AZALEA), TranquilityValue.of(EnumTranquilityType.TREE, STANDARD_VALUE), false);
    }
}
