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
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datamap.ImperfectRitualStats;
import com.breakinblocks.neovitae.common.event.ImperfectRitualEvent;
import com.breakinblocks.neovitae.ritual.ImperfectRitual;
import com.breakinblocks.neovitae.ritual.RitualResult;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import javax.annotation.Nullable;
import java.util.UUID;

public class ImperfectRitualStoneBlockEntity extends BlockEntity implements IImperfectRitualStone {

    public ImperfectRitualStoneBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.IMPERFECT_RITUAL_STONE_TYPE.get(), pos, state);
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

        if (stats != null && !stats.enabled()) {
            return RitualResult.failure(RitualResult.FailureReason.RITUAL_DISABLED);
        }

        UUID playerUUID = player.getUUID();
        Anima network = AnimaHelper.getAnima(playerUUID);

        if (network == null) {
            return RitualResult.failure(RitualResult.FailureReason.NO_ANIMA);
        }

        int activationCost = stats != null ? stats.activationCost() : imperfectRitual.getActivationCost();

        if (network.getCurrentEV() < activationCost) {
            return RitualResult.failure(RitualResult.FailureReason.NOT_ENOUGH_LP, activationCost);
        }

        ImperfectRitualEvent.Activate activateEvent = new ImperfectRitualEvent.Activate(this, imperfectRitual, player, stats);
        if (NeoForge.EVENT_BUS.post(activateEvent).isCanceled()) {
            return RitualResult.failure(RitualResult.FailureReason.EVENT_CANCELLED);
        }

        if (imperfectRitual.onActivate(this, player)) {
            network.syphon(AnimaTicket.create(activationCost));

            if (stats != null && stats.consumeBlock()) {
                BlockPos abovePos = pos.above();
                BlockProtectionHelper.tryBreakBlockNoDrops(world, abovePos, player);
            }

            boolean showLightning = stats != null ? stats.lightningEffect() : imperfectRitual.isLightShow();
            if (showLightning && world instanceof ServerLevel serverLevel) {
                LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
                if (lightning != null) {
                    lightning.setPos(pos.getX() + 0.5, pos.getY() + 2, pos.getZ() + 0.5);
                    lightning.setVisualOnly(true);
                    serverLevel.addFreshEntity(lightning);
                }
            }

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
