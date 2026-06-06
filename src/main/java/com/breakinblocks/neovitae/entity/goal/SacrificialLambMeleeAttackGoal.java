// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;

/**
 * Melee attack goal for the Sacrificial Lamb effect.
 * The entity approaches targets but doesn't actually attack - it explodes instead.
 */
public class SacrificialLambMeleeAttackGoal extends MeleeAttackGoal {

    public SacrificialLambMeleeAttackGoal(PathfinderMob creature, double speed, boolean useLongMemory) {
        super(creature, speed, useLongMemory);
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy) {
        // Do nothing - the explosion is handled by the effect tick
    }

    @Override
    public boolean canUse() {
        return this.mob.hasEffect(NVMobEffects.SACRIFICIAL_LAMB) && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.hasEffect(NVMobEffects.SACRIFICIAL_LAMB) && super.canContinueToUse();
    }
}
