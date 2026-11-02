package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectCollection;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectMiner;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class ArrayRedstoneTests {
    private static final BlockPos POS = new BlockPos(2, 2, 2);
    private static final BlockPos POWER = POS.north();

    private static AlchemyArrayBlockEntity array(GameTestHelper helper) {
        AlchemyArrayBlockEntity array = new AlchemyArrayBlockEntity(helper.absolutePos(POS), NVBlocks.ALCHEMY_ARRAY.get().defaultBlockState());
        array.setLevel(helper.getLevel());
        return array;
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public static void minerPreservesStateAndResumes(GameTestHelper helper) {
        AlchemyArrayBlockEntity array = array(helper);
        AlchemyArrayEffectMiner miner = new AlchemyArrayEffectMiner();
        helper.setBlock(POS.below(), Blocks.STONE);
        helper.setBlock(POS.east(), Blocks.CHEST);
        ChestBlockEntity chest = (ChestBlockEntity) helper.getBlockEntity(POS.east());
        ItemStack tool = new ItemStack(Items.IRON_PICKAXE);
        miner.loadItems(List.of(tool, new ItemStack(Items.COAL, 3)));
        CompoundTag state = new CompoundTag();
        state.putDouble("progress", 0.99);
        state.putInt("cooldown", 2);
        state.putInt("failDelay", 10);
        miner.readFromNBT(state);
        helper.setBlock(POWER, Blocks.REDSTONE_BLOCK);
        helper.assertTrue(helper.getLevel().hasNeighborSignal(array.getBlockPos()), "Test must power the array");
        for (int i = 0; i < 40; i++) miner.update(array, i);
        CompoundTag paused = new CompoundTag();
        miner.writeToNBT(paused);
        helper.assertTrue(paused.equals(state), "Power must preserve mining progress and cooldowns");
        helper.assertTrue(chest.isEmpty(), "Pending drops must not transfer while powered");
        helper.assertTrue(tool.getDamageValue() == 0 && miner.saveItems().size() == 2, "Tool and pending drops must be preserved");
        helper.assertTrue(helper.getLevel().getBlockState(array.getBlockPos().below()).is(Blocks.STONE), "Powered miner must not break blocks");
        helper.setBlock(POWER, Blocks.AIR);
        for (int i = 0; i < 3; i++) miner.update(array, i);
        helper.assertTrue(helper.getLevel().getBlockState(array.getBlockPos().below()).isAir(), "Miner must resume its saved progress");
        helper.assertTrue(chest.countItem(Items.COAL) == 3 && chest.countItem(Items.COBBLESTONE) == 1, "Resume must deliver pending and new drops exactly once");
        helper.assertTrue(tool.getDamageValue() == 1, "Only resumed mining should wear the tool");
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public static void collectionPausesPullAndInsertion(GameTestHelper helper) {
        AlchemyArrayBlockEntity array = array(helper);
        AlchemyArrayEffectCollection collection = new AlchemyArrayEffectCollection();
        helper.setBlock(POS.below(), Blocks.CHEST);
        ChestBlockEntity chest = (ChestBlockEntity) helper.getBlockEntity(POS.below());
        Vec3 center = Vec3.atCenterOf(array.getBlockPos());
        ItemEntity near = new ItemEntity(helper.getLevel(), center.x, center.y, center.z, new ItemStack(Items.COAL, 3));
        ItemEntity far = new ItemEntity(helper.getLevel(), center.x + 1.5, center.y, center.z, new ItemStack(Items.IRON_INGOT));
        for (ItemEntity item : List.of(near, far)) {
            item.tickCount = 10;
            item.setDeltaMovement(Vec3.ZERO);
            helper.getLevel().addFreshEntity(item);
        }
        helper.setBlock(POWER, Blocks.REDSTONE_BLOCK);
        collection.update(array, 1);
        helper.assertTrue(chest.isEmpty() && !near.isRemoved() && near.getItem().getCount() == 3, "Powered collection must not insert or consume items");
        helper.assertTrue(far.getDeltaMovement().equals(Vec3.ZERO), "Powered collection must not pull items");
        helper.setBlock(POWER, Blocks.AIR);
        collection.update(array, 2);
        helper.assertTrue(near.isRemoved() && chest.countItem(Items.COAL) == 3, "Unpowered collection must resume insertion");
        helper.assertTrue(far.getDeltaMovement().x < 0, "Unpowered collection must resume pulling");
        near.discard();
        far.discard();
        helper.succeed();
    }
}
