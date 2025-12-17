package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.ARCBlock;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonVariant;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;

public class BMBlockStateProvider extends BlockStateProvider {
    public BMBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, NeoVitae.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        BMBlocks.BASIC_BLOCKS.getEntries().forEach(block -> {
            String path = block.getId().getPath();
            // Skip tau blocks (special crop-like rendering)
            if (path.contains("tau")) {
                return;
            }
            // Demon crystals need cutout render type for transparency
            if (path.contains("demon_crystal")) {
                ModelFile model = models().cubeAll(path, bm("block/" + path))
                    .renderType("cutout");
                simpleBlockWithItem(block.get(), model);
                return;
            }
            simpleBlockWithItem(block.get(), cubeAll(block.get()));
        });

        simpleBlockWithItem(BMBlocks.BLANK_RITUAL_STONE.block().get(), cubeAll(BMBlocks.BLANK_RITUAL_STONE.block().get()));
        simpleBlockWithItem(BMBlocks.AIR_RITUAL_STONE.block().get(), cubeAll(BMBlocks.AIR_RITUAL_STONE.block().get()));
        simpleBlockWithItem(BMBlocks.WATER_RITUAL_STONE.block().get(), cubeAll(BMBlocks.WATER_RITUAL_STONE.block().get()));
        simpleBlockWithItem(BMBlocks.FIRE_RITUAL_STONE.block().get(), cubeAll(BMBlocks.FIRE_RITUAL_STONE.block().get()));
        simpleBlockWithItem(BMBlocks.EARTH_RITUAL_STONE.block().get(), cubeAll(BMBlocks.EARTH_RITUAL_STONE.block().get()));
        simpleBlockWithItem(BMBlocks.DUSK_RITUAL_STONE.block().get(), cubeAll(BMBlocks.DUSK_RITUAL_STONE.block().get()));
        simpleBlockWithItem(BMBlocks.DAWN_RITUAL_STONE.block().get(), cubeAll(BMBlocks.DAWN_RITUAL_STONE.block().get()));
        simpleBlockWithItem(BMBlocks.MASTER_RITUAL_STONE.block().get(), cubeAll(BMBlocks.MASTER_RITUAL_STONE.block().get()));
        // Inverted MRS uses same texture as regular MRS
        simpleBlockWithItem(BMBlocks.INVERTED_MASTER_RITUAL_STONE.block().get(),
            models().cubeAll("inverted_master_ritual_stone", bm("block/master_ritual_stone")));
        // Imperfect ritual stone uses the blank ritual stone texture
        simpleBlockWithItem(BMBlocks.IMPERFECT_RITUAL_STONE.block().get(),
            models().cubeAll("imperfect_ritual_stone", bm("block/ritual_stone")));

        // Incense altar, routing nodes - use manual OBJ models and blockstates
        // (see src/main/resources/assets/bloodmagic/blockstates/ and models/)

        VariantBlockStateBuilder builder = getVariantBuilder(BMBlocks.ARC_BLOCK.block().get());
        String bottom = "block/arc_bottom";
        String lit = "_lit";
        for (EnumWillType type : EnumWillType.values()) {
            String willName = type.getSerializedName();
            String side = "block/arc_side_" + willName;
            String front = "block/arc_front_" + willName;
            String top = "block/arc_top_" + willName;
            ModelFile on = models().orientableWithBottom("alchemical_reaction_chamber_" + willName + "_lit", bm(side + lit), bm(front + lit), bm(bottom), bm(top));
            ModelFile off = models().orientableWithBottom("alchemical_reaction_chamber_" + willName, bm(side), bm(front), bm(bottom), bm(top));
            if (type == EnumWillType.DEFAULT) {
                simpleBlockItem(BMBlocks.ARC_BLOCK.block().get(), off);
            }

            for (Direction facing : Direction.Plane.HORIZONTAL) {
                builder.partialState().with(ARCBlock.LIT, false).with(ARCBlock.FACING, facing).with(ARCBlock.TYPE, type).modelForState().modelFile(off).rotationY((int) facing.getOpposite().toYRot()).addModel();
                builder.partialState().with(ARCBlock.LIT, true).with(ARCBlock.FACING, facing).with(ARCBlock.TYPE, type).modelForState().modelFile(on).rotationY((int) facing.getOpposite().toYRot()).addModel();
            }
        }

        // Dungeon blocks
        registerDungeonBlocks();

        // Explosive charges - directional blocks attached to surfaces
        registerExplosiveCharges();

        // Mimic block - renders the mimicked block dynamically, this is the fallback texture
        ModelFile mimicModel = models().cubeAll("mimic", bm("block/dungeon/dungeon_brick1"));
        simpleBlockWithItem(BMBlocks.MIMIC.block().get(), mimicModel);

        // Ethereal Mimic block - same as mimic but non-solid (no collision)
        ModelFile etherealMimicModel = models().cubeAll("ethereal_mimic", bm("block/dungeon/dungeon_brick1"));
        simpleBlockWithItem(BMBlocks.ETHEREAL_MIMIC.block().get(), etherealMimicModel);

        // Dungeon Controller - manages procedural dungeon generation (uses dungeon stone texture)
        ModelFile dungeonControllerModel = models().cubeAll("dungeon_controller", bm("block/dungeon/dungeon_stone"));
        simpleBlockWithItem(BMBlocks.DUNGEON_CONTROLLER.block().get(), dungeonControllerModel);

        // Dungeon Seal - door seal blocks for dungeon progression (uses dungeon eye texture)
        ModelFile dungeonSealModel = models().cubeAll("dungeon_seal", bm("block/dungeon/dungeon_eye"));
        simpleBlockWithItem(BMBlocks.DUNGEON_SEAL.block().get(), dungeonSealModel);

        // Inversion Pillar - dungeon teleporter (uses custom pillar_mid parent)
        ModelFile inversionPillarModel = models().withExistingParent("inversion_pillar", bm("block/pillar_mid"))
                .texture("texture", bm("block/pillar_mid"));
        simpleBlockWithItem(BMBlocks.INVERSION_PILLAR.block().get(), inversionPillarModel);

        // Inversion Pillar Cap - uses block state for top/bottom orientation (uses custom pillar_top/pillar_bottom parents)
        ModelFile inversionPillarCapBottom = models().withExistingParent("inversion_pillar_bottom", bm("block/pillar_bottom"))
                .texture("texture", bm("block/pillar_base"));
        ModelFile inversionPillarCapTop = models().withExistingParent("inversion_pillar_top", bm("block/pillar_top"))
                .texture("texture", bm("block/pillar_base"));
        getVariantBuilder(BMBlocks.INVERSION_PILLAR_CAP.block().get())
                .partialState().with(com.breakinblocks.neovitae.common.block.BlockInversionPillarEnd.TYPE, com.breakinblocks.neovitae.common.block.type.PillarCapType.BOTTOM)
                .modelForState().modelFile(inversionPillarCapBottom).addModel()
                .partialState().with(com.breakinblocks.neovitae.common.block.BlockInversionPillarEnd.TYPE, com.breakinblocks.neovitae.common.block.type.PillarCapType.TOP)
                .modelForState().modelFile(inversionPillarCapTop).addModel();
        simpleBlockItem(BMBlocks.INVERSION_PILLAR_CAP.block().get(), inversionPillarCapBottom);
    }

    private void registerExplosiveCharges() {
        // Basic shaped charge - uses sub/shaped_charge parent
        shapedChargeModel(BMBlocks.SHAPED_CHARGE.block().get(), "shaped_charge",
                "neovitae:block/dungeon/dungeon_tile", "neovitae:block/dungeon/dungeon_stone",
                "neovitae:block/blankrune", "neovitae:block/largebloodstonebrick",
                "neovitae:block/defaultcrystal");

        // Augmented shaped charge - uses sub/augment_shaped_charge parent
        augmentedChargeModel(BMBlocks.AUG_SHAPED_CHARGE.block().get(), "aug_shaped_charge",
                "neovitae:block/dungeon/dungeon_tile", "neovitae:block/defaultcrystal",
                "neovitae:block/dungeon/dungeon_stone", "neovitae:block/blankrune",
                "neovitae:block/largebloodstonebrick", "neovitae:block/copper_trim");

        // Deep shaped charge - uses sub/augment_shaped_charge parent with iron core
        augmentedChargeModel(BMBlocks.SHAPED_CHARGE_DEEP.block().get(), "shaped_charge_deep",
                "neovitae:block/dungeon/dungeon_tile", "minecraft:block/iron_block",
                "neovitae:block/dungeon/dungeon_stone", "neovitae:block/blankrune",
                "neovitae:block/largebloodstonebrick", "neovitae:block/copper_trim");

        // Deforester charges - wood themed
        shapedChargeModel(BMBlocks.DEFORESTER_CHARGE.block().get(), "deforester_charge",
                "minecraft:block/oak_log_top", "minecraft:block/oak_log_top",
                "neovitae:block/blankrune", "minecraft:block/oak_planks",
                "neovitae:block/defaultcrystal");
        augmentedChargeModel(BMBlocks.DEFORESTER_CHARGE_2.block().get(), "deforester_charge_2",
                "minecraft:block/oak_log_top", "neovitae:block/defaultcrystal",
                "minecraft:block/oak_log_top", "neovitae:block/blankrune",
                "minecraft:block/oak_planks", "neovitae:block/copper_trim");

        // Veinmine charges - sand/stone themed
        shapedChargeModel(BMBlocks.VEINMINE_CHARGE.block().get(), "veinmine_charge",
                "minecraft:block/sandstone_bottom", "minecraft:block/sandstone_bottom",
                "neovitae:block/blankrune", "minecraft:block/sand",
                "neovitae:block/defaultcrystal");
        augmentedChargeModel(BMBlocks.VEINMINE_CHARGE_2.block().get(), "veinmine_charge_2",
                "minecraft:block/sandstone_bottom", "neovitae:block/defaultcrystal",
                "minecraft:block/sandstone_bottom", "neovitae:block/blankrune",
                "minecraft:block/sand", "neovitae:block/copper_trim");

        // Fungal charges - nether themed
        shapedChargeModel(BMBlocks.FUNGAL_CHARGE.block().get(), "fungal_charge",
                "minecraft:block/crimson_planks", "minecraft:block/nether_wart_block",
                "neovitae:block/blankrune", "minecraft:block/crimson_stem",
                "neovitae:block/defaultcrystal");
        augmentedChargeModel(BMBlocks.FUNGAL_CHARGE_2.block().get(), "fungal_charge_2",
                "minecraft:block/crimson_planks", "neovitae:block/defaultcrystal",
                "minecraft:block/nether_wart_block", "neovitae:block/blankrune",
                "minecraft:block/crimson_stem", "neovitae:block/copper_trim");
    }

    private void shapedChargeModel(net.minecraft.world.level.block.Block block, String name,
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

    private void augmentedChargeModel(net.minecraft.world.level.block.Block block, String name,
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

    private void directionalBlockState(net.minecraft.world.level.block.Block block, ModelFile model) {
        VariantBlockStateBuilder builder = getVariantBuilder(block);

        // UP: default orientation (attached to floor)
        builder.partialState()
            .with(com.breakinblocks.neovitae.common.block.BlockShapedExplosive.ATTACHED, Direction.UP)
            .modelForState().modelFile(model).addModel();

        // DOWN: rotated 180 on X axis (attached to ceiling)
        builder.partialState()
            .with(com.breakinblocks.neovitae.common.block.BlockShapedExplosive.ATTACHED, Direction.DOWN)
            .modelForState().modelFile(model).rotationX(180).addModel();

        // NORTH: rotated 90 on X axis
        builder.partialState()
            .with(com.breakinblocks.neovitae.common.block.BlockShapedExplosive.ATTACHED, Direction.NORTH)
            .modelForState().modelFile(model).rotationX(90).addModel();

        // SOUTH: rotated 270 on X axis (per 1.20.1)
        builder.partialState()
            .with(com.breakinblocks.neovitae.common.block.BlockShapedExplosive.ATTACHED, Direction.SOUTH)
            .modelForState().modelFile(model).rotationX(270).addModel();

        // EAST: rotated 90 on X, 90 on Y
        builder.partialState()
            .with(com.breakinblocks.neovitae.common.block.BlockShapedExplosive.ATTACHED, Direction.EAST)
            .modelForState().modelFile(model).rotationX(90).rotationY(90).addModel();

        // WEST: rotated 90 on X, 270 on Y
        builder.partialState()
            .with(com.breakinblocks.neovitae.common.block.BlockShapedExplosive.ATTACHED, Direction.WEST)
            .modelForState().modelFile(model).rotationX(90).rotationY(270).addModel();
    }

    private void registerDungeonBlocks() {
        // Non-variant dungeon blocks
        simpleBlockWithItem(DungeonBlocks.DUNGEON_ORE.block().get(),
            models().cubeAll("dungeon_ore", bm("block/dungeon/dungeon_ore")));
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

    private com.breakinblocks.neovitae.util.helper.BlockWithItemHolder<net.minecraft.world.level.block.Block, net.minecraft.world.item.BlockItem> getDungeonBlock(String name) {
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

    private com.breakinblocks.neovitae.util.helper.BlockWithItemHolder<net.minecraft.world.level.block.RotatedPillarBlock, net.minecraft.world.item.BlockItem> getDungeonPillarBlock(String name) {
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
}
