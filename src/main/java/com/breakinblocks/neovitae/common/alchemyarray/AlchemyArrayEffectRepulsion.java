// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;

import java.util.List;

public class AlchemyArrayEffectRepulsion extends AlchemyArrayEffect {

    private static final double RADIUS = 5.0;
    private static final double PUSH_STRENGTH = 0.4;

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide()) return false;

        if (ticksActive % 5 != 0) return false;

        BlockPos pos = tile.getBlockPos();
        Vec3 center = Vec3.atCenterOf(pos);
        AABB area = new AABB(pos).inflate(RADIUS);

        List<Mob> hostiles = level.getEntitiesOfClass(Mob.class, area);
        for (Mob mob : hostiles) {
            if (mob.getType().getCategory().isFriendly()) continue;

            Vec3 dir = mob.position().subtract(center).normalize();
            mob.setDeltaMovement(mob.getDeltaMovement().add(dir.scale(PUSH_STRENGTH)));
            mob.hurtMarked = true;
        }

        if (ticksActive % 20 == 0 && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0x4488FF),
                    center.x, center.y + 0.5, center.z, 8, RADIUS * 0.3, 0.2, RADIUS * 0.3, 0.01);
        }

        return false;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {}

    @Override
    public void readFromNBT(CompoundTag tag) {}

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectRepulsion();
    }
}
