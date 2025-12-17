package com.breakinblocks.neovitae.common.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.entity.BMEntities;
import com.breakinblocks.neovitae.common.item.BMItems;

/**
 * Standard throwing dagger projectile.
 */
public class EntityThrowingDagger extends AbstractEntityThrowingDagger {

    public EntityThrowingDagger(EntityType<? extends EntityThrowingDagger> type, Level level) {
        super(type, level);
    }

    public EntityThrowingDagger(Level level, LivingEntity thrower, ItemStack stack) {
        super(BMEntities.THROWING_DAGGER.get(), stack, level, thrower);
    }

    public EntityThrowingDagger(Level level, double x, double y, double z, ItemStack stack) {
        super(BMEntities.THROWING_DAGGER.get(), stack, level, x, y, z);
    }

    @Override
    protected Item getDefaultItem() {
        return BMItems.THROWING_DAGGER.get();
    }
}
