package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SentientStats;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.common.NeoForgeMod;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;

public class AquaticArmorTests {
    public static void register(NVTestRegistrar r) {
        r.add("aquatic_armor/lavacollisionrequiresprotectionandfullset", 20, AquaticArmorTests::lavaCollisionRequiresProtectionAndFullSet);
        r.add("aquatic_armor/gillsrestoreaironlywithfullset", 20, AquaticArmorTests::gillsRestoreAirOnlyWithFullSet);
        r.add("aquatic_armor/aquaticagilitylevelsandunequip", 20, AquaticArmorTests::aquaticAgilityLevelsAndUnequip);
        r.add("aquatic_armor/newarmortrainsgillsunderwater", 20, AquaticArmorTests::newArmorTrainsGillsUnderwater);
    }

    private static Player wearer(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack chest = new ItemStack(NVItems.SENTIENT_PLATE.get());
        SentientHelper.setDefaultSentient(chest, helper.getLevel().registryAccess());
        chest.set(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS, 1000);
        player.setItemSlot(EquipmentSlot.CHEST, chest);
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(NVItems.SENTIENT_HELMET.get()));
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(NVItems.SENTIENT_LEGGINGS.get()));
        player.setItemSlot(EquipmentSlot.FEET, new ItemStack(NVItems.SENTIENT_BOOTS.get()));
        player.setPos(helper.absoluteVec(new Vec3(2.5, 1, 2.5)));
        player.setNoGravity(true);
        return player;
    }

    private static Holder<SentientUpgrade> upgrade(GameTestHelper helper, String name) {
        return helper.getLevel().registryAccess().lookupOrThrow(NVRegistries.Keys.SENTIENT_UPGRADES)
                .getOrThrow(ResourceKey.create(NVRegistries.Keys.SENTIENT_UPGRADES, NeoVitae.rl(name)));
    }

    public static void lavaCollisionRequiresProtectionAndFullSet(GameTestHelper helper) {
        Player player = wearer(helper);
        SentientHelper.applyExpToCap(player, upgrade(helper, "lava_strider"), 600, true);
        BlockPos pos = new BlockPos(2, 1, 2);
        helper.setBlock(pos, Blocks.LAVA);
        player.setPos(helper.absoluteVec(new Vec3(2.5, 2, 2.5)));
        helper.assertTrue(!player.canStandOnFluid(Fluids.LAVA.defaultFluidState()), "No footing without Fire Resistance");
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600));
        helper.assertTrue(player.canStandOnFluid(Fluids.LAVA.defaultFluidState()), "Protected full set should stand on lava");
        helper.assertTrue(!player.canStandOnFluid(Fluids.WATER.defaultFluidState()), "Lava Strider must not stand on water");
        BlockPos absolute = helper.absolutePos(pos);
        helper.assertTrue(!Blocks.LAVA.defaultBlockState().getCollisionShape(helper.getLevel(), absolute, CollisionContext.of(player)).isEmpty(), "Lava must supply an actual collision shape");
        player.move(MoverType.SELF, new Vec3(0, -1, 0));
        helper.assertTrue(Math.abs(player.getY() - (absolute.getY() + 0.5)) < 0.001, "Downward movement must land on the lava surface");
        player.setShiftKeyDown(true);
        helper.assertTrue(!player.canStandOnFluid(Fluids.LAVA.defaultFluidState()), "Crouching should allow sinking");
        player.move(MoverType.SELF, new Vec3(0, -0.25, 0));
        helper.assertTrue(player.getY() < absolute.getY() + 0.5, "Crouching must remove the physical platform");
        player.setShiftKeyDown(false);
        player.removeEffect(MobEffects.FIRE_RESISTANCE);
        helper.assertTrue(!player.canStandOnFluid(Fluids.LAVA.defaultFluidState()), "Expired protection must remove footing");
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600));
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        helper.assertTrue(!player.canStandOnFluid(Fluids.LAVA.defaultFluidState()), "Incomplete set must remove footing");
        helper.succeed();
    }

    public static void gillsRestoreAirOnlyWithFullSet(GameTestHelper helper) {
        Player player = wearer(helper);
        helper.setBlock(new BlockPos(2, 1, 2), Blocks.WATER);
        helper.setBlock(new BlockPos(2, 2, 2), Blocks.WATER);
        SentientHelper.applyExpToCap(player, upgrade(helper, "gills"), 600, true);
        player.baseTick();
        player.baseTick();
        helper.assertTrue(player.isUnderWater(), "Test wearer must be underwater");
        player.setAirSupply(100);
        player.baseTick();
        helper.assertTrue(player.getAirSupply() > 100, "Gills must refill air underwater");
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        player.setAirSupply(100);
        player.baseTick();
        helper.assertTrue(player.getAirSupply() < 100, "Air must drain without the full set");
        helper.succeed();
    }

    public static void aquaticAgilityLevelsAndUnequip(GameTestHelper helper) {
        Player player = wearer(helper);
        int[] xp = {200, 1000, 2000, 4000, 7000};
        int previous = 0;
        for (int i = 0; i < xp.length; i++) {
            SentientHelper.applyExpToCap(player, upgrade(helper, "aquatic_agility"), xp[i] - previous, true);
            player.tick();
            player.tick();
            helper.assertTrue(Math.abs(player.getAttributeValue(NeoForgeMod.SWIM_SPEED) - (1 + 0.2 * (i + 1))) < 0.001,
                    "Swim speed must increase by 20% at each level, level " + (i + 1));
            previous = xp[i];
        }
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        player.tick();
        player.tick();
        helper.assertTrue(Math.abs(player.getAttributeValue(NeoForgeMod.SWIM_SPEED) - 1) < 0.001, "Unequipping must remove swim bonus");
        helper.succeed();
    }

    public static void newArmorTrainsGillsUnderwater(GameTestHelper helper) {
        Player player = wearer(helper);
        helper.setBlock(new BlockPos(2, 1, 2), Blocks.WATER);
        helper.setBlock(new BlockPos(2, 2, 2), Blocks.WATER);
        player.baseTick();
        player.baseTick();
        SentientHelper.runTick(player);
        float xp = player.getItemBySlot(EquipmentSlot.CHEST).getOrDefault(NVDataComponents.UPGRADES, SentientStats.EMPTY)
                .upgrades().getOrDefault(upgrade(helper, "gills"), 0f);
        helper.assertTrue(xp > 0, "Starter trainer must award Gills experience underwater");
        helper.succeed();
    }
}
