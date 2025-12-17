package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.tag.BMTags;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual of Living Evolution - Allows living armor to evolve and gain max upgrade points.
 * Player must stand on the Master Ritual Stone wearing living armor.
 * This is a Dusk tier ritual.
 */
public class RitualArmourEvolve extends Ritual {

    public RitualArmourEvolve() {
        super("armour_evolve", 1, 50000, "ritual." + NeoVitae.MODID + ".armour_evolve");
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) {
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            return;
        }

        // Check for player standing on the ritual
        AABB checkArea = new AABB(ctx.masterPos()).inflate(1, 2, 1);
        List<Player> players = ctx.level().getEntitiesOfClass(Player.class, checkArea);

        for (Player player : players) {
            // Check if wearing living armor (chestplate)
            ItemStack chestpiece = player.getInventory().armor.get(2);
            if (chestpiece.isEmpty() || !chestpiece.is(BMTags.Items.LIVING_SET)) {
                continue;
            }

            // Get current max points
            Integer currentMaxPoints = chestpiece.get(BMDataComponents.CURRENT_MAX_UPGRADE_POINTS.get());
            if (currentMaxPoints == null) {
                currentMaxPoints = 100; // Default starting max
            }

            int newMaxPoints = currentMaxPoints + 100; // Add 100 max upgrade points

            // Cap at reasonable maximum
            if (newMaxPoints > 500) {
                continue; // Already at max evolution
            }

            // Evolve the armor
            chestpiece.set(BMDataComponents.CURRENT_MAX_UPGRADE_POINTS.get(), newMaxPoints);

            // Consume LP and deactivate
            ctx.syphon(getRefreshCost());
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            return;
        }
    }

    @Override
    public int getRefreshTime() {
        return 20;
    }

    @Override
    public int getRefreshCost() {
        return 25000;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.DUSK);
        addParallelRunes(components, 2, 0, EnumRuneType.FIRE);
        addCornerRunes(components, 2, 0, EnumRuneType.DUSK);
        addParallelRunes(components, 3, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 3, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 4, 0, EnumRuneType.DUSK);
        addCornerRunes(components, 4, 0, EnumRuneType.AIR);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualArmourEvolve();
    }
}
