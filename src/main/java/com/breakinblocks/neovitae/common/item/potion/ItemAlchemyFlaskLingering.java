// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item.potion;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion;
import net.minecraft.world.item.Items;

/**
 * Lingering Alchemy Flask - Throws a lingering potion effect.
 * Has reduced duration compared to throwable flask but creates an area effect cloud.
 */
public class ItemAlchemyFlaskLingering extends ItemAlchemyFlaskThrowable {

    public ItemAlchemyFlaskLingering(Item.Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (getRemainingUses(stack) <= 0) {
            return InteractionResult.PASS;
        }

        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents == null || !contents.hasEffects()) {
            return InteractionResult.PASS;
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.LINGERING_POTION_THROW, SoundSource.PLAYERS,
                0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!level.isClientSide()) {
            AbstractThrownPotion thrownPotion = new ThrownLingeringPotion(level, player, ItemStack.EMPTY);
            ItemStack potionStack = new ItemStack(Items.LINGERING_POTION);
            potionStack.set(DataComponents.POTION_CONTENTS, contents);
            thrownPotion.setItem(potionStack);

            thrownPotion.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.0F);
            level.addFreshEntity(thrownPotion);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            stack.setDamageValue(stack.getDamageValue() + 1);
        }

        return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
    }

    /**
     * Get the duration modifier for this flask type.
     * Lingering flasks have 25% duration like vanilla lingering potions.
     */
    @Override
    public double getDurationModifier() {
        return 0.25;
    }
}
