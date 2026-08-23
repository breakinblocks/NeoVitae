// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

public class RitualCondor extends Ritual {

    public static final String FLIGHT_RANGE = "flightRange";
    private static final int FLIGHT_DURATION_TICKS = 12000;

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
            player.addEffect(new MobEffectInstance(NVMobEffects.FLIGHT, FLIGHT_DURATION_TICKS, 0, true, false));
            totalCost += getRefreshCost();
            RitualHelper.chanceStream(ctx.level(), 80, () ->
                    StreamPresets.emberMote(player.blockPosition().above()).build()
                            .sendToNearby(ctx.serverLevel(), player.blockPosition(), 128));
        }

        if (totalCost > 0) {
            ctx.syphon(Math.min(totalCost, ctx.currentEV()));
        }
    }

    @Override
    public void stopRitual(IMasterRitualStone masterRitualStone, BreakType breakType) {
        Level level = masterRitualStone.getLevel();
        if (level == null || level.isClientSide()) return;

        BlockPos masterPos = masterRitualStone.getBlockPos();
        AreaDescriptor range = RitualHelper.getEffectiveRange(masterRitualStone, this, FLIGHT_RANGE);

        AABB aabb = range.getAABB(masterPos);
        List<Player> players = level.getEntitiesOfClass(Player.class, aabb);
        for (Player player : players) {
            player.removeEffect(NVMobEffects.FLIGHT);
            // Attribute modifier handles mayfly permission; we just need to stop active flight
            if (!player.isCreative() && !player.isSpectator()) {
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        }
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.AIR);
        addParallelRunes(components, 2, 0, EnumRuneType.AIR);
        addCornerRunes(components, 2, 0, EnumRuneType.AIR);
        addParallelRunes(components, 3, 0, EnumRuneType.TENEBRAE);
        addCornerRunes(components, 3, 0, EnumRuneType.AIR);
        addParallelRunes(components, 4, 0, EnumRuneType.AIR);
        addCornerRunes(components, 4, 0, EnumRuneType.TENEBRAE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualCondor();
    }
}
