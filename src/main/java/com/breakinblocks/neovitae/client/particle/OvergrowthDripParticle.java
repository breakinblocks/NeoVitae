package com.breakinblocks.neovitae.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class OvergrowthDripParticle extends SingleQuadParticle {

    private static final float START_R = 0.80f;
    private static final float START_G = 0.13f;
    private static final float START_B = 0.13f;

    private static final float END_R = 0.27f;
    private static final float END_G = 0.80f;
    private static final float END_B = 0.20f;

    protected OvergrowthDripParticle(ClientLevel level, double x, double y, double z,
                                     double ySpeed, SpriteSet sprites) {
        super(level, x, y, z, sprites.first());

        this.rCol = START_R;
        this.gCol = START_G;
        this.bCol = START_B;
        this.alpha = 0.9f;

        this.quadSize = 0.03f + this.random.nextFloat() * 0.015f;
        this.lifetime = 15 + this.random.nextInt(10);

        this.xd = 0;
        this.yd = -0.04 - Math.abs(ySpeed) * 0.5;
        this.zd = 0;
        this.gravity = 0.8f;
        this.hasPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
        float t = this.lifetime <= 0 ? 1.0f : Math.min(1.0f, (float) this.age / (float) this.lifetime);
        this.rCol = START_R + (END_R - START_R) * t;
        this.gCol = START_G + (END_G - START_G) * t;
        this.bCol = START_B + (END_B - START_B) * t;
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return NVRenderPipelines.ADDITIVE_LAYER;
    }

    @Override
    public int getLightCoords(float partialTick) {
        return 0xF000F0;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new OvergrowthDripParticle(level, x, y, z, ySpeed, this.sprites);
        }
    }
}
