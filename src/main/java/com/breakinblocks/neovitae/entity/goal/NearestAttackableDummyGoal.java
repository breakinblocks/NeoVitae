// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;

/**
 * A dummy goal used by the Passivity effect to replace hostile targeting.
 * This goal does nothing when started and only continues while the effect is active.
 */
public class NearestAttackableDummyGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

    public NearestAttackableDummyGoal(Mob goalOwner, Class<T> targetClass, boolean checkSight) {
        super(goalOwner, targetClass, checkSight);
    }

    @Override
    public void start() {
        // Do nothing - this is a dummy goal that prevents attacks
    }

    @Override
    public boolean canUse() {
        return this.mob.hasEffect(NVMobEffects.PASSIVITY);
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.hasEffect(NVMobEffects.PASSIVITY);
    }
}
