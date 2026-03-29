package com.breakinblocks.neovitae.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class NVCommands {
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext buildContext = event.getBuildContext();

        AnimaCommand.register(dispatcher);
        LivingUpgradesCommand.register(dispatcher, buildContext);
        RitualCommand.register(dispatcher);
        ImperfectRitualCommand.register(dispatcher);
        AuraCommand.register(dispatcher);
        DungeonShowcaseCommand.register(dispatcher);
        GenerateMaterialsCommand.register(dispatcher);
        StreamTestCommand.register(dispatcher);

        CommandNode<CommandSourceStack> networkNode = dispatcher.getRoot().getChild("anima");
        CommandNode<CommandSourceStack> ritualNode = dispatcher.getRoot().getChild("nv-ritual");
        CommandNode<CommandSourceStack> imperfectRitualNode = dispatcher.getRoot().getChild("nv-imperfectritual");
        CommandNode<CommandSourceStack> auraNode = dispatcher.getRoot().getChild("nv-aura");
        CommandNode<CommandSourceStack> upgradeNode = dispatcher.getRoot().getChild("living-upgrade");
        CommandNode<CommandSourceStack> showcaseNode = dispatcher.getRoot().getChild("nv-dungeon-showcase");
        CommandNode<CommandSourceStack> generateNode = dispatcher.getRoot().getChild("nvgenerate");

        dispatcher.register(
                Commands.literal("neovitae")
                        .then(Commands.literal("network").redirect(networkNode))
                        .then(Commands.literal("ritual").redirect(ritualNode))
                        .then(Commands.literal("imperfect").redirect(imperfectRitualNode))
                        .then(Commands.literal("aura").redirect(auraNode))
                        .then(Commands.literal("upgrade").redirect(upgradeNode))
                        .then(Commands.literal("dungeon-showcase").redirect(showcaseNode))
                        .then(Commands.literal("generate").redirect(generateNode))
        );
    }
}
