package com.breakinblocks.neovitae.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.util.helper.ColorHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BloodGlowParticle extends SingleQuadParticle {

    private final float baseAlpha = 0.35f;

    protected BloodGlowParticle(ClientLevel level, double x, double y, double z,
                                 SpriteSet sprites, int color, boolean rawColor) {
        super(level, x, y, z, sprites.first());

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

        this.alpha = baseAlpha;
        this.quadSize = 0.12f + this.random.nextFloat() * 0.03f;
        this.lifetime = 12;

        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.gravity = 0.0f;
        this.hasPhysics = false;
    }

    @Override
    public void tick() {
        super.tick();
        float progress = (float) this.age / (float) this.lifetime;
        this.alpha = baseAlpha * (1.0f - progress * progress);
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return NVRenderPipelines.ADDITIVE_LAYER;
    }

    @Override
    public int getLightCoords(float partialTick) {
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
                                        double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            if (NeoVitae.CLIENT_CONFIG.USE_SIMPLE_EFFECTS.get()) {
                return SimpleParticleFactory.createSimpleGlow(level, x, y, z, this.sprites, options.color());
            }
            return new BloodGlowParticle(level, x, y, z, this.sprites, options.color(), options.rawColor());
        }
    }
}
