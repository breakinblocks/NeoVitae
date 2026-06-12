package com.breakinblocks.neovitae.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
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
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

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

        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cam = camera.getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        VertexConsumer lines = buffers.getBuffer(RenderType.lines());

        poseStack.pushPose();
        poseStack.translate(-cam.x, -cam.y, -cam.z);

        int index = 0;
        for (AreaDescriptor descriptor : ranges.values()) {
            AABB box = descriptor.getAABB(pos);
            int color = RANGE_COLORS[index % RANGE_COLORS.length];
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            LevelRenderer.renderLineBox(poseStack, lines, box, r, g, b, 1.0f);
            index++;
        }

        poseStack.popPose();
        buffers.endBatch(RenderType.lines());
    }

    private static boolean playerHasSeerSigil(Player player) {
        Item seer = NVItems.SIGIL_SEER.get();
        for (ItemStack stack : player.getInventory().items) {
            if (containsSigil(stack, seer)) return true;
        }
        if (containsSigil(player.getOffhandItem(), seer)) return true;
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
