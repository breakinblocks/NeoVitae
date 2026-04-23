package com.breakinblocks.neovitae.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;

/**
 * Creates vanilla-style particles as replacements for the custom NeoVitae particles
 * when the useSimpleEffects config option is enabled.
 */
public class SimpleParticleFactory {

    private static class NVSimpleParticle extends SimpleAnimatedParticle {
        protected NVSimpleParticle(ClientLevel level, double x, double y, double z,
                                    SpriteSet sprites, float gravityIn) {
            super(level, x, y, z, sprites, gravityIn);
        }

        void configure(int color, int life, float size,
                       double motionX, double motionY, double motionZ, float grav) {
            this.setColor(color);
            this.setLifetime(life);
            this.xd = motionX;
            this.yd = motionY;
            this.zd = motionZ;
            this.gravity = grav;
            this.quadSize = size;
            this.setSpriteFromAge(this.sprites);
        }
    }

    public static Particle createSimpleFlame(ClientLevel level, double x, double y, double z,
                                              double xSpeed, double ySpeed, double zSpeed,
                                              SpriteSet sprites, int color) {
        NVSimpleParticle p = new NVSimpleParticle(level, x, y, z, sprites, 0);
        p.configure(color, 12 + level.getRandom().nextInt(9),
                0.15f + level.getRandom().nextFloat() * 0.05f,
                xSpeed, ySpeed, zSpeed, 0.0f);
        return p;
    }

    public static Particle createSimpleGlow(ClientLevel level, double x, double y, double z,
                                             SpriteSet sprites, int color) {
        NVSimpleParticle p = new NVSimpleParticle(level, x, y, z, sprites, 0);
        p.configure(color, 20 + level.getRandom().nextInt(15),
                0.1f + level.getRandom().nextFloat() * 0.05f,
                0, 0.005, 0, 0.0f);
        return p;
    }

    public static Particle createSimpleDrip(ClientLevel level, double x, double y, double z,
                                             double xSpeed, double ySpeed, double zSpeed,
                                             SpriteSet sprites, int color) {
        NVSimpleParticle p = new NVSimpleParticle(level, x, y, z, sprites, 0.04f);
        p.configure(color, 15 + level.getRandom().nextInt(10), 0.08f,
                xSpeed, ySpeed, zSpeed, 0.04f);
        return p;
    }

    public static Particle createSimpleBubble(ClientLevel level, double x, double y, double z,
                                               SpriteSet sprites, int color) {
        NVSimpleParticle p = new NVSimpleParticle(level, x, y, z, sprites, -0.02f);
        p.configure(color, 20 + level.getRandom().nextInt(15),
                0.06f + level.getRandom().nextFloat() * 0.03f,
                0, 0.02, 0, -0.02f);
        return p;
    }

    public static Particle createSimpleRune(ClientLevel level, double x, double y, double z,
                                             SpriteSet sprites, int color) {
        NVSimpleParticle p = new NVSimpleParticle(level, x, y, z, sprites, 0);
        p.configure(color, 30 + level.getRandom().nextInt(20), 0.12f,
                0, 0, 0, 0.0f);
        return p;
    }
}
