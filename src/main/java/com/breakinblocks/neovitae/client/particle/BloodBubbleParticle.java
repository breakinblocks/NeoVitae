package com.breakinblocks.neovitae.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.Camera;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.util.helper.ColorHelper;

public class BloodBubbleParticle extends SingleQuadParticle {

    private final float baseQuadSize;
    private final SpriteSet sprites;

    protected BloodBubbleParticle(ClientLevel level, double x, double y, double z,
                                   SpriteSet sprites, int color) {
        super(level, x, y, z, sprites.first());
        this.sprites = sprites;

        this.rCol = ColorHelper.red(color);
        this.gCol = ColorHelper.green(color);
        this.bCol = ColorHelper.blue(color);
        this.alpha = 0.7f;

        this.baseQuadSize = 0.05f + this.random.nextFloat() * 0.04f;
        this.quadSize = this.baseQuadSize;
        this.lifetime = 20 + this.random.nextInt(15);

        this.xd = (this.random.nextDouble() - 0.5) * 0.01;
        this.yd = 0.02 + this.random.nextDouble() * 0.02;
        this.zd = (this.random.nextDouble() - 0.5) * 0.01;
        this.gravity = -0.01f;
        this.hasPhysics = false;
        this.friction = 0.98f;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);

        float progress = (float) this.age / (float) this.lifetime;
        if (progress > 0.8f) {
            float pop = (progress - 0.8f) / 0.2f;
            this.quadSize = this.baseQuadSize * (1.0f + pop * 0.5f);
            this.alpha = 0.7f * (1.0f - pop);
        } else {
            float wobble = 1.0f + (float) Math.sin(this.age * 0.3) * 0.15f;
            this.quadSize = this.baseQuadSize * wobble;
        }
    }

    @Override
    public void extract(QuadParticleRenderState state, Camera camera, float partialTick) {
        super.extract(state, camera, partialTick);
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return NVRenderPipelines.ADDITIVE_LAYER;
    }

    @Override
    public int getLightCoords(float partialTick) {
        return 0xF000F0;
    }

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
                return SimpleParticleFactory.createSimpleBubble(level, x, y, z, this.sprites, options.color());
            }
            return new BloodBubbleParticle(level, x, y, z, this.sprites, options.color());
        }
    }
}
