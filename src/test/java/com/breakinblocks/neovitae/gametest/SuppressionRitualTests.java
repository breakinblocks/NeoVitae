package com.breakinblocks.neovitae.gametest;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import com.breakinblocks.neovitae.api.sigil.effects.SuppressionSigilEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.SpectralBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.NVRituals;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualHelper;
import com.breakinblocks.neovitae.ritual.types.RitualSuppression;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class SuppressionRitualTests {

    private SuppressionRitualTests() {}

    private static final BlockPos MRS_POS = new BlockPos(12, 1, 12);
    private static final BlockPos CHEST_POS = new BlockPos(18, 2, 12);
    private static final UUID OWNER = UUID.fromString("c41e7a92-5b3d-4f08-9e26-7d1a8f3b6c05");

    public static void register(NVTestRegistrar r) {
        r.add("suppression/sigil_leaves_waterlogged_blocks_alone", 40, SuppressionRitualTests::sigilLeavesWaterloggedBlocksAlone);
        r.addIsolated("suppression/dome_holds_water_back", 200, SuppressionRitualTests::domeHoldsWaterBack);
        r.addIsolated("suppression/stopping_restores_water", 200, SuppressionRitualTests::stoppingRestoresWater);
        r.add("suppression/spectral_block_keeps_water_out", 40, SuppressionRitualTests::spectralBlockKeepsWaterOut);
    }

    private static void domeHoldsWaterBack(GameTestHelper helper) {
        MasterRitualStoneBlockEntity mrs = placeMrs(helper);
        List<BlockPos> water = buildTank(helper);
        helper.setBlock(CHEST_POS, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.WATERLOGGED, true));
        ChestBlockEntity chest = helper.getBlockEntity(CHEST_POS, ChestBlockEntity.class);
        chest.setItem(0, new ItemStack(Items.DIAMOND));

        helper.runAtTickTime(60, () -> assertDome(helper, mrs, water));
        helper.runAtTickTime(160, () -> {
            assertDome(helper, mrs, water);
            helper.assertBlockPresent(Blocks.CHEST, CHEST_POS);
            ChestBlockEntity kept = helper.getBlockEntity(CHEST_POS, ChestBlockEntity.class);
            helper.assertTrue(kept.getItem(0).is(Items.DIAMOND), "A waterlogged chest inside the dome should keep its contents");
            helper.succeed();
        });
    }

    private static void stoppingRestoresWater(GameTestHelper helper) {
        MasterRitualStoneBlockEntity mrs = placeMrs(helper);
        List<BlockPos> water = buildTank(helper);
        Set<BlockPos> dome = new HashSet<>();

        helper.runAtTickTime(40, () -> {
            dome.addAll(domePositions(mrs));
            mrs.stopRitual(Ritual.BreakType.DEACTIVATE);
        });
        helper.runAtTickTime(120, () -> {
            for (BlockPos rel : water) {
                if (!dome.contains(helper.absolutePos(rel))) continue;
                FluidState fluid = helper.getLevel().getFluidState(helper.absolutePos(rel));
                helper.assertTrue(fluid.is(Fluids.WATER) && fluid.isSource(),
                        "Water at " + rel + " should come back once the ritual stops");
            }
            helper.succeed();
        });
    }

    private static void spectralBlockKeepsWaterOut(GameTestHelper helper) {
        BlockPos spectralPos = new BlockPos(2, 2, 2);
        for (int x = 1; x <= 3; x++) {
            helper.setBlock(new BlockPos(x, 1, 2), Blocks.GLASS);
        }
        helper.setBlock(spectralPos.east(2), Blocks.GLASS);
        helper.setBlock(spectralPos.east().north(), Blocks.GLASS);
        helper.setBlock(spectralPos.east().south(), Blocks.GLASS);
        helper.setBlock(spectralPos, NVBlocks.SPECTRAL_BLOCK.get().defaultBlockState());
        SpectralBlockEntity spectral = helper.getBlockEntity(spectralPos, SpectralBlockEntity.class);
        spectral.resetDuration(SpectralBlockEntity.MAX_DURATION);
        helper.setBlock(spectralPos.east(), Blocks.WATER);

        helper.runAtTickTime(20, () -> {
            helper.assertBlockPresent(NVBlocks.SPECTRAL_BLOCK.get(), spectralPos);
            helper.assertTrue(helper.getLevel().getFluidState(helper.absolutePos(spectralPos)).isEmpty(),
                    "Water should not flow into a Spectral block");
            helper.succeed();
        });
    }

    private static void sigilLeavesWaterloggedBlocksAlone(GameTestHelper helper) {
        BlockPos waterPos = new BlockPos(1, 2, 2);
        BlockPos chestPos = new BlockPos(3, 2, 2);
        helper.setBlock(waterPos, Blocks.WATER);
        helper.setBlock(chestPos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.WATERLOGGED, true));
        ChestBlockEntity chest = helper.getBlockEntity(chestPos, ChestBlockEntity.class);
        chest.setItem(0, new ItemStack(Items.DIAMOND));
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos center = helper.absolutePos(new BlockPos(2, 2, 2));
        player.setPos(center.getX() + 0.5, center.getY(), center.getZ() + 0.5);

        new SuppressionSigilEffect(2, 2).activeTick(helper.getLevel(), player, ItemStack.EMPTY, 0, true);

        helper.assertBlockPresent(NVBlocks.SPECTRAL_BLOCK.get(), waterPos);
        helper.assertBlockPresent(Blocks.CHEST, chestPos);
        helper.assertTrue(helper.getBlockState(chestPos).getValue(ChestBlock.WATERLOGGED),
                "The sigil should leave a waterlogged chest waterlogged");
        ChestBlockEntity kept = helper.getBlockEntity(chestPos, ChestBlockEntity.class);
        helper.assertTrue(kept == chest && kept.getItem(0).is(Items.DIAMOND),
                "The sigil should not replace a waterlogged chest or touch its contents");
        helper.succeed();
    }

    private static void assertDome(GameTestHelper helper, MasterRitualStoneBlockEntity mrs, List<BlockPos> water) {
        Set<BlockPos> dome = domePositions(mrs);
        int inside = 0;
        int outside = 0;
        for (BlockPos rel : water) {
            BlockPos abs = helper.absolutePos(rel);
            FluidState fluid = helper.getLevel().getFluidState(abs);
            if (dome.contains(abs)) {
                inside++;
                helper.assertTrue(fluid.isEmpty(), "Water inside the dome at " + rel + " should be held back");
                helper.assertBlockPresent(NVBlocks.SPECTRAL_BLOCK.get(), rel);
            } else {
                outside++;
                helper.assertTrue(fluid.is(Fluids.WATER), "Water outside the dome at " + rel + " should stay");
            }
        }
        helper.assertTrue(inside > 0 && outside > 0, "The tank should straddle the dome edge, inside " + inside + ", outside " + outside);
    }

    private static Set<BlockPos> domePositions(MasterRitualStoneBlockEntity mrs) {
        List<BlockPos> positions = RitualHelper.getRangePositions(mrs, mrs.getCurrentRitual(),
                RitualSuppression.SUPPRESS_RANGE, mrs.getBlockPos());
        Set<BlockPos> set = new HashSet<>();
        for (BlockPos pos : positions) {
            set.add(pos.immutable());
        }
        return set;
    }

    private static List<BlockPos> buildTank(GameTestHelper helper) {
        for (int x = 16; x <= 23; x++) {
            for (int z = 10; z <= 14; z++) {
                helper.setBlock(new BlockPos(x, 1, z), Blocks.GLASS);
                boolean wall = x == 16 || x == 23 || z == 10 || z == 14;
                for (int y = 2; y <= 3; y++) {
                    if (wall) {
                        helper.setBlock(new BlockPos(x, y, z), Blocks.GLASS);
                    }
                }
            }
        }
        List<BlockPos> water = new ArrayList<>();
        for (int x = 17; x <= 22; x++) {
            for (int z = 11; z <= 13; z++) {
                for (int y = 2; y <= 3; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (pos.equals(CHEST_POS)) continue;
                    helper.setBlock(pos, Blocks.WATER);
                    water.add(pos);
                }
            }
        }
        return water;
    }

    private static MasterRitualStoneBlockEntity placeMrs(GameTestHelper helper) {
        Ritual ritual = NVRituals.SUPPRESSION.get();
        helper.setBlock(MRS_POS.below(), Blocks.STONE.defaultBlockState());
        helper.setBlock(MRS_POS, NVBlocks.MASTER_RITUAL_STONE.block().get().defaultBlockState());
        List<RitualComponent> components = new ArrayList<>();
        ritual.gatherComponents(components::add);
        for (RitualComponent component : components) {
            helper.setBlock(MRS_POS.offset(component.offset()), runeBlock(component.runeType()).defaultBlockState());
        }
        MasterRitualStoneBlockEntity mrs = helper.getBlockEntity(MRS_POS, MasterRitualStoneBlockEntity.class);
        Anima anima = AnimaHelper.getAnima(OWNER);
        anima.add(AnimaTicket.create(1000000), 10000000);
        mrs.forceActivateRitual(ritual, null);
        mrs.setOwner(OWNER);
        return mrs;
    }

    private static Block runeBlock(EnumRuneType type) {
        return switch (type) {
            case BLANK -> NVBlocks.BLANK_RITUAL_STONE.block().get();
            case WATER -> NVBlocks.WATER_RITUAL_STONE.block().get();
            case FIRE -> NVBlocks.FIRE_RITUAL_STONE.block().get();
            case EARTH -> NVBlocks.EARTH_RITUAL_STONE.block().get();
            case AIR -> NVBlocks.AIR_RITUAL_STONE.block().get();
            case TENEBRAE -> NVBlocks.TENEBRAE_RITUAL_STONE.block().get();
            case DEUS -> NVBlocks.DEUS_RITUAL_STONE.block().get();
        };
    }
}
