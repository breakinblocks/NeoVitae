package com.breakinblocks.neovitae.api.sigil.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;

import java.util.List;
import java.util.function.Supplier;

/**
 * Sigil effect that pulls nearby items and experience orbs toward the player.
 */
public record MagnetismSigilEffect(int range, double pullSpeed) implements SigilEffect {

    public static final int DEFAULT_RANGE = 5;
    public static final double DEFAULT_PULL_SPEED = 0.05;

    public static final MapCodec<MagnetismSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("range", DEFAULT_RANGE).forGetter(MagnetismSigilEffect::range),
                    Codec.DOUBLE.optionalFieldOf("pull_speed", DEFAULT_PULL_SPEED).forGetter(MagnetismSigilEffect::pullSpeed)
            ).apply(instance, MagnetismSigilEffect::new)
    );

    public static final Supplier<MapCodec<MagnetismSigilEffect>> REGISTRATION =
            SigilEffectRegistry.SIGIL_EFFECT_TYPES.register("magnetism", () -> CODEC);

    @Override
    public MapCodec<? extends SigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean isToggleable() {
        return true;
    }

    @Override
    public void activeTick(Level level, Player player, ItemStack stack, int itemSlot, boolean isSelected) {
        if (level.isClientSide) {
            return;
        }

        BlockPos playerPos = player.blockPosition();
        AABB searchArea = new AABB(
                playerPos.getX() - range, playerPos.getY() - range, playerPos.getZ() - range,
                playerPos.getX() + range + 1, playerPos.getY() + range + 1, playerPos.getZ() + range + 1
        );

        Vec3 playerCenter = player.position().add(0, 0.5, 0);

        // Pull items
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, searchArea);
        for (ItemEntity item : items) {
            if (item.isRemoved() || item.hasPickUpDelay()) {
                continue;
            }

            Vec3 motion = playerCenter.subtract(item.position()).normalize().scale(pullSpeed);
            item.setDeltaMovement(item.getDeltaMovement().add(motion));
        }

        // Pull experience orbs
        List<ExperienceOrb> orbs = level.getEntitiesOfClass(ExperienceOrb.class, searchArea);
        for (ExperienceOrb orb : orbs) {
            if (orb.isRemoved()) {
                continue;
            }

            Vec3 motion = playerCenter.subtract(orb.position()).normalize().scale(pullSpeed);
            orb.setDeltaMovement(orb.getDeltaMovement().add(motion));
        }
    }
}
