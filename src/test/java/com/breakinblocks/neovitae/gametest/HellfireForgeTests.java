package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.HellfireForgeBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class HellfireForgeTests {

    private static HellfireForgeBlockEntity placeForge(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.HELLFIRE_FORGE.block().get().defaultBlockState());
        BlockEntity be = helper.getBlockEntity(pos);
        if (!(be instanceof HellfireForgeBlockEntity forge)) {
            helper.fail("Expected HellfireForgeBlockEntity at " + pos);
            return null;
        }
        return forge;
    }

    private static ItemStack createGemWithSpiritus(double will) {
        ItemStack gem = new ItemStack(NVItems.SPIRITUS_GEM_PETTY.get());
        gem.set(NVDataComponents.SPIRITUS_AMOUNT, will);
        return gem;
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void forgePlacesAndInitializes(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        HellfireForgeBlockEntity forge = placeForge(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(5, () -> {
            if (forge == null) return;
            if (forge.getProgress() != 0) {
                helper.fail("Fresh forge should have 0 progress, got " + forge.getProgress());
            }
            helper.succeed();
        });
    }

    // Petty gem recipe: redstone + gold ingot + glass + lapis, needs 1 spiritus, drains 1
    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void forgeCraftsPettyGem(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        HellfireForgeBlockEntity forge = placeForge(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (forge == null) return;

            forge.inv.setStackInSlot(0, new ItemStack(Items.REDSTONE));
            forge.inv.setStackInSlot(1, new ItemStack(Items.GOLD_INGOT));
            forge.inv.setStackInSlot(2, new ItemStack(Items.GLASS));
            forge.inv.setStackInSlot(3, new ItemStack(Items.LAPIS_LAZULI));
            forge.inv.setStackInSlot(HellfireForgeBlockEntity.GEM_SLOT, createGemWithSpiritus(10.0));

            // MAX_PROGRESS is 100 ticks
            helper.runAfterDelay(150, () -> {
                ItemStack output = forge.inv.getStackInSlot(HellfireForgeBlockEntity.OUTPUT_SLOT);
                if (output.isEmpty()) {
                    helper.fail("Forge should have crafted petty gem, output is empty (progress=" + forge.getProgress() + ")");
                }
                if (!output.is(NVItems.SPIRITUS_GEM_PETTY.get())) {
                    helper.fail("Expected petty gem, got " + output);
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 120)
    public void forgeGemAbsorbsSpiritusFromChunk(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        BlockPos forgePos = new BlockPos(3, 1, 2);
        HellfireForgeBlockEntity forge = placeForge(helper, forgePos);

        helper.runAfterDelay(1, () -> {
            if (forge == null) return;

            BlockPos abs = helper.absolutePos(forgePos);
            WorldSpiritusHandler.addSpiritusToChunk(helper.getLevel(), abs, SpiritusType.RAW, 100.0);
            double seeded = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), abs, SpiritusType.RAW);
            forge.inv.setStackInSlot(HellfireForgeBlockEntity.GEM_SLOT, createGemWithSpiritus(0.0));

            helper.runAfterDelay(15, () -> {
                ItemStack gem = forge.inv.getStackInSlot(HellfireForgeBlockEntity.GEM_SLOT);
                double gemAmount = gem.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0.0);
                if (gemAmount <= 0) {
                    helper.fail("Gem in the forge should absorb spiritus from the chunk, has " + gemAmount);
                }
                double chunkRaw = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), abs, SpiritusType.RAW);
                if (chunkRaw >= seeded) {
                    helper.fail("Chunk spiritus should decrease as the gem absorbs, was " + seeded + " now " + chunkRaw);
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void forgeDoesNotCraftWithoutSpiritus(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        HellfireForgeBlockEntity forge = placeForge(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (forge == null) return;

            forge.inv.setStackInSlot(0, new ItemStack(Items.REDSTONE));
            forge.inv.setStackInSlot(1, new ItemStack(Items.GOLD_INGOT));
            forge.inv.setStackInSlot(2, new ItemStack(Items.GLASS));
            forge.inv.setStackInSlot(3, new ItemStack(Items.LAPIS_LAZULI));
            // No gem in slot

            helper.runAfterDelay(150, () -> {
                ItemStack output = forge.inv.getStackInSlot(HellfireForgeBlockEntity.OUTPUT_SLOT);
                if (!output.isEmpty()) {
                    helper.fail("Forge should not craft without spiritus gem, got " + output);
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void forgeConsumesInputsOnCraft(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        HellfireForgeBlockEntity forge = placeForge(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (forge == null) return;

            forge.inv.setStackInSlot(0, new ItemStack(Items.REDSTONE));
            forge.inv.setStackInSlot(1, new ItemStack(Items.GOLD_INGOT));
            forge.inv.setStackInSlot(2, new ItemStack(Items.GLASS));
            forge.inv.setStackInSlot(3, new ItemStack(Items.LAPIS_LAZULI));
            forge.inv.setStackInSlot(HellfireForgeBlockEntity.GEM_SLOT, createGemWithSpiritus(10.0));

            helper.runAfterDelay(150, () -> {
                for (int i = 0; i < 4; i++) {
                    ItemStack slot = forge.inv.getStackInSlot(i);
                    if (!slot.isEmpty()) {
                        helper.fail("Input slot " + i + " should be empty after crafting, has " + slot);
                    }
                }

                ItemStack gem = forge.inv.getStackInSlot(HellfireForgeBlockEntity.GEM_SLOT);
                if (!gem.isEmpty()) {
                    double remainingSpiritus = gem.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D);
                    if (remainingSpiritus >= 10.0) {
                        helper.fail("Gem should have less will after crafting, has " + remainingSpiritus);
                    }
                }

                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 150)
    public void forgeProgressResetsWithWrongRecipe(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        HellfireForgeBlockEntity forge = placeForge(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (forge == null) return;

            // Invalid recipe — just dirt in all slots
            forge.inv.setStackInSlot(0, new ItemStack(Items.DIRT));
            forge.inv.setStackInSlot(1, new ItemStack(Items.DIRT));
            forge.inv.setStackInSlot(HellfireForgeBlockEntity.GEM_SLOT, createGemWithSpiritus(10.0));

            helper.runAfterDelay(110, () -> {
                if (forge.getProgress() > 0) {
                    helper.fail("Progress should be 0 for invalid recipe, got " + forge.getProgress());
                }
                ItemStack output = forge.inv.getStackInSlot(HellfireForgeBlockEntity.OUTPUT_SLOT);
                if (!output.isEmpty()) {
                    helper.fail("Should not produce output for invalid recipe, got " + output);
                }
                helper.succeed();
            });
        });
    }
}
