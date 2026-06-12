package com.breakinblocks.neovitae.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import org.joml.Matrix4f;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.sigil.ItemSigilHolding;
import com.breakinblocks.neovitae.compat.curios.CuriosCompat;

import java.util.Map;

@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public class SeerRitualAreaRenderer {

    private static final int[] RANGE_COLORS = {
            0x4FC3F7, 0xFFD54F, 0xE57373, 0x81C784, 0xBA68C8, 0xFF8A65, 0x4DB6AC
    };

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent.AfterTranslucentParticles event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        HitResult hitResult = mc.hitResult;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        Level level = player.level();
        BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof MasterRitualStoneBlockEntity mrs)) {
            return;
        }
        if (!mrs.isActive() || mrs.getCurrentRitual() == null) {
            return;
        }

        Map<String, AreaDescriptor> ranges = mrs.getBlockRanges();
        if (ranges.isEmpty()) {
            return;
        }

        if (!playerHasSeerSigil(player)) {
            return;
        }

        Vec3 cam = event.getLevelRenderState().cameraRenderState.pos;
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        RenderType lineType = RenderTypes.lines();
        VertexConsumer lines = buffers.getBuffer(lineType);

        poseStack.pushPose();
        poseStack.translate(-cam.x, -cam.y, -cam.z);
        Matrix4f matrix = poseStack.last().pose();

        int index = 0;
        for (AreaDescriptor descriptor : ranges.values()) {
            AABB box = descriptor.getAABB(pos);
            int color = RANGE_COLORS[index % RANGE_COLORS.length];
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            drawLineBox(matrix, lines, box, r, g, b, 1.0f);
            index++;
        }

        poseStack.popPose();
        buffers.endBatch(lineType);
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
        c.addVertex(matrix, ax, ay, az).setColor(r, g, b, a).setNormal(nx, ny, nz);
        c.addVertex(matrix, bx, by, bz).setColor(r, g, b, a).setNormal(nx, ny, nz);
    }

    private static boolean playerHasSeerSigil(Player player) {
        Item seer = NVItems.SIGIL_SEER.get();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (containsSigil(player.getInventory().getItem(i), seer)) return true;
        }
        for (ItemStack stack : CuriosCompat.getCuriosInventory(player)) {
            if (containsSigil(stack, seer)) return true;
        }
        return false;
    }

    private static boolean containsSigil(ItemStack stack, Item sigil) {
        if (stack.isEmpty()) return false;
        if (stack.is(sigil)) return true;
        if (stack.getItem() instanceof ItemSigilHolding) {
            NonNullList<ItemStack> inner = ItemSigilHolding.getInternalInventory(stack);
            if (inner != null) {
                for (ItemStack held : inner) {
                    if (!held.isEmpty() && held.is(sigil)) return true;
                }
            }
        }
        return false;
    }
}
