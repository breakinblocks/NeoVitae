package com.breakinblocks.neovitae.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import com.breakinblocks.neovitae.util.helper.ColorHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BloodFlameParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private final float baseQuadSize;

    protected BloodFlameParticle(ClientLevel level, double x, double y, double z,
                                  double xSpeed, double ySpeed, double zSpeed,
                                  SpriteSet sprites, int color) {
        super(level, x, y, z);
        this.sprites = sprites;

        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        float r = ColorHelper.red(color);
        float g = ColorHelper.green(color);
        float b = ColorHelper.blue(color);
        this.rCol = Math.min(1.0f, r * (0.85f + this.random.nextFloat() * 0.3f));
        this.gCol = Math.min(1.0f, g * (0.85f + this.random.nextFloat() * 0.3f));
        this.bCol = Math.min(1.0f, b * (0.85f + this.random.nextFloat() * 0.3f));

        this.alpha = 0.5f;

        this.baseQuadSize = 0.35f + this.random.nextFloat() * 0.15f;
        this.quadSize = this.baseQuadSize;

        this.lifetime = 12 + this.random.nextInt(9);

        this.gravity = 0.0f;
        this.hasPhysics = false;
        this.friction = 0.96f;

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        int spriteFrame = (this.age * 3) % this.lifetime;
        this.setSprite(this.sprites.get(spriteFrame, this.lifetime));
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        float progress = ((float) this.age + scaleFactor) / (float) this.lifetime;
        float fadeIn = Math.min(1.0f, progress * 5.0f);
        float fadeOut = 1.0f - Math.max(0.0f, (progress - 0.6f) / 0.4f);
        return this.baseQuadSize * fadeIn * fadeOut;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        float progress = ((float) this.age + partialTicks) / (float) this.lifetime;
        this.alpha = 0.5f * (1.0f - progress * progress);
        super.render(buffer, camera, partialTicks);
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
            return new BloodFlameParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites, options.color());
        }
    }
}
