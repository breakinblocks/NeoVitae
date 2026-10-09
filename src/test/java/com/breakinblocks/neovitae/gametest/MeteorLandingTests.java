package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.entity.projectile.EntityMeteor;
import com.breakinblocks.neovitae.common.meteor.MeteorLayer;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;
import com.breakinblocks.neovitae.ritual.types.RitualMeteor;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.List;
import java.util.UUID;

public final class MeteorLandingTests {

    private MeteorLandingTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("meteor/empty_landing_area_is_clear", 20, MeteorLandingTests::emptyLandingAreaIsClear);
        r.add("meteor/filled_landing_area_is_blocked", 20, MeteorLandingTests::filledLandingAreaIsBlocked);
        r.add("meteor/stray_leftovers_do_not_block_landing", 20, MeteorLandingTests::strayLeftoversDoNotBlockLanding);
        r.add("meteor/chest_offerings_used_in_order_one_meteor_at_a_time", 20, MeteorLandingTests::chestOfferingsAreUsedInOrderOneMeteorAtATime);
    }

    private static final BlockPos CENTER = new BlockPos(2, 2, 2);
    private static final UUID OWNER = UUID.fromString("a7f3c9e2-41b6-4d0a-8e5f-2c9d71b04e38");

    private static MeteorRecipe smallMeteor() {
        return new MeteorRecipe(Ingredient.of(Items.BARRIER), 0, 0, List.of(new MeteorLayer(2, 0, Blocks.STONE)));
    }

    private static void emptyLandingAreaIsClear(GameTestHelper helper) {
        if (!smallMeteor().isLandingAreaClear(helper.getLevel(), helper.absolutePos(CENTER), 0.1)) {
            helper.fail("An empty landing area should count as clear");
            return;
        }
        helper.succeed();
    }

    private static void filledLandingAreaIsBlocked(GameTestHelper helper) {
        MeteorRecipe recipe = smallMeteor();
        recipe.spawnMeteorInWorld(helper.getLevel(), helper.absolutePos(CENTER));
        if (recipe.isLandingAreaClear(helper.getLevel(), helper.absolutePos(CENTER), 0.1)) {
            helper.fail("A landing area holding a whole meteor should be blocked");
            return;
        }
        helper.succeed();
    }

    private static void strayLeftoversDoNotBlockLanding(GameTestHelper helper) {
        helper.setBlock(CENTER, Blocks.GLOWSTONE);
        helper.setBlock(CENTER.above(), Blocks.CRYING_OBSIDIAN);
        helper.setBlock(CENTER.east(), Blocks.SHORT_GRASS);
        if (!smallMeteor().isLandingAreaClear(helper.getLevel(), helper.absolutePos(CENTER), 0.1)) {
            helper.fail("A few leftover blocks should not stop the next meteor");
            return;
        }
        helper.succeed();
    }

    private static void chestOfferingsAreUsedInOrderOneMeteorAtATime(GameTestHelper helper) {
        Anima anima = AnimaHelper.getAnima(OWNER);
        if (anima.getCurrentEV() < 5000000) {
            anima.add(AnimaTicket.create(10000000), 100000000);
        }
        BlockPos masterRel = new BlockPos(2, 1, 2);
        BlockPos chestRel = masterRel.above();
        helper.setBlock(chestRel, Blocks.CHEST);
        ChestBlockEntity chest = helper.getBlockEntity(chestRel, ChestBlockEntity.class);
        if (chest == null) {
            helper.fail("Chest did not place");
            return;
        }
        chest.setItem(0, new ItemStack(Items.DIRT));
        chest.setItem(1, new ItemStack(Items.DIAMOND, 3));
        chest.setItem(2, new ItemStack(Items.IRON_BLOCK));

        BlockPos master = helper.absolutePos(masterRel);
        RitualMeteor ritual = new RitualMeteor();
        QuarryThroughputTests.StubMaster stub = new QuarryThroughputTests.StubMaster(helper.getLevel(), master, OWNER);
        ritual.performRitual(stub);
        ritual.performRitual(stub);

        AABB column = new AABB(master.getX() - 1, helper.getLevel().getMinY(), master.getZ() - 1,
                master.getX() + 2, helper.getLevel().getMaxY() + 64, master.getZ() + 2);
        List<EntityMeteor> meteors = helper.getLevel().getEntitiesOfClass(EntityMeteor.class, column);
        int meteorCount = meteors.size();
        meteors.forEach(Entity::discard);

        if (meteorCount != 1) {
            helper.fail("Expected one meteor in flight, got " + meteorCount);
            return;
        }
        if (chest.getItem(0).getCount() != 1 || chest.getItem(1).getCount() != 2 || chest.getItem(2).getCount() != 1) {
            helper.fail("Expected only one diamond taken, got " + chest.getItem(0) + ", " + chest.getItem(1) + ", " + chest.getItem(2));
            return;
        }
        helper.succeed();
    }
}
