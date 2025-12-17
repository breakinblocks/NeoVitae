package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayTile;

/**
 * Alchemy array effect that changes the time to night over a period.
 */
public class AlchemyArrayEffectNight extends AlchemyArrayEffect {

    private static final int START_TICK = 100;
    private static final int END_TICK = 200;
    private static final long NIGHT_TIME = 13000; // Minecraft night time

    private long startTime = -1;

    public AlchemyArrayEffectNight() {
    }

    @Override
    public boolean update(AlchemyArrayTile tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null) return false;

        if (ticksActive < START_TICK) {
            return false;
        }

        if (ticksActive == START_TICK) {
            startTime = level.getDayTime();
            // Drop items at start
            tile.doDropIngredients(true);
        }

        if (ticksActive < END_TICK) {
            // Interpolate time towards night
            float progress = (float)(ticksActive - START_TICK) / (END_TICK - START_TICK);
            long targetTime = NIGHT_TIME;

            // Calculate time difference, accounting for day wrap
            long timeDiff = targetTime - startTime;
            if (timeDiff < 0) timeDiff += 24000;

            long newTime = startTime + (long)(timeDiff * progress);

            if (level instanceof ServerLevel serverLevel) {
                // Apply to all dimensions
                for (ServerLevel worldLevel : serverLevel.getServer().getAllLevels()) {
                    worldLevel.setDayTime(newTime);
                }
            }
            return false;
        }

        // Spawn lightning effect on completion
        if (level instanceof ServerLevel serverLevel) {
            BlockPos pos = tile.getBlockPos();
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (bolt != null) {
                bolt.moveTo(Vec3.atBottomCenterOf(pos));
                bolt.setVisualOnly(true);
                serverLevel.addFreshEntity(bolt);
            }
        }

        return true; // Complete
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.putLong("startTime", startTime);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        startTime = tag.getLong("startTime");
    }

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectNight();
    }
}
