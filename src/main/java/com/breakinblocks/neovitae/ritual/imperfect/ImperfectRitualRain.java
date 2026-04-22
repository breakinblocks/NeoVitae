package com.breakinblocks.neovitae.ritual.imperfect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.IImperfectRitualStone;
import com.breakinblocks.neovitae.ritual.ImperfectRitual;

public class ImperfectRitualRain extends ImperfectRitual {

    public ImperfectRitualRain() {
        super("rain",
                state -> state.is(Blocks.WATER),
                5000,
                false,
                "ritual." + NeoVitae.MODID + ".imperfect.rain");
    }

    @Override
    public boolean onActivate(IImperfectRitualStone imperfectRitualStone, Player player) {
        Level level = imperfectRitualStone.getRitualWorld();
        if (level == null || level.isClientSide()) return false;

        if (level instanceof ServerLevel serverLevel) {
            net.minecraft.world.level.saveddata.WeatherData weather = serverLevel.getWeatherData();
            weather.setClearWeatherTime(0);
            weather.setRainTime(24000);
            weather.setRaining(true);
            weather.setThunderTime(0);
            weather.setThundering(false);
            return true;
        }

        return false;
    }
}
