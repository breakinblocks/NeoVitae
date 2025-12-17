package com.breakinblocks.neovitae.common.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.breakinblocks.neovitae.common.entity.BMEntities;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * Throwable potion flask entity for alchemy flasks.
 * Supports both splash and lingering effects.
 */
@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class EntityPotionFlask extends ThrowableItemProjectile implements ItemSupplier {

    public static final Predicate<LivingEntity> WATER_SENSITIVE = LivingEntity::isSensitiveToWater;
    private boolean isLingering = false;

    public EntityPotionFlask(EntityType<? extends EntityPotionFlask> type, Level level) {
        super(type, level);
    }

    public EntityPotionFlask(Level level, LivingEntity thrower) {
        super(BMEntities.POTION_FLASK.get(), thrower, level);
    }

    public EntityPotionFlask(Level level, double x, double y, double z) {
        super(BMEntities.POTION_FLASK.get(), x, y, z, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SPLASH_POTION;
    }

    public void setIsLingering(boolean lingering) {
        this.isLingering = lingering;
    }

    public boolean isLingering() {
        return isLingering;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05D;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            ItemStack itemstack = this.getItem();
            PotionContents contents = itemstack.get(DataComponents.POTION_CONTENTS);
            boolean isWater = contents != null && !contents.getAllEffects().iterator().hasNext();

            if (isWater) {
                Direction direction = result.getDirection();
                BlockPos blockpos = result.getBlockPos();
                BlockPos blockpos1 = blockpos.relative(direction);
                this.extinguishFires(blockpos1, direction);
                this.extinguishFires(blockpos1.relative(direction.getOpposite()), direction);

                for (Direction horizontal : Direction.Plane.HORIZONTAL) {
                    this.extinguishFires(blockpos1.relative(horizontal), horizontal);
                }
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            ItemStack itemstack = this.getItem();
            PotionContents contents = itemstack.get(DataComponents.POTION_CONTENTS);

            if (contents == null) {
                this.discard();
                return;
            }

            List<MobEffectInstance> effects = new java.util.ArrayList<>();
            contents.getAllEffects().forEach(effects::add);
            boolean isWater = effects.isEmpty();

            if (isWater) {
                this.applyWater();
            } else {
                if (this.isLingering()) {
                    this.makeAreaOfEffectCloud(contents);
                } else {
                    this.applySplash(effects, result.getType() == HitResult.Type.ENTITY
                            ? ((EntityHitResult) result).getEntity()
                            : null);
                }
            }

            int color = contents.getColor();
            boolean hasInstant = effects.stream().anyMatch(e -> e.getEffect().value().isInstantenous());
            this.level().levelEvent(hasInstant ? 2007 : 2002, this.blockPosition(), color);
            this.discard();
        }
    }

    private void applyWater() {
        AABB aabb = this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb, WATER_SENSITIVE);

        for (LivingEntity entity : list) {
            double dist = this.distanceToSqr(entity);
            if (dist < 16.0D && entity.isSensitiveToWater()) {
                entity.hurt(entity.damageSources().indirectMagic(entity, this.getOwner()), 1.0F);
            }
        }
    }

    private void applySplash(List<MobEffectInstance> effects, @Nullable Entity target) {
        AABB aabb = this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb);

        for (LivingEntity entity : list) {
            if (entity.isAffectedByPotions()) {
                double dist = this.distanceToSqr(entity);
                if (dist < 16.0D) {
                    double intensity = 1.0D - Math.sqrt(dist) / 4.0D;
                    if (entity == target) {
                        intensity = 1.0D;
                    }

                    for (MobEffectInstance effect : effects) {
                        MobEffect mobEffect = effect.getEffect().value();
                        if (mobEffect.isInstantenous()) {
                            mobEffect.applyInstantenousEffect(this, this.getOwner(), entity,
                                    effect.getAmplifier(), intensity);
                        } else {
                            int duration = (int) (intensity * (double) effect.getDuration() + 0.5D);
                            if (duration > 20) {
                                entity.addEffect(new MobEffectInstance(effect.getEffect(), duration,
                                        effect.getAmplifier(), effect.isAmbient(), effect.isVisible()));
                            }
                        }
                    }
                }
            }
        }
    }

    private void makeAreaOfEffectCloud(PotionContents contents) {
        AreaEffectCloud cloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
        Entity owner = this.getOwner();
        if (owner instanceof LivingEntity livingOwner) {
            cloud.setOwner(livingOwner);
        }

        cloud.setRadius(3.0F);
        cloud.setRadiusOnUse(-0.5F);
        cloud.setWaitTime(10);
        cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());
        cloud.setPotionContents(contents);

        this.level().addFreshEntity(cloud);
    }

    private void extinguishFires(BlockPos pos, Direction direction) {
        BlockState blockstate = this.level().getBlockState(pos);
        if (blockstate.is(BlockTags.FIRE)) {
            this.level().removeBlock(pos, false);
        } else if (CampfireBlock.isLitCampfire(blockstate)) {
            this.level().levelEvent((Player) null, 1009, pos, 0);
            CampfireBlock.dowse(null, this.level(), pos, blockstate);
            this.level().setBlockAndUpdate(pos, blockstate.setValue(CampfireBlock.LIT, Boolean.FALSE));
        }
    }
}
