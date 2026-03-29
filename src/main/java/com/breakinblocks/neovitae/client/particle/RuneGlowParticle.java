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
public class RuneGlowParticle extends TextureSheetParticle {

    protected RuneGlowParticle(ClientLevel level, double x, double y, double z,
                                SpriteSet sprites, int color) {
        super(level, x, y, z);

        this.rCol = ColorHelper.red(color);
        this.gCol = ColorHelper.green(color);
        this.bCol = ColorHelper.blue(color);
        this.alpha = 0.6f;

        this.quadSize = 0.4f + this.random.nextFloat() * 0.1f;
        this.lifetime = 30 + this.random.nextInt(20);

        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.gravity = 0.0f;
        this.hasPhysics = false;

        this.pickSprite(sprites);
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        float progress = ((float) this.age + partialTicks) / (float) this.lifetime;
        this.alpha = 0.6f * (1.0f - progress);
        float pulse = 1.0f + (float) Math.sin(this.age * 0.2) * 0.1f;
        this.quadSize = (0.4f + this.random.nextFloat() * 0.01f) * pulse;
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
            return new RuneGlowParticle(level, x, y, z, this.sprites, options.color());
        }
    }
}
