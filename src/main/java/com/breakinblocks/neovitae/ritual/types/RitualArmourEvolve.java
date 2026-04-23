package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.entity.EquipmentSlot;

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

        AABB checkArea = new AABB(ctx.masterPos()).inflate(1, 2, 1);
        List<Player> players = ctx.level().getEntitiesOfClass(Player.class, checkArea);

        for (Player player : players) {
            ItemStack chestpiece = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chestpiece.isEmpty() || !chestpiece.is(NVTags.Items.LIVING_SET)) {
                continue;
            }

            // Get current max points
            Integer currentMaxPoints = chestpiece.get(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS.get());
            if (currentMaxPoints == null) {
                currentMaxPoints = 100; // Default starting max
            }

            int newMaxPoints = currentMaxPoints + 100;

            // Cap at reasonable maximum
            if (newMaxPoints > 500) {
                continue; // Already at max evolution
            }

            chestpiece.set(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS.get(), newMaxPoints);

            StreamPresets.soulSiphon(player, ctx.masterPos()).build()
                    .sendToNearby(ctx.serverLevel(), ctx.masterPos(), 128);

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
