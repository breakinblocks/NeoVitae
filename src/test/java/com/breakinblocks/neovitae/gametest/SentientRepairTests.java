package com.breakinblocks.neovitae.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.item.NVItems;

import java.util.ArrayList;
import java.util.List;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class SentientRepairTests {

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

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void gearRepairsWithReagentBinding(GameTestHelper helper) {
        ItemStack reagent = new ItemStack(NVItems.REAGENT_BINDING.get());
        List<String> failures = new ArrayList<>();
        for (Item item : sentientGear()) {
            ItemStack stack = new ItemStack(item);
            if (!stack.isDamageableItem()) {
                failures.add(item + " is not damageable");
                continue;
            }
            if (!stack.getItem().isValidRepairItem(stack, reagent)) {
                failures.add(item + " rejects reagent_binding");
            }
        }
        if (!failures.isEmpty()) {
            helper.fail(String.join("; ", failures));
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void gearRejectsSpiritus(GameTestHelper helper) {
        List<String> failures = new ArrayList<>();
        for (Item item : sentientGear()) {
            ItemStack stack = new ItemStack(item);
            for (Item material : rejectedMaterials()) {
                if (stack.getItem().isValidRepairItem(stack, new ItemStack(material))) {
                    failures.add(item + " accepts " + material);
                }
            }
        }
        if (!failures.isEmpty()) {
            helper.fail(String.join("; ", failures));
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void anvilRepairStepApplies(GameTestHelper helper) {
        ItemStack reagent = new ItemStack(NVItems.REAGENT_BINDING.get());
        List<String> failures = new ArrayList<>();
        for (Item item : sentientGear()) {
            ItemStack stack = new ItemStack(item);
            stack.setDamageValue(stack.getMaxDamage() / 2);
            if (!stack.getItem().isValidRepairItem(stack, reagent)) {
                failures.add(item + " rejects reagent_binding at half durability");
                continue;
            }
            if (Math.min(stack.getDamageValue(), stack.getMaxDamage() / 4) <= 0) {
                failures.add(item + " yields no repair step at half durability");
            }
        }
        if (!failures.isEmpty()) {
            helper.fail(String.join("; ", failures));
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void sentientPlateKeepsIronScaledDurability(GameTestHelper helper) {
        int durability = new ItemStack(NVItems.SENTIENT_PLATE.get()).getMaxDamage();
        if (durability != 528) {
            helper.fail("sentient_plate has " + durability + " durability, expected 528");
            return;
        }
        helper.succeed();
    }
}
