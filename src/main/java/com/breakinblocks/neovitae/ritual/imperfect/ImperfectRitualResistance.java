// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.imperfect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.IImperfectRitualStone;
import com.breakinblocks.neovitae.ritual.ImperfectRitual;

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

        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 1));
        return true;
    }
}
