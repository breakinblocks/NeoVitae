package com.breakinblocks.neovitae.common.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import com.breakinblocks.neovitae.NeoVitae;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = NeoVitae.MODID)
public final class AlternatorLinks {

    public static final int MAX_RECEIVERS = 8;
    public static final int MAX_RANGE = 256;

    private static volatile boolean anyPowered = false;
    private static int poweredCount = 0;

    private static final Map<GlobalPos, GlobalPos> LINKS = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, AlternatorLinkData> STORAGE = new ConcurrentHashMap<>();

    private AlternatorLinks() {
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        AlternatorLinkData data = storage(level);
        ResourceKey<Level> dimension = level.dimension();
        for (Map.Entry<BlockPos, BlockPos> entry : data.entries().entrySet()) {
            LINKS.put(GlobalPos.of(dimension, entry.getKey()), GlobalPos.of(dimension, entry.getValue()));
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        ResourceKey<Level> dimension = level.dimension();
        LINKS.keySet().removeIf(pos -> pos.dimension().equals(dimension));
        STORAGE.remove(dimension);
        if (level instanceof PoweredReceiverAccess access) {
            poweredCount -= access.neovitae$getPoweredReceivers().size();
            access.neovitae$getPoweredReceivers().clear();
            if (poweredCount <= 0) {
                poweredCount = 0;
                anyPowered = false;
            }
        }
    }

    private static AlternatorLinkData storage(ServerLevel level) {
        return STORAGE.computeIfAbsent(level.dimension(), key -> level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(AlternatorLinkData::new, AlternatorLinkData::load),
                AlternatorLinkData.ID));
    }

    public static void link(ServerLevel level, BlockPos receiver, BlockPos source) {
        storage(level).put(receiver, source);
        LINKS.put(GlobalPos.of(level.dimension(), receiver), GlobalPos.of(level.dimension(), source));
    }

    public static void unlink(ServerLevel level, BlockPos receiver) {
        storage(level).drop(receiver);
        LINKS.remove(GlobalPos.of(level.dimension(), receiver));
        setPowered(level, receiver, false);
    }

    public static BlockPos getSource(ServerLevel level, BlockPos receiver) {
        if (LINKS.isEmpty()) return null;
        GlobalPos source = LINKS.get(GlobalPos.of(level.dimension(), receiver));
        return source == null ? null : source.pos();
    }

    public static boolean setPowered(ServerLevel level, BlockPos receiver, boolean powered) {
        if (!(level instanceof PoweredReceiverAccess access)) return false;
        boolean changed = powered
                ? access.neovitae$getPoweredReceivers().add(receiver.asLong())
                : access.neovitae$getPoweredReceivers().remove(receiver.asLong());
        if (changed) {
            poweredCount += powered ? 1 : -1;
            if (poweredCount <= 0) {
                poweredCount = 0;
                anyPowered = false;
            } else {
                anyPowered = true;
            }
        }
        return changed;
    }

    public static boolean isPowered(Object levelLike, BlockPos pos) {
        if (!anyPowered) return false;
        return levelLike instanceof PoweredReceiverAccess access
                && access.neovitae$getPoweredReceivers().contains(pos.asLong());
    }
}
