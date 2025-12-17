package com.breakinblocks.neovitae.common.item.potion;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import com.breakinblocks.neovitae.common.datacomponent.FlaskEffects;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Alchemy Flask - A reusable potion container that can hold custom potion effects.
 * Uses durability to track remaining uses.
 *
 * The flask uses FlaskEffects data component to store effect data with duration modifiers,
 * and syncs to PotionContents for vanilla compatibility (tooltips, colors).
 */
public class ItemAlchemyFlask extends Item {

    public static final int MAX_USES = 8;

    public ItemAlchemyFlask() {
        super(new Item.Properties().stacksTo(1).durability(MAX_USES));
    }

    public ItemAlchemyFlask(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae.arctool.uses", getRemainingUses(stack))
                .withStyle(ChatFormatting.GOLD));

        // Add potion effect tooltips from PotionContents (synced from FlaskEffects)
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents != null) {
            contents.addPotionTooltip(tooltip::add, 1.0F, context.tickRate());
        }
    }

    public int getRemainingUses(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamageValue();
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);

        if (getRemainingUses(heldStack) <= 0) {
            return InteractionResultHolder.pass(heldStack);
        }

        // Check if flask has any effects (check FlaskEffects first, then PotionContents)
        if (!hasFlaskEffects(heldStack) && !hasEffects(heldStack)) {
            return InteractionResultHolder.pass(heldStack);
        }

        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        Player player = entityLiving instanceof Player ? (Player) entityLiving : null;

        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
        }

        if (!level.isClientSide) {
            // Apply effects from FlaskEffects if present, otherwise from PotionContents
            FlaskEffects flaskEffects = getFlaskEffects(stack);
            if (!flaskEffects.isEmpty()) {
                for (MobEffectInstance effectInstance : flaskEffects.toEffectInstances(false, true)) {
                    if (effectInstance.getEffect().value().isInstantenous()) {
                        effectInstance.getEffect().value().applyInstantenousEffect(
                                player, player, entityLiving, effectInstance.getAmplifier(), 1.0D);
                    } else {
                        entityLiving.addEffect(new MobEffectInstance(effectInstance));
                    }
                }
            } else {
                // Fallback to PotionContents for backwards compatibility
                PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
                if (contents != null) {
                    for (MobEffectInstance effectInstance : contents.getAllEffects()) {
                        if (effectInstance.getEffect().value().isInstantenous()) {
                            effectInstance.getEffect().value().applyInstantenousEffect(
                                    player, player, entityLiving, effectInstance.getAmplifier(), 1.0D);
                        } else {
                            entityLiving.addEffect(new MobEffectInstance(effectInstance));
                        }
                    }
                }
            }
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.setDamageValue(stack.getDamageValue() + 1);
            }
        }

        return stack;
    }

    // ==================== FlaskEffects methods ====================

    /**
     * Get the FlaskEffects from this flask.
     */
    public static FlaskEffects getFlaskEffects(ItemStack stack) {
        return stack.getOrDefault(BMDataComponents.FLASK_EFFECTS.get(), FlaskEffects.EMPTY);
    }

    /**
     * Set the FlaskEffects on this flask and sync to PotionContents for display.
     */
    public static void setFlaskEffects(ItemStack stack, FlaskEffects effects) {
        stack.set(BMDataComponents.FLASK_EFFECTS.get(), effects);
        resyncPotionContents(stack);
    }

    /**
     * Check if the flask has FlaskEffects.
     */
    public static boolean hasFlaskEffects(ItemStack stack) {
        FlaskEffects effects = stack.get(BMDataComponents.FLASK_EFFECTS.get());
        return effects != null && !effects.isEmpty();
    }

    /**
     * Sync FlaskEffects to PotionContents for vanilla tooltip/color display.
     */
    public static void resyncPotionContents(ItemStack stack) {
        FlaskEffects flaskEffects = getFlaskEffects(stack);
        if (flaskEffects.isEmpty()) {
            stack.remove(DataComponents.POTION_CONTENTS);
            return;
        }

        List<MobEffectInstance> effectList = flaskEffects.toEffectInstances(false, true);
        PotionContents contents = new PotionContents(Optional.empty(), Optional.empty(), effectList);
        stack.set(DataComponents.POTION_CONTENTS, contents);
    }

    /**
     * Get the list of EffectHolders from this flask.
     */
    public static List<EffectHolder> getEffectHolders(ItemStack stack) {
        return getFlaskEffects(stack).toMutableList();
    }

    /**
     * Set the list of EffectHolders on this flask.
     */
    public static void setEffectHolders(ItemStack stack, List<EffectHolder> holders) {
        setFlaskEffects(stack, new FlaskEffects(holders));
    }

    // ==================== Legacy PotionContents methods ====================

    /**
     * Set the potion contents of this flask (legacy method for compatibility).
     */
    public static ItemStack setEffects(ItemStack stack, Iterable<MobEffectInstance> effects) {
        List<MobEffectInstance> effectList = new ArrayList<>();
        effects.forEach(effectList::add);
        PotionContents contents = new PotionContents(Optional.empty(), Optional.empty(), effectList);
        stack.set(DataComponents.POTION_CONTENTS, contents);
        return stack;
    }

    /**
     * Get the potion contents of this flask.
     */
    public static PotionContents getContents(ItemStack stack) {
        return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
    }

    /**
     * Check if the flask has any effects (from PotionContents).
     */
    public static boolean hasEffects(ItemStack stack) {
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        return contents != null && contents.hasEffects();
    }
}
