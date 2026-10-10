package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectCollection;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectFurnace;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.List;
import java.util.UUID;

public final class FurnaceArrayTests {

    private FurnaceArrayTests() {}

    private static final int EV_COST = 10;
    private static final int STARTING_EV = 10_000;
    private static final int STACK = 64;

    private static final BlockPos ARRAY_POS = new BlockPos(3, 1, 2);
    private static final BlockPos COLLECTOR_POS = new BlockPos(4, 1, 2);
    private static final BlockPos CHEST_POS = COLLECTOR_POS.below();

    private record Rig(AlchemyArrayBlockEntity array, Anima anima) {}

    private static Rig build(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(ARRAY_POS, NVBlocks.ALCHEMY_ARRAY.get().defaultBlockState());

        AlchemyArrayBlockEntity array = helper.getBlockEntity(ARRAY_POS, AlchemyArrayBlockEntity.class);
        if (array == null) {
            helper.fail("Expected AlchemyArrayBlockEntity at " + ARRAY_POS);
            return null;
        }

        UUID owner = UUID.randomUUID();
        Anima anima = AnimaHelper.getAnima(owner);
        if (anima == null) {
            helper.fail("Could not create an anima for the array owner");
            return null;
        }
        anima.set(AnimaTicket.create(STARTING_EV), Integer.MAX_VALUE);

        AlchemyArrayEffectFurnace effect = new AlchemyArrayEffectFurnace();
        effect.setEvCost(EV_COST);
        array.arrayEffect = effect;
        array.isActive = true;
        array.setOwnerBinding(new Binding(owner, "Owner"));

        return new Rig(array, anima);
    }

    private static void drop(GameTestHelper helper, ItemStack stack) {
        BlockPos abs = helper.absolutePos(ARRAY_POS.above());
        ItemEntity entity = new ItemEntity(helper.getLevel(),
                abs.getX() + 0.5, abs.getY() + 0.5, abs.getZ() + 0.5, stack);
        entity.setDeltaMovement(0, 0, 0);
        helper.getLevel().addFreshEntity(entity);
    }

    private static int countAround(GameTestHelper helper, Item item) {
        BlockPos abs = helper.absolutePos(ARRAY_POS);
        List<ItemEntity> entities = helper.getLevel().getEntitiesOfClass(
                ItemEntity.class, new AABB(abs).inflate(3.0));
        int total = 0;
        for (ItemEntity entity : entities) {
            if (entity.getItem().is(item)) {
                total += entity.getItem().getCount();
            }
        }
        return total;
    }

    private static ChestBlockEntity buildCollector(GameTestHelper helper) {
        helper.setBlock(CHEST_POS, Blocks.CHEST.defaultBlockState());
        helper.setBlock(COLLECTOR_POS, NVBlocks.ALCHEMY_ARRAY.get().defaultBlockState());
        AlchemyArrayBlockEntity collector = helper.getBlockEntity(COLLECTOR_POS, AlchemyArrayBlockEntity.class);
        collector.arrayEffect = new AlchemyArrayEffectCollection();
        collector.isActive = true;
        return helper.getBlockEntity(CHEST_POS, ChestBlockEntity.class);
    }

    public static void register(NVTestRegistrar r) {
        r.add("furnace_array/collector_waits_for_smelting", 400, helper -> {
            Rig rig = build(helper);
            if (rig == null) return;
            ChestBlockEntity chest = buildCollector(helper);

            drop(helper, new ItemStack(Items.RAW_IRON, STACK));
            drop(helper, new ItemStack(Items.STICK, 1));

            helper.runAtTickTime(100, () -> {
                helper.assertTrue(chest.countItem(Items.STICK) == 1, "The collector should take items that are not being smelted");
                helper.assertTrue(chest.countItem(Items.RAW_IRON) == 0, "The collector took raw iron before it was smelted");
            });

            helper.succeedWhen(() -> {
                helper.assertTrue(helper.getTick() > 100, "Waiting for the mid-smelt check");
                helper.assertTrue(chest.countItem(Items.IRON_INGOT) == STACK,
                        "Expected " + STACK + " smelted ingots in the chest, got " + chest.countItem(Items.IRON_INGOT));
                helper.assertTrue(chest.countItem(Items.RAW_IRON) == 0, "No raw iron should reach the chest");
            });
        });

        r.add("furnace_array/full_stack_costs_one_charge", 400, helper -> {
            Rig rig = build(helper);
            if (rig == null) return;

            drop(helper, new ItemStack(Items.RAW_IRON, STACK));

            helper.succeedWhen(() -> {
                int spent = STARTING_EV - rig.anima().getCurrentEV();
                int smelted = countAround(helper, Items.IRON_INGOT);

                helper.assertTrue(smelted == STACK, "Expected " + STACK + " smelted ingots, got " + smelted);
                helper.assertTrue(spent == EV_COST, "A stack of " + STACK + " should cost " + EV_COST + " EV, it cost " + spent);
            });
        });

        r.add("furnace_array/single_item_costs_the_same_as_a_stack", 400, helper -> {
            Rig rig = build(helper);
            if (rig == null) return;

            drop(helper, new ItemStack(Items.RAW_IRON, 1));

            helper.succeedWhen(() -> {
                int spent = STARTING_EV - rig.anima().getCurrentEV();
                int smelted = countAround(helper, Items.IRON_INGOT);

                helper.assertTrue(smelted == 1, "Expected 1 smelted ingot, got " + smelted);
                helper.assertTrue(spent == EV_COST, "A single item should cost " + EV_COST + " EV, it cost " + spent);
            });
        });
    }
}
