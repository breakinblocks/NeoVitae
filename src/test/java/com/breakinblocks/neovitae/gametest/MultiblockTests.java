package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.NVRituals;

import java.util.ArrayList;
import java.util.List;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class MultiblockTests {

    private static BlockState runeBlock(EnumRuneType type) {
        return switch (type) {
            case WATER -> NVBlocks.WATER_RITUAL_STONE.block().get().defaultBlockState();
            case FIRE -> NVBlocks.FIRE_RITUAL_STONE.block().get().defaultBlockState();
            case EARTH -> NVBlocks.EARTH_RITUAL_STONE.block().get().defaultBlockState();
            case AIR -> NVBlocks.AIR_RITUAL_STONE.block().get().defaultBlockState();
            case DUSK -> NVBlocks.DUSK_RITUAL_STONE.block().get().defaultBlockState();
            case DAWN -> NVBlocks.DAWN_RITUAL_STONE.block().get().defaultBlockState();
            default -> NVBlocks.BLANK_RITUAL_STONE.block().get().defaultBlockState();
        };
    }

    // ==================== Ara Vitae Tier Tests ====================

    // Apprentice altar: 4 rune blocks at cardinal directions + 4 at diagonals, all at y=-1
    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void altarDetectsApprenticeTier(GameTestHelper helper) {
        BlockPos altarPos = new BlockPos(3, 2, 2);
        BlockState rune = NVBlocks.RUNE_BLANK.block().get().defaultBlockState();

        helper.setBlock(altarPos, NVBlocks.ARA_VITAE.block().get().defaultBlockState());

        // 4 cardinal runes at y-1
        helper.setBlock(altarPos.offset(1, -1, 0), rune);
        helper.setBlock(altarPos.offset(-1, -1, 0), rune);
        helper.setBlock(altarPos.offset(0, -1, 1), rune);
        helper.setBlock(altarPos.offset(0, -1, -1), rune);

        // 4 diagonal blocks at y-1
        helper.setBlock(altarPos.offset(1, -1, 1), rune);
        helper.setBlock(altarPos.offset(1, -1, -1), rune);
        helper.setBlock(altarPos.offset(-1, -1, 1), rune);
        helper.setBlock(altarPos.offset(-1, -1, -1), rune);

        // Wait for structure check (every 100 ticks)
        helper.runAfterDelay(150, () -> {
            AraVitaeTile altar = (AraVitaeTile) helper.getBlockEntity(altarPos);
            if (altar.getTier() < 1) {
                helper.fail("Apprentice altar should be tier 1+, got " + altar.getTier());
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void altarTierDropsWithMissingRune(GameTestHelper helper) {
        BlockPos altarPos = new BlockPos(3, 2, 2);
        BlockState rune = NVBlocks.RUNE_BLANK.block().get().defaultBlockState();

        helper.setBlock(altarPos, NVBlocks.ARA_VITAE.block().get().defaultBlockState());

        // Only 3 of 4 cardinal runes — incomplete
        helper.setBlock(altarPos.offset(1, -1, 0), rune);
        helper.setBlock(altarPos.offset(-1, -1, 0), rune);
        helper.setBlock(altarPos.offset(0, -1, 1), rune);
        // Missing: (0, -1, -1)

        helper.runAfterDelay(150, () -> {
            AraVitaeTile altar = (AraVitaeTile) helper.getBlockEntity(altarPos);
            if (altar.getTier() > 0) {
                helper.fail("Incomplete altar should be tier 0, got " + altar.getTier());
                return;
            }
            helper.succeed();
        });
    }

    // ==================== Ritual Structure Tests ====================

    // Water ritual: 4 WATER runes at corners (1,0,1), (1,0,-1), (-1,0,-1), (-1,0,1)
    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void waterRitualStructureValidates(GameTestHelper helper) {
        BlockPos mrsPos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(mrsPos, NVBlocks.MASTER_RITUAL_STONE.block().get().defaultBlockState());

        helper.setBlock(mrsPos.offset(1, 0, 1), runeBlock(EnumRuneType.WATER));
        helper.setBlock(mrsPos.offset(1, 0, -1), runeBlock(EnumRuneType.WATER));
        helper.setBlock(mrsPos.offset(-1, 0, -1), runeBlock(EnumRuneType.WATER));
        helper.setBlock(mrsPos.offset(-1, 0, 1), runeBlock(EnumRuneType.WATER));

        helper.runAfterDelay(5, () -> {
            MasterRitualStoneBlockEntity mrs = (MasterRitualStoneBlockEntity) helper.getBlockEntity(mrsPos);
            Ritual waterRitual = NVRituals.WATER.get();

            if (!mrs.checkStructure(waterRitual)) {
                helper.fail("Water ritual structure should validate with correct runes");
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void wrongRuneTypeFailsValidation(GameTestHelper helper) {
        BlockPos mrsPos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(mrsPos, NVBlocks.MASTER_RITUAL_STONE.block().get().defaultBlockState());

        // Place FIRE runes instead of WATER
        helper.setBlock(mrsPos.offset(1, 0, 1), runeBlock(EnumRuneType.FIRE));
        helper.setBlock(mrsPos.offset(1, 0, -1), runeBlock(EnumRuneType.FIRE));
        helper.setBlock(mrsPos.offset(-1, 0, -1), runeBlock(EnumRuneType.FIRE));
        helper.setBlock(mrsPos.offset(-1, 0, 1), runeBlock(EnumRuneType.FIRE));

        helper.runAfterDelay(5, () -> {
            MasterRitualStoneBlockEntity mrs = (MasterRitualStoneBlockEntity) helper.getBlockEntity(mrsPos);
            Ritual waterRitual = NVRituals.WATER.get();

            if (mrs.checkStructure(waterRitual)) {
                helper.fail("Water ritual should NOT validate with fire runes");
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void missingRuneFailsValidation(GameTestHelper helper) {
        BlockPos mrsPos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(mrsPos, NVBlocks.MASTER_RITUAL_STONE.block().get().defaultBlockState());

        // Only 3 of 4 runes
        helper.setBlock(mrsPos.offset(1, 0, 1), runeBlock(EnumRuneType.WATER));
        helper.setBlock(mrsPos.offset(1, 0, -1), runeBlock(EnumRuneType.WATER));
        helper.setBlock(mrsPos.offset(-1, 0, -1), runeBlock(EnumRuneType.WATER));

        helper.runAfterDelay(5, () -> {
            MasterRitualStoneBlockEntity mrs = (MasterRitualStoneBlockEntity) helper.getBlockEntity(mrsPos);
            Ritual waterRitual = NVRituals.WATER.get();

            if (mrs.checkStructure(waterRitual)) {
                helper.fail("Water ritual should NOT validate with missing rune");
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void ritualStructureChecksAllRotations(GameTestHelper helper) {
        BlockPos mrsPos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(mrsPos, NVBlocks.MASTER_RITUAL_STONE.block().get().defaultBlockState());

        // Water ritual is symmetric so any rotation should work
        helper.setBlock(mrsPos.offset(1, 0, 1), runeBlock(EnumRuneType.WATER));
        helper.setBlock(mrsPos.offset(1, 0, -1), runeBlock(EnumRuneType.WATER));
        helper.setBlock(mrsPos.offset(-1, 0, -1), runeBlock(EnumRuneType.WATER));
        helper.setBlock(mrsPos.offset(-1, 0, 1), runeBlock(EnumRuneType.WATER));

        helper.runAfterDelay(5, () -> {
            MasterRitualStoneBlockEntity mrs = (MasterRitualStoneBlockEntity) helper.getBlockEntity(mrsPos);
            Ritual waterRitual = NVRituals.WATER.get();

            // Should pass for at least one direction
            boolean found = mrs.checkStructure(waterRitual);
            if (!found) {
                helper.fail("Symmetric ritual should validate in at least one rotation");
                return;
            }
            helper.succeed();
        });
    }
}
