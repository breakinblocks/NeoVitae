package com.breakinblocks.neovitae.ritual.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Harvest handler for growing plants like Kelp and Twisting/Weeping Vines.
 */
public class HarvestHandlerGrowingPlant implements IHarvestHandler {

    private static final ItemStack mockHoe = new ItemStack(Items.DIAMOND_HOE, 1);

    @Override
    public boolean harvest(Level level, BlockPos pos, BlockState state, List<ItemStack> drops, @Nullable UUID ownerUUID) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        // Check protection before breaking
        if (!BlockProtectionHelper.tryBreakBlockNoDrops(level, pos, ownerUUID)) {
            return false;
        }
        LootParams.Builder lootBuilder = new LootParams.Builder(serverLevel);
        Vec3 blockCenter = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        List<ItemStack> blockDrops = state.getDrops(lootBuilder
                .withParameter(LootContextParams.ORIGIN, blockCenter)
                .withParameter(LootContextParams.TOOL, mockHoe));
        drops.addAll(blockDrops);
        return true;
    }

    @Override
    public boolean test(Level level, BlockPos pos, BlockState state) {
        // Check if this is a growing plant head block
        if (state.getBlock() instanceof GrowingPlantHeadBlock) {
            // Check if there's a body block either above or below
            // (kelp grows up, weeping vines grow down)
            BlockState above = level.getBlockState(pos.above());
            BlockState below = level.getBlockState(pos.below());

            return above.getBlock() instanceof GrowingPlantBodyBlock
                    || below.getBlock() instanceof GrowingPlantBodyBlock;
        }
        return false;
    }
}
