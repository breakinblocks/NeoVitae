package com.breakinblocks.neovitae.ritual.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Harvest handler for crops with stems such as Pumpkins and Melons.
 * Register a new crop for this handler with {@link HarvestRegistry#registerStemCrop(BlockState, BlockState)}
 */
public class HarvestHandlerStem implements IHarvestHandler {

    private static final ItemStack mockHoe = new ItemStack(Items.DIAMOND_HOE, 1);

    public HarvestHandlerStem() {
        // Register pumpkins and melons for all 4 directions
        for (int i = 0; i < 4; i++) {
            Direction facing = Direction.from2DDataValue(i);
            HarvestRegistry.registerStemCrop(
                    Blocks.PUMPKIN.defaultBlockState(),
                    Blocks.ATTACHED_PUMPKIN_STEM.defaultBlockState().setValue(AttachedStemBlock.FACING, facing));
            HarvestRegistry.registerStemCrop(
                    Blocks.MELON.defaultBlockState(),
                    Blocks.ATTACHED_MELON_STEM.defaultBlockState().setValue(AttachedStemBlock.FACING, facing));
        }
    }

    @Override
    public boolean harvest(Level level, BlockPos pos, BlockState state, List<ItemStack> drops, @Nullable UUID ownerUUID) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        Direction cropDir = state.getValue(AttachedStemBlock.FACING);

        if (cropDir != Direction.UP) {
            BlockPos cropPos = pos.relative(cropDir);
            BlockState probableCrop = level.getBlockState(cropPos);
            Collection<BlockState> registeredCrops = HarvestRegistry.getStemCrops().get(state);

            for (BlockState registeredCrop : registeredCrops) {
                if (registeredCrop.getBlock() == probableCrop.getBlock()) {
                    // Check protection before breaking
                    if (!BlockProtectionHelper.tryBreakBlockNoDrops(level, cropPos, ownerUUID)) {
                        return false;
                    }
                    LootParams.Builder lootBuilder = new LootParams.Builder(serverLevel);
                    Vec3 blockCenter = new Vec3(cropPos.getX() + 0.5, cropPos.getY() + 0.5, cropPos.getZ() + 0.5);
                    List<ItemStack> blockDrops = probableCrop.getDrops(lootBuilder
                            .withParameter(LootContextParams.ORIGIN, blockCenter)
                            .withParameter(LootContextParams.TOOL, mockHoe));
                    drops.addAll(blockDrops);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean test(Level level, BlockPos pos, BlockState state) {
        return HarvestRegistry.getStemCrops().containsKey(state);
    }
}
