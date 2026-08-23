package com.breakinblocks.neovitae.compat.ae2;

import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;

public final class AE2Compat {

    private static boolean available;

    private AE2Compat() {
    }

    public static void init() {
        available = ModList.get().isLoaded("ae2");
        if (available) {
            NeoVitae.LOGGER.info("Applied Energistics 2 found, terminals can be bound with the Sigil of Bound Treasures");
        }
    }

    public static boolean isTerminal(Level level, BlockPos pos) {
        if (!available) {
            return false;
        }
        try {
            return AE2Hooks.hasTerminal(level, pos);
        } catch (Throwable t) {
            disable(t);
            return false;
        }
    }

    public static Direction findTerminalSide(Level level, BlockPos pos, Vec3 hit) {
        if (!available) {
            return null;
        }
        try {
            return AE2Hooks.findTerminalSide(level, pos, hit);
        } catch (Throwable t) {
            disable(t);
            return null;
        }
    }

    public static boolean openTerminal(ServerPlayer player, Level level, BlockPos pos, Direction preferred) {
        if (!available) {
            return false;
        }
        try {
            return AE2Hooks.openTerminal(player, level, pos, preferred);
        } catch (Throwable t) {
            disable(t);
            return false;
        }
    }

    private static void disable(Throwable t) {
        available = false;
        NeoVitae.LOGGER.warn("Applied Energistics 2 terminal lookup failed, skipping AE2 terminals from now on", t);
    }
}
