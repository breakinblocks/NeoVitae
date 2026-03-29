package com.breakinblocks.neovitae.api.stream;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import com.breakinblocks.neovitae.common.network.NVPayloads;
import com.breakinblocks.neovitae.common.network.StreamPayload;

/**
 * Immutable configuration for a visual energy stream or stationary blob effect.
 * <p>
 * Use the {@link Builder} to construct an effect with customized appearance and behavior,
 * then call {@link #sendToNearby} to broadcast it to all players in range.
 *
 * <h3>Stream Mode (default)</h3>
 * A 3D tube flows from a source position to a target position with configurable physics,
 * wobble, spiral-into-target behavior, and alpha gradient. The stream approaches a point
 * above the target, spirals downward into it, then the tail accelerates to the destination
 * and is absorbed.
 *
 * <h3>Stationary Blob Mode</h3>
 * A pulsing glowing blob at a fixed position. No movement, no target. Expires after
 * {@link #lifetime} ticks.
 *
 * <h3>Usage Examples</h3>
 * <pre>{@code
 * // Blood stream from an entity to a block
 * StreamEffect.builder(player)
 *     .to(altarPos)
 *     .color(0xBB0000)
 *     .build()
 *     .sendToNearby(serverLevel, altarPos, 128);
 *
 * // Fast bright beam between two positions
 * StreamEffect.builder(sourcePos)
 *     .to(targetPos)
 *     .color(0x00CCFF)
 *     .speed(2.0f)
 *     .scale(0.2f)
 *     .glow(true)
 *     .wobble(0.01f)
 *     .build()
 *     .sendToNearby(serverLevel, sourcePos, 128);
 *
 * // Stationary glowing orb
 * StreamEffect.builder(blockPos)
 *     .color(0xFF00FF)
 *     .scale(0.3f)
 *     .stationary()
 *     .lifetime(60)
 *     .glow(true)
 *     .build()
 *     .sendToNearby(serverLevel, blockPos, 64);
 *
 * // Slow, heavy tendril with no spiral
 * StreamEffect.builder(fromX, fromY, fromZ)
 *     .to(toX, toY, toZ)
 *     .color(0x332211)
 *     .speed(0.5f)
 *     .gravity(0.5f)
 *     .scale(0.15f)
 *     .spiralInto(false)
 *     .build()
 *     .sendToNearby(serverLevel, targetPos, 128);
 * }</pre>
 *
 * @see Builder
 */
public final class StreamEffect {

    /** Source X position (world-space). */
    public final double sourceX, sourceY, sourceZ;
    /** Target X position (world-space). Ignored if {@link #stationary}. */
    public final double targetX, targetY, targetZ;

    /** If true, renders as a pulsing stationary blob instead of a flowing stream. */
    public final boolean stationary;

    /** Packed RGB color (e.g. {@code 0xBB0000} for blood red). */
    public final int color;
    /** Base tube radius in blocks. Default {@code 0.1f}. */
    public final float scale;
    /** Alpha at the tail/source end of the stream. Default {@code 0.1f}. */
    public final float alphaStart;
    /** Alpha at the head/target end of the stream. Default {@code 1.0f}. */
    public final float alphaEnd;
    /** If true, uses emissive shader (fullbright, ignores world lighting). */
    public final boolean glow;
    /** Number of sides in the tube cross-section. Default {@code 8}. Higher = smoother. */
    public final int tubeSegments;

    /** Overall speed multiplier for velocity and attraction. Default {@code 1.0f}. */
    public final float speed;
    /** Gravity factor applied to Y velocity each tick. Default {@code 0.2f}. Set to 0 for no gravity. */
    public final float gravity;
    /** Amplitude of per-point sinusoidal wobble in blocks. Default {@code 0.03f}. */
    public final float wobbleAmplitude;
    /** Frequency multiplier for wobble oscillation. Default {@code 1.0f}. */
    public final float wobbleFrequency;

    /** If true, the stream spirals downward into the target on arrival. Default {@code true}. */
    public final boolean spiralInto;
    /** Radius of the spiral orbit in blocks. Default {@code 0.35f}. */
    public final float spiralRadius;
    /** Rotation speed of the spiral in radians/tick. Default {@code 0.4f}. */
    public final float spiralSpeed;
    /** Height above the target to approach before spiraling. Default {@code 1.0f}. */
    public final float approachHeight;

    /** Maximum lifetime in ticks. {@code 0} = auto-calculate from distance. */
    public final int lifetime;
    /** Multiplier for drain-phase acceleration when the tail is absorbed. Default {@code 1.0f}. */
    public final float drainSpeed;
    public final BlockyMode blockyMode;

    /** Entity ID to track as the target. {@code -1} = no tracking, use fixed coordinates. */
    public final int targetEntityId;

    private StreamEffect(Builder b) {
        this.sourceX = b.sourceX;
        this.sourceY = b.sourceY;
        this.sourceZ = b.sourceZ;
        this.targetX = b.targetX;
        this.targetY = b.targetY;
        this.targetZ = b.targetZ;
        this.stationary = b.stationary;
        this.color = b.color;
        this.scale = b.scale;
        this.alphaStart = b.alphaStart;
        this.alphaEnd = b.alphaEnd;
        this.glow = b.glow;
        this.tubeSegments = b.tubeSegments;
        this.speed = b.speed;
        this.gravity = b.gravity;
        this.wobbleAmplitude = b.wobbleAmplitude;
        this.wobbleFrequency = b.wobbleFrequency;
        this.spiralInto = b.spiralInto;
        this.spiralRadius = b.spiralRadius;
        this.spiralSpeed = b.spiralSpeed;
        this.approachHeight = b.approachHeight;
        this.lifetime = b.lifetime;
        this.drainSpeed = b.drainSpeed;
        this.blockyMode = b.blockyMode;
        this.targetEntityId = b.targetEntityId;
    }

    /**
     * Create a builder with an exact source position.
     */
    public static Builder builder(double x, double y, double z) {
        return new Builder(x, y, z);
    }

    /**
     * Create a builder sourced from an entity's upper chest (75% hitbox height).
     */
    public static Builder builder(Entity entity) {
        return new Builder(entity.getX(), entity.getY() + entity.getBbHeight() * 0.75, entity.getZ());
    }

    /**
     * Create a builder sourced from the center of a block.
     */
    public static Builder builder(BlockPos pos) {
        return new Builder(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    /**
     * Send this effect to all players within {@code radius} blocks of {@code center}.
     *
     * @param level  the server level
     * @param center the center position for range checking
     * @param radius broadcast radius in blocks
     */
    public void sendToNearby(ServerLevel level, BlockPos center, double radius) {
        NVPayloads.sendToNearby(level, center, radius, new StreamPayload(this));
    }

    /**
     * Send this effect to all players within {@code radius} blocks of the source position.
     */
    public void sendToNearby(ServerLevel level, double radius) {
        BlockPos center = BlockPos.containing(sourceX, sourceY, sourceZ);
        sendToNearby(level, center, radius);
    }

    /**
     * Serialize this effect to a network buffer.
     */
    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(sourceX);
        buf.writeDouble(sourceY);
        buf.writeDouble(sourceZ);
        buf.writeDouble(targetX);
        buf.writeDouble(targetY);
        buf.writeDouble(targetZ);
        buf.writeBoolean(stationary);
        buf.writeInt(color);
        buf.writeFloat(scale);
        buf.writeFloat(alphaStart);
        buf.writeFloat(alphaEnd);
        buf.writeBoolean(glow);
        buf.writeInt(tubeSegments);
        buf.writeFloat(speed);
        buf.writeFloat(gravity);
        buf.writeFloat(wobbleAmplitude);
        buf.writeFloat(wobbleFrequency);
        buf.writeBoolean(spiralInto);
        buf.writeFloat(spiralRadius);
        buf.writeFloat(spiralSpeed);
        buf.writeFloat(approachHeight);
        buf.writeInt(lifetime);
        buf.writeFloat(drainSpeed);
        buf.writeInt(blockyMode.ordinal());
        buf.writeInt(targetEntityId);
    }

    /**
     * Deserialize an effect from a network buffer.
     */
    public static StreamEffect decode(FriendlyByteBuf buf) {
        Builder b = new Builder(buf.readDouble(), buf.readDouble(), buf.readDouble());
        b.targetX = buf.readDouble();
        b.targetY = buf.readDouble();
        b.targetZ = buf.readDouble();
        b.stationary = buf.readBoolean();
        b.color = buf.readInt();
        b.scale = buf.readFloat();
        b.alphaStart = buf.readFloat();
        b.alphaEnd = buf.readFloat();
        b.glow = buf.readBoolean();
        b.tubeSegments = buf.readInt();
        b.speed = buf.readFloat();
        b.gravity = buf.readFloat();
        b.wobbleAmplitude = buf.readFloat();
        b.wobbleFrequency = buf.readFloat();
        b.spiralInto = buf.readBoolean();
        b.spiralRadius = buf.readFloat();
        b.spiralSpeed = buf.readFloat();
        b.approachHeight = buf.readFloat();
        b.lifetime = buf.readInt();
        b.drainSpeed = buf.readFloat();
        b.blockyMode = BlockyMode.values()[buf.readInt()];
        b.targetEntityId = buf.readInt();
        return new StreamEffect(b);
    }

    /**
     * Fluent builder for {@link StreamEffect}.
     * All parameters have sensible defaults; only set what you need to customize.
     */
    public static class Builder {
        private final double sourceX, sourceY, sourceZ;
        private double targetX, targetY, targetZ;
        private boolean stationary = false;
        private int color = 0xFFFFFF;
        private float scale = 0.1f;
        private float alphaStart = 0.1f;
        private float alphaEnd = 1.0f;
        private boolean glow = false;
        private int tubeSegments = 8;
        private float speed = 1.0f;
        private float gravity = 0.2f;
        private float wobbleAmplitude = 0.03f;
        private float wobbleFrequency = 1.0f;
        private boolean spiralInto = true;
        private float spiralRadius = 0.35f;
        private float spiralSpeed = 0.4f;
        private float approachHeight = 1.0f;
        private int lifetime = 0;
        private float drainSpeed = 1.0f;
        private BlockyMode blockyMode = BlockyMode.NONE;
        private int targetEntityId = -1;

        private Builder(double x, double y, double z) {
            this.sourceX = x;
            this.sourceY = y;
            this.sourceZ = z;
        }

        /** Set the target position (exact coordinates). */
        public Builder to(double x, double y, double z) {
            this.targetX = x; this.targetY = y; this.targetZ = z;
            return this;
        }

        /** Set the target to the center of a block. */
        public Builder to(BlockPos pos) {
            return to(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        }

        /** Set the target to an entity's position (center mass). */
        public Builder to(Entity entity) {
            return to(entity.getX(), entity.getY() + entity.getBbHeight() * 0.5, entity.getZ());
        }

        /** Set the target to an entity and track its position each tick on the client. */
        public Builder toTracked(Entity entity) {
            this.targetEntityId = entity.getId();
            return to(entity);
        }

        /** Render as a stationary pulsing blob instead of a flowing stream. */
        public Builder stationary() {
            this.stationary = true;
            return this;
        }

        /** Set the RGB color (e.g. {@code 0xBB0000}). Default white. */
        public Builder color(int color) {
            this.color = color;
            return this;
        }

        /** Set the base tube radius in blocks. Default {@code 0.1f}. */
        public Builder scale(float scale) {
            this.scale = scale;
            return this;
        }

        /** Set the alpha at the source/tail end. Default {@code 0.1f}. */
        public Builder alphaStart(float alpha) {
            this.alphaStart = alpha;
            return this;
        }

        /** Set the alpha at the target/head end. Default {@code 1.0f}. */
        public Builder alphaEnd(float alpha) {
            this.alphaEnd = alpha;
            return this;
        }

        /** Set both start and end alpha to the same value (uniform opacity). */
        public Builder alpha(float alpha) {
            this.alphaStart = alpha;
            this.alphaEnd = alpha;
            return this;
        }

        /** If true, use emissive shader (ignores world lighting). Default false. */
        public Builder glow(boolean glow) {
            this.glow = glow;
            return this;
        }

        /** Set the number of tube cross-section sides. Default {@code 8}. Use 12-16 for smoother. */
        public Builder tubeSegments(int segments) {
            this.tubeSegments = Math.max(3, segments);
            return this;
        }

        /** Overall speed multiplier. {@code 2.0f} = twice as fast. Default {@code 1.0f}. */
        public Builder speed(float speed) {
            this.speed = speed;
            return this;
        }

        /** Gravity factor. {@code 0} = no gravity, {@code 0.5f} = heavy droop. Default {@code 0.2f}. */
        public Builder gravity(float gravity) {
            this.gravity = gravity;
            return this;
        }

        /** Set wobble amplitude in blocks. {@code 0} = perfectly straight. Default {@code 0.03f}. */
        public Builder wobble(float amplitude) {
            this.wobbleAmplitude = amplitude;
            return this;
        }

        /** Set wobble frequency multiplier. Higher = faster oscillation. Default {@code 1.0f}. */
        public Builder wobbleFrequency(float freq) {
            this.wobbleFrequency = freq;
            return this;
        }

        /** If true, the stream spirals into the target on arrival. Default {@code true}. */
        public Builder spiralInto(boolean spiral) {
            this.spiralInto = spiral;
            return this;
        }

        /** Radius of the spiral orbit in blocks. Default {@code 0.35f}. */
        public Builder spiralRadius(float radius) {
            this.spiralRadius = radius;
            return this;
        }

        /** Spiral rotation speed in radians/tick. Default {@code 0.4f}. */
        public Builder spiralSpeed(float speed) {
            this.spiralSpeed = speed;
            return this;
        }

        /** Height above target to approach before spiraling. Default {@code 1.0f} block. */
        public Builder approachHeight(float height) {
            this.approachHeight = height;
            return this;
        }

        /** Max lifetime in ticks. {@code 0} = auto from distance. */
        public Builder lifetime(int ticks) {
            this.lifetime = ticks;
            return this;
        }

        /** Multiplier for drain-phase acceleration. Default {@code 1.0f}. */
        public Builder drainSpeed(float speed) {
            this.drainSpeed = speed;
            return this;
        }

        public Builder blockyMode(BlockyMode mode) {
            this.blockyMode = mode;
            return this;
        }

        /** Build the immutable {@link StreamEffect}. */
        public StreamEffect build() {
            return new StreamEffect(this);
        }
    }
}
