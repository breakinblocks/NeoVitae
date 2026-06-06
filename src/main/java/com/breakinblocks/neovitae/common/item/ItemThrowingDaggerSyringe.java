// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.entity.projectile.AbstractEntityThrowingDagger;
import com.breakinblocks.neovitae.common.entity.projectile.EntityThrowingDaggerSyringe;

public class ItemThrowingDaggerSyringe extends ItemThrowingDagger {

    public ItemThrowingDaggerSyringe() {
        super();
    }

    @Override
    public AbstractEntityThrowingDagger getDagger(ItemStack stack, Level level, Player player) {
        EntityThrowingDaggerSyringe dagger = new EntityThrowingDaggerSyringe(level, player, stack);
        dagger.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3F, 0.5F);
        dagger.setDamage(8);
        return dagger;
    }
}
