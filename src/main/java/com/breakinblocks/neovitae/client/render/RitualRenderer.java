package com.breakinblocks.neovitae.client.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.common.item.ItemRitualDiviner;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualLayouts;

import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public class RitualRenderer {

    private static final Identifier RITUAL_STONE_BLANK = NeoVitae.rl("block/ritual_stone");
    private static final Identifier RITUAL_STONE_WATER = NeoVitae.rl("block/water_ritual_stone");
    private static final Identifier RITUAL_STONE_FIRE = NeoVitae.rl("block/fire_ritual_stone");
    private static final Identifier RITUAL_STONE_EARTH = NeoVitae.rl("block/earth_ritual_stone");
    private static final Identifier RITUAL_STONE_AIR = NeoVitae.rl("block/air_ritual_stone");
    private static final Identifier RITUAL_STONE_DAWN = NeoVitae.rl("block/dawn_ritual_stone");
    private static final Identifier RITUAL_STONE_DUSK = NeoVitae.rl("block/dusk_ritual_stone");

    private static final int GHOST_COLOR = 0xDDFFFFFF;
    private static final int FULL_BRIGHT = 0x00F000F0;
    private static final float GHOST_INSET = 0.05F;

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent.AfterTranslucentParticles event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        Level level = player.level();
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            heldItem = player.getOffhandItem();
        }
        if (!(heldItem.getItem() instanceof ItemRitualDiviner diviner)) return;

        HitResult hitResult = mc.hitResult;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) return;

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos mrsPos = blockHit.getBlockPos();
        BlockEntity be = level.getBlockEntity(mrsPos);
        if (!(be instanceof MasterRitualStoneBlockEntity)) return;

        Ritual ritual = diviner.getCurrentRitual(heldItem);
        if (ritual == null) return;

        Direction direction = diviner.getDirection(heldItem);

        Vec3 eyePos = event.getLevelRenderState().cameraRenderState.pos;
        PoseStack poseStack = event.getPoseStack();

        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        VertexConsumer buffer = buffers.getBuffer(Sheets.translucentBlockSheet());

        List<RitualComponent> components = RitualLayouts.get(level, ritual);

        for (RitualComponent component : components) {
            BlockPos rotatedOffset = rotateOffset(component.offset(), direction);
            BlockPos runePos = mrsPos.offset(rotatedOffset);

            if (!level.getBlockState(runePos).isAir()) continue;

            double minX = runePos.getX() - eyePos.x;
            double minY = runePos.getY() - eyePos.y;
            double minZ = runePos.getZ() - eyePos.z;

            Identifier textureRL = getRuneTexture(component.runeType());
            NeoVitaeRenderer.Model3D model = getBlockModel(textureRL);

            poseStack.pushPose();
            poseStack.translate(minX, minY, minZ);
            RenderResizableCuboid.INSTANCE.renderCube(
                    model, poseStack, buffer, GHOST_COLOR, FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }

        buffers.endBatch(Sheets.translucentBlockSheet());
    }

    private static Identifier getRuneTexture(EnumRuneType runeType) {
        return switch (runeType) {
            case BLANK -> RITUAL_STONE_BLANK;
            case WATER -> RITUAL_STONE_WATER;
            case FIRE -> RITUAL_STONE_FIRE;
            case EARTH -> RITUAL_STONE_EARTH;
            case AIR -> RITUAL_STONE_AIR;
            case DAWN -> RITUAL_STONE_DAWN;
            case DUSK -> RITUAL_STONE_DUSK;
        };
    }

    private static NeoVitaeRenderer.Model3D getBlockModel(Identifier textureRL) {
        NeoVitaeRenderer.Model3D model = new NeoVitaeRenderer.Model3D();
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getAtlasManager()
                .getAtlasOrThrow(AtlasIds.BLOCKS)
                .getSprite(textureRL);
        model.setTexture(sprite);
        model.minX = GHOST_INSET;
        model.minY = GHOST_INSET;
        model.minZ = GHOST_INSET;
        model.maxX = 1.0F - GHOST_INSET;
        model.maxY = 1.0F - GHOST_INSET;
        model.maxZ = 1.0F - GHOST_INSET;
        return model;
    }

    private static BlockPos rotateOffset(BlockPos offset, Direction direction) {
        return switch (direction) {
            case NORTH -> offset;
            case EAST -> new BlockPos(-offset.getZ(), offset.getY(), offset.getX());
            case SOUTH -> new BlockPos(-offset.getX(), offset.getY(), -offset.getZ());
            case WEST -> new BlockPos(offset.getZ(), offset.getY(), -offset.getX());
            default -> offset;
        };
    }
}
