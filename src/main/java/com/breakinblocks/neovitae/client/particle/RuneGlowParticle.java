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
public class RuneGlowParticle extends SingleQuadParticle {

    private final float baseAlpha = 0.6f;
    private final float baseQuadSize;

    protected RuneGlowParticle(ClientLevel level, double x, double y, double z,
                                SpriteSet sprites, int color) {
        super(level, x, y, z, sprites.first());

        this.rCol = ColorHelper.red(color);
        this.gCol = ColorHelper.green(color);
        this.bCol = ColorHelper.blue(color);
        this.alpha = baseAlpha;

        this.baseQuadSize = 0.4f + this.random.nextFloat() * 0.1f;
        this.quadSize = baseQuadSize;
        this.lifetime = 30 + this.random.nextInt(20);

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
        this.alpha = baseAlpha * (1.0f - progress);
        float pulse = 1.0f + (float) Math.sin(this.age * 0.2) * 0.1f;
        this.quadSize = baseQuadSize * pulse;
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
                return SimpleParticleFactory.createSimpleRune(level, x, y, z, this.sprites, options.color());
            }
            return new RuneGlowParticle(level, x, y, z, this.sprites, options.color());
        }
    }
}
