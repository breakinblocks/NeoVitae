// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.network.chat.Component;
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
 * Ritual of Sentient Evolution - Allows living armor to evolve and gain max upgrade points.
 * Player must stand on the Master Ritual Stone wearing living armor.
 * This is a Tenebrae tier ritual.
 */
public class RitualSentientArmourEvolve extends Ritual {

    public static final int POINTS_PER_EVOLUTION = 100;
    public static final int MAX_UPGRADE_POINTS = 500;

    public RitualSentientArmourEvolve() {
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
            if (chestpiece.isEmpty() || !chestpiece.is(NVTags.Items.SENTIENT_SET)) {
                continue;
            }

            Integer currentMaxPoints = chestpiece.get(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS.get());
            if (currentMaxPoints == null) {
                currentMaxPoints = NeoVitae.SERVER_CONFIG.DEFAULT_UPGRADE_POINTS.get();
            }

            int newMaxPoints = currentMaxPoints + POINTS_PER_EVOLUTION;

            if (newMaxPoints > MAX_UPGRADE_POINTS) {
                player.sendOverlayMessage(Component.translatable("chat." + NeoVitae.MODID + ".armour_evolve.maxed"));
                masterRitualStone.stopRitual(BreakType.DEACTIVATE);
                return;
            }

            chestpiece.set(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS.get(), newMaxPoints);
            player.sendOverlayMessage(Component.translatable("chat." + NeoVitae.MODID + ".armour_evolve.evolved", newMaxPoints));

            StreamPresets.soulSiphon(player, ctx.masterPos()).build()
                    .sendToNearby(ctx.serverLevel(), ctx.masterPos(), 128);

            ctx.syphon(getRefreshCost());
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            return;
        }
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.TENEBRAE);
        addParallelRunes(components, 2, 0, EnumRuneType.FIRE);
        addCornerRunes(components, 2, 0, EnumRuneType.TENEBRAE);
        addParallelRunes(components, 3, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 3, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 4, 0, EnumRuneType.TENEBRAE);
        addCornerRunes(components, 4, 0, EnumRuneType.AIR);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualSentientArmourEvolve();
    }
}
