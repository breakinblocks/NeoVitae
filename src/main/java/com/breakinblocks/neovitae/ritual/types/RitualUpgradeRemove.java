package com.breakinblocks.neovitae.ritual.types;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.LivingStats;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.entity.EquipmentSlot;

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

        AABB checkArea = new AABB(ctx.masterPos()).inflate(1, 2, 1);
        List<Player> players = ctx.level().getEntitiesOfClass(Player.class, checkArea);

        for (Player player : players) {
            boolean cleansedAny = false;

            for (EquipmentSlot slot : EquipmentSlot.VALUES) {
                if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
                ItemStack armorPiece = player.getItemBySlot(slot);
                if (armorPiece.isEmpty() || !armorPiece.is(NVTags.Items.LIVING_SET)) {
                    continue;
                }

                // Get living stats
                LivingStats stats = armorPiece.get(NVDataComponents.UPGRADES.get());
                if (stats == null || stats.upgrades().isEmpty()) {
                    continue;
                }

                // Remove all upgrades
                LivingStats newStats = new LivingStats(new Object2FloatOpenHashMap<>());
                armorPiece.set(NVDataComponents.UPGRADES.get(), newStats);

                // Reset used points
                armorPiece.set(NVDataComponents.CURRENT_UPGRADE_POINTS.get(), 0);

                cleansedAny = true;
            }

            if (cleansedAny) {
                ctx.syphon(getRefreshCost());
                com.breakinblocks.neovitae.api.stream.StreamPresets
                        .lifePulse(ctx.masterPos(), player.blockPosition()).build()
                        .sendToNearby(ctx.serverLevel(), ctx.masterPos(), 128);
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
