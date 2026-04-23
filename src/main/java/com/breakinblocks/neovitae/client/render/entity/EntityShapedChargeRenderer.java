package com.breakinblocks.neovitae.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.entity.projectile.EntityShapedCharge;

public class EntityShapedChargeRenderer extends EntityRenderer<EntityShapedCharge, EntityShapedChargeRenderer.State> {

    public EntityShapedChargeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
    }

    public static class State extends EntityRenderState {
        public @Nullable MovingBlockRenderState block;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(EntityShapedCharge entity, State state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        BlockState blockState = entity.getBlockState();
        Level level = entity.level();
        if (blockState.getRenderShape() == RenderShape.MODEL
                && blockState != level.getBlockState(entity.blockPosition())
                && blockState.getRenderShape() != RenderShape.INVISIBLE
                && level instanceof ClientLevel clientLevel) {
            BlockPos pos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
            MovingBlockRenderState moving = new MovingBlockRenderState();
            moving.randomSeedPos = pos;
            moving.blockPos = pos;
            moving.blockState = blockState;
            moving.biome = clientLevel.getBiome(pos);
            moving.cardinalLighting = clientLevel.cardinalLighting();
            moving.lightEngine = clientLevel.getLightEngine();
            state.block = moving;
        } else {
            state.block = null;
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.block != null) {
            poseStack.pushPose();
            poseStack.translate(-0.5, 0.0, -0.5);
            collector.submitMovingBlock(poseStack, state.block);
            poseStack.popPose();
        }
        super.submit(state, poseStack, collector, camera);
    }
}
