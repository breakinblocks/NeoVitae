package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonVariant;
import com.breakinblocks.neovitae.common.tag.BMTags;
import com.breakinblocks.neovitae.datagen.BlockGroups;

import java.util.concurrent.CompletableFuture;

public class BMBlockTagProvider extends BlockTagsProvider {
    public BMBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, NeoVitae.MODID, null);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BMTags.Blocks.RUNES)
                .addAll(BlockGroups.RUNE_T1)
                .addAll(BlockGroups.RUNE_T2);

        this.tag(BMTags.Blocks.T3_CAPSTONES)
                .add(Blocks.GLOWSTONE, Blocks.SHROOMLIGHT, Blocks.SEA_LANTERN)
                .add(Blocks.OCHRE_FROGLIGHT, Blocks.PEARLESCENT_FROGLIGHT, Blocks.VERDANT_FROGLIGHT);

        this.tag(BMTags.Blocks.T4_CAPSTONES)
                .addAll(BlockGroups.BLOODSTONE);

        this.tag(BMTags.Blocks.T5_CAPSTONES)
                .addAll(BlockGroups.HELLFORGED_BLOCK);

        this.tag(BMTags.Blocks.T6_CAPSTONES)
                .addAll(BlockGroups.CRYSTAL_CLUSTER);

        this.tag(BMTags.Blocks.PILLARS); // means all solid blocks are viable, has to be added otherwise the tag isnt generated

        this.tag(BMTags.Blocks.SOUL_NETWORK_COMPARATOR)
                .addAll(BlockGroups.BLOODSTONE);

        this.tag(BMTags.Blocks.PULSE_ON_CRAFTING)
                .add(Blocks.REDSTONE_LAMP, Blocks.NOTE_BLOCK);

        this.tag(BMTags.Blocks.STORAGE_BLOCKS_HELLFORGED)
                .addAll(BlockGroups.HELLFORGED_BLOCK);

        this.tag(BlockTags.BEACON_BASE_BLOCKS)
                .addAll(BlockGroups.HELLFORGED_BLOCK);

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(BMBlocks.BLOOD_ALTAR.block().getKey(), BMBlocks.BLOOD_TANK.block().getKey());

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(BMBlocks.BLOOD_ALTAR.block().getKey(), BMBlocks.BLOOD_TANK.block().getKey());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addAll(BlockGroups.BLOODSTONE)
                .addAll(BlockGroups.HELLFORGED_BLOCK)
                .addAll(BlockGroups.CRYSTAL_CLUSTER)
                .addAll(BlockGroups.RUNE_T1)
                .addAll(BlockGroups.RUNE_T2);

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .addAll(BlockGroups.BLOODSTONE)
                .addAll(BlockGroups.CRYSTAL_CLUSTER)
                .addAll(BlockGroups.RUNE_T1);

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .addAll(BlockGroups.HELLFORGED_BLOCK);

        this.tag(Tags.Blocks.NEEDS_NETHERITE_TOOL)
                .addAll(BlockGroups.RUNE_T2);

        // Incense Altar mineable
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(BMBlocks.INCENSE_ALTAR.block().getKey());
        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(BMBlocks.INCENSE_ALTAR.block().getKey());

        // Incense Path Blocks - higher level tags inherit from lower levels via tag hierarchy
        // Level 0 (innermost ring) - basic path blocks
        this.tag(BMTags.Blocks.INCENSE_PATH_LEVEL_0)
                .add(Blocks.DIRT_PATH)
                .addOptionalTag(BlockTags.STONE_BRICKS);

        // Each level adds the previous level's tag - blocks in level 0 work for all levels
        this.tag(BMTags.Blocks.INCENSE_PATH_LEVEL_1)
                .addTag(BMTags.Blocks.INCENSE_PATH_LEVEL_0);
        this.tag(BMTags.Blocks.INCENSE_PATH_LEVEL_2)
                .addTag(BMTags.Blocks.INCENSE_PATH_LEVEL_1);
        this.tag(BMTags.Blocks.INCENSE_PATH_LEVEL_3)
                .addTag(BMTags.Blocks.INCENSE_PATH_LEVEL_2);
        this.tag(BMTags.Blocks.INCENSE_PATH_LEVEL_4)
                .addTag(BMTags.Blocks.INCENSE_PATH_LEVEL_3);
        this.tag(BMTags.Blocks.INCENSE_PATH_LEVEL_5)
                .addTag(BMTags.Blocks.INCENSE_PATH_LEVEL_4);
        this.tag(BMTags.Blocks.INCENSE_PATH_LEVEL_6)
                .addTag(BMTags.Blocks.INCENSE_PATH_LEVEL_5);
        this.tag(BMTags.Blocks.INCENSE_PATH_LEVEL_7)
                .addTag(BMTags.Blocks.INCENSE_PATH_LEVEL_6);
        this.tag(BMTags.Blocks.INCENSE_PATH_LEVEL_8)
                .addTag(BMTags.Blocks.INCENSE_PATH_LEVEL_7);
        this.tag(BMTags.Blocks.INCENSE_PATH_LEVEL_9)
                .addTag(BMTags.Blocks.INCENSE_PATH_LEVEL_8);
        this.tag(BMTags.Blocks.INCENSE_PATH_LEVEL_10)
                .addTag(BMTags.Blocks.INCENSE_PATH_LEVEL_9);

        // Tranquility blocks - empty by default, uses runtime detection for vanilla blocks
        // Users can add custom blocks via datapacks
        this.tag(BMTags.Blocks.TRANQUILITY_PLANT);
        this.tag(BMTags.Blocks.TRANQUILITY_CROP);
        this.tag(BMTags.Blocks.TRANQUILITY_TREE);
        this.tag(BMTags.Blocks.TRANQUILITY_EARTHEN);
        this.tag(BMTags.Blocks.TRANQUILITY_WATER);
        this.tag(BMTags.Blocks.TRANQUILITY_FIRE);
        this.tag(BMTags.Blocks.TRANQUILITY_LAVA);

        // Mushroom blocks for fungal charges
        this.tag(BMTags.Blocks.MUSHROOM_STEM)
                .add(Blocks.CRIMSON_STEM, Blocks.WARPED_STEM,
                        Blocks.STRIPPED_CRIMSON_STEM, Blocks.STRIPPED_WARPED_STEM);
        this.tag(BMTags.Blocks.MUSHROOM_HYPHAE)
                .add(Blocks.CRIMSON_HYPHAE, Blocks.WARPED_HYPHAE,
                        Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE);

        // Inversion Pillar blocks
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(BMBlocks.INVERSION_PILLAR.block().getKey())
                .add(BMBlocks.INVERSION_PILLAR_CAP.block().getKey());
        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(BMBlocks.INVERSION_PILLAR.block().getKey())
                .add(BMBlocks.INVERSION_PILLAR_CAP.block().getKey());

        // Dungeon blocks
        addDungeonBlockTags();
    }

    private void addDungeonBlockTags() {
        // Add walls to WALLS tag so they connect properly
        var wallsTag = this.tag(BlockTags.WALLS);
        for (DungeonVariant variant : DungeonVariant.values()) {
            wallsTag.add(DungeonBlocks.DUNGEON_BRICK_WALL.get(variant).block().getKey());
            wallsTag.add(DungeonBlocks.DUNGEON_TILE_WALL.get(variant).block().getKey());
            wallsTag.add(DungeonBlocks.DUNGEON_POLISHED_WALL.get(variant).block().getKey());
            wallsTag.add(DungeonBlocks.DUNGEON_STONE_WALL.get(variant).block().getKey());
        }

        // Add stairs to STAIRS tag
        var stairsTag = this.tag(BlockTags.STAIRS);
        for (DungeonVariant variant : DungeonVariant.values()) {
            stairsTag.add(DungeonBlocks.DUNGEON_BRICK_STAIRS.get(variant).block().getKey());
            stairsTag.add(DungeonBlocks.DUNGEON_POLISHED_STAIRS.get(variant).block().getKey());
            stairsTag.add(DungeonBlocks.DUNGEON_STONE_STAIRS.get(variant).block().getKey());
        }

        // Add slabs to SLABS tag
        var slabsTag = this.tag(BlockTags.SLABS);
        for (DungeonVariant variant : DungeonVariant.values()) {
            slabsTag.add(DungeonBlocks.DUNGEON_BRICK_SLAB.get(variant).block().getKey());
            slabsTag.add(DungeonBlocks.DUNGEON_TILE_SLAB.get(variant).block().getKey());
            slabsTag.add(DungeonBlocks.DUNGEON_STONE_SLAB.get(variant).block().getKey());
            slabsTag.add(DungeonBlocks.DUNGEON_POLISHED_SLAB.get(variant).block().getKey());
        }

        // Add fence gates to FENCE_GATES tag
        var gatesTag = this.tag(BlockTags.FENCE_GATES);
        for (DungeonVariant variant : DungeonVariant.values()) {
            gatesTag.add(DungeonBlocks.DUNGEON_BRICK_GATE.get(variant).block().getKey());
            gatesTag.add(DungeonBlocks.DUNGEON_POLISHED_GATE.get(variant).block().getKey());
        }

        // Add all dungeon blocks to MINEABLE_WITH_PICKAXE
        var pickaxeTag = this.tag(BlockTags.MINEABLE_WITH_PICKAXE);
        pickaxeTag.add(DungeonBlocks.DUNGEON_ORE.block().getKey());
        pickaxeTag.add(DungeonBlocks.DUNGEON_BRICK_ASSORTED.block().getKey());

        // Path blocks
        pickaxeTag.add(DungeonBlocks.WOOD_BRICK_PATH.block().getKey());
        pickaxeTag.add(DungeonBlocks.WOOD_TILE_PATH.block().getKey());
        pickaxeTag.add(DungeonBlocks.STONE_BRICK_PATH.block().getKey());
        pickaxeTag.add(DungeonBlocks.STONE_TILE_PATH.block().getKey());
        pickaxeTag.add(DungeonBlocks.WORN_STONE_BRICK_PATH.block().getKey());
        pickaxeTag.add(DungeonBlocks.WORN_STONE_TILE_PATH.block().getKey());
        pickaxeTag.add(DungeonBlocks.OBSIDIAN_BRICK_PATH.block().getKey());
        pickaxeTag.add(DungeonBlocks.OBSIDIAN_TILE_PATH.block().getKey());

        // All variant blocks
        for (DungeonVariant variant : DungeonVariant.values()) {
            // Base blocks
            pickaxeTag.add(DungeonBlocks.DUNGEON_BRICK_1.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_BRICK_2.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_BRICK_3.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_STONE.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_EYE.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_POLISHED.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_TILE.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_SMALLBRICK.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_TILESPECIAL.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_METAL.get(variant).block().getKey());
            // Pillars
            pickaxeTag.add(DungeonBlocks.DUNGEON_PILLAR_CENTER.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_PILLAR_SPECIAL.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_PILLAR_CAP.get(variant).block().getKey());
            // Decorative
            pickaxeTag.add(DungeonBlocks.DUNGEON_BRICK_STAIRS.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_POLISHED_STAIRS.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_STONE_STAIRS.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_BRICK_WALL.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_TILE_WALL.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_POLISHED_WALL.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_STONE_WALL.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_BRICK_SLAB.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_TILE_SLAB.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_STONE_SLAB.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_POLISHED_SLAB.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_BRICK_GATE.get(variant).block().getKey());
            pickaxeTag.add(DungeonBlocks.DUNGEON_POLISHED_GATE.get(variant).block().getKey());
        }

        // Add stone tool requirement for dungeon blocks
        var stoneToolTag = this.tag(BlockTags.NEEDS_STONE_TOOL);
        stoneToolTag.add(DungeonBlocks.DUNGEON_ORE.block().getKey());
        stoneToolTag.add(DungeonBlocks.DUNGEON_BRICK_ASSORTED.block().getKey());
        for (DungeonVariant variant : DungeonVariant.values()) {
            stoneToolTag.add(DungeonBlocks.DUNGEON_BRICK_1.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_BRICK_2.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_BRICK_3.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_STONE.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_EYE.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_POLISHED.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_TILE.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_SMALLBRICK.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_TILESPECIAL.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_PILLAR_CENTER.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_PILLAR_SPECIAL.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_PILLAR_CAP.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_BRICK_STAIRS.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_POLISHED_STAIRS.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_STONE_STAIRS.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_BRICK_WALL.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_TILE_WALL.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_POLISHED_WALL.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_STONE_WALL.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_BRICK_SLAB.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_TILE_SLAB.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_STONE_SLAB.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_POLISHED_SLAB.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_BRICK_GATE.get(variant).block().getKey());
            stoneToolTag.add(DungeonBlocks.DUNGEON_POLISHED_GATE.get(variant).block().getKey());
        }

        // Iron tool for dungeon metal blocks
        var ironToolTag = this.tag(BlockTags.NEEDS_IRON_TOOL);
        for (DungeonVariant variant : DungeonVariant.values()) {
            ironToolTag.add(DungeonBlocks.DUNGEON_METAL.get(variant).block().getKey());
        }

        // Add path blocks to incense altar path tags
        this.tag(BMTags.Blocks.INCENSE_PATH_LEVEL_2)
                .add(DungeonBlocks.WOOD_BRICK_PATH.block().getKey())
                .add(DungeonBlocks.WOOD_TILE_PATH.block().getKey());
        this.tag(BMTags.Blocks.INCENSE_PATH_LEVEL_4)
                .add(DungeonBlocks.STONE_BRICK_PATH.block().getKey())
                .add(DungeonBlocks.STONE_TILE_PATH.block().getKey());
        this.tag(BMTags.Blocks.INCENSE_PATH_LEVEL_6)
                .add(DungeonBlocks.WORN_STONE_BRICK_PATH.block().getKey())
                .add(DungeonBlocks.WORN_STONE_TILE_PATH.block().getKey());
        this.tag(BMTags.Blocks.INCENSE_PATH_LEVEL_8)
                .add(DungeonBlocks.OBSIDIAN_BRICK_PATH.block().getKey())
                .add(DungeonBlocks.OBSIDIAN_TILE_PATH.block().getKey());
    }
}
