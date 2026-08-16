package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.common.item.ItemRitualReader;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.ritual.EnumRitualReaderState;
import com.breakinblocks.neovitae.ritual.NVRituals;
import com.breakinblocks.neovitae.ritual.types.RitualHarvest;

public final class RitualReaderTests {

    private RitualReaderTests() {}

    private static MasterRitualStoneBlockEntity placeMrs(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos.below(), Blocks.STONE.defaultBlockState());
        helper.setBlock(pos, NVBlocks.MASTER_RITUAL_STONE.block().get().defaultBlockState());
        MasterRitualStoneBlockEntity mrs = helper.getBlockEntity(pos, MasterRitualStoneBlockEntity.class);
        if (mrs == null) {
            helper.fail("Expected MasterRitualStoneBlockEntity at " + pos);
        }
        return mrs;
    }

    private static void clickCorner(GameTestHelper helper, Player player, ItemStack stack, BlockPos worldPos) {
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(worldPos), Direction.UP, worldPos, false);
        stack.getItem().useOn(new UseOnContext(helper.getLevel(), player, InteractionHand.MAIN_HAND, stack, hit));
    }

    public static void register(NVTestRegistrar r) {
        r.add("ritual_reader/configures_the_stone_it_was_opened_on", 80, helper -> {
            BlockPos growthPos = new BlockPos(1, 1, 1);
            BlockPos harvestPos = new BlockPos(3, 1, 4);
            MasterRitualStoneBlockEntity growthMrs = placeMrs(helper, growthPos);
            MasterRitualStoneBlockEntity harvestMrs = placeMrs(helper, harvestPos);

            helper.runAfterDelay(5, () -> {
                if (growthMrs == null || harvestMrs == null) return;
                growthMrs.forceActivateRitual(NVRituals.GREEN_GROVE.get(), null);
                harvestMrs.forceActivateRitual(NVRituals.HARVEST.get(), null);

                Player player = helper.makeMockPlayer(GameType.SURVIVAL);
                Vec3 standAt = helper.absoluteVec(Vec3.atCenterOf(growthPos.above()));
                player.setPos(standAt.x, standAt.y, standAt.z);

                ItemStack stack = new ItemStack(NVItems.RITUAL_READER.get());
                ItemRitualReader reader = (ItemRitualReader) stack.getItem();
                reader.setRangeKey(stack, RitualHarvest.HARVEST_RANGE);
                reader.setMasterPos(stack, helper.absolutePos(harvestPos));
                reader.setState(stack, EnumRitualReaderState.SET_AREA_CORNER_1);
                player.setItemInHand(InteractionHand.MAIN_HAND, stack);

                clickCorner(helper, player, stack, helper.absolutePos(harvestPos.offset(1, 0, 1)));
                if (reader.getState(stack) != EnumRitualReaderState.SET_AREA_CORNER_2) {
                    helper.fail("First corner next to the harvest stone should be accepted, state=" + reader.getState(stack));
                    return;
                }

                clickCorner(helper, player, stack, helper.absolutePos(harvestPos.offset(2, 1, 2)));
                if (reader.getState(stack) != EnumRitualReaderState.INFORMATION) {
                    helper.fail("Second corner should complete the edit, state=" + reader.getState(stack));
                    return;
                }

                AreaDescriptor range = harvestMrs.getBlockRange(RitualHarvest.HARVEST_RANGE);
                if (range == null) {
                    helper.fail("Harvest stone should hold the edited range");
                    return;
                }
                AABB box = range.getAABB(BlockPos.ZERO);
                if (Math.round(box.getXsize()) != 2 || Math.round(box.getYsize()) != 2 || Math.round(box.getZsize()) != 2) {
                    helper.fail("Edited range should be the 2x2x2 box that was clicked, got "
                            + box.getXsize() + "x" + box.getYsize() + "x" + box.getZsize());
                    return;
                }
                helper.succeed();
            });
        });

        r.add("ritual_reader/falls_back_to_nearby_search_without_stored_stone", 80, helper -> {
            BlockPos harvestPos = new BlockPos(2, 41, 2);
            MasterRitualStoneBlockEntity harvestMrs = placeMrs(helper, harvestPos);

            helper.runAfterDelay(5, () -> {
                if (harvestMrs == null) return;
                harvestMrs.forceActivateRitual(NVRituals.HARVEST.get(), null);

                Player player = helper.makeMockPlayer(GameType.SURVIVAL);
                Vec3 standAt = helper.absoluteVec(Vec3.atCenterOf(harvestPos.above()));
                player.setPos(standAt.x, standAt.y, standAt.z);

                ItemStack stack = new ItemStack(NVItems.RITUAL_READER.get());
                ItemRitualReader reader = (ItemRitualReader) stack.getItem();
                reader.setRangeKey(stack, RitualHarvest.HARVEST_RANGE);
                reader.setState(stack, EnumRitualReaderState.SET_AREA_CORNER_1);
                player.setItemInHand(InteractionHand.MAIN_HAND, stack);

                clickCorner(helper, player, stack, helper.absolutePos(harvestPos.offset(1, 0, 1)));
                clickCorner(helper, player, stack, helper.absolutePos(harvestPos.offset(2, 1, 2)));

                if (reader.getState(stack) != EnumRitualReaderState.INFORMATION) {
                    helper.fail("Reader without a stored stone should still find the only nearby MRS, state="
                            + reader.getState(stack));
                    return;
                }
                helper.succeed();
            });
        });
    }
}
