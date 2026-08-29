package com.breakinblocks.neovitae.compat.apotheosis;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.tiers.WorldTier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.util.FakePlayer;

final class ApotheosisHooks {

    private ApotheosisHooks() {
    }

    static Object readWorldTier(ServerPlayer player) {
        return WorldTier.getTier(player);
    }

    static void writeWorldTier(FakePlayer fakePlayer, Object tier) {
        if (tier instanceof WorldTier worldTier) {
            fakePlayer.setData(Apoth.Attachments.WORLD_TIER, worldTier);
        }
    }
}
