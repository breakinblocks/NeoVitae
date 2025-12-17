package com.breakinblocks.neovitae.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.breakinblocks.neovitae.anointment.AnointmentRegistrar;
import com.breakinblocks.neovitae.common.datacomponent.AnointmentHolder;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;

/**
 * Mixin to make anointments behave like actual enchantments.
 * <p>
 * This ensures:
 * - Silk Touch: Blocks with inventories (shulker boxes, etc.) preserve their contents
 * - Fortune: Additional fortune from anointments stacks with existing enchantments
 */
@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getEnchantmentLevel(Lnet/minecraft/core/Holder;)I", at = @At("RETURN"), cancellable = true)
    private void bloodmagic$applyAnointmentEnchantments(Holder<Enchantment> enchantment, CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack) (Object) this;
        AnointmentHolder holder = self.get(BMDataComponents.ANOINTMENT_HOLDER.get());

        if (holder == null) {
            return;
        }

        // Handle Silk Touch anointment
        if (enchantment.is(Enchantments.SILK_TOUCH)) {
            // Only apply if tool doesn't already have silk touch
            if (cir.getReturnValue() <= 0 && holder.getAnointmentLevel(AnointmentRegistrar.SILK_TOUCH) > 0) {
                cir.setReturnValue(1);
            }
            return;
        }

        // Handle Fortune anointment
        if (enchantment.is(Enchantments.FORTUNE)) {
            int fortuneLevel = holder.getAnointmentLevel(AnointmentRegistrar.FORTUNE);
            if (fortuneLevel > 0) {
                // Add anointment fortune to existing enchantment level
                cir.setReturnValue(cir.getReturnValue() + fortuneLevel);
            }
        }
    }
}
