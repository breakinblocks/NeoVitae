package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import com.breakinblocks.neovitae.api.ritual.IImperfectRitualStone;
import com.breakinblocks.neovitae.common.datacomponent.SoulNetwork;
import com.breakinblocks.neovitae.common.datamap.ImperfectRitualStats;
import com.breakinblocks.neovitae.common.event.ImperfectRitualEvent;
import com.breakinblocks.neovitae.ritual.ImperfectRitual;
import com.breakinblocks.neovitae.ritual.RitualResult;
import com.breakinblocks.neovitae.api.soul.SoulTicket;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;
import com.breakinblocks.neovitae.util.helper.SoulNetworkHelper;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Block entity for the Imperfect Ritual Stone.
 * Implements the logic for activating imperfect rituals.
 */
public class TileImperfectRitualStone extends BlockEntity implements IImperfectRitualStone {

    public TileImperfectRitualStone(BlockPos pos, BlockState state) {
        super(BMTiles.IMPERFECT_RITUAL_STONE_TYPE.get(), pos, state);
    }

    /**
     * Performs an imperfect ritual with data-driven stats.
     *
     * @param world The world
     * @param pos The position of the ritual stone
     * @param imperfectRitual The ritual to perform
     * @param stats The data-driven stats (or null to use ritual defaults)
     * @param player The player activating the ritual
     * @return RitualResult indicating success or specific failure reason
     */
    public RitualResult performRitual(Level world, BlockPos pos, ImperfectRitual imperfectRitual,
                                 @Nullable ImperfectRitualStats stats, Player player) {
        if (world.isClientSide() || imperfectRitual == null) {
            return RitualResult.failure(RitualResult.FailureReason.CLIENT_SIDE);
        }

        // Check if ritual is disabled via datapack
        if (stats != null && !stats.enabled()) {
            return RitualResult.failure(RitualResult.FailureReason.RITUAL_DISABLED);
        }

        // Get the player's soul network
        UUID playerUUID = player.getUUID();
        SoulNetwork network = SoulNetworkHelper.getSoulNetwork(playerUUID);

        if (network == null) {
            return RitualResult.failure(RitualResult.FailureReason.NO_SOUL_NETWORK);
        }

        // Use data-driven cost or fall back to ritual default
        int activationCost = stats != null ? stats.activationCost() : imperfectRitual.getActivationCost();

        // Check if player has enough LP
        if (network.getCurrentEssence() < activationCost) {
            return RitualResult.failure(RitualResult.FailureReason.NOT_ENOUGH_LP, activationCost);
        }

        // Fire pre-activation event (cancellable)
        ImperfectRitualEvent.Activate activateEvent = new ImperfectRitualEvent.Activate(this, imperfectRitual, player, stats);
        if (NeoForge.EVENT_BUS.post(activateEvent).isCanceled()) {
            return RitualResult.failure(RitualResult.FailureReason.EVENT_CANCELLED);
        }

        // Try to activate the ritual
        if (imperfectRitual.onActivate(this, player)) {
            // Drain LP
            network.syphon(SoulTicket.create(activationCost));

            // Handle block consumption if enabled in stats
            if (stats != null && stats.consumeBlock()) {
                BlockPos abovePos = pos.above();
                BlockProtectionHelper.tryBreakBlockNoDrops(world, abovePos, player);
            }

            // Light show - spawn visual-only lightning
            boolean showLightning = stats != null ? stats.lightningEffect() : imperfectRitual.isLightShow();
            if (showLightning && world instanceof ServerLevel serverLevel) {
                LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
                if (lightning != null) {
                    lightning.setPos(pos.getX() + 0.5, pos.getY() + 2, pos.getZ() + 0.5);
                    lightning.setVisualOnly(true);
                    serverLevel.addFreshEntity(lightning);
                }
            }

            // Fire post-activation event (not cancellable)
            NeoForge.EVENT_BUS.post(new ImperfectRitualEvent.Activated(this, imperfectRitual, player, stats));

            return RitualResult.success();
        }

        return RitualResult.failure(RitualResult.FailureReason.ACTIVATION_FAILED);
    }

    @Override
    public Level getRitualWorld() {
        return this.level;
    }

    @Override
    public BlockPos getRitualPos() {
        return this.worldPosition;
    }
}
