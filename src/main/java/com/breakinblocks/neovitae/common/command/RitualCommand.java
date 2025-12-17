package com.breakinblocks.neovitae.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneTile;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualRegistry;

/**
 * Admin command for managing rituals.
 * Usage:
 * - /bm-ritual <pos> info - Show ritual info at position
 * - /bm-ritual <pos> stop - Force stop the ritual
 * - /bm-ritual <pos> set <ritual_id> - Force set a ritual (without activation cost)
 * - /bm-ritual <pos> cooldown <ticks> - Set cooldown
 * - /bm-ritual list - List all registered rituals
 */
public class RitualCommand {

    private static final SimpleCommandExceptionType ERROR_NOT_MRS = new SimpleCommandExceptionType(
            Component.translatable("commands.neovitae.ritual.not_mrs"));
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_RITUAL = new DynamicCommandExceptionType(
            id -> Component.translatable("commands.neovitae.ritual.unknown", id));
    private static final SimpleCommandExceptionType ERROR_NO_RITUAL = new SimpleCommandExceptionType(
            Component.translatable("commands.neovitae.ritual.none_active"));

    private static final SuggestionProvider<CommandSourceStack> RITUAL_SUGGESTIONS = (context, builder) -> {
        return SharedSuggestionProvider.suggestResource(RitualRegistry.getRegisteredRituals(), builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("bm-ritual")
                        .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(
                                Commands.argument("pos", BlockPosArgument.blockPos())
                                        .then(
                                                Commands.literal("info")
                                                        .executes(RitualCommand::showInfo)
                                        )
                                        .then(
                                                Commands.literal("stop")
                                                        .executes(RitualCommand::stopRitual)
                                        )
                                        .then(
                                                Commands.literal("set")
                                                        .then(
                                                                Commands.argument("ritual", ResourceLocationArgument.id())
                                                                        .suggests(RITUAL_SUGGESTIONS)
                                                                        .executes(RitualCommand::setRitual)
                                                        )
                                        )
                                        .then(
                                                Commands.literal("cooldown")
                                                        .then(
                                                                Commands.argument("ticks", IntegerArgumentType.integer(0))
                                                                        .executes(RitualCommand::setCooldown)
                                                        )
                                        )
                        )
                        .then(
                                Commands.literal("list")
                                        .executes(RitualCommand::listRituals)
                        )
        );
    }

    private static MasterRitualStoneTile getMRS(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
        ServerLevel level = context.getSource().getLevel();
        BlockEntity be = level.getBlockEntity(pos);

        if (!(be instanceof MasterRitualStoneTile mrs)) {
            throw ERROR_NOT_MRS.create();
        }

        return mrs;
    }

    private static int showInfo(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        MasterRitualStoneTile mrs = getMRS(context);
        CommandSourceStack source = context.getSource();

        if (!mrs.isActive() || mrs.getCurrentRitual() == null) {
            source.sendSuccess(() -> Component.translatable("commands.neovitae.ritual.info.inactive"), false);
            return Command.SINGLE_SUCCESS;
        }

        Ritual ritual = mrs.getCurrentRitual();
        ResourceLocation ritualId = RitualRegistry.getId(ritual);

        source.sendSuccess(() -> Component.translatable("commands.neovitae.ritual.info.header"), false);
        source.sendSuccess(() -> Component.translatable("commands.neovitae.ritual.info.name",
                ritualId != null ? ritualId.toString() : "unknown"), false);
        source.sendSuccess(() -> Component.translatable("commands.neovitae.ritual.info.running_time",
                mrs.getRunningTime()), false);
        source.sendSuccess(() -> Component.translatable("commands.neovitae.ritual.info.cooldown",
                mrs.getCooldown()), false);
        source.sendSuccess(() -> Component.translatable("commands.neovitae.ritual.info.owner",
                mrs.getOwner() != null ? mrs.getOwner().toString() : "none"), false);
        source.sendSuccess(() -> Component.translatable("commands.neovitae.ritual.info.refresh_cost",
                ritual.getRefreshCost()), false);
        source.sendSuccess(() -> Component.translatable("commands.neovitae.ritual.info.direction",
                mrs.getDirection().getName()), false);

        return Command.SINGLE_SUCCESS;
    }

    private static int stopRitual(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        MasterRitualStoneTile mrs = getMRS(context);

        if (!mrs.isActive() || mrs.getCurrentRitual() == null) {
            throw ERROR_NO_RITUAL.create();
        }

        ResourceLocation ritualId = RitualRegistry.getId(mrs.getCurrentRitual());
        mrs.stopRitual(Ritual.BreakType.DEACTIVATE);

        context.getSource().sendSuccess(() -> Component.translatable("commands.neovitae.ritual.stopped",
                ritualId != null ? ritualId.toString() : "unknown"), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int setRitual(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        MasterRitualStoneTile mrs = getMRS(context);
        ResourceLocation ritualId = ResourceLocationArgument.getId(context, "ritual");

        Ritual ritual = RitualRegistry.getRitual(ritualId);
        if (ritual == null) {
            throw ERROR_UNKNOWN_RITUAL.create(ritualId);
        }

        // Stop any current ritual
        if (mrs.isActive() && mrs.getCurrentRitual() != null) {
            mrs.stopRitual(Ritual.BreakType.DEACTIVATE);
        }

        // Force activate without cost or structure check (admin override)
        // getPlayer() may return null if executed from console, which is fine for forceActivateRitual
        mrs.forceActivateRitual(ritual, context.getSource().getPlayer());

        context.getSource().sendSuccess(() -> Component.translatable("commands.neovitae.ritual.set",
                ritualId.toString()), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int setCooldown(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        MasterRitualStoneTile mrs = getMRS(context);
        int ticks = IntegerArgumentType.getInteger(context, "ticks");

        mrs.setCooldown(ticks);

        context.getSource().sendSuccess(() -> Component.translatable("commands.neovitae.ritual.cooldown_set",
                ticks), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int listRituals(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        source.sendSuccess(() -> Component.translatable("commands.neovitae.ritual.list.header"), false);

        for (ResourceLocation id : RitualRegistry.getRegisteredRituals()) {
            Ritual ritual = RitualRegistry.getRitual(id);
            if (ritual != null) {
                source.sendSuccess(() -> Component.literal(" - " + id.toString() +
                        " (Crystal: " + ritual.getCrystalLevel() + ", Cost: " + ritual.getActivationCost() + ")"), false);
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
