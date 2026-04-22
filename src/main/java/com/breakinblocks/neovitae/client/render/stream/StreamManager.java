package com.breakinblocks.neovitae.client.render.stream;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.stream.StreamEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side singleton managing all active stream and blob effects.
 * Thread-safe for network packet handling.
 */
public class StreamManager {

    private static final StreamManager INSTANCE = new StreamManager();

    private final ConcurrentHashMap<String, ActiveStream> activeStreams = new ConcurrentHashMap<>();
    private final List<SimpleStreamEffect> simpleStreams = new ArrayList<>();
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
        if (NeoVitae.CLIENT_CONFIG.USE_SIMPLE_EFFECTS.get()) {
            simpleStreams.add(new SimpleStreamEffect(effect));
            return;
        }

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
        simpleStreams.removeIf(simple -> {
            simple.tick();
            return simple.isExpired();
        });
    }

    /** Submit all active streams into the 26.1 render pipeline. */
    public void submitAll(PoseStack poseStack, SubmitNodeCollector collector,
                          Vec3 cameraPos, float partialTick) {
        if (activeStreams.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        for (ActiveStream stream : activeStreams.values()) {
            RenderType type = stream.isGlow() ? StreamRenderer.STREAM_GLOW_TYPE : StreamRenderer.STREAM_RENDER_TYPE;
            poseStack.pushPose();
            poseStack.translate(stream.getStartX(), stream.getStartY(), stream.getStartZ());
            collector.submitCustomGeometry(poseStack, type, (pose, buf) ->
                    StreamRenderer.render(stream, pose.pose(), buf, partialTick));
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    /** Clear all streams. Called on disconnect. */
    public void clear() {
        activeStreams.clear();
        simpleStreams.clear();
        tickCount = 0;
    }

    public boolean hasActiveStreams() {
        return !activeStreams.isEmpty() || !simpleStreams.isEmpty();
    }
}
