package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.effect.BMMobEffects;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * The Sinner's Burden - Prevents flight for entities in the area.
 * Applies Heavy Heart debuff which drags players down and prevents flight.
 */
public class RitualGrounding extends Ritual {

    public static final String GROUNDING_RANGE = "groundingRange";

    public RitualGrounding() {
        super("grounding", 0, 2000, "ritual." + NeoVitae.MODID + ".grounding");
        addBlockRange(GROUNDING_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-15, -15, -15), 31, 31, 31));
        setMaximumVolumeAndDistanceOfRange(GROUNDING_RANGE, 0, 30, 30);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) {
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            return;
        }

        List<Player> players = RitualHelper.getEntitiesInRange(ctx, this, GROUNDING_RANGE, Player.class);

        int totalCost = 0;
        for (Player player : players) {
            // Skip the ritual owner and creative/spectator players
            if (player.getUUID().equals(ctx.master().getOwner()) ||
                player.isCreative() || player.isSpectator()) {
                continue;
            }

            // Apply Heavy Heart effect - drags down and prevents flight
            player.addEffect(new MobEffectInstance(BMMobEffects.HEAVY_HEART, 30, 0, true, true));
            totalCost += getRefreshCost();
        }

        if (totalCost > 0) {
            ctx.syphon(Math.min(totalCost, ctx.currentEssence()));
        }
    }

    @Override
    public int getRefreshTime() {
        return 10;
    }

    @Override
    public int getRefreshCost() {
        return 10;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 3, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 3, 0, EnumRuneType.EARTH);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualGrounding();
    }
}
