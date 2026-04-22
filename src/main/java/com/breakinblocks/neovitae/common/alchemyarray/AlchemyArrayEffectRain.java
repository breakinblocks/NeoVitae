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
import net.minecraft.world.entity.EntitySpawnReason;

public class AlchemyArrayEffectRain extends AlchemyArrayEffect {

    private static final int START_TICK = 100;
    private static final int END_TICK = 200;
    private static final int LP_COST = 500;

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide()) return false;

        if (ticksActive < START_TICK) return false;

        if (ticksActive == START_TICK) {
            Binding binding = tile.getOwnerBinding();
            if (binding.isEmpty()) return true;

            Anima network = AnimaHelper.getAnima(binding);
            if (network == null || network.syphon(AnimaTicket.create(LP_COST)) < LP_COST) {
                return true;
            }

            tile.doDropIngredients(true);
        }

        if (ticksActive >= END_TICK) {
            if (level instanceof ServerLevel serverLevel) {
                net.minecraft.world.level.saveddata.WeatherData weather = serverLevel.getWeatherData();
                if (weather.isRaining()) {
                    weather.setClearWeatherTime(6000);
                    weather.setRainTime(0);
                    weather.setRaining(false);
                    weather.setThunderTime(0);
                    weather.setThundering(false);
                } else {
                    weather.setClearWeatherTime(0);
                    weather.setRainTime(24000);
                    weather.setRaining(true);
                }

                BlockPos pos = tile.getBlockPos();
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel, EntitySpawnReason.TRIGGERED);
                if (bolt != null) {
                    bolt.snapTo(Vec3.atBottomCenterOf(pos));
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
