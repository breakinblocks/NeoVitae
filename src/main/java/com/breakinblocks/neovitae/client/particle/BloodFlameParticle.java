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
public class BloodFlameParticle extends TextureSheetParticle {

    private final float baseQuadSize;
    private static final float BASE_ALPHA = 0.5f;

    protected BloodFlameParticle(ClientLevel level, double x, double y, double z,
                                  double xSpeed, double ySpeed, double zSpeed,
                                  SpriteSet sprites, int color) {
        super(level, x, y, z);

        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        float r = ColorHelper.red(color);
        float g = ColorHelper.green(color);
        float b = ColorHelper.blue(color);
        this.rCol = Math.min(1.0f, r * (0.85f + this.random.nextFloat() * 0.3f));
        this.gCol = Math.min(1.0f, g * (0.85f + this.random.nextFloat() * 0.3f));
        this.bCol = Math.min(1.0f, b * (0.85f + this.random.nextFloat() * 0.3f));

        this.alpha = 0.0f;

        this.baseQuadSize = 0.35f + this.random.nextFloat() * 0.15f;
        this.quadSize = this.baseQuadSize;

        this.lifetime = 18 + this.random.nextInt(10);

        this.gravity = 0.0f;
        this.hasPhysics = false;
        this.friction = 0.96f;

        // Pick one frame at random for the particle's whole life. Removing the per-tick
        // sprite cycle from the original implementation kills the strobe; variety comes
        // from different particles landing on different sheet frames instead.
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
        // Smoothstep at both ends so brightness eases off with zero slope at p=1.0
        // instead of a linear ramp, which the eye reads as a snap.
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
                return SimpleParticleFactory.createSimpleFlame(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites, options.color());
            }
            return new BloodFlameParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites, options.color());
        }
    }
}
