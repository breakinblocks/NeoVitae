package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.item.BloodOrbItem;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.List;

public class AlchemyArrayEffectVortex extends AlchemyArrayEffect {

    private static final double RADIUS = 8.0;
    private static final double PULL_SPEED = 1.05;
    private static final double INNER_DEADZONE = 0.6;
    private static final double UPKEEP_CHANCE = 0.01;
    private static final int UPKEEP_COST = 1;

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide()) return false;

        BlockPos pos = tile.getBlockPos();
        if (level.hasNeighborSignal(pos)) return false;

        if (level.getRandom().nextDouble() < UPKEEP_CHANCE) {
            Binding binding = tile.getOwnerBinding();
            if (binding.isEmpty()) return false;
            Anima network = AnimaHelper.getAnima(binding);
            if (network == null || network.syphon(AnimaTicket.create(UPKEEP_COST)) < UPKEEP_COST) {
                return false;
            }
        }

        Vec3 center = Vec3.atCenterOf(pos.below());
        AABB area = new AABB(pos).inflate(RADIUS);

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);
        for (LivingEntity entity : entities) {
            if (entity instanceof Player p) {
                if (p.isCreative() || p.isSpectator()) continue;
                if (p.getMainHandItem().getItem() instanceof BloodOrbItem) continue;
                if (p.getOffhandItem().getItem() instanceof BloodOrbItem) continue;
            }

            Vec3 dir = center.subtract(entity.position());
            double dist = dir.length();
            if (dist < INNER_DEADZONE) continue;

            Vec3 pull = dir.normalize().scale(PULL_SPEED);
            entity.setDeltaMovement(pull);
            entity.hurtMarked = true;
            if (entity instanceof Player player) {
                player.hurtMarked = true;
            }
        }

        return false;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {}

    @Override
    public void readFromNBT(CompoundTag tag) {}

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectVortex();
    }
}
