package com.breakinblocks.neovitae.client.render.stream;

import com.breakinblocks.neovitae.api.stream.StreamEffect;
import com.breakinblocks.neovitae.util.helper.ColorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;
import net.minecraft.util.ARGB;

/**
 * Lightweight vanilla-particle replacement for the custom stream renderer.
 * Spawns colored dust particles along the source-to-target path each tick.
 */
public class SimpleStreamEffect {

    private final StreamEffect effect;
    private int age;
    private final int maxAge;
    private boolean expired;

    private double targetX, targetY, targetZ;

    public SimpleStreamEffect(StreamEffect effect) {
        this.effect = effect;

        if (effect.lifetime > 0) {
            this.maxAge = effect.lifetime;
        } else if (effect.stationary) {
            this.maxAge = 100;
        } else {
            double dx = effect.targetX - effect.sourceX;
            double dy = effect.targetY - effect.sourceY;
            double dz = effect.targetZ - effect.sourceZ;
            this.maxAge = Math.max(20, (int) (Math.sqrt(dx * dx + dy * dy + dz * dz) * 10));
        }

        this.targetX = effect.targetX;
        this.targetY = effect.targetY;
        this.targetZ = effect.targetZ;
    }

    public void tick() {
        if (age++ >= maxAge) {
            expired = true;
            return;
        }

        updateTrackedTarget();

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        float r = ColorHelper.red(effect.color);
        float g = ColorHelper.green(effect.color);
        float b = ColorHelper.blue(effect.color);
        float scale = Math.max(0.4f, effect.scale * 3.0f);

        DustParticleOptions dust = new DustParticleOptions(ARGB.colorFromFloat(1.0f, r, g, b), scale);

        if (effect.stationary) {
            float pulse = 1.0f + Mth.sin(age / 4.0f) * 0.3f;
            double spread = effect.scale * pulse;
            for (int i = 0; i < 2; i++) {
                double px = effect.sourceX + (level.getRandom().nextDouble() - 0.5) * spread;
                double py = effect.sourceY + (level.getRandom().nextDouble() - 0.5) * spread;
                double pz = effect.sourceZ + (level.getRandom().nextDouble() - 0.5) * spread;
                level.addParticle(dust, px, py, pz, 0, 0.01, 0);
            }
        } else {
            double dx = targetX - effect.sourceX;
            double dy = targetY - effect.sourceY;
            double dz = targetZ - effect.sourceZ;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            int count = Math.max(2, (int) (dist * 2));

            float progress = (float) age / maxAge;
            float headT = Math.min(1.0f, progress * 1.5f);

            for (int i = 0; i < count; i++) {
                float t = (float) i / count;
                if (t > headT) break;

                double px = effect.sourceX + dx * t;
                double py = effect.sourceY + dy * t;
                double pz = effect.sourceZ + dz * t;

                px += (level.getRandom().nextDouble() - 0.5) * 0.1;
                py += (level.getRandom().nextDouble() - 0.5) * 0.1;
                pz += (level.getRandom().nextDouble() - 0.5) * 0.1;

                if (level.getRandom().nextFloat() < 0.3f) {
                    level.addParticle(dust, px, py, pz, 0, 0, 0);
                }
            }
        }
    }

    private void updateTrackedTarget() {
        if (effect.targetEntityId < 0) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        Entity entity = mc.level.getEntity(effect.targetEntityId);
        if (entity == null || !entity.isAlive()) return;
        this.targetX = entity.getX();
        this.targetY = entity.getY() + entity.getBbHeight() * 0.5;
        this.targetZ = entity.getZ();
    }

    public boolean isExpired() {
        return expired;
    }
}
