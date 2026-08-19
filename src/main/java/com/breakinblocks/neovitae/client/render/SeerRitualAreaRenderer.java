package com.breakinblocks.neovitae.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
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
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.common.item.ItemRitualReader;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.sigil.ItemSigilHolding;
import com.breakinblocks.neovitae.compat.curios.CuriosCompat;
import com.breakinblocks.neovitae.ritual.Ritual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public class SeerRitualAreaRenderer {

    private static final int[] RANGE_COLORS = {
            0x4FC3F7, 0xFFD54F, 0xE57373, 0x81C784, 0xBA68C8, 0xFF8A65, 0x4DB6AC
    };

    private static final int NEARBY_RADIUS = 32;
    private static final long RESOLVE_INTERVAL_TICKS = 40L;

    private static final Map<BlockPos, Resolved> RESOLVED = new HashMap<>();

    private static final int NEARBY_CHUNK_RADIUS = (NEARBY_RADIUS >> 4) + 1;

    private record Resolved(long expiresAt, List<AABB> boxes) {}

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent.AfterTranslucentParticles event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null) return;

        if (!playerHasSeerSigil(player)) return;

        List<MasterRitualStoneBlockEntity> targets = holdingConfigurator(player)
                ? nearbyMasterStones(level, player)
                : targetedMasterStone(mc, level);
        if (targets.isEmpty()) return;

        Vec3 cam = event.getLevelRenderState().cameraRenderState.pos;
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        RenderType lineType = RenderTypes.lines();
        VertexConsumer lines = buffers.getBuffer(lineType);

        poseStack.pushPose();
        poseStack.translate(-cam.x, -cam.y, -cam.z);
        Matrix4f matrix = poseStack.last().pose();

        long now = level.getGameTime();
        for (MasterRitualStoneBlockEntity mrs : targets) {
            List<AABB> boxes = boxesFor(mrs, now);
            for (int i = 0; i < boxes.size(); i++) {
                RitualBoxRenderer.drawLineBox(matrix, lines, boxes.get(i), RANGE_COLORS[i % RANGE_COLORS.length], 1.0f);
            }
        }

        poseStack.popPose();
        buffers.endBatch(lineType);
    }

    private static List<AABB> boxesFor(MasterRitualStoneBlockEntity mrs, long now) {
        BlockPos pos = mrs.getBlockPos();

        Map<String, AreaDescriptor> configured = mrs.getBlockRanges();
        if (!configured.isEmpty()) {
            RESOLVED.remove(pos);
            List<AABB> boxes = new ArrayList<>();
            for (AreaDescriptor descriptor : configured.values()) {
                boxes.add(descriptor.getAABB(pos));
            }
            return boxes;
        }

        Resolved cached = RESOLVED.get(pos);
        if (cached != null && now < cached.expiresAt()) {
            return cached.boxes();
        }

        List<AABB> boxes = new ArrayList<>();
        Ritual ritual = mrs.findStructureRitual();
        if (ritual != null) {
            for (AreaDescriptor descriptor : ritual.getModifiableRanges().values()) {
                boxes.add(descriptor.getAABB(pos));
            }
        }
        if (RESOLVED.size() > 256) RESOLVED.clear();
        RESOLVED.put(pos.immutable(), new Resolved(now + RESOLVE_INTERVAL_TICKS, boxes));
        return boxes;
    }

    private static List<MasterRitualStoneBlockEntity> targetedMasterStone(Minecraft mc, ClientLevel level) {
        HitResult hitResult = mc.hitResult;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            return List.of();
        }
        BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
        if (!(level.getBlockEntity(pos) instanceof MasterRitualStoneBlockEntity mrs)) {
            return List.of();
        }
        if (!mrs.isActive() || mrs.getCurrentRitual() == null || mrs.getBlockRanges().isEmpty()) {
            return List.of();
        }
        return List.of(mrs);
    }

    private static List<MasterRitualStoneBlockEntity> nearbyMasterStones(ClientLevel level, Player player) {
        List<MasterRitualStoneBlockEntity> found = new ArrayList<>();
        int centerX = player.blockPosition().getX() >> 4;
        int centerZ = player.blockPosition().getZ() >> 4;
        double radiusSqr = (double) NEARBY_RADIUS * NEARBY_RADIUS;

        for (int cx = centerX - NEARBY_CHUNK_RADIUS; cx <= centerX + NEARBY_CHUNK_RADIUS; cx++) {
            for (int cz = centerZ - NEARBY_CHUNK_RADIUS; cz <= centerZ + NEARBY_CHUNK_RADIUS; cz++) {
                LevelChunk chunk = level.getChunkSource().getChunk(cx, cz, false);
                if (chunk == null) continue;
                for (Map.Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
                    if (!(entry.getValue() instanceof MasterRitualStoneBlockEntity mrs)) continue;
                    if (entry.getKey().distToCenterSqr(player.position()) > radiusSqr) continue;
                    found.add(mrs);
                }
            }
        }
        return found;
    }

    private static boolean holdingConfigurator(Player player) {
        return player.getMainHandItem().getItem() instanceof ItemRitualReader
                || player.getOffhandItem().getItem() instanceof ItemRitualReader;
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
            for (ItemStack inner : ItemSigilHolding.getInternalInventory(stack)) {
                if (inner.is(sigil)) return true;
            }
        }
        return false;
    }
}
