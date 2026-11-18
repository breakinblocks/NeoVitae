package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.ritual.types.RitualSphere;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class NewMoonTests {

    private static final BlockPos MASTER = new BlockPos(2, 6, 2);

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void sourceStartsTwoBelowOnSolidGround(GameTestHelper helper) {
        helper.setBlock(MASTER.below(2), Blocks.STONE);
        helper.setBlock(MASTER.below(5), Blocks.STONE);
        int top = RitualSphere.findSourceTop(helper.getLevel(), helper.absolutePos(MASTER));
        helper.assertTrue(top == -2, "Expected the scoop to start 2 below the master stone, got " + top);
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void sourceFollowsTerrainBelowAFloatingCircle(GameTestHelper helper) {
        helper.setBlock(MASTER.below(), Blocks.IRON_BLOCK);
        helper.setBlock(MASTER.below(5), Blocks.STONE);
        int top = RitualSphere.findSourceTop(helper.getLevel(), helper.absolutePos(MASTER));
        helper.assertTrue(top == -5, "Expected the scoop to start at the stone 5 below the master stone, got " + top);
        helper.succeed();
    }
}
