package com.breakinblocks.neovitae.common.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
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
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.particle.NVParticles;

import java.util.List;

public class StreamTestCommand {

    private static final List<String> PRESET_NAMES = List.of(
            "bloodTendril", "soulSiphon", "voidTendril", "lifePulse",
            "demonTether", "corruptionSeep", "arcaneBolt",
            "blockBolt", "blockBolt2", "blockBolt3", "blockBolt4", "blockBolt5", "blockBolt6",
            "emberMote", "soulWisp", "voidMark", "ritual"
    );

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_PRESETS =
            (context, builder) -> SharedSuggestionProvider.suggest(PRESET_NAMES, builder);

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("stream")
                .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("preset", StringArgumentType.word())
                        .suggests(SUGGEST_PRESETS)
                        .executes(StreamTestCommand::execute));
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

        if (preset.equals("emberMote") || preset.equals("soulWisp") || preset.equals("voidMark")) {
            spawnBlobParticles(level, targetPos, preset);
            source.sendSuccess(() -> Component.literal("Spawned " + preset + " at " + targetPos.toShortString()), false);
            return 1;
        }

        if (preset.equals("ritual")) {
            net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(targetPos);
            if (be instanceof com.breakinblocks.neovitae.common.blockentity.AraVitaeTile altar) {
                altar.setActive(true);
                altar.setCooldownAfterCrafting(200);
                altar.setChanged();
                level.sendBlockUpdated(targetPos, level.getBlockState(targetPos), level.getBlockState(targetPos), 3);
                source.sendSuccess(() -> Component.literal("Activated ritual circle on Ara Vitae at " + targetPos.toShortString() + " for 10 seconds"), false);
            } else {
                source.sendFailure(Component.literal("No Ara Vitae at " + targetPos.toShortString() + " (found: " + (be != null ? be.getClass().getSimpleName() : "null") + ")"));
            }
            return 1;
        }

        StreamEffect.Builder builder = getPreset(preset, player, targetPos);

        if (builder == null) {
            source.sendFailure(Component.literal("Unknown preset: " + preset));
            return 0;
        }

        builder.build().sendToNearby(level, player.blockPosition(), 128);
        source.sendSuccess(() -> Component.literal("Fired " + preset + " to " + targetPos.toShortString()), false);
        return 1;
    }

    private static void spawnBlobParticles(ServerLevel level, net.minecraft.core.BlockPos pos, String type) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1.0;
        double z = pos.getZ() + 0.5;

        int color;
        int glowColor;
        switch (type) {
            case "emberMote" -> { color = 0xFF6600; glowColor = 0xFFCC00; }
            case "soulWisp" -> { color = 0xAADDFF; glowColor = 0xDDEEFF; }
            case "voidMark" -> { color = 0x1A0033; glowColor = 0x330066; }
            default -> { color = 0xFFFFFF; glowColor = 0xFFFFFF; }
        }

        level.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), color),
                x, y, z, 8, 0.15, 0.15, 0.15, 0.01);
        level.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), glowColor),
                x, y, z, 4, 0.1, 0.1, 0.1, 0);
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
            case "blockBolt" -> StreamPresets.blockBolt(player, target);
            case "blockBolt2" -> StreamPresets.blockBolt2(player, target);
            case "blockBolt3" -> StreamPresets.blockBolt3(player, target);
            case "blockBolt4" -> StreamPresets.blockBolt4(player, target);
            case "blockBolt5" -> StreamPresets.blockBolt5(player, target);
            case "blockBolt6" -> StreamPresets.blockBolt6(player, target);
            default -> null;
        };
    }
}
