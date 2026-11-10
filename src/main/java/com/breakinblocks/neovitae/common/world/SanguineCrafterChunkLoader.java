package com.breakinblocks.neovitae.common.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.SanguineCrafterBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.SanguineCrafterBlockEntity.ChunkLoading;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class SanguineCrafterChunkLoader {

    private static final TicketController CONTROLLER =
            new TicketController(NeoVitae.rl("sanguine_crafter"), SanguineCrafterChunkLoader::validate);

    private static final Set<GlobalPos> FORCED = new HashSet<>();

    private SanguineCrafterChunkLoader() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(RegisterTicketControllersEvent.class, event -> event.register(CONTROLLER));
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedInEvent.class, event -> onLogin(event.getEntity().getUUID(), event.getEntity().level().getServer()));
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedOutEvent.class, event -> onLogout(event.getEntity().getUUID(), event.getEntity().level().getServer()));
        NeoForge.EVENT_BUS.addListener(ServerStoppingEvent.class, event -> FORCED.clear());
    }

    private static SanguineCrafterAnchorData data(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(SanguineCrafterAnchorData.TYPE);
    }

    private static boolean enabled() {
        return NeoVitae.SERVER_CONFIG.SANGUINE_CRAFTER_CHUNK_LOADING.get();
    }

    private static boolean ownerOnlineOnly() {
        return NeoVitae.SERVER_CONFIG.SANGUINE_CRAFTER_CHUNK_LOADING_OWNER_ONLINE_ONLY.get();
    }

    private static boolean isOnline(MinecraftServer server, UUID owner) {
        return server.getPlayerList().getPlayer(owner) != null;
    }

    public static ChunkLoading update(ServerLevel level, SanguineCrafterBlockEntity crafter, boolean running, boolean wanted) {
        GlobalPos key = GlobalPos.of(level.dimension(), crafter.getBlockPos());
        SanguineCrafterAnchorData data = data(level.getServer());
        UUID owner = crafter.getOwner();

        if (!enabled()) {
            drop(level, key, data);
            return ChunkLoading.DISABLED;
        }
        if (!wanted) {
            drop(level, key, data);
            return ChunkLoading.TURNED_OFF;
        }
        if (!running || owner == null) {
            drop(level, key, data);
            return ChunkLoading.INACTIVE;
        }

        BlockPos center = crafter.getCenter();
        SanguineCrafterAnchorData.Anchor anchor = data.anchors().get(key);
        if (anchor == null) {
            int cap = NeoVitae.SERVER_CONFIG.SANGUINE_CRAFTER_CHUNK_LOADING_MAX_PER_PLAYER.get();
            if (cap > 0 && data.countFor(owner) >= cap) {
                return ChunkLoading.LIMIT_REACHED;
            }
            anchor = new SanguineCrafterAnchorData.Anchor(owner, center);
            data.put(key, anchor);
        } else if (!anchor.owner().equals(owner) || !anchor.center().equals(center)) {
            unforce(level, key, anchor);
            anchor = new SanguineCrafterAnchorData.Anchor(owner, center);
            data.put(key, anchor);
        }

        if (ownerOnlineOnly() && !isOnline(level.getServer(), owner)) {
            unforce(level, key, anchor);
            return ChunkLoading.OWNER_OFFLINE;
        }
        force(level, key, anchor);
        return ChunkLoading.ACTIVE;
    }

    public static void release(ServerLevel level, BlockPos pos) {
        drop(level, GlobalPos.of(level.dimension(), pos), data(level.getServer()));
    }

    public static boolean isForced(ServerLevel level, BlockPos pos) {
        return FORCED.contains(GlobalPos.of(level.dimension(), pos));
    }

    private static void drop(ServerLevel level, GlobalPos key, SanguineCrafterAnchorData data) {
        SanguineCrafterAnchorData.Anchor anchor = data.anchors().get(key);
        if (anchor == null) return;
        unforce(level, key, anchor);
        data.remove(key);
    }

    private static void force(ServerLevel level, GlobalPos key, SanguineCrafterAnchorData.Anchor anchor) {
        if (FORCED.add(key)) {
            setForced(level, key.pos(), anchor.center(), true);
        }
    }

    private static void unforce(ServerLevel level, GlobalPos key, SanguineCrafterAnchorData.Anchor anchor) {
        FORCED.remove(key);
        setForced(level, key.pos(), anchor.center(), false);
    }

    private static void setForced(ServerLevel level, BlockPos controller, BlockPos center, boolean add) {
        Set<Long> chunks = new HashSet<>();
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dz = -1; dz <= 1; dz += 2) {
                int cx = SectionPos.blockToSectionCoord(center.getX() + dx);
                int cz = SectionPos.blockToSectionCoord(center.getZ() + dz);
                if (chunks.add(((long) cx << 32) | (cz & 0xFFFFFFFFL))) {
                    CONTROLLER.forceChunk(level, controller, cx, cz, add, false);
                }
            }
        }
    }

    private static void onLogin(UUID player, MinecraftServer server) {
        if (server == null || !enabled()) return;
        for (Map.Entry<GlobalPos, SanguineCrafterAnchorData.Anchor> entry : data(server).anchors().entrySet()) {
            if (!entry.getValue().owner().equals(player)) continue;
            ServerLevel level = server.getLevel(entry.getKey().dimension());
            if (level != null) {
                force(level, entry.getKey(), entry.getValue());
            }
        }
    }

    private static void onLogout(UUID player, MinecraftServer server) {
        if (server == null || !ownerOnlineOnly()) return;
        for (Map.Entry<GlobalPos, SanguineCrafterAnchorData.Anchor> entry : data(server).anchors().entrySet()) {
            if (!entry.getValue().owner().equals(player)) continue;
            ServerLevel level = server.getLevel(entry.getKey().dimension());
            if (level != null) {
                unforce(level, entry.getKey(), entry.getValue());
            }
        }
    }

    private static void validate(ServerLevel level, TicketHelper helper) {
        boolean onlineOnly = ownerOnlineOnly();
        SanguineCrafterAnchorData data = data(level.getServer());
        for (BlockPos pos : new ArrayList<>(helper.getBlockTickets().keySet())) {
            GlobalPos key = GlobalPos.of(level.dimension(), pos);
            if (!enabled() || onlineOnly || !data.anchors().containsKey(key)) {
                helper.removeAllTickets(pos);
            } else {
                FORCED.add(key);
            }
        }
    }
}
