package com.breakinblocks.neovitae.common.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.entity.projectile.AbstractEntityThrowingDagger;
import com.breakinblocks.neovitae.common.entity.projectile.EntityThrowingDagger;

import java.util.List;

/**
 * Tipped throwing dagger - an amethyst throwing dagger with potion effects applied.
 * Created via ARC recipe using lingering alchemy potions.
 */
public class ItemTippedThrowingDagger extends ItemThrowingDagger {

    public ItemTippedThrowingDagger() {
        super();
    }

    @Override
    public AbstractEntityThrowingDagger getDagger(ItemStack stack, Level level, Player player) {
        EntityThrowingDagger dagger = new EntityThrowingDagger(level, player, stack);
        dagger.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3F, 0.5F);
        dagger.setDamage(10);
        dagger.setEffectsFromItem(stack);
        return dagger;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        // Add potion effect tooltips
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents != null) {
            contents.addPotionTooltip(tooltip::add, 1.0F, context.tickRate());
        }
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return this.getDescriptionId();
    }
}
