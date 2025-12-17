package com.breakinblocks.neovitae.ritual.types;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.LivingStats;
import com.breakinblocks.neovitae.common.tag.BMTags;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Sound of the Cleansing Soul - Removes all upgrades from living armor.
 * Player must stand on the Master Ritual Stone wearing living armor.
 * This is a Dusk tier ritual.
 */
public class RitualUpgradeRemove extends Ritual {

    public RitualUpgradeRemove() {
        super("upgrade_remove", 1, 20000, "ritual." + NeoVitae.MODID + ".upgrade_remove");
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
            // Check all armor pieces for living armor
            boolean cleansedAny = false;

            for (int i = 0; i < player.getInventory().armor.size(); i++) {
                ItemStack armorPiece = player.getInventory().armor.get(i);
                if (armorPiece.isEmpty() || !armorPiece.is(BMTags.Items.LIVING_SET)) {
                    continue;
                }

                // Get living stats
                LivingStats stats = armorPiece.get(BMDataComponents.UPGRADES.get());
                if (stats == null || stats.upgrades().isEmpty()) {
                    continue;
                }

                // Remove all upgrades
                LivingStats newStats = new LivingStats(new Object2FloatOpenHashMap<>());
                armorPiece.set(BMDataComponents.UPGRADES.get(), newStats);

                // Reset used points
                armorPiece.set(BMDataComponents.CURRENT_UPGRADE_POINTS.get(), 0);

                cleansedAny = true;
            }

            if (cleansedAny) {
                // Consume LP and deactivate
                ctx.syphon(getRefreshCost());
                masterRitualStone.stopRitual(BreakType.DEACTIVATE);
                return;
            }
        }
    }

    @Override
    public int getRefreshTime() {
        return 20;
    }

    @Override
    public int getRefreshCost() {
        return 10000;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.DUSK);
        addParallelRunes(components, 2, 0, EnumRuneType.WATER);
        addCornerRunes(components, 2, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 3, 0, EnumRuneType.DUSK);
        addCornerRunes(components, 3, 0, EnumRuneType.EARTH);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualUpgradeRemove();
    }
}
