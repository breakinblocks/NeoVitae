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
    public void accumulatorStartsUnattunedAndUnlocked(GameTestHelper helper) {
        SpiritAccumulatorBlockEntity be = place(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (be == null) return;
            if (be.getAttunedType() != null || be.isLocked()) {
                helper.fail("A placed accumulator should be unattuned and unlocked, type="
                        + be.getAttunedType() + " locked=" + be.isLocked());
                return;
            }
            if (be.insertSpiritus(SpiritusType.RUINA, 50)) {
                helper.fail("An unattuned accumulator must not accept spiritus");
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void accumulatorCyclesAndLocks(GameTestHelper helper) {
        SpiritAccumulatorBlockEntity be = place(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (be == null) return;
            if (be.cycleAttunement() != SpiritusType.RAW) {
                helper.fail("First cycle should select the first aspect, got " + be.getAttunedType());
                return;
            }
            if (be.cycleAttunement() != SpiritusType.RUINA) {
                helper.fail("Second cycle should advance the aspect, got " + be.getAttunedType());
                return;
            }
            if (!be.lock()) {
                helper.fail("Locking an attuned accumulator should succeed");
                return;
            }
            if (be.cycleAttunement() != SpiritusType.RUINA) {
                helper.fail("A locked accumulator must not cycle, got " + be.getAttunedType());
                return;
            }
            if (!be.canAccept(SpiritusType.RUINA) || be.canAccept(SpiritusType.NIHILUM)) {
                helper.fail("A locked accumulator should accept only its own aspect");
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 120)
    public void accumulatorUnlockedDoesNotDraw(GameTestHelper helper) {
        BlockPos pos = new BlockPos(3, 1, 2);
        SpiritAccumulatorBlockEntity be = place(helper, pos);

        helper.runAfterDelay(40, () -> {
            if (be == null) return;
            BlockPos worldPos = helper.absolutePos(pos);
            WorldSpiritusHandler.fillSpiritusToAmount(helper.getLevel(), worldPos, SpiritusType.RUINA, 100);
            be.cycleAttunement();
            be.cycleAttunement();

            helper.runAfterDelay(5, () -> {
                if (be.getStored() > 0) {
                    helper.fail("An unlocked accumulator must not draw from the chunk, stored=" + be.getStored());
                    return;
                }
                helper.succeed();
            });
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
            WorldSpiritusHandler.fillSpiritusToAmount(helper.getLevel(), worldPos, SpiritusType.RUINA, 20);
            be.attuneTo(SpiritusType.RUINA);
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
    public void accumulatorFillsFromBurntCrystal(GameTestHelper helper) {
        BlockPos pos = new BlockPos(3, 1, 2);
        SpiritAccumulatorBlockEntity be = place(helper, pos);

        helper.runAfterDelay(40, () -> {
            if (be == null) return;
            BlockPos worldPos = helper.absolutePos(pos);
            WorldSpiritusHandler.drainSpiritusFromChunk(helper.getLevel(), worldPos, SpiritusType.RUINA, 1_000_000);
            WorldSpiritusHandler.fillSpiritusToAmount(helper.getLevel(), worldPos, SpiritusType.RUINA, 50);
            be.attuneTo(SpiritusType.RUINA);
            double storedBefore = be.getStored();

            helper.runAfterDelay(5, () -> {
                double chunkAmount = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), worldPos, SpiritusType.RUINA);
                if (be.getStored() <= storedBefore) {
                    helper.fail("Accumulator should fill from a chunk above the floor, stored=" + be.getStored()
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
    public void accumulatorCrystalLocksWithoutBeingConsumed(GameTestHelper helper) {
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
            if (be.getAttunedType() != SpiritusType.RUINA || !be.isLocked()) {
                helper.fail("Shard use should attune and lock the accumulator, type=" + be.getAttunedType()
                        + " locked=" + be.isLocked());
                return;
            }
            if (be.getStored() != 0) {
                helper.fail("A shard should not deposit spiritus, stored=" + be.getStored());
                return;
            }
            if (player.getItemInHand(InteractionHand.MAIN_HAND).getCount() != 2) {
                helper.fail("Shard should not be consumed, count=" + player.getItemInHand(InteractionHand.MAIN_HAND).getCount());
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
            be.attuneTo(SpiritusType.VINDICTA);
            be.insertSpiritus(SpiritusType.VINDICTA, 50);
            if (be.unlock()) {
                helper.fail("A stocked accumulator should not unlock");
                return;
            }
            be.vent(SpiritAccumulatorBlockEntity.CAPACITY);
            if (be.getStored() != 0) {
                helper.fail("Venting should empty the accumulator, stored=" + be.getStored());
                return;
            }
            if (!be.unlock() || be.isLocked()) {
                helper.fail("An emptied accumulator should unlock");
                return;
            }
            if (be.cycleAttunement() != null || be.cycleAttunement() != SpiritusType.RAW) {
                helper.fail("An unlocked accumulator should cycle onwards, type=" + be.getAttunedType());
                return;
            }
            helper.succeed();
        });
    }
}
