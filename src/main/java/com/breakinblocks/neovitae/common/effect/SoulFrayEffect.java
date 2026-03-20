package com.breakinblocks.neovitae.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Debuff applied after a ceremonial sacrifice (incense-boosted self-sacrifice).
 * Prevents repeated ceremonial sacrifices to avoid trivially farming massive LP gains.
 */
public class SoulFrayEffect extends MobEffect {

    public static final int DEFAULT_DURATION = 400; // 20 seconds

    public SoulFrayEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public static boolean hasSoulFray(LivingEntity entity) {
        return entity.hasEffect(NVMobEffects.SOUL_FRAY);
    }

    public static boolean canPerformCeremonialSacrifice(Player player) {
        return !hasSoulFray(player);
    }
}
