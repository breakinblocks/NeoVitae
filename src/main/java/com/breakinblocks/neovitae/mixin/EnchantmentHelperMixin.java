package com.breakinblocks.neovitae.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.breakinblocks.neovitae.anointment.AnointmentRegistrar;
import com.breakinblocks.neovitae.common.datacomponent.AnointmentHolder;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getItemEnchantmentLevel", at = @At("RETURN"), cancellable = true)
    private static void neovitae$applyAnointmentEnchantments(
            Holder<Enchantment> enchantment, ItemInstance stack,
            CallbackInfoReturnable<Integer> cir) {
        AnointmentHolder holder = stack.get(NVDataComponents.ANOINTMENT_HOLDER.get());
        if (holder == null) return;

        if (enchantment.is(Enchantments.SILK_TOUCH)) {
            if (cir.getReturnValueI() <= 0 && holder.getAnointmentLevel(AnointmentRegistrar.SILK_TOUCH) > 0) {
                cir.setReturnValue(1);
            }
            return;
        }

        if (enchantment.is(Enchantments.FORTUNE)) {
            int fortuneLevel = holder.getAnointmentLevel(AnointmentRegistrar.FORTUNE);
            if (fortuneLevel > 0) {
                cir.setReturnValue(cir.getReturnValueI() + fortuneLevel);
            }
        }
    }
}
