package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.item.IUpgradeHolder;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.living.LivingEffectComponents;
import com.breakinblocks.neovitae.common.living.LivingHelper;

import java.util.function.Consumer;

/**
 * Internal implementation interface for Living Armor items.
 *
 * <p>This interface extends {@link IUpgradeHolder} (the public API) and adds
 * NeoForge item extension overrides for special armor behavior.</p>
 *
 * @see IUpgradeHolder
 */
public interface UpgradeHolderBase extends IItemExtension, IUpgradeHolder {

    @Override
    default <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        if (LivingHelper.isNeverValid(stack)) {
            return IItemExtension.super.damageItem(stack, amount, entity, onBroken);
        }

        int durRemaining = (stack.getMaxDamage() - 1 - stack.getDamageValue());
        return Math.max(Math.min(durRemaining, amount), 0);
    }

    @Override
    default boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        if (!(wearer instanceof Player player)) {
            return false;
        }
        return LivingHelper.hasFullSet(player) && LivingHelper.has(player, LivingEffectComponents.GILDED.get());
    }

    @Override
    default boolean canElytraFly(ItemStack stack, LivingEntity wearer) {
        if (!(wearer instanceof Player player)) {
            return false;
        }
        return LivingHelper.hasFullSet(player) && LivingHelper.has(player, LivingEffectComponents.ELYTRA.get());
    }

    @Override
    default boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        if (!entity.level().isClientSide) {
            int nextFlightTick = flightTicks + 1;
            if (nextFlightTick % 10 == 0) {
                // Scale damage interval based on upgrade level - higher levels take damage less often
                int level = entity instanceof Player player ? LivingHelper.getLevel(player, LivingEffectComponents.ELYTRA.get()) : 1;
                int damageInterval = 20 * Math.max(level, 1); // Level 1 = 20 ticks, Level 2 = 40 ticks, etc.
                if (nextFlightTick % damageInterval == 0) {
                    stack.hurtAndBreak(1, entity, EquipmentSlot.CHEST);
                }
                entity.gameEvent(GameEvent.ELYTRA_GLIDE);
            }
        }
        return true;
    }

    // currently unused but why the heck not provide the option if its the easiest thing ever
    @Override
    default boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
        if (!(wearer instanceof Player player)) {
            return false;
        }
        return LivingHelper.hasFullSet(player) && LivingHelper.has(player, LivingEffectComponents.WALK_ON_POWDERED_SNOW.get());
    }

    @Override
    default boolean isEnderMask(ItemStack stack, Player player, EnderMan endermanEntity) {
        return LivingHelper.hasFullSet(player) && LivingHelper.has(player, LivingEffectComponents.IS_ENDER_MASK.get());
    }

    // ========================================
    // IUpgradeHolder API Implementation
    // ========================================

    @Override
    default int getMaxUpgradePoints(ItemStack stack, Player player) {
        if (LivingHelper.isNeverValid(stack)) {
            return 0;
        }
        // Get max points from component, defaulting to config value if not set
        Integer maxPoints = stack.get(BMDataComponents.CURRENT_MAX_UPGRADE_POINTS.get());
        return maxPoints != null ? maxPoints : NeoVitae.SERVER_CONFIG.DEFAULT_UPGRADE_POINTS.get();
    }

    @Override
    default boolean hasFullLivingArmorSet(Player player) {
        return LivingHelper.hasFullSet(player);
    }

    @Override
    default boolean isInvalidArmor(ItemStack stack) {
        return LivingHelper.isNeverValid(stack);
    }
}
