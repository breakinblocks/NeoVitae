package com.breakinblocks.neovitae.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
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
import org.joml.Matrix4f;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.render.NVRenderTypes;
import com.breakinblocks.neovitae.client.render.RitualBoxRenderer;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonAlternatorBlockEntity;
import com.breakinblocks.neovitae.common.item.routing.ItemNodeRouter;

import java.util.LinkedHashSet;
import java.util.Set;

@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public final class AlternatorLinkOverlayHandler {

    private static final float LINE_WIDTH = 2.0F;
    private static final float RED = 1.0F;
    private static final float GREEN = 0.62F;
    private static final float BLUE = 0.1F;
    private static final float ALPHA = 0.9F;

    private AlternatorLinkOverlayHandler() {}

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent.AfterTranslucentParticles event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        ItemStack router = findRouter(player);
        if (router == null) return;

        Set<DungeonAlternatorBlockEntity> targets = new LinkedHashSet<>();
        if (mc.hitResult instanceof BlockHitResult hit && mc.hitResult.getType() == HitResult.Type.BLOCK
                && mc.level.getBlockEntity(hit.getBlockPos()) instanceof DungeonAlternatorBlockEntity aimed) {
            targets.add(aimed);
        }
        ItemNodeRouter item = (ItemNodeRouter) router.getItem();
        if (item.isAlternatorMode(router)) {
            BlockPos stored = item.getBlockPos(router);
            if (!stored.equals(BlockPos.ZERO) && mc.level.isLoaded(stored)
                    && mc.level.getBlockEntity(stored) instanceof DungeonAlternatorBlockEntity selected) {
                targets.add(selected);
            }
        }
        if (targets.isEmpty()) return;

        Vec3 cameraPos = event.getLevelRenderState().cameraRenderState.pos;
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer lines = bufferSource.getBuffer(NVRenderTypes.LINES_SEE_THROUGH);

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        Matrix4f matrix = poseStack.last().pose();

        for (DungeonAlternatorBlockEntity alternator : targets) {
            BlockPos sourcePos = alternator.getBlockPos();
            RitualBoxRenderer.drawLineBox(matrix, lines, new AABB(sourcePos).inflate(0.004), RED, GREEN, BLUE, ALPHA);
            Vec3 sourceCenter = Vec3.atCenterOf(sourcePos);
            for (BlockPos receiver : alternator.getReceivers()) {
                RitualBoxRenderer.drawLineBox(matrix, lines, new AABB(receiver).inflate(0.004), RED, GREEN, BLUE, ALPHA);
                drawLine(matrix, lines, sourceCenter, Vec3.atCenterOf(receiver));
            }
        }

        poseStack.popPose();
        bufferSource.endBatch(NVRenderTypes.LINES_SEE_THROUGH);
    }

    private static void drawLine(Matrix4f matrix, VertexConsumer consumer, Vec3 from, Vec3 to) {
        Vec3 direction = to.subtract(from).normalize();
        float nx = (float) direction.x;
        float ny = (float) direction.y;
        float nz = (float) direction.z;
        consumer.addVertex(matrix, (float) from.x, (float) from.y, (float) from.z)
                .setColor(RED, GREEN, BLUE, ALPHA)
                .setNormal(nx, ny, nz)
                .setLineWidth(LINE_WIDTH);
        consumer.addVertex(matrix, (float) to.x, (float) to.y, (float) to.z)
                .setColor(RED, GREEN, BLUE, ALPHA)
                .setNormal(nx, ny, nz)
                .setLineWidth(LINE_WIDTH);
    }

    private static ItemStack findRouter(Player player) {
        ItemStack main = player.getMainHandItem();
        if (main.getItem() instanceof ItemNodeRouter) return main;
        ItemStack off = player.getOffhandItem();
        if (off.getItem() instanceof ItemNodeRouter) return off;
        return null;
    }
}
