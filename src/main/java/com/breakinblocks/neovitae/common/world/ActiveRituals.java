package com.breakinblocks.neovitae.common.world;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
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

    public static List<Component> report(MinecraftServer server, UUID owner) {
        ActiveRitualData data = storage(server);
        List<Map.Entry<GlobalPos, ActiveRitualData.Entry>> owned = new ArrayList<>();
        List<GlobalPos> stale = new ArrayList<>();

        for (Map.Entry<GlobalPos, ActiveRitualData.Entry> e : data.entries().entrySet()) {
            if (!owner.equals(e.getValue().owner())) continue;
            if (isStale(server, e.getKey(), e.getValue())) {
                stale.add(e.getKey());
            } else {
                owned.add(e);
            }
        }
        stale.forEach(data::remove);

        List<Component> lines = new ArrayList<>();
        if (owned.isEmpty()) {
            lines.add(Component.translatable("chat.neovitae.ritual_ledger.none").withStyle(ChatFormatting.GRAY));
            return lines;
        }

        lines.add(Component.translatable("chat.neovitae.ritual_ledger.header", owned.size()).withStyle(ChatFormatting.DARK_RED));
        for (Map.Entry<GlobalPos, ActiveRitualData.Entry> e : owned) {
            lines.add(describe(server, e.getKey(), e.getValue()));
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

    private static Component describe(MinecraftServer server, GlobalPos pos, ActiveRitualData.Entry entry) {
        Ritual ritual = RitualRegistry.getRitual(entry.ritual());
        Component name = ritual != null
                ? Component.translatable(ritual.getTranslationKey())
                : Component.literal(entry.ritual().toString());

        BlockPos p = pos.pos();
        String coords = p.getX() + " " + p.getY() + " " + p.getZ();
        MutableComponent location = Component.literal("[" + coords + "]")
                .withStyle(style -> style.withColor(ChatFormatting.GREEN)
                        .withClickEvent(new ClickEvent.CopyToClipboard(coords))
                        .withHoverEvent(new HoverEvent.ShowText(
                                Component.translatable("chat.neovitae.ritual_ledger.copy"))));

        MutableComponent line = Component.literal("- ").withStyle(ChatFormatting.GRAY)
                .append(Component.empty().append(name).withStyle(ChatFormatting.GOLD))
                .append(Component.literal(" "))
                .append(location)
                .append(Component.literal(" " + pos.dimension().identifier()).withStyle(ChatFormatting.GRAY));

        if (ritual != null) {
            String seconds = String.format("%.1f", ritual.getRefreshTime() / 20.0);
            line.append(Component.literal(" "))
                    .append(Component.translatable("chat.neovitae.ritual_ledger.cost", ritual.getRefreshCost(), seconds)
                            .withStyle(ChatFormatting.DARK_GRAY));
        }

        ServerLevel level = server.getLevel(pos.dimension());
        if (level == null || !level.isLoaded(p)) {
            line.append(Component.literal(" "))
                    .append(Component.translatable("chat.neovitae.ritual_ledger.unloaded").withStyle(ChatFormatting.DARK_GRAY));
        } else if (level.getBlockEntity(p) instanceof MasterRitualStoneBlockEntity mrs && mrs.isSuspended()) {
            line.append(Component.literal(" "))
                    .append(Component.translatable("chat.neovitae.ritual_ledger.paused").withStyle(ChatFormatting.YELLOW));
        }
        return line;
    }
}
