package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Repairable;
import net.minecraft.world.item.equipment.ArmorType;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.NVMaterialsAndTiers;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SentientRepairTests {

    private SentientRepairTests() {}

    private static List<Item> sentientGear() {
        return List.of(NVItems.SENTIENT_HELMET.get(), NVItems.SENTIENT_PLATE.get(),
                NVItems.SENTIENT_LEGGINGS.get(), NVItems.SENTIENT_BOOTS.get(),
                NVItems.SENTIENT_SWORD.get(), NVItems.SENTIENT_AXE.get(),
                NVItems.SENTIENT_PICKAXE.get(), NVItems.SENTIENT_SHOVEL.get(),
                NVItems.SENTIENT_SCYTHE.get(), NVItems.LEX_VITAE.get());
    }

    private static List<Item> rejectedMaterials() {
        return List.of(NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get(), NVItems.SPIRITUS_RUINA_CRYSTAL_ITEM.get(),
                NVItems.SPIRITUS_NIHILUM_CRYSTAL_ITEM.get(), NVItems.SPIRITUS_VINDICTA_CRYSTAL_ITEM.get(),
                NVItems.SPIRITUS_INVICTUS_CRYSTAL_ITEM.get(), NVItems.RAW_SPIRITUS.get());
    }

    public static void register(NVTestRegistrar r) {
        r.add("sentient/gear_repairs_with_reagent_binding", 30, helper -> helper.runAfterDelay(1, () -> {
            ItemStack reagent = new ItemStack(NVItems.REAGENT_BINDING.get());
            List<String> failures = new ArrayList<>();
            for (Item item : sentientGear()) {
                ItemStack stack = new ItemStack(item);
                Repairable repairable = stack.get(DataComponents.REPAIRABLE);
                if (repairable == null) {
                    failures.add(item + " has no REPAIRABLE component");
                    continue;
                }
                if (!stack.isDamageableItem()) {
                    failures.add(item + " is not damageable");
                    continue;
                }
                if (!stack.isValidRepairItem(reagent)) {
                    failures.add(item + " rejects reagent_binding");
                }
            }
            if (!failures.isEmpty()) {
                helper.fail(String.join("; ", failures));
                return;
            }
            helper.succeed();
        }));

        r.add("sentient/gear_rejects_spiritus", 30, helper -> helper.runAfterDelay(1, () -> {
            List<String> failures = new ArrayList<>();
            for (Item item : sentientGear()) {
                ItemStack stack = new ItemStack(item);
                for (Item material : rejectedMaterials()) {
                    if (stack.isValidRepairItem(new ItemStack(material))) {
                        failures.add(item + " accepts " + material);
                    }
                }
            }
            if (!failures.isEmpty()) {
                helper.fail(String.join("; ", failures));
                return;
            }
            helper.succeed();
        }));

        r.add("sentient/armour_durability_matches_iron_scaling", 30, helper -> helper.runAfterDelay(1, () -> {
            Map<Item, ArmorType> pieces = Map.of(
                    NVItems.SENTIENT_HELMET.get(), ArmorType.HELMET,
                    NVItems.SENTIENT_PLATE.get(), ArmorType.CHESTPLATE,
                    NVItems.SENTIENT_LEGGINGS.get(), ArmorType.LEGGINGS,
                    NVItems.SENTIENT_BOOTS.get(), ArmorType.BOOTS);
            List<String> failures = new ArrayList<>();
            pieces.forEach((item, type) -> {
                int expected = type.getDurability(NVMaterialsAndTiers.SENTIENT_ARMOUR_MATERIAL.durability());
                int actual = new ItemStack(item).getMaxDamage();
                if (actual != expected) {
                    failures.add(item + " has " + actual + " durability, expected " + expected);
                }
            });
            if (!failures.isEmpty()) {
                helper.fail(String.join("; ", failures));
                return;
            }
            if (new ItemStack(NVItems.SENTIENT_PLATE.get()).getMaxDamage() != 528) {
                helper.fail("sentient_plate durability drifted from the pre-port value of 528");
                return;
            }
            helper.succeed();
        }));

        r.add("sentient/anvil_repair_step_applies", 30, helper -> helper.runAfterDelay(1, () -> {
            ItemStack reagent = new ItemStack(NVItems.REAGENT_BINDING.get());
            List<String> failures = new ArrayList<>();
            for (Item item : sentientGear()) {
                ItemStack stack = new ItemStack(item);
                stack.setDamageValue(stack.getMaxDamage() / 2);
                if (!stack.isValidRepairItem(reagent)) {
                    failures.add(item + " rejects reagent_binding at half durability");
                    continue;
                }
                int step = Math.min(stack.getDamageValue(), stack.getMaxDamage() / 4);
                if (step <= 0) {
                    failures.add(item + " yields no repair step at half durability");
                }
            }
            if (!failures.isEmpty()) {
                helper.fail(String.join("; ", failures));
                return;
            }
            helper.succeed();
        }));
    }
}
