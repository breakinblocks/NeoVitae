// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SentientStats;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

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

            for (int i = 0; i < player.getInventory().armor.size(); i++) {
                ItemStack armorPiece = player.getInventory().armor.get(i);
                if (armorPiece.isEmpty() || !armorPiece.is(NVTags.Items.SENTIENT_SET)) {
                    continue;
                }

                SentientStats stats = armorPiece.get(NVDataComponents.UPGRADES.get());
                if (stats == null || stats.upgrades().isEmpty()) {
                    continue;
                }

                // Remove all upgrades
                SentientStats newStats = new SentientStats(new Object2FloatOpenHashMap<>());
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
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.TENEBRAE);
        addParallelRunes(components, 2, 0, EnumRuneType.WATER);
        addCornerRunes(components, 2, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 3, 0, EnumRuneType.TENEBRAE);
        addCornerRunes(components, 3, 0, EnumRuneType.EARTH);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualUpgradeRemove();
    }
}
