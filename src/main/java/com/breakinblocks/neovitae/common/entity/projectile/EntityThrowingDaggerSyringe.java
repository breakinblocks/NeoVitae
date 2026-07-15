// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.entity.projectile;

import net.minecraft.world.Containers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import com.breakinblocks.neovitae.common.entity.NVEntities;
import com.breakinblocks.neovitae.common.item.NVItems;

public class EntityThrowingDaggerSyringe extends AbstractEntityThrowingDagger {

    public EntityThrowingDaggerSyringe(EntityType<? extends EntityThrowingDaggerSyringe> type, Level level) {
        super(type, level);
    }

    public EntityThrowingDaggerSyringe(Level level, LivingEntity thrower, ItemStack stack) {
        super(NVEntities.THROWING_DAGGER_SYRINGE.get(), stack, level, thrower);
    }

    public EntityThrowingDaggerSyringe(Level level, double x, double y, double z, ItemStack stack) {
        super(NVEntities.THROWING_DAGGER_SYRINGE.get(), stack, level, x, y, z);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide()) {
            return;
        }
        if (result.getEntity() instanceof LivingEntity living && !living.isAlive()) {
            double maxHealth = living.getMaxHealth();
            int count = (int) (maxHealth / 20.0D)
                    + (this.random.nextDouble() < (maxHealth % 20.0D) / 20.0D ? 1 : 0);
            if (count > 0) {
                Containers.dropItemStack(this.level(), this.getX(), this.getY(), this.getZ(),
                        new ItemStack(NVItems.TABULA_AMPOULE.get(), count));
            }
        }
    }

    @Override
    protected Item getDefaultItem() {
        return NVItems.THROWING_DAGGER_SYRINGE.get();
    }
}
