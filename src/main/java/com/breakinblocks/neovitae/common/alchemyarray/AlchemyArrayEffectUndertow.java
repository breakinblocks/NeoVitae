package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;

/**
 * Undertow Array: creates a bubble-column-like current in the open column
 * directly above the array. Acts as a smooth continuous lift or drop,
 * independent of water. Right-clicking with an empty hand toggles between
 * upward (lift) and downward (drag) modes.
 */
public class AlchemyArrayEffectUndertow extends AlchemyArrayEffect {

    private boolean dragDown = false;

    public boolean isDragDown() {
        return dragDown;
    }

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null) return false;

        BlockPos pos = tile.getBlockPos();

        int topY = pos.getY() + 1;
        for (int i = 1; i <= 64; i++) {
            BlockPos check = pos.above(i);
            if (!level.getBlockState(check).getCollisionShape(level, check).isEmpty()) break;
            topY = check.getY() + 1;
        }

        AABB columnAabb = new AABB(
                pos.getX() + 0.05, pos.getY() + 0.0625, pos.getZ() + 0.05,
                pos.getX() + 0.95, topY,                pos.getZ() + 0.95);

        int kelpCount = tile.getItem(0).is(Items.KELP) ? tile.getItem(0).getCount() : 0;
        int redstoneCount = tile.getItem(1).is(Items.REDSTONE) ? tile.getItem(1).getCount() : 0;
        double accel = (dragDown ? 0.05 : 0.12) + 0.003 * redstoneCount;
        double maxVel = (dragDown ? 0.9 : 1.8) + 0.015 * kelpCount;

        boolean isClient = level.isClientSide;
        for (Entity entity : level.getEntitiesOfClass(Entity.class, columnAabb, Entity::isAlive)) {
            if (entity instanceof Player player) {
                if (!isClient || !player.isLocalPlayer()) continue;
            } else {
                if (isClient) continue;
            }
            entity.fallDistance = 0;
            Vec3 motion = entity.getDeltaMovement();
            double newY;
            if (dragDown) {
                newY = Math.max(-maxVel, motion.y - accel);
            } else {
                newY = Math.min(maxVel, motion.y + accel);
            }
            entity.setDeltaMovement(motion.x, newY, motion.z);
        }

        if (level instanceof ServerLevel serverLevel && ticksActive % 4 == 0) {
            double height = Math.max(0.5, topY - pos.getY() - 1);
            int color = dragDown ? 0x2233AA : 0x22AAEE;
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), color),
                    pos.getX() + 0.5, pos.getY() + 0.5 + height * 0.5, pos.getZ() + 0.5,
                    6, 0.3, height * 0.4, 0.3, 0.04);
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), color),
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    3, 0.2, height * 0.3, 0.2, 0.05);
        }

        return false;
    }

    @Override
    public boolean onUse(AlchemyArrayBlockEntity tile, Player player) {
        dragDown = !dragDown;
        Level level = tile.getLevel();
        if (level != null && !level.isClientSide) {
            BlockPos pos = tile.getBlockPos();
            level.playSound(null, pos,
                    dragDown ? SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT : SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT,
                    SoundSource.BLOCKS, 0.7f, 1.0f);
            player.displayClientMessage(Component.translatable(
                    dragDown ? "chat.neovitae.undertow.downward" : "chat.neovitae.undertow.upward"), true);
            tile.setChanged();
        }
        return true;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.putBoolean("dragDown", dragDown);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        dragDown = tag.getBoolean("dragDown");
    }

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectUndertow();
    }
}
