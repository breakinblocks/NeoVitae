package com.breakinblocks.neovitae.ritual;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.common.world.SpawnerSuppressionData;
import com.breakinblocks.neovitae.ritual.types.RitualTormentNexus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks spawners a Torment Nexus has claimed, so they stay frozen even before the
 * ritual stone's chunk has loaded. The set is persisted per dimension: relying on the
 * master ritual stone's block entity to repopulate it left a window at server start
 * where a spawner's chunk began ticking first and got a free wave of mobs.
 */
@EventBusSubscriber(modid = NeoVitae.MODID)
public final class SpawnerSuppression {

    private static final Map<GlobalPos, GlobalPos> SUPPRESSED = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, SpawnerSuppressionData> STORAGE = new ConcurrentHashMap<>();

    private SpawnerSuppression() {
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        SpawnerSuppressionData data = storage(level);
        ResourceKey<Level> dimension = level.dimension();
        for (Map.Entry<BlockPos, BlockPos> entry : data.entries().entrySet()) {
            SUPPRESSED.put(GlobalPos.of(dimension, entry.getKey()), GlobalPos.of(dimension, entry.getValue()));
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        ResourceKey<Level> dimension = level.dimension();
        SUPPRESSED.keySet().removeIf(pos -> pos.dimension().equals(dimension));
        STORAGE.remove(dimension);
    }

    private static SpawnerSuppressionData storage(ServerLevel level) {
        return STORAGE.computeIfAbsent(level.dimension(), key -> level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(SpawnerSuppressionData::new, SpawnerSuppressionData::load),
                SpawnerSuppressionData.ID));
    }

    public static boolean add(ServerLevel level, BlockPos spawner, BlockPos master) {
        GlobalPos key = GlobalPos.of(level.dimension(), spawner);
        GlobalPos value = GlobalPos.of(level.dimension(), master);
        storage(level).put(spawner, master);
        return !value.equals(SUPPRESSED.put(key, value));
    }

    public static boolean remove(ServerLevel level, BlockPos spawner) {
        storage(level).drop(spawner);
        return SUPPRESSED.remove(GlobalPos.of(level.dimension(), spawner)) != null;
    }

    public static boolean isEmpty() {
        return SUPPRESSED.isEmpty();
    }

    public static void clear() {
        SUPPRESSED.clear();
        STORAGE.clear();
    }

    public static boolean isSuppressed(Level level, BlockPos pos) {
        if (SUPPRESSED.isEmpty() || !(level instanceof ServerLevel serverLevel)) return false;
        GlobalPos master = SUPPRESSED.get(GlobalPos.of(serverLevel.dimension(), pos));
        if (master == null) return false;
        if (!master.dimension().equals(serverLevel.dimension()) || !serverLevel.hasChunkAt(master.pos())) {
            return true;
        }
        if (serverLevel.getBlockEntity(master.pos()) instanceof MasterRitualStoneBlockEntity stone
                && stone.isActive()
                && stone.getCurrentRitual() instanceof RitualTormentNexus) {
            return true;
        }
        remove(serverLevel, pos);
        return false;
    }

    public static boolean coversSpawnAt(ResourceKey<Level> dimension, BlockPos at) {
        for (GlobalPos gp : SUPPRESSED.keySet()) {
            if (gp.dimension().equals(dimension) && isNear(gp.pos(), at)) return true;
        }
        return false;
    }

    public static boolean isNear(BlockPos spawner, BlockPos at) {
        return Math.abs(spawner.getX() - at.getX()) <= 8
                && Math.abs(spawner.getZ() - at.getZ()) <= 8
                && Math.abs(spawner.getY() - at.getY()) <= 6;
    }
}
