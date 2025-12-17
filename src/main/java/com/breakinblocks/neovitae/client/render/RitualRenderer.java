package com.breakinblocks.neovitae.client.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
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
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneTile;
import com.breakinblocks.neovitae.common.item.ItemRitualDiviner;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;

import java.util.List;

/**
 * Renders holographic previews of ritual stone placements.
 * Shows the ritual pattern when a player holds a Ritual Diviner and looks at an MRS.
 * Uses textured block rendering for accurate preview of actual rune blocks.
 */
@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public class RitualRenderer {

    // Texture resource locations for each rune type
    private static final ResourceLocation RITUAL_STONE_BLANK = NeoVitae.rl("block/ritual_stone");
    private static final ResourceLocation RITUAL_STONE_WATER = NeoVitae.rl("block/water_ritual_stone");
    private static final ResourceLocation RITUAL_STONE_FIRE = NeoVitae.rl("block/fire_ritual_stone");
    private static final ResourceLocation RITUAL_STONE_EARTH = NeoVitae.rl("block/earth_ritual_stone");
    private static final ResourceLocation RITUAL_STONE_AIR = NeoVitae.rl("block/air_ritual_stone");
    private static final ResourceLocation RITUAL_STONE_DAWN = NeoVitae.rl("block/dawn_ritual_stone");
    private static final ResourceLocation RITUAL_STONE_DUSK = NeoVitae.rl("block/dusk_ritual_stone");

    // Translucent white with alpha for ghost effect
    private static final int GHOST_COLOR = 0xDDFFFFFF;
    private static final int FULL_BRIGHT = 0x00F000F0;

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        Level level = player.level();
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            heldItem = player.getOffhandItem();
        }

        if (!(heldItem.getItem() instanceof ItemRitualDiviner diviner)) {
            return;
        }

        // Check if player is looking at an MRS
        HitResult hitResult = mc.hitResult;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos mrsPos = blockHit.getBlockPos();
        BlockEntity be = level.getBlockEntity(mrsPos);

        if (!(be instanceof MasterRitualStoneTile)) {
            return;
        }

        Ritual ritual = diviner.getCurrentRitual(heldItem);
        if (ritual == null) {
            return;
        }

        Direction direction = diviner.getDirection(heldItem);

        // Get buffer source and render
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        PoseStack poseStack = event.getPoseStack();

        renderRitualStones(poseStack, buffers, mrsPos, ritual, direction, level);

        RenderSystem.disableDepthTest();
        buffers.endBatch();
    }

    private static void renderRitualStones(PoseStack poseStack, MultiBufferSource buffers,
                                            BlockPos mrsPos, Ritual ritual, Direction direction, Level level) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 eyePos = camera.getPosition();
        VertexConsumer buffer = buffers.getBuffer(Sheets.translucentCullBlockSheet());

        List<RitualComponent> components = Lists.newArrayList();
        ritual.gatherComponents(components::add);

        for (RitualComponent component : components) {
            poseStack.pushPose();

            BlockPos rotatedOffset = rotateOffset(component.offset(), direction);
            BlockPos runePos = mrsPos.offset(rotatedOffset);

            double minX = runePos.getX() - eyePos.x;
            double minY = runePos.getY() - eyePos.y;
            double minZ = runePos.getZ() - eyePos.z;

            poseStack.translate(minX, minY, minZ);

            // Only render if the position doesn't have a solid block
            if (!level.getBlockState(runePos).isSolidRender(level, runePos)) {
                ResourceLocation textureRL = getRuneTexture(component.runeType());
                NeoVitaeRenderer.Model3D model = getBlockModel(textureRL);

                RenderResizableCuboid.INSTANCE.renderCube(
                        model, poseStack, buffer, GHOST_COLOR, FULL_BRIGHT, OverlayTexture.NO_OVERLAY
                );
            }

            poseStack.popPose();
        }
    }

    private static ResourceLocation getRuneTexture(EnumRuneType runeType) {
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

    private static NeoVitaeRenderer.Model3D getBlockModel(ResourceLocation textureRL) {
        NeoVitaeRenderer.Model3D model = new NeoVitaeRenderer.Model3D();
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(textureRL);
        model.setTexture(sprite);
        model.minX = 0;
        model.minY = 0;
        model.minZ = 0;
        model.maxX = 1;
        model.maxY = 1;
        model.maxZ = 1;
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
