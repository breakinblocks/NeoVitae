package com.breakinblocks.neovitae.client.render.stream;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side singleton managing all active energy streams.
 * Thread-safe for network packet handling from the netty thread.
 */
public class StreamManager {

    private static final StreamManager INSTANCE = new StreamManager();

    private final ConcurrentHashMap<String, ActiveStream> activeStreams = new ConcurrentHashMap<>();
    private int tickCount = 0;

    private StreamManager() {
    }

    public static StreamManager getInstance() {
        return INSTANCE;
    }

    /**
     * Add a stream. Called from network packet handler.
     * If an identical stream (same source region + target + color) is still active, skip it.
     *
     * @param srcX   source X (precise, e.g. entity position)
     * @param srcY   source Y (precise, e.g. upper chest height)
     * @param srcZ   source Z (precise, e.g. entity position)
     * @param target target block position (altar)
     * @param color  packed RGB color
     */
    public void addStream(double srcX, double srcY, double srcZ, BlockPos target, int color) {
        // Key uses floored source coords so slight player movement doesn't bypass dedup
        String key = net.minecraft.util.Mth.floor(srcX) + ":" + net.minecraft.util.Mth.floor(srcY) + ":"
                + net.minecraft.util.Mth.floor(srcZ) + ":" + target.asLong() + ":" + color;

        ActiveStream existing = activeStreams.get(key);
        if (existing != null && !existing.isExpired()) {
            return; // let the current stream finish naturally
        }

        activeStreams.put(key, new ActiveStream(
                key,
                srcX, srcY, srcZ,
                target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5,
                color, tickCount
        ));
    }

    /**
     * Tick all active streams, removing expired ones.
     */
    public void tick() {
        tickCount++;
        activeStreams.values().removeIf(stream -> {
            stream.tick();
            return stream.isExpired();
        });
    }

    /**
     * Render all active streams.
     *
     * @param poseStack    the current pose stack
     * @param bufferSource the buffer source for rendering
     * @param cameraPos    camera position for world-to-camera-relative translation
     * @param partialTick  partial tick for interpolation
     */
    public void renderAll(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource,
                          Vec3 cameraPos, float partialTick) {
        if (activeStreams.isEmpty()) return;

        VertexConsumer buffer = bufferSource.getBuffer(StreamRenderer.STREAM_RENDER_TYPE);

        poseStack.pushPose();
        // Translate from camera-relative origin to world origin
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        for (ActiveStream stream : activeStreams.values()) {
            StreamRenderer.render(stream, poseStack, buffer, partialTick);
        }

        poseStack.popPose();

        // Flush the stream render type
        bufferSource.endBatch(StreamRenderer.STREAM_RENDER_TYPE);
    }

    /**
     * Clear all streams. Called on disconnect.
     */
    public void clear() {
        activeStreams.clear();
        tickCount = 0;
    }

    public boolean hasActiveStreams() {
        return !activeStreams.isEmpty();
    }
}
