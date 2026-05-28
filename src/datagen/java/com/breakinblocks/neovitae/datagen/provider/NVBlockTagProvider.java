package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonVariant;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.datagen.BlockGroups;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;

public class NVBlockTagProvider extends BlockTagsProvider {
    public NVBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, NeoVitae.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(NVTags.Blocks.RUNES)
                .addAll(BlockGroups.RUNE_T1)
                .addAll(BlockGroups.RUNE_T2);

        this.tag(NVTags.Blocks.BLOODSTONES)
                .addAll(BlockGroups.BLOODSTONE);

        this.tag(NVTags.Blocks.T3_CAPSTONES)
                .add(NVBlocks.BLOOD_STAINED_GLASS.block().get());

        this.tag(NVTags.Blocks.T4_CAPSTONES)
                .addAll(BlockGroups.BLOODSTONE);

        this.tag(NVTags.Blocks.T5_CAPSTONES)
                .addAll(BlockGroups.HELLFORGED_BLOCK);

        this.tag(NVTags.Blocks.T6_CAPSTONES)
                .addAll(BlockGroups.CRYSTAL_CLUSTER);

        this.tag(NVTags.Blocks.PILLARS); // means all solid blocks are viable, has to be added otherwise the tag isnt generated

        this.tag(NVTags.Blocks.ANIMA_COMPARATOR)
                .add(NVBlocks.BLOOD_STAINED_GLASS.block().get());

        this.tag(NVTags.Blocks.PULSE_ON_CRAFTING)
                .add(Blocks.REDSTONE_LAMP, Blocks.NOTE_BLOCK);

        this.tag(NVTags.Blocks.TELEPOSE_BLACKLIST)
                .add(Blocks.BEDROCK,
                        Blocks.BARRIER,
                        Blocks.COMMAND_BLOCK,
                        Blocks.CHAIN_COMMAND_BLOCK,
                        Blocks.REPEATING_COMMAND_BLOCK,
                        Blocks.STRUCTURE_BLOCK,
                        Blocks.STRUCTURE_VOID,
                        Blocks.JIGSAW,
                        Blocks.END_PORTAL,
                        Blocks.END_PORTAL_FRAME,
                        Blocks.END_GATEWAY,
                        Blocks.NETHER_PORTAL);

        this.tag(NVTags.Blocks.STORAGE_BLOCKS_HELLFORGED)
                .addAll(BlockGroups.HELLFORGED_BLOCK);

        this.tag(BlockTags.BEACON_BASE_BLOCKS)
                .addAll(BlockGroups.HELLFORGED_BLOCK);

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(NVBlocks.ARA_VITAE.block().get(), NVBlocks.BLOOD_TANK.block().get())
                .add(NVBlocks.HELLFIRE_FORGE.block().get())
                .add(NVBlocks.ATHANOR_BLOCK.block().get())
                .add(NVBlocks.TABULA_VITAE.block().get())
                .add(NVBlocks.TELEPOSER.block().get())
                .add(NVBlocks.BLANK_RITUAL_STONE.block().get())
                .add(NVBlocks.AIR_RITUAL_STONE.block().get())
                .add(NVBlocks.WATER_RITUAL_STONE.block().get())
                .add(NVBlocks.FIRE_RITUAL_STONE.block().get())
                .add(NVBlocks.EARTH_RITUAL_STONE.block().get())
                .add(NVBlocks.DUSK_RITUAL_STONE.block().get())
                .add(NVBlocks.DAWN_RITUAL_STONE.block().get())
                .add(NVBlocks.MASTER_RITUAL_STONE.block().get())
                .add(NVBlocks.INVERTED_MASTER_RITUAL_STONE.block().get())
                .add(NVBlocks.IMPERFECT_RITUAL_STONE.block().get())
                .add(NVBlocks.VAS_MALEFICUM.block().get())
                .add(NVBlocks.CRYSTALLARIUM_MALEFICUM.block().get())
                .add(NVBlocks.SPIRA_INFERNALIS.block().get())
                // Demon crystals
                .add(NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get())
                .add(NVBlocks.SPIRITUS_RUINA_CRYSTAL.block().get())
                .add(NVBlocks.SPIRITUS_NIHILUM_CRYSTAL.block().get())
                .add(NVBlocks.SPIRITUS_VINDICTA_CRYSTAL.block().get())
                .add(NVBlocks.SPIRITUS_INVICTUS_CRYSTAL.block().get())
                // Routing nodes
                .add(NVBlocks.ROUTING_CONDUIT.block().get())
                .add(NVBlocks.INPUT_ROUTING_NODE.block().get())
                .add(NVBlocks.OUTPUT_ROUTING_NODE.block().get())
                .add(NVBlocks.MASTER_ROUTING_NODE.block().get())
                // Explosive charges
                .add(NVBlocks.SHAPED_CHARGE.block().get())
                .add(NVBlocks.AUG_SHAPED_CHARGE.block().get())
                .add(NVBlocks.SHAPED_CHARGE_DEEP.block().get())
                .add(NVBlocks.DEFORESTER_CHARGE.block().get())
                .add(NVBlocks.DEFORESTER_CHARGE_2.block().get())
                .add(NVBlocks.VEINMINE_CHARGE.block().get())
                .add(NVBlocks.VEINMINE_CHARGE_2.block().get())
                .add(NVBlocks.FUNGAL_CHARGE.block().get())
                .add(NVBlocks.FUNGAL_CHARGE_2.block().get());

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(NVBlocks.ARA_VITAE.block().get(), NVBlocks.BLOOD_TANK.block().get())
                .add(NVBlocks.HELLFIRE_FORGE.block().get())
                .add(NVBlocks.ATHANOR_BLOCK.block().get())
                .add(NVBlocks.TABULA_VITAE.block().get())
                .add(NVBlocks.TELEPOSER.block().get())
                .add(NVBlocks.BLANK_RITUAL_STONE.block().get())
                .add(NVBlocks.AIR_RITUAL_STONE.block().get())
                .add(NVBlocks.WATER_RITUAL_STONE.block().get())
                .add(NVBlocks.FIRE_RITUAL_STONE.block().get())
                .add(NVBlocks.EARTH_RITUAL_STONE.block().get())
                .add(NVBlocks.DUSK_RITUAL_STONE.block().get())
                .add(NVBlocks.DAWN_RITUAL_STONE.block().get())
                .add(NVBlocks.MASTER_RITUAL_STONE.block().get())
                .add(NVBlocks.INVERTED_MASTER_RITUAL_STONE.block().get())
                .add(NVBlocks.IMPERFECT_RITUAL_STONE.block().get())
                .add(NVBlocks.VAS_MALEFICUM.block().get())
                .add(NVBlocks.CRYSTALLARIUM_MALEFICUM.block().get())
                .add(NVBlocks.SPIRA_INFERNALIS.block().get())
                // Demon crystals
                .add(NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get())
                .add(NVBlocks.SPIRITUS_RUINA_CRYSTAL.block().get())
                .add(NVBlocks.SPIRITUS_NIHILUM_CRYSTAL.block().get())
                .add(NVBlocks.SPIRITUS_VINDICTA_CRYSTAL.block().get())
                .add(NVBlocks.SPIRITUS_INVICTUS_CRYSTAL.block().get())
                // Routing nodes
                .add(NVBlocks.ROUTING_CONDUIT.block().get())
                .add(NVBlocks.INPUT_ROUTING_NODE.block().get())
                .add(NVBlocks.OUTPUT_ROUTING_NODE.block().get())
                .add(NVBlocks.MASTER_ROUTING_NODE.block().get())
                // Explosive charges
                .add(NVBlocks.SHAPED_CHARGE.block().get())
                .add(NVBlocks.AUG_SHAPED_CHARGE.block().get())
                .add(NVBlocks.SHAPED_CHARGE_DEEP.block().get())
                .add(NVBlocks.DEFORESTER_CHARGE.block().get())
                .add(NVBlocks.DEFORESTER_CHARGE_2.block().get())
                .add(NVBlocks.VEINMINE_CHARGE.block().get())
                .add(NVBlocks.VEINMINE_CHARGE_2.block().get())
                .add(NVBlocks.FUNGAL_CHARGE.block().get())
                .add(NVBlocks.FUNGAL_CHARGE_2.block().get());

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
                .addAll(BlockGroups.HELLFORGED_BLOCK)
                .add(NVBlocks.RAW_DEMONITE_BLOCK.block().get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(NVBlocks.RAW_DEMONITE_BLOCK.block().get());

        this.tag(Tags.Blocks.NEEDS_NETHERITE_TOOL)
                .addAll(BlockGroups.RUNE_T2);

        // Incense Altar mineable
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(NVBlocks.INCENSE_ALTAR.block().get());
        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(NVBlocks.INCENSE_ALTAR.block().get());

        // Incense Path Blocks - higher level tags inherit from lower levels via tag hierarchy
        // Level 0 (innermost ring) - basic path blocks
        this.tag(NVTags.Blocks.INCENSE_PATH_LEVEL_0)
                .add(Blocks.DIRT_PATH)
                .addOptionalTag(BlockTags.STONE_BRICKS);

        // Each level adds the previous level's tag - blocks in level 0 work for all levels
        this.tag(NVTags.Blocks.INCENSE_PATH_LEVEL_1)
                .addTag(NVTags.Blocks.INCENSE_PATH_LEVEL_0);
        this.tag(NVTags.Blocks.INCENSE_PATH_LEVEL_2)
                .addTag(NVTags.Blocks.INCENSE_PATH_LEVEL_1);
        this.tag(NVTags.Blocks.INCENSE_PATH_LEVEL_3)
                .addTag(NVTags.Blocks.INCENSE_PATH_LEVEL_2);
        this.tag(NVTags.Blocks.INCENSE_PATH_LEVEL_4)
                .addTag(NVTags.Blocks.INCENSE_PATH_LEVEL_3);
        this.tag(NVTags.Blocks.INCENSE_PATH_LEVEL_5)
                .addTag(NVTags.Blocks.INCENSE_PATH_LEVEL_4);
        this.tag(NVTags.Blocks.INCENSE_PATH_LEVEL_6)
                .addTag(NVTags.Blocks.INCENSE_PATH_LEVEL_5);
        this.tag(NVTags.Blocks.INCENSE_PATH_LEVEL_7)
                .addTag(NVTags.Blocks.INCENSE_PATH_LEVEL_6);
        this.tag(NVTags.Blocks.INCENSE_PATH_LEVEL_8)
                .addTag(NVTags.Blocks.INCENSE_PATH_LEVEL_7);
        this.tag(NVTags.Blocks.INCENSE_PATH_LEVEL_9)
                .addTag(NVTags.Blocks.INCENSE_PATH_LEVEL_8);
        this.tag(NVTags.Blocks.INCENSE_PATH_LEVEL_10)
                .addTag(NVTags.Blocks.INCENSE_PATH_LEVEL_9);

        // Tranquility blocks - empty by default, uses runtime detection for vanilla blocks
        // Users can add custom blocks via datapacks
        this.tag(NVTags.Blocks.TRANQUILITY_PLANT);
        this.tag(NVTags.Blocks.TRANQUILITY_CROP);
        this.tag(NVTags.Blocks.TRANQUILITY_TREE);
        this.tag(NVTags.Blocks.TRANQUILITY_EARTHEN)
                .add(NVBlocks.SANDS_OF_VITAE.block().get());
        this.tag(NVTags.Blocks.TRANQUILITY_WATER);
        this.tag(NVTags.Blocks.TRANQUILITY_FIRE);
        this.tag(NVTags.Blocks.TRANQUILITY_LAVA);

        // Crystallum Fractura ritual - harvestable crystal blocks (mature clusters)
        // Other mods (AE2, GeoOres, etc.) can add their clusters via datapacks
        this.tag(NVTags.Blocks.GEODE_HARVESTABLE)
                .add(Blocks.AMETHYST_CLUSTER);

        this.tag(NVTags.Blocks.GEODE_ACCELERATABLE)
                .add(Blocks.BUDDING_AMETHYST)
                .addOptionalTag(TagKey.create(Registries.BLOCK,
                        Identifier.fromNamespaceAndPath("c", "budding_blocks")));

        // Mushroom blocks for fungal charges
        this.tag(NVTags.Blocks.MUSHROOM_STEM)
                .add(Blocks.CRIMSON_STEM, Blocks.WARPED_STEM,
                        Blocks.STRIPPED_CRIMSON_STEM, Blocks.STRIPPED_WARPED_STEM);
        this.tag(NVTags.Blocks.MUSHROOM_HYPHAE)
                .add(Blocks.CRIMSON_HYPHAE, Blocks.WARPED_HYPHAE,
                        Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE);

        // Inversion Pillar blocks
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(NVBlocks.INVERSION_PILLAR.block().get())
                .add(NVBlocks.INVERSION_PILLAR_CAP.block().get());
        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(NVBlocks.INVERSION_PILLAR.block().get())
                .add(NVBlocks.INVERSION_PILLAR_CAP.block().get());

        // Dungeon blocks
        addDungeonBlockTags();
    }

    private void addDungeonBlockTags() {
        // Add walls to WALLS tag so they connect properly
        var wallsTag = this.tag(BlockTags.WALLS);
        for (DungeonVariant variant : DungeonVariant.values()) {
            wallsTag.add(DungeonBlocks.DUNGEON_BRICK_WALL.get(variant).block().get());
            wallsTag.add(DungeonBlocks.DUNGEON_TILE_WALL.get(variant).block().get());
            wallsTag.add(DungeonBlocks.DUNGEON_POLISHED_WALL.get(variant).block().get());
            wallsTag.add(DungeonBlocks.DUNGEON_STONE_WALL.get(variant).block().get());
        }

        // Add stairs to STAIRS tag
        var stairsTag = this.tag(BlockTags.STAIRS);
        for (DungeonVariant variant : DungeonVariant.values()) {
            stairsTag.add(DungeonBlocks.DUNGEON_BRICK_STAIRS.get(variant).block().get());
            stairsTag.add(DungeonBlocks.DUNGEON_POLISHED_STAIRS.get(variant).block().get());
            stairsTag.add(DungeonBlocks.DUNGEON_STONE_STAIRS.get(variant).block().get());
        }

        // Add slabs to SLABS tag
        var slabsTag = this.tag(BlockTags.SLABS);
        for (DungeonVariant variant : DungeonVariant.values()) {
            slabsTag.add(DungeonBlocks.DUNGEON_BRICK_SLAB.get(variant).block().get());
            slabsTag.add(DungeonBlocks.DUNGEON_TILE_SLAB.get(variant).block().get());
            slabsTag.add(DungeonBlocks.DUNGEON_STONE_SLAB.get(variant).block().get());
            slabsTag.add(DungeonBlocks.DUNGEON_POLISHED_SLAB.get(variant).block().get());
        }

        // Add fence gates to FENCE_GATES tag
        var gatesTag = this.tag(BlockTags.FENCE_GATES);
        for (DungeonVariant variant : DungeonVariant.values()) {
            gatesTag.add(DungeonBlocks.DUNGEON_BRICK_GATE.get(variant).block().get());
            gatesTag.add(DungeonBlocks.DUNGEON_POLISHED_GATE.get(variant).block().get());
        }

        // Add all dungeon blocks to MINEABLE_WITH_PICKAXE
        var pickaxeTag = this.tag(BlockTags.MINEABLE_WITH_PICKAXE);
        pickaxeTag.add(DungeonBlocks.DUNGEON_ORE.block().get());
        pickaxeTag.add(DungeonBlocks.PRISMATIC_DEMONITE.block().get());
        pickaxeTag.add(DungeonBlocks.DUNGEON_BRICK_ASSORTED.block().get());

        // Path blocks
        pickaxeTag.add(DungeonBlocks.WOOD_BRICK_PATH.block().get());
        pickaxeTag.add(DungeonBlocks.WOOD_TILE_PATH.block().get());
        pickaxeTag.add(DungeonBlocks.STONE_BRICK_PATH.block().get());
        pickaxeTag.add(DungeonBlocks.STONE_TILE_PATH.block().get());
        pickaxeTag.add(DungeonBlocks.WORN_STONE_BRICK_PATH.block().get());
        pickaxeTag.add(DungeonBlocks.WORN_STONE_TILE_PATH.block().get());
        pickaxeTag.add(DungeonBlocks.OBSIDIAN_BRICK_PATH.block().get());
        pickaxeTag.add(DungeonBlocks.OBSIDIAN_TILE_PATH.block().get());

        // All variant blocks
        for (DungeonVariant variant : DungeonVariant.values()) {
            // Base blocks
            pickaxeTag.add(DungeonBlocks.DUNGEON_BRICK_1.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_BRICK_2.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_BRICK_3.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_STONE.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_EYE.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_POLISHED.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_TILE.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_SMALLBRICK.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_TILESPECIAL.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_METAL.get(variant).block().get());
            // Pillars
            pickaxeTag.add(DungeonBlocks.DUNGEON_PILLAR_CENTER.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_PILLAR_SPECIAL.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_PILLAR_CAP.get(variant).block().get());
            // Decorative
            pickaxeTag.add(DungeonBlocks.DUNGEON_BRICK_STAIRS.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_POLISHED_STAIRS.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_STONE_STAIRS.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_BRICK_WALL.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_TILE_WALL.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_POLISHED_WALL.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_STONE_WALL.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_BRICK_SLAB.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_TILE_SLAB.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_STONE_SLAB.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_POLISHED_SLAB.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_BRICK_GATE.get(variant).block().get());
            pickaxeTag.add(DungeonBlocks.DUNGEON_POLISHED_GATE.get(variant).block().get());
        }

        // Add stone tool requirement for dungeon blocks
        var stoneToolTag = this.tag(BlockTags.NEEDS_STONE_TOOL);
        stoneToolTag.add(DungeonBlocks.DUNGEON_ORE.block().get());
        stoneToolTag.add(DungeonBlocks.PRISMATIC_DEMONITE.block().get());
        stoneToolTag.add(DungeonBlocks.DUNGEON_BRICK_ASSORTED.block().get());
        for (DungeonVariant variant : DungeonVariant.values()) {
            stoneToolTag.add(DungeonBlocks.DUNGEON_BRICK_1.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_BRICK_2.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_BRICK_3.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_STONE.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_EYE.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_POLISHED.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_TILE.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_SMALLBRICK.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_TILESPECIAL.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_PILLAR_CENTER.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_PILLAR_SPECIAL.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_PILLAR_CAP.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_BRICK_STAIRS.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_POLISHED_STAIRS.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_STONE_STAIRS.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_BRICK_WALL.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_TILE_WALL.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_POLISHED_WALL.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_STONE_WALL.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_BRICK_SLAB.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_TILE_SLAB.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_STONE_SLAB.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_POLISHED_SLAB.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_BRICK_GATE.get(variant).block().get());
            stoneToolTag.add(DungeonBlocks.DUNGEON_POLISHED_GATE.get(variant).block().get());
        }

        // Spike trap - pickaxe mineable, requires iron tier, drops self (loot table in MineBlock)
        pickaxeTag.add(DungeonBlocks.SPIKE_TRAP.block().get());

        // Iron tool for dungeon metal blocks + spike trap
        var ironToolTag = this.tag(BlockTags.NEEDS_IRON_TOOL);
        for (DungeonVariant variant : DungeonVariant.values()) {
            ironToolTag.add(DungeonBlocks.DUNGEON_METAL.get(variant).block().get());
        }
        ironToolTag.add(DungeonBlocks.SPIKE_TRAP.block().get());

        // Add path blocks to incense altar path tags
        this.tag(NVTags.Blocks.INCENSE_PATH_LEVEL_2)
                .add(DungeonBlocks.WOOD_BRICK_PATH.block().get())
                .add(DungeonBlocks.WOOD_TILE_PATH.block().get());
        this.tag(NVTags.Blocks.INCENSE_PATH_LEVEL_4)
                .add(DungeonBlocks.STONE_BRICK_PATH.block().get())
                .add(DungeonBlocks.STONE_TILE_PATH.block().get());
        this.tag(NVTags.Blocks.INCENSE_PATH_LEVEL_6)
                .add(DungeonBlocks.WORN_STONE_BRICK_PATH.block().get())
                .add(DungeonBlocks.WORN_STONE_TILE_PATH.block().get());
        this.tag(NVTags.Blocks.INCENSE_PATH_LEVEL_8)
                .add(DungeonBlocks.OBSIDIAN_BRICK_PATH.block().get())
                .add(DungeonBlocks.OBSIDIAN_TILE_PATH.block().get());
    }
}
