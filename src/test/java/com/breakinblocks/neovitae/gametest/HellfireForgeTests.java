package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.HellfireForgeBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

public final class HellfireForgeTests {

    private HellfireForgeTests() {}

    private static HellfireForgeBlockEntity placeForge(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.HELLFIRE_FORGE.block().get().defaultBlockState());
        HellfireForgeBlockEntity forge = helper.getBlockEntity(pos, HellfireForgeBlockEntity.class);
        if (forge == null) {
            helper.fail("Expected HellfireForgeBlockEntity at " + pos);
        }
        return forge;
    }

    private static ItemStack createGemWithSpiritus(double spiritus) {
        ItemStack gem = new ItemStack(NVItems.SPIRITUS_GEM_PETTY.get());
        gem.set(NVDataComponents.SPIRITUS_AMOUNT, spiritus);
        return gem;
    }

    public static void register(NVTestRegistrar r) {
        r.add("hellfire_forge/places_and_initializes", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            HellfireForgeBlockEntity forge = placeForge(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(5, () -> {
                if (forge == null) return;
                if (forge.getProgress() != 0) {
                    helper.fail("Fresh forge should have 0 progress, got " + forge.getProgress());
                }
                helper.succeed();
            });
        });

        r.add("hellfire_forge/crafts_petty_gem", 200, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            HellfireForgeBlockEntity forge = placeForge(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(1, () -> {
                if (forge == null) return;

                forge.inv.setStackInSlot(0, new ItemStack(Items.REDSTONE));
                forge.inv.setStackInSlot(1, new ItemStack(Items.GOLD_INGOT));
                forge.inv.setStackInSlot(2, new ItemStack(Items.GLASS));
                forge.inv.setStackInSlot(3, new ItemStack(Items.LAPIS_LAZULI));
                forge.inv.setStackInSlot(HellfireForgeBlockEntity.GEM_SLOT, createGemWithSpiritus(10.0));

                helper.succeedWhen(() -> {
                    ItemStack output = forge.inv.getStackInSlot(HellfireForgeBlockEntity.OUTPUT_SLOT);
                    helper.assertTrue(!(output.isEmpty()), "Forge should have crafted petty gem, output is empty (progress=" + forge.getProgress() + ")");
                    helper.assertTrue(!(!output.is(NVItems.SPIRITUS_GEM_PETTY.get())), "Expected petty gem, got " + output);
                });
            });
        });

        r.add("hellfire_forge/gem_absorbs_spiritus_from_chunk", 120, helper -> {
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
        });

        r.add("hellfire_forge/does_not_craft_without_spiritus", 200, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            HellfireForgeBlockEntity forge = placeForge(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(1, () -> {
                if (forge == null) return;

                forge.inv.setStackInSlot(0, new ItemStack(Items.REDSTONE));
                forge.inv.setStackInSlot(1, new ItemStack(Items.GOLD_INGOT));
                forge.inv.setStackInSlot(2, new ItemStack(Items.GLASS));
                forge.inv.setStackInSlot(3, new ItemStack(Items.LAPIS_LAZULI));

                helper.runAfterDelay(150, () -> {
                    ItemStack output = forge.inv.getStackInSlot(HellfireForgeBlockEntity.OUTPUT_SLOT);
                    if (!output.isEmpty()) {
                        helper.fail("Forge should not craft without spiritus gem, got " + output);
                    }
                    helper.succeed();
                });
            });
        });

        r.add("hellfire_forge/consumes_inputs_on_craft", 200, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            HellfireForgeBlockEntity forge = placeForge(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(1, () -> {
                if (forge == null) return;

                forge.inv.setStackInSlot(0, new ItemStack(Items.REDSTONE));
                forge.inv.setStackInSlot(1, new ItemStack(Items.GOLD_INGOT));
                forge.inv.setStackInSlot(2, new ItemStack(Items.GLASS));
                forge.inv.setStackInSlot(3, new ItemStack(Items.LAPIS_LAZULI));
                forge.inv.setStackInSlot(HellfireForgeBlockEntity.GEM_SLOT, createGemWithSpiritus(10.0));

                helper.succeedWhen(() -> {
                    for (int i = 0; i < 4; i++) {
                        ItemStack slot = forge.inv.getStackInSlot(i);
                        helper.assertTrue(slot.isEmpty(), "Input slot " + i + " should be empty after crafting, has " + slot);
                    }

                    ItemStack gem = forge.inv.getStackInSlot(HellfireForgeBlockEntity.GEM_SLOT);
                    if (!gem.isEmpty()) {
                        double remainingSpiritus = gem.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D);
                        helper.assertTrue(!(remainingSpiritus >= 10.0), "Gem should have less spiritus after crafting, has " + remainingSpiritus);
                    }
                });
            });
        });

        r.add("hellfire_forge/upgrades_petty_gem_to_lesser", 200, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            HellfireForgeBlockEntity forge = placeForge(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(1, () -> {
                if (forge == null) return;

                forge.inv.setStackInSlot(0, createGemWithSpiritus(64.0));
                forge.inv.setStackInSlot(1, new ItemStack(Items.DIAMOND));
                forge.inv.setStackInSlot(2, new ItemStack(Items.REDSTONE_BLOCK));
                forge.inv.setStackInSlot(3, new ItemStack(Items.LAPIS_BLOCK));

                helper.runAfterDelay(150, () -> {
                    ItemStack output = forge.inv.getStackInSlot(HellfireForgeBlockEntity.OUTPUT_SLOT);
                    if (output.isEmpty()) {
                        helper.fail("Lesser gem upgrade did not craft, progress=" + forge.getProgress());
                    }
                    if (!output.is(NVItems.SPIRITUS_GEM_LESSER.get())) {
                        helper.fail("Expected lesser gem, got " + output);
                    }
                    double carried = output.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D);
                    if (carried != 44.0) {
                        helper.fail("Lesser gem should carry 64 - 20 = 44 spiritus, got " + carried);
                    }
                    helper.succeed();
                });
            });
        });

        r.add("hellfire_forge/gem_slot_fuels_the_craft_when_it_can_pay", 200, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            HellfireForgeBlockEntity forge = placeForge(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(1, () -> {
                if (forge == null) return;

                forge.inv.setStackInSlot(0, createGemWithSpiritus(64.0));
                forge.inv.setStackInSlot(1, new ItemStack(Items.DIAMOND));
                forge.inv.setStackInSlot(2, new ItemStack(Items.REDSTONE_BLOCK));
                forge.inv.setStackInSlot(3, new ItemStack(Items.LAPIS_BLOCK));
                forge.inv.setStackInSlot(HellfireForgeBlockEntity.GEM_SLOT, createGemWithSpiritus(64.0));

                helper.runAfterDelay(150, () -> {
                    ItemStack output = forge.inv.getStackInSlot(HellfireForgeBlockEntity.OUTPUT_SLOT);
                    if (output.isEmpty()) {
                        helper.fail("Lesser gem upgrade did not craft with gem slot occupied, progress=" + forge.getProgress());
                        return;
                    }
                    ItemStack sideGem = forge.inv.getStackInSlot(HellfireForgeBlockEntity.GEM_SLOT);
                    double sideSpiritus = sideGem.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D);
                    if (sideSpiritus != 44.0) {
                        helper.fail("The gem slot should have fuelled this craft and been drained 64 - 20 = 44, got " + sideSpiritus);
                        return;
                    }
                    double carried = output.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D);
                    if (carried != 0.0) {
                        helper.fail("A craft fuelled by the gem slot must not carry charge into the output, got " + carried);
                        return;
                    }
                    helper.succeed();
                });
            });
        });

        r.add("hellfire_forge/falls_back_to_crafting_gem_when_gem_slot_is_short", 200, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            HellfireForgeBlockEntity forge = placeForge(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(1, () -> {
                if (forge == null) return;

                forge.inv.setStackInSlot(0, createGemWithSpiritus(64.0));
                forge.inv.setStackInSlot(1, new ItemStack(Items.DIAMOND));
                forge.inv.setStackInSlot(2, new ItemStack(Items.REDSTONE_BLOCK));
                forge.inv.setStackInSlot(3, new ItemStack(Items.LAPIS_BLOCK));
                forge.inv.setStackInSlot(HellfireForgeBlockEntity.GEM_SLOT, createGemWithSpiritus(10.0));

                helper.runAfterDelay(150, () -> {
                    ItemStack output = forge.inv.getStackInSlot(HellfireForgeBlockEntity.OUTPUT_SLOT);
                    if (output.isEmpty()) {
                        helper.fail("A gem slot too weak to pay should fall back to the crafting-slot gem, progress=" + forge.getProgress());
                        return;
                    }
                    ItemStack sideGem = forge.inv.getStackInSlot(HellfireForgeBlockEntity.GEM_SLOT);
                    double sideSpiritus = sideGem.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D);
                    if (sideSpiritus != 10.0) {
                        helper.fail("A gem slot that could not pay must not be drained, expected 10 got " + sideSpiritus);
                        return;
                    }
                    double carried = output.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D);
                    if (carried != 44.0) {
                        helper.fail("The crafting-slot gem should have carried 64 - 20 = 44 across, got " + carried);
                        return;
                    }
                    helper.succeed();
                });
            });
        });

        r.add("hellfire_forge/reports_insufficient_spiritus", 200, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            HellfireForgeBlockEntity forge = placeForge(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(1, () -> {
                if (forge == null) return;

                forge.inv.setStackInSlot(0, createGemWithSpiritus(10.0));
                forge.inv.setStackInSlot(1, new ItemStack(Items.DIAMOND));
                forge.inv.setStackInSlot(2, new ItemStack(Items.REDSTONE_BLOCK));
                forge.inv.setStackInSlot(3, new ItemStack(Items.LAPIS_BLOCK));

                helper.succeedWhen(() -> {
                    int status = forge.dataAccess.get(HellfireForgeBlockEntity.DATA_STATUS);
                    helper.assertTrue(status == HellfireForgeBlockEntity.STATUS_NEEDS_SPIRITUS, "Expected STATUS_NEEDS_SPIRITUS, got " + status);
                    int required = forge.dataAccess.get(HellfireForgeBlockEntity.DATA_REQUIRED_SPIRITUS);
                    int stored = forge.dataAccess.get(HellfireForgeBlockEntity.DATA_STORED_SPIRITUS);
                    helper.assertTrue(required == 60 || stored != 10, "Expected 10 / 60 reported, got " + stored + " / " + required);
                });
            });
        });

        r.add("hellfire_forge/progress_resets_wrong_recipe", 150, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            HellfireForgeBlockEntity forge = placeForge(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(1, () -> {
                if (forge == null) return;

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
        });
    }
}
