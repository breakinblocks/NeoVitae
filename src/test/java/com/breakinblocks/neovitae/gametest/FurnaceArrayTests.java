package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectFurnace;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.List;
import java.util.UUID;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class FurnaceArrayTests {

    private static final int EV_COST = 10;
    private static final int STARTING_EV = 10_000;
    private static final int STACK = 64;

    private static final BlockPos ARRAY_POS = new BlockPos(3, 1, 2);

    private record Rig(AlchemyArrayBlockEntity array, Anima anima) {}

    private static Rig build(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(ARRAY_POS, NVBlocks.ALCHEMY_ARRAY.get().defaultBlockState());

        BlockEntity be = helper.getBlockEntity(ARRAY_POS);
        if (!(be instanceof AlchemyArrayBlockEntity array)) {
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

    private static int countAround(GameTestHelper helper, net.minecraft.world.item.Item item) {
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

    @GameTest(template = "empty_5x5x7", timeoutTicks = 400)
    public void fullStackCostsOneCharge(GameTestHelper helper) {
        Rig rig = build(helper);
        if (rig == null) return;

        helper.spawnItem(Items.RAW_IRON, ARRAY_POS.above()).setItem(new ItemStack(Items.RAW_IRON, STACK));

        helper.succeedWhen(() -> {
            int spent = STARTING_EV - rig.anima().getCurrentEV();
            int smelted = countAround(helper, Items.IRON_INGOT);

            helper.assertTrue(!(smelted != STACK), "Expected " + STACK + " smelted ingots, got " + smelted);
            helper.assertTrue(!(spent != EV_COST), "A stack of " + STACK + " should cost " + EV_COST + " EV, it cost " + spent);
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 400)
    public void singleItemCostsTheSameAsAStack(GameTestHelper helper) {
        Rig rig = build(helper);
        if (rig == null) return;

        helper.spawnItem(Items.RAW_IRON, ARRAY_POS.above()).setItem(new ItemStack(Items.RAW_IRON, 1));

        helper.succeedWhen(() -> {
            int spent = STARTING_EV - rig.anima().getCurrentEV();
            int smelted = countAround(helper, Items.IRON_INGOT);

            helper.assertTrue(!(smelted != 1), "Expected 1 smelted ingot, got " + smelted);
            helper.assertTrue(!(spent != EV_COST), "A single item should cost " + EV_COST + " EV, it cost " + spent);
        });
    }
}
