package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.damagesource.BMDamageSources;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual that damages nearby players (with consent) to generate LP.
 */
public class RitualFeatheredKnife extends Ritual {

    public static final String SACRIFICE_RANGE = "sacrificeRange";

    private static final int LP_PER_HP = 100;

    public RitualFeatheredKnife() {
        super("feathered_knife", 0, 25000, "ritual." + NeoVitae.MODID + ".feathered_knife");
        addBlockRange(SACRIFICE_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -5, -5), 11, 11, 11));
        setMaximumVolumeAndDistanceOfRange(SACRIFICE_RANGE, 0, 10, 10);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        int maxLPGenerated = 1000000;

        List<Player> players = RitualHelper.getEntitiesInRange(ctx, this, SACRIFICE_RANGE, Player.class,
                player -> player.isAlive() && !player.isCreative() && !player.isSpectator() && player.getHealth() > 6.0F);

        int totalLP = 0;

        for (Player player : players) {
            float health = player.getHealth();
            if (health <= 6.0F) continue;

            float damage = Math.min(1.0F, health - 6.0F);
            player.hurt(ctx.level().damageSources().source(BMDamageSources.SACRIFICE, player), damage);

            if (player.getHealth() < health) {
                totalLP += (int) (damage * LP_PER_HP);
            }
        }

        if (totalLP > 0) {
            ctx.syphon(getRefreshCost());
            ctx.network().add(ctx.master().ticket(totalLP), maxLPGenerated);
        }
    }

    @Override
    public int getRefreshTime() {
        return 20;
    }

    @Override
    public int getRefreshCost() {
        return 20;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addParallelRunes(components, 1, 0, EnumRuneType.DUSK);
        addCornerRunes(components, 1, 0, EnumRuneType.AIR);
        addParallelRunes(components, 2, 0, EnumRuneType.AIR);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualFeatheredKnife();
    }
}
