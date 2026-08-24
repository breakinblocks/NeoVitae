package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.SpiritAccumulatorBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.AccumulatorContent;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

import java.util.List;

public final class SpiritAccumulatorTests {

    private SpiritAccumulatorTests() {}

    private static SpiritAccumulatorBlockEntity place(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos.below(), Blocks.STONE.defaultBlockState());
        helper.setBlock(pos, NVBlocks.SPIRIT_ACCUMULATOR.block().get().defaultBlockState());
        SpiritAccumulatorBlockEntity be = helper.getBlockEntity(pos, SpiritAccumulatorBlockEntity.class);
        if (be == null) {
            helper.fail("Expected SpiritAccumulatorBlockEntity at " + pos);
        }
        return be;
    }

    public static void register(NVTestRegistrar r) {
        r.addIsolated("spirit_accumulator/starts_unattuned_and_unlocked", 40, helper -> {
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
        });

        r.addIsolated("spirit_accumulator/cycles_and_locks", 40, helper -> {
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
        });

        r.addIsolated("spirit_accumulator/unlocked_does_not_draw", 120, helper -> {
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
        });

        r.addIsolated("spirit_accumulator/no_fill_below_saturation", 120, helper -> {
            BlockPos pos = new BlockPos(3, 1, 2);
            SpiritAccumulatorBlockEntity be = place(helper, pos);

            helper.runAfterDelay(40, () -> {
                if (be == null) return;
                BlockPos worldPos = helper.absolutePos(pos);
                WorldSpiritusHandler.drainSpiritusFromChunk(helper.getLevel(), worldPos, SpiritusType.NIHILUM, 1_000_000);
                WorldSpiritusHandler.fillSpiritusToAmount(helper.getLevel(), worldPos, SpiritusType.NIHILUM, 20);
                be.attuneTo(SpiritusType.NIHILUM);
                double storedBefore = be.getStored();

                helper.runAfterDelay(5, () -> {
                    double chunkAmount = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), worldPos, SpiritusType.NIHILUM);
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
        });

        r.addIsolated("spirit_accumulator/fills_from_burnt_crystal", 120, helper -> {
            BlockPos pos = new BlockPos(3, 1, 2);
            SpiritAccumulatorBlockEntity be = place(helper, pos);

            helper.runAfterDelay(40, () -> {
                if (be == null) return;
                BlockPos worldPos = helper.absolutePos(pos);
                WorldSpiritusHandler.drainSpiritusFromChunk(helper.getLevel(), worldPos, SpiritusType.VINDICTA, 1_000_000);
                WorldSpiritusHandler.fillSpiritusToAmount(helper.getLevel(), worldPos, SpiritusType.VINDICTA, 50);
                be.attuneTo(SpiritusType.VINDICTA);
                double storedBefore = be.getStored();

                helper.runAfterDelay(5, () -> {
                    double taken = be.getStored() - storedBefore;
                    if (taken <= 0) {
                        helper.fail("Accumulator should fill from a chunk above the floor, stored=" + be.getStored()
                                + " chunk=" + WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), worldPos, SpiritusType.VINDICTA));
                        return;
                    }
                    double headroom = 50 - SpiritAccumulatorBlockEntity.SATURATION_FLOOR;
                    if (taken > headroom + 0.0001) {
                        helper.fail("Accumulator took " + taken + " from a chunk holding only " + headroom
                                + " above the saturation floor");
                        return;
                    }
                    helper.succeed();
                });
            });
        });

        r.addIsolated("spirit_accumulator/crystal_locks_without_being_consumed", 60, helper -> {
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
                InteractionResult result = state.useItemOn(player.getItemInHand(InteractionHand.MAIN_HAND),
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
        });

        r.addIsolated("spirit_accumulator/break_keeps_contents", 40, helper -> {
            BlockPos pos = new BlockPos(3, 1, 2);
            SpiritAccumulatorBlockEntity be = place(helper, pos);

            helper.runAfterDelay(1, () -> {
                if (be == null) return;
                be.attuneTo(SpiritusType.NIHILUM);
                be.insertSpiritus(SpiritusType.NIHILUM, 250);

                BlockPos worldPos = helper.absolutePos(pos);
                BlockState state = helper.getLevel().getBlockState(worldPos);
                List<ItemStack> drops = Block.getDrops(state, helper.getLevel(), worldPos, be, null, ItemStack.EMPTY);
                if (drops.size() != 1) {
                    helper.fail("Breaking should drop exactly one accumulator, got " + drops.size());
                    return;
                }

                AccumulatorContent content = drops.getFirst().get(NVDataComponents.ACCUMULATOR_CONTENT.get());
                if (content == null) {
                    helper.fail("The dropped accumulator should carry its contents");
                    return;
                }
                if (content.typeOrNull() != SpiritusType.NIHILUM || content.stored() != 250 || !content.locked()) {
                    helper.fail("Dropped contents wrong: type=" + content.typeOrNull()
                            + " stored=" + content.stored() + " locked=" + content.locked());
                    return;
                }

                helper.setBlock(pos, Blocks.AIR.defaultBlockState());
                helper.setBlock(pos, NVBlocks.SPIRIT_ACCUMULATOR.block().get().defaultBlockState());
                SpiritAccumulatorBlockEntity replaced = helper.getBlockEntity(pos, SpiritAccumulatorBlockEntity.class);
                if (replaced == null) {
                    helper.fail("Expected a replaced accumulator");
                    return;
                }
                replaced.applyComponentsFromItemStack(drops.getFirst());
                if (replaced.getAttunedType() != SpiritusType.NIHILUM || replaced.getStored() != 250 || !replaced.isLocked()) {
                    helper.fail("Replacing should restore contents: type=" + replaced.getAttunedType()
                            + " stored=" + replaced.getStored() + " locked=" + replaced.isLocked());
                    return;
                }
                helper.succeed();
            });
        });

        r.addIsolated("spirit_accumulator/break_while_empty_drops_plain_item", 40, helper -> {
            BlockPos pos = new BlockPos(3, 1, 2);
            SpiritAccumulatorBlockEntity be = place(helper, pos);

            helper.runAfterDelay(1, () -> {
                if (be == null) return;
                BlockPos worldPos = helper.absolutePos(pos);
                BlockState state = helper.getLevel().getBlockState(worldPos);
                List<ItemStack> drops = Block.getDrops(state, helper.getLevel(), worldPos, be, null, ItemStack.EMPTY);
                if (drops.size() != 1) {
                    helper.fail("Breaking should drop exactly one accumulator, got " + drops.size());
                    return;
                }
                if (drops.getFirst().has(NVDataComponents.ACCUMULATOR_CONTENT.get())) {
                    helper.fail("An untouched accumulator should drop without contents attached");
                    return;
                }
                helper.succeed();
            });
        });

        r.addIsolated("spirit_accumulator/empties_and_retunes", 40, helper -> {
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
        });
    }
}
