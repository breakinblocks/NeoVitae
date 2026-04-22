package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import net.minecraft.world.entity.EntitySpawnReason;

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
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null) return false;

        if (ticksActive < START_TICK) {
            return false;
        }

        Holder<WorldClock> overworldClock = level.registryAccess()
                .lookupOrThrow(Registries.WORLD_CLOCK)
                .getOrThrow(WorldClocks.OVERWORLD);

        if (ticksActive == START_TICK) {
            startTime = level.clockManager().getTotalTicks(overworldClock);
            tile.doDropIngredients(true);
        }

        if (ticksActive < END_TICK) {
            if (level instanceof ServerLevel serverLevel) {
                float progress = (float) (ticksActive - START_TICK) / (END_TICK - START_TICK);
                long dayTicks = startTime % 24000L;
                long timeDiff = NIGHT_TIME - dayTicks;
                if (timeDiff < 0) timeDiff += 24000L;
                long newTime = startTime + (long) (timeDiff * progress);
                serverLevel.getServer().getAllLevels().forEach(worldLevel ->
                        worldLevel.clockManager().setTotalTicks(overworldClock, newTime));
            }
            return false;
        }

        // Spawn lightning effect on completion
        if (level instanceof ServerLevel serverLevel) {
            BlockPos pos = tile.getBlockPos();
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel, EntitySpawnReason.TRIGGERED);
            if (bolt != null) {
                bolt.snapTo(Vec3.atBottomCenterOf(pos));
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
        startTime = tag.getLongOr("startTime", 0L);
    }

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectNight();
    }
}
