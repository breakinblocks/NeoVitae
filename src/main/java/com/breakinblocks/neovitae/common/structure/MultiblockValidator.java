package com.breakinblocks.neovitae.common.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Predicate;
import java.util.HashMap;

/**
 * Custom multiblock validation system for NeoVitae.
 * Validates multiblock structures for Ara Vitae tiers.
 */
public class MultiblockValidator {

    private final Map<BlockPos, Predicate<BlockState>> matchers;
    private final BlockPos offset;
    private final boolean symmetrical;

    public MultiblockValidator(Map<BlockPos, Predicate<BlockState>> matchers, BlockPos offset, boolean symmetrical) {
        this.matchers = matchers;
        this.offset = offset;
        this.symmetrical = symmetrical;
    }

    public MultiblockValidator(Map<BlockPos, Predicate<BlockState>> matchers) {
        this(matchers, BlockPos.ZERO, false);
    }

    /**
     * Validates the multiblock structure at the given anchor position.
     * @param level The world level
     * @param anchor The anchor position (typically the altar position + offset)
     * @return The rotation if valid, null if invalid
     */
    @Nullable
    public Rotation validate(Level level, BlockPos anchor) {
        for (Rotation rotation : Rotation.values()) {
            if (validateWithRotation(level, anchor, rotation)) {
                return rotation;
            }
            if (symmetrical) {
                break;
            }
        }
        return null;
    }

    private boolean validateWithRotation(Level level, BlockPos anchor, Rotation rotation) {
        for (Map.Entry<BlockPos, Predicate<BlockState>> entry : matchers.entrySet()) {
            BlockPos relativePos = entry.getKey();
            BlockPos rotatedPos = rotatePos(relativePos, rotation);
            BlockPos worldPos = anchor.offset(rotatedPos).offset(offset);
            
            BlockState stateAtPos = level.getBlockState(worldPos);
            if (!entry.getValue().test(stateAtPos)) {
                return false;
            }
        }
        return true;
    }

    private BlockPos rotatePos(BlockPos pos, Rotation rotation) {
        return switch (rotation) {
            case NONE -> pos;
            case CLOCKWISE_90 -> new BlockPos(-pos.getZ(), pos.getY(), pos.getX());
            case CLOCKWISE_180 -> new BlockPos(-pos.getX(), pos.getY(), -pos.getZ());
            case COUNTERCLOCKWISE_90 -> new BlockPos(pos.getZ(), pos.getY(), -pos.getX());
        };
    }

    public static class Builder {
        private final HashMap<BlockPos, Predicate<BlockState>> matchers = new HashMap<>();
        private BlockPos offset = BlockPos.ZERO;
        private boolean symmetrical = false;

        public Builder add(BlockPos pos, Predicate<BlockState> matcher) {
            matchers.put(pos, matcher);
            return this;
        }

        public Builder add(BlockPos pos, Block block) {
            matchers.put(pos, state -> state.is(block));
            return this;
        }

        public Builder add(BlockPos pos, TagKey<Block> tag) {
            matchers.put(pos, state -> state.is(tag));
            return this;
        }

        public Builder offset(int x, int y, int z) {
            this.offset = new BlockPos(x, y, z);
            return this;
        }

        public Builder symmetrical(boolean symmetrical) {
            this.symmetrical = symmetrical;
            return this;
        }

        public MultiblockValidator build() {
            return new MultiblockValidator(Map.copyOf(matchers), offset, symmetrical);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
