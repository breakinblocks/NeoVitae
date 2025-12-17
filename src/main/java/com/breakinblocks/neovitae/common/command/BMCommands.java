package com.breakinblocks.neovitae.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class BMCommands {
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext buildContext = event.getBuildContext();

        // Register standalone commands
        SoulNetworkCommand.register(dispatcher);
        LivingUpgradesCommand.register(dispatcher, buildContext);
        RitualCommand.register(dispatcher);
        ImperfectRitualCommand.register(dispatcher);
        AuraCommand.register(dispatcher);

        // Get the registered command nodes for redirecting
        CommandNode<CommandSourceStack> networkNode = dispatcher.getRoot().getChild("bm-network");
        CommandNode<CommandSourceStack> ritualNode = dispatcher.getRoot().getChild("bm-ritual");
        CommandNode<CommandSourceStack> imperfectRitualNode = dispatcher.getRoot().getChild("bm-imperfectritual");
        CommandNode<CommandSourceStack> auraNode = dispatcher.getRoot().getChild("bm-aura");
        CommandNode<CommandSourceStack> upgradeNode = dispatcher.getRoot().getChild("living-upgrade");

        // Register /bloodmagic parent command with subcommand redirects
        dispatcher.register(
                Commands.literal("bloodmagic")
                        .then(Commands.literal("network").redirect(networkNode))
                        .then(Commands.literal("ritual").redirect(ritualNode))
                        .then(Commands.literal("imperfect").redirect(imperfectRitualNode))
                        .then(Commands.literal("aura").redirect(auraNode))
                        .then(Commands.literal("upgrade").redirect(upgradeNode))
        );
    }
}
