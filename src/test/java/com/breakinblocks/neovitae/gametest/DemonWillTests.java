package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.block.BlockDemonCrystal;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.VasMaleficumBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.DemonCrystalBlockEntity;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.will.WillChunk;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.will.WorldDemonWillHandler;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class DemonWillTests {

    private static void setChunkWill(GameTestHelper helper, BlockPos relativePos, double amount) {
        BlockPos absPos = helper.absolutePos(relativePos);
        LevelChunk chunk = helper.getLevel().getChunkAt(absPos);
        chunk.setData(NVDataAttachments.WILL_CHUNK.get(), new WillChunk(amount, 0, 0, 0, 0));
        chunk.setUnsaved(true);
    }

    private static double getChunkWill(GameTestHelper helper, BlockPos relativePos) {
        BlockPos absPos = helper.absolutePos(relativePos);
        return WorldDemonWillHandler.getCurrentWill(helper.getLevel(), absPos, EnumWillType.DEFAULT);
    }

    // ==================== Crystal Growth ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 300)
    public void crystalGrowsWithChunkWill(GameTestHelper helper) {
        BlockPos crystalPos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(crystalPos, NVBlocks.RAW_DEMON_CRYSTAL.block().get().defaultBlockState());

        setChunkWill(helper, crystalPos, 100.0);

        helper.runAfterDelay(250, () -> {
            BlockEntity be = helper.getBlockEntity(crystalPos);
            if (!(be instanceof DemonCrystalBlockEntity crystal)) {
                helper.fail("Expected DemonCrystalBlockEntity");
                return;
            }

            if (crystal.progressToNextCrystal <= 0) {
                helper.fail("Crystal should have growth progress with chunk will present");
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void crystalDoesNotGrowWithoutWill(GameTestHelper helper) {
        BlockPos crystalPos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(crystalPos, NVBlocks.RAW_DEMON_CRYSTAL.block().get().defaultBlockState());

        setChunkWill(helper, crystalPos, 0.0);

        helper.runAfterDelay(40, () -> {
            BlockEntity be = helper.getBlockEntity(crystalPos);
            if (!(be instanceof DemonCrystalBlockEntity crystal)) {
                helper.fail("Expected DemonCrystalBlockEntity");
                return;
            }

            if (crystal.progressToNextCrystal > 0) {
                helper.fail("Crystal should not grow without chunk will");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void crystalDrainsChunkWill(GameTestHelper helper) {
        BlockPos crystalPos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(crystalPos, NVBlocks.RAW_DEMON_CRYSTAL.block().get().defaultBlockState());

        setChunkWill(helper, crystalPos, 50.0);

        helper.runAfterDelay(40, () -> {
            double remaining = getChunkWill(helper, crystalPos);
            if (remaining >= 50.0) {
                helper.fail("Crystal should drain chunk will, but it's still " + remaining);
            }
            helper.succeed();
        });
    }

    // ==================== Crucible ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void cruciblePlacesAndInitializes(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(new BlockPos(3, 1, 2), NVBlocks.VAS_MALEFICUM.block().get().defaultBlockState());

        helper.runAfterDelay(5, () -> {
            BlockEntity be = helper.getBlockEntity(new BlockPos(3, 1, 2));
            if (!(be instanceof VasMaleficumBlockEntity)) {
                helper.fail("Expected VasMaleficumBlockEntity");
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 100)
    public void crucibleDrainsGemToChunk(GameTestHelper helper) {
        BlockPos cruciblePos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(cruciblePos, NVBlocks.VAS_MALEFICUM.block().get().defaultBlockState());

        helper.runAfterDelay(1, () -> {
            VasMaleficumBlockEntity crucible = (VasMaleficumBlockEntity) helper.getBlockEntity(cruciblePos);

            ItemStack gem = new ItemStack(NVItems.SOUL_GEM_PETTY.get());
            gem.set(NVDataComponents.DEMON_WILL_AMOUNT, 50.0);
            crucible.handleInteraction(gem);

            double willBefore = getChunkWill(helper, cruciblePos);

            helper.runAfterDelay(60, () -> {
                double willAfter = getChunkWill(helper, cruciblePos);
                if (willAfter <= willBefore) {
                    helper.fail("Crucible should drain gem will into chunk, but will didn't increase (before=" + willBefore + " after=" + willAfter + ")");
                }
                helper.succeed();
            });
        });
    }
}
