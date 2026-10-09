package com.breakinblocks.neovitae.common.item.athanor;

import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

public interface IAthanorTool {

    enum Readiness {
        READY, UNBOUND, NOT_ENOUGH_EV
    }

    default double getCraftingSpeedMultiplier(ItemStack stack) {
        return 1;
    }

    default double getAdditionalOutputChanceMultiplier(ItemStack stack) {
        return 1;
    }

    default SpiritusType getDominantSpiritusType(ItemStack stack) {
        return SpiritusType.RAW;
    }

    default Readiness getReadiness(ItemStack stack) {
        return Readiness.READY;
    }

    default boolean consumeUse(ItemStack stack) {
        return false;
    }

    static Readiness readinessOf(ItemStack stack) {
        return stack.getItem() instanceof IAthanorTool tool ? tool.getReadiness(stack) : Readiness.READY;
    }

    static boolean tryConsumeUse(ItemStack stack) {
        return stack.getItem() instanceof IAthanorTool tool && tool.consumeUse(stack);
    }

    static Readiness networkReadiness(ItemStack stack, int evCost) {
        Binding binding = stack.getOrDefault(NVDataComponents.BINDING, Binding.EMPTY);
        if (binding.isEmpty()) return Readiness.UNBOUND;
        Anima network = AnimaHelper.getAnima(binding);
        return network != null && network.getCurrentEV() >= evCost ? Readiness.READY : Readiness.NOT_ENOUGH_EV;
    }

    static boolean syphonNetwork(ItemStack stack, int evCost) {
        Binding binding = stack.getOrDefault(NVDataComponents.BINDING, Binding.EMPTY);
        if (!binding.isEmpty()) {
            Anima network = AnimaHelper.getAnima(binding);
            if (network != null) {
                network.syphon(AnimaTicket.create(evCost));
            }
        }
        return true;
    }
}
