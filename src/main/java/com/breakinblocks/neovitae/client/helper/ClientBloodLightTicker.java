package com.breakinblocks.neovitae.client.helper;

import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.block.BloodLightBlock;
import com.breakinblocks.neovitae.common.blockentity.BloodLightBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.util.helper.ColorHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class ClientBloodLightTicker {

    private ClientBloodLightTicker() {}

    public static void tick(Level level, BlockPos pos, BlockState state, BloodLightBlockEntity be) {
        if (!state.getValue(BloodLightBlock.POWERED)) return;

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (!camera.isInitialized()) return;

        double dx = pos.getX() + 0.5 - camera.position().x;
        double dy = pos.getY() + 0.5 - camera.position().y;
        double dz = pos.getZ() + 0.5 - camera.position().z;
        if (dx * dx + dy * dy + dz * dz > 128.0 * 128.0) return;

        RandomSource random = level.getRandom();
        int color = ColorHelper.fromDye(be.getColor());
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;

        for (int i = 0; i < 2; i++) {
            double ox = (random.nextDouble() - 0.5) * 0.2;
            double oy = (random.nextDouble() - 0.5) * 0.2;
            double oz = (random.nextDouble() - 0.5) * 0.2;

            double vx = ox * 0.3;
            double vy = oy * 0.3 + 0.005;
            double vz = oz * 0.3;

            level.addAlwaysVisibleParticle(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), color),
                    true, cx + ox, cy + oy, cz + oz, vx, vy, vz);
        }

        if (level.getGameTime() % 24L == 0L) {
            level.addAlwaysVisibleParticle(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), color),
                    true, cx, cy, cz, 0, 0, 0);
        }
    }
}
