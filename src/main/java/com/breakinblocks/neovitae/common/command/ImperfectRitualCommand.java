package com.breakinblocks.neovitae.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.breakinblocks.neovitae.common.blockentity.TileImperfectRitualStone;
import com.breakinblocks.neovitae.common.datamap.BMDataMaps;
import com.breakinblocks.neovitae.common.datamap.ImperfectRitualStats;
import com.breakinblocks.neovitae.ritual.ImperfectRitual;
import com.breakinblocks.neovitae.ritual.RitualRegistry;

/**
 * Admin command for managing imperfect rituals.
 * Usage:
 * - /bm-imperfectritual <pos> set <ritual_id> - Place required block and activate ritual
 * - /bm-imperfectritual list - List all registered imperfect rituals with their requirements
 */
public class ImperfectRitualCommand {

    private static final SimpleCommandExceptionType ERROR_NOT_IRS = new SimpleCommandExceptionType(
            Component.translatable("commands.neovitae.imperfect_ritual.not_irs"));
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_RITUAL = new DynamicCommandExceptionType(
            id -> Component.translatable("commands.neovitae.imperfect_ritual.unknown", id));
    private static final DynamicCommandExceptionType ERROR_NO_BLOCK_REQUIREMENT = new DynamicCommandExceptionType(
            id -> Component.translatable("commands.neovitae.imperfect_ritual.no_block", id));

    private static final SuggestionProvider<CommandSourceStack> RITUAL_SUGGESTIONS = (context, builder) -> {
        return SharedSuggestionProvider.suggestResource(RitualRegistry.getRegisteredImperfectRituals(), builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("bm-imperfectritual")
                        .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(
                                Commands.argument("pos", BlockPosArgument.blockPos())
                                        .then(
                                                Commands.literal("set")
                                                        .then(
                                                                Commands.argument("ritual", ResourceLocationArgument.id())
                                                                        .suggests(RITUAL_SUGGESTIONS)
                                                                        .executes(ImperfectRitualCommand::setRitual)
                                                        )
                                        )
                        )
                        .then(
                                Commands.literal("list")
                                        .executes(ImperfectRitualCommand::listRituals)
                        )
        );
    }

    private static TileImperfectRitualStone getIRS(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
        ServerLevel level = context.getSource().getLevel();
        BlockEntity be = level.getBlockEntity(pos);

        if (!(be instanceof TileImperfectRitualStone irs)) {
            throw ERROR_NOT_IRS.create();
        }

        return irs;
    }

    private static int setRitual(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        TileImperfectRitualStone irs = getIRS(context);
        ResourceLocation ritualId = ResourceLocationArgument.getId(context, "ritual");
        ServerLevel level = context.getSource().getLevel();

        ImperfectRitual ritual = RitualRegistry.getImperfectRitual(ritualId);
        if (ritual == null) {
            throw ERROR_UNKNOWN_RITUAL.create(ritualId);
        }

        // Get the block requirement from DataMap
        var registry = RitualRegistry.getImperfectRitualRegistry();
        if (registry == null) {
            throw ERROR_UNKNOWN_RITUAL.create(ritualId);
        }

        Holder<ImperfectRitual> holder = registry.wrapAsHolder(ritual);
        ImperfectRitualStats stats = holder.getData(BMDataMaps.IMPERFECT_RITUAL_STATS);

        Block blockToPlace = null;
        if (stats != null && stats.block().isPresent()) {
            blockToPlace = stats.block().get();
        }

        if (blockToPlace == null) {
            throw ERROR_NO_BLOCK_REQUIREMENT.create(ritualId);
        }

        // Place the required block above the imperfect ritual stone
        BlockPos abovePos = irs.getRitualPos().above();
        level.setBlockAndUpdate(abovePos, blockToPlace.defaultBlockState());

        // Force activate the ritual bypassing LP checks (admin command)
        var player = context.getSource().getPlayer();
        boolean success = ritual.onActivate(irs, player);

        // Handle block consumption if enabled in stats
        if (success && stats != null && stats.consumeBlock()) {
            level.removeBlock(abovePos, false);
        }

        // Show lightning effect if enabled
        if (success) {
            boolean showLightning = stats != null ? stats.lightningEffect() : ritual.isLightShow();
            if (showLightning) {
                net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.spawn(level, abovePos.above(),
                        net.minecraft.world.entity.MobSpawnType.TRIGGERED);
            }
        }

        if (success) {
            context.getSource().sendSuccess(() -> Component.translatable("commands.neovitae.imperfect_ritual.activated",
                    ritualId.toString()), true);
        } else {
            context.getSource().sendSuccess(() -> Component.translatable("commands.neovitae.imperfect_ritual.failed",
                    ritualId.toString()), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int listRituals(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        source.sendSuccess(() -> Component.translatable("commands.neovitae.imperfect_ritual.list.header"), false);

        var registry = RitualRegistry.getImperfectRitualRegistry();

        for (ResourceLocation id : RitualRegistry.getRegisteredImperfectRituals()) {
            ImperfectRitual ritual = RitualRegistry.getImperfectRitual(id);
            if (ritual != null && registry != null) {
                Holder<ImperfectRitual> holder = registry.wrapAsHolder(ritual);
                ImperfectRitualStats stats = holder.getData(BMDataMaps.IMPERFECT_RITUAL_STATS);

                String blockName = "unknown";
                int cost = ritual.getActivationCost();

                if (stats != null) {
                    cost = stats.activationCost();
                    if (stats.block().isPresent()) {
                        blockName = BuiltInRegistries.BLOCK.getKey(stats.block().get()).toString();
                    } else if (stats.blockTag().isPresent()) {
                        blockName = "#" + stats.blockTag().get().location().toString();
                    }
                }

                final String finalBlockName = blockName;
                final int finalCost = cost;
                source.sendSuccess(() -> Component.literal(" - " + id.toString() +
                        " (Block: " + finalBlockName + ", Cost: " + finalCost + ")"), false);
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
