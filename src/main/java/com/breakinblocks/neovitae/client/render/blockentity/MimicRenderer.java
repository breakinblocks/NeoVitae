package com.breakinblocks.neovitae.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.MimicBlockEntity;

public class MimicRenderer implements BlockEntityRenderer<MimicBlockEntity, MimicRenderer.State> {

    public MimicRenderer(BlockEntityRendererProvider.Context context) {}

    public static class State extends BlockEntityRenderState {
        public @Nullable MovingBlockRenderState block;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(MimicBlockEntity mimic, State s, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(mimic, s, partialTick, cameraPos, crumbling);
        BlockState mimicState = mimic.getMimic();
        if (mimicState == null || mimicState.isAir()) {
            mimicState = Blocks.STONE.defaultBlockState();
        }
        Level level = mimic.getLevel();
        if (level instanceof ClientLevel clientLevel) {
            BlockPos pos = mimic.getBlockPos();
            Holder<Biome> biome = clientLevel.getBiome(pos);
            MovingBlockRenderState moving = new MovingBlockRenderState();
            moving.randomSeedPos = pos;
            moving.blockPos = pos;
            moving.blockState = mimicState;
            moving.biome = biome;
            moving.cardinalLighting = clientLevel.cardinalLighting();
            moving.lightEngine = clientLevel.getLightEngine();
            s.block = moving;
        } else {
            s.block = null;
        }
    }

    @Override
    public void submit(State s, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (s.block != null) {
            collector.submitMovingBlock(poseStack, s.block);
        }
    }
}
