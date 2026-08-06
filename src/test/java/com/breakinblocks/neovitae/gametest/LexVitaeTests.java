package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.soul.LexVitaeItem;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;

public final class LexVitaeTests {

    private LexVitaeTests() {}

    private static ItemStack activeLexVitae(int radius) {
        ItemStack stack = new ItemStack(NVItems.LEX_VITAE.get());
        stack.set(NVDataComponents.LEX_ACTIVE.get(), true);
        stack.set(NVDataComponents.LEX_RADIUS.get(), radius);
        return stack;
    }

    private static Player armedPlayer(GameTestHelper helper, int radius) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, activeLexVitae(radius));
        return player;
    }

    private static boolean postBreak(GameTestHelper helper, Player player, BlockPos absPos) {
        BlockState state = helper.getLevel().getBlockState(absPos);
        BreakBlockEvent event = new BreakBlockEvent(helper.getLevel(), absPos, state, player);
        NeoForge.EVENT_BUS.post(event);
        return event.isCanceled();
    }

    public static void register(NVTestRegistrar r) {
        r.add("lex_vitae/attack_blocks_mining", 60, helper -> {
            BlockPos pos = new BlockPos(2, 1, 2);
            helper.setBlock(pos, Blocks.STONE.defaultBlockState());
            Player player = armedPlayer(helper, 1);

            helper.runAfterDelay(1, () -> {
                LexVitaeItem.markEntityHit(player);
                if (!postBreak(helper, player, helper.absolutePos(pos))) {
                    helper.fail("Block break should be canceled right after hitting an entity");
                }
                helper.succeed();
            });
        });

        r.add("lex_vitae/mining_resumes_after_combat", 120, helper -> {
            BlockPos pos = new BlockPos(2, 1, 2);
            helper.setBlock(pos, Blocks.STONE.defaultBlockState());
            Player player = armedPlayer(helper, 1);

            helper.runAfterDelay(1, () -> LexVitaeItem.markEntityHit(player));

            helper.runAfterDelay(LexVitaeItem.ATTACK_BLOCKS_MINING_TICKS + 5, () -> {
                if (postBreak(helper, player, helper.absolutePos(pos))) {
                    helper.fail("Block break should be allowed once the combat window has passed");
                }
                helper.succeed();
            });
        });

        r.add("lex_vitae/aoe_skips_instant_break_block", 60, helper -> {
            BlockPos center = new BlockPos(2, 1, 2);
            BlockPos neighbor = new BlockPos(2, 2, 2);
            helper.setBlock(new BlockPos(2, 0, 2), Blocks.DIRT.defaultBlockState());
            helper.setBlock(center, Blocks.SHORT_GRASS.defaultBlockState());
            helper.setBlock(neighbor, Blocks.STONE.defaultBlockState());
            Player player = armedPlayer(helper, 1);

            helper.runAfterDelay(1, () -> {
                postBreak(helper, player, helper.absolutePos(center));
                helper.assertBlockPresent(Blocks.STONE, neighbor);
                helper.succeed();
            });
        });

        r.add("lex_vitae/mining_blocks_attack", 60, helper -> {
            Player player = armedPlayer(helper, 0);
            var target = helper.spawn(EntityType.PIG, new BlockPos(2, 1, 2));

            helper.runAfterDelay(1, () -> {
                ItemStack stack = player.getMainHandItem();
                LexVitaeItem.markBlockMined(player);
                boolean canceled = ((LexVitaeItem) stack.getItem()).onLeftClickEntity(stack, player, target);
                if (!canceled) {
                    helper.fail("Attack should be canceled right after mining a block");
                }
                if (LexVitaeItem.attackedRecently(player)) {
                    helper.fail("A canceled attack should not open the combat window");
                }
                helper.succeed();
            });
        });
    }
}
