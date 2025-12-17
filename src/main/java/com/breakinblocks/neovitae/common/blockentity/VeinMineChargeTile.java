package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.block.BlockShapedExplosive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Block entity for vein mining charges.
 * Finds and breaks connected blocks of the same type (ores).
 */
public class VeinMineChargeTile extends ExplosiveChargeTile {
    private Map<BlockPos, Boolean> veinPartsMap;
    private List<BlockPos> veinPartsCache;

    private static final Vec3i[] DIAGONALS = new Vec3i[]{
            new Vec3i(0, 1, 1), new Vec3i(0, 1, -1), new Vec3i(0, -1, 1), new Vec3i(0, -1, -1),
            new Vec3i(1, 0, 1), new Vec3i(-1, 0, 1), new Vec3i(1, 0, -1), new Vec3i(-1, 0, -1),
            new Vec3i(1, 1, 0), new Vec3i(-1, 1, 0), new Vec3i(1, -1, 0), new Vec3i(-1, -1, 0)
    };

    public int currentBlocks = 0;
    public int maxBlocks = 128;

    public VeinMineChargeTile(BlockEntityType<?> type, int maxBlocks, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.maxBlocks = maxBlocks;
    }

    public VeinMineChargeTile(BlockPos pos, BlockState state) {
        this(128, pos, state);
    }

    public VeinMineChargeTile(int maxBlocks, BlockPos pos, BlockState state) {
        this(BMTiles.VEINMINE_CHARGE_TYPE.get(), maxBlocks, pos, state);
    }

    @Override
    public void onUpdate() {
        if (level.isClientSide) {
            return;
        }

        Direction explosiveDirection = this.getBlockState().getValue(BlockShapedExplosive.ATTACHED).getOpposite();
        BlockState attachedState = level.getBlockState(worldPosition.relative(explosiveDirection));

        if (!isValidStartingBlock(attachedState)) {
            return;
        }

        if (veinPartsMap == null) {
            veinPartsMap = new HashMap<>();
            veinPartsMap.put(worldPosition.relative(explosiveDirection), false);
            veinPartsCache = new ArrayList<>();
            veinPartsCache.add(worldPosition.relative(explosiveDirection));
            resetCounter();
            currentBlocks = 1;
        }

        boolean foundNew = false;
        List<BlockPos> newPositions = new ArrayList<>();

        for (BlockPos currentPos : veinPartsCache) {
            if (!veinPartsMap.getOrDefault(currentPos, false)) {
                // Check cardinal directions
                for (Direction dir : Direction.values()) {
                    BlockPos checkPos = currentPos.relative(dir);
                    if (veinPartsMap.containsKey(checkPos)) {
                        continue;
                    }

                    BlockState checkState = level.getBlockState(checkPos);

                    if (currentBlocks < maxBlocks && isValidBlock(attachedState, checkState)) {
                        currentBlocks++;
                        veinPartsMap.put(checkPos, false);
                        newPositions.add(checkPos);
                        foundNew = true;
                    }
                }

                // Check diagonals
                if (checkDiagonals()) {
                    for (Vec3i vec : DIAGONALS) {
                        BlockPos checkPos = currentPos.offset(vec);
                        if (veinPartsMap.containsKey(checkPos)) {
                            continue;
                        }

                        BlockState checkState = level.getBlockState(checkPos);

                        if (currentBlocks < maxBlocks && isValidBlock(attachedState, checkState)) {
                            currentBlocks++;
                            veinPartsMap.put(checkPos, false);
                            newPositions.add(checkPos);
                            foundNew = true;
                        }
                    }
                }

                veinPartsMap.put(currentPos, true);
                if (currentBlocks >= maxBlocks) {
                    break;
                }
            }
        }

        veinPartsCache.addAll(newPositions);

        if (foundNew) {
            return;
        }

        // Use base class countdown and explosion logic
        if (tickCountdown()) {
            explodeAndBreakBlocks(explosiveDirection, veinPartsCache);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        maxBlocks = tag.getInt("maxBlocks");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("maxBlocks", maxBlocks);
    }

    public boolean isValidBlock(BlockState originalBlockState, BlockState testState) {
        return originalBlockState.getBlock() == testState.getBlock();
    }

    public boolean isValidStartingBlock(BlockState originalBlockState) {
        return originalBlockState.getDestroySpeed(level, worldPosition) != -1.0F;
    }

    public boolean checkDiagonals() {
        return true;
    }
}
