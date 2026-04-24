package com.breakinblocks.neovitae.common.command;

import com.breakinblocks.neovitae.NeoVitae;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ShowcaseCommand {

    private static final int BLOCK_COLS = 16;
    private static final int BLOCK_STRIDE = 2;
    private static final int WALL_HEIGHT = 4;
    private static final int SECTION_GAP = 6;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("nv-showcase")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(ShowcaseCommand::placeShowcase)
        );
    }

    static int placeShowcase(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos origin = BlockPos.containing(source.getPosition());
        int baseY = origin.getY();

        List<Holder.Reference<Block>> blocks = new ArrayList<>();
        BuiltInRegistries.BLOCK.listElements().forEach(ref -> {
            if (ref.key().identifier().getNamespace().equals(NeoVitae.MODID)) {
                blocks.add(ref);
            }
        });
        blocks.sort(Comparator.comparing(ref -> ref.key().identifier().getPath()));

        List<Holder.Reference<Item>> items = new ArrayList<>();
        BuiltInRegistries.ITEM.listElements().forEach(ref -> {
            if (!ref.key().identifier().getNamespace().equals(NeoVitae.MODID)) return;
            String path = ref.key().identifier().getPath();
            if (path.startsWith("array_") || path.equals("spatial_rift")) return;
            items.add(ref);
        });
        items.sort(Comparator.comparing(ref -> ref.key().identifier().getPath()));

        int blockRows = (blocks.size() + BLOCK_COLS - 1) / BLOCK_COLS;
        int blockSectionDepth = blockRows * BLOCK_STRIDE;
        int itemCols = (items.size() + WALL_HEIGHT - 1) / WALL_HEIGHT;
        int totalWidth = Math.max(BLOCK_COLS * BLOCK_STRIDE, itemCols);
        int totalDepth = blockSectionDepth + SECTION_GAP + 2;

        floorPlatform(level, origin.getX() - 1, baseY - 1, origin.getZ() - 1, totalWidth + 2, totalDepth + 2);

        int placedBlocks = placeBlockGrid(level, blocks, origin.getX(), baseY, origin.getZ());

        int wallZ = origin.getZ() + blockSectionDepth + SECTION_GAP;
        int placedItems = placeItemWall(level, items, origin.getX(), baseY, wallZ);

        final int b = placedBlocks;
        final int it = placedItems;
        source.sendSuccess(() -> Component.literal(
                "Placed " + b + " blocks and " + it + " items on frames"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int placeBlockGrid(ServerLevel level, List<Holder.Reference<Block>> blocks,
                                       int startX, int y, int startZ) {
        int placed = 0;
        for (int i = 0; i < blocks.size(); i++) {
            int row = i / BLOCK_COLS;
            int col = i % BLOCK_COLS;
            BlockPos pos = new BlockPos(startX + col * BLOCK_STRIDE, y, startZ + row * BLOCK_STRIDE);
            Holder.Reference<Block> ref = blocks.get(i);
            try {
                BlockState state = ref.value().defaultBlockState();
                level.setBlock(pos, state, 2);
                placed++;
            } catch (Exception e) {
                NeoVitae.LOGGER.warn("Failed to place showcase block {}: {}", ref.key().identifier(), e.getMessage());
            }
        }
        return placed;
    }

    private static int placeItemWall(ServerLevel level, List<Holder.Reference<Item>> items,
                                      int startX, int baseY, int wallZ) {
        int placed = 0;
        BlockState backing = Blocks.OAK_PLANKS.defaultBlockState();
        int cols = (items.size() + WALL_HEIGHT - 1) / WALL_HEIGHT;

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < WALL_HEIGHT; y++) {
                level.setBlock(new BlockPos(startX + x, baseY + y, wallZ), backing, 2);
                level.setBlock(new BlockPos(startX + x, baseY + y, wallZ + 1), Blocks.AIR.defaultBlockState(), 2);
            }
        }

        for (int i = 0; i < items.size(); i++) {
            int row = i / cols;
            int col = i % cols;
            int yOffset = WALL_HEIGHT - 1 - row;
            BlockPos framePos = new BlockPos(startX + col, baseY + yOffset, wallZ + 1);

            Holder.Reference<Item> ref = items.get(i);
            try {
                ItemFrame frame = new ItemFrame(level, framePos, Direction.SOUTH);
                frame.setItem(new ItemStack(ref.value()), false);
                level.addFreshEntity(frame);
                placed++;
            } catch (Exception e) {
                NeoVitae.LOGGER.warn("Failed to place showcase item {}: {}", ref.key().identifier(), e.getMessage());
            }
        }
        return placed;
    }

    private static void floorPlatform(ServerLevel level, int x, int y, int z, int width, int depth) {
        BlockState stone = Blocks.SMOOTH_STONE.defaultBlockState();
        for (int dx = 0; dx < width; dx++) {
            for (int dz = 0; dz < depth; dz++) {
                level.setBlock(new BlockPos(x + dx, y, z + dz), stone, 2);
            }
        }
    }
}
