package com.breakinblocks.neovitae.datagen.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Builder for Modonomicon sparse multiblock JSON definitions.
 * Sparse format uses explicit [x, y, z] coordinates per character key,
 * ideal for large structures with lots of empty space (e.g., ritual layouts).
 */
public class SparseMultiblockBuilder {
    private final Map<Character, List<int[]>> positions = new LinkedHashMap<>();
    private final Map<Character, JsonObject> matchers = new LinkedHashMap<>();

    /**
     * Add a position for the given character key.
     */
    public SparseMultiblockBuilder at(char key, int x, int y, int z) {
        positions.computeIfAbsent(key, k -> new ArrayList<>()).add(new int[]{x, y, z});
        return this;
    }

    /**
     * Add multiple positions for the given character key.
     */
    public SparseMultiblockBuilder at(char key, List<int[]> coords) {
        for (int[] coord : coords) {
            at(key, coord[0], coord[1], coord[2]);
        }
        return this;
    }

    /**
     * Map a character to a block matcher (matches block, ignores state properties).
     */
    public SparseMultiblockBuilder block(char key, Supplier<? extends Block> block) {
        JsonObject matcher = new JsonObject();
        matcher.addProperty("type", "modonomicon:block");
        matcher.addProperty("block", blockId(block));
        matchers.put(key, matcher);
        return this;
    }

    /**
     * Map a character to a block matcher with a separate display block.
     */
    public SparseMultiblockBuilder block(char key, Supplier<? extends Block> block, Supplier<? extends Block> display) {
        JsonObject matcher = new JsonObject();
        matcher.addProperty("type", "modonomicon:block");
        matcher.addProperty("block", blockId(block));
        matcher.addProperty("display", blockId(display));
        matchers.put(key, matcher);
        return this;
    }

    /**
     * Map a character to a tag matcher. When no display is set, cycles through all tag members.
     */
    public SparseMultiblockBuilder tag(char key, TagKey<Block> tag) {
        JsonObject matcher = new JsonObject();
        matcher.addProperty("type", "modonomicon:tag");
        matcher.addProperty("tag", "#" + tag.location());
        matchers.put(key, matcher);
        return this;
    }

    /**
     * Map a character to a tag matcher with a specific display block.
     */
    public SparseMultiblockBuilder tag(char key, TagKey<Block> tag, Supplier<? extends Block> display) {
        JsonObject matcher = new JsonObject();
        matcher.addProperty("type", "modonomicon:tag");
        matcher.addProperty("tag", "#" + tag.location());
        matcher.addProperty("display", blockId(display));
        matchers.put(key, matcher);
        return this;
    }

    /**
     * Map a character to a display-only matcher (renders but matches anything).
     */
    public SparseMultiblockBuilder display(char key, Supplier<? extends Block> block) {
        JsonObject matcher = new JsonObject();
        matcher.addProperty("type", "modonomicon:display");
        matcher.addProperty("display", blockId(block));
        matchers.put(key, matcher);
        return this;
    }

    /**
     * Map a character to an air matcher (invisible, used for bounding box padding).
     */
    public SparseMultiblockBuilder air(char key) {
        JsonObject matcher = new JsonObject();
        matcher.addProperty("type", "modonomicon:block");
        matcher.addProperty("block", "minecraft:air");
        matchers.put(key, matcher);
        return this;
    }

    /**
     * Map a character to a predicate matcher (custom Java predicate registered by ID).
     */
    public SparseMultiblockBuilder predicate(char key, ResourceLocation predicateId, Supplier<? extends Block> display) {
        JsonObject matcher = new JsonObject();
        matcher.addProperty("type", "modonomicon:predicate");
        matcher.addProperty("predicate", predicateId.toString());
        matcher.addProperty("display", blockId(display));
        matchers.put(key, matcher);
        return this;
    }

    /**
     * Build the sparse multiblock as a JsonObject ready for file output.
     */
    public JsonObject build() {
        return build(true);
    }

    public JsonObject build(boolean symmetrical) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "modonomicon:sparse");
        if (symmetrical) {
            root.addProperty("symmetrical", true);
        }

        // Pattern: { "B": [[x,y,z], ...], "W": [[x,y,z], ...] }
        JsonObject pattern = new JsonObject();
        for (Map.Entry<Character, List<int[]>> entry : positions.entrySet()) {
            JsonArray coordArray = new JsonArray();
            for (int[] pos : entry.getValue()) {
                JsonArray coord = new JsonArray();
                coord.add(pos[0]);
                coord.add(pos[1]);
                coord.add(pos[2]);
                coordArray.add(coord);
            }
            pattern.add(String.valueOf(entry.getKey()), coordArray);
        }
        root.add("pattern", pattern);

        // Mapping: { "B": { "type": "...", "block": "..." }, ... }
        JsonObject mapping = new JsonObject();
        for (Map.Entry<Character, JsonObject> entry : matchers.entrySet()) {
            mapping.add(String.valueOf(entry.getKey()), entry.getValue());
        }
        root.add("mapping", mapping);

        return root;
    }

    private static String blockId(Supplier<? extends Block> block) {
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block.get()).toString();
    }
}
