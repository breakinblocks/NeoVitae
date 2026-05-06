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
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.sentient.SentientEffectComponents;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;

import java.util.function.Consumer;

/**
 * Internal implementation interface for Sentient Armor items.
 *
 * <p>This interface extends {@link IUpgradeHolder} (the public API) and adds
 * NeoForge item extension overrides for special armor behavior.</p>
 *
 * @see IUpgradeHolder
 */
public interface UpgradeHolderBase extends IItemExtension, IUpgradeHolder {

    @Override
    default <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        if (SentientHelper.isNeverValid(stack)) {
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
        return SentientHelper.hasFullSet(player) && SentientHelper.has(player, SentientEffectComponents.GILDED.get());
    }

    @Override
    default boolean canElytraFly(ItemStack stack, LivingEntity wearer) {
        if (!(wearer instanceof Player player)) {
            return false;
        }
        return SentientHelper.hasFullSet(player) && SentientHelper.has(player, SentientEffectComponents.ELYTRA.get());
    }

    @Override
    default boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        if (!entity.level().isClientSide) {
            int nextFlightTick = flightTicks + 1;
            if (nextFlightTick % 10 == 0) {
                // Scale damage interval based on upgrade level - higher levels take damage less often
                int level = entity instanceof Player player ? SentientHelper.getLevel(player, SentientEffectComponents.ELYTRA.get()) : 1;
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
        return SentientHelper.hasFullSet(player) && SentientHelper.has(player, SentientEffectComponents.WALK_ON_POWDERED_SNOW.get());
    }

    @Override
    default boolean isEnderMask(ItemStack stack, Player player, EnderMan endermanEntity) {
        return SentientHelper.hasFullSet(player) && SentientHelper.has(player, SentientEffectComponents.IS_ENDER_MASK.get());
    }


    @Override
    default int getMaxUpgradePoints(ItemStack stack, Player player) {
        if (SentientHelper.isNeverValid(stack)) {
            return 0;
        }
        Integer maxPoints = stack.get(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS.get());
        return maxPoints != null ? maxPoints : NeoVitae.SERVER_CONFIG.DEFAULT_UPGRADE_POINTS.get();
    }

    @Override
    default boolean hasFullLivingArmorSet(Player player) {
        return SentientHelper.hasFullSet(player);
    }

    @Override
    default boolean isInvalidArmor(ItemStack stack) {
        return SentientHelper.isNeverValid(stack);
    }
}
