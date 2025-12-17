package com.breakinblocks.neovitae.ritual.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Harvest handler for vines (jungle vines).
 * Only harvests vines that have another vine directly above them,
 * preserving the top of the vine to allow regrowth.
 */
public class HarvestHandlerVines implements IHarvestHandler {

    // Use shears for proper vine drops
    private static final ItemStack mockShears = new ItemStack(Items.SHEARS, 1);

    @Override
    public boolean harvest(Level level, BlockPos pos, BlockState state, List<ItemStack> drops, @Nullable UUID ownerUUID) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        // Check protection before breaking
        if (!BlockProtectionHelper.tryBreakBlockNoDrops(level, pos, ownerUUID)) {
            return false;
        }

        // Get drops using shears (vines require shears to drop)
        LootParams.Builder lootBuilder = new LootParams.Builder(serverLevel);
        Vec3 blockCenter = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        List<ItemStack> blockDrops = state.getDrops(lootBuilder
                .withParameter(LootContextParams.ORIGIN, blockCenter)
                .withParameter(LootContextParams.TOOL, mockShears));
        drops.addAll(blockDrops);

        return true;
    }

    @Override
    public boolean test(Level level, BlockPos pos, BlockState state) {
        // Only harvest vines that have another vine above them
        // This preserves the top vine for regrowth
        if (state.is(Blocks.VINE)) {
            BlockState above = level.getBlockState(pos.above());
            return above.is(Blocks.VINE);
        }
        return false;
    }
}
