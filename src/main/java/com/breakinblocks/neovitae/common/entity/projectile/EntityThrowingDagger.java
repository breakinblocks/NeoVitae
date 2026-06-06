// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.entity.NVEntities;
import com.breakinblocks.neovitae.common.item.NVItems;

/**
 * Standard throwing dagger projectile.
 */
public class EntityThrowingDagger extends AbstractEntityThrowingDagger {

    public EntityThrowingDagger(EntityType<? extends EntityThrowingDagger> type, Level level) {
        super(type, level);
    }

    public EntityThrowingDagger(Level level, LivingEntity thrower, ItemStack stack) {
        super(NVEntities.THROWING_DAGGER.get(), stack, level, thrower);
    }

    public EntityThrowingDagger(Level level, double x, double y, double z, ItemStack stack) {
        super(NVEntities.THROWING_DAGGER.get(), stack, level, x, y, z);
    }

    @Override
    protected Item getDefaultItem() {
        return NVItems.THROWING_DAGGER.get();
    }
}
