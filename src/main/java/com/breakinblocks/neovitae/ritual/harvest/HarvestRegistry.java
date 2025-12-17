package com.breakinblocks.neovitae.ritual.harvest;

import com.google.common.collect.*;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry for harvest handlers and crop configurations.
 * Thread-safe: All collections use concurrent implementations for safe access
 * from multiple threads during mod initialization and gameplay.
 */
public class HarvestRegistry {

    private static final List<IHarvestHandler> HARVEST_HANDLERS = new CopyOnWriteArrayList<>();
    private static final Map<Block, Integer> STANDARD_CROPS = new ConcurrentHashMap<>();
    private static final Set<BlockState> TALL_CROPS = ConcurrentHashMap.newKeySet();
    private static final Multimap<BlockState, BlockState> STEM_CROPS = Multimaps.synchronizedMultimap(ArrayListMultimap.create());
    private static final Map<BlockState, Integer> AMPLIFIERS = new ConcurrentHashMap<>();

    /**
     * Registers a handler for the Harvest Ritual to call.
     *
     * @param handler - The custom handler to register
     */
    public static void registerHandler(IHarvestHandler handler) {
        if (!HARVEST_HANDLERS.contains(handler)) {
            HARVEST_HANDLERS.add(handler);
        }
    }

    /**
     * Registers a standard crop (IE: Wheat, Carrots, Potatoes, Netherwart, etc) for
     * the HarvestHandlerPlantable handler to handle.
     *
     * @param crop       - The crop block to handle.
     * @param matureMeta - The age value at which the crop is considered mature.
     */
    public static void registerStandardCrop(Block crop, int matureMeta) {
        if (!STANDARD_CROPS.containsKey(crop)) {
            STANDARD_CROPS.put(crop, matureMeta);
        }
    }

    /**
     * Registers a tall crop (Sugar Cane and Cactus) for the
     * HarvestHandlerTall handler to handle.
     *
     * @param crop - The crop block state to handle.
     */
    public static void registerTallCrop(BlockState crop) {
        if (!TALL_CROPS.contains(crop)) {
            TALL_CROPS.add(crop);
        }
    }

    /**
     * Registers a stem crop (Melon and Pumpkin) for the
     * HarvestHandlerStem handler to handle.
     *
     * The Stem must be instanceof AttachedStemBlock.
     *
     * @param crop - The crop block state to handle.
     * @param stem - The stem block state of the crop
     */
    public static void registerStemCrop(BlockState crop, BlockState stem) {
        if (!STEM_CROPS.containsKey(crop) && stem.getBlock() instanceof AttachedStemBlock) {
            STEM_CROPS.put(stem, crop);
        }
    }

    /**
     * Registers a range amplifier for the Harvest Ritual.
     *
     * @param block - The block state for the amplifier.
     * @param range - The range the amplifier provides.
     */
    public static void registerRangeAmplifier(BlockState block, int range) {
        if (!AMPLIFIERS.containsKey(block)) {
            AMPLIFIERS.put(block, range);
        }
    }

    public static List<IHarvestHandler> getHarvestHandlers() {
        return ImmutableList.copyOf(HARVEST_HANDLERS);
    }

    public static Map<Block, Integer> getStandardCrops() {
        return ImmutableMap.copyOf(STANDARD_CROPS);
    }

    public static Set<BlockState> getTallCrops() {
        return ImmutableSet.copyOf(TALL_CROPS);
    }

    public static Multimap<BlockState, BlockState> getStemCrops() {
        return ImmutableMultimap.copyOf(STEM_CROPS);
    }

    public static Map<BlockState, Integer> getAmplifiers() {
        return ImmutableMap.copyOf(AMPLIFIERS);
    }
}
