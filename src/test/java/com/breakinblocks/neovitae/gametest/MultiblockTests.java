package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.NVRituals;
import com.breakinblocks.neovitae.ritual.Ritual;

public final class MultiblockTests {

    private MultiblockTests() {}

    private static BlockState runeBlock(EnumRuneType type) {
        return switch (type) {
            case WATER -> NVBlocks.WATER_RITUAL_STONE.block().get().defaultBlockState();
            case FIRE -> NVBlocks.FIRE_RITUAL_STONE.block().get().defaultBlockState();
            case EARTH -> NVBlocks.EARTH_RITUAL_STONE.block().get().defaultBlockState();
            case AIR -> NVBlocks.AIR_RITUAL_STONE.block().get().defaultBlockState();
            case TENEBRAE -> NVBlocks.TENEBRAE_RITUAL_STONE.block().get().defaultBlockState();
            case DEUS -> NVBlocks.DEUS_RITUAL_STONE.block().get().defaultBlockState();
            default -> NVBlocks.BLANK_RITUAL_STONE.block().get().defaultBlockState();
        };
    }

    public static void register(NVTestRegistrar r) {
        r.add("multiblock/altar_detects_apprentice_tier", 200, helper -> {
            BlockPos altarPos = new BlockPos(3, 2, 2);
            BlockState rune = NVBlocks.RUNE_BLANK.block().get().defaultBlockState();

            helper.setBlock(altarPos, NVBlocks.ARA_VITAE.block().get().defaultBlockState());

            helper.setBlock(altarPos.offset(1, -1, 0), rune);
            helper.setBlock(altarPos.offset(-1, -1, 0), rune);
            helper.setBlock(altarPos.offset(0, -1, 1), rune);
            helper.setBlock(altarPos.offset(0, -1, -1), rune);

            helper.setBlock(altarPos.offset(1, -1, 1), rune);
            helper.setBlock(altarPos.offset(1, -1, -1), rune);
            helper.setBlock(altarPos.offset(-1, -1, 1), rune);
            helper.setBlock(altarPos.offset(-1, -1, -1), rune);

            helper.succeedWhen(() -> {
                AraVitaeTile altar = helper.getBlockEntity(altarPos, AraVitaeTile.class);
                helper.assertTrue(altar != null, "Missing altar BE");
                helper.assertTrue(!(altar.getTier() < 1), "Apprentice altar should be tier 1+, got " + altar.getTier());
            });
        });

        r.add("multiblock/altar_tier_drops_with_missing_rune", 200, helper -> {
            BlockPos altarPos = new BlockPos(3, 2, 2);
            BlockState rune = NVBlocks.RUNE_BLANK.block().get().defaultBlockState();

            helper.setBlock(altarPos, NVBlocks.ARA_VITAE.block().get().defaultBlockState());

            helper.setBlock(altarPos.offset(1, -1, 0), rune);
            helper.setBlock(altarPos.offset(-1, -1, 0), rune);
            helper.setBlock(altarPos.offset(0, -1, 1), rune);

            helper.succeedWhen(() -> {
                AraVitaeTile altar = helper.getBlockEntity(altarPos, AraVitaeTile.class);
                helper.assertTrue(altar != null, "Missing altar BE");
                helper.assertTrue(!(altar.getTier() > 0), "Incomplete altar should be tier 0, got " + altar.getTier());
            });
        });

        r.add("multiblock/water_ritual_validates", 60, helper -> {
            BlockPos mrsPos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(mrsPos, NVBlocks.MASTER_RITUAL_STONE.block().get().defaultBlockState());

            helper.setBlock(mrsPos.offset(1, 0, 1), runeBlock(EnumRuneType.WATER));
            helper.setBlock(mrsPos.offset(1, 0, -1), runeBlock(EnumRuneType.WATER));
            helper.setBlock(mrsPos.offset(-1, 0, -1), runeBlock(EnumRuneType.WATER));
            helper.setBlock(mrsPos.offset(-1, 0, 1), runeBlock(EnumRuneType.WATER));

            helper.runAfterDelay(5, () -> {
                MasterRitualStoneBlockEntity mrs = helper.getBlockEntity(mrsPos, MasterRitualStoneBlockEntity.class);
                if (mrs == null) {
                    helper.fail("Missing MRS");
                    return;
                }
                Ritual waterRitual = NVRituals.WATER.get();
                if (!mrs.checkStructure(waterRitual)) {
                    helper.fail("Water ritual structure should validate with correct runes");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("multiblock/wrong_rune_type_fails_validation", 60, helper -> {
            BlockPos mrsPos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(mrsPos, NVBlocks.MASTER_RITUAL_STONE.block().get().defaultBlockState());

            helper.setBlock(mrsPos.offset(1, 0, 1), runeBlock(EnumRuneType.FIRE));
            helper.setBlock(mrsPos.offset(1, 0, -1), runeBlock(EnumRuneType.FIRE));
            helper.setBlock(mrsPos.offset(-1, 0, -1), runeBlock(EnumRuneType.FIRE));
            helper.setBlock(mrsPos.offset(-1, 0, 1), runeBlock(EnumRuneType.FIRE));

            helper.runAfterDelay(5, () -> {
                MasterRitualStoneBlockEntity mrs = helper.getBlockEntity(mrsPos, MasterRitualStoneBlockEntity.class);
                if (mrs == null) {
                    helper.fail("Missing MRS");
                    return;
                }
                if (mrs.checkStructure(NVRituals.WATER.get())) {
                    helper.fail("Water ritual should NOT validate with fire runes");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("multiblock/missing_rune_fails_validation", 60, helper -> {
            BlockPos mrsPos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(mrsPos, NVBlocks.MASTER_RITUAL_STONE.block().get().defaultBlockState());

            helper.setBlock(mrsPos.offset(1, 0, 1), runeBlock(EnumRuneType.WATER));
            helper.setBlock(mrsPos.offset(1, 0, -1), runeBlock(EnumRuneType.WATER));
            helper.setBlock(mrsPos.offset(-1, 0, -1), runeBlock(EnumRuneType.WATER));

            helper.runAfterDelay(5, () -> {
                MasterRitualStoneBlockEntity mrs = helper.getBlockEntity(mrsPos, MasterRitualStoneBlockEntity.class);
                if (mrs == null) {
                    helper.fail("Missing MRS");
                    return;
                }
                if (mrs.checkStructure(NVRituals.WATER.get())) {
                    helper.fail("Water ritual should NOT validate with missing rune");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("multiblock/symmetric_ritual_rotation", 60, helper -> {
            BlockPos mrsPos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(mrsPos, NVBlocks.MASTER_RITUAL_STONE.block().get().defaultBlockState());

            helper.setBlock(mrsPos.offset(1, 0, 1), runeBlock(EnumRuneType.WATER));
            helper.setBlock(mrsPos.offset(1, 0, -1), runeBlock(EnumRuneType.WATER));
            helper.setBlock(mrsPos.offset(-1, 0, -1), runeBlock(EnumRuneType.WATER));
            helper.setBlock(mrsPos.offset(-1, 0, 1), runeBlock(EnumRuneType.WATER));

            helper.runAfterDelay(5, () -> {
                MasterRitualStoneBlockEntity mrs = helper.getBlockEntity(mrsPos, MasterRitualStoneBlockEntity.class);
                if (mrs == null) {
                    helper.fail("Missing MRS");
                    return;
                }
                if (!mrs.checkStructure(NVRituals.WATER.get())) {
                    helper.fail("Symmetric ritual should validate in at least one rotation");
                    return;
                }
                helper.succeed();
            });
        });
    }
}
