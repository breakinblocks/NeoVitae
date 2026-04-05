package com.breakinblocks.neovitae.api.stream;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

/**
 * Pre-configured {@link StreamEffect} presets themed around weighty magical energy.
 * <p>
 * Each preset returns a {@link StreamEffect.Builder} with all visual parameters pre-set.
 * The caller can override any setting before calling {@code .build().sendToNearby(...)}.
 *
 * <h3>Stream Presets</h3>
 * <ul>
 *   <li>{@link #bloodTendril} &ndash; Viscous crimson tendril, droops with weight</li>
 *   <li>{@link #soulSiphon} &ndash; Thin ghostly blue-white wisp, fast and ethereal</li>
 *   <li>{@link #voidTendril} &ndash; Thick near-black mass, slow and oppressive</li>
 *   <li>{@link #lifePulse} &ndash; Warm golden surge, quick and radiant</li>
 *   <li>{@link #demonTether} &ndash; Fiery orange chain, rises with heat</li>
 *   <li>{@link #corruptionSeep} &ndash; Sickly green ooze, sluggish and heavy</li>
 *   <li>{@link #arcaneBolt} &ndash; Crackling blue-white thread, near-instant</li>
 * </ul>
 *
 * <h3>Blob Presets</h3>
 * <ul>
 *   <li>{@link #emberMote} &ndash; Small floating ember</li>
 *   <li>{@link #soulWisp} &ndash; Pale drifting wisp</li>
 *   <li>{@link #voidMark} &ndash; Heavy dark stain in the air</li>
 * </ul>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * // Direct use
 * StreamPresets.bloodTendril(player, altarPos)
 *     .build()
 *     .sendToNearby(serverLevel, altarPos, 128);
 *
 * // Customized preset
 * StreamPresets.voidTendril(sourcePos, targetPos)
 *     .scale(0.25f)    // override thickness
 *     .glow(true)      // add glow
 *     .build()
 *     .sendToNearby(serverLevel, targetPos, 128);
 *
 * // Blob at a position
 * StreamPresets.emberMote(blockPos)
 *     .build()
 *     .sendToNearby(serverLevel, blockPos, 64);
 * }</pre>
 */
public final class StreamPresets {

    private StreamPresets() {}

    /**
     * Viscous crimson tendril. Droops under its own weight with a slow,
     * deliberate spiral into the target. Thick enough to feel like flowing blood.
     */
    public static StreamEffect.Builder bloodTendril(Entity source, BlockPos target) {
        return applyBloodTendril(StreamEffect.builder(source).to(target));
    }

    /** @see #bloodTendril(Entity, BlockPos) */
    public static StreamEffect.Builder bloodTendril(BlockPos source, BlockPos target) {
        return applyBloodTendril(StreamEffect.builder(source).to(target));
    }

    private static StreamEffect.Builder applyBloodTendril(StreamEffect.Builder b) {
        return b.color(0x990011)
                .scale(0.1f)
                .speed(2.0f)
                .gravity(0.25f)
                .wobble(0.03f)
                .wobbleFrequency(0.8f)
                .spiralInto(true)
                .spiralRadius(0.35f)
                .spiralSpeed(0.35f)
                .approachHeight(1.0f)
                .alphaStart(0.08f)
                .alphaEnd(0.95f);
    }

    /**
     * Thin ghostly blue-white wisp. Nearly weightless, fast, and transparent &mdash;
     * like a soul being drawn from a body. Glows faintly.
     */
    public static StreamEffect.Builder soulSiphon(Entity source, BlockPos target) {
        return applySoulSiphon(StreamEffect.builder(source).to(target));
    }

    /** @see #soulSiphon(Entity, BlockPos) */
    public static StreamEffect.Builder soulSiphon(BlockPos source, BlockPos target) {
        return applySoulSiphon(StreamEffect.builder(source).to(target));
    }

    private static StreamEffect.Builder applySoulSiphon(StreamEffect.Builder b) {
        return b.color(0x22AADD)
                .scale(0.14f)
                .speed(2.2f)
                .gravity(0.03f)
                .wobble(0.04f)
                .wobbleFrequency(1.2f)
                .spiralInto(true)
                .spiralRadius(0.25f)
                .spiralSpeed(0.5f)
                .approachHeight(0.8f)
                .alphaStart(0.15f)
                .alphaEnd(0.8f)
                .glow(true);
    }

    /**
     * Thick near-black tendril of void energy. Moves slowly and deliberately,
     * sagging heavily under its own mass. Does not spiral &mdash; it simply
     * crashes into the target with oppressive weight.
     */
    public static StreamEffect.Builder voidTendril(Entity source, BlockPos target) {
        return applyVoidTendril(StreamEffect.builder(source).to(target));
    }

    /** @see #voidTendril(Entity, BlockPos) */
    public static StreamEffect.Builder voidTendril(BlockPos source, BlockPos target) {
        return applyVoidTendril(StreamEffect.builder(source).to(target));
    }

    private static StreamEffect.Builder applyVoidTendril(StreamEffect.Builder b) {
        return b.color(0x1A0028)
                .scale(0.18f)
                .speed(1.0f)
                .gravity(0.15f)
                .wobble(0.005f)
                .wobbleFrequency(0.3f)
                .spiralInto(false)
                .approachHeight(0.3f)
                .alphaStart(0.5f)
                .alphaEnd(1.0f)
                .drainSpeed(1.0f)
                .tubeSegments(12);
    }

    /**
     * Warm golden surge of vitality. Travels quickly in a wide arc, spiraling
     * gracefully down from above. Radiates light.
     */
    public static StreamEffect.Builder lifePulse(Entity source, BlockPos target) {
        return applyLifePulse(StreamEffect.builder(source).to(target));
    }

    /** @see #lifePulse(Entity, BlockPos) */
    public static StreamEffect.Builder lifePulse(BlockPos source, BlockPos target) {
        return applyLifePulse(StreamEffect.builder(source).to(target));
    }

    private static StreamEffect.Builder applyLifePulse(StreamEffect.Builder b) {
        return b.color(0xAA0011)
                .scale(0.08f)
                .speed(2.0f)
                .gravity(0.0f)
                .wobble(0.02f)
                .wobbleFrequency(1.2f)
                .spiralInto(true)
                .spiralRadius(0.5f)
                .spiralSpeed(0.45f)
                .approachHeight(1.5f)
                .alphaStart(0.15f)
                .alphaEnd(1.0f)
                .glow(true)
                .drainSpeed(1.5f);
    }

    /**
     * Fiery orange chain of demon will. Aggressive wobble, rises slightly
     * against gravity like heated air. Emits a harsh glow.
     */
    public static StreamEffect.Builder demonTether(Entity source, BlockPos target) {
        return applyDemonTether(StreamEffect.builder(source).to(target));
    }

    /** @see #demonTether(Entity, BlockPos) */
    public static StreamEffect.Builder demonTether(BlockPos source, BlockPos target) {
        return applyDemonTether(StreamEffect.builder(source).to(target));
    }

    private static StreamEffect.Builder applyDemonTether(StreamEffect.Builder b) {
        return b.color(0x180028)
                .scale(0.12f)
                .speed(1.3f)
                .gravity(-0.08f)
                .wobble(0.06f)
                .wobbleFrequency(1.6f)
                .spiralInto(true)
                .spiralRadius(0.3f)
                .spiralSpeed(0.6f)
                .approachHeight(1.2f)
                .alphaStart(0.2f)
                .alphaEnd(1.0f)
                .glow(true);
    }

    /**
     * Sickly green ooze of corruption. Sluggish and heavy, drooping low
     * before reluctantly reaching its target. Undulates slowly like
     * something alive. No spiral &mdash; it seeps in from below.
     */
    public static StreamEffect.Builder corruptionSeep(Entity source, BlockPos target) {
        return applyCorruptionSeep(StreamEffect.builder(source).to(target));
    }

    /** @see #corruptionSeep(Entity, BlockPos) */
    public static StreamEffect.Builder corruptionSeep(BlockPos source, BlockPos target) {
        return applyCorruptionSeep(StreamEffect.builder(source).to(target));
    }

    private static StreamEffect.Builder applyCorruptionSeep(StreamEffect.Builder b) {
        return b.color(0x44BB22)
                .scale(0.15f)
                .speed(1.2f)
                .gravity(0.3f)
                .wobble(0.05f)
                .wobbleFrequency(0.5f)
                .spiralInto(true)
                .spiralRadius(0.25f)
                .spiralSpeed(0.3f)
                .approachHeight(0.5f)
                .alphaStart(0.4f)
                .alphaEnd(0.85f)
                .drainSpeed(1.2f)
                .tubeSegments(10);
    }

    /**
     * Wispy violet thread of arcane energy. Thin and fast, the body jitters
     * with a faint wobble and leaves a fuzzy trail of glow particles behind
     * the head as it arcs into its target.
     */
    public static StreamEffect.Builder arcaneBolt(Entity source, BlockPos target) {
        return applyArcaneBolt(StreamEffect.builder(source).to(target));
    }

    /** @see #arcaneBolt(Entity, BlockPos) */
    public static StreamEffect.Builder arcaneBolt(BlockPos source, BlockPos target) {
        return applyArcaneBolt(StreamEffect.builder(source).to(target));
    }

    private static StreamEffect.Builder applyArcaneBolt(StreamEffect.Builder b) {
        return b.color(0x8822EE)
                .scale(0.022f)
                .speed(5.0f)
                .gravity(0.0f)
                .wobble(0.08f)
                .wobbleFrequency(2.4f)
                .spiralInto(false)
                .approachHeight(0.0f)
                .alphaStart(0.35f)
                .alphaEnd(0.9f)
                .glow(true)
                .drainSpeed(4.0f)
                .trailDensity(2);
    }

    private static StreamEffect.Builder applyBlockBoltBase(StreamEffect.Builder b, BlockyMode mode, float scale) {
        return b.color(0x880011)
                .scale(scale)
                .speed(2.0f)
                .gravity(0.0f)
                .wobble(0.0f)
                .wobbleFrequency(0.0f)
                .spiralInto(false)
                .approachHeight(0.0f)
                .alphaStart(0.3f)
                .alphaEnd(1.0f)
                .glow(true)
                .blockyMode(mode)
                .drainSpeed(1.5f);
    }

    public static StreamEffect.Builder blockBolt(Entity source, BlockPos target) {
        return applyBlockBolt(StreamEffect.builder(source).to(target));
    }

    public static StreamEffect.Builder blockBolt(BlockPos source, BlockPos target) {
        return applyBlockBolt(StreamEffect.builder(source).to(target));
    }

    private static StreamEffect.Builder applyBlockBolt(StreamEffect.Builder b) {
        return b.color(0x880011)
                .scale(0.12f)
                .speed(2.0f)
                .gravity(0.02f)
                .wobble(0.0f)
                .wobbleFrequency(0.0f)
                .spiralInto(true)
                .spiralRadius(0.2f)
                .spiralSpeed(0.3f)
                .approachHeight(0.8f)
                .alphaStart(0.3f)
                .alphaEnd(1.0f)
                .glow(true)
                .tubeSegments(4)
                .blockyMode(BlockyMode.BLOCKY)
                .drainSpeed(1.5f);
    }

    public static StreamEffect.Builder blockBolt2(Entity source, BlockPos target) {
        return applyBlockBoltBase(StreamEffect.builder(source).to(target), BlockyMode.BLOCKY_STEPS, 0.12f).tubeSegments(4);
    }

    public static StreamEffect.Builder blockBolt2(BlockPos source, BlockPos target) {
        return applyBlockBoltBase(StreamEffect.builder(source).to(target), BlockyMode.BLOCKY_STEPS, 0.12f).tubeSegments(4);
    }

    public static StreamEffect.Builder blockBolt3(Entity source, BlockPos target) {
        return applyBlockBoltBase(StreamEffect.builder(source).to(target), BlockyMode.BLOCKY_UNIFORM, 0.12f).tubeSegments(4);
    }

    public static StreamEffect.Builder blockBolt3(BlockPos source, BlockPos target) {
        return applyBlockBoltBase(StreamEffect.builder(source).to(target), BlockyMode.BLOCKY_UNIFORM, 0.12f).tubeSegments(4);
    }

    public static StreamEffect.Builder blockBolt4(Entity source, BlockPos target) {
        return applyBlockBoltBase(StreamEffect.builder(source).to(target), BlockyMode.BLOCKY_BOX, 0.12f);
    }

    public static StreamEffect.Builder blockBolt4(BlockPos source, BlockPos target) {
        return applyBlockBoltBase(StreamEffect.builder(source).to(target), BlockyMode.BLOCKY_BOX, 0.12f);
    }

    public static StreamEffect.Builder blockBolt5(Entity source, BlockPos target) {
        return applyBlockBoltBase(StreamEffect.builder(source).to(target), BlockyMode.BLOCKY_BEAM, 0.12f);
    }

    public static StreamEffect.Builder blockBolt5(BlockPos source, BlockPos target) {
        return applyBlockBoltBase(StreamEffect.builder(source).to(target), BlockyMode.BLOCKY_BEAM, 0.12f);
    }

    public static StreamEffect.Builder blockBolt6(Entity source, BlockPos target) {
        return applyBlockBoltBase(StreamEffect.builder(source).to(target), BlockyMode.BLOCKY_BEAM, 0.05f);
    }

    public static StreamEffect.Builder blockBolt6(BlockPos source, BlockPos target) {
        return applyBlockBoltBase(StreamEffect.builder(source).to(target), BlockyMode.BLOCKY_BEAM, 0.05f);
    }

    public static StreamEffect.Builder emberMote(BlockPos pos) {
        return applyEmberMote(StreamEffect.builder(pos));
    }

    /** @see #emberMote(BlockPos) */
    public static StreamEffect.Builder emberMote(double x, double y, double z) {
        return applyEmberMote(StreamEffect.builder(x, y, z));
    }

    private static StreamEffect.Builder applyEmberMote(StreamEffect.Builder b) {
        return b.stationary()
                .color(0xFF6600)
                .scale(0.12f)
                .lifetime(40)
                .alpha(0.8f)
                .wobble(0.02f)
                .glow(true);
    }

    /**
     * Pale drifting wisp. A faint, ghostly presence that hovers in place.
     */
    public static StreamEffect.Builder soulWisp(BlockPos pos) {
        return applySoulWisp(StreamEffect.builder(pos));
    }

    /** @see #soulWisp(BlockPos) */
    public static StreamEffect.Builder soulWisp(double x, double y, double z) {
        return applySoulWisp(StreamEffect.builder(x, y, z));
    }

    private static StreamEffect.Builder applySoulWisp(StreamEffect.Builder b) {
        return b.stationary()
                .color(0xAADDFF)
                .scale(0.08f)
                .lifetime(80)
                .alpha(0.45f)
                .wobble(0.05f)
                .wobbleFrequency(0.6f)
                .glow(true);
    }

    /**
     * A dense, slow-pulsing void residue that takes its time to fade.
     */
    public static StreamEffect.Builder voidMark(BlockPos pos) {
        return applyVoidMark(StreamEffect.builder(pos));
    }

    /** @see #voidMark(BlockPos) */
    public static StreamEffect.Builder voidMark(double x, double y, double z) {
        return applyVoidMark(StreamEffect.builder(x, y, z));
    }

    private static StreamEffect.Builder applyVoidMark(StreamEffect.Builder b) {
        return b.stationary()
                .color(0x1A0033)
                .scale(0.25f)
                .lifetime(100)
                .alpha(0.7f)
                .wobble(0.08f)
                .wobbleFrequency(0.3f)
                .glow(false)
                .tubeSegments(12);
    }
}
