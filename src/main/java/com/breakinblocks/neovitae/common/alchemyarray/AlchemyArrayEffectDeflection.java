package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AlchemyArrayEffectDeflection extends AlchemyArrayEffect {

    private static final double COLUMN_RADIUS = 1.5;
    private static final int COLUMN_HEIGHT = 6;
    private final Set<UUID> deflected = new HashSet<>();

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide) return false;

        BlockPos pos = tile.getBlockPos();
        AABB column = new AABB(
                pos.getX() + 0.5 - COLUMN_RADIUS, pos.getY(),
                pos.getZ() + 0.5 - COLUMN_RADIUS,
                pos.getX() + 0.5 + COLUMN_RADIUS, pos.getY() + COLUMN_HEIGHT,
                pos.getZ() + 0.5 + COLUMN_RADIUS
        );

        List<Projectile> projectiles = level.getEntitiesOfClass(Projectile.class, column);
        for (Projectile proj : projectiles) {
            if (proj.isRemoved()) continue;
            if (deflected.contains(proj.getUUID())) continue;

            Vec3 motion = proj.getDeltaMovement();
            proj.setDeltaMovement(-motion.x, Math.abs(motion.y) * 0.5 + 0.1, -motion.z);
            proj.setOwner(null);
            proj.hurtMarked = true;
            deflected.add(proj.getUUID());

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xCC2244),
                        proj.getX(), proj.getY(), proj.getZ(), 4, 0.15, 0.15, 0.15, 0.03);
            }
        }

        if (ticksActive % 100 == 0) {
            deflected.clear();
        }

        if (ticksActive % 30 == 0 && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.RUNE_GLOW.get(), 0xCC2244),
                    pos.getX() + 0.5, pos.getY() + COLUMN_HEIGHT * 0.5, pos.getZ() + 0.5,
                    2, COLUMN_RADIUS * 0.3, COLUMN_HEIGHT * 0.2, COLUMN_RADIUS * 0.3, 0.0);
        }

        return false;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {}

    @Override
    public void readFromNBT(CompoundTag tag) {}

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectDeflection();
    }
}
