package com.breakinblocks.neovitae.client.render.stream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Mth;

/**
 * Represents a single animated energy stream flowing from source to target.
 * <p>
 * Three phases:
 * <ol>
 *   <li><b>Approach</b>: Head flies toward a point 1 block above the altar with wobble physics.</li>
 *   <li><b>Spiral</b>: Head corkscrews downward into the altar, radius tightening.</li>
 *   <li><b>Drain</b>: Head absorbed. All remaining path points accelerate toward the altar
 *       and are removed on arrival. Stream expires when the last point is consumed.</li>
 * </ol>
 * Alpha ramps from transparent at the source to opaque at the target.
 */
public class ActiveStream {

    private static final float GRAVITY = 0.2f;
    private static final float DAMPING = 0.985f;
    private static final float MAX_VEL = 0.05f;
    private static final float ACCEL = 0.01f;
    private static final float BASE_SCALE = 0.1f;
    private static final Random RANDOM = new Random();

    // Spiral parameters
    private static final float SPIRAL_RADIUS = 0.35f;
    private static final float SPIRAL_SPEED = 0.4f;
    private static final float SPIRAL_DESCENT_SPEED = 0.04f;

    // Drain parameters
    private static final float DRAIN_BASE_SPEED = 0.06f;
    private static final float DRAIN_ACCEL = 0.008f;

    private final String key;

    // Endpoints (world-space)
    private final double startX, startY, startZ;
    private final double altarX, altarY, altarZ;
    private final double approachX, approachY, approachZ;

    // Color (0-1 floats)
    private final float red, green, blue;

    // Physics state
    private double headX, headY, headZ;
    private double velX, velY, velZ;

    // Stream state
    private float currentScale;
    private int maxAge;
    private int age;
    private final int phaseOffset;
    private boolean expired = false;

    // Phase tracking
    private boolean spiraling = false;
    private boolean draining = false;
    private float spiralAngle = 0;
    private int drainAge = 0;

    // Destination in relative coords (for drain phase attraction)
    private final float altarRelX, altarRelY, altarRelZ;

    // Path points: each is {relX, relY, relZ, scale}
    private final List<float[]> pathPoints = new ArrayList<>();

    // Render cache (rebuilt each tick)
    private double[][] positions;
    private float[][] colors;
    private float[] radii;

    public ActiveStream(String key, double fromX, double fromY, double fromZ,
                        double toX, double toY, double toZ,
                        int color, int phaseOffset) {
        this.key = key;
        this.startX = fromX;
        this.startY = fromY;
        this.startZ = fromZ;

        this.altarX = toX;
        this.altarY = toY;
        this.altarZ = toZ;

        this.approachX = toX;
        this.approachY = toY + 1.0;
        this.approachZ = toZ;

        this.altarRelX = (float) (toX - fromX);
        this.altarRelY = (float) (toY - fromY);
        this.altarRelZ = (float) (toZ - fromZ);

        this.headX = fromX;
        this.headY = fromY;
        this.headZ = fromZ;
        this.phaseOffset = phaseOffset;

        this.currentScale = (float) (BASE_SCALE * (1.0 + RANDOM.nextGaussian() * 0.15));

        double dx = toX - fromX;
        double dy = (toY + 1.0) - fromY;
        double dz = toZ - fromZ;
        int dist = Math.max(1, (int) (Math.sqrt(dx * dx + dy * dy + dz * dz) * 21));
        // maxAge is a safety cap only - drain phase handles normal expiry
        this.maxAge = Math.max(dist, 20) + 200;

        this.red = ((color >> 16) & 0xFF) / 255.0f;
        this.green = ((color >> 8) & 0xFF) / 255.0f;
        this.blue = (color & 0xFF) / 255.0f;

        this.velX = Mth.sin(phaseOffset / 4.0f) * 0.015;
        this.velY = Mth.sin(phaseOffset / 3.0f) * 0.015;
        this.velZ = Mth.sin(phaseOffset / 2.0f) * 0.015;

        pathPoints.add(new float[]{0, 0, 0, 0.001f});
        pathPoints.add(new float[]{0, 0, 0, 0.001f});
    }

    public void tick() {
        if (age++ >= maxAge) {
            expired = true;
            return;
        }

        if (draining) {
            tickDrain();
        } else if (spiraling) {
            tickSpiral();
        } else {
            tickApproach();
        }

        rebuildRenderData();
    }

    private void tickApproach() {
        velY += 0.01 * GRAVITY;

        headX += velX;
        headY += velY;
        headZ += velZ;

        velX *= DAMPING;
        velY *= DAMPING;
        velZ *= DAMPING;

        velX = Mth.clamp(velX, -MAX_VEL, MAX_VEL);
        velY = Mth.clamp(velY, -MAX_VEL, MAX_VEL);
        velZ = Mth.clamp(velZ, -MAX_VEL, MAX_VEL);

        double dx = approachX - headX;
        double dy = approachY - headY;
        double dz = approachZ - headZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (dist > 0.001) {
            double ndx = dx / dist;
            double ndy = dy / dist;
            double ndz = dz / dist;
            double factor = ACCEL / Math.min(1.0, dist);
            velX += ndx * factor;
            velY += ndy * factor;
            velZ += ndz * factor;
        }

        float scale = currentScale * (0.75f + Mth.sin((phaseOffset + age) / 2.0f) * 0.25f);

        if (dist < 0.5) {
            spiraling = true;
            spiralAngle = (float) Math.atan2(headZ - altarZ, headX - altarX);
        }

        pathPoints.add(new float[]{
                (float) (headX - startX),
                (float) (headY - startY),
                (float) (headZ - startZ),
                scale
        });
    }

    private void tickSpiral() {
        spiralAngle += SPIRAL_SPEED;

        double heightAboveAltar = headY - altarY;
        float spiralProgress = (float) Mth.clamp(heightAboveAltar / 1.0, 0.0, 1.0);
        float currentSpiralRadius = SPIRAL_RADIUS * spiralProgress;

        headX = altarX + Math.cos(spiralAngle) * currentSpiralRadius;
        headZ = altarZ + Math.sin(spiralAngle) * currentSpiralRadius;
        headY -= SPIRAL_DESCENT_SPEED;

        float scale = currentScale * spiralProgress * (0.75f + Mth.sin((phaseOffset + age) / 2.0f) * 0.25f);

        if (headY <= altarY || spiralProgress < 0.05f) {
            // Head has reached the altar - switch to drain phase
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

    /**
     * Drain phase: all remaining points accelerate toward the altar center.
     * Points are removed when they arrive. Stream expires when all points are consumed.
     */
    private void tickDrain() {
        drainAge++;
        float speed = DRAIN_BASE_SPEED + drainAge * DRAIN_ACCEL;

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
                // Gently shrink scale as point nears altar
                pt[3] *= 0.97f;
            }
        }

        if (pathPoints.isEmpty()) {
            expired = true;
        }
    }

    private void rebuildRenderData() {
        int size = pathPoints.size();
        if (size < 3) {
            positions = null;
            return;
        }

        positions = new double[size][3];
        colors = new float[size][4];
        radii = new float[size];

        for (int i = 0; i < size; i++) {
            float[] pt = pathPoints.get(i);

            float wobbleX = Mth.sin((i + age) / 6.0f) * 0.03f;
            float wobbleY = Mth.sin((i + age) / 7.0f) * 0.03f;
            float wobbleZ = Mth.sin((i + age) / 8.0f) * 0.03f;

            positions[i][0] = pt[0] + wobbleX;
            positions[i][1] = pt[1] + wobbleY;
            positions[i][2] = pt[2] + wobbleZ;

            float variance = 1.0f + Mth.sin((i + age) / 3.0f) * 0.2f;
            radii[i] = pt[3] * variance;

            // Tail taper (source end, first 10 points)
            if (i < 10 && size > 12) {
                float t = (float) i / 10.0f;
                radii[i] *= Mth.sin(t * (float) (Math.PI / 2.0));
            }

            // Head taper (target end, last 5 points) - only during approach/spiral
            if (!draining) {
                int fromEnd = size - 1 - i;
                if (fromEnd == 0 || fromEnd == 1) {
                    radii[i] = 0;
                } else if (fromEnd == 2) {
                    radii[i] = (currentScale * 0.5f + radii[i]) / 2.0f;
                } else if (fromEnd == 3) {
                    radii[i] = (currentScale + radii[i]) / 2.0f;
                } else if (fromEnd == 4) {
                    radii[i] = (currentScale + radii[i] * 2.0f) / 3.0f;
                }
            }

            radii[i] = Math.max(0, radii[i]);

            float colorVar = 1.0f - Mth.sin((i + age) / 2.0f) * 0.1f;
            colors[i][0] = red * colorVar;
            colors[i][1] = green * colorVar;
            colors[i][2] = blue * colorVar;

            // Alpha: transparent at tail (source), opaque at head (target)
            float progress = (float) i / (size - 1);
            float alpha = 0.1f + 0.9f * progress * progress;
            colors[i][3] = alpha;
        }
    }

    public boolean isExpired() {
        return expired;
    }

    public String getKey() {
        return key;
    }

    public double[][] getPositions() {
        return positions;
    }

    public float[][] getColors() {
        return colors;
    }

    public float[] getRadii() {
        return radii;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getStartZ() {
        return startZ;
    }

    public int getAge() {
        return age;
    }

    public int getGrowingTick() {
        return -1;
    }

    public int getMaxAge() {
        return maxAge;
    }
}
