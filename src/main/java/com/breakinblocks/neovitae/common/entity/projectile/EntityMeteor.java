package com.breakinblocks.neovitae.common.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.entity.BMEntities;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipeHelper;

/**
 * Meteor entity that falls from the sky and spawns blocks based on meteor recipes.
 * Used by the Meteor Ritual for customizable meteor generation.
 */
public class EntityMeteor extends ThrowableProjectile {

    private ItemStack containedStack = ItemStack.EMPTY;

    public EntityMeteor(EntityType<EntityMeteor> type, Level level) {
        super(type, level);
    }

    public EntityMeteor(Level level, LivingEntity thrower) {
        super(BMEntities.METEOR.get(), thrower, level);
    }

    public EntityMeteor(Level level, double x, double y, double z) {
        super(BMEntities.METEOR.get(), x, y, z, level);
    }

    public void setContainedStack(ItemStack stack) {
        this.containedStack = stack.copy();
    }

    public ItemStack getContainedStack() {
        return containedStack;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (!containedStack.isEmpty()) {
            compound.put("item", containedStack.save(level().registryAccess()));
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("item")) {
            containedStack = ItemStack.parse(level().registryAccess(), compound.getCompound("item"))
                    .orElse(ItemStack.EMPTY);
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void onInsideBlock(BlockState state) {
        if (level().isClientSide) {
            return;
        }

        if (!state.canOcclude()) {
            return;
        }

        int i = Mth.floor(position().x);
        int j = Mth.floor(position().y);
        int k = Mth.floor(position().z);
        BlockPos blockpos = new BlockPos(i, j, k);

        // Find and execute the meteor recipe
        MeteorRecipe recipe = MeteorRecipeHelper.findRecipe(level(), containedStack);
        if (recipe != null) {
            recipe.spawnMeteorInWorld(level(), blockpos);
        }

        this.discard();
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        // No synched data needed for now
    }

    @Override
    protected double getDefaultGravity() {
        return 0.03D;
    }
}
