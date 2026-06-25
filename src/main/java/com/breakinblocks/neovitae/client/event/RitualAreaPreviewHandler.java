package com.breakinblocks.neovitae.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import org.joml.Matrix4f;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.ItemRitualReader;
import com.breakinblocks.neovitae.ritual.EnumRitualReaderState;

@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public final class RitualAreaPreviewHandler {

    private static final float LINE_WIDTH = 2.0f;

    private RitualAreaPreviewHandler() {}

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent.AfterTranslucentParticles event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        ItemStack stack = findReader(player);
        if (stack == null) return;

        ItemRitualReader reader = (ItemRitualReader) stack.getItem();
        EnumRitualReaderState state = reader.getState(stack);
        boolean corner1 = state == EnumRitualReaderState.SET_AREA_CORNER_1;
        boolean corner2 = state == EnumRitualReaderState.SET_AREA_CORNER_2;
        if (!corner1 && !corner2) return;

        if (!(mc.hitResult instanceof BlockHitResult hit) || mc.hitResult.getType() != HitResult.Type.BLOCK) return;
        BlockPos target = hit.getBlockPos();

        AABB box = corner2 ? enclosing(reader.getCorner1(stack), target) : new AABB(target);

        Vec3 cam = event.getLevelRenderState().cameraRenderState.pos;
        PoseStack pose = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        RenderType lineType = RenderTypes.lines();
        VertexConsumer lines = buffers.getBuffer(lineType);

        pose.pushPose();
        pose.translate(-cam.x, -cam.y, -cam.z);
        drawLineBox(pose.last().pose(), lines, box, 0.72f, 0.16f, 0.16f, 0.9f);
        pose.popPose();

        buffers.endBatch(lineType);
    }

    private static AABB enclosing(BlockPos a, BlockPos b) {
        return new AABB(
                Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()),
                Math.max(a.getX(), b.getX()) + 1, Math.max(a.getY(), b.getY()) + 1, Math.max(a.getZ(), b.getZ()) + 1);
    }

    private static void drawLineBox(Matrix4f matrix, VertexConsumer c, AABB box, float r, float g, float b, float a) {
        float x0 = (float) box.minX, y0 = (float) box.minY, z0 = (float) box.minZ;
        float x1 = (float) box.maxX, y1 = (float) box.maxY, z1 = (float) box.maxZ;
        edge(matrix, c, x0, y0, z0, x1, y0, z0, r, g, b, a);
        edge(matrix, c, x1, y0, z0, x1, y0, z1, r, g, b, a);
        edge(matrix, c, x1, y0, z1, x0, y0, z1, r, g, b, a);
        edge(matrix, c, x0, y0, z1, x0, y0, z0, r, g, b, a);
        edge(matrix, c, x0, y1, z0, x1, y1, z0, r, g, b, a);
        edge(matrix, c, x1, y1, z0, x1, y1, z1, r, g, b, a);
        edge(matrix, c, x1, y1, z1, x0, y1, z1, r, g, b, a);
        edge(matrix, c, x0, y1, z1, x0, y1, z0, r, g, b, a);
        edge(matrix, c, x0, y0, z0, x0, y1, z0, r, g, b, a);
        edge(matrix, c, x1, y0, z0, x1, y1, z0, r, g, b, a);
        edge(matrix, c, x1, y0, z1, x1, y1, z1, r, g, b, a);
        edge(matrix, c, x0, y0, z1, x0, y1, z1, r, g, b, a);
    }

    private static void edge(Matrix4f matrix, VertexConsumer c, float ax, float ay, float az,
                             float bx, float by, float bz, float r, float g, float b, float a) {
        float nx = bx - ax, ny = by - ay, nz = bz - az;
        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (len != 0) { nx /= len; ny /= len; nz /= len; }
        c.addVertex(matrix, ax, ay, az).setColor(r, g, b, a).setNormal(nx, ny, nz).setLineWidth(LINE_WIDTH);
        c.addVertex(matrix, bx, by, bz).setColor(r, g, b, a).setNormal(nx, ny, nz).setLineWidth(LINE_WIDTH);
    }

    private static ItemStack findReader(Player player) {
        ItemStack main = player.getMainHandItem();
        if (main.getItem() instanceof ItemRitualReader) return main;
        ItemStack off = player.getOffhandItem();
        if (off.getItem() instanceof ItemRitualReader) return off;
        return null;
    }
}
