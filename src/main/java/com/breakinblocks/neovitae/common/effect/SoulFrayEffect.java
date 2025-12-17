package com.breakinblocks.neovitae.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Soul Fray is a debuff applied after using a ceremonial sacrifice (incense-boosted self-sacrifice).
 * While active, the player cannot perform another ceremonial sacrifice.
 * This prevents players from repeatedly sacrificing large amounts of health for massive LP gains.
 *
 * Duration: 20 seconds (400 ticks) by default
 * Applied when: Player performs ceremonial sacrifice (using incense bonus)
 * Effect: Prevents ceremonial sacrifice while active
 */
public class SoulFrayEffect extends MobEffect {

    public static final int DEFAULT_DURATION = 400; // 20 seconds

    public SoulFrayEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    /**
     * Checks if the given entity has Soul Fray active.
     */
    public static boolean hasSoulFray(LivingEntity entity) {
        return entity.hasEffect(BMMobEffects.SOUL_FRAY);
    }

    /**
     * Checks if the player can perform ceremonial sacrifice.
     * Returns false if Soul Fray is active.
     */
    public static boolean canPerformCeremonialSacrifice(Player player) {
        return !hasSoulFray(player);
    }
}
