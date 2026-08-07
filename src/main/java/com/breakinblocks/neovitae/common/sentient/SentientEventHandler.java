package com.breakinblocks.neovitae.common.sentient;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.event.LexVitaeAoeHandler;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.NVMaterialsAndTiers;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.compat.curios.CuriosCompat;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class SentientEventHandler {

    @SubscribeEvent
    public static void onTotemUse(LivingUseTotemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (player.level().isClientSide()) {
            return;
        }

        if (SentientHelper.hasFullSet(player) && event.getHandHolding() == InteractionHand.OFF_HAND) {
            if (SentientHelper.has(player, SentientEffectComponents.CRIPPLED_ARM.get())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (SentientHelper.hasFullSet(event.getEntity())) {
            boolean cancel = false;
            if (event.getHand() == InteractionHand.OFF_HAND && SentientHelper.has(event.getEntity(), SentientEffectComponents.CRIPPLED_ARM.get())) {
                cancel = true;
            }
            // Quenched downgrade prevents drinking
            if (event.getItemStack().getUseAnimation() == ItemUseAnimation.DRINK && SentientHelper.has(event.getEntity(), SentientEffectComponents.QUENCHED.get())) {
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
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (SentientHelper.hasFullSet(event.getEntity()) && event.getHand() == InteractionHand.OFF_HAND) {
            if (SentientHelper.has(event.getEntity(), SentientEffectComponents.CRIPPLED_ARM.get())) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        DamageSource source = event.getEntity().getLastDamageSource();
        if (source == null) {
            return;
        }
        Entity causer = event.getEntity().getLastDamageSource().getEntity();
        if (causer instanceof Player player) {
            if (SentientHelper.hasFullSet(player)) {
                float changed = SentientHelper.modifyKnockback(player, event.getEntity(), event.getEntity().getLastDamageSource(), event.getStrength());
                event.setStrength(changed);
            }
        }
    }

    @SubscribeEvent
    public static void onExpPickup(PlayerXpEvent.PickupXp event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (SentientHelper.hasFullSet(event.getEntity())) {
            int starting = event.getOrb().getValue();
            int ending = SentientHelper.modifyExperience(event.getEntity(), starting);
            if (ending != starting) {
                event.getOrb().setValue(ending);
            }
        }
    }

    @SubscribeEvent
    public static void onHeal(LivingHealEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (event.getEntity() instanceof Player player) {
            if (SentientHelper.hasFullSet(player)) {
                float changed = SentientHelper.modifyHealing(player, event.getAmount());
                event.setAmount(changed);
            }
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        Entity causer = event.getSource().getEntity();
        LivingEntity victim = event.getEntity();

        if (causer instanceof Player playerCauser) {
            if (SentientHelper.hasFullSet(playerCauser)) {
                float newDamage = SentientHelper.modifyDamageDealt(playerCauser, victim, event.getSource(), event.getNewDamage());
                // Debug: NeoVitae.LOGGER.info("{} -> {}", event.getNewDamage(), newDamage);
                event.setNewDamage(newDamage);
                if (victim.getType().getTags().noneMatch(t -> t.equals(NVTags.Entities.NO_SENTIENT_TRAINING))) {
                    SentientHelper.reactToDamageDealt(playerCauser, victim, event.getSource(), event.getNewDamage()); // here we want the damage included
                }
            }
        }

        if (victim instanceof Player playerVictim) {
            if (SentientHelper.hasFullSet(playerVictim)) {
                SentientHelper.reactToDamageTaken(playerVictim, event.getSource(), relevantDamage(event)); // here we do not so though etc arent as grindy as they were
                float newDamage = SentientHelper.modifyDamageTaken(playerVictim, event.getSource(), event.getNewDamage());
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
    public static void onBlockBroken(BreakBlockEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (LexVitaeAoeHandler.isAoeBreaking()) {
            return;
        }

        if (SentientHelper.hasFullSet(event.getPlayer())) {
            SentientHelper.runBlockBroken(event.getPlayer(), event.getState());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (SentientHelper.hasFullSet(event.getEntity())) {
            SentientHelper.ensureInitialized(event.getEntity());
            SentientHelper.runTick(event.getEntity());

            // Quenched downgrade - extinguish fire on the player
            if (SentientHelper.has(event.getEntity(), SentientEffectComponents.QUENCHED.get())) {
                if (event.getEntity().isOnFire()) {
                    event.getEntity().clearFire();
                }
            }
        }

        syncLivingElytraAsset(event.getEntity());
    }

    /**
     * Rewrites the worn Living chestplate's EQUIPPABLE component so its
     * EquipmentAsset matches whether the Elytra upgrade is currently active.
     * Pre-port client-side ElytraLayer handled this; the 26.1 EquipmentAsset
     * system requires the swap to live on the stack itself.
     */
    private static void syncLivingElytraAsset(Player player) {
        ItemStack chest = SentientHelper.getChest(player);
        if (chest.isEmpty() || !chest.is(NVItems.SENTIENT_PLATE.get())) return;

        Equippable current = chest.get(DataComponents.EQUIPPABLE);
        if (current == null) return;

        boolean hasElytra = SentientHelper.has(chest, SentientEffectComponents.ELYTRA.get());

        if (hasElytra && !chest.has(DataComponents.GLIDER)) {
            chest.set(DataComponents.GLIDER, Unit.INSTANCE);
        } else if (!hasElytra && chest.has(DataComponents.GLIDER)) {
            chest.remove(DataComponents.GLIDER);
        }

        ResourceKey<EquipmentAsset> desired = hasElytra
                ? NVMaterialsAndTiers.SENTIENT_ELYTRA_EQUIPMENT_ASSET
                : NVMaterialsAndTiers.SENTIENT_EQUIPMENT_ASSET;

        if (current.assetId().isPresent() && current.assetId().get().equals(desired)) return;

        chest.set(DataComponents.EQUIPPABLE, new Equippable(
                current.slot(),
                current.equipSound(),
                Optional.of(desired),
                current.cameraOverlay(),
                current.allowedEntities(),
                current.dispensable(),
                current.swappable(),
                current.damageOnHurt(),
                current.equipOnInteract(),
                current.canBeSheared(),
                current.shearingSound()));
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
        boolean from = fromStack.is(NVTags.Items.SENTIENT_UPGRADE_SET);
        boolean to = toStack.is(NVTags.Items.SENTIENT_UPGRADE_SET);

        if (!fromStack.is(NVTags.Items.SENTIENT_UPGRADE_SET) && !toStack.is(NVTags.Items.SENTIENT_UPGRADE_SET)) {
            return;
        }

        ItemStack chestStack = SentientHelper.getChest(player);
        if (chestStack.isEmpty()) {
            return;
        }

        ItemAttributeModifiers base = chestStack.getItem().components().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        base.forEach(EquipmentSlot.CHEST, (holder, modifier) -> builder.add(holder, modifier, EquipmentSlotGroup.CHEST));
        if (SentientHelper.hasFullSet(player)) {
            SentientHelper.getAttributes(chestStack, builder);
        }

        chestStack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());

        syncLivingElytraAsset(player);

        CuriosCompat.recalculateCuriosSlots(player);
    }

    @SubscribeEvent
    public static void onAttributeNonsense(ItemAttributeModifierEvent event) {
        ItemStack chestStack = event.getItemStack();
        if (chestStack.is(NVTags.Items.SENTIENT_UPGRADE_SET) && !SentientHelper.isNeverValid(chestStack)) {
            if (chestStack.getOrDefault(NVDataComponents.FULL_SET_MARKER, false)) {
            }
        }
    }

    @SubscribeEvent
    public static void entityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (event.getEntity() instanceof Projectile projectile) {
            if (projectile.getOwner() instanceof Player player) {
                if (SentientHelper.hasFullSet(player)) {
                    SentientHelper.runProjectile(player, projectile);
                }
            }
        }
    }
}
