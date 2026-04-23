package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.SpiritusCrystalBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.VasMaleficumBlockEntity;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.will.SpiritusChunk;
import com.breakinblocks.neovitae.will.WorldSpiritusHandler;

public final class SpiritusTests {

    private SpiritusTests() {}

    private static void setChunkWill(GameTestHelper helper, BlockPos relativePos, double amount) {
        BlockPos absPos = helper.absolutePos(relativePos);
        LevelChunk chunk = helper.getLevel().getChunkAt(absPos);
        chunk.setData(NVDataAttachments.SPIRITUS_CHUNK.get(), new SpiritusChunk(amount, 0, 0, 0, 0));
        chunk.markUnsaved();
    }

    private static double getChunkWill(GameTestHelper helper, BlockPos relativePos) {
        BlockPos absPos = helper.absolutePos(relativePos);
        return WorldSpiritusHandler.getCurrentWill(helper.getLevel(), absPos, SpiritusType.DEFAULT);
    }

    public static void register(NVTestRegistrar r) {
        r.add("spiritus/crystal_grows_with_chunk_will", 300, helper -> {
            BlockPos crystalPos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(crystalPos, NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get().defaultBlockState());

            setChunkWill(helper, crystalPos, 100.0);

            helper.runAfterDelay(250, () -> {
                SpiritusCrystalBlockEntity crystal = helper.getBlockEntity(crystalPos, SpiritusCrystalBlockEntity.class);
                if (crystal == null) {
                    helper.fail("Expected SpiritusCrystalBlockEntity");
                    return;
                }
                if (crystal.progressToNextCrystal <= 0) {
                    helper.fail("Crystal should have growth progress with chunk will present");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("spiritus/crystal_does_not_grow_without_will", 60, helper -> {
            BlockPos crystalPos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(crystalPos, NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get().defaultBlockState());

            setChunkWill(helper, crystalPos, 0.0);

            helper.runAfterDelay(40, () -> {
                SpiritusCrystalBlockEntity crystal = helper.getBlockEntity(crystalPos, SpiritusCrystalBlockEntity.class);
                if (crystal == null) {
                    helper.fail("Expected SpiritusCrystalBlockEntity");
                    return;
                }
                if (crystal.progressToNextCrystal > 0) {
                    helper.fail("Crystal should not grow without chunk will, got " + crystal.progressToNextCrystal);
                }
                helper.succeed();
            });
        });

        r.add("spiritus/crystal_drains_chunk_will", 60, helper -> {
            BlockPos crystalPos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(crystalPos, NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get().defaultBlockState());

            setChunkWill(helper, crystalPos, 50.0);

            helper.runAfterDelay(40, () -> {
                double remaining = getChunkWill(helper, crystalPos);
                if (remaining >= 50.0) {
                    helper.fail("Crystal should drain chunk will, but it's still " + remaining);
                }
                helper.succeed();
            });
        });

        r.add("spiritus/crucible_places_and_initializes", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            BlockPos pos = new BlockPos(3, 1, 2);
            helper.setBlock(pos, NVBlocks.VAS_MALEFICUM.block().get().defaultBlockState());

            helper.runAfterDelay(5, () -> {
                VasMaleficumBlockEntity crucible = helper.getBlockEntity(pos, VasMaleficumBlockEntity.class);
                if (crucible == null) {
                    helper.fail("Expected VasMaleficumBlockEntity");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("spiritus/crucible_drains_gem_to_chunk", 100, helper -> {
            BlockPos cruciblePos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(cruciblePos, NVBlocks.VAS_MALEFICUM.block().get().defaultBlockState());

            helper.runAfterDelay(1, () -> {
                VasMaleficumBlockEntity crucible = helper.getBlockEntity(cruciblePos, VasMaleficumBlockEntity.class);
                if (crucible == null) {
                    helper.fail("No crucible");
                    return;
                }

                ItemStack gem = new ItemStack(NVItems.SPIRITUS_GEM_PETTY.get());
                gem.set(NVDataComponents.SPIRITUS_AMOUNT, 50.0);
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
        });
    }
}
