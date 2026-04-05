package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;

import java.util.List;

/**
 * Alchemy array effect that pushes entities upward through a column of open air
 * above the array, behaving like a stronger vanilla bubble column.
 * <p>
 * Acceleration scales with feathers in slot 0 and the velocity cap scales with
 * glowstone dust in slot 1.
 */
public class AlchemyArrayEffectUpdraft extends AlchemyArrayEffect {

    public AlchemyArrayEffectUpdraft() {
    }

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide) return false;

        BlockPos pos = tile.getBlockPos();

        int featherCount = tile.getItem(0).is(Items.FEATHER) ? tile.getItem(0).getCount() : 0;
        int glowstoneCount = tile.getItem(1).is(Items.GLOWSTONE_DUST) ? tile.getItem(1).getCount() : 0;
        double accel = 0.15 + 0.005 * featherCount;
        double maxVel = 1.2 + 0.02 * glowstoneCount;

        int topY = pos.getY() + 1;
        for (int i = 1; i <= 64; i++) {
            BlockPos check = pos.above(i);
            if (!level.getBlockState(check).getCollisionShape(level, check).isEmpty()) break;
            topY = check.getY() + 1;
        }

        AABB columnAabb = new AABB(
                pos.getX() + 0.05, pos.getY() + 0.0625, pos.getZ() + 0.05,
                pos.getX() + 0.95, topY,                pos.getZ() + 0.95);

        List<Entity> entities = level.getEntitiesOfClass(Entity.class, columnAabb, Entity::isAlive);
        for (Entity entity : entities) {
            entity.fallDistance = 0;
            Vec3 motion = entity.getDeltaMovement();
            double newY = Math.min(maxVel, motion.y + accel);
            entity.setDeltaMovement(motion.x, newY, motion.z);
            entity.hasImpulse = true;
            if (entity instanceof ServerPlayer sp) {
                sp.connection.send(new ClientboundSetEntityMotionPacket(entity));
            }
        }

        if (level instanceof ServerLevel serverLevel && ticksActive % 3 == 0) {
            double height = Math.max(0.5, topY - pos.getY() - 1);
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0xFFCC22),
                    pos.getX() + 0.5, pos.getY() + 0.5 + height * 0.5, pos.getZ() + 0.5,
                    8, 0.3, height * 0.5, 0.3, 0.05);
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xFFEE44),
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    3, 0.15, height * 0.3, 0.15, 0.04);
        }

        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(AlchemyArrayBlockEntity tile, Level level, BlockPos pos, BlockState state, Entity entity) {
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
    }

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectUpdraft();
    }
}
