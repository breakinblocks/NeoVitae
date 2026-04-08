package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public final class SummonedUndeadHelper {

    public static final int MAX_LIFETIME = 6000;

    public static void registerMeleeGoals(PathfinderMob entity) {
        entity.goalSelector.addGoal(0, new FloatGoal(entity));
        entity.goalSelector.addGoal(2, new MeleeAttackGoal(entity, 1.0, false));
        entity.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(entity, 1.0));
        entity.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(entity, 1.0));
        entity.goalSelector.addGoal(8, new LookAtPlayerGoal(entity, Player.class, 8.0F));
        entity.goalSelector.addGoal(8, new RandomLookAroundGoal(entity));
    }

    @Nullable
    public static Player getOwner(Mob entity, @Nullable UUID ownerUUID) {
        if (ownerUUID == null || !(entity.level() instanceof ServerLevel serverLevel)) return null;
        return serverLevel.getServer().getPlayerList().getPlayer(ownerUUID);
    }

    public static boolean tickSummon(Mob entity, @Nullable UUID ownerUUID, int lifetime) {
        if (entity.level().isClientSide) return false;

        if (lifetime > MAX_LIFETIME) { entity.discard(); return true; }

        Player owner = getOwner(entity, ownerUUID);
        if (owner == null) { entity.discard(); return true; }
        if (entity.distanceToSqr(owner) > 1024) { entity.discard(); return true; }

        LivingEntity ownerTarget = owner.getLastHurtMob();
        if (ownerTarget != null && ownerTarget.isAlive() && ownerTarget != entity) {
            entity.setTarget(ownerTarget);
        }

        if (entity.getTarget() == null && entity.distanceToSqr(owner) > 100) {
            entity.getNavigation().moveTo(owner, 1.2);
        }

        return false;
    }

    public static boolean shouldBlockDamage(DamageSource source, @Nullable UUID ownerUUID) {
        return ownerUUID != null && source.getEntity() instanceof Player player && player.getUUID().equals(ownerUUID);
    }

    public static void save(CompoundTag tag, @Nullable UUID ownerUUID, int lifetime) {
        if (ownerUUID != null) tag.putUUID("Owner", ownerUUID);
        tag.putInt("Lifetime", lifetime);
    }

    public static UUID load(CompoundTag tag) {
        return tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
    }
}
