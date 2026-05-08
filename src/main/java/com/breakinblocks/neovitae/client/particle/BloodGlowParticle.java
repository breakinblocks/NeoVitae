package com.breakinblocks.neovitae.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.util.helper.ColorHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BloodGlowParticle extends TextureSheetParticle {

    private static final float BASE_ALPHA = 0.35f;
    private float baseQuadSize;

    protected BloodGlowParticle(ClientLevel level, double x, double y, double z,
                                 SpriteSet sprites, int color, boolean rawColor) {
        super(level, x, y, z);

        float r = ColorHelper.red(color);
        float g = ColorHelper.green(color);
        float b = ColorHelper.blue(color);
        if (rawColor) {
            this.rCol = r;
            this.gCol = g;
            this.bCol = b;
        } else {
            this.rCol = Math.min(1.0f, r * 0.5f + 0.5f);
            this.gCol = Math.min(1.0f, g * 0.5f + 0.5f);
            this.bCol = Math.min(1.0f, b * 0.5f + 0.5f);
        }

        this.alpha = 0.0f;
        this.baseQuadSize = 0.12f + this.random.nextFloat() * 0.03f;
        this.quadSize = this.baseQuadSize;
        this.lifetime = 18;

        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.gravity = 0.0f;
        this.hasPhysics = false;

        this.pickSprite(sprites);
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        float progress = ((float) this.age + scaleFactor) / (float) this.lifetime;
        return this.baseQuadSize * envelope(progress);
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        float progress = ((float) this.age + partialTicks) / (float) this.lifetime;
        this.alpha = BASE_ALPHA * envelope(progress);
        super.render(buffer, camera, partialTicks);
    }

    private static float envelope(float progress) {
        float fadeIn = smoothstep(0.0f, 0.20f, progress);
        float fadeOut = 1.0f - smoothstep(0.55f, 1.0f, progress);
        return fadeIn * fadeOut;
    }

    private static float smoothstep(float edge0, float edge1, float x) {
        float t = Math.max(0.0f, Math.min(1.0f, (x - edge0) / (edge1 - edge0)));
        return t * t * (3.0f - 2.0f * t);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return BloodFlameRenderType.ADDITIVE_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<ColoredParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(ColoredParticleOptions options, ClientLevel level,
                                        double x, double y, double z,
                                        double xSpeed, double ySpeed, double zSpeed) {
            if (NeoVitae.CLIENT_CONFIG.USE_SIMPLE_EFFECTS.get()) {
                return SimpleParticleFactory.createSimpleGlow(level, x, y, z, this.sprites, options.color());
            }
            return new BloodGlowParticle(level, x, y, z, this.sprites, options.color(), options.rawColor());
        }
    }
}
