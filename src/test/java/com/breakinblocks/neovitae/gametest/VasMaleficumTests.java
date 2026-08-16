package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.VasMaleficumBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.spiritus.SpiritusHelper;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

public final class VasMaleficumTests {

    private VasMaleficumTests() {}

    private static VasMaleficumBlockEntity place(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos.below(), Blocks.STONE.defaultBlockState());
        helper.setBlock(pos, NVBlocks.VAS_MALEFICUM.block().get().defaultBlockState());
        VasMaleficumBlockEntity be = helper.getBlockEntity(pos, VasMaleficumBlockEntity.class);
        if (be == null) {
            helper.fail("Expected VasMaleficumBlockEntity at " + pos);
        }
        return be;
    }

    private static ItemStack gemWith(SpiritusType type, double amount) {
        ItemStack gem = new ItemStack(NVItems.SPIRITUS_GEM_COMMON.get());
        SpiritusHelper.setSpiritus(gem, type, amount);
        return gem;
    }

    public static void register(NVTestRegistrar r) {
        r.add("vas_maleficum/powered_fills_gem_from_chunk", 120, helper -> {
            BlockPos pos = new BlockPos(3, 1, 2);
            VasMaleficumBlockEntity be = place(helper, pos);
            helper.setBlock(pos.below(), Blocks.REDSTONE_BLOCK.defaultBlockState());

            helper.runAfterDelay(1, () -> {
                if (be == null) return;
                BlockPos worldPos = helper.absolutePos(pos);
                WorldSpiritusHandler.fillSpiritusToAmount(helper.getLevel(), worldPos, SpiritusType.NIHILUM, 100);
                be.inventory.setStackInSlot(0, gemWith(SpiritusType.NIHILUM, 1));

                helper.runAfterDelay(40, () -> {
                    ItemStack gem = be.inventory.getStackInSlot(0);
                    double stored = SpiritusHelper.getSpiritus(gem, SpiritusType.NIHILUM);
                    if (stored <= 1) {
                        helper.fail("Powered vas should fill the gem from the chunk, gem has " + stored);
                        return;
                    }
                    double chunkAmount = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), worldPos, SpiritusType.NIHILUM);
                    if (chunkAmount >= 100) {
                        helper.fail("Chunk spiritus should decrease as the gem fills, chunk=" + chunkAmount);
                        return;
                    }
                    helper.succeed();
                });
            });
        });

        r.add("vas_maleficum/unpowered_drains_gem_into_chunk", 120, helper -> {
            BlockPos pos = new BlockPos(3, 1, 2);
            VasMaleficumBlockEntity be = place(helper, pos);

            helper.runAfterDelay(1, () -> {
                if (be == null) return;
                BlockPos worldPos = helper.absolutePos(pos);
                WorldSpiritusHandler.drainSpiritusFromChunk(helper.getLevel(), worldPos, SpiritusType.NIHILUM, 1_000_000);
                be.inventory.setStackInSlot(0, gemWith(SpiritusType.NIHILUM, 500));

                helper.runAfterDelay(40, () -> {
                    ItemStack gem = be.inventory.getStackInSlot(0);
                    double stored = SpiritusHelper.getSpiritus(gem, SpiritusType.NIHILUM);
                    if (stored >= 500) {
                        helper.fail("Unpowered vas should drain the gem, gem still has " + stored);
                        return;
                    }
                    double chunkAmount = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), worldPos, SpiritusType.NIHILUM);
                    double moved = 500 - stored;
                    if (chunkAmount > moved + 0.0001) {
                        helper.fail("Chunk gained " + chunkAmount + " but the gem only lost " + moved + " (dupe)");
                        return;
                    }
                    helper.succeed();
                });
            });
        });
    }
}
