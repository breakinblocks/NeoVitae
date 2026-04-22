package com.breakinblocks.neovitae.common.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.entity.projectile.AbstractEntityThrowingDagger;
import com.breakinblocks.neovitae.common.entity.projectile.EntityThrowingDagger;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * Tipped throwing dagger - an amethyst throwing dagger with potion effects applied.
 * Created via Athanor recipe using lingering alchemy potions.
 */
public class ItemTippedThrowingDagger extends ItemThrowingDagger {

    public ItemTippedThrowingDagger(Item.Properties props) {
        super(props);
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents != null) {
            PotionContents.addPotionTooltip(contents.getAllEffects(), tooltip, 1.0F, context.tickRate());
        }
    }

    // @Override (removed: not an override in 26.1)
    public String getDescriptionId(ItemStack stack) {
        return this.getDescriptionId();
    }
}
