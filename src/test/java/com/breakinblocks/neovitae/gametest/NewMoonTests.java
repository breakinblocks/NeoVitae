package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.ritual.types.RitualSphere;

public final class NewMoonTests {

    private static final BlockPos MASTER = new BlockPos(2, 6, 2);

    private NewMoonTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("new_moon/source_starts_two_below_on_solid_ground", 20, helper -> {
            helper.setBlock(MASTER.below(2), Blocks.STONE);
            helper.setBlock(MASTER.below(5), Blocks.STONE);
            int top = RitualSphere.findSourceTop(helper.getLevel(), helper.absolutePos(MASTER));
            helper.assertTrue(top == -2, "Expected the scoop to start 2 below the master stone, got " + top);
            helper.succeed();
        });

        r.add("new_moon/source_follows_terrain_below_a_floating_circle", 20, helper -> {
            helper.setBlock(MASTER.below(), Blocks.IRON_BLOCK);
            helper.setBlock(MASTER.below(5), Blocks.STONE);
            int top = RitualSphere.findSourceTop(helper.getLevel(), helper.absolutePos(MASTER));
            helper.assertTrue(top == -5, "Expected the scoop to start at the stone 5 below the master stone, got " + top);
            helper.succeed();
        });
    }
}
