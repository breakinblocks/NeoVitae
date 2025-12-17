package com.breakinblocks.neovitae.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.will.WorldDemonWillHandler;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Command to get/set demon will in the chunk aura.
 * Usage:
 *   /bm-aura get                     - Get all will types (shows current and max)
 *   /bm-aura get all                 - Get all will types (explicit)
 *   /bm-aura get <type>              - Get will amount for specific type
 *   /bm-aura set <type> <amount>     - Set will amount for a type (clamped to 0-max)
 *   /bm-aura set all <amount>        - Set all types to the same amount (clamped to 0-max)
 *   /bm-aura add <type> <amount>     - Add will to a type (result clamped to 0-max)
 *   /bm-aura clear                   - Clear all will from chunk
 *
 * <p>Maximum will per chunk is configurable in server config and can be
 * increased per-chunk via rituals.</p>
 */
public class AuraCommand {

    // Maximum argument value - actual max is per-chunk and configurable
    private static final double COMMAND_MAX = 10000.0;

    private static final SuggestionProvider<CommandSourceStack> WILL_TYPE_SUGGESTIONS = (context, builder) -> {
        Stream<String> types = Arrays.stream(EnumWillType.values())
                .map(EnumWillType::getSerializedName);
        return SharedSuggestionProvider.suggest(Stream.concat(types, Stream.of("all")), builder);
    };

    private static final SuggestionProvider<CommandSourceStack> WILL_TYPE_ONLY_SUGGESTIONS = (context, builder) -> {
        Stream<String> types = Arrays.stream(EnumWillType.values())
                .map(EnumWillType::getSerializedName);
        return SharedSuggestionProvider.suggest(Stream.concat(types, Stream.of("all")), builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("bm-aura")
                        .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        // Get will
                        .then(
                                Commands.literal("get")
                                        .executes(AuraCommand::getAllWill)
                                        .then(
                                                Commands.argument("type", StringArgumentType.word())
                                                        .suggests(WILL_TYPE_ONLY_SUGGESTIONS)
                                                        .executes(context -> getWill(context, StringArgumentType.getString(context, "type")))
                                        )
                        )
                        // Set will (clamped to 0-max for chunk)
                        .then(
                                Commands.literal("set")
                                        .then(
                                                Commands.argument("type", StringArgumentType.word())
                                                        .suggests(WILL_TYPE_SUGGESTIONS)
                                                        .then(
                                                                Commands.argument("amount", DoubleArgumentType.doubleArg(0, COMMAND_MAX))
                                                                        .executes(context -> setWill(context,
                                                                                StringArgumentType.getString(context, "type"),
                                                                                DoubleArgumentType.getDouble(context, "amount")))
                                                        )
                                        )
                        )
                        // Add will (result clamped to 0-max for chunk)
                        .then(
                                Commands.literal("add")
                                        .then(
                                                Commands.argument("type", StringArgumentType.word())
                                                        .suggests(WILL_TYPE_SUGGESTIONS)
                                                        .then(
                                                                Commands.argument("amount", DoubleArgumentType.doubleArg(-COMMAND_MAX, COMMAND_MAX))
                                                                        .executes(context -> addWill(context,
                                                                                StringArgumentType.getString(context, "type"),
                                                                                DoubleArgumentType.getDouble(context, "amount")))
                                                        )
                                        )
                        )
                        // Clear all will
                        .then(
                                Commands.literal("clear")
                                        .executes(AuraCommand::clearWill)
                        )
        );
    }

    private static int getAllWill(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());

        StringBuilder sb = new StringBuilder("Demon Will in chunk at ");
        sb.append(pos.toShortString()).append(":\n");

        for (EnumWillType type : EnumWillType.values()) {
            double amount = WorldDemonWillHandler.getCurrentWill(level, pos, type);
            double max = WorldDemonWillHandler.getMaxWill(level, pos, type);
            double bonus = WorldDemonWillHandler.getMaxBonus(level, pos, type);
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

    private static int getWill(CommandContext<CommandSourceStack> context, String typeStr) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());

        // "all" shows all will types
        if (typeStr.equalsIgnoreCase("all")) {
            return getAllWill(context);
        }

        EnumWillType type = parseWillType(typeStr);
        if (type == null) {
            source.sendFailure(Component.literal("Invalid will type: " + typeStr));
            return 0;
        }

        double amount = WorldDemonWillHandler.getCurrentWill(level, pos, type);
        double max = WorldDemonWillHandler.getMaxWill(level, pos, type);
        double bonus = WorldDemonWillHandler.getMaxBonus(level, pos, type);
        StringBuilder msg = new StringBuilder(type.getSerializedName() + " will in chunk: " +
                String.format("%.2f", amount) + " / " + String.format("%.2f", max));
        if (bonus > 0) {
            msg.append(" (+").append(String.format("%.0f", bonus)).append(" bonus)");
        }
        source.sendSuccess(() -> Component.literal(msg.toString()), false);
        return 1;
    }

    private static int setWill(CommandContext<CommandSourceStack> context, String typeStr, double amount) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());

        if (typeStr.equalsIgnoreCase("all")) {
            for (EnumWillType type : EnumWillType.values()) {
                double max = WorldDemonWillHandler.getMaxWill(level, pos, type);
                double clampedAmount = Math.max(0, Math.min(max, amount));
                setWillForType(level, pos, type, clampedAmount);
            }
            source.sendSuccess(() -> Component.literal("Set all will types to " + String.format("%.2f", amount) + " (clamped to max)"), true);
        } else {
            EnumWillType type = parseWillType(typeStr);
            if (type == null) {
                source.sendFailure(Component.literal("Invalid will type: " + typeStr));
                return 0;
            }
            double max = WorldDemonWillHandler.getMaxWill(level, pos, type);
            double clampedAmount = Math.max(0, Math.min(max, amount));
            setWillForType(level, pos, type, clampedAmount);
            source.sendSuccess(() -> Component.literal("Set " + type.getSerializedName() + " will to " + String.format("%.2f", clampedAmount)), true);
        }
        return 1;
    }

    private static int addWill(CommandContext<CommandSourceStack> context, String typeStr, double amount) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());

        if (typeStr.equalsIgnoreCase("all")) {
            StringBuilder results = new StringBuilder("Added will to all types:\n");
            for (EnumWillType type : EnumWillType.values()) {
                double before = WorldDemonWillHandler.getCurrentWill(level, pos, type);
                if (amount >= 0) {
                    WorldDemonWillHandler.addWillToChunk(level, pos, type, amount);
                } else {
                    WorldDemonWillHandler.drainWillFromChunk(level, pos, type, -amount);
                }
                double after = WorldDemonWillHandler.getCurrentWill(level, pos, type);
                results.append("  ").append(type.getSerializedName()).append(": ")
                        .append(String.format("%.2f", before)).append(" -> ").append(String.format("%.2f", after)).append("\n");
            }
            source.sendSuccess(() -> Component.literal(results.toString()), true);
        } else {
            EnumWillType type = parseWillType(typeStr);
            if (type == null) {
                source.sendFailure(Component.literal("Invalid will type: " + typeStr));
                return 0;
            }
            double before = WorldDemonWillHandler.getCurrentWill(level, pos, type);
            if (amount >= 0) {
                WorldDemonWillHandler.addWillToChunk(level, pos, type, amount);
            } else {
                WorldDemonWillHandler.drainWillFromChunk(level, pos, type, -amount);
            }
            double after = WorldDemonWillHandler.getCurrentWill(level, pos, type);
            source.sendSuccess(() -> Component.literal(type.getSerializedName() + " will: " +
                    String.format("%.2f", before) + " -> " + String.format("%.2f", after)), true);
        }
        return 1;
    }

    private static int clearWill(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());

        for (EnumWillType type : EnumWillType.values()) {
            setWillForType(level, pos, type, 0);
        }

        source.sendSuccess(() -> Component.literal("Cleared all demon will from chunk"), true);
        return 1;
    }

    private static void setWillForType(ServerLevel level, BlockPos pos, EnumWillType type, double amount) {
        double current = WorldDemonWillHandler.getCurrentWill(level, pos, type);
        if (amount > current) {
            WorldDemonWillHandler.addWillToChunk(level, pos, type, amount - current);
        } else if (amount < current) {
            WorldDemonWillHandler.drainWillFromChunk(level, pos, type, current - amount);
        }
    }

    private static EnumWillType parseWillType(String str) {
        for (EnumWillType type : EnumWillType.values()) {
            if (type.getSerializedName().equalsIgnoreCase(str)) {
                return type;
            }
        }
        return null;
    }
}
