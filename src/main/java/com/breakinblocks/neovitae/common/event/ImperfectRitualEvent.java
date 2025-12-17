package com.breakinblocks.neovitae.common.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import com.breakinblocks.neovitae.common.blockentity.TileImperfectRitualStone;
import com.breakinblocks.neovitae.common.datamap.ImperfectRitualStats;
import com.breakinblocks.neovitae.ritual.ImperfectRitual;

import javax.annotation.Nullable;

/**
 * Base event for imperfect ritual-related events.
 * Allows mods to hook into imperfect ritual activation.
 */
public abstract class ImperfectRitualEvent extends Event {
    private final TileImperfectRitualStone ritualStone;
    private final ImperfectRitual ritual;
    private final Player player;
    @Nullable
    private final ImperfectRitualStats stats;

    public ImperfectRitualEvent(TileImperfectRitualStone ritualStone, ImperfectRitual ritual,
                                Player player, @Nullable ImperfectRitualStats stats) {
        this.ritualStone = ritualStone;
        this.ritual = ritual;
        this.player = player;
        this.stats = stats;
    }

    public TileImperfectRitualStone getRitualStone() {
        return ritualStone;
    }

    public ImperfectRitual getRitual() {
        return ritual;
    }

    public Player getPlayer() {
        return player;
    }

    @Nullable
    public ImperfectRitualStats getStats() {
        return stats;
    }

    public Level getLevel() {
        return ritualStone.getLevel();
    }

    public BlockPos getPos() {
        return ritualStone.getBlockPos();
    }

    /**
     * Gets the activation cost, considering data-driven stats.
     */
    public int getActivationCost() {
        return stats != null ? stats.activationCost() : ritual.getActivationCost();
    }

    /**
     * Fired when an imperfect ritual is about to be activated.
     * Cancel to prevent activation.
     */
    public static class Activate extends ImperfectRitualEvent implements ICancellableEvent {
        public Activate(TileImperfectRitualStone ritualStone, ImperfectRitual ritual,
                        Player player, @Nullable ImperfectRitualStats stats) {
            super(ritualStone, ritual, player, stats);
        }
    }

    /**
     * Fired after an imperfect ritual has been successfully activated.
     * Not cancellable - use for notification purposes only.
     */
    public static class Activated extends ImperfectRitualEvent {
        public Activated(TileImperfectRitualStone ritualStone, ImperfectRitual ritual,
                         Player player, @Nullable ImperfectRitualStats stats) {
            super(ritualStone, ritual, player, stats);
        }
    }
}
