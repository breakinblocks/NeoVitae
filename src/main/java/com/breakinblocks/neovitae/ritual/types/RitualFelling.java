package com.breakinblocks.neovitae.ritual.types;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.util.FakePlayer;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.Utils;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.*;
import java.util.function.Consumer;

/**
 * Ritual that cuts down trees in the configured area.
 * Drops are inserted into a chest inventory if available, otherwise spawned in the world.
 */
public class RitualFelling extends Ritual {

    public static final String FELL_RANGE = "fellRange";
    public static final String CHEST_RANGE = "chestRange";
    private static final int MAX_BLOCKS_PER_OPERATION = 128;

    public RitualFelling() {
        super("felling", 0, 2000, "ritual." + NeoVitae.MODID + ".felling");
        addBlockRange(FELL_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-10, 0, -10), 21, 30, 21));
        addBlockRange(CHEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        setMaximumVolumeAndDistanceOfRange(FELL_RANGE, 15000, 15, 40);
        setMaximumVolumeAndDistanceOfRange(CHEST_RANGE, 1, 5, 5);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        if (!(ctx.level() instanceof ServerLevel serverLevel)) return;

        List<BlockPos> positions = RitualHelper.getRangePositions(ctx.master(), this, FELL_RANGE, ctx.masterPos());
        UUID owner = ctx.master().getOwner();
        int blocksBroken = 0;
        int maxBlocks = Math.min(ctx.maxOperations(getRefreshCost()), MAX_BLOCKS_PER_OPERATION);

        ItemStack toolStack = new ItemStack(Items.NETHERITE_AXE);

        FakePlayer fakePlayer = new FakePlayer(serverLevel, new GameProfile(owner, "[NeoVitae]"));

        BlockPos chestPos = RitualHelper.getRangePositions(ctx.master(), this, CHEST_RANGE, ctx.masterPos()).getFirst();
        BlockEntity inv = ctx.level().getBlockEntity(chestPos);
        boolean hasInv = inv != null && Utils.getNumberOfFreeSlots(inv, Direction.DOWN) >= 1;

        // Find and break logs first, then leaves
        for (BlockPos pos : positions) {
            if (blocksBroken >= maxBlocks) break;

            BlockState state = ctx.level().getBlockState(pos);
            if (state.is(BlockTags.LOGS)) {
                if (BlockProtectionHelper.canBreakBlock(ctx.level(), pos, owner)) {
                    blocksBroken += breakAndCollect(ctx, serverLevel, pos, state, toolStack, fakePlayer, inv, hasInv);
                }
            }
        }

        // If we still have capacity, break leaves
        if (blocksBroken < maxBlocks) {
            for (BlockPos pos : positions) {
                if (blocksBroken >= maxBlocks) break;

                BlockState state = ctx.level().getBlockState(pos);
                if (state.is(BlockTags.LEAVES)) {
                    if (BlockProtectionHelper.canBreakBlock(ctx.level(), pos, owner)) {
                        blocksBroken += breakAndCollect(ctx, serverLevel, pos, state, toolStack, fakePlayer, inv, hasInv);
                    }
                }
            }
        }

        ctx.syphon(getRefreshCost() * blocksBroken);
    }

    /**
     * Breaks a block and collects its drops into an inventory or spawns them in the world.
     * @return 1 if the block was broken, 0 otherwise
     */
    private int breakAndCollect(RitualContext ctx, ServerLevel serverLevel, BlockPos pos,
                                BlockState state, ItemStack toolStack, FakePlayer fakePlayer,
                                BlockEntity inv, boolean hasInv) {
        LootParams.Builder lootBuilder = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, pos.getCenter())
                .withParameter(LootContextParams.BLOCK_STATE, state)
                .withParameter(LootContextParams.TOOL, toolStack)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, ctx.level().getBlockEntity(pos))
                .withOptionalParameter(LootContextParams.THIS_ENTITY, fakePlayer);

        List<ItemStack> drops = state.getDrops(lootBuilder);

        // Break the block without natural drops
        ctx.level().destroyBlock(pos, false);

        for (ItemStack dropStack : drops) {
            if (hasInv) {
                dropStack = Utils.insertStackIntoTile(dropStack, inv, Direction.DOWN);
            }
            if (!dropStack.isEmpty()) {
                Block.popResource(ctx.level(), ctx.masterPos(), dropStack);
            }
        }

        return 1;
    }

    @Override
    public int getRefreshTime() {
        return 20;
    }

    @Override
    public int getRefreshCost() {
        return 10;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 0, EnumRuneType.FIRE);
        addRune(components, 3, 0, 0, EnumRuneType.EARTH);
        addRune(components, -3, 0, 0, EnumRuneType.EARTH);
        addRune(components, 0, 0, 3, EnumRuneType.EARTH);
        addRune(components, 0, 0, -3, EnumRuneType.EARTH);
        addCornerRunes(components, 3, 0, EnumRuneType.AIR);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualFelling();
    }
}
