package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.blockentity.VitaeLinkBlockEntity;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;

public final class VitaeLinkTests {

    private VitaeLinkTests() {}

    private static AraVitaeTile placeAltar(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.ARA_VITAE.block().get().defaultBlockState());
        AraVitaeTile altar = helper.getBlockEntity(pos, AraVitaeTile.class);
        if (altar == null) {
            helper.fail("Expected AraVitaeTile at " + pos);
        }
        return altar;
    }

    private static VitaeLinkBlockEntity placeLink(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.VITAE_LINK.block().get().defaultBlockState());
        VitaeLinkBlockEntity link = helper.getBlockEntity(pos, VitaeLinkBlockEntity.class);
        if (link == null) {
            helper.fail("Expected VitaeLinkBlockEntity at " + pos);
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

    public static void register(NVTestRegistrar r) {
        r.add("vitae_link/binds_to_nearby_altar", 80, helper -> {
            BlockPos altarPos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            placeAltar(helper, altarPos);
            VitaeLinkBlockEntity link = placeLink(helper, new BlockPos(1, 1, 2));

            helper.runAfterDelay(20, () -> {
                if (link == null) return;
                if (!link.isLinked()) {
                    helper.fail("Link should bind to an altar within 8 blocks");
                    return;
                }
                BlockPos bound = link.getAltarPos();
                if (bound == null || !bound.equals(helper.absolutePos(altarPos))) {
                    helper.fail("Link bound to " + bound + ", expected " + helper.absolutePos(altarPos));
                    return;
                }
                helper.succeed();
            });
        });

        r.add("vitae_link/no_craft_tier_zero", 80, helper -> {
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
        });

        r.add("vitae_link/caps_at_tier_below", 600, helper -> {
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

                helper.runAfterDelay(400, () -> {
                    ItemStack out = link.inv.getStackInSlot(VitaeLinkBlockEntity.OUTPUT_SLOT);
                    if (!out.is(NVItems.TABULA_RASA.get())) {
                        helper.fail("Link capped at tier 0 should eject Tabula Rasa to output, got " + out
                                + " (in=" + link.inv.getStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT) + ")");
                        return;
                    }
                    helper.succeed();
                });
            });
        });

        r.add("vitae_link/rejects_input_while_output_occupied", 60, helper -> {
            BlockPos altarPos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            placeAltar(helper, altarPos);
            VitaeLinkBlockEntity link = placeLink(helper, new BlockPos(1, 1, 2));

            helper.runAfterDelay(5, () -> {
                if (link == null) return;
                link.inv.setStackInSlot(VitaeLinkBlockEntity.OUTPUT_SLOT, new ItemStack(NVItems.TABULA_RASA.get()));

                int blocked;
                try (Transaction tx = Transaction.openRoot()) {
                    blocked = link.inv.insert(VitaeLinkBlockEntity.INPUT_SLOT,
                            ItemResource.of(new ItemStack(Items.DEEPSLATE)), 1, tx);
                    tx.commit();
                }
                if (blocked != 0 || !link.inv.getStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT).isEmpty()) {
                    helper.fail("Input must be rejected while output is occupied; inserted=" + blocked);
                    return;
                }

                link.inv.setStackInSlot(VitaeLinkBlockEntity.OUTPUT_SLOT, ItemStack.EMPTY);
                int allowed;
                try (Transaction tx = Transaction.openRoot()) {
                    allowed = link.inv.insert(VitaeLinkBlockEntity.INPUT_SLOT,
                            ItemResource.of(new ItemStack(Items.DEEPSLATE)), 1, tx);
                    tx.commit();
                }
                if (allowed != 1 || link.inv.getStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT).isEmpty()) {
                    helper.fail("Input must accept once output is clear; inserted=" + allowed);
                    return;
                }
                helper.succeed();
            });
        });
    }
}
