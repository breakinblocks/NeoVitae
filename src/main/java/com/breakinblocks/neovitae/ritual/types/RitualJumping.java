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
 * Ritual that grants jump boost to nearby players.
 */
public class RitualJumping extends Ritual {

    public static final String JUMP_RANGE = "jumpRange";

    public RitualJumping() {
        super("jumping", 0, 500, "ritual." + NeoVitae.MODID + ".jumping");
        addBlockRange(JUMP_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -5, -5), 11, 11, 11));
        setMaximumVolumeAndDistanceOfRange(JUMP_RANGE, 2000, 15, 15);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        List<Player> players = RitualHelper.getEntitiesInRange(ctx, this, JUMP_RANGE, Player.class);

        int cost = 0;
        for (Player player : players) {
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 100, 1, true, false));
            cost += getRefreshCost();
        }

        if (cost > 0) {
            ctx.syphon(Math.min(cost, ctx.currentEssence()));
        }
    }

    @Override
    public int getRefreshTime() {
        return 60;
    }

    @Override
    public int getRefreshCost() {
        return 20;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.AIR);
        addParallelRunes(components, 2, 0, EnumRuneType.AIR);
        addRune(components, 0, 0, 3, EnumRuneType.EARTH);
        addRune(components, 0, 0, -3, EnumRuneType.EARTH);
        addRune(components, 3, 0, 0, EnumRuneType.EARTH);
        addRune(components, -3, 0, 0, EnumRuneType.EARTH);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualJumping();
    }
}
