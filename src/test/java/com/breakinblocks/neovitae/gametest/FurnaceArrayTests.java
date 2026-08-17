package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
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

    public static void register(NVTestRegistrar r) {
        r.add("furnace_array/full_stack_costs_one_charge", 400, helper -> {
            Rig rig = build(helper);
            if (rig == null) return;

            drop(helper, new ItemStack(Items.RAW_IRON, STACK));

            helper.runAfterDelay(260, () -> {
                int spent = STARTING_EV - rig.anima().getCurrentEV();
                int smelted = countAround(helper, Items.IRON_INGOT);

                if (smelted != STACK) {
                    helper.fail("Expected " + STACK + " smelted ingots, got " + smelted);
                    return;
                }
                if (spent != EV_COST) {
                    helper.fail("A stack of " + STACK + " should cost " + EV_COST + " EV, it cost " + spent);
                    return;
                }
                helper.succeed();
            });
        });

        r.add("furnace_array/single_item_costs_the_same_as_a_stack", 400, helper -> {
            Rig rig = build(helper);
            if (rig == null) return;

            drop(helper, new ItemStack(Items.RAW_IRON, 1));

            helper.runAfterDelay(260, () -> {
                int spent = STARTING_EV - rig.anima().getCurrentEV();
                int smelted = countAround(helper, Items.IRON_INGOT);

                if (smelted != 1) {
                    helper.fail("Expected 1 smelted ingot, got " + smelted);
                    return;
                }
                if (spent != EV_COST) {
                    helper.fail("A single item should cost " + EV_COST + " EV, it cost " + spent);
                    return;
                }
                helper.succeed();
            });
        });
    }
}
