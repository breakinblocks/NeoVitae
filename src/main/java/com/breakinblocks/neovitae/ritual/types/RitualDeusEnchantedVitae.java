package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class RitualDeusEnchantedVitae extends RitualEnchantedVitae {
    public static final String NAME = "deus_enchanted_vitae";

    public RitualDeusEnchantedVitae() {
        super(NAME, 2, 100000);
    }

    @Override
    protected EnchantPlan createEnchantPlan(ItemStack target, List<ItemStack> books) {
        return planEnchantments(target, books);
    }

    public static EnchantPlan planEnchantments(ItemStack target, List<ItemStack> books) {
        Map<Holder<Enchantment>, Integer> offered = new HashMap<>();
        for (ItemStack book : books) {
            ItemEnchantments stored = EnchantmentHelper.getEnchantmentsForCrafting(book);
            for (Holder<Enchantment> holder : stored.keySet()) {
                offered.merge(holder, stored.getLevel(holder), Math::max);
            }
        }
        ItemEnchantments existing = EnchantmentHelper.getEnchantmentsForCrafting(target);
        Map<Holder<Enchantment>, Integer> plan = new HashMap<>();
        for (Map.Entry<Holder<Enchantment>, Integer> entry : offered.entrySet()) {
            Holder<Enchantment> holder = entry.getKey();
            if (!target.supportsEnchantment(holder)) continue;
            int current = existing.getLevel(holder);
            if (current == Integer.MAX_VALUE) continue;
            int requested = Math.max(current + 1, entry.getValue());
            // Ask the game's setter to enforce its supported storage limit, including mod changes.
            ItemEnchantments.Mutable supported = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            supported.set(holder, requested);
            int level = supported.getLevel(holder);
            if (level > current) plan.put(holder, level);
        }
        return new EnchantPlan(plan, null);
    }

    @Override
    protected int enchantmentCost(Map<Holder<Enchantment>, Integer> plan) {
        return costFor(plan);
    }

    public static int costFor(Map<Holder<Enchantment>, Integer> plan) {
        long total = 0;
        for (Map.Entry<Holder<Enchantment>, Integer> entry : plan.entrySet()) {
            Enchantment enchantment = entry.getKey().value();
            int rarity = Math.min(5, Math.max(1, 10 / Math.max(1, enchantment.getWeight())));
            long cost = (long) entry.getValue() * 2000 * rarity;
            int extraLevels = Math.max(0, entry.getValue() - enchantment.getMaxLevel());
            for (int i = 0; i < extraLevels && cost < Integer.MAX_VALUE; i++) cost *= 2;
            total += Math.min(cost, Integer.MAX_VALUE);
            if (total >= Integer.MAX_VALUE) return Integer.MAX_VALUE;
        }
        return (int) total;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addDeusCrown(components, super::gatherComponents);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualDeusEnchantedVitae();
    }
}
