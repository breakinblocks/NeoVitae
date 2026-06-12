// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.IItemHandler;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.UUID;
import java.util.function.Consumer;

public class RitualMagnetism extends Ritual {

    public static final String PLACEMENT_RANGE = "placementRange";
    public static final String CHEST_RANGE = "chestRange";
    private static final int MAX_CHECKS_PER_REFRESH = 100;
    private static final int MAX_ORES_PER_REFRESH = 3;

    private BlockPos lastPos;

    public RitualMagnetism() {
        super("magnetism", 0, 5000, "ritual." + NeoVitae.MODID + ".magnetism");
        addBlockRange(PLACEMENT_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-1, 1, -1), 3, 3, 3));
        setMaximumVolumeAndDistanceOfRange(PLACEMENT_RANGE, 50, 4, 4);
        addBlockRange(CHEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        setMaximumVolumeAndDistanceOfRange(CHEST_RANGE, 1, 5, 5);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        Level world = ctx.level();
        BlockPos masterPos = ctx.masterPos();
        UUID owner = ctx.master().getOwner();

        BlockPos chestPos = RitualHelper.firstPositionInRange(ctx.master(), this, CHEST_RANGE, masterPos).orElse(masterPos.above());
        IItemHandler container = world.getCapability(Capabilities.ItemHandler.BLOCK, chestPos, null);
        int radius = getRadius(world.getBlockState(masterPos.below()).getBlock());
        int minRelY = world.getMinBuildHeight() - masterPos.getY();

        int i = -radius, j = -1, k = -radius;
        if (lastPos != null) {
            i = clamp(lastPos.getX(), -radius, radius);
            j = lastPos.getY();
            k = clamp(lastPos.getZ(), -radius, radius);
        }

        int checks = 0;
        int oresMoved = 0;

        while (j >= minRelY) {
            while (i <= radius) {
                while (k <= radius) {
                    if (checks >= MAX_CHECKS_PER_REFRESH || oresMoved >= MAX_ORES_PER_REFRESH) {
                        lastPos = new BlockPos(i, j, k);
                        return;
                    }
                    checks++;

                    BlockPos srcPos = masterPos.offset(i, j, k);
                    BlockState state = world.getBlockState(srcPos);
                    if (state.is(Tags.Blocks.ORES)
                            && BlockProtectionHelper.canBreakBlock(world, srcPos, owner)
                            && moveOre(ctx, world, srcPos, state, container, masterPos)) {
                        oresMoved++;
                        if (oresMoved >= MAX_ORES_PER_REFRESH) {
                            k++;
                            lastPos = new BlockPos(i, j, k);
                            return;
                        }
                    }
                    k++;
                }
                k = -radius;
                i++;
            }
            i = -radius;
            j--;
        }

        lastPos = new BlockPos(-radius, -1, -radius);
    }

    private boolean moveOre(RitualContext ctx, Level world, BlockPos srcPos, BlockState state,
                            IItemHandler container, BlockPos masterPos) {
        if (container != null) {
            ItemStack oreStack = new ItemStack(state.getBlock().asItem());
            if (!oreStack.isEmpty() && oreStack.getItem() != Items.AIR) {
                ItemStack remainder = insertAll(container, oreStack);
                if (remainder.isEmpty()) {
                    world.setBlock(srcPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    ctx.syphon(getRefreshCost());
                    return true;
                }
            }
        }

        BlockPos destination = findFreePlacementSlot(ctx, masterPos);
        if (destination == null) return false;
        if (!world.isEmptyBlock(destination)) return false;
        if (!world.setBlock(destination, state, Block.UPDATE_ALL)) return false;
        world.setBlock(srcPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        ctx.syphon(getRefreshCost());
        return true;
    }

    private static ItemStack insertAll(IItemHandler handler, ItemStack stack) {
        ItemStack remainder = stack;
        for (int slot = 0; slot < handler.getSlots() && !remainder.isEmpty(); slot++) {
            remainder = handler.insertItem(slot, remainder, false);
        }
        return remainder;
    }

    private BlockPos findFreePlacementSlot(RitualContext ctx, BlockPos masterPos) {
        for (BlockPos offset : RitualHelper.getRangePositions(ctx.master(), this, PLACEMENT_RANGE, masterPos)) {
            if (ctx.level().isEmptyBlock(offset)) {
                return offset.immutable();
            }
        }
        return null;
    }

    private static int clamp(int value, int lo, int hi) {
        return Math.min(hi, Math.max(lo, value));
    }

    private static int getRadius(Block foundation) {
        if (foundation == Blocks.IRON_BLOCK) return 7;
        if (foundation == Blocks.GOLD_BLOCK) return 15;
        if (foundation == Blocks.DIAMOND_BLOCK) return 31;
        if (foundation == Blocks.NETHERITE_BLOCK) return 63;
        return 3;
    }



    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        if (tag.contains("lastPosX")) {
            lastPos = new BlockPos(tag.getInt("lastPosX"), tag.getInt("lastPosY"), tag.getInt("lastPosZ"));
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        if (lastPos != null) {
            tag.putInt("lastPosX", lastPos.getX());
            tag.putInt("lastPosY", lastPos.getY());
            tag.putInt("lastPosZ", lastPos.getZ());
        }
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 1, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 1, EnumRuneType.AIR);
        addParallelRunes(components, 2, 2, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualMagnetism();
    }
}
