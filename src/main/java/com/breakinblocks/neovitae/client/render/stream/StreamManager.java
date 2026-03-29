package com.breakinblocks.neovitae.client.render.stream;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.api.stream.StreamEffect;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side singleton managing all active stream and blob effects.
 * Thread-safe for network packet handling.
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
     * Add a stream from a {@link StreamEffect} configuration.
     * If an identical stream is still active, the request is ignored.
     */
    public void addStream(StreamEffect effect) {
        String key = Mth.floor(effect.sourceX) + ":" + Mth.floor(effect.sourceY) + ":"
                + Mth.floor(effect.sourceZ) + ":" + Mth.floor(effect.targetX) + ":"
                + Mth.floor(effect.targetY) + ":" + Mth.floor(effect.targetZ) + ":"
                + effect.color + ":" + (effect.stationary ? "s" : "m")
                + (effect.targetEntityId >= 0 ? ":e" + effect.targetEntityId : "");

        ActiveStream existing = activeStreams.get(key);
        if (existing != null && !existing.isExpired()) {
            return;
        }

        activeStreams.put(key, new ActiveStream(key, effect, tickCount));
    }

    /** Tick all active streams, removing expired ones. */
    public void tick() {
        tickCount++;
        activeStreams.values().removeIf(stream -> {
            stream.tick();
            return stream.isExpired();
        });
    }

    /** Render all active streams. */
    public void renderAll(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource,
                          Vec3 cameraPos, float partialTick) {
        if (activeStreams.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        for (ActiveStream stream : activeStreams.values()) {
            RenderType type = stream.isGlow() ? StreamRenderer.STREAM_GLOW_TYPE : StreamRenderer.STREAM_RENDER_TYPE;
            VertexConsumer buffer = bufferSource.getBuffer(type);
            StreamRenderer.render(stream, poseStack, buffer, partialTick);
        }

        poseStack.popPose();

        bufferSource.endBatch(StreamRenderer.STREAM_RENDER_TYPE);
        bufferSource.endBatch(StreamRenderer.STREAM_GLOW_TYPE);
    }

    /** Clear all streams. Called on disconnect. */
    public void clear() {
        activeStreams.clear();
        tickCount = 0;
    }

    public boolean hasActiveStreams() {
        return !activeStreams.isEmpty();
    }
}
