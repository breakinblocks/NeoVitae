package com.breakinblocks.neovitae.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.routing.IRoutingNode;
import com.breakinblocks.neovitae.client.render.stream.BeamRenderer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public final class RoutingBeamHandler {

    private static final Set<BlockEntity> NODES = ConcurrentHashMap.newKeySet();

    private static final int BEAM_COLOR = 0xB8DDE0;

    private RoutingBeamHandler() {}

    public static void register(BlockEntity be) {
        if (be instanceof IRoutingNode) {
            NODES.add(be);
        }
    }

    public static void unregister(BlockEntity be) {
        NODES.remove(be);
    }

    @SubscribeEvent
    public static void onSubmitCustomGeometry(SubmitCustomGeometryEvent event) {
        if (NODES.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Vec3 cameraPos = event.getLevelRenderState().cameraRenderState.pos;
        long gameTime = mc.level.getGameTime();
        float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);

        SubmitNodeCollector collector = event.getSubmitNodeCollector();
        PoseStack poseStack = event.getPoseStack();

        float r = ((BEAM_COLOR >> 16) & 0xFF) / 255f;
        float g = ((BEAM_COLOR >> 8) & 0xFF) / 255f;
        float b = (BEAM_COLOR & 0xFF) / 255f;

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        Set<Edge> edges = new HashSet<>();
        NODES.removeIf(BlockEntity::isRemoved);
        for (BlockEntity be : NODES) {

            IRoutingNode node = (IRoutingNode) be;
            List<BlockPos> connections = node.getConnected();
            if (connections.isEmpty()) continue;

            BlockPos nodePos = be.getBlockPos();
            for (BlockPos targetPos : connections) {
                if (nodePos.equals(targetPos)) continue;
                // Only draw a beam we can actually vouch for. A stale connection entry pointing at a
                // destroyed node is indistinguishable from a live one until the far end is loaded, so
                // an unverifiable target is left undrawn rather than trailing off into nothing.
                if (!mc.level.isLoaded(targetPos)) continue;
                if (!(mc.level.getBlockEntity(targetPos) instanceof IRoutingNode)) continue;
                edges.add(Edge.of(nodePos, targetPos));
            }
        }

        for (Edge edge : edges) {
            BeamRenderer.submitBeam(poseStack, collector, edge.a(), edge.b(), r, g, b, gameTime, partialTick);
        }

        poseStack.popPose();
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        NODES.clear();
    }

    private record Edge(BlockPos a, BlockPos b) {
        static Edge of(BlockPos p1, BlockPos p2) {
            return p1.compareTo(p2) <= 0 ? new Edge(p1, p2) : new Edge(p2, p1);
        }
    }
}
