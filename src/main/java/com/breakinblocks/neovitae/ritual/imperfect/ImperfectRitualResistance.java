package com.breakinblocks.neovitae.ritual.imperfect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.IImperfectRitualStone;
import com.breakinblocks.neovitae.ritual.ImperfectRitual;

/**
 * Imperfect ritual that grants Fire Resistance II for 1 minute.
 * Requires the ritual stone to be placed UNDER bedrock (in the Nether).
 * This is unique as it checks the block above, but doesn't consume it.
 * Costs 5000 LP.
 */
public class ImperfectRitualResistance extends ImperfectRitual {

    public ImperfectRitualResistance() {
        super("resistance",
                state -> state.is(Blocks.BEDROCK),
                5000,
                false,
                "ritual." + NeoVitae.MODID + ".imperfect.resistance");
    }

    @Override
    public boolean onActivate(IImperfectRitualStone imperfectRitualStone, Player player) {
        Level level = imperfectRitualStone.getRitualWorld();
        if (level == null || level.isClientSide()) return false;

        // Grant Fire Resistance II (amplifier 1) for 1 minute (1200 ticks)
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 1));
        return true;
    }
}
