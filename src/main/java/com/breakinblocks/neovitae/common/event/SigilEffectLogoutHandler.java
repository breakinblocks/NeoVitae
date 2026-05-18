package com.breakinblocks.neovitae.common.event;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.breakinblocks.neovitae.api.sigil.SigilType;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Optional;
import java.util.UUID;

/**
 * Dispatches the {@link ISigilEffect#onPlayerLogout(UUID, MinecraftServer)} hook
 * to every registered {@link SigilType} when a player logs out. Effects that
 * hold per-player static state can override that hook to clean up.
 */
@EventBusSubscriber(modid = NeoVitae.MODID)
public final class SigilEffectLogoutHandler {

    private SigilEffectLogoutHandler() {}

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        MinecraftServer server = serverPlayer.level().getServer();
        if (server == null) {
            return;
        }
        UUID uuid = player.getUUID();
        Optional<Registry<SigilType>> registry =
                server.registryAccess().lookup(SigilTypeRegistry.SIGIL_TYPE_KEY);
        if (registry.isEmpty()) {
            return;
        }
        for (SigilType type : registry.get()) {
            type.effect().ifPresent(effect -> effect.onPlayerLogout(uuid, server));
        }
    }
}
