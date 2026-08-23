package com.breakinblocks.neovitae.common.world;

import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import com.breakinblocks.neovitae.NeoVitae;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class BoundTreasureLeases {

    private static final int LEASE_TICKS = 1200;

    private static final TicketController CONTROLLER = new TicketController(
            NeoVitae.rl("bound_treasure"),
            (level, helper) -> helper.getEntityTickets().keySet().forEach(helper::removeAllTickets));

    private static final Map<UUID, Lease> LEASES = new HashMap<>();

    private BoundTreasureLeases() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(RegisterTicketControllersEvent.class, event -> event.register(CONTROLLER));
        NeoForge.EVENT_BUS.addListener(ServerTickEvent.Post.class, BoundTreasureLeases::onServerTick);
        NeoForge.EVENT_BUS.addListener(ServerStoppingEvent.class, event -> LEASES.clear());
    }

    public static MenuProvider findMenuProvider(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        MenuProvider provider = state.getMenuProvider(level, pos);
        if (provider != null) {
            return provider;
        }
        return level.getBlockEntity(pos) instanceof MenuProvider be ? be : null;
    }

    public static boolean isContainer(Level level, BlockPos pos) {
        if (findMenuProvider(level, pos) != null) {
            return true;
        }
        return level.getBlockEntity(pos) != null
                && level.getCapability(Capabilities.Item.BLOCK, pos, null) != null;
    }

    public static void open(ServerPlayer player, ServerLevel level, BlockPos pos) {
        AbstractContainerMenu menu = player.containerMenu;
        if (menu == player.inventoryMenu) return;

        MinecraftServer server = level.getServer();

        Lease existing = LEASES.get(player.getUUID());
        Lease lease = new Lease(level.dimension(), pos, chunksToHold(level, pos));
        if (existing != null) {
            if (existing.covers(lease)) {
                lease = existing;
            } else {
                existing.release(server, player.getUUID());
            }
        }

        lease.menu = menu;
        lease.expiresAt = server.getTickCount() + LEASE_TICKS;
        lease.hold(server, player.getUUID());
        LEASES.put(player.getUUID(), lease);
    }

    public static boolean keepOpen(Player player, AbstractContainerMenu menu) {
        Lease lease = LEASES.get(player.getUUID());
        if (lease == null || lease.menu != menu) return false;

        MinecraftServer server = player.level().getServer();
        if (server == null) return false;

        ServerLevel level = server.getLevel(lease.dimension);
        if (level == null || !level.isLoaded(lease.pos)) return false;

        return isContainer(level, lease.pos);
    }

    private static void onServerTick(ServerTickEvent.Post event) {
        if (LEASES.isEmpty()) return;

        MinecraftServer server = event.getServer();
        long now = server.getTickCount();

        Iterator<Map.Entry<UUID, Lease>> it = LEASES.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Lease> entry = it.next();
            Lease lease = entry.getValue();
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());

            if (player != null && lease.menu != null && player.containerMenu == lease.menu) {
                lease.expiresAt = now + LEASE_TICKS;
                continue;
            }

            lease.menu = null;
            if (now >= lease.expiresAt) {
                lease.release(server, entry.getKey());
                it.remove();
            }
        }
    }

    private static List<ChunkPos> chunksToHold(ServerLevel level, BlockPos pos) {
        List<ChunkPos> chunks = new ArrayList<>(2);
        chunks.add(ChunkPos.containing(pos));

        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof ChestBlock && state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
            ChunkPos other = ChunkPos.containing(pos.relative(ChestBlock.getConnectedDirection(state)));
            if (!chunks.contains(other)) chunks.add(other);
        }
        return chunks;
    }

    private static final class Lease {
        private final ResourceKey<Level> dimension;
        private final BlockPos pos;
        private final List<ChunkPos> chunks;
        private AbstractContainerMenu menu;
        private long expiresAt;

        private Lease(ResourceKey<Level> dimension, BlockPos pos, List<ChunkPos> chunks) {
            this.dimension = dimension;
            this.pos = pos;
            this.chunks = chunks;
        }

        private boolean covers(Lease other) {
            return dimension.equals(other.dimension) && pos.equals(other.pos);
        }

        private void hold(MinecraftServer server, UUID owner) {
            ServerLevel level = server.getLevel(dimension);
            if (level == null) return;
            for (ChunkPos chunk : chunks) {
                CONTROLLER.forceChunk(level, owner, chunk.x(), chunk.z(), true, false);
            }
        }

        private void release(MinecraftServer server, UUID owner) {
            ServerLevel level = server.getLevel(dimension);
            if (level == null) return;
            for (ChunkPos chunk : chunks) {
                CONTROLLER.forceChunk(level, owner, chunk.x(), chunk.z(), false, false);
            }
        }
    }
}
