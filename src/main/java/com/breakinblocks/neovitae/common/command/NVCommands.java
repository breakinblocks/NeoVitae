package com.breakinblocks.neovitae.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class NVCommands {
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext buildContext = event.getBuildContext();

        dispatcher.register(
                Commands.literal("neovitae")
                        .then(AltarCommand.build())
                        .then(AnimaCommand.build())
                        .then(AuraCommand.build())
                        .then(DungeonShowcaseCommand.build())
                        .then(GenerateMaterialsCommand.build())
                        .then(ImperfectRitualCommand.build())
                        .then(RitualCommand.build())
                        .then(Commands.literal("routing").then(RoutingRescanCommand.build()))
                        .then(SentientUpgradesCommand.build(buildContext))
                        .then(SetOrbFillCommand.build())
                        .then(ShowcaseCommand.build())
                        .then(StreamTestCommand.build())
        );
    }
}
