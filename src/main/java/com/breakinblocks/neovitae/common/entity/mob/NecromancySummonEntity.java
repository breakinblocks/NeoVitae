package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;
import java.util.UUID;

public class NecromancySummonEntity extends Zombie {

    private int lifetime = 0;
    @Nullable private UUID ownerUUID;

    public NecromancySummonEntity(EntityType<? extends Zombie> type, Level level) {
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
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (SummonedUndeadHelper.shouldBlockDamage(source, ownerUUID)) return false;
        return super.hurtServer(level, source, amount);
    }

    // @Override (removed: not an override in 26.1) protected boolean shouldDropLoot() { return false; }
    @Override protected boolean isSunSensitive() { return false; }
    @Override public boolean removeWhenFarAway(double distance) { return false; }

    @Override
    protected void addAdditionalSaveData(ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        SummonedUndeadHelper.save(tag, ownerUUID, lifetime);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
        ownerUUID = SummonedUndeadHelper.load(tag);
        lifetime = tag.getIntOr("Lifetime", 0);
    }
}
