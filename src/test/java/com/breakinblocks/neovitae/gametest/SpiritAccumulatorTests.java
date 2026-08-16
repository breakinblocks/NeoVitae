package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.SpiritAccumulatorBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class SpiritAccumulatorTests {

    private static SpiritAccumulatorBlockEntity place(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos.below(), Blocks.STONE.defaultBlockState());
        helper.setBlock(pos, NVBlocks.SPIRIT_ACCUMULATOR.block().get().defaultBlockState());
        BlockEntity be = helper.getBlockEntity(pos);
        if (!(be instanceof SpiritAccumulatorBlockEntity accumulator)) {
            helper.fail("Expected SpiritAccumulatorBlockEntity at " + pos);
            return null;
        }
        return accumulator;
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void accumulatorAttunesAndLocks(GameTestHelper helper) {
        SpiritAccumulatorBlockEntity be = place(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (be == null) return;
            if (!be.insertSpiritus(SpiritusType.RUINA, 50)) {
                helper.fail("Fresh accumulator should accept any type");
                return;
            }
            if (be.getAttunedType() != SpiritusType.RUINA) {
                helper.fail("Accumulator should attune to the inserted type, got " + be.getAttunedType());
                return;
            }
            if (be.canAccept(SpiritusType.NIHILUM)) {
                helper.fail("Attuned accumulator must reject other types");
                return;
            }
            if (!be.canAccept(SpiritusType.RUINA)) {
                helper.fail("Attuned accumulator must keep accepting its own type");
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 120)
    public void accumulatorNoFillBelowSaturation(GameTestHelper helper) {
        BlockPos pos = new BlockPos(3, 1, 2);
        SpiritAccumulatorBlockEntity be = place(helper, pos);

        helper.runAfterDelay(40, () -> {
            if (be == null) return;
            BlockPos worldPos = helper.absolutePos(pos);
            WorldSpiritusHandler.drainSpiritusFromChunk(helper.getLevel(), worldPos, SpiritusType.RUINA, 1_000_000);
            WorldSpiritusHandler.fillSpiritusToAmount(helper.getLevel(), worldPos, SpiritusType.RUINA, 50);
            be.insertSpiritus(SpiritusType.RUINA, 50);
            double storedBefore = be.getStored();

            helper.runAfterDelay(5, () -> {
                double chunkAmount = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), worldPos, SpiritusType.RUINA);
                if (chunkAmount > SpiritAccumulatorBlockEntity.SATURATION_FLOOR) {
                    helper.fail("Neighbouring test raised the chunk to " + chunkAmount + "; cannot measure the floor");
                    return;
                }
                if (be.getStored() > storedBefore) {
                    helper.fail("Accumulator filled from an unsaturated chunk, stored=" + be.getStored());
                    return;
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 120)
    public void accumulatorFillsFromSaturatedChunk(GameTestHelper helper) {
        BlockPos pos = new BlockPos(3, 1, 2);
        SpiritAccumulatorBlockEntity be = place(helper, pos);

        helper.runAfterDelay(40, () -> {
            if (be == null) return;
            BlockPos worldPos = helper.absolutePos(pos);
            WorldSpiritusHandler.fillSpiritusToAmount(helper.getLevel(), worldPos, SpiritusType.RUINA, 100);
            be.insertSpiritus(SpiritusType.RUINA, 50);
            double storedBefore = be.getStored();

            helper.runAfterDelay(5, () -> {
                double chunkAmount = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), worldPos, SpiritusType.RUINA);
                if (be.getStored() <= storedBefore) {
                    helper.fail("Accumulator should fill from a saturated chunk, stored=" + be.getStored()
                            + " chunk=" + chunkAmount);
                    return;
                }
                if (chunkAmount < SpiritAccumulatorBlockEntity.SATURATION_FLOOR - 0.0001) {
                    helper.fail("Accumulator drained the chunk below the saturation floor, chunk=" + chunkAmount);
                    return;
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void accumulatorShardUseAttunes(GameTestHelper helper) {
        BlockPos pos = new BlockPos(3, 1, 2);
        SpiritAccumulatorBlockEntity be = place(helper, pos);

        helper.runAfterDelay(1, () -> {
            if (be == null) return;
            Player player = helper.makeMockPlayer(GameType.SURVIVAL);
            ItemStack shard = new ItemStack(NVItems.SPIRITUS_RUINA_CRYSTAL_ITEM.get(), 2);
            player.setItemInHand(InteractionHand.MAIN_HAND, shard);

            BlockPos worldPos = helper.absolutePos(pos);
            BlockState state = helper.getLevel().getBlockState(worldPos);
            BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(worldPos), Direction.NORTH, worldPos, false);
            ItemInteractionResult result = state.useItemOn(player.getItemInHand(InteractionHand.MAIN_HAND),
                    helper.getLevel(), player, InteractionHand.MAIN_HAND, hit);

            if (!result.consumesAction()) {
                helper.fail("Using a shard on the accumulator should succeed, got " + result);
                return;
            }
            if (be.getAttunedType() != SpiritusType.RUINA) {
                helper.fail("Shard use should attune the accumulator, type=" + be.getAttunedType());
                return;
            }
            if (be.getStored() != 50) {
                helper.fail("One shard should add 50 spiritus, stored=" + be.getStored());
                return;
            }
            if (player.getItemInHand(InteractionHand.MAIN_HAND).getCount() != 1) {
                helper.fail("Shard should be consumed, count=" + player.getItemInHand(InteractionHand.MAIN_HAND).getCount());
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void accumulatorEmptiesAndRetunes(GameTestHelper helper) {
        SpiritAccumulatorBlockEntity be = place(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (be == null) return;
            be.insertSpiritus(SpiritusType.VINDICTA, 50);
            be.vent(SpiritAccumulatorBlockEntity.CAPACITY);
            if (be.getAttunedType() != null || be.getStored() != 0) {
                helper.fail("Fully vented accumulator should clear its attunement, type="
                        + be.getAttunedType() + " stored=" + be.getStored());
                return;
            }
            if (!be.insertSpiritus(SpiritusType.NIHILUM, 50) || be.getAttunedType() != SpiritusType.NIHILUM) {
                helper.fail("Emptied accumulator should re-attune to a new type");
                return;
            }
            helper.succeed();
        });
    }
}
