package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.soul.HellforgedHelper;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.NVRituals;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualRegistry;
import com.breakinblocks.neovitae.ritual.types.RitualDeusArmourEvolve;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class HellforgingTests {

    private HellforgingTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("hellforging/forges_chestplate_and_consumes_offering", 20, HellforgingTests::forgesChestplateAndConsumesOffering);
        r.add("hellforging/refuses_chestplate_below_deus_cap", 20, HellforgingTests::refusesChestplateBelowDeusCap);
        r.add("hellforging/needs_gem_and_ingot", 20, HellforgingTests::needsGemAndIngot);
        r.add("hellforging/pickaxe_gains_netherite_floor", 20, HellforgingTests::pickaxeGainsNetheriteFloor);
        r.add("hellforging/layout_reuses_evolution_positions", 20, HellforgingTests::layoutReusesEvolutionPositions);
        r.add("hellforging/overcharge_has_diminishing_returns", 20, HellforgingTests::overchargeHasDiminishingReturns);
    }


    private static final UUID OWNER = UUID.fromString("8c4f2e19-6b7a-4d35-9e01-f3a2c5b8d740");
    private static final BlockPos MASTER = new BlockPos(2, 1, 2);

    private static void fund() {
        Anima anima = AnimaHelper.getAnima(OWNER);
        if (anima.getCurrentEV() < 5000000) {
            anima.add(AnimaTicket.create(10000000), 100000000);
        }
    }

    private static ItemEntity drop(GameTestHelper helper, ItemStack stack) {
        Vec3 at = helper.absoluteVec(new Vec3(MASTER.getX() + 0.5, MASTER.getY() + 1.2, MASTER.getZ() + 0.5));
        ItemEntity entity = new ItemEntity(helper.getLevel(), at.x, at.y, at.z, stack);
        entity.setDeltaMovement(Vec3.ZERO);
        entity.setNoGravity(true);
        helper.getLevel().addFreshEntity(entity);
        return entity;
    }

    private static void runRitual(GameTestHelper helper) {
        fund();
        Ritual ritual = NVRituals.HELLFORGING.get().getNewCopy();
        ritual.performRitual(new QuarryThroughputTests.StubMaster(helper.getLevel(), helper.absolutePos(MASTER), OWNER));
    }

    private static void clearItems(GameTestHelper helper) {
        BlockPos master = helper.absolutePos(MASTER);
        helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(master).inflate(4)).forEach(Entity::discard);
    }

    private static ItemStack plate(int maxPoints) {
        ItemStack plate = new ItemStack(NVItems.SENTIENT_PLATE.get());
        plate.set(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS, maxPoints);
        return plate;
    }

    private static void forgesChestplateAndConsumesOffering(GameTestHelper helper) {
        clearItems(helper);
        ItemEntity plate = drop(helper, plate(RitualDeusArmourEvolve.DEUS_MAX_UPGRADE_POINTS));
        ItemEntity gem = drop(helper, new ItemStack(NVItems.PRISMATIC_SPIRITUS_GEM.get()));
        ItemEntity metal = drop(helper, new ItemStack(NVItems.HELLFORGED_INGOT.get(), 2));
        runRitual(helper);

        ItemStack forged = plate.getItem();
        boolean hellforged = HellforgedHelper.isHellforged(forged);
        int points = forged.getOrDefault(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS, 0);
        boolean gemGone = !gem.isAlive();
        int metalLeft = metal.getItem().getCount();
        boolean fireResistant = forged.has(DataComponents.DAMAGE_RESISTANT);
        clearItems(helper);

        helper.assertTrue(hellforged, "The chestplate should be Hellforged");
        helper.assertTrue(points == RitualDeusArmourEvolve.DEUS_MAX_UPGRADE_POINTS + 100,
                "The chestplate should hold 700 Upgrade Points, holds " + points);
        helper.assertTrue(gemGone, "The Prismatic Spiritus Gem should be consumed");
        helper.assertTrue(metalLeft == 1, "One Hellforged Ingot should be consumed, " + metalLeft + " left");
        helper.assertTrue(fireResistant, "A Hellforged item should resist fire");
        helper.succeed();
    }

    private static void refusesChestplateBelowDeusCap(GameTestHelper helper) {
        clearItems(helper);
        ItemEntity plate = drop(helper, plate(500));
        ItemEntity gem = drop(helper, new ItemStack(NVItems.PRISMATIC_SPIRITUS_GEM.get()));
        drop(helper, new ItemStack(NVItems.HELLFORGED_INGOT.get()));
        runRitual(helper);

        boolean hellforged = HellforgedHelper.isHellforged(plate.getItem());
        boolean gemAlive = gem.isAlive();
        clearItems(helper);

        helper.assertTrue(!hellforged, "A chestplate below 600 Upgrade Points should not be Hellforged");
        helper.assertTrue(gemAlive, "The gem should not be consumed when the ritual refuses");
        helper.succeed();
    }

    private static void needsGemAndIngot(GameTestHelper helper) {
        clearItems(helper);
        ItemEntity sword = drop(helper, new ItemStack(NVItems.SENTIENT_SWORD.get()));
        drop(helper, new ItemStack(NVItems.HELLFORGED_INGOT.get()));
        runRitual(helper);

        boolean hellforged = HellforgedHelper.isHellforged(sword.getItem());
        clearItems(helper);

        helper.assertTrue(!hellforged, "Without a Prismatic Spiritus Gem nothing should be Hellforged");
        helper.succeed();
    }

    private static void pickaxeGainsNetheriteFloor(GameTestHelper helper) {
        clearItems(helper);
        ItemEntity pickaxe = drop(helper, new ItemStack(NVItems.SENTIENT_PICKAXE.get()));
        float before = pickaxe.getItem().getDestroySpeed(Blocks.STONE.defaultBlockState());
        drop(helper, new ItemStack(NVItems.PRISMATIC_SPIRITUS_GEM.get()));
        drop(helper, new ItemStack(NVItems.HELLFORGED_INGOT.get()));
        runRitual(helper);

        ItemStack forged = pickaxe.getItem();
        float after = forged.getDestroySpeed(Blocks.STONE.defaultBlockState());
        int durability = forged.getMaxDamage();
        boolean obsidian = forged.isCorrectToolForDrops(Blocks.OBSIDIAN.defaultBlockState());
        clearItems(helper);

        helper.assertTrue(HellforgedHelper.isHellforged(forged), "The pickaxe should be Hellforged");
        helper.assertTrue(after >= 9.0F && after > before, "Stone mining speed should reach Netherite, was " + before + " now " + after);
        helper.assertTrue(durability == 2031, "Durability should match Netherite, is " + durability);
        helper.assertTrue(obsidian, "A Hellforged pickaxe should harvest obsidian");
        helper.succeed();
    }

    private static void layoutReusesEvolutionPositions(GameTestHelper helper) {
        List<RitualComponent> forging = layout(NVRituals.HELLFORGING.get());
        List<RitualComponent> evolution = layout(RitualRegistry.getRitual("armour_evolve"));
        helper.assertTrue(forging.size() == 28 && evolution.size() == 28,
                "Both layouts should have 28 stones, have " + forging.size() + " and " + evolution.size());
        for (RitualComponent component : evolution) {
            boolean samePosition = forging.stream().anyMatch(c -> c.offset().equals(component.offset()));
            helper.assertTrue(samePosition, "Hellforging is missing an Evolution stone position at " + component.offset());
        }
        long deus = forging.stream().filter(c -> c.runeType() == EnumRuneType.DEUS).count();
        long tenebrae = evolution.stream().filter(c -> c.runeType() == EnumRuneType.TENEBRAE).count();
        helper.assertTrue(deus == tenebrae && deus == 12, "The 12 Tenebrae stones should become Deus stones, found " + deus);
        helper.assertTrue(NVRituals.HELLFORGING.get().getCrystalLevel() == 2, "Hellforging should need the Divinus crystal");
        helper.succeed();
    }

    private static void overchargeHasDiminishingReturns(GameTestHelper helper) {
        double atSoftCap = HellforgedHelper.overchargeFactor(4000);
        double at8k = HellforgedHelper.overchargeFactor(8000);
        double at16k = HellforgedHelper.overchargeFactor(16000);
        double atMax = HellforgedHelper.overchargeFactor(32000);
        double past = HellforgedHelper.overchargeFactor(1000000);
        helper.assertTrue(Math.abs(atSoftCap - 1.0) < 1.0E-6, "No overcharge at the soft cap, got " + atSoftCap);
        helper.assertTrue(Math.abs(atMax - 1.5) < 1.0E-6, "Full overcharge at 32k should be 1.5, got " + atMax);
        helper.assertTrue(Math.abs(past - 1.5) < 1.0E-6, "Overcharge should stop at the maximum, got " + past);
        helper.assertTrue(Math.abs((at8k - atSoftCap) - (at16k - at8k)) < 1.0E-6,
                "Each doubling should add the same amount");
        ItemStack plain = new ItemStack(NVItems.SENTIENT_SWORD.get());
        ItemStack forged = new ItemStack(NVItems.SENTIENT_SWORD.get());
        forged.set(NVDataComponents.HELLFORGED, true);
        helper.assertTrue(HellforgedHelper.overchargeFactor(plain, 32000) == 1.0, "A plain Sentient tool should not overcharge");
        helper.assertTrue(Math.abs(HellforgedHelper.overchargeFactor(forged, 32000) - 1.5) < 1.0E-6, "A Hellforged tool should overcharge");
        helper.succeed();
    }

    private static List<RitualComponent> layout(Ritual ritual) {
        List<RitualComponent> components = new ArrayList<>();
        ritual.gatherComponents(components::add);
        return components;
    }
}
