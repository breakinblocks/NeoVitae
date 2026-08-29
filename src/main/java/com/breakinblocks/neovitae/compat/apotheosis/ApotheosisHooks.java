package com.breakinblocks.neovitae.compat.apotheosis;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.tiers.WorldTier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.util.FakePlayer;

final class ApotheosisHooks {

    private ApotheosisHooks() {
    }

    static String readWorldTier(ServerPlayer player) {
        WorldTier tier = WorldTier.getTier(player);
        return tier == null ? null : tier.getSerializedName();
    }

    static void writeWorldTier(FakePlayer fakePlayer, String tier) {
        for (WorldTier candidate : WorldTier.values()) {
            if (candidate.getSerializedName().equals(tier)) {
                fakePlayer.setData(Apoth.Attachments.WORLD_TIER, candidate);
                return;
            }
        }
    }
}
