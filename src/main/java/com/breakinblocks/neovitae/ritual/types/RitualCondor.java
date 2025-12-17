package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.effect.BMMobEffects;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Reverence of the Condor - Grants creative-style flight to players in the area.
 * This is a Dusk tier ritual (requires awakened activation crystal).
 */
public class RitualCondor extends Ritual {

    public static final String FLIGHT_RANGE = "flightRange";

    public RitualCondor() {
        super("condor", 1, 10000, "ritual." + NeoVitae.MODID + ".condor");
        addBlockRange(FLIGHT_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-25, -25, -25), 51, 51, 51));
        setMaximumVolumeAndDistanceOfRange(FLIGHT_RANGE, 0, 50, 50);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) {
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            return;
        }

        List<Player> players = RitualHelper.getEntitiesInRange(ctx, this, FLIGHT_RANGE, Player.class);

        int totalCost = 0;
        for (Player player : players) {
            // Apply flight effect - lasts slightly longer than refresh time to prevent flickering
            player.addEffect(new MobEffectInstance(BMMobEffects.FLIGHT, 40, 0, true, false));
            totalCost += getRefreshCost();
        }

        if (totalCost > 0) {
            ctx.syphon(Math.min(totalCost, ctx.currentEssence()));
        }
    }

    @Override
    public void stopRitual(IMasterRitualStone masterRitualStone, BreakType breakType) {
        Level level = masterRitualStone.getLevel();
        if (level == null || level.isClientSide()) return;

        BlockPos masterPos = masterRitualStone.getBlockPos();
        AreaDescriptor range = RitualHelper.getEffectiveRange(masterRitualStone, this, FLIGHT_RANGE);

        // Remove flight effect from players when ritual stops
        AABB aabb = range.getAABB(masterPos);
        List<Player> players = level.getEntitiesOfClass(Player.class, aabb);
        for (Player player : players) {
            player.removeEffect(BMMobEffects.FLIGHT);
            // Disable active flying when effect is removed (attribute modifier handles mayfly permission)
            if (!player.isCreative() && !player.isSpectator()) {
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        }
    }

    @Override
    public int getRefreshTime() {
        return 20; // Every second
    }

    @Override
    public int getRefreshCost() {
        return 100; // Cost per player per second
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        // Dusk tier ritual - complex rune pattern
        addCornerRunes(components, 1, 0, EnumRuneType.AIR);
        addParallelRunes(components, 2, 0, EnumRuneType.AIR);
        addCornerRunes(components, 2, 0, EnumRuneType.AIR);
        addParallelRunes(components, 3, 0, EnumRuneType.DUSK);
        addCornerRunes(components, 3, 0, EnumRuneType.AIR);
        addParallelRunes(components, 4, 0, EnumRuneType.AIR);
        addCornerRunes(components, 4, 0, EnumRuneType.DUSK);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualCondor();
    }
}
