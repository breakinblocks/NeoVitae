package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.ritual.types.RitualEnchantedVitae;
import com.breakinblocks.neovitae.ritual.types.RitualEnchantedVitae.EnchantPlan;

import java.util.List;
import java.util.ArrayList;
import com.breakinblocks.neovitae.ritual.types.RitualDeusEnchantedVitae;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualRegistry;
import java.util.Map;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class EnchantedVitaeTests {

    private static Holder<Enchantment> ench(GameTestHelper helper, ResourceKey<Enchantment> key) {
        return helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
    }

    private static ItemStack book(GameTestHelper helper, ResourceKey<Enchantment> key, int level) {
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        mutable.set(ench(helper, key), level);
        EnchantmentHelper.setEnchantments(stack, mutable.toImmutable());
        return stack;
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void appliesEveryValidEnchantmentAndLeavesBooksAlone(GameTestHelper helper) {
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        ItemStack sharpness = book(helper, Enchantments.SHARPNESS, 5);
        ItemStack looting = book(helper, Enchantments.LOOTING, 3);

        EnchantPlan plan = RitualEnchantedVitae.planEnchantments(sword, List.of(sharpness, looting));

        if (plan.conflict() != null) {
            helper.fail("Sharpness and Looting do not conflict");
            return;
        }
        if (plan.enchantments().size() != 2) {
            helper.fail("Expected both enchantments to be planned, got " + plan.enchantments().size());
            return;
        }

        RitualEnchantedVitae.applyEnchantments(sword, plan.enchantments());

        if (sword.getEnchantmentLevel(ench(helper, Enchantments.SHARPNESS)) != 5) {
            helper.fail("Sword should carry Sharpness V");
            return;
        }
        if (sword.getEnchantmentLevel(ench(helper, Enchantments.LOOTING)) != 3) {
            helper.fail("Sword should carry Looting III");
            return;
        }
        if (EnchantmentHelper.getEnchantmentsForCrafting(sharpness).getLevel(ench(helper, Enchantments.SHARPNESS)) != 5) {
            helper.fail("The Sharpness book should be untouched");
            return;
        }
        if (EnchantmentHelper.getEnchantmentsForCrafting(looting).getLevel(ench(helper, Enchantments.LOOTING)) != 3) {
            helper.fail("The Looting book should be untouched");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void skipsEnchantmentsTheItemCannotTake(GameTestHelper helper) {
        ItemStack pickaxe = new ItemStack(Items.DIAMOND_PICKAXE);

        EnchantPlan plan = RitualEnchantedVitae.planEnchantments(pickaxe,
                List.of(book(helper, Enchantments.SHARPNESS, 5), book(helper, Enchantments.FORTUNE, 3)));

        if (plan.conflict() != null) {
            helper.fail("An enchantment the pickaxe cannot take should be skipped, not reported as a conflict");
            return;
        }
        if (plan.enchantments().containsKey(ench(helper, Enchantments.SHARPNESS))) {
            helper.fail("Sharpness is not valid on a pickaxe and should have been skipped");
            return;
        }
        if (plan.enchantments().get(ench(helper, Enchantments.FORTUNE)) != 3) {
            helper.fail("Fortune III should have been planned for the pickaxe");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void reportsAnExclusivePairInsteadOfPickingOne(GameTestHelper helper) {
        ItemStack pickaxe = new ItemStack(Items.DIAMOND_PICKAXE);

        EnchantPlan plan = RitualEnchantedVitae.planEnchantments(pickaxe,
                List.of(book(helper, Enchantments.FORTUNE, 3), book(helper, Enchantments.SILK_TOUCH, 1)));

        if (plan.conflict() == null) {
            helper.fail("Fortune and Silk Touch cannot share an item and should be reported as a conflict");
            return;
        }
        if (!plan.enchantments().isEmpty()) {
            helper.fail("A conflicting offer should bind nothing at all");
            return;
        }

        List<Holder<Enchantment>> involved = List.of(plan.conflict().first(), plan.conflict().second());
        if (!involved.contains(ench(helper, Enchantments.FORTUNE))
                || !involved.contains(ench(helper, Enchantments.SILK_TOUCH))) {
            helper.fail("The conflict should name both Fortune and Silk Touch");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void reportsAConflictAgainstWhatTheItemAlreadyHolds(GameTestHelper helper) {
        ItemStack pickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
        ItemEnchantments.Mutable existing = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        existing.set(ench(helper, Enchantments.SILK_TOUCH), 1);
        EnchantmentHelper.setEnchantments(pickaxe, existing.toImmutable());

        EnchantPlan plan = RitualEnchantedVitae.planEnchantments(pickaxe,
                List.of(book(helper, Enchantments.FORTUNE, 3)));

        if (plan.conflict() == null) {
            helper.fail("Fortune offered to a Silk Touch pickaxe should be reported as a conflict");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void honoursLevelsBeyondTheVanillaCeiling(GameTestHelper helper) {
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);

        EnchantPlan plan = RitualEnchantedVitae.planEnchantments(sword,
                List.of(book(helper, Enchantments.SHARPNESS, 2),
                        book(helper, Enchantments.SHARPNESS, 4),
                        book(helper, Enchantments.SHARPNESS, 99)));

        int planned = plan.enchantments().get(ench(helper, Enchantments.SHARPNESS));
        if (planned != 99) {
            helper.fail("Expected the Sharpness 99 book to be honoured, got " + planned);
            return;
        }

        RitualEnchantedVitae.applyEnchantments(sword, plan.enchantments());
        if (sword.getEnchantmentLevel(ench(helper, Enchantments.SHARPNESS)) != 99) {
            helper.fail("The sword should carry Sharpness 99");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void skipsWhatTheItemAlreadyHasAtOrAboveTheOffer(GameTestHelper helper) {
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        ItemEnchantments.Mutable existing = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        existing.set(ench(helper, Enchantments.SHARPNESS), 4);
        EnchantmentHelper.setEnchantments(sword, existing.toImmutable());

        EnchantPlan equalOffer = RitualEnchantedVitae.planEnchantments(sword,
                List.of(book(helper, Enchantments.SHARPNESS, 4)));
        if (!equalOffer.enchantments().isEmpty()) {
            helper.fail("An offer matching what the item already holds should be skipped");
            return;
        }

        EnchantPlan betterOffer = RitualEnchantedVitae.planEnchantments(sword,
                List.of(book(helper, Enchantments.SHARPNESS, 5)));
        if (betterOffer.enchantments().get(ench(helper, Enchantments.SHARPNESS)) != 5) {
            helper.fail("A higher offer should upgrade the existing enchantment");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void consumingBooksOnlyEatsTheOnesThatContributed(GameTestHelper helper) {
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        ItemStack sharpness = book(helper, Enchantments.SHARPNESS, 5);
        ItemStack bystander = book(helper, Enchantments.EFFICIENCY, 5);

        EnchantPlan plan = RitualEnchantedVitae.planEnchantments(sword, List.of(sharpness, bystander));

        if (!RitualEnchantedVitae.contributedTo(sharpness, plan.enchantments())) {
            helper.fail("The Sharpness book fed this craft and should count as a contributor");
            return;
        }
        if (RitualEnchantedVitae.contributedTo(bystander, plan.enchantments())) {
            helper.fail("Efficiency is not valid on a sword, so that book contributed nothing");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void aStackOfBooksLosesOnlyOne(GameTestHelper helper) {
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        ItemStack stack = book(helper, Enchantments.SHARPNESS, 5);
        stack.setCount(4);

        EnchantPlan plan = RitualEnchantedVitae.planEnchantments(sword, List.of(stack));
        ItemEntity entity = new ItemEntity(helper.getLevel(), 0, 0, 0, stack);
        helper.getLevel().addFreshEntity(entity);

        RitualEnchantedVitae.consumeBooks(List.of(entity), plan.enchantments());

        if (entity.getItem().getCount() != 3) {
            helper.fail("A stack of books should lose exactly one, got " + entity.getItem().getCount());
            return;
        }
        entity.discard();
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void costScalesWithLevelAndRarity(GameTestHelper helper) {
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);

        int oneLevel = RitualEnchantedVitae.costFor(
                RitualEnchantedVitae.planEnchantments(sword, List.of(book(helper, Enchantments.SHARPNESS, 1))).enchantments());
        int fiveLevels = RitualEnchantedVitae.costFor(
                RitualEnchantedVitae.planEnchantments(sword, List.of(book(helper, Enchantments.SHARPNESS, 5))).enchantments());

        if (fiveLevels != oneLevel * 5) {
            helper.fail("Cost should scale linearly with level: " + oneLevel + " vs " + fiveLevels);
            return;
        }

        int commonPerLevel = oneLevel;
        int rarePerLevel = RitualEnchantedVitae.costFor(
                RitualEnchantedVitae.planEnchantments(sword, List.of(book(helper, Enchantments.MENDING, 1))).enchantments());
        if (rarePerLevel <= commonPerLevel) {
            helper.fail("A rare enchantment should cost more per level than a common one: "
                    + rarePerLevel + " vs " + commonPerLevel);
            return;
        }

        if (RitualEnchantedVitae.costFor(Map.of()) != 0) {
            helper.fail("An empty plan should cost nothing");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void deusAllowsConflictsAndUpgradesExistingLevels(GameTestHelper helper) {
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        RitualEnchantedVitae.applyEnchantments(sword, Map.of(ench(helper, Enchantments.SHARPNESS), 5));
        EnchantPlan plan = RitualDeusEnchantedVitae.planEnchantments(sword,
                List.of(book(helper, Enchantments.SHARPNESS, 1), book(helper, Enchantments.SMITE, 5), book(helper, Enchantments.EFFICIENCY, 5)));
        helper.assertTrue(plan.conflict() == null, "Deus should allow conflicts");
        helper.assertTrue(plan.enchantments().get(ench(helper, Enchantments.SHARPNESS)) == 6, "Existing Sharpness V should become VI");
        helper.assertTrue(plan.enchantments().get(ench(helper, Enchantments.SMITE)) == 5, "Higher book levels should transfer");
        helper.assertTrue(!plan.enchantments().containsKey(ench(helper, Enchantments.EFFICIENCY)), "Item support still applies");
        RitualEnchantedVitae.applyEnchantments(sword, plan.enchantments());
        helper.assertTrue(sword.getEnchantmentLevel(ench(helper, Enchantments.SMITE)) == 5, "Conflicting enchantments must be stored");
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void deusUsesTheGameStorageCeiling(GameTestHelper helper) {
        Holder<Enchantment> sharpness = ench(helper, Enchantments.SHARPNESS);
        ItemEnchantments.Mutable supported = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        supported.set(sharpness, Integer.MAX_VALUE);
        int ceiling = supported.getLevel(sharpness);
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        RitualEnchantedVitae.applyEnchantments(sword, Map.of(sharpness, ceiling - 1));
        List<ItemStack> books = List.of(book(helper, Enchantments.SHARPNESS, 1));
        EnchantPlan last = RitualDeusEnchantedVitae.planEnchantments(sword, books);
        helper.assertTrue(last.enchantments().get(sharpness) == ceiling, "Last supported level must remain reachable");
        RitualEnchantedVitae.applyEnchantments(sword, last.enchantments());
        helper.assertTrue(RitualDeusEnchantedVitae.planEnchantments(sword, books).enchantments().isEmpty(), "No paid no-op at the ceiling");
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void deusCostsScaleWithoutOverflow(GameTestHelper helper) {
        Holder<Enchantment> sharpness = ench(helper, Enchantments.SHARPNESS);
        int normal = sharpness.value().getMaxLevel();
        int base = RitualEnchantedVitae.costFor(Map.of(sharpness, normal + 1));
        helper.assertTrue(RitualDeusEnchantedVitae.costFor(Map.of(sharpness, normal + 1)) == base * 2, "First excess level doubles normal cost");
        int two = RitualEnchantedVitae.costFor(Map.of(sharpness, normal + 2));
        helper.assertTrue(RitualDeusEnchantedVitae.costFor(Map.of(sharpness, normal + 2)) == two * 4, "Second excess level quadruples normal cost");
        helper.assertTrue(RitualDeusEnchantedVitae.costFor(Map.of(sharpness, 255, ench(helper, Enchantments.SMITE), 255)) == Integer.MAX_VALUE, "Large costs must saturate rather than overflow");
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void deusHasCrownAndConfigurableConsumption(GameTestHelper helper) {
        RitualDeusEnchantedVitae deus = new RitualDeusEnchantedVitae();
        List<RitualComponent> base = new ArrayList<>();
        List<RitualComponent> upgraded = new ArrayList<>();
        new RitualEnchantedVitae().gatherComponents(base::add);
        deus.gatherComponents(upgraded::add);
        helper.assertTrue(upgraded.containsAll(base) && upgraded.size() == base.size() + 16, "Deus must add its crown");
        helper.assertTrue(deus.getCrystalLevel() == 2, "Deus needs the Divinus crystal");
        helper.assertTrue(RitualRegistry.getStats(deus).consumeBooks(), "Generated default must consume books");
        helper.succeed();
    }
}
