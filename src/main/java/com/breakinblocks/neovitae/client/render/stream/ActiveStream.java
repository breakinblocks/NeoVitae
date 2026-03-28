package com.breakinblocks.neovitae.client.render.stream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Mth;
import com.breakinblocks.neovitae.api.stream.StreamEffect;
import com.breakinblocks.neovitae.util.helper.ColorHelper;

/**
 * Client-side instance of an active stream or blob effect, driven by a {@link StreamEffect} config.
 * <p>
 * <b>Streams</b> have three phases:
 * <ol>
 *   <li><b>Approach</b> &ndash; head flies toward approachHeight above target with wobble.</li>
 *   <li><b>Spiral</b> &ndash; head corkscrews down into the target (if enabled).</li>
 *   <li><b>Drain</b> &ndash; all remaining points accelerate into the target and are absorbed.</li>
 * </ol>
 * <b>Blobs</b> are stationary pulsing shapes that expire after their lifetime.
 */
public class ActiveStream {

    private static final Random RANDOM = new Random();
    private static final float DAMPING = 0.985f;
    private static final float BASE_ACCEL = 0.01f;
    private static final float BASE_MAX_VEL = 0.05f;
    private static final float DRAIN_BASE_SPEED = 0.06f;
    private static final float DRAIN_ACCEL = 0.008f;

    private final String key;
    private final StreamEffect effect;

    // Resolved positions
    private final double startX, startY, startZ;
    private final double altarX, altarY, altarZ;
    private final double approachX, approachY, approachZ;
    private final float altarRelX, altarRelY, altarRelZ;

    // Color
    private final float red, green, blue;

    // Physics state
    private double headX, headY, headZ;
    private double velX, velY, velZ;
    private float currentScale;

    // Phase
    private int age;
    private final int maxAge;
    private final int phaseOffset;
    private boolean expired = false;
    private boolean spiraling = false;
    private boolean draining = false;
    private float spiralAngle = 0;
    private int drainAge = 0;

    // Path: each entry is {relX, relY, relZ, scale}
    private final List<float[]> pathPoints = new ArrayList<>();

    // Render cache
    private double[][] positions;
    private float[][] colors;
    private float[] radii;

    public ActiveStream(String key, StreamEffect effect, int phaseOffset) {
        this.key = key;
        this.effect = effect;
        this.phaseOffset = phaseOffset;

        this.startX = effect.sourceX;
        this.startY = effect.sourceY;
        this.startZ = effect.sourceZ;
        this.altarX = effect.targetX;
        this.altarY = effect.targetY;
        this.altarZ = effect.targetZ;
        this.approachX = effect.targetX;
        this.approachY = effect.targetY + effect.approachHeight;
        this.approachZ = effect.targetZ;
        this.altarRelX = (float) (effect.targetX - startX);
        this.altarRelY = (float) (effect.targetY - startY);
        this.altarRelZ = (float) (effect.targetZ - startZ);

        this.headX = startX;
        this.headY = startY;
        this.headZ = startZ;

        this.currentScale = (float) (effect.scale * (1.0 + RANDOM.nextGaussian() * 0.15));

        this.red = ColorHelper.red(effect.color);
        this.green = ColorHelper.green(effect.color);
        this.blue = ColorHelper.blue(effect.color);

        if (effect.lifetime > 0) {
            this.maxAge = effect.lifetime + 200; // safety cap
        } else if (effect.stationary) {
            this.maxAge = 200;
        } else {
            double dx = approachX - startX;
            double dy = approachY - startY;
            double dz = approachZ - startZ;
            int dist = Math.max(1, (int) (Math.sqrt(dx * dx + dy * dy + dz * dz) * 21.0 / effect.speed));
            this.maxAge = dist + 200;
        }

        this.velX = Mth.sin(phaseOffset / 4.0f) * 0.015;
        this.velY = Mth.sin(phaseOffset / 3.0f) * 0.015;
        this.velZ = Mth.sin(phaseOffset / 2.0f) * 0.015;

        if (effect.stationary) {
            initBlobPoints();
        } else {
            pathPoints.add(new float[]{0, 0, 0, 0.001f});
            pathPoints.add(new float[]{0, 0, 0, 0.001f});
        }
    }

    private void initBlobPoints() {
        int count = 10;
        for (int i = 0; i < count; i++) {
            float t = (float) i / count;
            float py = (t - 0.5f) * effect.scale * 2.0f;
            float px = Mth.sin(t * (float) (Math.PI * 2)) * 0.02f;
            float pz = Mth.cos(t * (float) (Math.PI * 2)) * 0.02f;
            pathPoints.add(new float[]{px, py, pz, effect.scale});
        }
    }

    // ---- Tick ----

    public void tick() {
        if (age++ >= maxAge) {
            expired = true;
            return;
        }

        if (effect.stationary) {
            tickStationary();
        } else if (draining) {
            tickDrain();
        } else if (spiraling) {
            tickSpiral();
        } else {
            tickApproach();
        }

        rebuildRenderData();
    }

    private void tickStationary() {
        int life = effect.lifetime > 0 ? effect.lifetime : 100;
        if (age >= life) {
            // Fade out: shrink all points
            boolean anyLeft = false;
            for (float[] pt : pathPoints) {
                pt[3] *= 0.85f;
                if (pt[3] > 0.005f) anyLeft = true;
            }
            if (!anyLeft) expired = true;
            return;
        }
        float pulse = 1.0f + Mth.sin(age / 4.0f) * 0.2f;
        for (float[] pt : pathPoints) {
            pt[3] = effect.scale * pulse;
        }
    }

    private void tickApproach() {
        float spd = effect.speed;
        float maxVel = BASE_MAX_VEL * spd;
        float accel = BASE_ACCEL * spd;

        velY += 0.01 * effect.gravity;
        headX += velX;
        headY += velY;
        headZ += velZ;
        velX *= DAMPING;
        velY *= DAMPING;
        velZ *= DAMPING;
        velX = Mth.clamp(velX, -maxVel, maxVel);
        velY = Mth.clamp(velY, -maxVel, maxVel);
        velZ = Mth.clamp(velZ, -maxVel, maxVel);

        double dx = approachX - headX;
        double dy = approachY - headY;
        double dz = approachZ - headZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (dist > 0.001) {
            double factor = accel / Math.min(1.0, dist);
            velX += (dx / dist) * factor;
            velY += (dy / dist) * factor;
            velZ += (dz / dist) * factor;
        }

        float scale = currentScale * (0.75f + Mth.sin((phaseOffset + age) / 2.0f) * 0.25f);

        if (dist < 0.5) {
            if (effect.spiralInto) {
                spiraling = true;
                spiralAngle = (float) Math.atan2(headZ - altarZ, headX - altarX);
            } else {
                // No spiral - go directly to drain
                draining = true;
                drainAge = 0;
            }
        }

        pathPoints.add(new float[]{
                (float) (headX - startX),
                (float) (headY - startY),
                (float) (headZ - startZ),
                scale
        });
    }

    private void tickSpiral() {
        spiralAngle += effect.spiralSpeed;
        double heightAboveAltar = headY - altarY;
        float progress = (float) Mth.clamp(heightAboveAltar / Math.max(0.1, effect.approachHeight), 0.0, 1.0);
        float curRadius = effect.spiralRadius * progress;

        headX = altarX + Math.cos(spiralAngle) * curRadius;
        headZ = altarZ + Math.sin(spiralAngle) * curRadius;
        headY -= effect.spiralSpeed * 0.1;

        float scale = currentScale * progress * (0.75f + Mth.sin((phaseOffset + age) / 2.0f) * 0.25f);

        if (headY <= altarY || progress < 0.05f) {
            draining = true;
            drainAge = 0;
        } else {
            pathPoints.add(new float[]{
                    (float) (headX - startX),
                    (float) (headY - startY),
                    (float) (headZ - startZ),
                    Math.max(0, scale)
            });
        }
    }

    private void tickDrain() {
        drainAge++;
        float speed = (DRAIN_BASE_SPEED + drainAge * DRAIN_ACCEL) * effect.drainSpeed;

        Iterator<float[]> it = pathPoints.iterator();
        while (it.hasNext()) {
            float[] pt = it.next();
            float dx = altarRelX - pt[0];
            float dy = altarRelY - pt[1];
            float dz = altarRelZ - pt[2];
            float dist = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (dist < 0.15f) {
                it.remove();
            } else {
                float move = Math.min(speed, dist);
                pt[0] += (dx / dist) * move;
                pt[1] += (dy / dist) * move;
                pt[2] += (dz / dist) * move;
                pt[3] *= 0.97f;
            }
        }

        if (pathPoints.isEmpty()) {
            expired = true;
        }
    }

    // ---- Render Data ----

    private void rebuildRenderData() {
        int size = pathPoints.size();
        if (size < 3) {
            positions = null;
            return;
        }

        positions = new double[size][3];
        colors = new float[size][4];
        radii = new float[size];
        float wAmp = effect.wobbleAmplitude;
        float wFreq = effect.wobbleFrequency;

        for (int i = 0; i < size; i++) {
            float[] pt = pathPoints.get(i);

            float wx = Mth.sin((i + age) / (6.0f / wFreq)) * wAmp;
            float wy = Mth.sin((i + age) / (7.0f / wFreq)) * wAmp;
            float wz = Mth.sin((i + age) / (8.0f / wFreq)) * wAmp;

            positions[i][0] = pt[0] + wx;
            positions[i][1] = pt[1] + wy;
            positions[i][2] = pt[2] + wz;

            float variance = 1.0f + Mth.sin((i + age) / 3.0f) * 0.2f;
            radii[i] = pt[3] * variance;

            // Tail taper (source end)
            if (i < 10 && size > 12 && !effect.stationary) {
                radii[i] *= Mth.sin((float) i / 10.0f * (float) (Math.PI / 2.0));
            }

            // Head taper (target end) - only during approach/spiral
            if (!draining && !effect.stationary) {
                int fromEnd = size - 1 - i;
                if (fromEnd == 0 || fromEnd == 1) radii[i] = 0;
                else if (fromEnd == 2) radii[i] = (currentScale * 0.5f + radii[i]) / 2.0f;
                else if (fromEnd == 3) radii[i] = (currentScale + radii[i]) / 2.0f;
                else if (fromEnd == 4) radii[i] = (currentScale + radii[i] * 2.0f) / 3.0f;
            }

            radii[i] = Math.max(0, radii[i]);

            float colorVar = 1.0f - Mth.sin((i + age) / 2.0f) * 0.1f;
            colors[i][0] = red * colorVar;
            colors[i][1] = green * colorVar;
            colors[i][2] = blue * colorVar;

            // Alpha gradient
            if (effect.stationary) {
                colors[i][3] = effect.alphaEnd;
            } else {
                float progress = (float) i / (size - 1);
                colors[i][3] = effect.alphaStart + (effect.alphaEnd - effect.alphaStart) * progress * progress;
            }
        }
    }

    // ---- Accessors ----

    public boolean isExpired() { return expired; }
    public String getKey() { return key; }
    public StreamEffect getEffect() { return effect; }
    public double[][] getPositions() { return positions; }
    public float[][] getColors() { return colors; }
    public float[] getRadii() { return radii; }
    public double getStartX() { return startX; }
    public double getStartY() { return startY; }
    public double getStartZ() { return startZ; }
    public int getAge() { return age; }
    public int getTubeSegments() { return effect.tubeSegments; }
    public boolean isGlow() { return effect.glow; }
}
