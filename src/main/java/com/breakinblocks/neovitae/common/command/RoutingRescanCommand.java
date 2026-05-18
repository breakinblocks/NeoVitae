package com.breakinblocks.neovitae.common.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;

import java.util.Map;

/** Emergency rebuild of a Master's connection graph from live block entities in range. */
public final class RoutingRescanCommand {

    private static final int RESCAN_RADIUS = 32;
    private static final int LOCATE_RADIUS = 16;

    private RoutingRescanCommand() {}

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("rescan")
                .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(RoutingRescanCommand::execute);
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("This command must be run by a player"));
            return 0;
        }

        ServerLevel level = player.serverLevel();
        MasterRoutingNodeBlockEntity master = findMaster(player, level);

        if (master == null) {
            source.sendFailure(Component.literal(
                    "No Master Routing Node found. Look directly at one or stand within "
                            + LOCATE_RADIUS + " blocks of one."));
            return 0;
        }

        BlockPos masterPos = master.getBlockPos();
        int added = master.rescanNetwork(RESCAN_RADIUS);

        source.sendSuccess(() -> Component.literal(
                "Rescanned master at " + masterPos.getX() + ", " + masterPos.getY() + ", " + masterPos.getZ()
                        + " (" + added + " nodes found within " + RESCAN_RADIUS + " blocks)"), true);
        return added;
    }

    private static MasterRoutingNodeBlockEntity findMaster(ServerPlayer player, ServerLevel level) {
        HitResult hit = player.pick(16.0D, 0.0F, false);
        if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK) {
            BlockEntity be = level.getBlockEntity(blockHit.getBlockPos());
            if (be instanceof MasterRoutingNodeBlockEntity master) {
                return master;
            }
        }

        BlockPos origin = player.blockPosition();
        int r = LOCATE_RADIUS;
        int radiusSq = r * r;
        MasterRoutingNodeBlockEntity best = null;
        double bestDistSq = Double.MAX_VALUE;

        int minCx = (origin.getX() - r) >> 4;
        int maxCx = (origin.getX() + r) >> 4;
        int minCz = (origin.getZ() - r) >> 4;
        int maxCz = (origin.getZ() + r) >> 4;

        for (int cx = minCx; cx <= maxCx; cx++) {
            for (int cz = minCz; cz <= maxCz; cz++) {
                if (!level.hasChunk(cx, cz)) continue;
                LevelChunk chunk = level.getChunk(cx, cz);
                for (Map.Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
                    BlockEntity be = entry.getValue();
                    if (!(be instanceof MasterRoutingNodeBlockEntity master)) continue;
                    double distSq = entry.getKey().distSqr(origin);
                    if (distSq <= radiusSq && distSq < bestDistSq) {
                        bestDistSq = distSq;
                        best = master;
                    }
                }
            }
        }
        return best;
    }
}
