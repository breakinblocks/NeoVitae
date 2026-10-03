package com.breakinblocks.neovitae.common.world;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ActiveRituals {

    public enum Status {
        ACTIVE, PAUSED, UNLOADED
    }

    public record LedgerEntry(Identifier ritual, Identifier dimension, BlockPos pos, int refreshCost, int refreshTime, Status status) {

        public void write(FriendlyByteBuf buf) {
            buf.writeIdentifier(ritual);
            buf.writeIdentifier(dimension);
            buf.writeBlockPos(pos);
            buf.writeVarInt(refreshCost);
            buf.writeVarInt(refreshTime);
            buf.writeEnum(status);
        }

        public static LedgerEntry read(FriendlyByteBuf buf) {
            return new LedgerEntry(buf.readIdentifier(), buf.readIdentifier(), buf.readBlockPos(),
                    buf.readVarInt(), buf.readVarInt(), buf.readEnum(Status.class));
        }

        public String coordinates() {
            return pos.getX() + " " + pos.getY() + " " + pos.getZ();
        }

        public Component name() {
            Ritual ritual = RitualRegistry.getRitual(this.ritual);
            return ritual != null ? Component.translatable(ritual.getTranslationKey()) : Component.literal(this.ritual.toString());
        }

        public Component costLine() {
            return Component.translatable("chat.neovitae.ritual_ledger.cost", refreshCost, String.format("%.1f", refreshTime / 20.0));
        }
    }

    private ActiveRituals() {
    }

    private static ActiveRitualData storage(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(ActiveRitualData.TYPE);
    }

    public static void track(ServerLevel level, BlockPos pos, UUID owner, Identifier ritual) {
        if (owner == null || ritual == null) return;
        storage(level.getServer()).put(GlobalPos.of(level.dimension(), pos.immutable()), new ActiveRitualData.Entry(owner, ritual));
    }

    public static void untrack(ServerLevel level, BlockPos pos) {
        storage(level.getServer()).remove(GlobalPos.of(level.dimension(), pos));
    }

    public static List<LedgerEntry> collect(MinecraftServer server, UUID owner) {
        ActiveRitualData data = storage(server);
        List<LedgerEntry> entries = new ArrayList<>();
        List<GlobalPos> stale = new ArrayList<>();

        for (Map.Entry<GlobalPos, ActiveRitualData.Entry> e : data.entries().entrySet()) {
            if (!owner.equals(e.getValue().owner())) continue;
            if (isStale(server, e.getKey(), e.getValue())) {
                stale.add(e.getKey());
                continue;
            }
            Ritual ritual = RitualRegistry.getRitual(e.getValue().ritual());
            entries.add(new LedgerEntry(e.getValue().ritual(), e.getKey().dimension().identifier(), e.getKey().pos(),
                    ritual != null ? ritual.getRefreshCost() : 0, ritual != null ? ritual.getRefreshTime() : 0,
                    statusOf(server, e.getKey())));
        }
        stale.forEach(data::remove);
        return entries;
    }

    public static List<Component> report(MinecraftServer server, UUID owner) {
        List<LedgerEntry> entries = collect(server, owner);
        List<Component> lines = new ArrayList<>();
        if (entries.isEmpty()) {
            lines.add(Component.translatable("chat.neovitae.ritual_ledger.none").withStyle(ChatFormatting.GRAY));
            return lines;
        }
        lines.add(Component.translatable("chat.neovitae.ritual_ledger.header", entries.size()).withStyle(ChatFormatting.DARK_RED));
        for (LedgerEntry entry : entries) {
            lines.add(describe(entry));
        }
        return lines;
    }

    private static boolean isStale(MinecraftServer server, GlobalPos pos, ActiveRitualData.Entry entry) {
        ServerLevel level = server.getLevel(pos.dimension());
        if (level == null) return true;
        if (!level.isLoaded(pos.pos())) return false;
        return !(level.getBlockEntity(pos.pos()) instanceof MasterRitualStoneBlockEntity mrs)
                || !mrs.isActive()
                || !entry.owner().equals(mrs.getOwner())
                || !entry.ritual().equals(mrs.getCurrentRitualId());
    }

    private static Status statusOf(MinecraftServer server, GlobalPos pos) {
        ServerLevel level = server.getLevel(pos.dimension());
        if (level == null || !level.isLoaded(pos.pos())) return Status.UNLOADED;
        if (level.getBlockEntity(pos.pos()) instanceof MasterRitualStoneBlockEntity mrs && mrs.isSuspended()) {
            return Status.PAUSED;
        }
        return Status.ACTIVE;
    }

    private static Component describe(LedgerEntry entry) {
        String coords = entry.coordinates();
        MutableComponent location = Component.literal("[" + coords + "]")
                .withStyle(style -> style.withColor(ChatFormatting.GREEN)
                        .withClickEvent(new ClickEvent.CopyToClipboard(coords))
                        .withHoverEvent(new HoverEvent.ShowText(
                                Component.translatable("chat.neovitae.ritual_ledger.copy"))));

        MutableComponent line = Component.literal("- ").withStyle(ChatFormatting.GRAY)
                .append(Component.empty().append(entry.name()).withStyle(ChatFormatting.GOLD))
                .append(Component.literal(" "))
                .append(location)
                .append(Component.literal(" " + entry.dimension()).withStyle(ChatFormatting.GRAY))
                .append(Component.literal(" "))
                .append(entry.costLine().copy().withStyle(ChatFormatting.DARK_GRAY));

        if (entry.status() == Status.UNLOADED) {
            line.append(Component.literal(" "))
                    .append(Component.translatable("chat.neovitae.ritual_ledger.unloaded").withStyle(ChatFormatting.DARK_GRAY));
        } else if (entry.status() == Status.PAUSED) {
            line.append(Component.literal(" "))
                    .append(Component.translatable("chat.neovitae.ritual_ledger.paused").withStyle(ChatFormatting.YELLOW));
        }
        return line;
    }
}
