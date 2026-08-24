package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.blockentity.VitaeLinkBlockEntity;
import com.breakinblocks.neovitae.common.item.NVItems;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class VitaeLinkTests {

    private static AraVitaeTile placeAltar(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.ARA_VITAE.block().get().defaultBlockState());
        BlockEntity be = helper.getBlockEntity(pos);
        if (!(be instanceof AraVitaeTile altar)) {
            helper.fail("Expected AraVitaeTile at " + pos);
            return null;
        }
        return altar;
    }

    private static VitaeLinkBlockEntity placeLink(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.VITAE_LINK.block().get().defaultBlockState());
        BlockEntity be = helper.getBlockEntity(pos);
        if (!(be instanceof VitaeLinkBlockEntity link)) {
            helper.fail("Expected VitaeLinkBlockEntity at " + pos);
            return null;
        }
        return link;
    }

    private static void buildApprenticeRing(GameTestHelper helper, BlockPos altarPos) {
        BlockState rune = NVBlocks.RUNE_BLANK.block().get().defaultBlockState();
        helper.setBlock(altarPos.offset(1, -1, 0), rune);
        helper.setBlock(altarPos.offset(-1, -1, 0), rune);
        helper.setBlock(altarPos.offset(0, -1, 1), rune);
        helper.setBlock(altarPos.offset(0, -1, -1), rune);
        helper.setBlock(altarPos.offset(1, -1, 1), rune);
        helper.setBlock(altarPos.offset(1, -1, -1), rune);
        helper.setBlock(altarPos.offset(-1, -1, 1), rune);
        helper.setBlock(altarPos.offset(-1, -1, -1), rune);
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 80)
    public void linkBindsToNearbyAltar(GameTestHelper helper) {
        BlockPos altarPos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        placeAltar(helper, altarPos);
        VitaeLinkBlockEntity link = placeLink(helper, new BlockPos(1, 1, 2));

        helper.succeedWhen(() -> {
            if (link == null) return;
            helper.assertTrue(link.isLinked(), "Link should bind to an altar within 8 blocks");
            BlockPos bound = link.getAltarPos();
            helper.assertTrue(!(bound == null || !bound.equals(helper.absolutePos(altarPos))), "Link bound to " + bound + ", expected " + helper.absolutePos(altarPos));
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 80)
    public void linkDoesNotCraftWithTierZeroAltar(GameTestHelper helper) {
        BlockPos altarPos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AraVitaeTile altar = placeAltar(helper, altarPos);
        VitaeLinkBlockEntity link = placeLink(helper, new BlockPos(1, 1, 2));

        helper.runAfterDelay(5, () -> {
            if (altar == null || link == null) return;
            altar.addSacrificeEV(5000, false);
            link.inv.setStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT, new ItemStack(Items.DEEPSLATE));

            helper.runAfterDelay(60, () -> {
                ItemStack in = link.inv.getStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT);
                ItemStack out = link.inv.getStackInSlot(VitaeLinkBlockEntity.OUTPUT_SLOT);
                if (!in.is(Items.DEEPSLATE) || !out.isEmpty()) {
                    helper.fail("Link on a tier-0 altar (maxLinkTier -1) must not craft; in=" + in + " out=" + out);
                    return;
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 600)
    public void linkCapsAtTierBelowAltar(GameTestHelper helper) {
        BlockPos altarPos = new BlockPos(3, 2, 2);
        AraVitaeTile altar = placeAltar(helper, altarPos);
        buildApprenticeRing(helper, altarPos);
        VitaeLinkBlockEntity link = placeLink(helper, new BlockPos(1, 2, 2));

        helper.runAfterDelay(150, () -> {
            if (altar == null || link == null) return;
            if (altar.getTier() < 1) {
                helper.fail("Apprentice altar should be tier 1+, got " + altar.getTier());
                return;
            }
            if (link.getMaxLinkTier() != altar.getTier() - 1) {
                helper.fail("maxLinkTier should be altarTier-1 (" + (altar.getTier() - 1) + "), got " + link.getMaxLinkTier());
                return;
            }
            altar.addSacrificeEV(20000, false);
            link.inv.setStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT, new ItemStack(Items.DEEPSLATE));

            helper.succeedWhen(() -> {
                ItemStack out = link.inv.getStackInSlot(VitaeLinkBlockEntity.OUTPUT_SLOT);
                helper.assertTrue(!(!out.is(NVItems.TABULA_RASA.get())), "Link capped at tier 0 should eject Tabula Rasa to output, got " + out
                            + " (in=" + link.inv.getStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT) + ")");
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void linkRejectsInputWhileOutputOccupied(GameTestHelper helper) {
        BlockPos altarPos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        placeAltar(helper, altarPos);
        VitaeLinkBlockEntity link = placeLink(helper, new BlockPos(1, 1, 2));

        helper.runAfterDelay(5, () -> {
            if (link == null) return;
            link.inv.setStackInSlot(VitaeLinkBlockEntity.OUTPUT_SLOT, new ItemStack(NVItems.TABULA_RASA.get()));

            ItemStack remainder = link.inv.insertItem(VitaeLinkBlockEntity.INPUT_SLOT, new ItemStack(Items.DEEPSLATE), false);
            if (remainder.getCount() != 1 || !link.inv.getStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT).isEmpty()) {
                helper.fail("Input must be rejected while output is occupied; remainder=" + remainder
                        + " in=" + link.inv.getStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT));
                return;
            }

            link.inv.setStackInSlot(VitaeLinkBlockEntity.OUTPUT_SLOT, ItemStack.EMPTY);
            ItemStack remainder2 = link.inv.insertItem(VitaeLinkBlockEntity.INPUT_SLOT, new ItemStack(Items.DEEPSLATE), false);
            if (!remainder2.isEmpty() || link.inv.getStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT).isEmpty()) {
                helper.fail("Input must accept once output is clear; remainder=" + remainder2);
                return;
            }
            helper.succeed();
        });
    }
}
