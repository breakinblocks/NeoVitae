package com.breakinblocks.neovitae.common.structure;

import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.data.LoaderRegistry;
import com.klikli_dev.modonomicon.multiblock.SparseMultiblock;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds a Modonomicon sparse multiblock for a ritual from its rune layout so
 * the in-book preview matches the loaded datapack layout.
 */
public final class RitualMultiblockBuilder {

    private RitualMultiblockBuilder() {
    }

    public static Multiblock build(List<RitualComponent> components, HolderLookup.Provider provider) {
        Map<Character, JsonArray> positions = new LinkedHashMap<>();
        Map<Character, Block> mapping = new LinkedHashMap<>();

        positions.computeIfAbsent('0', k -> new JsonArray()).add(coord(0, 0, 0));
        mapping.put('0', NVBlocks.MASTER_RITUAL_STONE.block().get());

        JsonArray air = new JsonArray();
        air.add(coord(0, 3, 0));
        air.add(coord(0, -1, 0));
        positions.put('_', air);

        for (RitualComponent comp : components) {
            char ch = runeChar(comp.runeType());
            positions.computeIfAbsent(ch, k -> new JsonArray()).add(coord(
                    comp.offset().getX(), comp.offset().getY(), comp.offset().getZ()
            ));
            mapping.putIfAbsent(ch, runeBlock(comp.runeType()));
        }

        JsonObject root = new JsonObject();
        root.addProperty("type", SparseMultiblock.TYPE.toString());
        root.addProperty("symmetrical", true);

        JsonObject patternJson = new JsonObject();
        for (Map.Entry<Character, JsonArray> e : positions.entrySet()) {
            patternJson.add(String.valueOf(e.getKey()), e.getValue());
        }
        root.add("pattern", patternJson);

        JsonObject mappingJson = new JsonObject();
        for (Map.Entry<Character, Block> e : mapping.entrySet()) {
            JsonObject matcher = new JsonObject();
            matcher.addProperty("type", "modonomicon:block");
            matcher.addProperty("block", BuiltInRegistries.BLOCK.getKey(e.getValue()).toString());
            mappingJson.add(String.valueOf(e.getKey()), matcher);
        }
        JsonObject airMatcher = new JsonObject();
        airMatcher.addProperty("type", "modonomicon:block");
        airMatcher.addProperty("block", "minecraft:air");
        mappingJson.add("_", airMatcher);
        root.add("mapping", mappingJson);

        return LoaderRegistry.getMultiblockJsonLoader(SparseMultiblock.TYPE).fromJson(root, provider);
    }

    public static char runeChar(EnumRuneType rune) {
        return switch (rune) {
            case BLANK -> 'B';
            case WATER -> 'W';
            case FIRE -> 'F';
            case EARTH -> 'E';
            case AIR -> 'A';
            case DUSK -> 'D';
            case DAWN -> 'd';
        };
    }

    private static Block runeBlock(EnumRuneType rune) {
        return switch (rune) {
            case BLANK -> NVBlocks.BLANK_RITUAL_STONE.block().get();
            case WATER -> NVBlocks.WATER_RITUAL_STONE.block().get();
            case FIRE -> NVBlocks.FIRE_RITUAL_STONE.block().get();
            case EARTH -> NVBlocks.EARTH_RITUAL_STONE.block().get();
            case AIR -> NVBlocks.AIR_RITUAL_STONE.block().get();
            case DUSK -> NVBlocks.DUSK_RITUAL_STONE.block().get();
            case DAWN -> NVBlocks.DAWN_RITUAL_STONE.block().get();
        };
    }

    private static JsonArray coord(int x, int y, int z) {
        JsonArray a = new JsonArray();
        a.add(x);
        a.add(y);
        a.add(z);
        return a;
    }
}
