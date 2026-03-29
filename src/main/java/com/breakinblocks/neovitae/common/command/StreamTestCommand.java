package com.breakinblocks.neovitae.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.api.stream.StreamEffect;
import com.breakinblocks.neovitae.api.stream.StreamPresets;

import java.util.List;

public class StreamTestCommand {

    private static final List<String> PRESET_NAMES = List.of(
            "bloodTendril", "soulSiphon", "voidTendril", "lifePulse",
            "demonTether", "corruptionSeep", "arcaneBolt",
            "emberMote", "soulWisp", "voidMark"
    );

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_PRESETS =
            (context, builder) -> SharedSuggestionProvider.suggest(PRESET_NAMES, builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("nvstream")
                        .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.argument("preset", StringArgumentType.word())
                                .suggests(SUGGEST_PRESETS)
                                .executes(StreamTestCommand::execute))
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Must be run by a player"));
            return 0;
        }

        String preset = StringArgumentType.getString(context, "preset");
        ServerLevel level = player.serverLevel();

        Vec3 eye = player.getEyePosition();
        BlockHitResult hit = level.clip(new ClipContext(
                eye, eye.add(player.getLookAngle().scale(64)),
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

        if (hit.getType() == HitResult.Type.MISS) {
            source.sendFailure(Component.literal("No block in range"));
            return 0;
        }

        net.minecraft.core.BlockPos targetPos = hit.getBlockPos();
        StreamEffect.Builder builder = getPreset(preset, player, targetPos);

        if (builder == null) {
            source.sendFailure(Component.literal("Unknown preset: " + preset));
            return 0;
        }

        builder.build().sendToNearby(level, player.blockPosition(), 128);
        source.sendSuccess(() -> Component.literal("Fired " + preset + " to " + targetPos.toShortString()), false);
        return 1;
    }

    private static StreamEffect.Builder getPreset(String name, ServerPlayer player, net.minecraft.core.BlockPos target) {
        return switch (name) {
            case "bloodTendril" -> StreamPresets.bloodTendril(player, target);
            case "soulSiphon" -> StreamPresets.soulSiphon(player, target);
            case "voidTendril" -> StreamPresets.voidTendril(player, target);
            case "lifePulse" -> StreamPresets.lifePulse(player, target);
            case "demonTether" -> StreamPresets.demonTether(player, target);
            case "corruptionSeep" -> StreamPresets.corruptionSeep(player, target);
            case "arcaneBolt" -> StreamPresets.arcaneBolt(player, target);
            case "emberMote" -> StreamPresets.emberMote(target).scale(0.3f).lifetime(100);
            case "soulWisp" -> StreamPresets.soulWisp(target).scale(0.3f).lifetime(100);
            case "voidMark" -> StreamPresets.voidMark(target).scale(0.3f).lifetime(100);
            default -> null;
        };
    }
}
