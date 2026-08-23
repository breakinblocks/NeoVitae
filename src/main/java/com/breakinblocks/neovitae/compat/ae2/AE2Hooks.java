package com.breakinblocks.neovitae.compat.ae2;

import appeng.api.parts.IPartHost;
import appeng.api.parts.SelectedPart;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.reporting.AbstractTerminalPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

final class AE2Hooks {

    private AE2Hooks() {
    }

    static boolean hasTerminal(Level level, BlockPos pos) {
        return findTerminal(level, pos, null) != null;
    }

    static Direction findTerminalSide(Level level, BlockPos pos, Vec3 hit) {
        if (!(level.getBlockEntity(pos) instanceof IPartHost host)) {
            return null;
        }
        SelectedPart selected = host.selectPartWorld(hit);
        if (selected != null && selected.part instanceof AbstractTerminalPart && selected.side != null) {
            return selected.side;
        }
        for (Direction side : Direction.values()) {
            if (host.getPart(side) instanceof AbstractTerminalPart) {
                return side;
            }
        }
        return null;
    }

    static boolean openTerminal(ServerPlayer player, Level level, BlockPos pos, Direction preferred) {
        AbstractTerminalPart terminal = findTerminal(level, pos, preferred);
        if (terminal == null) {
            return false;
        }
        MenuType<?> menuType = terminal.getMenuType(player);
        if (menuType == null) {
            return false;
        }
        return MenuOpener.open(menuType, player, MenuLocators.forPart(terminal));
    }

    private static AbstractTerminalPart findTerminal(Level level, BlockPos pos, Direction preferred) {
        if (!(level.getBlockEntity(pos) instanceof IPartHost host)) {
            return null;
        }
        if (preferred != null && host.getPart(preferred) instanceof AbstractTerminalPart terminal) {
            return terminal;
        }
        for (Direction side : Direction.values()) {
            if (host.getPart(side) instanceof AbstractTerminalPart terminal) {
                return terminal;
            }
        }
        return null;
    }
}
