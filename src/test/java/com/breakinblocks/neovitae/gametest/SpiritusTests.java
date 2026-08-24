package com.breakinblocks.neovitae.gametest;

import com.mojang.serialization.JsonOps;
import com.google.gson.JsonElement;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import com.breakinblocks.neovitae.common.block.BlockSpiritusCrystal;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.SpiritusCrystalBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.VasMaleficumBlockEntity;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.spiritus.SpiritusChunk;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

public final class SpiritusTests {

    private SpiritusTests() {}

    private static void setChunkSpiritus(GameTestHelper helper, BlockPos relativePos, double amount) {
        BlockPos absPos = helper.absolutePos(relativePos);
        LevelChunk chunk = helper.getLevel().getChunkAt(absPos);
        chunk.setData(NVDataAttachments.SPIRITUS_CHUNK.get(), new SpiritusChunk(amount, 0, 0, 0, 0));
        chunk.markUnsaved();
    }

    private static double getChunkSpiritus(GameTestHelper helper, BlockPos relativePos) {
        BlockPos absPos = helper.absolutePos(relativePos);
        return WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), absPos, SpiritusType.RAW);
    }

    public static void register(NVTestRegistrar r) {
        r.addIsolated("spiritus/crystal_grows_with_chunk_spiritus", 300, helper -> {
            BlockPos crystalPos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(crystalPos, NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get().defaultBlockState());

            setChunkSpiritus(helper, crystalPos, 100.0);

            helper.succeedWhen(() -> {
                SpiritusCrystalBlockEntity crystal = helper.getBlockEntity(crystalPos, SpiritusCrystalBlockEntity.class);
                helper.assertTrue(crystal != null, "Expected SpiritusCrystalBlockEntity");
                double remaining = WorldSpiritusHandler.getCurrentSpiritus(
                        helper.getLevel(), helper.absolutePos(crystalPos), SpiritusType.RAW);
                boolean grew = crystal.progressToNextCrystal > 0
                        || crystal.getCrystalCount() > 1
                        || remaining < 100.0;
                helper.assertTrue(grew, "Crystal should have grown with chunk spiritus present (progress="
                            + crystal.progressToNextCrystal + ", count=" + crystal.getCrystalCount()
                            + ", spiritus=" + remaining + ")");
            });
        });

        r.addIsolated("spiritus/crystal_does_not_grow_without_spiritus", 60, helper -> {
            BlockPos crystalPos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(crystalPos, NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get().defaultBlockState());

            setChunkSpiritus(helper, crystalPos, 0.0);

            helper.runAfterDelay(40, () -> {
                SpiritusCrystalBlockEntity crystal = helper.getBlockEntity(crystalPos, SpiritusCrystalBlockEntity.class);
                if (crystal == null) {
                    helper.fail("Expected SpiritusCrystalBlockEntity");
                    return;
                }
                if (crystal.progressToNextCrystal > 0) {
                    helper.fail("Crystal should not grow without chunk spiritus, got " + crystal.progressToNextCrystal);
                }
                helper.succeed();
            });
        });

        r.addIsolated("spiritus/crystal_breaks_when_support_removed", 60, helper -> {
            BlockPos supportPos = new BlockPos(3, 0, 2);
            BlockPos crystalPos = new BlockPos(3, 1, 2);
            helper.setBlock(supportPos, Blocks.STONE.defaultBlockState());
            helper.setBlock(crystalPos, NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get().defaultBlockState());

            helper.runAfterDelay(2, () -> {
                helper.setBlock(supportPos, Blocks.AIR.defaultBlockState());

                helper.runAfterDelay(2, () -> {
                    if (!helper.getBlockState(crystalPos).isAir()) {
                        helper.fail("Spiritus Crystal should break when its support is removed, still present: "
                                + helper.getBlockState(crystalPos));
                    }
                    helper.succeed();
                });
            });
        });

        r.addIsolated("spiritus/crystal_drains_chunk_spiritus", 60, helper -> {
            BlockPos crystalPos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(crystalPos, NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get().defaultBlockState());

            setChunkSpiritus(helper, crystalPos, 50.0);

            helper.succeedWhen(() -> {
                double remaining = getChunkSpiritus(helper, crystalPos);
                helper.assertTrue(!(remaining >= 50.0), "Crystal should drain chunk spiritus, but it's still " + remaining);
            });
        });

        r.addIsolated("spiritus/crucible_places_and_initializes", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            BlockPos pos = new BlockPos(3, 1, 2);
            helper.setBlock(pos, NVBlocks.VAS_MALEFICUM.block().get().defaultBlockState());

            helper.runAfterDelay(5, () -> {
                VasMaleficumBlockEntity crucible = helper.getBlockEntity(pos, VasMaleficumBlockEntity.class);
                if (crucible == null) {
                    helper.fail("Expected VasMaleficumBlockEntity");
                    return;
                }
                helper.succeed();
            });
        });

        r.addIsolated("spiritus/crucible_drains_gem_to_chunk", 100, helper -> {
            BlockPos cruciblePos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(cruciblePos, NVBlocks.VAS_MALEFICUM.block().get().defaultBlockState());

            helper.runAfterDelay(1, () -> {
                VasMaleficumBlockEntity crucible = helper.getBlockEntity(cruciblePos, VasMaleficumBlockEntity.class);
                if (crucible == null) {
                    helper.fail("No crucible");
                    return;
                }

                ItemStack gem = new ItemStack(NVItems.SPIRITUS_GEM_PETTY.get());
                gem.set(NVDataComponents.SPIRITUS_AMOUNT, 50.0);
                crucible.handleInteraction(gem);

                helper.succeedWhen(() -> {
                    double gemAfter = crucible.getInventory().getStackInSlot(0)
                            .getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0.0);
                    helper.assertTrue(!(gemAfter >= 50.0), "Crucible should drain the gem into the chunk, gem still holds " + gemAfter);
                });
            });
        });

        r.addIsolated("spiritus/chunk_codec_round_trips_all_aspects", 5, helper -> {
            SpiritusChunk original = new SpiritusChunk(11, 22, 33, 44, 55, 6, 7, 8, 9, 10);
            JsonElement encoded = SpiritusChunk.CODEC.encodeStart(JsonOps.INSTANCE, original)
                    .getOrThrow(s -> new IllegalStateException("encode failed: " + s));
            SpiritusChunk decoded = SpiritusChunk.CODEC.parse(JsonOps.INSTANCE, encoded)
                    .getOrThrow(s -> new IllegalStateException("decode failed: " + s));

            for (SpiritusType type : SpiritusType.values()) {
                if (Math.abs(decoded.getSpiritus(type) - original.getSpiritus(type)) > 1e-6) {
                    helper.fail("Codec lost amount for " + type);
                    return;
                }
                if (Math.abs(decoded.getMaxBonus(type) - original.getMaxBonus(type)) > 1e-6) {
                    helper.fail("Codec lost bonus for " + type);
                    return;
                }
            }

            String json = encoded.toString();
            for (String oldName : new String[]{"corrosive", "destructive", "vengeful", "steadfast", "default"}) {
                if (json.contains("\"" + oldName + "\"")) {
                    helper.fail("Codec still uses old field name " + oldName + ": " + json);
                    return;
                }
            }
            for (String newName : new String[]{"raw", "ruina", "nihilum", "vindicta", "invictus"}) {
                if (!json.contains("\"" + newName + "\"")) {
                    helper.fail("Codec missing new field name " + newName + ": " + json);
                    return;
                }
            }
            helper.succeed();
        });

        r.addIsolated("spiritus/injection_multiplier_boosts_raw_without_bias", 5, helper -> {
            SpiritusChunk chunk = new SpiritusChunk();
            chunk.setInjectionMultiplier(1.25, SpiritusType.RAW, 100, 0);
            chunk.addSpiritus(SpiritusType.RAW, 80);
            double total = chunk.getSpiritus(SpiritusType.RAW);
            if (Math.abs(total - 100.0) > 1e-6) {
                helper.fail("Expected RAW=100, got " + total);
                return;
            }
            helper.succeed();
        });

        r.addIsolated("spiritus/injection_multiplier_redirects_bonus_to_bias", 5, helper -> {
            SpiritusChunk chunk = new SpiritusChunk();
            chunk.setInjectionMultiplier(1.25, SpiritusType.RUINA, 100, 0);
            chunk.addSpiritus(SpiritusType.RAW, 80);
            double raw = chunk.getSpiritus(SpiritusType.RAW);
            double ruina = chunk.getSpiritus(SpiritusType.RUINA);
            if (Math.abs(raw - 80.0) > 1e-6) {
                helper.fail("Expected RAW=80, got " + raw);
                return;
            }
            if (Math.abs(ruina - 20.0) > 1e-6) {
                helper.fail("Expected RUINA=20, got " + ruina);
                return;
            }
            helper.succeed();
        });

        r.addIsolated("spiritus/injection_multiplier_does_not_redirect_aspected", 5, helper -> {
            SpiritusChunk chunk = new SpiritusChunk();
            chunk.setInjectionMultiplier(1.25, SpiritusType.NIHILUM, 100, 0);
            chunk.addSpiritus(SpiritusType.RUINA, 80);
            double ruina = chunk.getSpiritus(SpiritusType.RUINA);
            double nihilum = chunk.getSpiritus(SpiritusType.NIHILUM);
            if (Math.abs(ruina - 100.0) > 1e-6) {
                helper.fail("Expected RUINA=100, got " + ruina);
                return;
            }
            if (nihilum != 0.0) {
                helper.fail("Expected NIHILUM=0, got " + nihilum);
                return;
            }
            helper.succeed();
        });

        r.addIsolated("spiritus/no_multiplier_is_exact_add", 5, helper -> {
            SpiritusChunk chunk = new SpiritusChunk();
            chunk.addSpiritus(SpiritusType.RAW, 50);
            double raw = chunk.getSpiritus(SpiritusType.RAW);
            if (Math.abs(raw - 50.0) > 1e-6) {
                helper.fail("Expected RAW=50, got " + raw);
                return;
            }
            helper.succeed();
        });

        r.addIsolated("spiritus/multipliers_decay_after_expiry", 5, helper -> {
            SpiritusChunk chunk = new SpiritusChunk();
            chunk.setGrowthMultiplier(2.0, 50, 1000);
            chunk.setInjectionMultiplier(1.25, SpiritusType.RUINA, 50, 1000);
            chunk.tickRitualBuffs(1040);
            if (chunk.getGrowthMultiplier() != 2.0) {
                helper.fail("Growth should still be 2.0 before expiry, got " + chunk.getGrowthMultiplier());
                return;
            }
            chunk.tickRitualBuffs(1100);
            if (chunk.getGrowthMultiplier() != 1.0) {
                helper.fail("Growth should decay to 1.0, got " + chunk.getGrowthMultiplier());
                return;
            }
            if (chunk.getInjectionMultiplier() != 1.0) {
                helper.fail("Injection should decay to 1.0, got " + chunk.getInjectionMultiplier());
                return;
            }
            if (chunk.getInjectionAspectBias() != SpiritusType.RAW) {
                helper.fail("Bias should reset to RAW, got " + chunk.getInjectionAspectBias());
                return;
            }
            helper.succeed();
        });

        r.addIsolated("spiritus/copy_preserves_transient_buffs", 5, helper -> {
            SpiritusChunk chunk = new SpiritusChunk();
            chunk.setGrowthMultiplier(2.0, 200, 500);
            chunk.setInjectionMultiplier(1.5, SpiritusType.VINDICTA, 200, 500);
            SpiritusChunk copy = chunk.copy();
            if (copy.getGrowthMultiplier() != 2.0) {
                helper.fail("copy() lost growth multiplier");
                return;
            }
            if (copy.getInjectionMultiplier() != 1.5) {
                helper.fail("copy() lost injection multiplier");
                return;
            }
            if (copy.getInjectionAspectBias() != SpiritusType.VINDICTA) {
                helper.fail("copy() lost aspect bias");
                return;
            }
            helper.succeed();
        });

        r.addIsolated("spiritus/multiplier_applies_via_world_handler", 10, helper -> {
            BlockPos pos = new BlockPos(3, 1, 2);
            BlockPos absPos = helper.absolutePos(pos);
            LevelChunk chunk = helper.getLevel().getChunkAt(absPos);

            SpiritusChunk fresh = new SpiritusChunk();
            long now = helper.getLevel().getGameTime();
            fresh.setInjectionMultiplier(1.25, SpiritusType.RUINA, 200, now);
            chunk.setData(NVDataAttachments.SPIRITUS_CHUNK.get(), fresh);

            WorldSpiritusHandler.addSpiritusToChunk(helper.getLevel(), absPos, SpiritusType.RAW, 40);
            double raw = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), absPos, SpiritusType.RAW);
            double ruina = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), absPos, SpiritusType.RUINA);
            if (Math.abs(raw - 40.0) > 1e-6) {
                helper.fail("Expected RAW=40 after first inject, got " + raw);
                return;
            }
            if (Math.abs(ruina - 10.0) > 1e-6) {
                helper.fail("Expected RUINA=10 (bias redirect), got " + ruina);
                return;
            }

            WorldSpiritusHandler.addSpiritusToChunk(helper.getLevel(), absPos, SpiritusType.RAW, 30);
            double raw2 = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), absPos, SpiritusType.RAW);
            double ruina2 = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), absPos, SpiritusType.RUINA);
            if (Math.abs(raw2 - 70.0) > 1e-6) {
                helper.fail("Expected RAW=70 after second inject, got " + raw2);
                return;
            }
            if (Math.abs(ruina2 - 17.5) > 1e-6) {
                helper.fail("Expected RUINA=17.5 (multiplier survives copy()), got " + ruina2);
                return;
            }
            helper.succeed();
        });

        r.addIsolated("spiritus/crystal_reaches_max_age", 10, helper -> {
            BlockPos crystalPos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(crystalPos, NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get().defaultBlockState()
                    .setValue(BlockSpiritusCrystal.AGE, 6));

            helper.runAfterDelay(2, () -> {
                BlockState state = helper.getBlockState(crystalPos);
                if (!state.hasProperty(BlockSpiritusCrystal.AGE)) {
                    helper.fail("Missing AGE property");
                    return;
                }
                if (state.getValue(BlockSpiritusCrystal.AGE) != 6) {
                    helper.fail("Expected AGE=6, got " + state.getValue(BlockSpiritusCrystal.AGE));
                    return;
                }
                helper.succeed();
            });
        });
    }
}
