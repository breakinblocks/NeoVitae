package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.api.sigil.effects.BoundTreasuresSigilEffect;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.world.BoundTreasureLeases;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public final class BoundTreasuresTests {

    private BoundTreasuresTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("bound_treasures/vanilla_chest_is_bindable", 60, helper -> {
            ServerLevel level = helper.getLevel();
            BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
            helper.setBlock(new BlockPos(2, 1, 2), Blocks.CHEST);

            if (!BoundTreasureLeases.isContainer(level, pos)) {
                helper.fail("A vanilla chest should be bindable");
                return;
            }
            if (BoundTreasureLeases.findMenuProvider(level, pos) == null) {
                helper.fail("A vanilla chest should still open through its menu provider");
                return;
            }
            helper.succeed();
        });

        r.add("bound_treasures/inventory_without_menu_provider_is_bindable", 60, helper -> {
            ServerLevel level = helper.getLevel();
            BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
            helper.setBlock(new BlockPos(2, 1, 2), NVBlocks.ARA_VITAE.block().get());

            if (BoundTreasureLeases.findMenuProvider(level, pos) != null) {
                helper.fail("This test needs a block that exposes no menu provider");
                return;
            }
            if (!BoundTreasureLeases.isContainer(level, pos)) {
                helper.fail("A block holding an item handler should be bindable even with no menu provider");
                return;
            }
            helper.succeed();
        });

        r.add("bound_treasures/binding_clears_a_stale_terminal_side", 60, helper -> {
            ServerLevel level = helper.getLevel();
            BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
            helper.setBlock(new BlockPos(2, 1, 2), Blocks.CHEST);

            ItemStack sigil = new ItemStack(Items.STICK);
            sigil.set(NVDataComponents.BOUND_TREASURE_SIDE.get(), Direction.NORTH);

            Player player = helper.makeMockPlayer(GameType.SURVIVAL);
            player.setShiftKeyDown(true);
            new BoundTreasuresSigilEffect().useOnBlock(level, player, sigil, pos, Direction.UP, Vec3.atCenterOf(pos));

            if (!pos.equals(sigil.get(NVDataComponents.TELEPOSER_POS.get()))) {
                helper.fail("Binding to a chest should record its position");
                return;
            }
            if (sigil.get(NVDataComponents.BOUND_TREASURE_SIDE.get()) != null) {
                helper.fail("Binding to a chest should clear a side left over from a terminal");
                return;
            }
            helper.succeed();
        });

        r.add("bound_treasures/plain_block_is_not_bindable", 60, helper -> {
            ServerLevel level = helper.getLevel();
            BlockPos pos = helper.absolutePos(new BlockPos(2, 1, 2));
            helper.setBlock(new BlockPos(2, 1, 2), Blocks.STONE);

            if (BoundTreasureLeases.isContainer(level, pos)) {
                helper.fail("Plain stone should not be bindable");
                return;
            }
            helper.succeed();
        });
    }
}
