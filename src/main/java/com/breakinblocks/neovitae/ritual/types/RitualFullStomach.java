package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual that replenishes hunger for nearby players.
 */
public class RitualFullStomach extends Ritual {

    public static final String HUNGER_RANGE = "hungerRange";

    public RitualFullStomach() {
        super("full_stomach", 0, 1000, "ritual." + NeoVitae.MODID + ".full_stomach");
        addBlockRange(HUNGER_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-10, -5, -10), 21, 11, 21));
        setMaximumVolumeAndDistanceOfRange(HUNGER_RANGE, 5000, 20, 20);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        List<Player> players = RitualHelper.getEntitiesInRange(ctx, this, HUNGER_RANGE, Player.class);

        int playersFed = 0;

        for (Player player : players) {
            int hunger = player.getFoodData().getFoodLevel();
            if (hunger < 20) {
                int toFeed = Math.min(20 - hunger, 4);
                player.getFoodData().eat(toFeed, 0.5f);
                playersFed++;
            }
        }

        ctx.syphon(getRefreshCost() * playersFed);
    }

    @Override
    public int getRefreshTime() {
        return 40;
    }

    @Override
    public int getRefreshCost() {
        return 100;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.WATER);
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 0, EnumRuneType.WATER);
        addRune(components, 3, 0, 0, EnumRuneType.FIRE);
        addRune(components, -3, 0, 0, EnumRuneType.FIRE);
        addRune(components, 0, 0, 3, EnumRuneType.FIRE);
        addRune(components, 0, 0, -3, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualFullStomach();
    }
}
