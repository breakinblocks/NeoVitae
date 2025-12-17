package com.breakinblocks.neovitae.ritual.imperfect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.IImperfectRitualStone;
import com.breakinblocks.neovitae.ritual.ImperfectRitual;

/**
 * Imperfect ritual that summons a thunderstorm.
 * Requires a water source block above the ritual stone.
 * Costs 5000 LP.
 */
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
            // Summon a thunderstorm
            serverLevel.setWeatherParameters(0, 6000, true, true);
            return true;
        }

        return false;
    }
}
