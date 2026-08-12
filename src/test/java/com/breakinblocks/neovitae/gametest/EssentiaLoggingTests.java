package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.api.fluid.EssentiaLoggingAPI;
import com.breakinblocks.neovitae.common.fluid.EssentiaLogging;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class EssentiaLoggingTests {

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void supportedBlocksCarryTheEssenceProperty(GameTestHelper helper) {
        Block[] supported = {
                Blocks.OAK_SLAB, Blocks.OAK_STAIRS, Blocks.OAK_FENCE, Blocks.COBBLESTONE_WALL,
                Blocks.OAK_TRAPDOOR, Blocks.LADDER, Blocks.IRON_BARS, Blocks.CHEST,
                Blocks.OAK_SIGN, Blocks.LANTERN, Blocks.SCAFFOLDING, Blocks.GLOW_LICHEN
        };
        for (Block block : supported) {
            BlockState state = block.defaultBlockState();
            if (!EssentiaLogging.isLoggable(state)) {
                helper.fail(block + " did not receive the essence logging property");
                return;
            }
            if (state.getValue(EssentiaLogging.ESSENTIA_LOGGED)) {
                helper.fail(block + " defaults to essence logged");
                return;
            }
        }

        for (Block block : new Block[]{Blocks.STONE, Blocks.OAK_PLANKS, Blocks.OAK_DOOR}) {
            if (EssentiaLogging.isLoggable(block.defaultBlockState())) {
                helper.fail(block + " received the essence logging property but should not hold fluid");
                return;
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 120)
    public void essentiaVitaeHydratesFarmland(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos farmland = new BlockPos(2, 1, 2);
        BlockPos fluid = new BlockPos(3, 1, 2);

        helper.setBlock(farmland, Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 0));
        helper.setBlock(fluid, NVFluids.ESSENTIA_VITAE_BLOCK.get());

        helper.runAfterDelay(2, () -> {
            BlockPos abs = helper.absolutePos(farmland);
            BlockState state = level.getBlockState(abs);
            if (!state.is(Blocks.FARMLAND)) {
                helper.fail("Farmland turned to " + state + " next to essentia vitae");
                return;
            }
            state.randomTick(level, abs, level.getRandom());
            int moisture = level.getBlockState(abs).getValue(FarmBlock.MOISTURE);
            if (moisture != 7) {
                helper.fail("Farmland next to essentia vitae has moisture " + moisture + " instead of 7");
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void unsupportedBlockTypesAreLeftAlone(GameTestHelper helper) {
        if (EssentiaLoggingAPI.isSupported(Blocks.STONE)) {
            helper.fail("Stone is reported as supporting essence logging");
            return;
        }
        if (!EssentiaLoggingAPI.isSupported(Blocks.OAK_SLAB)) {
            helper.fail("Oak slab is not reported as supporting essence logging");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void essentiaLoggedBlockReportsEssenceFluid(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = new BlockPos(2, 1, 2);
        BlockPos abs = helper.absolutePos(pos);

        BlockState slab = Blocks.OAK_SLAB.defaultBlockState();
        if (!(slab.getBlock() instanceof LiquidBlockContainer container)) {
            helper.fail("Oak slab is not a liquid block container");
            return;
        }
        helper.setBlock(pos, slab);

        if (!container.canPlaceLiquid(null, level, abs, slab, NVFluids.ESSENTIA_VITAE_SOURCE.get())) {
            helper.fail("Oak slab refused essentia vitae");
            return;
        }
        if (!container.placeLiquid(level, abs, slab, NVFluids.ESSENTIA_VITAE_SOURCE.get().defaultFluidState())) {
            helper.fail("Placing essentia vitae into the slab failed");
            return;
        }

        BlockState logged = level.getBlockState(abs);
        if (!EssentiaLogging.isLogged(logged)) {
            helper.fail("Slab was not marked as essence logged");
            return;
        }
        if (logged.getValue(BlockStateProperties.WATERLOGGED)) {
            helper.fail("Slab was marked waterlogged instead of essence logged");
            return;
        }
        if (!level.getFluidState(abs).is(NVFluids.ESSENTIA_VITAE_SOURCE.get())) {
            helper.fail("Essence logged slab reports " + level.getFluidState(abs).getType() + " instead of essentia vitae");
            return;
        }

        ItemStack picked = ((SimpleWaterloggedBlock) logged.getBlock()).pickupBlock(null, level, abs, logged);
        if (!picked.is(NVFluids.ESSENTIA_VITAE_BUCKET.get())) {
            helper.fail("Draining an essence logged slab gave " + picked + " instead of an essentia vitae bucket");
            return;
        }
        if (!level.getFluidState(abs).isEmpty()) {
            helper.fail("Slab still holds fluid after being drained");
            return;
        }
        helper.succeed();
    }
}
