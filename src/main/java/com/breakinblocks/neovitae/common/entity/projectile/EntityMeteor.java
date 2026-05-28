package com.breakinblocks.neovitae.common.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import com.breakinblocks.neovitae.common.entity.NVEntities;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipeHelper;
import net.minecraft.network.syncher.SynchedEntityData;

/**
 * Meteor entity that falls from the sky and spawns blocks based on meteor recipes.
 * Used by the Meteor Ritual for customizable meteor generation.
 */
public class EntityMeteor extends ThrowableProjectile {

    private static final int NO_TARGET_Y = Integer.MIN_VALUE;

    private ItemStack containedStack = ItemStack.EMPTY;
    private int targetY = NO_TARGET_Y;

    public EntityMeteor(EntityType<EntityMeteor> type, Level level) {
        super(type, level);
    }

    public EntityMeteor(Level level, LivingEntity thrower) {
        super(NVEntities.METEOR.get(), thrower.getX(), thrower.getEyeY() - 0.1, thrower.getZ(), level);
        this.setOwner(thrower);
    }

    public EntityMeteor(Level level, double x, double y, double z) {
        super(NVEntities.METEOR.get(), x, y, z, level);
    }

    public void setContainedStack(ItemStack stack) {
        this.containedStack = stack.copy();
    }

    public ItemStack getContainedStack() {
        return containedStack;
    }

    public void setTargetY(int targetY) {
        this.targetY = targetY;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput compound) {
        super.addAdditionalSaveData(compound);
        if (!containedStack.isEmpty()) {
            compound.store("item", ItemStack.CODEC, containedStack);
        }
        if (targetY != NO_TARGET_Y) {
            compound.putInt("targetY", targetY);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput compound) {
        super.readAdditionalSaveData(compound);
        containedStack = compound.read("item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        targetY = compound.getIntOr("targetY", NO_TARGET_Y);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide() || targetY == NO_TARGET_Y) return;
        if (position().y <= targetY) {
            detonateAt(Mth.floor(position().x), targetY, Mth.floor(position().z));
        }
    }

    @Override
    protected void onInsideBlock(BlockState state) {
        if (level().isClientSide()) return;
        if (targetY != NO_TARGET_Y) return;
        if (!state.canOcclude()) return;

        detonateAt(Mth.floor(position().x), Mth.floor(position().y), Mth.floor(position().z));
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        if (targetY != NO_TARGET_Y) return;
        super.onHitBlock(hitResult);
    }

    private void detonateAt(int x, int y, int z) {
        MeteorRecipe recipe = MeteorRecipeHelper.findRecipe(level(), containedStack);
        if (recipe != null) {
            recipe.spawnMeteorInWorld(level(), new BlockPos(x, y, z));
        }
        this.discard();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected double getDefaultGravity() {
        return 0.03D;
    }
}
