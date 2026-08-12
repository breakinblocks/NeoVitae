package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.api.fluid.EssentiaLoggingAPI;
import com.breakinblocks.neovitae.common.fluid.EssentiaLogging;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class EssentiaLoggingTests {

    private EssentiaLoggingTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("essentia_logging/supported_blocks_carry_the_property", 60, helper -> {
            Block[] supported = {
                    Blocks.OAK_SLAB, Blocks.OAK_STAIRS, Blocks.OAK_FENCE, Blocks.COBBLESTONE_WALL,
                    Blocks.OAK_TRAPDOOR, Blocks.LADDER, Blocks.IRON_BARS, Blocks.CHEST,
                    Blocks.OAK_SIGN, Blocks.LANTERN, Blocks.SCAFFOLDING, Blocks.GLOW_LICHEN
            };
            for (Block block : supported) {
                BlockState state = block.defaultBlockState();
                if (!EssentiaLogging.isLoggable(state)) {
                    helper.fail(block + " did not receive the essentia logging property");
                    return;
                }
                if (state.getValue(EssentiaLogging.ESSENTIA_LOGGED)) {
                    helper.fail(block + " defaults to essentia logged");
                    return;
                }
            }

            for (Block block : new Block[]{Blocks.STONE, Blocks.OAK_PLANKS, Blocks.OAK_DOOR}) {
                if (EssentiaLogging.isLoggable(block.defaultBlockState())) {
                    helper.fail(block + " received the essentia logging property but should not hold fluid");
                    return;
                }
            }
            helper.succeed();
        });

        r.add("essentia_logging/logged_block_reports_essentia_vitae", 60, helper -> {
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
                helper.fail("Slab was not marked as essentia logged");
                return;
            }
            if (logged.getValue(BlockStateProperties.WATERLOGGED)) {
                helper.fail("Slab was marked waterlogged instead of essentia logged");
                return;
            }
            if (!level.getFluidState(abs).is(NVFluids.ESSENTIA_VITAE_SOURCE.get())) {
                helper.fail("Logged slab reports " + level.getFluidState(abs).getType() + " instead of essentia vitae");
                return;
            }

            ItemStack picked = ((SimpleWaterloggedBlock) logged.getBlock()).pickupBlock(null, level, abs, logged);
            if (!picked.is(NVFluids.ESSENTIA_VITAE_BUCKET.get())) {
                helper.fail("Draining a logged slab gave " + picked + " instead of an essentia vitae bucket");
                return;
            }
            if (!level.getFluidState(abs).isEmpty()) {
                helper.fail("Slab still holds fluid after being drained");
                return;
            }
            helper.succeed();
        });

        r.add("essentia_logging/essentia_vitae_hydrates_farmland", 120, helper -> {
            ServerLevel level = helper.getLevel();
            BlockPos farmland = new BlockPos(2, 1, 2);
            BlockPos fluid = new BlockPos(3, 1, 2);

            helper.setBlock(farmland, Blocks.FARMLAND.defaultBlockState().setValue(FarmlandBlock.MOISTURE, 0));
            helper.setBlock(fluid, NVFluids.ESSENTIA_VITAE_BLOCK.get());

            helper.runAfterDelay(2, () -> {
                BlockPos abs = helper.absolutePos(farmland);
                BlockState state = level.getBlockState(abs);
                if (!state.is(Blocks.FARMLAND)) {
                    helper.fail("Farmland turned to " + state + " next to essentia vitae");
                    return;
                }
                state.randomTick(level, abs, level.getRandom());
                int moisture = level.getBlockState(abs).getValue(FarmlandBlock.MOISTURE);
                if (moisture != 7) {
                    helper.fail("Farmland next to essentia vitae has moisture " + moisture + " instead of 7");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("essentia_logging/unsupported_block_types_are_left_alone", 60, helper -> {
            if (EssentiaLoggingAPI.isSupported(Blocks.STONE)) {
                helper.fail("Stone is reported as supporting essentia logging");
                return;
            }
            if (!EssentiaLoggingAPI.isSupported(Blocks.OAK_SLAB)) {
                helper.fail("Oak slab is not reported as supporting essentia logging");
                return;
            }
            helper.succeed();
        });
    }
}
