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
 * Ritual that grants speed boost to nearby players.
 */
public class RitualSpeed extends Ritual {

    public static final String SPEED_RANGE = "speedRange";

    public RitualSpeed() {
        super("speed", 0, 500, "ritual." + NeoVitae.MODID + ".speed");
        addBlockRange(SPEED_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -5, -5), 11, 11, 11));
        setMaximumVolumeAndDistanceOfRange(SPEED_RANGE, 2000, 15, 15);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        List<Player> players = RitualHelper.getEntitiesInRange(ctx, this, SPEED_RANGE, Player.class);

        int cost = 0;
        for (Player player : players) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1, true, false));
            cost += getRefreshCost();
        }

        ctx.syphon(cost);
    }

    @Override
    public int getRefreshTime() {
        return 60;
    }

    @Override
    public int getRefreshCost() {
        return 25;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.AIR);
        addParallelRunes(components, 2, 0, EnumRuneType.AIR);
        addCornerRunes(components, 2, 0, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualSpeed();
    }
}
