package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual that provides regeneration to nearby players.
 */
public class RitualRegeneration extends Ritual {

    public static final String HEAL_RANGE = "healRange";

    public RitualRegeneration() {
        super("regeneration", 0, 500, "ritual." + NeoVitae.MODID + ".regeneration");
        addBlockRange(HEAL_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -5, -5), 11, 11, 11));
        setMaximumVolumeAndDistanceOfRange(HEAL_RANGE, 2000, 15, 15);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        List<Player> players = RitualHelper.getEntitiesInRange(ctx, this, HEAL_RANGE, Player.class);

        int cost = 0;
        for (Player player : players) {
            if (player.getHealth() < player.getMaxHealth()) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, false));
                cost += getRefreshCost();
            }
        }

        if (cost > 0) {
            ctx.syphon(Math.min(cost, ctx.currentEssence()));
        }
    }

    @Override
    public int getRefreshTime() {
        return 40;
    }

    @Override
    public int getRefreshCost() {
        return 50;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.WATER);
        addParallelRunes(components, 2, 0, EnumRuneType.WATER);
        addCornerRunes(components, 2, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 3, 0, EnumRuneType.EARTH);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualRegeneration();
    }
}
