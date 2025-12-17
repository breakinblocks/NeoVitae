package com.breakinblocks.neovitae.common.living;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemLore;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.item.BMItems;
import com.breakinblocks.neovitae.common.tag.BMTags;
import com.breakinblocks.neovitae.compat.curios.CuriosCompat;

import java.util.List;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class LivingEventHandler {

    @SubscribeEvent
    public static void onTotemUse(LivingUseTotemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (player.level().isClientSide) {
            return;
        }

        if (LivingHelper.hasFullSet(player) && event.getHandHolding() == InteractionHand.OFF_HAND) {
            if (LivingHelper.has(player, LivingEffectComponents.CRIPPLED_ARM.get())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        if (LivingHelper.hasFullSet(event.getEntity())) {
            boolean cancel = false;
            if (event.getHand() == InteractionHand.OFF_HAND && LivingHelper.has(event.getEntity(), LivingEffectComponents.CRIPPLED_ARM.get())) {
                cancel = true;
            }
            // Quenched downgrade prevents drinking
            if (event.getItemStack().getUseAnimation() == UseAnim.DRINK && LivingHelper.has(event.getEntity(), LivingEffectComponents.QUENCHED.get())) {
                cancel = true;
            }

            if (cancel) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        if (LivingHelper.hasFullSet(event.getEntity()) && event.getHand() == InteractionHand.OFF_HAND) {
            if (LivingHelper.has(event.getEntity(), LivingEffectComponents.CRIPPLED_ARM.get())) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        DamageSource source = event.getEntity().getLastDamageSource();
        if (source == null) {
            return;
        }
        Entity causer = event.getEntity().getLastDamageSource().getEntity();
        if (causer instanceof Player player) {
            if (LivingHelper.hasFullSet(player)) {
                float changed = LivingHelper.modifyKnockback(player, event.getEntity(), event.getEntity().getLastDamageSource(), event.getStrength());
                event.setStrength(changed);
            }
        }
    }

    @SubscribeEvent
    public static void onExpPickup(PlayerXpEvent.PickupXp event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        if (LivingHelper.hasFullSet(event.getEntity())) {
            int starting = event.getOrb().getValue();
            int ending = LivingHelper.modifyExperience(event.getEntity(), starting);
            event.getOrb().value = ending;
        }
    }

    @SubscribeEvent
    public static void onHeal(LivingHealEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        if (event.getEntity() instanceof Player player) {
            if (LivingHelper.hasFullSet(player)) {
                float changed = LivingHelper.modifyHealing(player, event.getAmount());
                event.setAmount(changed);
            }
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        Entity causer = event.getSource().getEntity();
        LivingEntity victim = event.getEntity();

        if (causer instanceof Player playerCauser) {
            if (LivingHelper.hasFullSet(playerCauser)) {
                float newDamage = LivingHelper.modifyDamageDealt(playerCauser, victim, event.getSource(), event.getNewDamage());
                // Debug: NeoVitae.LOGGER.info("{} -> {}", event.getNewDamage(), newDamage);
                event.setNewDamage(newDamage);
                LivingHelper.reactToDamageDealt(playerCauser, victim, event.getSource(), event.getNewDamage()); // here we want the damage included
            }
        }

        if (victim instanceof Player playerVictim) {
            if (LivingHelper.hasFullSet(playerVictim)) {
                LivingHelper.reactToDamageTaken(playerVictim, event.getSource(), relevantDamage(event)); // here we do not so though etc arent as grindy as they were
                float newDamage = LivingHelper.modifyDamageTaken(playerVictim, event.getSource(), event.getNewDamage());
                event.setNewDamage(newDamage);
            }
        }
    }

    private static float relevantDamage(LivingDamageEvent.Pre event) {
        float taken = event.getNewDamage();
        float reduced = event.getContainer().getReduction(DamageContainer.Reduction.ARMOR); // exclude armour reduction as well. its the thing learning after all
        // Note: Consider excluding enchantment reduction as well for more consistent learning progression
        float ret = taken + reduced;
        return ret;
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (LivingHelper.hasFullSet(event.getPlayer())) {
            LivingHelper.runBlockBroken(event.getPlayer(), event.getState());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        if (LivingHelper.hasFullSet(event.getEntity())) {
            LivingHelper.runTick(event.getEntity());

            // Quenched downgrade - extinguish fire on the player
            if (LivingHelper.has(event.getEntity(), LivingEffectComponents.QUENCHED.get())) {
                if (event.getEntity().isOnFire()) {
                    event.getEntity().clearFire();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!event.getSlot().isArmor()) {
            return;
        }
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack fromStack = event.getFrom();
        ItemStack toStack = event.getTo();
        EquipmentSlot slot = event.getSlot();
        boolean from = fromStack.is(BMTags.Items.LIVING_UPGRADE_SET);
        boolean to = toStack.is(BMTags.Items.LIVING_UPGRADE_SET);

        if (!fromStack.is(BMTags.Items.LIVING_UPGRADE_SET) && !toStack.is(BMTags.Items.LIVING_UPGRADE_SET)) {
            // no upgrades involved, bye
            return;
        }

        ItemStack chestStack = LivingHelper.getChest(player);
        if (chestStack.isEmpty()) {
            return;
        }

        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        // include the armour points
        chestStack.getAttributeModifiers().forEach(EquipmentSlot.CHEST, (holder, modifier) -> builder.add(holder, modifier, EquipmentSlotGroup.CHEST));
        if (LivingHelper.hasFullSet(player)) {
            // add from upgrades if full set
            LivingHelper.getAttributes(chestStack, builder);
        }

        chestStack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());

        // Recalculate Curios living armour socket slots when armor changes
        CuriosCompat.recalculateCuriosSlots(player);
    }

    @SubscribeEvent
    public static void onAttributeNonsense(ItemAttributeModifierEvent event) {
        ItemStack chestStack = event.getItemStack();
        if (chestStack.is(BMTags.Items.LIVING_UPGRADE_SET) && !LivingHelper.isNeverValid(chestStack)) {
            if (chestStack.getOrDefault(BMDataComponents.FULL_SET_MARKER, false)) {
                // add all attributes
            }
        }
    }

    @SubscribeEvent
    public static void entityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        if (event.getEntity() instanceof Projectile projectile) {
            if (projectile.getOwner() instanceof Player player) {
                if (LivingHelper.hasFullSet(player)) {
                    LivingHelper.runProjectile(player, projectile);
                }
            }
        }
    }
}
