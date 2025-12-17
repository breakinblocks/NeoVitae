package com.breakinblocks.neovitae.anointment;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.AnointmentHolder;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;

/**
 * Handles anointment effects during gameplay events.
 */
@EventBusSubscriber(modid = NeoVitae.MODID)
public class AnointmentEventHandler {

    /**
     * Handle anointment damage bonuses on hit (before armor calculations)
     */
    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        ItemStack heldStack = player.getMainHandItem();
        AnointmentHolder holder = heldStack.get(BMDataComponents.ANOINTMENT_HOLDER.get());

        if (holder == null || holder.isEmpty()) {
            return;
        }

        LivingEntity attacked = event.getEntity();
        double additionalDamage = 0;

        // Calculate additional damage from anointments
        for (AnointmentHolder.AnointmentEntry entry : holder.anointments()) {
            Anointment anoint = AnointmentRegistrar.get(entry.key());
            if (anoint.getDamageProvider() != null) {
                additionalDamage += anoint.getDamageProvider().getAdditionalDamage(
                        player, heldStack, event.getAmount(), holder, attacked, anoint, entry.level());
            }
        }

        if (additionalDamage > 0) {
            event.setAmount((float) (event.getAmount() + additionalDamage));
        }
    }

    /**
     * Handle anointment consumption after damage is dealt
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        ItemStack heldStack = player.getMainHandItem();
        AnointmentHolder holder = heldStack.get(BMDataComponents.ANOINTMENT_HOLDER.get());

        if (holder == null || holder.isEmpty()) {
            return;
        }

        int oldSize = holder.anointments().size();
        AnointmentHolder newHolder = holder.consumeOnAttack();

        // Check for weapon repair anointment
        int repairLevel = holder.getAnointmentLevel(AnointmentRegistrar.WEAPON_REPAIR);
        if (repairLevel > 0 && heldStack.isDamageableItem() && heldStack.isDamaged()) {
            double expBonus = AnointmentRegistrar.WEAPON_REPAIR.getBonusValue("exp", repairLevel).doubleValue();
            double repairRatio = heldStack.getXpRepairRatio();
            double durabilityBonus = Math.min(expBonus / repairRatio, heldStack.getDamageValue());

            int durabilityAdded = (int) durabilityBonus + (durabilityBonus % 1 > player.level().getRandom().nextDouble() ? 1 : 0);
            if (durabilityAdded > 0) {
                heldStack.setDamageValue(Math.max(0, heldStack.getDamageValue() - durabilityAdded));
            }

            newHolder = newHolder.consumeAnointment(AnointmentRegistrar.WEAPON_REPAIR.getKey());
        }

        // Update the item if anointments changed
        if (newHolder.anointments().size() != oldSize || !newHolder.equals(holder)) {
            if (newHolder.isEmpty()) {
                heldStack.remove(BMDataComponents.ANOINTMENT_HOLDER.get());
            } else {
                heldStack.set(BMDataComponents.ANOINTMENT_HOLDER.get(), newHolder);
            }

            // Play effect when anointment expires
            if (newHolder.anointments().size() < oldSize) {
                playAnointmentExpiredEffect(player);
            }
        }
    }

    /**
     * Handle harvest anointments (silk touch, fortune, smelting, voiding)
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        ItemStack heldStack = player.getMainHandItem();
        AnointmentHolder holder = heldStack.get(BMDataComponents.ANOINTMENT_HOLDER.get());

        if (holder == null || holder.isEmpty()) {
            return;
        }

        int oldSize = holder.anointments().size();
        boolean hasChanges = false;
        AnointmentHolder newHolder = holder;

        // Handle hidden knowledge XP bonus - handled in loot modifier instead
        int hiddenLevel = holder.getAnointmentLevel(AnointmentRegistrar.HIDDEN_KNOWLEDGE);
        if (hiddenLevel > 0) {
            newHolder = newHolder.consumeAnointment(AnointmentRegistrar.HIDDEN_KNOWLEDGE.getKey());
            hasChanges = true;
        }

        // Handle weapon repair
        int repairLevel = holder.getAnointmentLevel(AnointmentRegistrar.WEAPON_REPAIR);
        if (repairLevel > 0 && heldStack.isDamageableItem() && heldStack.isDamaged()) {
            double expBonus = AnointmentRegistrar.WEAPON_REPAIR.getBonusValue("exp", repairLevel).doubleValue();
            double repairRatio = heldStack.getXpRepairRatio();
            double durabilityBonus = Math.min(expBonus / repairRatio, heldStack.getDamageValue());

            int durabilityAdded = (int) durabilityBonus + (durabilityBonus % 1 > player.level().getRandom().nextDouble() ? 1 : 0);
            if (durabilityAdded > 0) {
                heldStack.setDamageValue(Math.max(0, heldStack.getDamageValue() - durabilityAdded));
            }

            newHolder = newHolder.consumeAnointment(AnointmentRegistrar.WEAPON_REPAIR.getKey());
            hasChanges = true;
        }

        // Consume harvest anointments
        if (holder.getAnointmentLevel(AnointmentRegistrar.SILK_TOUCH) > 0 ||
                holder.getAnointmentLevel(AnointmentRegistrar.FORTUNE) > 0 ||
                holder.getAnointmentLevel(AnointmentRegistrar.SMELTING) > 0 ||
                holder.getAnointmentLevel(AnointmentRegistrar.VOIDING) > 0) {
            newHolder = newHolder.consumeOnHarvest();
            hasChanges = true;
        }

        // Update the item if anointments changed
        if (hasChanges) {
            if (newHolder.isEmpty()) {
                heldStack.remove(BMDataComponents.ANOINTMENT_HOLDER.get());
            } else {
                heldStack.set(BMDataComponents.ANOINTMENT_HOLDER.get(), newHolder);
            }

            // Play effect when anointment expires
            if (newHolder.anointments().size() < oldSize) {
                playAnointmentExpiredEffect(player);
            }
        }
    }

    /**
     * Handle anointment enchantment level modifications.
     * Makes silk touch and fortune anointments behave like actual enchantments.
     */
    @SubscribeEvent
    public static void onGetEnchantmentLevel(GetEnchantmentLevelEvent event) {
        ItemStack stack = event.getStack();
        AnointmentHolder holder = stack.get(BMDataComponents.ANOINTMENT_HOLDER.get());

        if (holder == null || holder.isEmpty()) {
            return;
        }

        // Handle Silk Touch anointment
        if (event.isTargetting(Enchantments.SILK_TOUCH)) {
            int currentLevel = event.getEnchantments().getLevel(event.getTargetEnchant());
            // Only apply if tool doesn't already have silk touch
            if (currentLevel <= 0 && holder.getAnointmentLevel(AnointmentRegistrar.SILK_TOUCH) > 0) {
                event.getEnchantments().set(event.getTargetEnchant(), 1);
            }
            return;
        }

        // Handle Fortune anointment
        if (event.isTargetting(Enchantments.FORTUNE)) {
            int fortuneLevel = holder.getAnointmentLevel(AnointmentRegistrar.FORTUNE);
            if (fortuneLevel > 0) {
                int currentLevel = event.getEnchantments().getLevel(event.getTargetEnchant());
                // Add anointment fortune to existing enchantment level
                event.getEnchantments().set(event.getTargetEnchant(), currentLevel + fortuneLevel);
            }
        }

        // Handle Looting anointment
        if (event.isTargetting(Enchantments.LOOTING)) {
            int lootingLevel = holder.getAnointmentLevel(AnointmentRegistrar.LOOTING);
            if (lootingLevel > 0) {
                int currentLevel = event.getEnchantments().getLevel(event.getTargetEnchant());
                event.getEnchantments().set(event.getTargetEnchant(), currentLevel + lootingLevel);
            }
        }
    }

    /**
     * Play visual and audio effect when an anointment expires
     */
    private static void playAnointmentExpiredEffect(Player player) {
        player.level().playSound(null, player.blockPosition(), SoundEvents.SPLASH_POTION_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (player.level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.LARGE_SMOKE, player.getX(), player.getY() + 1, player.getZ(), 16, 0.3, 0, 0.3, 0);
        }
    }

    /**
     * Add anointment information to item tooltips
     */
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        AnointmentHolder holder = stack.get(BMDataComponents.ANOINTMENT_HOLDER.get());

        if (holder == null || holder.isEmpty()) {
            return;
        }

        boolean showDetails = Screen.hasShiftDown();

        for (AnointmentHolder.AnointmentEntry entry : holder.anointments()) {
            Anointment anoint = AnointmentRegistrar.get(entry.key());

            if (showDetails) {
                // Show detailed info with remaining uses
                event.getToolTip().add(Component.translatable(anoint.getTranslationKey())
                        .append(" ")
                        .append(Component.translatable("enchantment.level." + entry.level()))
                        .append(Component.literal(" (" + entry.remainingUses() + "/" + entry.maxDamage() + ")"))
                        .withStyle(ChatFormatting.DARK_PURPLE));
            } else {
                // Show basic info
                event.getToolTip().add(Component.translatable(anoint.getTranslationKey())
                        .append(" ")
                        .append(Component.translatable("enchantment.level." + entry.level()))
                        .withStyle(ChatFormatting.DARK_PURPLE));
            }
        }

        if (!showDetails && !holder.isEmpty()) {
            event.getToolTip().add(Component.translatable("tooltip.neovitae.anointment.shift_for_details")
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
