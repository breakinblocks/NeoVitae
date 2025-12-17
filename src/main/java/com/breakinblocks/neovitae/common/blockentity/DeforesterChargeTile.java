package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.block.BlockShapedExplosive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Block entity for deforester charges.
 * Finds and breaks connected logs and leaves.
 */
public class DeforesterChargeTile extends ExplosiveChargeTile {
    private Map<BlockPos, Boolean> treePartsMap;
    private List<BlockPos> treePartsCache;

    public int currentLogs = 0;
    public int maxLogs = 128;

    public DeforesterChargeTile(BlockEntityType<?> type, int maxLogs, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.maxLogs = maxLogs;
    }

    public DeforesterChargeTile(BlockPos pos, BlockState state) {
        this(128, pos, state);
    }

    public DeforesterChargeTile(int maxLogs, BlockPos pos, BlockState state) {
        this(BMTiles.DEFORESTER_CHARGE_TYPE.get(), maxLogs, pos, state);
    }

    @Override
    public void onUpdate() {
        if (level.isClientSide) {
            return;
        }

        Direction explosiveDirection = this.getBlockState().getValue(BlockShapedExplosive.ATTACHED).getOpposite();
        BlockState attachedState = level.getBlockState(worldPosition.relative(explosiveDirection));
        if (!attachedState.is(BlockTags.LOGS) && !attachedState.is(BlockTags.LEAVES)) {
            return;
        }

        if (treePartsMap == null) {
            treePartsMap = new HashMap<>();
            treePartsMap.put(worldPosition.relative(explosiveDirection), false);
            treePartsCache = new ArrayList<>();
            treePartsCache.add(worldPosition.relative(explosiveDirection));
            resetCounter();
        }

        boolean foundNew = false;
        List<BlockPos> newPositions = new ArrayList<>();

        for (BlockPos currentPos : treePartsCache) {
            if (!treePartsMap.getOrDefault(currentPos, false)) {
                for (Direction dir : Direction.values()) {
                    BlockPos checkPos = currentPos.relative(dir);
                    if (treePartsMap.containsKey(checkPos)) {
                        continue;
                    }

                    BlockState checkState = level.getBlockState(checkPos);

                    boolean isTree = false;
                    if (currentLogs >= maxLogs) {
                        continue;
                    }
                    if (checkState.is(BlockTags.LOGS)) {
                        currentLogs++;
                        isTree = true;
                    } else if (checkState.is(BlockTags.LEAVES)) {
                        isTree = true;
                    }

                    if (isTree) {
                        treePartsMap.put(checkPos, false);
                        newPositions.add(checkPos);
                        foundNew = true;
                    }
                }

                treePartsMap.put(currentPos, true);
                if (currentLogs >= maxLogs) {
                    break;
                }
            }
        }

        treePartsCache.addAll(newPositions);

        if (foundNew) {
            return;
        }

        // Use base class countdown and explosion logic
        if (tickCountdown()) {
            explodeAndBreakBlocks(explosiveDirection, treePartsCache);
        }
    }

}
