package com.breakinblocks.neovitae.common.command;

import com.breakinblocks.neovitae.NeoVitae;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.world.level.block.StructureBlock;

public class DungeonShowcaseCommand {

    private static final int PADDING = 5;
    private static final int COLUMNS = 6;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("nv-dungeon-showcase")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(DungeonShowcaseCommand::placeShowcase)
        );
    }

    static int placeShowcase(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos origin = BlockPos.containing(source.getPosition());

        // Start placement at a clean Y level on superflat
        BlockPos startPos = new BlockPos(origin.getX(), 4, origin.getZ());

        StructureTemplateManager templateManager = level.getStructureManager();
        ResourceManager resourceManager = level.getServer().getResourceManager();

        // Find all structure NBT files under the neovitae namespace
        Map<Identifier, Resource> structureResources = resourceManager.listResources(
                "structure", rl -> rl.getNamespace().equals("neovitae") && rl.getPath().endsWith(".nbt")
        );

        List<StructureEntry> entries = new ArrayList<>();

        for (Identifier fullPath : structureResources.keySet()) {
            // Convert "structure/standard/antechamber.nbt" -> "neovitae:standard/antechamber"
            String path = fullPath.getPath();
            path = path.substring("structure/".length(), path.length() - ".nbt".length());
            Identifier structureId = NeoVitae.rl(path);

            Optional<StructureTemplate> template = templateManager.get(structureId);
            if (template.isPresent()) {
                Vec3i size = template.get().getSize();
                entries.add(new StructureEntry(structureId, path, size));
            }
        }

        if (entries.isEmpty()) {
            source.sendFailure(Component.translatable("command.neovitae.dungeon_showcase.no_structures"));
            return 0;
        }

        // Sort by category (directory), then by name
        entries.sort(Comparator.comparing(e -> e.path));

        source.sendSuccess(() -> Component.translatable("command.neovitae.dungeon_showcase.placing", entries.size()), true);

        int curX = startPos.getX();
        int curZ = startPos.getZ();
        int col = 0;
        int rowMaxDepth = 0;
        int placed = 0;
        String lastCategory = "";

        StructurePlaceSettings settings = new StructurePlaceSettings();
        RandomSource rand = level.getRandom();

        for (StructureEntry entry : entries) {
            // Add extra gap between categories
            String category = entry.path.contains("/") ? entry.path.substring(0, entry.path.indexOf('/')) : "root";
            if (!category.equals(lastCategory) && !lastCategory.isEmpty()) {
                // New category - start a new row
                curX = startPos.getX();
                curZ += rowMaxDepth + PADDING * 2;
                rowMaxDepth = 0;
                col = 0;
            }
            lastCategory = category;

            Vec3i size = entry.size;
            int placeY = startPos.getY() + 1;

            // Clear area and place a stone platform
            clearAndPlatform(level, curX - 1, startPos.getY(), curZ - 1, size.getX() + 2, size.getZ() + 2);

            // Place the structure
            BlockPos structurePos = new BlockPos(curX, placeY, curZ);
            Optional<StructureTemplate> template = templateManager.get(entry.id);
            if (template.isPresent()) {
                template.get().placeInWorld(level, structurePos, structurePos, settings, rand, 2);
            }

            // Place structure block in SAVE mode one block below the structure, offset to the side
            BlockPos sbPos = new BlockPos(curX - 1, startPos.getY(), curZ - 1);
            placeStructureBlock(level, sbPos, entry.id, size);

            // Place a sign labeling the structure
            BlockPos signPos = new BlockPos(curX - 1, startPos.getY() + 1, curZ - 1);
            placeLabel(level, signPos, entry.path);

            placed++;
            col++;

            // Track max depth in this row
            rowMaxDepth = Math.max(rowMaxDepth, size.getZ());

            if (col >= COLUMNS) {
                curX = startPos.getX();
                curZ += rowMaxDepth + PADDING;
                rowMaxDepth = 0;
                col = 0;
            } else {
                curX += size.getX() + PADDING;
            }
        }

        int finalPlaced = placed;
        source.sendSuccess(() -> Component.translatable("command.neovitae.dungeon_showcase.placed", finalPlaced), true);
        source.sendSuccess(() -> Component.translatable("command.neovitae.dungeon_showcase.preconfigured"), false);
        source.sendSuccess(() -> Component.translatable("command.neovitae.dungeon_showcase.save_hint"), false);

        return Command.SINGLE_SUCCESS;
    }

    private static void clearAndPlatform(ServerLevel level, int x, int y, int z, int width, int depth) {
        BlockState stone = Blocks.SMOOTH_STONE.defaultBlockState();
        for (int dx = 0; dx < width; dx++) {
            for (int dz = 0; dz < depth; dz++) {
                level.setBlock(new BlockPos(x + dx, y, z + dz), stone, 2);
            }
        }
    }

    private static void placeStructureBlock(ServerLevel level, BlockPos pos, Identifier structureId, Vec3i size) {
        level.setBlock(pos, Blocks.STRUCTURE_BLOCK.defaultBlockState().setValue(
                StructureBlock.MODE, StructureMode.SAVE), 2);

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof StructureBlockEntity sb) {
            sb.setStructureName(structureId);
            sb.setStructureSize(size);
            // Offset: structure starts 1 block east and 1 block south and 1 block up from the structure block
            sb.setStructurePos(new BlockPos(1, 1, 1));
            sb.setChanged();
        }
    }

    private static void placeLabel(ServerLevel level, BlockPos pos, String name) {
        level.setBlock(pos, Blocks.OAK_SIGN.defaultBlockState(), 2);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SignBlockEntity sign) {
            // Split name into lines if needed
            String[] parts = name.split("/");
            SignText text = sign.getFrontText();
            if (parts.length >= 2) {
                text = text.setMessage(0, Component.literal(parts[0]));
                text = text.setMessage(1, Component.literal(parts[1]));
            } else {
                text = text.setMessage(0, Component.literal(name));
            }
            sign.setText(text, true);
            sign.setChanged();
        }
    }

    private record StructureEntry(Identifier id, String path, Vec3i size) {}
}
