package com.breakinblocks.neovitae.common.structure;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.registry.AltarComponent;
import com.breakinblocks.neovitae.common.registry.AltarTier;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.multiblock.DenseMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs.TagOrElementLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds a Modonomicon Multiblock from an AltarTier's component list so the
 * in-book preview always reflects the loaded datapack layout.
 *
 * The altar block at world origin (0, 0, 0) becomes the multiblock center
 * marker ('0'). Other materials are assigned characters dynamically, with
 * upgrade-rune cells distinguished from blank-rune cells so the preview
 * cycles real rune blocks where the player is meant to install runes.
 */
public final class AltarMultiblockBuilder {

    private static final String CENTER_CHAR = "0";
    private static final char AIR_CHAR = '_';

    private static final String CHAR_POOL =
            "RPGSHCMNKTUVWXYZABDEFIJLOQabcdefghijklmnopqrstuvwxyz123456789!@#$%^&*?";

    private AltarMultiblockBuilder() {
    }

    public static Multiblock build(AltarTier tier, HolderLookup.Provider provider) {
        List<AltarComponent> components = tier.components();
        if (components.isEmpty()) {
            throw new IllegalArgumentException("Altar tier has no components");
        }

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (AltarComponent c : components) {
            if (c.optional()) continue;
            BlockPos p = c.pos();
            if (p.getX() < minX) minX = p.getX();
            if (p.getY() < minY) minY = p.getY();
            if (p.getZ() < minZ) minZ = p.getZ();
            if (p.getX() > maxX) maxX = p.getX();
            if (p.getY() > maxY) maxY = p.getY();
            if (p.getZ() > maxZ) maxZ = p.getZ();
        }
        if (minX > 0) minX = 0;
        if (minY > 0) minY = 0;
        if (minZ > 0) minZ = 0;
        if (maxX < 0) maxX = 0;
        if (maxY < 0) maxY = 0;
        if (maxZ < 0) maxZ = 0;

        int xSize = maxX - minX + 1;
        int ySize = maxY - minY + 1;
        int zSize = maxZ - minZ + 1;

        char[][][] grid = new char[ySize][xSize][zSize];
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                for (int z = 0; z < zSize; z++) {
                    grid[y][x][z] = AIR_CHAR;
                }
            }
        }

        Map<MaterialKey, Character> charMap = new HashMap<>();
        Map<Character, MaterialKey> reverseMap = new HashMap<>();
        int nextCharIdx = 0;

        boolean altarPlaced = false;
        for (AltarComponent c : components) {
            if (c.optional()) continue;
            BlockPos p = c.pos();
            int gx = p.getX() - minX;
            int gy = maxY - p.getY();
            int gz = p.getZ() - minZ;

            boolean isAltar = p.getX() == 0 && p.getY() == 0 && p.getZ() == 0;
            char ch;
            if (isAltar) {
                ch = '0';
                altarPlaced = true;
                MaterialKey key = new MaterialKey(c.material(), c.isUpgrade());
                reverseMap.putIfAbsent('0', key);
            } else {
                MaterialKey key = new MaterialKey(c.material(), c.isUpgrade());
                Character existing = charMap.get(key);
                if (existing != null) {
                    ch = existing;
                } else {
                    if (nextCharIdx >= CHAR_POOL.length()) {
                        throw new IllegalStateException("Altar tier has more unique material kinds than the char pool supports");
                    }
                    ch = CHAR_POOL.charAt(nextCharIdx++);
                    charMap.put(key, ch);
                    reverseMap.put(ch, key);
                }
            }
            grid[gy][gx][gz] = ch;
        }

        if (!altarPlaced) {
            throw new IllegalArgumentException("Altar tier has no component at (0, 0, 0); cannot derive multiblock center");
        }

        String[][] pattern = new String[ySize][xSize];
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                pattern[y][x] = new String(grid[y][x]);
            }
        }

        JsonObject root = new JsonObject();
        root.addProperty("type", DenseMultiblock.ID.toString());
        root.addProperty("symmetrical", true);

        JsonArray patternJson = new JsonArray();
        for (String[] row : pattern) {
            JsonArray xRow = new JsonArray();
            for (String s : row) xRow.add(s);
            patternJson.add(xRow);
        }
        root.add("pattern", patternJson);

        JsonObject mapping = new JsonObject();
        for (Map.Entry<Character, MaterialKey> e : reverseMap.entrySet()) {
            mapping.add(String.valueOf(e.getKey()), matcherFor(e.getValue()));
        }
        root.add("mapping", mapping);

        Multiblock multiblock = Multiblock.fromJson(root, provider);
        return multiblock.offset(0, 1, 0);
    }

    private static JsonObject matcherFor(MaterialKey key) {
        JsonObject matcher = new JsonObject();
        TagOrElementLocation material = key.material();
        if (material.tag()) {
            matcher.addProperty("type", "modonomicon:tag");
            matcher.addProperty("tag", "#" + material.id());
            Identifier runesTag = NVTags.Blocks.RUNES.location();
            if (material.id().equals(runesTag) && !key.upgrade()) {
                Identifier blankRune = blockId(NVBlocks.RUNE_BLANK.block().getId());
                matcher.addProperty("display", blankRune.toString());
            }
        } else {
            matcher.addProperty("type", "modonomicon:block");
            matcher.addProperty("block", material.id().toString());
        }
        return matcher;
    }

    private static Identifier blockId(Identifier id) {
        return id != null ? id : NeoVitae.rl("ara_vitae");
    }

    private record MaterialKey(TagOrElementLocation material, boolean upgrade) {
    }
}
