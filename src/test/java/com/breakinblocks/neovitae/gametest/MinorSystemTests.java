package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.BloodTankBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.SpiraInfernalisBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.TeleposerBlockEntity;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.spiritus.SpiritusChunk;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class MinorSystemTests {

    // ==================== Blood Tank ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void bloodTankStoresAndRetrievesFluid(GameTestHelper helper) {
        BlockPos pos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());

        // Place and configure tier via NBT
        helper.setBlock(pos, NVBlocks.BLOOD_TANK.block().get().defaultBlockState());
        BloodTankBlockEntity tank = (BloodTankBlockEntity) helper.getBlockEntity(pos);
        net.minecraft.nbt.CompoundTag tag = tank.saveWithoutMetadata(helper.getLevel().registryAccess());
        tag.putInt("tier", 1);
        tank.loadWithComponents(tag, helper.getLevel().registryAccess());

        helper.runAfterDelay(1, () -> {
            BlockPos absPos = helper.absolutePos(pos);
            IFluidHandler handler = helper.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, absPos, null);
            if (handler == null) { helper.fail("No fluid handler"); return; }

            int filled = handler.fill(new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE.get(), 5000), IFluidHandler.FluidAction.EXECUTE);
            if (filled != 5000) { helper.fail("Should fill 5000mB, filled " + filled); return; }

            FluidStack drained = handler.drain(2000, IFluidHandler.FluidAction.EXECUTE);
            if (drained.isEmpty() || drained.getAmount() != 2000) {
                helper.fail("Should drain 2000mB, got " + (drained.isEmpty() ? 0 : drained.getAmount()));
                return;
            }

            FluidStack remaining = handler.getFluidInTank(0);
            if (remaining.getAmount() != 3000) {
                helper.fail("Should have 3000mB remaining, has " + remaining.getAmount());
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void bloodTankRespectsCapacity(GameTestHelper helper) {
        BlockPos pos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(pos, NVBlocks.BLOOD_TANK.block().get().defaultBlockState());
        BloodTankBlockEntity tank = (BloodTankBlockEntity) helper.getBlockEntity(pos);
        net.minecraft.nbt.CompoundTag tag = tank.saveWithoutMetadata(helper.getLevel().registryAccess());
        tag.putInt("tier", 1);
        tank.loadWithComponents(tag, helper.getLevel().registryAccess());

        helper.runAfterDelay(1, () -> {
            int capacity = tank.getCapacity();
            BlockPos absPos = helper.absolutePos(pos);
            IFluidHandler handler = helper.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, absPos, null);

            int filled = handler.fill(new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE.get(), capacity + 5000), IFluidHandler.FluidAction.EXECUTE);
            if (filled > capacity) {
                helper.fail("Should not exceed capacity " + capacity + ", filled " + filled);
                return;
            }
            helper.succeed();
        });
    }

    // ==================== Spira Infernalis ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void pylonPlacesAndInitializes(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(new BlockPos(3, 1, 2), NVBlocks.SPIRA_INFERNALIS.block().get().defaultBlockState());

        helper.runAfterDelay(5, () -> {
            BlockEntity be = helper.getBlockEntity(new BlockPos(3, 1, 2));
            if (!(be instanceof SpiraInfernalisBlockEntity)) {
                helper.fail("Expected SpiraInfernalisBlockEntity");
                return;
            }
            helper.succeed();
        });
    }

    // ==================== Teleposer ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void teleposerPlacesAndInitializes(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(new BlockPos(3, 1, 2), NVBlocks.TELEPOSER.block().get().defaultBlockState());

        helper.runAfterDelay(5, () -> {
            BlockEntity be = helper.getBlockEntity(new BlockPos(3, 1, 2));
            if (!(be instanceof TeleposerBlockEntity)) {
                helper.fail("Expected TeleposerBlockEntity");
                return;
            }
            helper.succeed();
        });
    }

    // ==================== Incense Altar ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void incenseAltarPlacesAndInitializes(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(new BlockPos(3, 1, 2), NVBlocks.INCENSE_ALTAR.block().get().defaultBlockState());

        helper.runAfterDelay(5, () -> {
            BlockEntity be = helper.getBlockEntity(new BlockPos(3, 1, 2));
            if (be == null) {
                helper.fail("Expected IncenseAltarBlockEntity");
                return;
            }
            helper.succeed();
        });
    }
}
