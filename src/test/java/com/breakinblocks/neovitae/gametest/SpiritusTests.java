package com.breakinblocks.neovitae.gametest;

import com.mojang.serialization.JsonOps;
import com.google.gson.JsonElement;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.block.BlockSpiritusCrystal;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.VasMaleficumBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.SpiritusCrystalBlockEntity;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.spiritus.SpiritusChunk;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class SpiritusTests {

    private static void setChunkSpiritus(GameTestHelper helper, BlockPos relativePos, double amount) {
        BlockPos absPos = helper.absolutePos(relativePos);
        LevelChunk chunk = helper.getLevel().getChunkAt(absPos);
        chunk.setData(NVDataAttachments.SPIRITUS_CHUNK.get(), new SpiritusChunk(amount, 0, 0, 0, 0));
        chunk.setUnsaved(true);
    }

    private static double getChunkSpiritus(GameTestHelper helper, BlockPos relativePos) {
        BlockPos absPos = helper.absolutePos(relativePos);
        return WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), absPos, SpiritusType.RAW);
    }

    // ==================== Crystal Growth ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 300)
    public void crystalGrowsWithChunkSpiritus(GameTestHelper helper) {
        BlockPos crystalPos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(crystalPos, NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get().defaultBlockState());

        setChunkSpiritus(helper, crystalPos, 100.0);

        helper.runAfterDelay(250, () -> {
            BlockEntity be = helper.getBlockEntity(crystalPos);
            if (!(be instanceof SpiritusCrystalBlockEntity crystal)) {
                helper.fail("Expected SpiritusCrystalBlockEntity");
                return;
            }

            if (crystal.progressToNextCrystal <= 0) {
                helper.fail("Crystal should have growth progress with chunk will present");
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void crystalDoesNotGrowWithoutSpiritus(GameTestHelper helper) {
        BlockPos crystalPos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(crystalPos, NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get().defaultBlockState());

        setChunkSpiritus(helper, crystalPos, 0.0);

        helper.runAfterDelay(40, () -> {
            BlockEntity be = helper.getBlockEntity(crystalPos);
            if (!(be instanceof SpiritusCrystalBlockEntity crystal)) {
                helper.fail("Expected SpiritusCrystalBlockEntity");
                return;
            }

            if (crystal.progressToNextCrystal > 0) {
                helper.fail("Crystal should not grow without chunk will");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void crystalDrainsChunkWill(GameTestHelper helper) {
        BlockPos crystalPos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(crystalPos, NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get().defaultBlockState());

        setChunkSpiritus(helper, crystalPos, 50.0);

        helper.runAfterDelay(40, () -> {
            double remaining = getChunkSpiritus(helper, crystalPos);
            if (remaining >= 50.0) {
                helper.fail("Crystal should drain chunk will, but it's still " + remaining);
            }
            helper.succeed();
        });
    }

    // ==================== Crucible ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void cruciblePlacesAndInitializes(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(new BlockPos(3, 1, 2), NVBlocks.VAS_MALEFICUM.block().get().defaultBlockState());

        helper.runAfterDelay(5, () -> {
            BlockEntity be = helper.getBlockEntity(new BlockPos(3, 1, 2));
            if (!(be instanceof VasMaleficumBlockEntity)) {
                helper.fail("Expected VasMaleficumBlockEntity");
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 100)
    public void crucibleDrainsGemToChunk(GameTestHelper helper) {
        BlockPos cruciblePos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(cruciblePos, NVBlocks.VAS_MALEFICUM.block().get().defaultBlockState());

        helper.runAfterDelay(1, () -> {
            VasMaleficumBlockEntity crucible = (VasMaleficumBlockEntity) helper.getBlockEntity(cruciblePos);

            ItemStack gem = new ItemStack(NVItems.SPIRITUS_GEM_PETTY.get());
            gem.set(NVDataComponents.SPIRITUS_AMOUNT, 50.0);
            crucible.handleInteraction(gem);

            helper.runAfterDelay(60, () -> {
                double gemAfter = crucible.getInventory().getStackInSlot(0)
                        .getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0.0);
                if (gemAfter >= 50.0) {
                    helper.fail("Crucible should drain the gem into the chunk, gem still holds " + gemAfter);
                }
                helper.succeed();
            });
        });
    }

    // ==================== Codec Roundtrip ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 5)
    public void chunkCodecRoundTripsAllAspects(GameTestHelper helper) {
        SpiritusChunk original = new SpiritusChunk(11, 22, 33, 44, 55, 6, 7, 8, 9, 10);
        JsonElement encoded = SpiritusChunk.CODEC.encodeStart(JsonOps.INSTANCE, original)
                .getOrThrow(s -> new IllegalStateException("encode failed: " + s));
        SpiritusChunk decoded = SpiritusChunk.CODEC.parse(JsonOps.INSTANCE, encoded)
                .getOrThrow(s -> new IllegalStateException("decode failed: " + s));

        for (SpiritusType type : SpiritusType.values()) {
            if (Math.abs(decoded.getSpiritus(type) - original.getSpiritus(type)) > 1e-6) {
                helper.fail("Codec lost amount for " + type + ": expected " + original.getSpiritus(type) + " got " + decoded.getSpiritus(type));
                return;
            }
            if (Math.abs(decoded.getMaxBonus(type) - original.getMaxBonus(type)) > 1e-6) {
                helper.fail("Codec lost bonus for " + type);
                return;
            }
        }

        // Confirm the new field names are what got serialized (not the old corrosive/destructive/etc.)
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
    }

    // ==================== Multipliers ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 5)
    public void injectionMultiplierBoostsRawWithoutBias(GameTestHelper helper) {
        SpiritusChunk chunk = new SpiritusChunk();
        chunk.setInjectionMultiplier(1.25, SpiritusType.RAW, 100, 0);
        double added = chunk.addSpiritus(SpiritusType.RAW, 80);
        double total = chunk.getSpiritus(SpiritusType.RAW);
        if (Math.abs(total - 100.0) > 1e-6) {
            helper.fail("Expected RAW=100 (80 * 1.25 with no bias), got " + total + " (added=" + added + ")");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 5)
    public void injectionMultiplierRedirectsBonusToBiasAspect(GameTestHelper helper) {
        SpiritusChunk chunk = new SpiritusChunk();
        chunk.setInjectionMultiplier(1.25, SpiritusType.RUINA, 100, 0);
        chunk.addSpiritus(SpiritusType.RAW, 80);

        double raw = chunk.getSpiritus(SpiritusType.RAW);
        double ruina = chunk.getSpiritus(SpiritusType.RUINA);
        if (Math.abs(raw - 80.0) > 1e-6) {
            helper.fail("Expected RAW=80 (base), got " + raw);
            return;
        }
        if (Math.abs(ruina - 20.0) > 1e-6) {
            helper.fail("Expected RUINA=20 (25% bonus redirected from RAW), got " + ruina);
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 5)
    public void injectionMultiplierDoesNotRedirectAspectedAdditions(GameTestHelper helper) {
        SpiritusChunk chunk = new SpiritusChunk();
        chunk.setInjectionMultiplier(1.25, SpiritusType.NIHILUM, 100, 0);
        chunk.addSpiritus(SpiritusType.RUINA, 80);

        double ruina = chunk.getSpiritus(SpiritusType.RUINA);
        double nihilum = chunk.getSpiritus(SpiritusType.NIHILUM);
        if (Math.abs(ruina - 100.0) > 1e-6) {
            helper.fail("Expected RUINA=100 (80 * 1.25, bias only redirects RAW), got " + ruina);
            return;
        }
        if (nihilum != 0.0) {
            helper.fail("Expected NIHILUM=0 (bias should not redirect non-RAW), got " + nihilum);
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 5)
    public void noMultiplierIsExactAdd(GameTestHelper helper) {
        SpiritusChunk chunk = new SpiritusChunk();
        chunk.addSpiritus(SpiritusType.RAW, 50);
        double raw = chunk.getSpiritus(SpiritusType.RAW);
        if (Math.abs(raw - 50.0) > 1e-6) {
            helper.fail("Expected exact RAW=50 with no multiplier, got " + raw);
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 5)
    public void multipliersDecayAfterExpiry(GameTestHelper helper) {
        SpiritusChunk chunk = new SpiritusChunk();
        chunk.setGrowthMultiplier(2.0, 50, 1000);
        chunk.setInjectionMultiplier(1.25, SpiritusType.RUINA, 50, 1000);
        chunk.tickRitualBuffs(1040);
        if (chunk.getGrowthMultiplier() != 2.0) {
            helper.fail("Growth multiplier should still be 2.0 before expiry, got " + chunk.getGrowthMultiplier());
            return;
        }
        chunk.tickRitualBuffs(1100);
        if (chunk.getGrowthMultiplier() != 1.0) {
            helper.fail("Growth multiplier should decay to 1.0 past expiry, got " + chunk.getGrowthMultiplier());
            return;
        }
        if (chunk.getInjectionMultiplier() != 1.0) {
            helper.fail("Injection multiplier should decay to 1.0 past expiry, got " + chunk.getInjectionMultiplier());
            return;
        }
        if (chunk.getInjectionAspectBias() != SpiritusType.RAW) {
            helper.fail("Aspect bias should reset to RAW on injection decay, got " + chunk.getInjectionAspectBias());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 5)
    public void copyPreservesTransientBuffs(GameTestHelper helper) {
        SpiritusChunk chunk = new SpiritusChunk();
        chunk.setGrowthMultiplier(2.0, 200, 500);
        chunk.setInjectionMultiplier(1.5, SpiritusType.VINDICTA, 200, 500);

        SpiritusChunk copy = chunk.copy();
        if (copy.getGrowthMultiplier() != 2.0) {
            helper.fail("copy() lost growth multiplier (got " + copy.getGrowthMultiplier() + ")");
            return;
        }
        if (copy.getInjectionMultiplier() != 1.5) {
            helper.fail("copy() lost injection multiplier (got " + copy.getInjectionMultiplier() + ")");
            return;
        }
        if (copy.getInjectionAspectBias() != SpiritusType.VINDICTA) {
            helper.fail("copy() lost aspect bias (got " + copy.getInjectionAspectBias() + ")");
            return;
        }
        helper.succeed();
    }

    // ==================== Through addSpiritusToChunk ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 10)
    public void multiplierAppliesViaWorldHandler(GameTestHelper helper) {
        BlockPos pos = new BlockPos(3, 1, 2);
        BlockPos absPos = helper.absolutePos(pos);
        LevelChunk chunk = helper.getLevel().getChunkAt(absPos);

        // Reset chunk and install a +25% RUINA-bias multiplier
        SpiritusChunk fresh = new SpiritusChunk();
        long now = helper.getLevel().getGameTime();
        fresh.setInjectionMultiplier(1.25, SpiritusType.RUINA, 200, now);
        chunk.setData(NVDataAttachments.SPIRITUS_CHUNK.get(), fresh);

        // First injection: 40 raw -> +40 RAW (base) + 10 RUINA (bias)
        WorldSpiritusHandler.addSpiritusToChunk(helper.getLevel(), absPos, SpiritusType.RAW, 40);
        double raw = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), absPos, SpiritusType.RAW);
        double ruina = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), absPos, SpiritusType.RUINA);
        if (Math.abs(raw - 40.0) > 1e-6) {
            helper.fail("Expected RAW=40 after first injection, got " + raw);
            return;
        }
        if (Math.abs(ruina - 10.0) > 1e-6) {
            helper.fail("Expected RUINA=10 (bias redirect), got " + ruina);
            return;
        }

        // Second injection: regression for copy() losing transient state. +30 RAW + 7.5 RUINA expected.
        WorldSpiritusHandler.addSpiritusToChunk(helper.getLevel(), absPos, SpiritusType.RAW, 30);
        double raw2 = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), absPos, SpiritusType.RAW);
        double ruina2 = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), absPos, SpiritusType.RUINA);
        if (Math.abs(raw2 - 70.0) > 1e-6) {
            helper.fail("Expected RAW=70 after second injection, got " + raw2);
            return;
        }
        if (Math.abs(ruina2 - 17.5) > 1e-6) {
            helper.fail("Expected RUINA=17.5 after second injection (multiplier should survive copy()), got " + ruina2);
            return;
        }
        helper.succeed();
    }

    // ==================== Fully-grown gating ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 10)
    public void crystalReachesMaxAge(GameTestHelper helper) {
        BlockPos crystalPos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(crystalPos, NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get().defaultBlockState()
                .setValue(BlockSpiritusCrystal.AGE, 6));

        helper.runAfterDelay(2, () -> {
            BlockState state = helper.getBlockState(crystalPos);
            if (!state.hasProperty(BlockSpiritusCrystal.AGE)) {
                helper.fail("Crystal block missing AGE property");
                return;
            }
            if (state.getValue(BlockSpiritusCrystal.AGE) != 6) {
                helper.fail("Expected AGE=6 (fully grown), got " + state.getValue(BlockSpiritusCrystal.AGE));
                return;
            }
            helper.succeed();
        });
    }
}
