package com.breakinblocks.neovitae.client.particle;

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
public class BloodDripParticle extends TextureSheetParticle {

    protected BloodDripParticle(ClientLevel level, double x, double y, double z,
                                 double ySpeed, SpriteSet sprites, int color) {
        super(level, x, y, z);

        this.rCol = ColorHelper.red(color);
        this.gCol = ColorHelper.green(color);
        this.bCol = ColorHelper.blue(color);
        this.alpha = 0.9f;

        this.quadSize = 0.06f + this.random.nextFloat() * 0.03f;
        this.lifetime = 15 + this.random.nextInt(10);

        this.xd = 0;
        this.yd = -0.04 - Math.abs(ySpeed) * 0.5;
        this.zd = 0;
        this.gravity = 0.8f;
        this.hasPhysics = true;

        this.pickSprite(sprites);
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
                return SimpleParticleFactory.createSimpleDrip(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites, options.color());
            }
            return new BloodDripParticle(level, x, y, z, ySpeed, this.sprites, options.color());
        }
    }
}
