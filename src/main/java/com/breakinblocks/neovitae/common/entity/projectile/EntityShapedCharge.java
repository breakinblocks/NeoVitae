// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import com.breakinblocks.neovitae.common.block.BlockShapedExplosive;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.ExplosiveChargeBlockEntity;
import com.breakinblocks.neovitae.common.entity.NVEntities;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;
import net.minecraft.server.level.ServerLevel;

/**
 * Throwable shaped charge entity.
 * When it hits a surface, it places itself as a block.
 */
public class EntityShapedCharge extends ThrowableProjectile {

    private static final EntityDataAccessor<BlockState> BLOCK_STATE_DATA = SynchedEntityData.defineId(
            EntityShapedCharge.class, EntityDataSerializers.BLOCK_STATE);

    public EntityShapedCharge(EntityType<EntityShapedCharge> type, Level level) {
        super(type, level);
    }

    public EntityShapedCharge(Level level, Block block, LivingEntity thrower) {
        super(NVEntities.SHAPED_CHARGE.get(), thrower.getX(), thrower.getEyeY() - 0.1, thrower.getZ(), level);
        this.setOwner(thrower);
        this.setFallTile(block.defaultBlockState());
    }

    public EntityShapedCharge(Level level, Block block, double x, double y, double z) {
        super(NVEntities.SHAPED_CHARGE.get(), x, y, z, level);
        this.setFallTile(block.defaultBlockState());
    }

    public void setFallTile(BlockState state) {
        this.entityData.set(BLOCK_STATE_DATA, state);
    }

    public BlockState getBlockState() {
        return this.entityData.get(BLOCK_STATE_DATA);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(BLOCK_STATE_DATA, Blocks.SAND.defaultBlockState());
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            return;
        }

        HitResult rayResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

        if (rayResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) rayResult;
            Direction faceHit = blockHit.getDirection();
            BlockPos targetPos = blockHit.getBlockPos().relative(faceHit);
            BlockState targetState = this.level().getBlockState(targetPos);
            BlockState fallTile = this.getBlockState();

            // Check if we can place the charge
            if (targetState.isAir() || targetState.is(BlockTags.FIRE) ||
                    !targetState.getFluidState().isEmpty() || targetState.canBeReplaced()) {

                // Get the block state to place
                BlockState stateToPlace = fallTile.hasProperty(BlockShapedExplosive.ATTACHED)
                        ? fallTile.setValue(BlockShapedExplosive.ATTACHED, faceHit)
                        : fallTile;

                // Check block protection before placing
                if (BlockProtectionHelper.tryPlaceBlock(this.level(), targetPos, stateToPlace, this.getOwner())) {
                    // Transfer owner to the tile entity
                    BlockEntity tile = this.level().getBlockEntity(targetPos);
                    if (tile instanceof ExplosiveChargeBlockEntity explosiveCharge) {
                        Entity owner = this.getOwner();
                        if (owner instanceof Player player) {
                            explosiveCharge.setOwner(player.getUUID());
                        }
                    }
                    this.discard();
                } else {
                    // Protection prevented placement, drop the item
                    if (this.level() instanceof ServerLevel sl) {
                        this.spawnAtLocation(sl, fallTile.getBlock());
                    }
                    this.discard();
                }
            } else {
                // Can't place - drop as item
                if (this.level() instanceof ServerLevel sl) this.spawnAtLocation(sl, fallTile.getBlock());
                this.discard();
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput compound) {
        super.addAdditionalSaveData(compound);
        compound.store("TileEntityData", BlockState.CODEC, this.getBlockState());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput compound) {
        super.readAdditionalSaveData(compound);
        BlockState fallTile = compound.read("TileEntityData", BlockState.CODEC)
                .orElseGet(() -> NVBlocks.SHAPED_CHARGE.block().get().defaultBlockState());
        this.setFallTile(fallTile);
    }

    public Level getWorldObj() {
        return this.level();
    }
}
