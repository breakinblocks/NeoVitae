package com.breakinblocks.neovitae.api.ritual;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes an area of effect for a ritual.
 * Supports rectangular (AABB), hemispherical, and cross-shaped areas.
 *
 * <p>This is part of the Neo Vitae API and can be used by addon mods
 * to define custom ritual areas.</p>
 */
public abstract class AreaDescriptor {

    /**
     * Resets any cached position data.
     */
    public abstract void resetCache();

    /**
     * Checks if a position is within this area (relative to offset 0,0,0).
     *
     * @param pos The position to check
     * @return true if the position is within the area
     */
    public abstract boolean isWithinArea(BlockPos pos);

    /**
     * Gets all block positions contained within this area.
     *
     * @param masterPos The position of the master ritual stone
     * @return List of all contained positions
     */
    public abstract List<BlockPos> getContainedPositions(BlockPos masterPos);

    /**
     * Gets the axis-aligned bounding box for this area.
     *
     * @param masterPos The position of the master ritual stone
     * @return The AABB encompassing this area
     */
    public abstract AABB getAABB(BlockPos masterPos);

    /**
     * Modifies this area's bounds using two offset positions.
     *
     * @param offset1 First corner offset
     * @param offset2 Second corner offset
     */
    public abstract void modifyAreaByBlockPositions(BlockPos offset1, BlockPos offset2);

    /**
     * Checks if this area's offsets are within the given limits.
     *
     * @param offset1         First corner offset
     * @param offset2         Second corner offset
     * @param verticalLimit   Maximum vertical distance
     * @param horizontalLimit Maximum horizontal distance
     * @return true if within limits
     */
    public abstract boolean isWithinRange(BlockPos offset1, BlockPos offset2, int verticalLimit, int horizontalLimit);

    /**
     * Saves this area descriptor to NBT.
     *
     * @param tag The tag to save to
     */
    public abstract void saveToNBT(CompoundTag tag);

    /**
     * Loads this area descriptor from NBT.
     *
     * @param tag The tag to load from
     */
    public abstract void loadFromNBT(CompoundTag tag);

    /**
     * Creates a copy of this area descriptor.
     *
     * @return A new AreaDescriptor with the same configuration
     */
    public abstract AreaDescriptor copy();

    /**
     * Checks if this area descriptor intersects with another.
     *
     * @param other The other area descriptor
     * @return true if the areas intersect
     */
    public abstract boolean intersects(AreaDescriptor other);

    /**
     * Returns a new AreaDescriptor offset by the given position.
     *
     * @param offset The offset to apply
     * @return A new offset AreaDescriptor
     */
    public abstract AreaDescriptor offset(BlockPos offset);

    // Aliases for legacy compatibility
    public void writeToNBT(CompoundTag tag) { saveToNBT(tag); }
    public void readFromNBT(CompoundTag tag) { loadFromNBT(tag); }

    /**
     * Rectangular area descriptor using AABB.
     * Most commonly used for simple box-shaped ritual areas.
     */
    public static class Rectangle extends AreaDescriptor {
        public static final Codec<Rectangle> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BlockPos.CODEC.fieldOf("min").forGetter(r -> r.minimumOffset),
                        BlockPos.CODEC.fieldOf("max").forGetter(r -> r.maximumOffset)
                ).apply(instance, Rectangle::new)
        );

        private BlockPos minimumOffset;
        private BlockPos maximumOffset;
        private List<BlockPos> cachedPositions;
        private BlockPos cachedMasterPos;

        public Rectangle(BlockPos minimumOffset, BlockPos maximumOffset) {
            this.minimumOffset = minimumOffset;
            this.maximumOffset = maximumOffset;
        }

        public Rectangle(BlockPos offset, int sizeX, int sizeY, int sizeZ) {
            this(offset, offset.offset(sizeX - 1, sizeY - 1, sizeZ - 1));
        }

        /**
         * Creates a rectangle centered at the given position.
         *
         * @param center The center position offset
         * @param radius Horizontal radius from center
         * @param height Vertical height
         * @return A new centered Rectangle
         */
        public static Rectangle createCenteredAt(BlockPos center, int radius, int height) {
            return new Rectangle(
                    center.offset(-radius, 0, -radius),
                    center.offset(radius, height - 1, radius)
            );
        }

        @Override
        public void resetCache() {
            cachedPositions = null;
            cachedMasterPos = null;
        }

        @Override
        public boolean isWithinArea(BlockPos pos) {
            return pos.getX() >= minimumOffset.getX() && pos.getX() <= maximumOffset.getX() &&
                   pos.getY() >= minimumOffset.getY() && pos.getY() <= maximumOffset.getY() &&
                   pos.getZ() >= minimumOffset.getZ() && pos.getZ() <= maximumOffset.getZ();
        }

        @Override
        public List<BlockPos> getContainedPositions(BlockPos masterPos) {
            if (cachedPositions != null && masterPos.equals(cachedMasterPos)) {
                return cachedPositions;
            }

            List<BlockPos> positions = new ArrayList<>();
            for (BlockPos pos : BlockPos.betweenClosed(
                    masterPos.offset(minimumOffset),
                    masterPos.offset(maximumOffset))) {
                positions.add(pos.immutable());
            }
            cachedPositions = positions;
            cachedMasterPos = masterPos;
            return positions;
        }

        @Override
        public AABB getAABB(BlockPos masterPos) {
            BlockPos min = masterPos.offset(minimumOffset);
            BlockPos max = masterPos.offset(maximumOffset).offset(1, 1, 1);
            return new AABB(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
        }

        @Override
        public void modifyAreaByBlockPositions(BlockPos offset1, BlockPos offset2) {
            this.minimumOffset = new BlockPos(
                    Math.min(offset1.getX(), offset2.getX()),
                    Math.min(offset1.getY(), offset2.getY()),
                    Math.min(offset1.getZ(), offset2.getZ())
            );
            this.maximumOffset = new BlockPos(
                    Math.max(offset1.getX(), offset2.getX()),
                    Math.max(offset1.getY(), offset2.getY()),
                    Math.max(offset1.getZ(), offset2.getZ())
            );
            resetCache();
        }

        @Override
        public boolean isWithinRange(BlockPos offset1, BlockPos offset2, int verticalLimit, int horizontalLimit) {
            return Math.abs(offset1.getX()) <= horizontalLimit &&
                   Math.abs(offset1.getZ()) <= horizontalLimit &&
                   Math.abs(offset2.getX()) <= horizontalLimit &&
                   Math.abs(offset2.getZ()) <= horizontalLimit &&
                   Math.abs(offset1.getY()) <= verticalLimit &&
                   Math.abs(offset2.getY()) <= verticalLimit;
        }

        @Override
        public void saveToNBT(CompoundTag tag) {
            tag.putInt("minX", minimumOffset.getX());
            tag.putInt("minY", minimumOffset.getY());
            tag.putInt("minZ", minimumOffset.getZ());
            tag.putInt("maxX", maximumOffset.getX());
            tag.putInt("maxY", maximumOffset.getY());
            tag.putInt("maxZ", maximumOffset.getZ());
        }

        @Override
        public void loadFromNBT(CompoundTag tag) {
            minimumOffset = new BlockPos(tag.getInt("minX"), tag.getInt("minY"), tag.getInt("minZ"));
            maximumOffset = new BlockPos(tag.getInt("maxX"), tag.getInt("maxY"), tag.getInt("maxZ"));
            resetCache();
        }

        @Override
        public AreaDescriptor copy() {
            return new Rectangle(minimumOffset, maximumOffset);
        }

        /**
         * Gets the minimum corner offset.
         */
        public BlockPos getMinimumOffset() {
            return minimumOffset;
        }

        /**
         * Gets the maximum corner offset.
         */
        public BlockPos getMaximumOffset() {
            return maximumOffset;
        }

        @Override
        public boolean intersects(AreaDescriptor other) {
            if (other instanceof Rectangle rect) {
                return !(maximumOffset.getX() < rect.minimumOffset.getX() ||
                         minimumOffset.getX() > rect.maximumOffset.getX() ||
                         maximumOffset.getY() < rect.minimumOffset.getY() ||
                         minimumOffset.getY() > rect.maximumOffset.getY() ||
                         maximumOffset.getZ() < rect.minimumOffset.getZ() ||
                         minimumOffset.getZ() > rect.maximumOffset.getZ());
            }
            // For other types, use AABB-based intersection check
            AABB thisBox = getAABB(BlockPos.ZERO);
            AABB otherBox = other.getAABB(BlockPos.ZERO);
            return thisBox.intersects(otherBox);
        }

        @Override
        public AreaDescriptor offset(BlockPos offset) {
            return new Rectangle(minimumOffset.offset(offset), maximumOffset.offset(offset));
        }
    }

    /**
     * Hemispherical area descriptor.
     * Useful for rituals with spherical effects above the ritual stone.
     */
    public static class HemiSphere extends AreaDescriptor {
        private BlockPos centerOffset;
        private int radius;
        private List<BlockPos> cachedPositions;
        private BlockPos cachedMasterPos;

        public HemiSphere(BlockPos centerOffset, int radius) {
            this.centerOffset = centerOffset;
            this.radius = radius;
        }

        @Override
        public void resetCache() {
            cachedPositions = null;
            cachedMasterPos = null;
        }

        @Override
        public boolean isWithinArea(BlockPos pos) {
            double distSq = pos.distSqr(centerOffset);
            return distSq <= radius * radius && pos.getY() >= centerOffset.getY();
        }

        @Override
        public List<BlockPos> getContainedPositions(BlockPos masterPos) {
            if (cachedPositions != null && masterPos.equals(cachedMasterPos)) {
                return cachedPositions;
            }

            List<BlockPos> positions = new ArrayList<>();
            BlockPos center = masterPos.offset(centerOffset);
            int radiusSq = radius * radius;

            for (int x = -radius; x <= radius; x++) {
                for (int y = 0; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x * x + y * y + z * z <= radiusSq) {
                            positions.add(center.offset(x, y, z));
                        }
                    }
                }
            }

            cachedPositions = positions;
            cachedMasterPos = masterPos;
            return positions;
        }

        @Override
        public AABB getAABB(BlockPos masterPos) {
            BlockPos center = masterPos.offset(centerOffset);
            BlockPos min = center.offset(-radius, 0, -radius);
            BlockPos max = center.offset(radius + 1, radius + 1, radius + 1);
            return new AABB(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
        }

        @Override
        public void modifyAreaByBlockPositions(BlockPos offset1, BlockPos offset2) {
            // For hemisphere, we adjust the radius based on the offset
            this.radius = (int) Math.sqrt(offset1.distSqr(centerOffset));
            resetCache();
        }

        @Override
        public boolean isWithinRange(BlockPos offset1, BlockPos offset2, int verticalLimit, int horizontalLimit) {
            return radius <= horizontalLimit && radius <= verticalLimit;
        }

        @Override
        public void saveToNBT(CompoundTag tag) {
            tag.putInt("centerX", centerOffset.getX());
            tag.putInt("centerY", centerOffset.getY());
            tag.putInt("centerZ", centerOffset.getZ());
            tag.putInt("radius", radius);
        }

        @Override
        public void loadFromNBT(CompoundTag tag) {
            centerOffset = new BlockPos(tag.getInt("centerX"), tag.getInt("centerY"), tag.getInt("centerZ"));
            radius = tag.getInt("radius");
            resetCache();
        }

        @Override
        public AreaDescriptor copy() {
            return new HemiSphere(centerOffset, radius);
        }

        /**
         * Gets the center offset position.
         */
        public BlockPos getCenterOffset() {
            return centerOffset;
        }

        /**
         * Gets the radius of the hemisphere.
         */
        public int getRadius() {
            return radius;
        }

        @Override
        public boolean intersects(AreaDescriptor other) {
            AABB thisBox = getAABB(BlockPos.ZERO);
            AABB otherBox = other.getAABB(BlockPos.ZERO);
            return thisBox.intersects(otherBox);
        }

        @Override
        public AreaDescriptor offset(BlockPos offset) {
            return new HemiSphere(centerOffset.offset(offset), radius);
        }
    }

    /**
     * Cross-shaped area descriptor (extends in 4 cardinal directions).
     * Useful for rituals that need a plus-shaped area of effect.
     */
    public static class Cross extends AreaDescriptor {
        private BlockPos centerOffset;
        private int length;
        private int height;
        private List<BlockPos> cachedPositions;
        private BlockPos cachedMasterPos;

        public Cross(BlockPos centerOffset, int length, int height) {
            this.centerOffset = centerOffset;
            this.length = length;
            this.height = height;
        }

        @Override
        public void resetCache() {
            cachedPositions = null;
            cachedMasterPos = null;
        }

        @Override
        public boolean isWithinArea(BlockPos pos) {
            int dx = Math.abs(pos.getX() - centerOffset.getX());
            int dz = Math.abs(pos.getZ() - centerOffset.getZ());
            int dy = pos.getY() - centerOffset.getY();
            return dy >= 0 && dy < height && ((dx == 0 && dz <= length) || (dz == 0 && dx <= length));
        }

        @Override
        public List<BlockPos> getContainedPositions(BlockPos masterPos) {
            if (cachedPositions != null && masterPos.equals(cachedMasterPos)) {
                return cachedPositions;
            }

            List<BlockPos> positions = new ArrayList<>();
            BlockPos center = masterPos.offset(centerOffset);

            for (int y = 0; y < height; y++) {
                // Center column
                positions.add(center.offset(0, y, 0));
                // Arms in each direction
                for (int i = 1; i <= length; i++) {
                    positions.add(center.offset(i, y, 0));
                    positions.add(center.offset(-i, y, 0));
                    positions.add(center.offset(0, y, i));
                    positions.add(center.offset(0, y, -i));
                }
            }

            cachedPositions = positions;
            cachedMasterPos = masterPos;
            return positions;
        }

        @Override
        public AABB getAABB(BlockPos masterPos) {
            BlockPos center = masterPos.offset(centerOffset);
            BlockPos min = center.offset(-length, 0, -length);
            BlockPos max = center.offset(length + 1, height, length + 1);
            return new AABB(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
        }

        @Override
        public void modifyAreaByBlockPositions(BlockPos offset1, BlockPos offset2) {
            this.length = Math.max(Math.abs(offset1.getX()), Math.abs(offset1.getZ()));
            this.height = Math.abs(offset2.getY() - offset1.getY()) + 1;
            resetCache();
        }

        @Override
        public boolean isWithinRange(BlockPos offset1, BlockPos offset2, int verticalLimit, int horizontalLimit) {
            return length <= horizontalLimit && height <= verticalLimit;
        }

        @Override
        public void saveToNBT(CompoundTag tag) {
            tag.putInt("centerX", centerOffset.getX());
            tag.putInt("centerY", centerOffset.getY());
            tag.putInt("centerZ", centerOffset.getZ());
            tag.putInt("length", length);
            tag.putInt("height", height);
        }

        @Override
        public void loadFromNBT(CompoundTag tag) {
            centerOffset = new BlockPos(tag.getInt("centerX"), tag.getInt("centerY"), tag.getInt("centerZ"));
            length = tag.getInt("length");
            height = tag.getInt("height");
            resetCache();
        }

        @Override
        public AreaDescriptor copy() {
            return new Cross(centerOffset, length, height);
        }

        /**
         * Gets the center offset position.
         */
        public BlockPos getCenterOffset() {
            return centerOffset;
        }

        /**
         * Gets the arm length of the cross.
         */
        public int getLength() {
            return length;
        }

        /**
         * Gets the height of the cross.
         */
        public int getHeight() {
            return height;
        }

        @Override
        public boolean intersects(AreaDescriptor other) {
            AABB thisBox = getAABB(BlockPos.ZERO);
            AABB otherBox = other.getAABB(BlockPos.ZERO);
            return thisBox.intersects(otherBox);
        }

        @Override
        public AreaDescriptor offset(BlockPos offset) {
            return new Cross(centerOffset.offset(offset), length, height);
        }
    }
}
