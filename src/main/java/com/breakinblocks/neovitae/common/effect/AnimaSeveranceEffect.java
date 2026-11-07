package com.breakinblocks.neovitae.common.effect;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Set;
import java.util.UUID;

public class AnimaSeveranceEffect extends MobEffect {

    public static final int DURATION_TICKS = 200;

    private static final ThreadLocal<Boolean> BYPASS_UPKEEP = ThreadLocal.withInitial(() -> false);

    public AnimaSeveranceEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
    }

    public static boolean isSevered(UUID owner) {
        if (owner == null) return false;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return false;
        ServerPlayer player = server.getPlayerList().getPlayer(owner);
        return player != null && player.hasEffect(NVMobEffects.ANIMA_SEVERANCE);
    }

    public static int upkeepMultiplier(UUID owner) {
        return !BYPASS_UPKEEP.get() && isSevered(owner) ? 2 : 1;
    }

    public static void withoutUpkeepPenalty(Runnable action) {
        boolean previous = BYPASS_UPKEEP.get();
        BYPASS_UPKEEP.set(true);
        try {
            action.run();
        } finally {
            BYPASS_UPKEEP.set(previous);
        }
    }
}
