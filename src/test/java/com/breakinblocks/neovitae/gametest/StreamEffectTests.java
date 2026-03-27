package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.api.stream.StreamEffect;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import io.netty.buffer.Unpooled;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class StreamEffectTests {

    // ==================== Builder Defaults ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void builderCreatesWithDefaults(GameTestHelper helper) {
        StreamEffect effect = StreamEffect.builder(0, 64, 0).to(5, 64, 5).build();

        if (effect.scale != 0.1f) { helper.fail("Default scale should be 0.1, got " + effect.scale); return; }
        if (effect.speed != 1.0f) { helper.fail("Default speed should be 1.0, got " + effect.speed); return; }
        if (effect.gravity != 0.2f) { helper.fail("Default gravity should be 0.2, got " + effect.gravity); return; }
        if (effect.color != 0xFFFFFF) { helper.fail("Default color should be 0xFFFFFF, got " + effect.color); return; }
        if (effect.stationary) { helper.fail("Default should not be stationary"); return; }
        if (effect.glow) { helper.fail("Default should not glow"); return; }
        if (!effect.spiralInto) { helper.fail("Default should spiral into target"); return; }
        if (effect.tubeSegments != 8) { helper.fail("Default tubeSegments should be 8, got " + effect.tubeSegments); return; }
        if (effect.alphaStart != 0.1f) { helper.fail("Default alphaStart should be 0.1, got " + effect.alphaStart); return; }
        if (effect.alphaEnd != 1.0f) { helper.fail("Default alphaEnd should be 1.0, got " + effect.alphaEnd); return; }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void builderAppliesCustomValues(GameTestHelper helper) {
        StreamEffect effect = StreamEffect.builder(1, 2, 3)
                .to(10, 20, 30)
                .color(0xFF0000)
                .scale(0.5f)
                .speed(2.5f)
                .gravity(-0.1f)
                .wobble(0.1f)
                .wobbleFrequency(2.0f)
                .spiralInto(false)
                .spiralRadius(0.8f)
                .approachHeight(2.0f)
                .glow(true)
                .alphaStart(0.5f)
                .alphaEnd(0.8f)
                .lifetime(100)
                .drainSpeed(0.5f)
                .tubeSegments(16)
                .build();

        if (effect.sourceX != 1.0) { helper.fail("sourceX mismatch"); return; }
        if (effect.targetX != 10.0) { helper.fail("targetX mismatch"); return; }
        if (effect.color != 0xFF0000) { helper.fail("color mismatch"); return; }
        if (effect.scale != 0.5f) { helper.fail("scale mismatch"); return; }
        if (effect.speed != 2.5f) { helper.fail("speed mismatch"); return; }
        if (effect.gravity != -0.1f) { helper.fail("gravity mismatch"); return; }
        if (effect.wobbleAmplitude != 0.1f) { helper.fail("wobble mismatch"); return; }
        if (effect.wobbleFrequency != 2.0f) { helper.fail("wobbleFrequency mismatch"); return; }
        if (effect.spiralInto) { helper.fail("spiralInto should be false"); return; }
        if (effect.spiralRadius != 0.8f) { helper.fail("spiralRadius mismatch"); return; }
        if (effect.approachHeight != 2.0f) { helper.fail("approachHeight mismatch"); return; }
        if (!effect.glow) { helper.fail("glow should be true"); return; }
        if (effect.alphaStart != 0.5f) { helper.fail("alphaStart mismatch"); return; }
        if (effect.alphaEnd != 0.8f) { helper.fail("alphaEnd mismatch"); return; }
        if (effect.lifetime != 100) { helper.fail("lifetime mismatch"); return; }
        if (effect.drainSpeed != 0.5f) { helper.fail("drainSpeed mismatch"); return; }
        if (effect.tubeSegments != 16) { helper.fail("tubeSegments mismatch"); return; }
        helper.succeed();
    }

    // ==================== BlockPos/Entity Convenience ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void builderFromBlockPosCenters(GameTestHelper helper) {
        StreamEffect effect = StreamEffect.builder(new BlockPos(5, 10, 15)).to(new BlockPos(20, 30, 40)).build();

        if (effect.sourceX != 5.5) { helper.fail("BlockPos source X should be 5.5, got " + effect.sourceX); return; }
        if (effect.sourceY != 10.5) { helper.fail("BlockPos source Y should be 10.5, got " + effect.sourceY); return; }
        if (effect.sourceZ != 15.5) { helper.fail("BlockPos source Z should be 15.5, got " + effect.sourceZ); return; }
        if (effect.targetX != 20.5) { helper.fail("BlockPos target X should be 20.5, got " + effect.targetX); return; }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void stationaryFlagSets(GameTestHelper helper) {
        StreamEffect effect = StreamEffect.builder(0, 64, 0).stationary().lifetime(60).build();

        if (!effect.stationary) { helper.fail("Should be stationary"); return; }
        if (effect.lifetime != 60) { helper.fail("Lifetime should be 60, got " + effect.lifetime); return; }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void uniformAlphaSetsStartAndEnd(GameTestHelper helper) {
        StreamEffect effect = StreamEffect.builder(0, 0, 0).to(1, 1, 1).alpha(0.7f).build();

        if (effect.alphaStart != 0.7f) { helper.fail("alphaStart should be 0.7, got " + effect.alphaStart); return; }
        if (effect.alphaEnd != 0.7f) { helper.fail("alphaEnd should be 0.7, got " + effect.alphaEnd); return; }
        helper.succeed();
    }

    // ==================== Serialization ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void serializeAndDeserializePreservesAllFields(GameTestHelper helper) {
        StreamEffect original = StreamEffect.builder(1.5, 64.75, -3.2)
                .to(100.1, 200.2, 300.3)
                .color(0xABCDEF)
                .scale(0.33f)
                .speed(1.7f)
                .gravity(-0.05f)
                .wobble(0.09f)
                .wobbleFrequency(3.0f)
                .spiralInto(false)
                .spiralRadius(0.77f)
                .spiralSpeed(0.55f)
                .approachHeight(1.8f)
                .glow(true)
                .alphaStart(0.22f)
                .alphaEnd(0.88f)
                .lifetime(42)
                .drainSpeed(2.3f)
                .tubeSegments(12)
                .stationary()
                .build();

        // Encode
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        original.encode(buf);

        // Decode
        StreamEffect decoded = StreamEffect.decode(buf);
        buf.release();

        if (decoded.sourceX != original.sourceX) { helper.fail("sourceX mismatch after decode"); return; }
        if (decoded.sourceY != original.sourceY) { helper.fail("sourceY mismatch after decode"); return; }
        if (decoded.sourceZ != original.sourceZ) { helper.fail("sourceZ mismatch after decode"); return; }
        if (decoded.targetX != original.targetX) { helper.fail("targetX mismatch after decode"); return; }
        if (decoded.targetY != original.targetY) { helper.fail("targetY mismatch after decode"); return; }
        if (decoded.targetZ != original.targetZ) { helper.fail("targetZ mismatch after decode"); return; }
        if (decoded.stationary != original.stationary) { helper.fail("stationary mismatch after decode"); return; }
        if (decoded.color != original.color) { helper.fail("color mismatch after decode"); return; }
        if (decoded.scale != original.scale) { helper.fail("scale mismatch after decode"); return; }
        if (decoded.speed != original.speed) { helper.fail("speed mismatch after decode"); return; }
        if (decoded.gravity != original.gravity) { helper.fail("gravity mismatch after decode"); return; }
        if (decoded.wobbleAmplitude != original.wobbleAmplitude) { helper.fail("wobble mismatch after decode"); return; }
        if (decoded.wobbleFrequency != original.wobbleFrequency) { helper.fail("wobbleFrequency mismatch after decode"); return; }
        if (decoded.spiralInto != original.spiralInto) { helper.fail("spiralInto mismatch after decode"); return; }
        if (decoded.spiralRadius != original.spiralRadius) { helper.fail("spiralRadius mismatch after decode"); return; }
        if (decoded.spiralSpeed != original.spiralSpeed) { helper.fail("spiralSpeed mismatch after decode"); return; }
        if (decoded.approachHeight != original.approachHeight) { helper.fail("approachHeight mismatch after decode"); return; }
        if (decoded.glow != original.glow) { helper.fail("glow mismatch after decode"); return; }
        if (decoded.alphaStart != original.alphaStart) { helper.fail("alphaStart mismatch after decode"); return; }
        if (decoded.alphaEnd != original.alphaEnd) { helper.fail("alphaEnd mismatch after decode"); return; }
        if (decoded.lifetime != original.lifetime) { helper.fail("lifetime mismatch after decode"); return; }
        if (decoded.drainSpeed != original.drainSpeed) { helper.fail("drainSpeed mismatch after decode"); return; }
        if (decoded.tubeSegments != original.tubeSegments) { helper.fail("tubeSegments mismatch after decode"); return; }
        helper.succeed();
    }

    // ==================== Presets ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void bloodTendrilPresetHasCorrectDefaults(GameTestHelper helper) {
        StreamEffect effect = StreamPresets.bloodTendril(new BlockPos(0, 64, 0), new BlockPos(5, 64, 5)).build();
        if (effect.color != 0x990011) { helper.fail("bloodTendril color should be 0x990011, got " + Integer.toHexString(effect.color)); return; }
        if (!effect.spiralInto) { helper.fail("bloodTendril should spiral"); return; }
        if (effect.glow) { helper.fail("bloodTendril should not glow"); return; }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void arcaneBoltPresetIsfast(GameTestHelper helper) {
        StreamEffect effect = StreamPresets.arcaneBolt(new BlockPos(0, 64, 0), new BlockPos(5, 64, 5)).build();
        if (effect.speed < 3.0f) { helper.fail("arcaneBolt should be fast (>3.0), got " + effect.speed); return; }
        if (!effect.glow) { helper.fail("arcaneBolt should glow"); return; }
        if (effect.spiralInto) { helper.fail("arcaneBolt should not spiral"); return; }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void voidTendrilPresetIsHeavy(GameTestHelper helper) {
        StreamEffect effect = StreamPresets.voidTendril(new BlockPos(0, 64, 0), new BlockPos(5, 64, 5)).build();
        if (effect.gravity < 0.5f) { helper.fail("voidTendril gravity should be heavy (>0.5), got " + effect.gravity); return; }
        if (effect.speed > 0.5f) { helper.fail("voidTendril should be slow (<0.5), got " + effect.speed); return; }
        if (effect.spiralInto) { helper.fail("voidTendril should not spiral"); return; }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void demonTetherHasNegativeGravity(GameTestHelper helper) {
        StreamEffect effect = StreamPresets.demonTether(new BlockPos(0, 64, 0), new BlockPos(5, 64, 5)).build();
        if (effect.gravity >= 0) { helper.fail("demonTether should have negative gravity (rises), got " + effect.gravity); return; }
        if (!effect.glow) { helper.fail("demonTether should glow"); return; }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void emberMoteIsStationary(GameTestHelper helper) {
        StreamEffect effect = StreamPresets.emberMote(new BlockPos(0, 64, 0)).build();
        if (!effect.stationary) { helper.fail("emberMote should be stationary"); return; }
        if (!effect.glow) { helper.fail("emberMote should glow"); return; }
        if (effect.lifetime <= 0) { helper.fail("emberMote should have a positive lifetime"); return; }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void presetCanBeOverridden(GameTestHelper helper) {
        StreamEffect effect = StreamPresets.bloodTendril(new BlockPos(0, 64, 0), new BlockPos(5, 64, 5))
                .color(0x00FF00)
                .glow(true)
                .scale(0.5f)
                .build();

        if (effect.color != 0x00FF00) { helper.fail("Overridden color should be 0x00FF00"); return; }
        if (!effect.glow) { helper.fail("Overridden glow should be true"); return; }
        if (effect.scale != 0.5f) { helper.fail("Overridden scale should be 0.5"); return; }
        helper.succeed();
    }
}
