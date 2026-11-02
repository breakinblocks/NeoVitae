package com.breakinblocks.neovitae.api.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.ritual.EnumFillMode;

public final class AreaCursor {

    private final AreaDescriptor area;
    private final BlockPos masterPos;
    private final int minX, minY, minZ;
    private final int sizeX, sizeY, sizeZ;
    private final long volume;
    private final boolean exactBox;

    private final BlockPos.MutableBlockPos scratch = new BlockPos.MutableBlockPos();
    private final BlockPos.MutableBlockPos neighbour = new BlockPos.MutableBlockPos();

    private AreaCursor(AreaDescriptor area, BlockPos masterPos, AABB box) {
        this.area = area;
        this.masterPos = masterPos;
        this.minX = (int) Math.floor(box.minX);
        this.minY = (int) Math.floor(box.minY);
        this.minZ = (int) Math.floor(box.minZ);
        this.sizeX = Math.max(0, (int) Math.round(box.maxX - box.minX));
        this.sizeY = Math.max(0, (int) Math.round(box.maxY - box.minY));
        this.sizeZ = Math.max(0, (int) Math.round(box.maxZ - box.minZ));
        this.volume = (long) sizeX * sizeY * sizeZ;
        this.exactBox = area instanceof AreaDescriptor.Rectangle;
    }

    public static AreaCursor of(AreaDescriptor area, BlockPos masterPos) {
        return new AreaCursor(area, masterPos, area.getAABB(masterPos));
    }

    public long volume() {
        return volume;
    }

    public BlockPos at(long index) {
        return atMutable(index).immutable();
    }

    public BlockPos.MutableBlockPos atMutable(long index) {
        long layer = (long) sizeX * sizeZ;
        int x = minX + (int) (index % sizeX);
        int z = minZ + (int) ((index / sizeX) % sizeZ);
        int y = minY + (int) (index / layer);
        return scratch.set(x, y, z);
    }

    public boolean accepts(long index, EnumFillMode mode) {
        long layer = (long) sizeX * sizeZ;
        int x = minX + (int) (index % sizeX);
        int z = minZ + (int) ((index / sizeX) % sizeZ);
        int y = minY + (int) (index / layer);

        if (!exactBox && !area.isWithinArea(relative(x, y, z))) {
            return false;
        }

        return switch (mode) {
            case CYLINDER -> roundedContains(x, y, z, false);
            case HOLLOW_CYLINDER -> roundedBoundary(x, y, z, false);
            case SPHERE -> roundedContains(x, y, z, true);
            case HOLLOW_SPHERE -> roundedBoundary(x, y, z, true);
            case SOLID -> true;
            case FLOOR -> y == minY;
            case ROOF -> y == minY + sizeY - 1;
            case HOLLOW -> boundaryX(x, y, z) || boundaryY(x, y, z) || boundaryZ(x, y, z);
            case WALLS -> boundaryX(x, y, z) || boundaryZ(x, y, z);
            case FRAME -> (boundaryX(x, y, z) ? 1 : 0)
                    + (boundaryY(x, y, z) ? 1 : 0)
                    + (boundaryZ(x, y, z) ? 1 : 0) >= 2;
        };
    }

    public long skipTo(long index, EnumFillMode mode) {
        if (index >= volume || mode == EnumFillMode.SOLID) return index;

        long layer = (long) sizeX * sizeZ;

        if (mode == EnumFillMode.FLOOR) {
            return index >= layer ? volume : index;
        }
        if (mode == EnumFillMode.ROOF) {
            long base = (sizeY - 1) * layer;
            return index < base ? base : index;
        }
        if (!exactBox) return index;

        int x = minX + (int) (index % sizeX);
        int z = minZ + (int) ((index / sizeX) % sizeZ);
        int y = minY + (int) (index / layer);

        boolean edgeZ = z == minZ || z == minZ + sizeZ - 1;
        boolean edgeY = y == minY || y == minY + sizeY - 1;
        boolean interiorRow = switch (mode) {
            case WALLS -> !edgeZ;
            case HOLLOW, FRAME -> !edgeZ && !edgeY;
            default -> false;
        };
        if (!interiorRow) return index;

        int lastX = minX + sizeX - 1;
        if (x > minX && x < lastX) {
            return index + (lastX - x);
        }
        return index;
    }

    // Test block centers against an ellipsoid fitted to the configured block bounds.
    // Half-block centers preserve symmetry for both odd and even dimensions.
    private boolean roundedContains(int x, int y, int z, boolean sphere) {
        if (x < minX || x >= minX + sizeX || y < minY || y >= minY + sizeY
                || z < minZ || z >= minZ + sizeZ) return false;
        double dx = (2.0 * (x - minX) + 1 - sizeX) / sizeX;
        double dz = (2.0 * (z - minZ) + 1 - sizeZ) / sizeZ;
        double dy = sphere ? (2.0 * (y - minY) + 1 - sizeY) / sizeY : 0;
        return dx * dx + dy * dy + dz * dz <= 1.0 + 1.0e-12;
    }

    private boolean roundedBoundary(int x, int y, int z, boolean sphere) {
        if (!roundedContains(x, y, z, sphere)) return false;
        // Cylinders only have side walls; spheres also close their top and bottom.
        return !roundedContains(x - 1, y, z, sphere) || !roundedContains(x + 1, y, z, sphere)
                || !roundedContains(x, y, z - 1, sphere) || !roundedContains(x, y, z + 1, sphere)
                || (sphere && (!roundedContains(x, y - 1, z, true) || !roundedContains(x, y + 1, z, true)));
    }

    private BlockPos relative(int x, int y, int z) {
        return neighbour.set(x - masterPos.getX(), y - masterPos.getY(), z - masterPos.getZ());
    }

    private boolean boundaryX(int x, int y, int z) {
        if (exactBox) return x == minX || x == minX + sizeX - 1;
        return outside(x - 1, y, z) || outside(x + 1, y, z);
    }

    private boolean boundaryY(int x, int y, int z) {
        if (exactBox) return y == minY || y == minY + sizeY - 1;
        return outside(x, y - 1, z) || outside(x, y + 1, z);
    }

    private boolean boundaryZ(int x, int y, int z) {
        if (exactBox) return z == minZ || z == minZ + sizeZ - 1;
        return outside(x, y, z - 1) || outside(x, y, z + 1);
    }

    private boolean outside(int x, int y, int z) {
        return !area.isWithinArea(relative(x, y, z));
    }
}
