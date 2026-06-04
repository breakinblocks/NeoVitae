package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

public class AlchemyArrayEffectRain extends AlchemyArrayEffect {

    private static final int START_TICK = 100;
    private static final int END_TICK = 200;

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide) return false;

        if (ticksActive < START_TICK) return false;

        if (ticksActive == START_TICK) {
            Binding binding = tile.getOwnerBinding();
            if (binding.isEmpty()) return true;

            Anima network = AnimaHelper.getAnima(binding);
            int cost = getEvCost();
            if (network == null || (cost > 0 && network.syphon(AnimaTicket.create(cost)) < cost)) {
                return true;
            }

            tile.doDropIngredients(true);
        }

        if (ticksActive >= END_TICK) {
            if (level instanceof ServerLevel serverLevel) {
                boolean isRaining = serverLevel.isRaining();
                if (isRaining) {
                    serverLevel.getServer().overworld().setWeatherParameters(6000 + level.random.nextInt(12000), 0, false, false);
                } else {
                    serverLevel.getServer().overworld().setWeatherParameters(0, 6000 + level.random.nextInt(12000), true, false);
                }

                BlockPos pos = tile.getBlockPos();
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
                if (bolt != null) {
                    bolt.moveTo(Vec3.atBottomCenterOf(pos));
                    bolt.setVisualOnly(true);
                    serverLevel.addFreshEntity(bolt);
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {}

    @Override
    public void readFromNBT(CompoundTag tag) {}

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectRain();
    }
}
