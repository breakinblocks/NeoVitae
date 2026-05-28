package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.UUID;
import java.util.function.Consumer;

public class RitualSphere extends Ritual {

    private static final int MAX_CHECKS_PER_REFRESH = 100;
    private static final int SOURCE_TOP_OFFSET = -2;

    private BlockPos lastPos;

    public RitualSphere() {
        super("sphere", 0, 20000, "ritual." + NeoVitae.MODID + ".sphere");
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        Level world = ctx.level();
        BlockPos masterPos = ctx.masterPos();
        UUID owner = ctx.master().getOwner();

        int radius = getRadius(world.getBlockState(masterPos.below()).getBlock());
        int sourceBottomY = SOURCE_TOP_OFFSET - 2 * radius;
        int sourceCenterY = SOURCE_TOP_OFFSET - radius;
        int yTeleportOffset = -2 * sourceCenterY;
        double radiusSq = (radius + 0.5) * (radius + 0.5);

        int i = -radius, j = sourceBottomY, k = -radius;
        if (lastPos != null) {
            i = clamp(lastPos.getX(), -radius, radius);
            j = clamp(lastPos.getY(), sourceBottomY, SOURCE_TOP_OFFSET);
            k = clamp(lastPos.getZ(), -radius, radius);
        }

        int checks = 0;
        while (j <= SOURCE_TOP_OFFSET) {
            while (i <= radius) {
                while (k <= radius) {
                    if (checks >= MAX_CHECKS_PER_REFRESH) {
                        lastPos = new BlockPos(i, j, k);
                        return;
                    }
                    checks++;

                    double dx = i, dy = j - sourceCenterY, dz = k;
                    if (dx * dx + dy * dy + dz * dz <= radiusSq) {
                        if (tryLift(world, masterPos, i, j, k, yTeleportOffset, owner)) {
                            ctx.syphon(getRefreshCost());
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
            j++;
        }

        masterRitualStone.stopRitual(BreakType.DEACTIVATE);
        lastPos = null;
    }

    private boolean tryLift(Level world, BlockPos masterPos, int i, int j, int k, int yOffset, UUID owner) {
        BlockPos src = masterPos.offset(i, j, k);
        if (world.isEmptyBlock(src)) return false;

        BlockPos dst = src.offset(0, yOffset, 0);
        if (!world.isEmptyBlock(dst)) return false;

        if (!BlockProtectionHelper.canBreakBlock(world, src, owner)) return false;

        BlockState srcState = world.getBlockState(src);
        if (srcState.getDestroySpeed(world, src) < 0) return false;

        if (!BlockProtectionHelper.tryPlaceBlock(world, dst, srcState, owner)) return false;
        world.setBlock(src, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        return true;
    }

    private static int clamp(int value, int lo, int hi) {
        return Math.min(hi, Math.max(lo, value));
    }

    private static int getRadius(Block foundation) {
        if (foundation == Blocks.IRON_BLOCK) return 20;
        if (foundation == Blocks.GOLD_BLOCK) return 24;
        if (foundation == Blocks.DIAMOND_BLOCK) return 28;
        if (foundation == Blocks.NETHERITE_BLOCK) return 32;
        return 16;
    }

    @Override
    public int getRefreshTime() {
        return 1;
    }

    @Override
    public int getRefreshCost() {
        return 10;
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
        addRune(components, 1, 0, 1, EnumRuneType.EARTH);
        addRune(components, 1, 0, -1, EnumRuneType.EARTH);
        addRune(components, -1, 0, 1, EnumRuneType.EARTH);
        addRune(components, -1, 0, -1, EnumRuneType.EARTH);
        addRune(components, 2, 1, 0, EnumRuneType.EARTH);
        addRune(components, 0, 1, 2, EnumRuneType.EARTH);
        addRune(components, -2, 1, 0, EnumRuneType.EARTH);
        addRune(components, 0, 1, -2, EnumRuneType.EARTH);
        addRune(components, 2, 1, 2, EnumRuneType.AIR);
        addRune(components, 2, 1, -2, EnumRuneType.AIR);
        addRune(components, -2, 1, 2, EnumRuneType.AIR);
        addRune(components, -2, 1, -2, EnumRuneType.AIR);
        addRune(components, 2, 2, 0, EnumRuneType.FIRE);
        addRune(components, 0, 2, 2, EnumRuneType.FIRE);
        addRune(components, -2, 2, 0, EnumRuneType.FIRE);
        addRune(components, 0, 2, -2, EnumRuneType.DUSK);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualSphere();
    }
}
