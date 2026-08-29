package com.breakinblocks.neovitae.compat.apotheosis;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ApotheosisCompat {

    private static final Map<UUID, String> LAST_KNOWN_TIER = new ConcurrentHashMap<>();

    private static boolean available;

    private ApotheosisCompat() {
    }

    public static void init() {
        available = ModList.get().isLoaded("apotheosis");
        if (available) {
            NeoVitae.LOGGER.info("Apotheosis found, ritual kills will use the ritual owner's world tier");
        }
    }

    public static void rememberWorldTier(ServerPlayer player) {
        if (!available) {
            return;
        }
        try {
            String tier = ApotheosisHooks.readWorldTier(player);
            if (tier != null) {
                LAST_KNOWN_TIER.put(player.getUUID(), tier);
            }
        } catch (Throwable t) {
            disable(t);
        }
    }

    public static void captureWorldTier(MasterRitualStoneBlockEntity master, ServerPlayer player) {
        if (!available) {
            return;
        }
        try {
            String tier = ApotheosisHooks.readWorldTier(player);
            if (tier != null) {
                LAST_KNOWN_TIER.put(player.getUUID(), tier);
                master.setOwnerWorldTier(tier);
            }
        } catch (Throwable t) {
            disable(t);
        }
    }

    public static void applyOwnerWorldTier(FakePlayer fakePlayer, ServerLevel level, UUID owner, BlockPos masterPos) {
        if (!available || owner == null) {
            return;
        }
        try {
            MasterRitualStoneBlockEntity master =
                    level.getBlockEntity(masterPos) instanceof MasterRitualStoneBlockEntity mrs ? mrs : null;

            String tier = null;
            ServerPlayer online = level.getServer().getPlayerList().getPlayer(owner);
            if (online != null) {
                tier = ApotheosisHooks.readWorldTier(online);
                if (tier != null) {
                    LAST_KNOWN_TIER.put(owner, tier);
                    if (master != null) {
                        master.setOwnerWorldTier(tier);
                    }
                }
            }
            if (tier == null) {
                tier = LAST_KNOWN_TIER.get(owner);
            }
            if (tier == null && master != null && !master.getOwnerWorldTier().isEmpty()) {
                tier = master.getOwnerWorldTier();
            }
            if (tier != null) {
                ApotheosisHooks.writeWorldTier(fakePlayer, tier);
            }
        } catch (Throwable t) {
            disable(t);
        }
    }

    private static void disable(Throwable t) {
        available = false;
        NeoVitae.LOGGER.warn("Apotheosis world tier lookup failed, ritual kills will use the default tier from now on", t);
    }
}
