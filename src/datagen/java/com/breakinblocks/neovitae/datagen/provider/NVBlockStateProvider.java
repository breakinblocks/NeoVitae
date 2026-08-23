package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.AthanorBlock;
import com.breakinblocks.neovitae.common.block.BlockDungeonSeal;
import com.breakinblocks.neovitae.common.block.BlockDungeonSealInaccessible;
import com.breakinblocks.neovitae.common.block.BlockInversionPillar;
import com.breakinblocks.neovitae.common.block.BlockInversionPillarEnd;
import com.breakinblocks.neovitae.common.block.BlockShapedExplosive;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.SpiritCacheBlock;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonVariant;
import com.breakinblocks.neovitae.common.block.type.PillarCapType;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.util.helper.BlockWithItemHolder;

public class NVBlockStateProvider extends BlockStateProvider {
    public NVBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, NeoVitae.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        NVBlocks.BASIC_BLOCKS.getEntries().forEach(block -> {
            String path = block.getId().getPath();
            // Skip tau blocks (special crop-like rendering)
            if (path.contains("tau")) {
                return;
            }
            // Demon crystals need cutout render type for transparency
            if (path.contains("spiritus_crystal")) {
                ModelFile model = models().cubeAll(path, bm("block/" + path))
                    .renderType("cutout");
                simpleBlockWithItem(block.get(), model);
                return;
            }
            if (path.startsWith("rune_") && !path.equals("rune_blank")) {
                simpleBlockWithItem(block.get(), glowingRune(path));
                return;
            }
            simpleBlockWithItem(block.get(), cubeAll(block.get()));
        });

        simpleBlockWithItem(NVBlocks.BLANK_RITUAL_STONE.block().get(), cubeAll(NVBlocks.BLANK_RITUAL_STONE.block().get()));
        simpleBlockWithItem(NVBlocks.AIR_RITUAL_STONE.block().get(), cubeAll(NVBlocks.AIR_RITUAL_STONE.block().get()));
        simpleBlockWithItem(NVBlocks.WATER_RITUAL_STONE.block().get(), cubeAll(NVBlocks.WATER_RITUAL_STONE.block().get()));
        simpleBlockWithItem(NVBlocks.FIRE_RITUAL_STONE.block().get(), cubeAll(NVBlocks.FIRE_RITUAL_STONE.block().get()));
        simpleBlockWithItem(NVBlocks.EARTH_RITUAL_STONE.block().get(), cubeAll(NVBlocks.EARTH_RITUAL_STONE.block().get()));
        simpleBlockWithItem(NVBlocks.TENEBRAE_RITUAL_STONE.block().get(), cubeAll(NVBlocks.TENEBRAE_RITUAL_STONE.block().get()));
        simpleBlockWithItem(NVBlocks.DEUS_RITUAL_STONE.block().get(), cubeAll(NVBlocks.DEUS_RITUAL_STONE.block().get()));
        simpleBlockWithItem(NVBlocks.MASTER_RITUAL_STONE.block().get(), cubeAll(NVBlocks.MASTER_RITUAL_STONE.block().get()));
        // Inverted MRS uses same texture as regular MRS
        simpleBlockWithItem(NVBlocks.INVERTED_MASTER_RITUAL_STONE.block().get(),
            models().cubeAll("inverted_master_ritual_stone", bm("block/master_ritual_stone")));
        // Imperfect ritual stone uses the blank ritual stone texture
        simpleBlockWithItem(NVBlocks.IMPERFECT_RITUAL_STONE.block().get(),
            models().cubeAll("imperfect_ritual_stone", bm("block/ritual_stone")));

        // Incense altar, routing nodes - use manual OBJ models and blockstates
        // (see src/main/resources/assets/neovitae/blockstates/ and models/)

        VariantBlockStateBuilder builder = getVariantBuilder(NVBlocks.ATHANOR_BLOCK.block().get());
        ModelFile athanorModel = models().getExistingFile(bm("block/athanor"));
        simpleBlockItem(NVBlocks.ATHANOR_BLOCK.block().get(), athanorModel);
        for (SpiritusType type : SpiritusType.values()) {
            for (Direction facing : Direction.Plane.HORIZONTAL) {
                int yRot = (int) facing.getOpposite().toYRot();
                builder.partialState().with(AthanorBlock.LIT, false).with(AthanorBlock.FACING, facing).with(AthanorBlock.TYPE, type).modelForState().modelFile(athanorModel).rotationY(yRot).addModel();
                builder.partialState().with(AthanorBlock.LIT, true).with(AthanorBlock.FACING, facing).with(AthanorBlock.TYPE, type).modelForState().modelFile(athanorModel).rotationY(yRot).addModel();
            }
        }

        // Dungeon blocks
        registerDungeonBlocks();

        // Explosive charges - directional blocks attached to surfaces
        registerExplosiveCharges();

        // Spirit Cache container
        registerSpiritCache();

        // Mimic block - renders the mimicked block dynamically, this is the fallback texture
        ModelFile mimicModel = models().cubeAll("mimic", bm("block/dungeon/dungeon_brick1"));
        simpleBlockWithItem(NVBlocks.MIMIC.block().get(), mimicModel);

        // Ethereal Mimic block - same as mimic but non-solid (no collision)
        ModelFile etherealMimicModel = models().cubeAll("ethereal_mimic", bm("block/dungeon/dungeon_brick1"));
        simpleBlockWithItem(NVBlocks.ETHEREAL_MIMIC.block().get(), etherealMimicModel);

        // Dungeon Controller - manages procedural dungeon generation (uses dungeon stone texture)
        ModelFile dungeonControllerModel = models().cubeAll("dungeon_controller", bm("block/dungeon/dungeon_stone"));
        simpleBlockWithItem(NVBlocks.DUNGEON_CONTROLLER.block().get(), dungeonControllerModel);

        // Dungeon Seal - door seal blocks for dungeon progression (uses dungeon eye texture)
        ModelFile dungeonSealModel = models().cubeAll("dungeon_seal", bm("block/dungeon/dungeon_eye"));
        ModelFile dungeonSealSpecialModel = models().cubeAll("dungeon_seal_special", bm("block/dungeon/dungeon_eye_c"));
        getVariantBuilder(NVBlocks.DUNGEON_SEAL.block().get())
                .partialState().with(BlockDungeonSeal.SPECIAL, false)
                .modelForState().modelFile(dungeonSealModel).addModel()
                .partialState().with(BlockDungeonSeal.SPECIAL, true)
                .modelForState().modelFile(dungeonSealSpecialModel).addModel();
        simpleBlockItem(NVBlocks.DUNGEON_SEAL.block().get(), dungeonSealModel);

        ModelFile dungeonSealInertModel = models().cubeAll("dungeon_seal_inaccessible", bm("block/dungeon/dungeon_eye_inert"));
        ModelFile dungeonSealInertSpecialModel = models().cubeAll("dungeon_seal_inaccessible_special", bm("block/dungeon/dungeon_eye_inert_c"));
        getVariantBuilder(NVBlocks.DUNGEON_SEAL_INACCESSIBLE.block().get())
                .partialState().with(BlockDungeonSealInaccessible.SPECIAL, false)
                .modelForState().modelFile(dungeonSealInertModel).addModel()
                .partialState().with(BlockDungeonSealInaccessible.SPECIAL, true)
                .modelForState().modelFile(dungeonSealInertSpecialModel).addModel();
        simpleBlockItem(NVBlocks.DUNGEON_SEAL_INACCESSIBLE.block().get(), dungeonSealInertModel);

        // Inversion Pillar - dungeon teleporter (uses custom pillar_mid parent)
        ModelFile inversionPillarModel = models().withExistingParent("inversion_pillar", bm("block/pillar_mid"))
                .texture("texture", bm("block/pillar_mid"));
        getVariantBuilder(NVBlocks.INVERSION_PILLAR.block().get())
                .partialState().with(BlockInversionPillar.RIFT_RETURN, false)
                .modelForState().modelFile(inversionPillarModel).addModel()
                .partialState().with(BlockInversionPillar.RIFT_RETURN, true)
                .modelForState().modelFile(inversionPillarModel).addModel();
        simpleBlockItem(NVBlocks.INVERSION_PILLAR.block().get(), inversionPillarModel);

        // Sands of Vitae
        simpleBlockWithItem(NVBlocks.SANDS_OF_VITAE.block().get(), cubeAll(NVBlocks.SANDS_OF_VITAE.block().get()));

        // Blood Stained Glass
        simpleBlockWithItem(NVBlocks.BLOOD_STAINED_GLASS.block().get(),
                models().cubeAll("blood_stained_glass", bm("block/blood_stained_glass")).renderType("translucent"));

        // Blood Stained Glass Pane
        paneBlockWithRenderType((IronBarsBlock) NVBlocks.BLOOD_STAINED_GLASS_PANE.block().get(),
                bm("block/blood_stained_glass"), bm("block/blood_stained_glass_pane_top"), "translucent");

        // Inversion Pillar Cap - uses block state for top/bottom orientation (uses custom pillar_top/pillar_bottom parents)
        ModelFile inversionPillarCapBottom = models().withExistingParent("inversion_pillar_bottom", bm("block/pillar_bottom"))
                .texture("texture", bm("block/pillar_base"));
        ModelFile inversionPillarCapTop = models().withExistingParent("inversion_pillar_top", bm("block/pillar_top"))
                .texture("texture", bm("block/pillar_base"));
        getVariantBuilder(NVBlocks.INVERSION_PILLAR_CAP.block().get())
                .partialState().with(BlockInversionPillarEnd.TYPE, PillarCapType.BOTTOM)
                .with(BlockInversionPillarEnd.RIFT_RETURN, false)
                .modelForState().modelFile(inversionPillarCapBottom).addModel()
                .partialState().with(BlockInversionPillarEnd.TYPE, PillarCapType.TOP)
                .with(BlockInversionPillarEnd.RIFT_RETURN, false)
                .modelForState().modelFile(inversionPillarCapTop).addModel()
                .partialState().with(BlockInversionPillarEnd.TYPE, PillarCapType.BOTTOM)
                .with(BlockInversionPillarEnd.RIFT_RETURN, true)
                .modelForState().modelFile(inversionPillarCapBottom).addModel()
                .partialState().with(BlockInversionPillarEnd.TYPE, PillarCapType.TOP)
                .with(BlockInversionPillarEnd.RIFT_RETURN, true)
                .modelForState().modelFile(inversionPillarCapTop).addModel();
        simpleBlockItem(NVBlocks.INVERSION_PILLAR_CAP.block().get(), inversionPillarCapBottom);
    }

    private void registerExplosiveCharges() {
        // Basic shaped charge - uses sub/shaped_charge parent
        shapedChargeModel(NVBlocks.SHAPED_CHARGE.block().get(), "shaped_charge",
                "neovitae:block/dungeon/dungeon_tile", "neovitae:block/dungeon/dungeon_stone",
                "neovitae:block/blankrune", "neovitae:block/largebloodstonebrick",
                "neovitae:block/defaultcrystal");

        // Augmented shaped charge - uses sub/augment_shaped_charge parent
        augmentedChargeModel(NVBlocks.AUG_SHAPED_CHARGE.block().get(), "aug_shaped_charge",
                "neovitae:block/dungeon/dungeon_tile", "neovitae:block/defaultcrystal",
                "neovitae:block/dungeon/dungeon_stone", "neovitae:block/blankrune",
                "neovitae:block/largebloodstonebrick", "neovitae:block/copper_trim");

        // Deep shaped charge - uses sub/augment_shaped_charge parent with iron core
        augmentedChargeModel(NVBlocks.SHAPED_CHARGE_DEEP.block().get(), "shaped_charge_deep",
                "neovitae:block/dungeon/dungeon_tile", "minecraft:block/iron_block",
                "neovitae:block/dungeon/dungeon_stone", "neovitae:block/blankrune",
                "neovitae:block/largebloodstonebrick", "neovitae:block/copper_trim");

        // Deforester charges - wood themed
        shapedChargeModel(NVBlocks.DEFORESTER_CHARGE.block().get(), "deforester_charge",
                "minecraft:block/oak_log_top", "minecraft:block/oak_log_top",
                "neovitae:block/blankrune", "minecraft:block/oak_planks",
                "neovitae:block/defaultcrystal");
        augmentedChargeModel(NVBlocks.DEFORESTER_CHARGE_2.block().get(), "deforester_charge_2",
                "minecraft:block/oak_log_top", "neovitae:block/defaultcrystal",
                "minecraft:block/oak_log_top", "neovitae:block/blankrune",
                "minecraft:block/oak_planks", "neovitae:block/copper_trim");

        // Veinmine charges - sand/stone themed
        shapedChargeModel(NVBlocks.VEINMINE_CHARGE.block().get(), "veinmine_charge",
                "minecraft:block/sandstone_bottom", "minecraft:block/sandstone_bottom",
                "neovitae:block/blankrune", "minecraft:block/sand",
                "neovitae:block/defaultcrystal");
        augmentedChargeModel(NVBlocks.VEINMINE_CHARGE_2.block().get(), "veinmine_charge_2",
                "minecraft:block/sandstone_bottom", "neovitae:block/defaultcrystal",
                "minecraft:block/sandstone_bottom", "neovitae:block/blankrune",
                "minecraft:block/sand", "neovitae:block/copper_trim");

        // Fungal charges - nether themed
        shapedChargeModel(NVBlocks.FUNGAL_CHARGE.block().get(), "fungal_charge",
                "minecraft:block/crimson_planks", "minecraft:block/nether_wart_block",
                "neovitae:block/blankrune", "minecraft:block/crimson_stem",
                "neovitae:block/defaultcrystal");
        augmentedChargeModel(NVBlocks.FUNGAL_CHARGE_2.block().get(), "fungal_charge_2",
                "minecraft:block/crimson_planks", "neovitae:block/defaultcrystal",
                "minecraft:block/nether_wart_block", "neovitae:block/blankrune",
                "minecraft:block/crimson_stem", "neovitae:block/copper_trim");
    }

    private void shapedChargeModel(Block block, String name,
            String tex1, String tex3, String tex4, String tex5, String tex6) {
        // Create model with parent sub/shaped_charge
        ModelFile model = models().withExistingParent(name, bm("block/sub/shaped_charge"))
                .texture("1", ResourceLocation.parse(tex1))
                .texture("3", ResourceLocation.parse(tex3))
                .texture("4", ResourceLocation.parse(tex4))
                .texture("5", ResourceLocation.parse(tex5))
                .texture("6", ResourceLocation.parse(tex6))
                .texture("particle", ResourceLocation.parse(tex6))
                .renderType("cutout");

        directionalBlockState(block, model);
        simpleBlockItem(block, model);
    }

    private void augmentedChargeModel(Block block, String name,
            String tex1, String tex2, String tex3, String tex4, String tex5, String tex7) {
        // Create model with parent sub/augment_shaped_charge
        ModelFile model = models().withExistingParent(name, bm("block/sub/augment_shaped_charge"))
                .texture("1", ResourceLocation.parse(tex1))
                .texture("2", ResourceLocation.parse(tex2))
                .texture("3", ResourceLocation.parse(tex3))
                .texture("4", ResourceLocation.parse(tex4))
                .texture("5", ResourceLocation.parse(tex5))
                .texture("7", ResourceLocation.parse(tex7))
                .texture("particle", ResourceLocation.parse(tex2))
                .renderType("cutout");

        directionalBlockState(block, model);
        simpleBlockItem(block, model);
    }

    private void directionalBlockState(Block block, ModelFile model) {
        VariantBlockStateBuilder builder = getVariantBuilder(block);

        // UP: default orientation (attached to floor)
        builder.partialState()
            .with(BlockShapedExplosive.ATTACHED, Direction.UP)
            .modelForState().modelFile(model).addModel();

        // DOWN: rotated 180 on X axis (attached to ceiling)
        builder.partialState()
            .with(BlockShapedExplosive.ATTACHED, Direction.DOWN)
            .modelForState().modelFile(model).rotationX(180).addModel();

        // NORTH: rotated 90 on X axis
        builder.partialState()
            .with(BlockShapedExplosive.ATTACHED, Direction.NORTH)
            .modelForState().modelFile(model).rotationX(90).addModel();

        // SOUTH: rotated 270 on X axis (per 1.20.1)
        builder.partialState()
            .with(BlockShapedExplosive.ATTACHED, Direction.SOUTH)
            .modelForState().modelFile(model).rotationX(270).addModel();

        // EAST: rotated 90 on X, 90 on Y
        builder.partialState()
            .with(BlockShapedExplosive.ATTACHED, Direction.EAST)
            .modelForState().modelFile(model).rotationX(90).rotationY(90).addModel();

        // WEST: rotated 90 on X, 270 on Y
        builder.partialState()
            .with(BlockShapedExplosive.ATTACHED, Direction.WEST)
            .modelForState().modelFile(model).rotationX(90).rotationY(270).addModel();
    }

    private void registerSpiritCache() {
        ModelFile model = models().withExistingParent("spirit_cache", bm("block/sub/shaped_charge"))
                .texture("1", bm("block/altar_corner"))
                .texture("3", bm("block/altar_side"))
                .texture("4", bm("block/altar_edge"))
                .texture("5", bm("block/altar_inside"))
                .texture("6", bm("block/altar_corner_decoration"))
                .texture("particle", bm("block/altar_side"))
                .renderType("cutout");

        VariantBlockStateBuilder builder = getVariantBuilder(NVBlocks.SPIRIT_CACHE.block().get());
        builder.partialState().with(SpiritCacheBlock.FACING, Direction.NORTH)
                .modelForState().modelFile(model).addModel();
        builder.partialState().with(SpiritCacheBlock.FACING, Direction.SOUTH)
                .modelForState().modelFile(model).rotationY(180).addModel();
        builder.partialState().with(SpiritCacheBlock.FACING, Direction.WEST)
                .modelForState().modelFile(model).rotationY(270).addModel();
        builder.partialState().with(SpiritCacheBlock.FACING, Direction.EAST)
                .modelForState().modelFile(model).rotationY(90).addModel();

        simpleBlockItem(NVBlocks.SPIRIT_CACHE.block().get(), model);
    }

    private void registerDungeonBlocks() {
        // Non-variant dungeon blocks
        simpleBlockWithItem(DungeonBlocks.DUNGEON_ORE.block().get(),
            models().cubeAll("dungeon_ore", bm("block/dungeon/dungeon_ore")));
        simpleBlockWithItem(DungeonBlocks.PRISMATIC_DEMONITE.block().get(),
            emissiveCube("prismatic_demonite",
                bm("block/dungeon/prismatic_demonite"),
                bm("block/dungeon/prismatic_demonite_e")));
        simpleBlockWithItem(DungeonBlocks.DUNGEON_BRICK_ASSORTED.block().get(),
            models().cubeAll("dungeon_brick_assorted", bm("block/dungeon/dungeon_cracked_brick1")));

        // Path blocks
        simpleBlockWithItem(DungeonBlocks.WOOD_BRICK_PATH.block().get(),
            models().cubeAll("wood_brick_path", bm("block/wood_brick_path")));
        simpleBlockWithItem(DungeonBlocks.WOOD_TILE_PATH.block().get(),
            models().cubeAll("wood_tile_path", bm("block/wood_tile_path")));
        simpleBlockWithItem(DungeonBlocks.STONE_BRICK_PATH.block().get(),
            models().cubeAll("stone_brick_path", bm("block/stone_brick_path")));
        simpleBlockWithItem(DungeonBlocks.STONE_TILE_PATH.block().get(),
            models().cubeAll("stone_tile_path", bm("block/stone_tile_path")));
        simpleBlockWithItem(DungeonBlocks.WORN_STONE_BRICK_PATH.block().get(),
            models().cubeAll("worn_stone_brick_path", bm("block/worn_stone_brick_path")));
        simpleBlockWithItem(DungeonBlocks.WORN_STONE_TILE_PATH.block().get(),
            models().cubeAll("worn_stone_tile_path", bm("block/worn_stone_tile_path")));
        simpleBlockWithItem(DungeonBlocks.OBSIDIAN_BRICK_PATH.block().get(),
            models().cubeAll("obsidian_brick_path", bm("block/obsidian_brick_path")));
        simpleBlockWithItem(DungeonBlocks.OBSIDIAN_TILE_PATH.block().get(),
            models().cubeAll("obsidian_tile_path", bm("block/obsidian_tile_path")));

        // Variant dungeon blocks
        for (DungeonVariant variant : DungeonVariant.values()) {
            String suffix = variant.getSuffix();

            // Simple cube blocks
            dungeonSimpleBlock("dungeon_brick1" + suffix, "dungeon_brick1" + suffix, variant);
            dungeonSimpleBlock("dungeon_brick2" + suffix, "dungeon_brick2" + suffix, variant);
            dungeonSimpleBlock("dungeon_brick3" + suffix, "dungeon_brick3" + suffix, variant);
            dungeonSimpleBlock("dungeon_stone" + suffix, "dungeon_stone" + suffix, variant);
            dungeonSimpleBlock("dungeon_eye" + suffix, "dungeon_eye" + suffix, variant);
            dungeonSimpleBlock("dungeon_polished" + suffix, "dungeon_polished" + suffix, variant);
            dungeonSimpleBlock("dungeon_tile" + suffix, "dungeon_tile" + suffix, variant);
            dungeonSimpleBlock("dungeon_smallbrick" + suffix, "dungeon_smallbrick" + suffix, variant);
            dungeonSimpleBlock("dungeon_tilespecial" + suffix, "dungeon_tilespecial" + suffix, variant);
            dungeonSimpleBlock("dungeon_metal" + suffix, "dungeon_metal" + suffix, variant);

            // Pillar center - cube_column with pillar side and pillarheart end
            dungeonPillarBlock("dungeon_pillar_center" + suffix,
                "dungeon_pillar" + suffix, "dungeon_pillarheart" + suffix, variant);

            // Pillar special - cube_column with pillarspecial side and pillarheart end
            dungeonPillarBlock("dungeon_pillar_special" + suffix,
                "dungeon_pillarspecial" + suffix, "dungeon_pillarheart" + suffix, variant);

            // Pillar cap - simple cube_all using pillartop texture
            simpleBlockWithItem(DungeonBlocks.DUNGEON_PILLAR_CAP.get(variant).block().get(),
                models().cubeAll("dungeon_pillar_cap" + suffix, bm("block/dungeon/dungeon_pillartop" + suffix)));

            // Stairs
            stairsBlock(DungeonBlocks.DUNGEON_BRICK_STAIRS.get(variant).block().get(),
                bm("block/dungeon/dungeon_brick1" + suffix));
            simpleBlockItem(DungeonBlocks.DUNGEON_BRICK_STAIRS.get(variant).block().get(),
                models().getExistingFile(bm("block/dungeon_brick_stairs" + suffix)));

            stairsBlock(DungeonBlocks.DUNGEON_POLISHED_STAIRS.get(variant).block().get(),
                bm("block/dungeon/dungeon_polished" + suffix));
            simpleBlockItem(DungeonBlocks.DUNGEON_POLISHED_STAIRS.get(variant).block().get(),
                models().getExistingFile(bm("block/dungeon_polished_stairs" + suffix)));

            stairsBlock(DungeonBlocks.DUNGEON_STONE_STAIRS.get(variant).block().get(),
                bm("block/dungeon/dungeon_stone" + suffix));
            simpleBlockItem(DungeonBlocks.DUNGEON_STONE_STAIRS.get(variant).block().get(),
                models().getExistingFile(bm("block/dungeon_stone_stairs" + suffix)));

            // Walls
            wallBlock(DungeonBlocks.DUNGEON_BRICK_WALL.get(variant).block().get(),
                bm("block/dungeon/dungeon_brick1" + suffix));
            simpleBlockItem(DungeonBlocks.DUNGEON_BRICK_WALL.get(variant).block().get(),
                models().wallInventory("dungeon_brick_wall" + suffix + "_inventory", bm("block/dungeon/dungeon_brick1" + suffix)));

            wallBlock(DungeonBlocks.DUNGEON_TILE_WALL.get(variant).block().get(),
                bm("block/dungeon/dungeon_tile" + suffix));
            simpleBlockItem(DungeonBlocks.DUNGEON_TILE_WALL.get(variant).block().get(),
                models().wallInventory("dungeon_tile_wall" + suffix + "_inventory", bm("block/dungeon/dungeon_tile" + suffix)));

            wallBlock(DungeonBlocks.DUNGEON_POLISHED_WALL.get(variant).block().get(),
                bm("block/dungeon/dungeon_polished" + suffix));
            simpleBlockItem(DungeonBlocks.DUNGEON_POLISHED_WALL.get(variant).block().get(),
                models().wallInventory("dungeon_polished_wall" + suffix + "_inventory", bm("block/dungeon/dungeon_polished" + suffix)));

            wallBlock(DungeonBlocks.DUNGEON_STONE_WALL.get(variant).block().get(),
                bm("block/dungeon/dungeon_stone" + suffix));
            simpleBlockItem(DungeonBlocks.DUNGEON_STONE_WALL.get(variant).block().get(),
                models().wallInventory("dungeon_stone_wall" + suffix + "_inventory", bm("block/dungeon/dungeon_stone" + suffix)));

            // Slabs
            slabBlock(DungeonBlocks.DUNGEON_BRICK_SLAB.get(variant).block().get(),
                bm("block/dungeon_brick1" + suffix), bm("block/dungeon/dungeon_brick1" + suffix));
            simpleBlockItem(DungeonBlocks.DUNGEON_BRICK_SLAB.get(variant).block().get(),
                models().getExistingFile(bm("block/dungeon_brick_slab" + suffix)));

            slabBlock(DungeonBlocks.DUNGEON_TILE_SLAB.get(variant).block().get(),
                bm("block/dungeon_tile" + suffix), bm("block/dungeon/dungeon_tile" + suffix));
            simpleBlockItem(DungeonBlocks.DUNGEON_TILE_SLAB.get(variant).block().get(),
                models().getExistingFile(bm("block/dungeon_tile_slab" + suffix)));

            slabBlock(DungeonBlocks.DUNGEON_STONE_SLAB.get(variant).block().get(),
                bm("block/dungeon_stone" + suffix), bm("block/dungeon/dungeon_stone" + suffix));
            simpleBlockItem(DungeonBlocks.DUNGEON_STONE_SLAB.get(variant).block().get(),
                models().getExistingFile(bm("block/dungeon_stone_slab" + suffix)));

            slabBlock(DungeonBlocks.DUNGEON_POLISHED_SLAB.get(variant).block().get(),
                bm("block/dungeon_polished" + suffix), bm("block/dungeon/dungeon_polished" + suffix));
            simpleBlockItem(DungeonBlocks.DUNGEON_POLISHED_SLAB.get(variant).block().get(),
                models().getExistingFile(bm("block/dungeon_polished_slab" + suffix)));

            // Gates (fence gates)
            fenceGateBlock(DungeonBlocks.DUNGEON_BRICK_GATE.get(variant).block().get(),
                bm("block/dungeon/dungeon_brick1" + suffix));
            simpleBlockItem(DungeonBlocks.DUNGEON_BRICK_GATE.get(variant).block().get(),
                models().getExistingFile(bm("block/dungeon_brick_gate" + suffix)));

            fenceGateBlock(DungeonBlocks.DUNGEON_POLISHED_GATE.get(variant).block().get(),
                bm("block/dungeon/dungeon_polished" + suffix));
            simpleBlockItem(DungeonBlocks.DUNGEON_POLISHED_GATE.get(variant).block().get(),
                models().getExistingFile(bm("block/dungeon_polished_gate" + suffix)));
        }
    }

    private void dungeonSimpleBlock(String name, String textureName, DungeonVariant variant) {
        var holder = getDungeonBlock(name);
        if (holder != null) {
            simpleBlockWithItem(holder.block().get(),
                models().cubeAll(name, bm("block/dungeon/" + textureName)));
        }
    }

    private void dungeonPillarBlock(String name, String sideTexture, String endTexture, DungeonVariant variant) {
        var block = getDungeonPillarBlock(name);
        if (block != null) {
            ModelFile model = models().cubeColumn(name, bm("block/dungeon/" + sideTexture), bm("block/dungeon/" + endTexture));
            axisBlock(block.block().get(), model, model);
            simpleBlockItem(block.block().get(), model);
        }
    }

    private BlockWithItemHolder<Block, BlockItem> getDungeonBlock(String name) {
        // Find the block by iterating through variant maps
        for (DungeonVariant v : DungeonVariant.values()) {
            String suffix = v.getSuffix();
            if (name.equals("dungeon_brick1" + suffix)) return DungeonBlocks.DUNGEON_BRICK_1.get(v);
            if (name.equals("dungeon_brick2" + suffix)) return DungeonBlocks.DUNGEON_BRICK_2.get(v);
            if (name.equals("dungeon_brick3" + suffix)) return DungeonBlocks.DUNGEON_BRICK_3.get(v);
            if (name.equals("dungeon_stone" + suffix)) return DungeonBlocks.DUNGEON_STONE.get(v);
            if (name.equals("dungeon_eye" + suffix)) return DungeonBlocks.DUNGEON_EYE.get(v);
            if (name.equals("dungeon_polished" + suffix)) return DungeonBlocks.DUNGEON_POLISHED.get(v);
            if (name.equals("dungeon_tile" + suffix)) return DungeonBlocks.DUNGEON_TILE.get(v);
            if (name.equals("dungeon_smallbrick" + suffix)) return DungeonBlocks.DUNGEON_SMALLBRICK.get(v);
            if (name.equals("dungeon_tilespecial" + suffix)) return DungeonBlocks.DUNGEON_TILESPECIAL.get(v);
            if (name.equals("dungeon_metal" + suffix)) return DungeonBlocks.DUNGEON_METAL.get(v);
        }
        return null;
    }

    private BlockWithItemHolder<RotatedPillarBlock, BlockItem> getDungeonPillarBlock(String name) {
        for (DungeonVariant v : DungeonVariant.values()) {
            String suffix = v.getSuffix();
            if (name.equals("dungeon_pillar_center" + suffix)) return DungeonBlocks.DUNGEON_PILLAR_CENTER.get(v);
            if (name.equals("dungeon_pillar_special" + suffix)) return DungeonBlocks.DUNGEON_PILLAR_SPECIAL.get(v);
        }
        return null;
    }

    private static ResourceLocation bm(String path) {
        return ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, path);
    }

    private BlockModelBuilder glowingRune(String name) {
        return emissiveCube(name, bm("block/" + name), bm("block/" + name + "_e"));
    }

    private BlockModelBuilder emissiveCube(String name, ResourceLocation base, ResourceLocation glow) {
        models().existingFileHelper.trackGenerated(glow, ModelProvider.TEXTURE);
        BlockModelBuilder b = models().getBuilder(name)
                .parent(models().getExistingFile(ResourceLocation.parse("block/block")))
                .renderType("cutout")
                .texture("particle", base)
                .texture("base", base)
                .texture("glow", glow);

        b.element()
                .from(0, 0, 0).to(16, 16, 16)
                .face(Direction.DOWN ).texture("#base").cullface(Direction.DOWN ).end()
                .face(Direction.UP   ).texture("#base").cullface(Direction.UP   ).end()
                .face(Direction.NORTH).texture("#base").cullface(Direction.NORTH).end()
                .face(Direction.SOUTH).texture("#base").cullface(Direction.SOUTH).end()
                .face(Direction.WEST ).texture("#base").cullface(Direction.WEST ).end()
                .face(Direction.EAST ).texture("#base").cullface(Direction.EAST ).end()
                .end();

        b.element()
                .from(-0.005f, -0.005f, -0.005f).to(16.005f, 16.005f, 16.005f)
                .emissivity(15, 15)
                .face(Direction.DOWN ).texture("#glow").end()
                .face(Direction.UP   ).texture("#glow").end()
                .face(Direction.NORTH).texture("#glow").end()
                .face(Direction.SOUTH).texture("#glow").end()
                .face(Direction.WEST ).texture("#glow").end()
                .face(Direction.EAST ).texture("#glow").end()
                .end();

        return b;
    }

    @Override
    public void stairsBlock(StairBlock block, ModelFile stairs, ModelFile stairsInner, ModelFile stairsOuter) {
        getVariantBuilder(block)
                .forAllStatesExcept(state -> {
                    Direction facing = state.getValue(StairBlock.FACING);
                    Half half = state.getValue(StairBlock.HALF);
                    StairsShape shape = state.getValue(StairBlock.SHAPE);
                    int yRot = (int) facing.getClockWise().toYRot();
                    if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
                        yRot += 270;
                    }
                    if (shape != StairsShape.STRAIGHT && half == Half.TOP) {
                        yRot += 90;
                    }
                    yRot %= 360;
                    boolean uvlock = yRot != 0 || half == Half.TOP;
                    return ConfiguredModel.builder()
                            .modelFile(shape == StairsShape.STRAIGHT ? stairs
                                    : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? stairsInner : stairsOuter)
                            .rotationX(half == Half.BOTTOM ? 0 : 180)
                            .rotationY(yRot)
                            .uvLock(uvlock)
                            .build();
                }, StairBlock.WATERLOGGED);
    }

}
