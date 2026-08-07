package com.breakinblocks.neovitae.anointment;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.AnointmentHolder;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.event.LexVitaeAoeHandler;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.util.helper.KeyboardHelper;

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
        AnointmentHolder holder = heldStack.get(NVDataComponents.ANOINTMENT_HOLDER.get());

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
        AnointmentHolder holder = heldStack.get(NVDataComponents.ANOINTMENT_HOLDER.get());

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
                heldStack.remove(NVDataComponents.ANOINTMENT_HOLDER.get());
            } else {
                heldStack.set(NVDataComponents.ANOINTMENT_HOLDER.get(), newHolder);
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
    public static void onBlockBreak(BreakBlockEvent event) {
        if (LexVitaeAoeHandler.isAoeBreaking()) {
            return;
        }

        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        ItemStack heldStack = player.getMainHandItem();
        AnointmentHolder holder = heldStack.get(NVDataComponents.ANOINTMENT_HOLDER.get());

        if (holder == null || holder.isEmpty()) {
            return;
        }

        if (heldStack.is(NVTags.Items.CHARGES)) {
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
                heldStack.remove(NVDataComponents.ANOINTMENT_HOLDER.get());
            } else {
                heldStack.set(NVDataComponents.ANOINTMENT_HOLDER.get(), newHolder);
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
        if (!(event.getStack() instanceof ItemStack stack)) return;
        AnointmentHolder holder = stack.get(NVDataComponents.ANOINTMENT_HOLDER.get());

        if (holder == null || holder.isEmpty()) {
            return;
        }

        // Handle Silk Touch anointment
        if (event.isTargetting(Enchantments.SILK_TOUCH) && holder.getAnointmentLevel(AnointmentRegistrar.SILK_TOUCH) > 0) {
            event.getHolder(Enchantments.SILK_TOUCH).ifPresent(ench -> {
                // Only apply if tool doesn't already have silk touch
                if (event.getEnchantments().getLevel(ench) <= 0) {
                    event.getEnchantments().set(ench, 1);
                }
            });
        }

        // Handle Fortune anointment
        if (event.isTargetting(Enchantments.FORTUNE)) {
            int fortuneLevel = holder.getAnointmentLevel(AnointmentRegistrar.FORTUNE);
            if (fortuneLevel > 0) {
                event.getHolder(Enchantments.FORTUNE).ifPresent(ench ->
                        event.getEnchantments().set(ench, event.getEnchantments().getLevel(ench) + fortuneLevel));
            }
        }

        // Handle Looting anointment
        if (event.isTargetting(Enchantments.LOOTING)) {
            int lootingLevel = holder.getAnointmentLevel(AnointmentRegistrar.LOOTING);
            if (lootingLevel > 0) {
                event.getHolder(Enchantments.LOOTING).ifPresent(ench ->
                        event.getEnchantments().set(ench, event.getEnchantments().getLevel(ench) + lootingLevel));
            }
        }
    }

    /**
     * Quick-draw anointment: reduce bow/crossbow draw time during use.
     */
    @SubscribeEvent
    public static void onItemUseTick(LivingEntityUseItemEvent.Tick event) {
        ItemStack stack = event.getItem();
        if (!(stack.getItem() instanceof BowItem) && !(stack.getItem() instanceof CrossbowItem)) {
            return;
        }

        AnointmentHolder holder = stack.get(NVDataComponents.ANOINTMENT_HOLDER.get());
        if (holder == null || holder.isEmpty()) {
            return;
        }

        int quickDrawLevel = holder.getAnointmentLevel(AnointmentRegistrar.QUICK_DRAW);
        if (quickDrawLevel > 0) {
            double speedBonus = AnointmentRegistrar.QUICK_DRAW.getBonusValue("speed", quickDrawLevel).doubleValue();
            // Accumulate fractional tick reductions
            if (speedBonus >= 1) {
                event.setDuration(event.getDuration() - (int) speedBonus);
            } else if (event.getEntity().level().getRandom().nextDouble() < speedBonus) {
                event.setDuration(event.getDuration() - 1);
            }
        }
    }

    /**
     * Consume anointments when item use finishes (bow shot, crossbow loaded, etc.)
     */
    @SubscribeEvent
    public static void onItemUseStop(LivingEntityUseItemEvent.Stop event) {
        ItemStack stack = event.getItem();
        AnointmentHolder holder = stack.get(NVDataComponents.ANOINTMENT_HOLDER.get());

        if (holder == null || holder.isEmpty()) {
            return;
        }

        int oldSize = holder.anointments().size();
        AnointmentHolder newHolder = holder.consumeOnUseFinish();

        if (!newHolder.equals(holder)) {
            if (newHolder.isEmpty()) {
                stack.remove(NVDataComponents.ANOINTMENT_HOLDER.get());
            } else {
                stack.set(NVDataComponents.ANOINTMENT_HOLDER.get(), newHolder);
            }

            if (newHolder.anointments().size() < oldSize && event.getEntity() instanceof Player player) {
                playAnointmentExpiredEffect(player);
            }
        }
    }

    /**
     * Bow power and bow velocity anointments: modify arrows when they spawn.
     */
    @SubscribeEvent
    public static void onArrowSpawn(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof AbstractArrow arrow)) {
            return;
        }
        if (entity.tickCount > 0) {
            return;
        }

        Entity shooter = arrow.getOwner();
        if (!(shooter instanceof Player player)) {
            return;
        }

        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack heldStack = player.getItemInHand(hand);
            AnointmentHolder holder = heldStack.get(NVDataComponents.ANOINTMENT_HOLDER.get());
            if (holder == null || holder.isEmpty()) {
                continue;
            }

            int powerLevel = holder.getAnointmentLevel(AnointmentRegistrar.BOW_POWER);
            if (powerLevel > 0) {
                double damageMultiplier = AnointmentRegistrar.BOW_POWER.getBonusValue("damage", powerLevel).doubleValue();
                arrow.setBaseDamage(2.0 + damageMultiplier);
            }

            int velocityLevel = holder.getAnointmentLevel(AnointmentRegistrar.BOW_VELOCITY);
            if (velocityLevel > 0) {
                double multiplier = AnointmentRegistrar.BOW_VELOCITY.getBonusValue("velocity", velocityLevel).doubleValue();
                Vec3 motion = arrow.getDeltaMovement();
                arrow.setDeltaMovement(motion.scale(1 + multiplier));
                arrow.setBaseDamage(2.0 / (1 + multiplier));
            }

            break; // Only process the first matching hand
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
        AnointmentHolder holder = stack.get(NVDataComponents.ANOINTMENT_HOLDER.get());

        if (holder == null || holder.isEmpty()) {
            return;
        }

        boolean showDetails = KeyboardHelper.isShiftDown();

        for (AnointmentHolder.AnointmentEntry entry : holder.anointments()) {
            Anointment anoint = AnointmentRegistrar.get(entry.key());

            if (showDetails) {
                // Show detailed info with remaining uses
                event.getToolTip().add(Component.translatable(anoint.getTranslationKey())
                        .append(" ")
                        .append(Component.translatable("enchantment.level." + entry.level()))
                        .append(Component.translatable("tooltip.neovitae.anointment.uses", entry.remainingUses(), entry.maxDamage()))
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
