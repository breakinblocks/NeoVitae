package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;

public class NecromancySummonEntity extends Zombie {

    private int lifetime = 0;
    @Nullable private UUID ownerUUID;

    public NecromancySummonEntity(EntityType<? extends NecromancySummonEntity> type, Level level) {
        super(type, level);
        this.xpReward = 0;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    public void setOwner(Player owner) { this.ownerUUID = owner.getUUID(); }
    @Nullable public UUID getOwnerUUID() { return ownerUUID; }

    @Override
    protected void registerGoals() {
        SummonedUndeadHelper.registerMeleeGoals(this);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        lifetime++;
        SummonedUndeadHelper.tickSummon(this, ownerUUID, lifetime);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (SummonedUndeadHelper.shouldBlockDamage(source, ownerUUID)) return false;
        return super.hurt(source, amount);
    }

    @Override protected boolean shouldDropLoot() { return false; }
    @Override public boolean isSunSensitive() { return false; }
    @Override public boolean removeWhenFarAway(double distance) { return false; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        SummonedUndeadHelper.save(tag, ownerUUID, lifetime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ownerUUID = SummonedUndeadHelper.load(tag);
        lifetime = tag.getInt("Lifetime");
    }
}
