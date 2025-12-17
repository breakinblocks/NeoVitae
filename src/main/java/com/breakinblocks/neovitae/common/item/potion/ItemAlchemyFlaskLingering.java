package com.breakinblocks.neovitae.common.item.potion;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

/**
 * Lingering Alchemy Flask - Throws a lingering potion effect.
 * Has reduced duration compared to throwable flask but creates an area effect cloud.
 */
public class ItemAlchemyFlaskLingering extends ItemAlchemyFlaskThrowable {

    public ItemAlchemyFlaskLingering() {
        // Calls ItemAlchemyFlask constructor via parent
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (getRemainingUses(stack) <= 0) {
            return InteractionResultHolder.pass(stack);
        }

        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents == null || !contents.hasEffects()) {
            return InteractionResultHolder.pass(stack);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.LINGERING_POTION_THROW, SoundSource.PLAYERS,
                0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));

        if (!level.isClientSide) {
            // Create a thrown potion entity using a lingering potion stack
            ThrownPotion thrownPotion = new ThrownPotion(level, player);

            // Create a temporary lingering potion stack with the same effects
            ItemStack potionStack = new ItemStack(net.minecraft.world.item.Items.LINGERING_POTION);
            potionStack.set(DataComponents.POTION_CONTENTS, contents);
            thrownPotion.setItem(potionStack);

            thrownPotion.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.0F);
            level.addFreshEntity(thrownPotion);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            stack.setDamageValue(stack.getDamageValue() + 1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
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
