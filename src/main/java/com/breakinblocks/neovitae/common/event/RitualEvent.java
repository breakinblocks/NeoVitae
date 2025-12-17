package com.breakinblocks.neovitae.common.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneTile;
import com.breakinblocks.neovitae.ritual.Ritual;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Base event for ritual-related events.
 * Allows mods to hook into ritual activation, performance, and deactivation.
 */
public abstract class RitualEvent extends Event {
    private final MasterRitualStoneTile masterRitualStone;
    private final Ritual ritual;

    public RitualEvent(MasterRitualStoneTile masterRitualStone, Ritual ritual) {
        this.masterRitualStone = masterRitualStone;
        this.ritual = ritual;
    }

    public MasterRitualStoneTile getMasterRitualStone() {
        return masterRitualStone;
    }

    public Ritual getRitual() {
        return ritual;
    }

    public Level getLevel() {
        return masterRitualStone.getLevel();
    }

    public BlockPos getPos() {
        return masterRitualStone.getBlockPos();
    }

    @Nullable
    public UUID getOwnerUUID() {
        return masterRitualStone.getOwner();
    }

    /**
     * Fired when a ritual is about to be activated.
     * Cancel to prevent activation.
     */
    public static class Activate extends RitualEvent implements ICancellableEvent {
        private final Player player;
        private final int crystalLevel;

        public Activate(MasterRitualStoneTile masterRitualStone, Ritual ritual, Player player, int crystalLevel) {
            super(masterRitualStone, ritual);
            this.player = player;
            this.crystalLevel = crystalLevel;
        }

        /**
         * The player attempting to activate the ritual.
         */
        public Player getPlayer() {
            return player;
        }

        /**
         * The crystal level used for activation (0 = weak, 1 = standard, 2 = awakened).
         */
        public int getCrystalLevel() {
            return crystalLevel;
        }
    }

    /**
     * Fired after a ritual has been successfully activated.
     * Not cancellable - use for notification purposes only.
     */
    public static class Activated extends RitualEvent {
        private final Player player;

        public Activated(MasterRitualStoneTile masterRitualStone, Ritual ritual, Player player) {
            super(masterRitualStone, ritual);
            this.player = player;
        }

        public Player getPlayer() {
            return player;
        }
    }

    /**
     * Fired when a ritual is about to stop.
     * Cannot be cancelled - use for cleanup/notification purposes.
     */
    public static class Stop extends RitualEvent {
        private final Ritual.BreakType breakType;

        public Stop(MasterRitualStoneTile masterRitualStone, Ritual ritual, Ritual.BreakType breakType) {
            super(masterRitualStone, ritual);
            this.breakType = breakType;
        }

        /**
         * The reason the ritual is stopping.
         */
        public Ritual.BreakType getBreakType() {
            return breakType;
        }
    }

    /**
     * Fired before each ritual performance tick.
     * Cancel to skip this performance cycle.
     */
    public static class Perform extends RitualEvent implements ICancellableEvent {
        public Perform(MasterRitualStoneTile masterRitualStone, Ritual ritual) {
            super(masterRitualStone, ritual);
        }
    }
}
