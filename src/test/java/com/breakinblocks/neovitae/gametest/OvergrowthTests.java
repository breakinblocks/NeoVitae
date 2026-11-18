package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.ritual.types.RitualGreenGrove;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.UUID;

public final class OvergrowthTests {

    private static final UUID OWNER = UUID.fromString("8a4f1c27-5b9e-4d63-b0a8-3c7e9f2d6a51");
    private static final BlockPos MASTER = new BlockPos(2, 0, 2);
    private static final BlockPos WEAK = new BlockPos(1, 2, 1);
    private static final BlockPos STRONG = new BlockPos(3, 2, 3);

    private OvergrowthTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("overgrowth/tau_stays_common_without_nihilum", 20, helper -> {
            QuarryThroughputTests.StubMaster master = field(helper);
            setNihilum(helper, 0);

            new RitualGreenGrove().performRitual(master);

            helper.assertTrue(helper.getBlockState(WEAK).is(NVBlocks.WEAK_TAU.block().get()),
                    "Tau must stay common Tau when the chunk holds no Nihilum");
            helper.assertTrue(age(helper.getBlockState(STRONG)) == 0,
                    "Saturated Tau must not grow without a creature or Nihilum");
            helper.succeed();
        });

        r.add("overgrowth/nihilum_feeds_tau_into_saturated_tau", 20, helper -> {
            QuarryThroughputTests.StubMaster master = field(helper);
            Anima anima = AnimaHelper.getAnima(OWNER);
            setNihilum(helper, 50);
            double nihilumBefore = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), helper.absolutePos(MASTER), SpiritusType.NIHILUM);
            int evBefore = anima.getCurrentEV();

            new RitualGreenGrove().performRitual(master);

            double nihilumLeft = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), helper.absolutePos(MASTER), SpiritusType.NIHILUM);
            int evSpent = evBefore - anima.getCurrentEV();
            setNihilum(helper, 0);

            BlockState fedWeak = helper.getBlockState(WEAK);
            helper.assertTrue(fedWeak.is(NVBlocks.STRONG_TAU.block().get()) && age(fedWeak) == 1,
                    "Fed Tau should become Saturated Tau one stage on, got " + fedWeak);
            helper.assertTrue(age(helper.getBlockState(STRONG)) == 1,
                    "Fed Saturated Tau should grow one stage, got " + helper.getBlockState(STRONG));
            helper.assertTrue(evSpent == 200, "Two fed vines should cost 200 EV, spent " + evSpent);
            helper.assertTrue(Math.abs(nihilumBefore - nihilumLeft - 2 * RitualGreenGrove.WILL_PER_TAU_FEED) < 1.0E-6,
                    "Two fed vines should use 1.0 Nihilum, used " + (nihilumBefore - nihilumLeft));
            helper.succeed();
        });
    }

    private static QuarryThroughputTests.StubMaster field(GameTestHelper helper) {
        Anima anima = AnimaHelper.getAnima(OWNER);
        if (anima.getCurrentEV() < 200000) {
            anima.add(AnimaTicket.create(1000000), 10000000);
        }
        helper.setBlock(WEAK.below(), Blocks.FARMLAND);
        helper.setBlock(STRONG.below(), Blocks.FARMLAND);
        helper.setBlock(WEAK, NVBlocks.WEAK_TAU.block().get());
        helper.setBlock(STRONG, NVBlocks.STRONG_TAU.block().get());
        QuarryThroughputTests.StubMaster master =
                new QuarryThroughputTests.StubMaster(helper.getLevel(), helper.absolutePos(MASTER), OWNER);
        master.setBlockRange(RitualGreenGrove.GROWTH_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-2, 1, -2), 5, 3, 5));
        return master;
    }

    private static void setNihilum(GameTestHelper helper, double amount) {
        BlockPos pos = helper.absolutePos(MASTER);
        double current = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), pos, SpiritusType.NIHILUM);
        if (current > amount) {
            WorldSpiritusHandler.drainSpiritusFromChunk(helper.getLevel(), pos, SpiritusType.NIHILUM, current - amount);
        } else if (current < amount) {
            WorldSpiritusHandler.addSpiritusToChunk(helper.getLevel(), pos, SpiritusType.NIHILUM, amount - current);
        }
    }

    private static int age(BlockState state) {
        return state.getValue(CropBlock.AGE);
    }
}
