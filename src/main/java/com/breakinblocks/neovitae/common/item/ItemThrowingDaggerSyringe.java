package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.entity.projectile.AbstractEntityThrowingDagger;
import com.breakinblocks.neovitae.common.entity.projectile.EntityThrowingDaggerSyringe;

/**
 * Syringe throwing dagger - applies potion effects on hit.
 */
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
