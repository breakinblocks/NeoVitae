package com.breakinblocks.neovitae.common.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.will.WorldSpiritusHandler;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Command to get/set spiritus in the chunk aura.
 * Usage:
 *   /nv-aura get                     - Get all will types (shows current and max)
 *   /nv-aura get all                 - Get all will types (explicit)
 *   /nv-aura get <type>              - Get will amount for specific type
 *   /nv-aura set <type> <amount>     - Set will amount for a type (clamped to 0-max)
 *   /nv-aura set all <amount>        - Set all types to the same amount (clamped to 0-max)
 *   /nv-aura add <type> <amount>     - Add will to a type (result clamped to 0-max)
 *   /nv-aura clear                   - Clear all will from chunk
 *
 * <p>Maximum will per chunk is configurable in server config and can be
 * increased per-chunk via rituals.</p>
 */
public class AuraCommand {

    private static final double COMMAND_MAX = 10000.0;

    private static final SuggestionProvider<CommandSourceStack> SPIRITUS_TYPE_SUGGESTIONS = (context, builder) -> {
        Stream<String> types = Arrays.stream(SpiritusType.values())
                .map(SpiritusType::getSerializedName);
        return SharedSuggestionProvider.suggest(Stream.of(types, Stream.of("raw", "all")).flatMap(s -> s), builder);
    };

    private static final SuggestionProvider<CommandSourceStack> WILL_TYPE_ONLY_SUGGESTIONS = (context, builder) -> {
        Stream<String> types = Arrays.stream(SpiritusType.values())
                .map(SpiritusType::getSerializedName);
        return SharedSuggestionProvider.suggest(Stream.of(types, Stream.of("raw", "all")).flatMap(s -> s), builder);
    };

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("aura")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(
                        Commands.literal("get")
                                .executes(AuraCommand::getAllWill)
                                .then(
                                        Commands.argument("type", StringArgumentType.word())
                                                .suggests(WILL_TYPE_ONLY_SUGGESTIONS)
                                                .executes(context -> getSpiritus(context, StringArgumentType.getString(context, "type")))
                                )
                )
                .then(
                        Commands.literal("set")
                                .then(
                                        Commands.argument("type", StringArgumentType.word())
                                                .suggests(SPIRITUS_TYPE_SUGGESTIONS)
                                                .then(
                                                        Commands.argument("amount", DoubleArgumentType.doubleArg(0, COMMAND_MAX))
                                                                .executes(context -> setSpiritus(context,
                                                                        StringArgumentType.getString(context, "type"),
                                                                        DoubleArgumentType.getDouble(context, "amount")))
                                                )
                                )
                )
                .then(
                        Commands.literal("add")
                                .then(
                                        Commands.argument("type", StringArgumentType.word())
                                                .suggests(SPIRITUS_TYPE_SUGGESTIONS)
                                                .then(
                                                        Commands.argument("amount", DoubleArgumentType.doubleArg(-COMMAND_MAX, COMMAND_MAX))
                                                                .executes(context -> addSpiritus(context,
                                                                        StringArgumentType.getString(context, "type"),
                                                                        DoubleArgumentType.getDouble(context, "amount")))
                                                )
                                )
                )
                .then(
                        Commands.literal("clear")
                                .executes(AuraCommand::clearWill)
                );
    }

    private static int getAllWill(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());

        StringBuilder sb = new StringBuilder("Spiritus in chunk at ");
        sb.append(pos.toShortString()).append(":\n");

        for (SpiritusType type : SpiritusType.values()) {
            double amount = WorldSpiritusHandler.getCurrentWill(level, pos, type);
            double max = WorldSpiritusHandler.getMaxSpiritus(level, pos, type);
            double bonus = WorldSpiritusHandler.getMaxBonus(level, pos, type);
            sb.append("  ").append(type.getSerializedName()).append(": ")
                    .append(String.format("%.2f", amount)).append(" / ").append(String.format("%.2f", max));
            if (bonus > 0) {
                sb.append(" (+").append(String.format("%.0f", bonus)).append(" bonus)");
            }
            sb.append("\n");
        }

        source.sendSuccess(() -> Component.literal(sb.toString()), false);
        return 1;
    }

    private static int getSpiritus(CommandContext<CommandSourceStack> context, String typeStr) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());

        if (typeStr.equalsIgnoreCase("all")) {
            return getAllWill(context);
        }

        SpiritusType type = parseWillType(typeStr);
        if (type == null) {
            source.sendFailure(Component.translatable("command.neovitae.aura.invalid_will_type", typeStr));
            return 0;
        }

        double amount = WorldSpiritusHandler.getCurrentWill(level, pos, type);
        double max = WorldSpiritusHandler.getMaxSpiritus(level, pos, type);
        double bonus = WorldSpiritusHandler.getMaxBonus(level, pos, type);
        StringBuilder msg = new StringBuilder(type.getSerializedName() + " will in chunk: " +
                String.format("%.2f", amount) + " / " + String.format("%.2f", max));
        if (bonus > 0) {
            msg.append(" (+").append(String.format("%.0f", bonus)).append(" bonus)");
        }
        source.sendSuccess(() -> Component.literal(msg.toString()), false);
        return 1;
    }

    private static int setSpiritus(CommandContext<CommandSourceStack> context, String typeStr, double amount) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());

        if (typeStr.equalsIgnoreCase("all")) {
            for (SpiritusType type : SpiritusType.values()) {
                double max = WorldSpiritusHandler.getMaxSpiritus(level, pos, type);
                double clampedAmount = Math.max(0, Math.min(max, amount));
                setWillForType(level, pos, type, clampedAmount);
            }
            source.sendSuccess(() -> Component.translatable("command.neovitae.aura.set_all", String.format("%.2f", amount)), true);
        } else {
            SpiritusType type = parseWillType(typeStr);
            if (type == null) {
                source.sendFailure(Component.translatable("command.neovitae.aura.invalid_will_type", typeStr));
                return 0;
            }
            double max = WorldSpiritusHandler.getMaxSpiritus(level, pos, type);
            double clampedAmount = Math.max(0, Math.min(max, amount));
            setWillForType(level, pos, type, clampedAmount);
            source.sendSuccess(() -> Component.translatable("command.neovitae.aura.set_type", type.getSerializedName(), String.format("%.2f", clampedAmount)), true);
        }
        return 1;
    }

    private static int addSpiritus(CommandContext<CommandSourceStack> context, String typeStr, double amount) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());

        if (typeStr.equalsIgnoreCase("all")) {
            StringBuilder results = new StringBuilder("Added will to all types:\n");
            for (SpiritusType type : SpiritusType.values()) {
                double before = WorldSpiritusHandler.getCurrentWill(level, pos, type);
                if (amount >= 0) {
                    WorldSpiritusHandler.addWillToChunk(level, pos, type, amount);
                } else {
                    WorldSpiritusHandler.drainWillFromChunk(level, pos, type, -amount);
                }
                double after = WorldSpiritusHandler.getCurrentWill(level, pos, type);
                results.append("  ").append(type.getSerializedName()).append(": ")
                        .append(String.format("%.2f", before)).append(" -> ").append(String.format("%.2f", after)).append("\n");
            }
            source.sendSuccess(() -> Component.literal(results.toString()), true);
        } else {
            SpiritusType type = parseWillType(typeStr);
            if (type == null) {
                source.sendFailure(Component.translatable("command.neovitae.aura.invalid_will_type", typeStr));
                return 0;
            }
            double before = WorldSpiritusHandler.getCurrentWill(level, pos, type);
            if (amount >= 0) {
                WorldSpiritusHandler.addWillToChunk(level, pos, type, amount);
            } else {
                WorldSpiritusHandler.drainWillFromChunk(level, pos, type, -amount);
            }
            double after = WorldSpiritusHandler.getCurrentWill(level, pos, type);
            source.sendSuccess(() -> Component.literal(type.getSerializedName() + " will: " +
                    String.format("%.2f", before) + " -> " + String.format("%.2f", after)), true);
        }
        return 1;
    }

    private static int clearWill(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());

        for (SpiritusType type : SpiritusType.values()) {
            setWillForType(level, pos, type, 0);
        }

        source.sendSuccess(() -> Component.translatable("command.neovitae.aura.cleared_chunk"), true);
        return 1;
    }

    private static void setWillForType(ServerLevel level, BlockPos pos, SpiritusType type, double amount) {
        double current = WorldSpiritusHandler.getCurrentWill(level, pos, type);
        if (amount > current) {
            WorldSpiritusHandler.addWillToChunk(level, pos, type, amount - current);
        } else if (amount < current) {
            WorldSpiritusHandler.drainWillFromChunk(level, pos, type, current - amount);
        }
    }

    private static SpiritusType parseWillType(String str) {
        if (str.equalsIgnoreCase("raw")) return SpiritusType.RAW;
        for (SpiritusType type : SpiritusType.values()) {
            if (type.getSerializedName().equalsIgnoreCase(str)) {
                return type;
            }
        }
        return null;
    }
}
