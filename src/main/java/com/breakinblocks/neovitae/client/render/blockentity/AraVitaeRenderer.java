package com.breakinblocks.neovitae.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class AraVitaeRenderer implements BlockEntityRenderer<AraVitaeTile, AraVitaeRenderer.State> {

    private static final Identifier RITUAL_TEXTURE = NeoVitae.rl("textures/particle/ritual.png");
    private static final Identifier FLUID_FILL_TEXTURE = NeoVitae.rl("textures/models/alchemyarrays/basearray.png");

    private static final int[][] HELLFORGED_CAPS = {{8, -4, 8}, {8, -4, -8}, {-8, -4, 8}, {-8, -4, -8}};

    private static final Identifier[] CAPSTONE_ARRAY_TEXTURES = {
            NeoVitae.rl("textures/models/alchemyarrays/basearray.png"),
            NeoVitae.rl("textures/models/alchemyarrays/bindingarray.png"),
            NeoVitae.rl("textures/models/alchemyarrays/bouncearray.png"),
            NeoVitae.rl("textures/models/alchemyarrays/collectionarray.png"),
            NeoVitae.rl("textures/models/alchemyarrays/deflectionarray.png"),
            NeoVitae.rl("textures/models/alchemyarrays/fountainarray.png"),
            NeoVitae.rl("textures/models/alchemyarrays/freezearray.png"),
            NeoVitae.rl("textures/models/alchemyarrays/furnacearray.png"),
            NeoVitae.rl("textures/models/alchemyarrays/growtharray.png"),
            NeoVitae.rl("textures/models/alchemyarrays/lightarray.png"),
            NeoVitae.rl("textures/models/alchemyarrays/moonarray.png"),
            NeoVitae.rl("textures/models/alchemyarrays/movementarray.png"),
            NeoVitae.rl("textures/models/alchemyarrays/rainarray.png"),
            NeoVitae.rl("textures/models/alchemyarrays/repulsionarray.png"),
            NeoVitae.rl("textures/models/alchemyarrays/spikearray.png"),
            NeoVitae.rl("textures/models/alchemyarrays/spiritsiphonarray.png"),
            NeoVitae.rl("textures/models/alchemyarrays/sunarray.png"),
            NeoVitae.rl("textures/models/alchemyarrays/updraftarray.png")
    };

    private static final int CYCLE_TICKS = 140;
    private static final int RISE_START = 0;
    private static final int RISE_END = 80;
    private static final int CASCADE_START = 50;
    private static final int CASCADE_END = 120;
    private static final float CASCADE_HEIGHT = 3.0f;
    private static final int CAP_PHASE_OFFSET = CYCLE_TICKS / 4;
    private static final int BLOOD_GLOW_COLOR = 0x000000;

    private final ItemModelResolver itemModelResolver;

    public AraVitaeRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    public static class State extends BlockEntityRenderState {
        public ItemStackRenderState inputItem = new ItemStackRenderState();
        public boolean hasInput;
        public boolean active;
        public int tier;
        public float fluidLevel;
        public float animationTicks;
        public long gameTime;
        public float itemRotation;
        public float fluidU0 = 0f;
        public float fluidU1 = 1f;
        public float fluidV0 = 0f;
        public float fluidV1 = 1f;
        public final Identifier[] capstoneTextures = new Identifier[HELLFORGED_CAPS.length];
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public void extractRenderState(AraVitaeTile altar, State s, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(altar, s, partialTick, cameraPos, crumbling);

        Level level = altar.getLevel();
        ItemStack inputStack = altar.inv.getStackInSlot(0);
        s.hasInput = !inputStack.isEmpty();
        if (s.hasInput) {
            this.itemModelResolver.updateForTopItem(s.inputItem, inputStack, ItemDisplayContext.FIXED, level, null, 0);
        } else {
            s.inputItem.clear();
        }
        s.itemRotation = (float) (720.0F * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);

        s.active = altar.isVisuallyActive();
        s.tier = altar.getTier();
        s.fluidLevel = (float) altar.getMainTank() / (float) altar.getMainCapacity();

        s.gameTime = level != null ? level.getGameTime() : 0L;
        s.animationTicks = s.gameTime + partialTick;

        FluidModel fluidModel = Minecraft.getInstance()
                .getModelManager()
                .getFluidStateModelSet()
                .get(com.breakinblocks.neovitae.common.fluid.NVFluids.ESSENTIA_VITAE_SOURCE.get().defaultFluidState());
        if (fluidModel != null) {
            TextureAtlasSprite sprite = fluidModel.stillMaterial().sprite();
            s.fluidU0 = sprite.getU0();
            s.fluidU1 = sprite.getU1();
            s.fluidV0 = sprite.getV0();
            s.fluidV1 = sprite.getV1();
        }

        if (s.active && s.tier >= 4) {
            for (int i = 0; i < HELLFORGED_CAPS.length; i++) {
                s.capstoneTextures[i] = CAPSTONE_ARRAY_TEXTURES[pickTextureIndex(altar, i, s.gameTime)];
            }
            if (level != null) {
                for (int i = 0; i < HELLFORGED_CAPS.length; i++) {
                    spawnCascadeParticles(altar, level, HELLFORGED_CAPS[i], i, s.gameTime);
                }
            }
        }
    }

    @Override
    public void submit(State s, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        submitInputItem(s, poseStack, collector);

        if (s.active) {
            if (s.tier >= 1) {
                submitRitualCircle(s, poseStack, collector);
            }
            if (s.tier >= 4) {
                submitHellforgedCapstoneArrays(s, poseStack, collector);
            }
        }

        if (s.fluidLevel > 0f) {
            submitFluid(s, poseStack, collector);
        }
    }

    private void submitInputItem(State s, PoseStack poseStack, SubmitNodeCollector collector) {
        if (!s.hasInput) return;
        poseStack.pushPose();
        poseStack.translate(0.5, 1, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(s.itemRotation));
        poseStack.scale(0.5F, 0.5F, 0.5F);
        s.inputItem.submit(poseStack, collector, s.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    private void submitRitualCircle(State s, PoseStack poseStack, SubmitNodeCollector collector) {
        float rotation = (s.animationTicks * 0.5f) % 360f;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.01, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotation));

        final float half = 1.682f;
        final int packedOverlay = OverlayTexture.NO_OVERLAY;
        final int fullbright = 0xF000F0;

        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucentEmissive(RITUAL_TEXTURE), (pose, buf) -> {
            Matrix4f matrix = pose.pose();
            Vector3f normUp = new Vector3f();
            pose.transformNormal(0, 1, 0, normUp);
            Vector3f normDown = new Vector3f();
            pose.transformNormal(0, -1, 0, normDown);

            buf.addVertex(matrix, -half, 0, -half).setColor(255, 255, 255, 64).setUv(0, 0).setOverlay(packedOverlay).setLight(fullbright).setNormal(normUp.x, normUp.y, normUp.z);
            buf.addVertex(matrix, -half, 0, half).setColor(255, 255, 255, 64).setUv(0, 1).setOverlay(packedOverlay).setLight(fullbright).setNormal(normUp.x, normUp.y, normUp.z);
            buf.addVertex(matrix, half, 0, half).setColor(255, 255, 255, 64).setUv(1, 1).setOverlay(packedOverlay).setLight(fullbright).setNormal(normUp.x, normUp.y, normUp.z);
            buf.addVertex(matrix, half, 0, -half).setColor(255, 255, 255, 64).setUv(1, 0).setOverlay(packedOverlay).setLight(fullbright).setNormal(normUp.x, normUp.y, normUp.z);

            buf.addVertex(matrix, half, 0, -half).setColor(255, 255, 255, 64).setUv(1, 0).setOverlay(packedOverlay).setLight(fullbright).setNormal(normDown.x, normDown.y, normDown.z);
            buf.addVertex(matrix, half, 0, half).setColor(255, 255, 255, 64).setUv(1, 1).setOverlay(packedOverlay).setLight(fullbright).setNormal(normDown.x, normDown.y, normDown.z);
            buf.addVertex(matrix, -half, 0, half).setColor(255, 255, 255, 64).setUv(0, 1).setOverlay(packedOverlay).setLight(fullbright).setNormal(normDown.x, normDown.y, normDown.z);
            buf.addVertex(matrix, -half, 0, -half).setColor(255, 255, 255, 64).setUv(0, 0).setOverlay(packedOverlay).setLight(fullbright).setNormal(normDown.x, normDown.y, normDown.z);
        });

        poseStack.popPose();
    }

    private void submitHellforgedCapstoneArrays(State s, PoseStack poseStack, SubmitNodeCollector collector) {
        for (int i = 0; i < HELLFORGED_CAPS.length; i++) {
            submitCapstoneArray(s, HELLFORGED_CAPS[i], i, poseStack, collector);
        }
    }

    private void submitCapstoneArray(State s, int[] cap, int capIndex, PoseStack poseStack, SubmitNodeCollector collector) {
        float cycleTime = ((s.gameTime + capIndex * CAP_PHASE_OFFSET) % CYCLE_TICKS) + (s.animationTicks - s.gameTime);
        if (cycleTime < RISE_START || cycleTime >= RISE_END) return;

        float riseProgress = (cycleTime - RISE_START) / (float) (RISE_END - RISE_START);
        float eased = easeInOut(Mth.clamp(riseProgress, 0f, 1f));
        float height = eased * CASCADE_HEIGHT;

        float alpha;
        if (riseProgress < 0.15f) {
            alpha = riseProgress / 0.15f;
        } else if (riseProgress > 0.80f) {
            alpha = (1f - riseProgress) / 0.20f;
        } else {
            alpha = 1f;
        }
        int a = Mth.clamp((int) (alpha * 255f), 0, 255);
        if (a <= 0) return;

        float pulse = 1.0f + 0.08f * Mth.sin(cycleTime * 0.25f);
        float rotation = (cycleTime * 3.0f) % 360f;

        Identifier texture = s.capstoneTextures[capIndex];
        if (texture == null) return;

        poseStack.pushPose();
        poseStack.translate(cap[0] + 0.5, cap[1] + 1.0 + 0.01 + height, cap[2] + 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.scale(pulse, 1f, pulse);
        poseStack.translate(-0.5, 0, -0.5);

        AlchemyArrayRenderer.submitArrayQuad(texture, poseStack, collector, 255, 255, 255, a);

        poseStack.popPose();
    }

    private void spawnCascadeParticles(AraVitaeTile altar, Level level, int[] cap, int capIndex, long gameTime) {
        long phased = (gameTime + capIndex * CAP_PHASE_OFFSET) % CYCLE_TICKS;
        if (phased < CASCADE_START || phased >= CASCADE_END) return;

        float cascadeProgress = (phased - CASCADE_START) / (float) (CASCADE_END - CASCADE_START);
        float sourceHeight;
        if (phased < RISE_END) {
            float riseP = (phased - RISE_START) / (float) (RISE_END - RISE_START);
            sourceHeight = easeInOut(Mth.clamp(riseP, 0f, 1f)) * CASCADE_HEIGHT;
        } else {
            sourceHeight = CASCADE_HEIGHT * (1f - 0.3f * cascadeProgress);
        }

        var altarPos = altar.getBlockPos();
        double baseX = altarPos.getX() + cap[0] + 0.5;
        double baseY = altarPos.getY() + cap[1] + 1.0 + sourceHeight;
        double baseZ = altarPos.getZ() + cap[2] + 0.5;

        var rng = level.getRandom();
        for (int i = 0; i < 2; i++) {
            double jitterX = (rng.nextDouble() - 0.5) * 0.6;
            double jitterZ = (rng.nextDouble() - 0.5) * 0.6;
            level.addParticle(
                    new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), BLOOD_GLOW_COLOR),
                    baseX + jitterX, baseY, baseZ + jitterZ,
                    0.0, -0.08 - rng.nextDouble() * 0.04, 0.0
            );
        }
    }

    private int pickTextureIndex(AraVitaeTile altar, int capIndex, long gameTime) {
        long cycleNumber = (gameTime + capIndex * CAP_PHASE_OFFSET) / CYCLE_TICKS;
        var pos = altar.getBlockPos();
        int hash = Long.hashCode(cycleNumber * 1103515245L
                + capIndex * 12345L
                + pos.getX() * 73856093L
                + pos.getZ() * 19349663L);
        return Math.floorMod(hash, CAPSTONE_ARRAY_TEXTURES.length);
    }

    private static float easeInOut(float t) {
        return t * t * (3f - 2f * t);
    }

    private void submitFluid(State s, PoseStack poseStack, SubmitNodeCollector collector) {
        float minHeight = 8F / 16F;
        float maxHeight = 12F / 16F;
        float start = 3F / 16F;
        float end = 13F / 16F;
        float height = minHeight + s.fluidLevel * (maxHeight - minHeight);
        int light = s.lightCoords;
        int overlay = OverlayTexture.NO_OVERLAY;
        float u0 = s.fluidU0, u1 = s.fluidU1, v0 = s.fluidV0, v1 = s.fluidV1;

        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS), (pose, buf) -> {
            Matrix4f matrix = pose.pose();
            Vector3f norm = new Vector3f();
            pose.transformNormal(0, 1, 0, norm);

            buf.addVertex(matrix, end, height, end).setColor(0xFF, 0xFF, 0xFF, 0xFF).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(norm.x, norm.y, norm.z);
            buf.addVertex(matrix, end, height, start).setColor(0xFF, 0xFF, 0xFF, 0xFF).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(norm.x, norm.y, norm.z);
            buf.addVertex(matrix, start, height, start).setColor(0xFF, 0xFF, 0xFF, 0xFF).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(norm.x, norm.y, norm.z);
            buf.addVertex(matrix, start, height, end).setColor(0xFF, 0xFF, 0xFF, 0xFF).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(norm.x, norm.y, norm.z);
        });
    }
}
