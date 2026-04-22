package com.breakinblocks.neovitae.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import com.breakinblocks.neovitae.common.material.MaterialDefinition;
import com.breakinblocks.neovitae.common.material.MaterialRegistry;
import com.breakinblocks.neovitae.util.helper.OreDiscoveryHelper;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;

public class GenerateMaterialsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("nvgenerate")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(GenerateMaterialsCommand::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        source.sendSuccess(() -> Component.translatable("command.neovitae.generate.scanning"), false);

        List<MaterialDefinition> newMaterials = OreDiscoveryHelper.discoverNewMaterials(level);
        List<String> addedNames = new ArrayList<>();
        for (MaterialDefinition mat : newMaterials) {
            addedNames.add(mat.getName());
        }

        int totalOreCount = com.breakinblocks.neovitae.util.helper.TagHelper.getOreNames(
                level.registryAccess().lookupOrThrow(Registries.ITEM)).size();

        if (newMaterials.isEmpty()) {
            int existing = totalOreCount;
            source.sendSuccess(() -> Component.translatable("command.neovitae.generate.no_new", existing), false);
            return 0;
        }

        MaterialRegistry.appendAndSave(newMaterials);

        final String names = String.join(", ", addedNames);
        final int added = addedNames.size();
        final int skippedCount = totalOreCount - added;
        source.sendSuccess(() -> Component.translatable("command.neovitae.generate.added", added, names), true);
        if (skippedCount > 0) {
            source.sendSuccess(() -> Component.translatable("command.neovitae.generate.skipped", skippedCount), false);
        }
        source.sendSuccess(() -> Component.translatable("command.neovitae.generate.restart").withStyle(net.minecraft.ChatFormatting.YELLOW), false);

        return added;
    }
}
