package com.breakinblocks.neovitae.common.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;

public class AnimaCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("anima-network")
                .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(
                        Commands.argument("target", EntityArgument.player())
                                .then(
                                        Commands.literal("query")
                                                .executes(context -> showNetwork(context, EntityArgument.getPlayer(context, "target")))
                                )
                                .then(
                                        Commands.literal("reset")
                                                .executes(context -> setNetwork(context, EntityArgument.getPlayer(context, "target"), 0))
                                )
                                .then(
                                        Commands.literal("set")
                                                .then(
                                                        Commands.argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                                                .executes(context -> setNetwork(context, EntityArgument.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "amount")))
                                                )
                                )
                                .then(
                                        Commands.literal("add")
                                                .then(
                                                        Commands.argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                                                .executes(context -> addNetwork(context, EntityArgument.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "amount")))
                                                )
                                )
                );
    }

    private static int setNetwork(CommandContext<CommandSourceStack> context, ServerPlayer target, int amount) {
        Anima anima = AnimaHelper.getAnima(target);
        int setAmount = anima.set(AnimaTicket.create(amount), Integer.MAX_VALUE);
        context.getSource().sendSuccess(() -> Component.literal("Successfully set " + target.getGameProfile().getName() + "'s Essence to " + setAmount), true);
        return 1;
    }

    private static int addNetwork(CommandContext<CommandSourceStack> context, ServerPlayer target, int amount) {
        Anima anima = AnimaHelper.getAnima(target);
        int added = anima.add(AnimaTicket.create(amount), Integer.MAX_VALUE);
        context.getSource().sendSuccess(() -> Component.literal("Successfully added " + added + " EV to " + target.getGameProfile().getName() + "'s Anima"), true);
        return 1;
    }

    private static int showNetwork(CommandContext<CommandSourceStack> context, ServerPlayer target) {
        Anima anima = AnimaHelper.getAnima(target);
        int amount = anima.getCurrentEV();
        context.getSource().sendSuccess(() -> Component.literal(target.getGameProfile().getName() + " has " + amount + " EV in their Anima"), true);
        return 1;
    }
}
