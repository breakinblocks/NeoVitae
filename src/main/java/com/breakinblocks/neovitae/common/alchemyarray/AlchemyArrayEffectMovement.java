package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayTile;

/**
 * Alchemy array effect that pushes entities in the direction the array is facing.
 * Speed scales with items in the array slots.
 */
public class AlchemyArrayEffectMovement extends AlchemyArrayEffect {

    public AlchemyArrayEffectMovement() {
    }

    @Override
    public boolean update(AlchemyArrayTile tile, int ticksActive) {
        return false; // Doesn't complete on its own
    }

    @Override
    public void onEntityCollidedWithBlock(AlchemyArrayTile tile, Level level, BlockPos pos, BlockState state, Entity entity) {
        double verticalMotion = 0.5;
        double horizontalSpeed = 1.5;

        // Scale with items in slots
        int slot0Count = tile.getItem(0).getCount();
        int slot1Count = tile.getItem(1).getCount();
        verticalMotion += 0.05 * slot0Count;
        horizontalSpeed += 0.1 * slot1Count;

        entity.fallDistance = 0;

        Direction facing = tile.getRotation();
        Vec3 newMotion = switch (facing) {
            case NORTH -> new Vec3(0, verticalMotion, -horizontalSpeed);
            case SOUTH -> new Vec3(0, verticalMotion, horizontalSpeed);
            case WEST -> new Vec3(-horizontalSpeed, verticalMotion, 0);
            case EAST -> new Vec3(horizontalSpeed, verticalMotion, 0);
            default -> new Vec3(0, verticalMotion, 0);
        };

        entity.setDeltaMovement(newMotion);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
    }

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectMovement();
    }
}
