package com.breakinblocks.neovitae.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.block.AraVitaeBlock;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.common.registry.AltarEffect;
import com.breakinblocks.neovitae.common.registry.AltarEffectType;
import com.breakinblocks.neovitae.common.registry.AltarTier;
import com.breakinblocks.neovitae.common.structure.NVMultiblock;
import com.breakinblocks.neovitae.util.helper.RenderHelper;

public class AraVitaeRenderer extends GeoBlockRenderer<AraVitaeTile> {

    private static final ResourceLocation RITUAL_TEXTURE = NeoVitae.rl("textures/particle/ritual.png");

    public AraVitaeRenderer(BlockEntityRendererProvider.Context context) {
        super(new AraVitaeModel());
        addRenderLayer(new AraVitaeRodGlowLayer(this));
    }

    @Override
    public boolean shouldRenderOffScreen(AraVitaeTile blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public void render(AraVitaeTile tileAltar, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // Rotate the geo model by the block's FACING; pop before custom passes so item/ritual/fluid stay in block-local space.
        Direction facing = tileAltar.getBlockState()
                .getOptionalValue(AraVitaeBlock.FACING)
                .orElse(Direction.NORTH);
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));
        poseStack.translate(-0.5, 0, -0.5);
        super.render(tileAltar, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();

        ItemStack inputStack = tileAltar.inv.getStackInSlot(0);
        this.renderItem(inputStack, tileAltar.getLevel(), poseStack, bufferSource, packedLight, packedOverlay);

        if (tileAltar.isVisuallyActive()) {
            if (tileAltar.getTier() >= 1) {
                renderRitualCircle(tileAltar, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
            }
            renderHoverArrays(tileAltar, partialTick, poseStack, bufferSource);
        }

        float level = (float) tileAltar.getMainTank() / (float) tileAltar.getMainCapacity();
        if (level == 0) {
            return;
        }
        this.renderFluid(level, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private void renderRitualCircle(AraVitaeTile altar, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float ticks = (altar.getLevel().getGameTime() + partialTick);
        float rotation = (ticks * 0.5f) % 360f;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.01, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotation));

        float half = 1.682f;
        VertexConsumer buf = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RITUAL_TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        Vector3f normUp = new Vector3f();
        pose.transformNormal(0, 1, 0, normUp);
        Vector3f normDown = new Vector3f();
        pose.transformNormal(0, -1, 0, normDown);

        int fullbright = 0xF000F0;

        buf.addVertex(matrix, -half, 0, -half).setColor(255, 255, 255, 64).setUv(0, 0).setOverlay(packedOverlay).setLight(fullbright).setNormal(normUp.x, normUp.y, normUp.z);
        buf.addVertex(matrix, -half, 0, half).setColor(255, 255, 255, 64).setUv(0, 1).setOverlay(packedOverlay).setLight(fullbright).setNormal(normUp.x, normUp.y, normUp.z);
        buf.addVertex(matrix, half, 0, half).setColor(255, 255, 255, 64).setUv(1, 1).setOverlay(packedOverlay).setLight(fullbright).setNormal(normUp.x, normUp.y, normUp.z);
        buf.addVertex(matrix, half, 0, -half).setColor(255, 255, 255, 64).setUv(1, 0).setOverlay(packedOverlay).setLight(fullbright).setNormal(normUp.x, normUp.y, normUp.z);

        buf.addVertex(matrix, half, 0, -half).setColor(255, 255, 255, 64).setUv(1, 0).setOverlay(packedOverlay).setLight(fullbright).setNormal(normDown.x, normDown.y, normDown.z);
        buf.addVertex(matrix, half, 0, half).setColor(255, 255, 255, 64).setUv(1, 1).setOverlay(packedOverlay).setLight(fullbright).setNormal(normDown.x, normDown.y, normDown.z);
        buf.addVertex(matrix, -half, 0, half).setColor(255, 255, 255, 64).setUv(0, 1).setOverlay(packedOverlay).setLight(fullbright).setNormal(normDown.x, normDown.y, normDown.z);
        buf.addVertex(matrix, -half, 0, -half).setColor(255, 255, 255, 64).setUv(0, 0).setOverlay(packedOverlay).setLight(fullbright).setNormal(normDown.x, normDown.y, normDown.z);

        poseStack.popPose();
    }

    private void renderItem(ItemStack inputStack, Level level , PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        //inputStack = new ItemStack(Blocks.DIAMOND_BLOCK);
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();
        if (!inputStack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1, 0.5);
            float rotation = (float) (720.0F * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            poseStack.scale(0.5F, 0.5F, 0.5F);
            BakedModel bakedModel = itemRenderer.getModel(inputStack, level, (LivingEntity) null, 1);
            itemRenderer.render(inputStack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, packedLight, packedOverlay, bakedModel);
            poseStack.popPose();
        }
    }

    private static final ResourceLocation[] CAPSTONE_ARRAY_TEXTURES = {
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

    private void renderHoverArrays(AraVitaeTile altar, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        Level level = altar.getLevel();
        if (level == null) return;
        int tier = altar.getTier();
        if (tier < 0 || tier >= NVMultiblock.TIER_LIST.length) return;
        AltarTier tierData = NVMultiblock.TIER_LIST[tier];
        if (tierData == null) return;

        long gameTime = level.getGameTime();
        int capIndex = 0;
        for (AltarEffect effect : tierData.effects()) {
            if (effect.type() != AltarEffectType.CAP_RENDER_HOVER_ARRAY) continue;
            for (BlockPos cap : effect.origins()) {
                renderCapstoneArray(altar, cap, capIndex, gameTime, partialTick, poseStack, bufferSource);
                spawnCascadeParticles(altar, cap, capIndex, gameTime);
                capIndex++;
            }
        }
    }

    private void renderCapstoneArray(AraVitaeTile altar, BlockPos cap, int capIndex, long gameTime, float partialTick,
                                     PoseStack poseStack, MultiBufferSource bufferSource) {
        float cycleTime = ((gameTime + capIndex * CAP_PHASE_OFFSET) % CYCLE_TICKS) + partialTick;
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

        ResourceLocation texture = CAPSTONE_ARRAY_TEXTURES[pickTextureIndex(altar, capIndex, gameTime)];

        poseStack.pushPose();
        poseStack.translate(cap.getX() + 0.5, cap.getY() + 1.0 + 0.01 + height, cap.getZ() + 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.scale(pulse, 1f, pulse);
        poseStack.translate(-0.5, 0, -0.5);

        AlchemyArrayRenderer.renderArrayQuad(texture, poseStack, bufferSource, 255, 255, 255, a);

        poseStack.popPose();
    }

    private void spawnCascadeParticles(AraVitaeTile altar, BlockPos cap, int capIndex, long gameTime) {
        long phased = (gameTime + capIndex * CAP_PHASE_OFFSET) % CYCLE_TICKS;
        if (phased < CASCADE_START || phased >= CASCADE_END) return;

        Level level = altar.getLevel();
        if (level == null) return;

        float cascadeProgress = (phased - CASCADE_START) / (float) (CASCADE_END - CASCADE_START);
        float sourceHeight;
        if (phased < RISE_END) {
            float riseP = (phased - RISE_START) / (float) (RISE_END - RISE_START);
            sourceHeight = easeInOut(Mth.clamp(riseP, 0f, 1f)) * CASCADE_HEIGHT;
        } else {
            sourceHeight = CASCADE_HEIGHT * (1f - 0.3f * cascadeProgress);
        }

        var altarPos = altar.getBlockPos();
        double baseX = altarPos.getX() + cap.getX() + 0.5;
        double baseY = altarPos.getY() + cap.getY() + 1.0 + sourceHeight;
        double baseZ = altarPos.getZ() + cap.getZ() + 0.5;

        var rng = level.random;
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

    private void renderFluid(float fluidLevel, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        Minecraft minecraft = Minecraft.getInstance();
        IClientFluidTypeExtensions fluidClientInfo = IClientFluidTypeExtensions.of(NVFluids.ESSENTIA_VITAE_TYPE.get());
        RenderType blockRenderType = ItemBlockRenderTypes.getRenderLayer(NVFluids.ESSENTIA_VITAE_SOURCE.get().defaultFluidState());
        TextureAtlasSprite texture = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidClientInfo.getStillTexture());
        int tintColour = fluidClientInfo.getTintColor();

        VertexConsumer buf = bufferSource.getBuffer(RenderTypeHelper.getEntityRenderType(blockRenderType, false));
        float minHeight = 7F/16F;
        float maxHeight = 11F/16F;
        float start = 3F/16F;
        float end = 13F/16F;
        float height = minHeight + fluidLevel * (maxHeight - minHeight);

        Vector3f norm = new Vector3f();
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        float u0 = texture.getU0();
        float u1 = texture.getU1();

        float v0 = texture.getV0();
        float v1 = texture.getV1();

        pose.transformNormal(0, 1, 0, norm);
        RenderHelper.addVertex(buf, matrix, end, height, end, u0, v0, tintColour, light, overlay, norm);
        RenderHelper.addVertex(buf, matrix, end, height, start, u0, v1, tintColour, light, overlay, norm);
        RenderHelper.addVertex(buf, matrix, start, height, start, u1, v1, tintColour, light, overlay, norm);
        RenderHelper.addVertex(buf, matrix, start, height, end, u1, v0, tintColour, light, overlay, norm);
    }
}
