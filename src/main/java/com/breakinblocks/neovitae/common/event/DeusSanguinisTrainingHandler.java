package com.breakinblocks.neovitae.common.event;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.ServerConfig;
import com.breakinblocks.neovitae.api.event.AnimaEvent;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SentientStats;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = NeoVitae.MODID)
public final class DeusSanguinisTrainingHandler {

    public static final ResourceKey<SentientUpgrade> DEUS_SANGUINIS =
            ResourceKey.create(NVRegistries.Keys.SENTIENT_UPGRADES, NeoVitae.rl("deus_sanguinis"));

    private static final Map<UUID, Progress> PROGRESS = new ConcurrentHashMap<>();

    private static final class Progress {
        long spent;
        long lastCredit = Long.MIN_VALUE;
    }

    private DeusSanguinisTrainingHandler() {}

    @SubscribeEvent
    public static void onSyphon(AnimaEvent.PostSyphon event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || event.getActualAmount() <= 0) return;
        UUID owner = event.getNetwork().getPlayerId();
        if (owner == null) return;
        ServerPlayer player = server.getPlayerList().getPlayer(owner);
        if (player == null) return;

        ItemStack chest = SentientHelper.getChest(player);
        Optional<Holder.Reference<SentientUpgrade>> holder = upgrade(server);
        if (holder.isEmpty() || !hasUpgrade(chest, holder.get())) return;

        if (!recordSpend(owner, event.getActualAmount(), player.level().getGameTime())) return;
        SentientHelper.applyExp(player, holder.get(), NeoVitae.SERVER_CONFIG.DEUS_SANGUINIS_EXP_PER_CREDIT.get().floatValue());
    }

    public static boolean recordSpend(UUID owner, int amount, long now) {
        ServerConfig config = NeoVitae.SERVER_CONFIG;
        Progress progress = PROGRESS.computeIfAbsent(owner, id -> new Progress());
        progress.spent += amount;
        if (progress.lastCredit != Long.MIN_VALUE && now - progress.lastCredit < config.DEUS_SANGUINIS_COOLDOWN.get()) return false;
        if (progress.spent < config.DEUS_SANGUINIS_MIN_EV.get()) return false;
        progress.spent = 0;
        progress.lastCredit = now;
        return true;
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PROGRESS.remove(event.getEntity().getUUID());
    }

    private static Optional<Holder.Reference<SentientUpgrade>> upgrade(MinecraftServer server) {
        return server.registryAccess().lookupOrThrow(NVRegistries.Keys.SENTIENT_UPGRADES).get(DEUS_SANGUINIS);
    }

    private static boolean hasUpgrade(ItemStack chest, Holder<SentientUpgrade> upgrade) {
        if (chest.isEmpty()) return false;
        return chest.getOrDefault(NVDataComponents.UPGRADES, SentientStats.EMPTY).upgrades().getFloat(upgrade) > 0;
    }
}
