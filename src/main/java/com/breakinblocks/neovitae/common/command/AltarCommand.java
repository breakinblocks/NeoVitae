package com.breakinblocks.neovitae.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Arrays;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.registry.AltarComponent;
import com.breakinblocks.neovitae.common.registry.AltarTier;
import com.breakinblocks.neovitae.common.structure.NVMultiblock;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.ArrayList;
import java.util.List;

/**
 * /neovitae altar [tier] - Places an Ara Vitae at the player's feet and constructs the
 * full multiblock around it. With no argument the highest available tier is built; pass a
 * tier number to build that tier instead. Rune positions are filled with a fixed
 * distribution: 10 efficiency, 19 acceleration, 9 speed, 15 augmented capacity,
 * 22 dislocation, and the remainder as sacrifice.
 */
public class AltarCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("altar")
                .requires(s -> s.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> execute(ctx, -1))
                .then(Commands.argument("tier", IntegerArgumentType.integer(1))
                        .suggests((c, b) -> {
                            for (AltarTier t : NVMultiblock.TIER_LIST) {
                                b.suggest(t.tier());
                            }
                            return b.buildFuture();
                        })
                        .executes(ctx -> execute(ctx, IntegerArgumentType.getInteger(ctx, "tier"))));
    }

    public static int execute(CommandContext<CommandSourceStack> ctx, int requestedTier) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = player.serverLevel();

        AltarTier[] tiers = NVMultiblock.TIER_LIST;
        if (tiers.length == 0) {
            source.sendFailure(Component.literal("No altar tiers are loaded."));
            return 0;
        }

        AltarTier selectedTier;
        if (requestedTier < 0) {
            selectedTier = tiers[tiers.length - 1];
        } else {
            selectedTier = Arrays.stream(tiers)
                    .filter(t -> t.tier() == requestedTier)
                    .findFirst()
                    .orElse(null);
            if (selectedTier == null) {
                String available = Arrays.stream(tiers)
                        .map(t -> String.valueOf(t.tier()))
                        .collect(Collectors.joining(", "));
                source.sendFailure(Component.literal(
                        "No altar tier " + requestedTier + ". Available tiers: " + available));
                return 0;
            }
        }

        BlockPos origin = player.blockPosition();
        RegistryAccess registries = level.registryAccess();

        // Pre-build the rune dispenser queue in the user-specified order.
        List<Block> runeQueue = buildRuneQueue(selectedTier);

        int placed = 0;
        int runeIdx = 0;
        for (AltarComponent component : selectedTier.components()) {
            BlockPos pos = origin.offset(component.pos());

            BlockState state;
            if (isRune(component)) {
                Block runeBlock = runeIdx < runeQueue.size()
                        ? runeQueue.get(runeIdx++)
                        : NVBlocks.RUNE_2_SACRIFICE.block().get();
                state = runeBlock.defaultBlockState();
            } else {
                List<BlockState> options = NVMultiblock.getDisplayStates(component, registries);
                if (options.isEmpty()) continue;
                state = options.get(0);
            }

            try {
                level.setBlock(pos, state, 2);
                placed++;
            } catch (Exception e) {
                NeoVitae.LOGGER.warn("AltarCommand: failed to place at {}: {}", pos, e.getMessage());
            }
        }

        int placedFinal = placed;
        int tierFinal = selectedTier.tier();
        source.sendSuccess(() -> Component.literal(
                "Placed tier " + tierFinal + " altar at " + origin.toShortString() + " (" + placedFinal + " blocks)"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static boolean isRune(AltarComponent component) {
        return component.material().tag()
                && component.material().id().equals(NVTags.Blocks.RUNES.location());
    }

    private static List<Block> buildRuneQueue(AltarTier tier) {
        int runeSlots = (int) tier.components().stream().filter(AltarCommand::isRune).count();
        int efficiency = 10;
        int acceleration = 19;
        int speed = 9;
        int capacityAugmented = 15;
        int dislocation = 22;
        int specified = efficiency + acceleration + speed + capacityAugmented + dislocation;
        int sacrifice = Math.max(0, runeSlots - specified);

        List<Block> queue = new ArrayList<>(runeSlots);
        addCopies(queue, NVBlocks.RUNE_2_EFFICIENCY.block().get(), efficiency);
        addCopies(queue, NVBlocks.RUNE_2_ACCELERATION.block().get(), acceleration);
        addCopies(queue, NVBlocks.RUNE_2_SPEED.block().get(), speed);
        addCopies(queue, NVBlocks.RUNE_2_CAPACITY_AUGMENTED.block().get(), capacityAugmented);
        addCopies(queue, NVBlocks.RUNE_2_DISLOCATION.block().get(), dislocation);
        addCopies(queue, NVBlocks.RUNE_2_SACRIFICE.block().get(), sacrifice);
        return queue;
    }

    private static void addCopies(List<Block> list, Block block, int n) {
        for (int i = 0; i < n; i++) list.add(block);
    }
}
