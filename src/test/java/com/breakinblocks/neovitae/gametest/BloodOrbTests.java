package com.breakinblocks.neovitae.gametest;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.item.BloodOrbItem;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.OrbFluidHandler;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;

public final class BloodOrbTests {

    private BloodOrbTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("blood_orb/not_foil_when_empty", 20, helper -> {
            ItemStack orb = new ItemStack(NVItems.ORB_WEAK.get());
            if (!(orb.getItem() instanceof BloodOrbItem orbItem)) {
                helper.fail("ORB_WEAK is not a BloodOrbItem");
                return;
            }
            if (orbItem.isFoil(orb)) {
                helper.fail("Empty orb should not be foil");
                return;
            }
            helper.succeed();
        });

        r.add("blood_orb/is_foil_when_full", 20, helper -> {
            ItemStack orb = new ItemStack(NVItems.ORB_WEAK.get());
            int capacity = OrbFluidHandler.getOrbFluidCapacity(orb);
            if (capacity <= 0) {
                helper.fail("Orb capacity should be > 0");
                return;
            }
            FluidStack blood = new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE.get(), capacity);
            OrbFluidHandler.fillInternal(orb, blood, true);
            if (orb.getItem() instanceof BloodOrbItem orbItem && !orbItem.isFoil(orb)) {
                helper.fail("Full orb should be foil");
                return;
            }
            helper.succeed();
        });

        r.add("blood_orb/not_foil_when_partial", 20, helper -> {
            ItemStack orb = new ItemStack(NVItems.ORB_WEAK.get());
            int capacity = OrbFluidHandler.getOrbFluidCapacity(orb);
            FluidStack blood = new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE.get(), capacity / 2);
            OrbFluidHandler.fillInternal(orb, blood, true);
            if (orb.getItem() instanceof BloodOrbItem orbItem && orbItem.isFoil(orb)) {
                helper.fail("Partially filled orb should not be foil");
                return;
            }
            helper.succeed();
        });

        r.add("blood_orb/internal_fill_works", 20, helper -> {
            ItemStack orb = new ItemStack(NVItems.ORB_APPRENTICE.get());
            FluidStack blood = new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE.get(), 1000);
            int filled = OrbFluidHandler.fillInternal(orb, blood, true);
            if (filled != 1000) {
                helper.fail("Should have filled 1000, got " + filled);
                return;
            }
            SimpleFluidContent content = orb.getOrDefault(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.EMPTY);
            if (content.isEmpty() || content.getAmount() != 1000) {
                helper.fail("Orb should contain 1000 mB");
                return;
            }
            helper.succeed();
        });

        r.add("blood_orb/fill_respects_capacity", 20, helper -> {
            ItemStack orb = new ItemStack(NVItems.ORB_WEAK.get());
            int capacity = OrbFluidHandler.getOrbFluidCapacity(orb);
            FluidStack blood = new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE.get(), capacity * 2);
            int filled = OrbFluidHandler.fillInternal(orb, blood, true);
            if (filled != capacity) {
                helper.fail("Should have filled " + capacity + " (capacity), got " + filled);
                return;
            }
            helper.succeed();
        });

        r.add("blood_orb/tier_matches_data_map", 20, helper -> {
            if (!(NVItems.ORB_WEAK.get() instanceof BloodOrbItem weak)) {
                helper.fail("Not BloodOrbItem");
                return;
            }
            ItemStack weakStack = new ItemStack(NVItems.ORB_WEAK.get());
            ItemStack grandStack = new ItemStack(NVItems.ORB_TRANSCENDENT.get());
            int weakTier = weak.getOrbTier(weakStack);
            int grandTier = ((BloodOrbItem) NVItems.ORB_TRANSCENDENT.get()).getOrbTier(grandStack);
            if (weakTier != 0) {
                helper.fail("Weak orb should be tier 0, got " + weakTier);
                return;
            }
            if (grandTier != 5) {
                helper.fail("Transcendent orb should be tier 5, got " + grandTier);
                return;
            }
            helper.succeed();
        });
    }
}
