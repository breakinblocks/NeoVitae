package com.breakinblocks.neovitae.common.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.entity.BMEntities;
import com.breakinblocks.neovitae.common.item.BMItems;

/**
 * Syringe throwing dagger projectile - applies potion effects on hit.
 */
public class EntityThrowingDaggerSyringe extends AbstractEntityThrowingDagger {

    public EntityThrowingDaggerSyringe(EntityType<? extends EntityThrowingDaggerSyringe> type, Level level) {
        super(type, level);
    }

    public EntityThrowingDaggerSyringe(Level level, LivingEntity thrower, ItemStack stack) {
        super(BMEntities.THROWING_DAGGER_SYRINGE.get(), stack, level, thrower);
    }

    public EntityThrowingDaggerSyringe(Level level, double x, double y, double z, ItemStack stack) {
        super(BMEntities.THROWING_DAGGER_SYRINGE.get(), stack, level, x, y, z);
    }

    @Override
    protected Item getDefaultItem() {
        return BMItems.THROWING_DAGGER_SYRINGE.get();
    }
}
