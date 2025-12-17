package com.breakinblocks.neovitae.incense;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import com.breakinblocks.neovitae.common.datamap.TranquilityHelper;
import com.breakinblocks.neovitae.common.tag.BMTags;

/**
 * Registry for determining tranquility values of blocks.
 *
 * <p>This registry now uses NeoForge DataMaps as the primary source for
 * tranquility values. Values can be customized via datapacks at:
 * {@code data/<namespace>/data_maps/block/tranquility.json}</p>
 *
 * <h2>Lookup Priority</h2>
 * <ol>
 *   <li>DataMap lookup (supports blocks and block tags)</li>
 *   <li>Fluid state detection (water, lava)</li>
 *   <li>CropBlock instanceof check (for modded crops)</li>
 * </ol>
 *
 * <h2>Multiple Tag Matching</h2>
 * <p>When a block matches multiple tags in the datamap, the entry with the
 * <b>highest value</b> is used.</p>
 */
public class TranquilityRegistry {

    /**
     * Gets the tranquility stack for a block at a position.
     * First checks the datamap, then falls back to fluid/crop detection.
     *
     * @param level The level
     * @param pos   The block position
     * @param state The block state
     * @return TranquilityStack if the block provides tranquility, null otherwise
     */
    public static TranquilityStack getTranquility(Level level, BlockPos pos, BlockState state) {
        // Primary lookup: DataMap (includes both direct blocks and tag-based entries)
        TranquilityStack fromDatamap = TranquilityHelper.getTranquilityStack(state);
        if (fromDatamap != null) {
            return fromDatamap;
        }

        // Fallback: Fluid state detection for water/lava blocks not in datamap
        if (state.getFluidState().is(Fluids.WATER) || state.getFluidState().is(Fluids.FLOWING_WATER)) {
            return new TranquilityStack(EnumTranquilityType.WATER, 1.0);
        }
        if (state.getFluidState().is(Fluids.LAVA) || state.getFluidState().is(Fluids.FLOWING_LAVA)) {
            return new TranquilityStack(EnumTranquilityType.LAVA, 1.0);
        }

        // Fallback: CropBlock instanceof for modded crops not in datamap
        if (state.getBlock() instanceof CropBlock) {
            return new TranquilityStack(EnumTranquilityType.CROP, 1.0);
        }

        return null;
    }

    /**
     * Gets the incense path level for a block using tags.
     * Higher levels work at greater distances from the altar.
     *
     * @param state The block state
     * @return The path level (0-10), or -1 if not a path block
     */
    public static int getPathLevel(BlockState state) {
        // Check from highest to lowest level
        if (state.is(BMTags.Blocks.INCENSE_PATH_LEVEL_10)) return 10;
        if (state.is(BMTags.Blocks.INCENSE_PATH_LEVEL_9)) return 9;
        if (state.is(BMTags.Blocks.INCENSE_PATH_LEVEL_8)) return 8;
        if (state.is(BMTags.Blocks.INCENSE_PATH_LEVEL_7)) return 7;
        if (state.is(BMTags.Blocks.INCENSE_PATH_LEVEL_6)) return 6;
        if (state.is(BMTags.Blocks.INCENSE_PATH_LEVEL_5)) return 5;
        if (state.is(BMTags.Blocks.INCENSE_PATH_LEVEL_4)) return 4;
        if (state.is(BMTags.Blocks.INCENSE_PATH_LEVEL_3)) return 3;
        if (state.is(BMTags.Blocks.INCENSE_PATH_LEVEL_2)) return 2;
        if (state.is(BMTags.Blocks.INCENSE_PATH_LEVEL_1)) return 1;
        if (state.is(BMTags.Blocks.INCENSE_PATH_LEVEL_0)) return 0;
        return -1;
    }

    /**
     * Gets the array of path level tags for datagen.
     */
    public static TagKey<Block>[] getPathLevelTags() {
        @SuppressWarnings("unchecked")
        TagKey<Block>[] tags = new TagKey[]{
                BMTags.Blocks.INCENSE_PATH_LEVEL_0,
                BMTags.Blocks.INCENSE_PATH_LEVEL_1,
                BMTags.Blocks.INCENSE_PATH_LEVEL_2,
                BMTags.Blocks.INCENSE_PATH_LEVEL_3,
                BMTags.Blocks.INCENSE_PATH_LEVEL_4,
                BMTags.Blocks.INCENSE_PATH_LEVEL_5,
                BMTags.Blocks.INCENSE_PATH_LEVEL_6,
                BMTags.Blocks.INCENSE_PATH_LEVEL_7,
                BMTags.Blocks.INCENSE_PATH_LEVEL_8,
                BMTags.Blocks.INCENSE_PATH_LEVEL_9,
                BMTags.Blocks.INCENSE_PATH_LEVEL_10
        };
        return tags;
    }
}
