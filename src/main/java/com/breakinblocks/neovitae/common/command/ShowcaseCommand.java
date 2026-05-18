package com.breakinblocks.neovitae.common.command;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.BlockShapedExplosive;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.SpectralBlock;
import com.breakinblocks.neovitae.common.datamap.ImperfectRitualStats;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.registry.AltarComponent;
import com.breakinblocks.neovitae.common.registry.AltarTier;
import com.breakinblocks.neovitae.common.structure.NVMultiblock;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.ImperfectRitual;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualLayouts;
import com.breakinblocks.neovitae.ritual.RitualRegistry;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ShowcaseCommand {

    private static final int BLOCK_COLS = 16;
    private static final int BLOCK_STRIDE = 2;
    private static final int WALL_HEIGHT = 4;
    private static final int SECTION_GAP = 6;

    private static final int ALTAR_PADDING = 3;
    private static final int RITUAL_CELL = 15;
    private static final int RITUAL_COLS = 6;
    private static final int IMPERFECT_CELL = 3;
    private static final int IMPERFECT_COLS = 12;

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("showcase")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ShowcaseCommand::placeShowcase);
    }

    public static int placeShowcase(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        BlockPos origin = BlockPos.containing(source.getPosition());
        int baseY = origin.getY();
        RegistryAccess registries = level.registryAccess();

        List<Block> blocks = discoverBlocks();
        List<Item> items = discoverFrameItems();
        List<Ritual> rituals = sortedRituals();
        List<ImperfectRitual> imperfects = sortedImperfects();
        AltarTier[] tiers = NVMultiblock.TIER_LIST;

        int blockRows = blocks.isEmpty() ? 0 : (blocks.size() + BLOCK_COLS - 1) / BLOCK_COLS;
        int blockSectionDepth = blockRows * BLOCK_STRIDE;
        int itemCols = items.isEmpty() ? 0 : (items.size() + WALL_HEIGHT - 1) / WALL_HEIGHT;

        int altarWidth = altarSectionWidth(tiers);
        int altarDepth = altarSectionDepth(tiers);

        int ritualRows = rituals.isEmpty() ? 0 : (rituals.size() + RITUAL_COLS - 1) / RITUAL_COLS;
        int ritualWidth = ritualRows == 0 ? 0 : RITUAL_COLS * RITUAL_CELL;
        int ritualDepth = ritualRows * RITUAL_CELL;

        int imperfectRows = imperfects.isEmpty() ? 0 : (imperfects.size() + IMPERFECT_COLS - 1) / IMPERFECT_COLS;
        int imperfectWidth = imperfectRows == 0 ? 0 : IMPERFECT_COLS * IMPERFECT_CELL;
        int imperfectDepth = imperfectRows * IMPERFECT_CELL;

        int totalWidth = Math.max(Math.max(BLOCK_COLS * BLOCK_STRIDE, itemCols),
                Math.max(altarWidth, Math.max(ritualWidth, imperfectWidth)));

        int totalDepth = blockSectionDepth
                + (itemCols > 0 ? SECTION_GAP + 2 : 0)
                + (altarDepth > 0 ? SECTION_GAP + altarDepth : 0)
                + (ritualDepth > 0 ? SECTION_GAP + ritualDepth : 0)
                + (imperfectDepth > 0 ? SECTION_GAP + imperfectDepth : 0);

        floorPlatform(level, origin.getX() - 1, baseY - 1, origin.getZ() - 1, totalWidth + 2, totalDepth + 2);

        int placedBlocks = placeBlockGrid(level, blocks, origin.getX(), baseY, origin.getZ());

        int cursorZ = origin.getZ() + blockSectionDepth;
        int placedItems = 0;
        if (itemCols > 0) {
            cursorZ += SECTION_GAP;
            placedItems = placeItemWall(level, items, origin.getX(), baseY, cursorZ);
            cursorZ += 2;
        }

        int placedAltars = 0;
        if (altarDepth > 0) {
            cursorZ += SECTION_GAP;
            placedAltars = placeAltarTiers(level, tiers, registries, origin.getX(), baseY, cursorZ);
            cursorZ += altarDepth;
        }

        int placedRituals = 0;
        if (ritualDepth > 0) {
            cursorZ += SECTION_GAP;
            placedRituals = placeRituals(level, rituals, origin.getX(), baseY, cursorZ);
            cursorZ += ritualDepth;
        }

        int placedImperfects = 0;
        if (imperfectDepth > 0) {
            cursorZ += SECTION_GAP;
            placedImperfects = placeImperfectRituals(level, imperfects, origin.getX(), baseY, cursorZ);
        }

        final int b = placedBlocks, it = placedItems, a = placedAltars, r = placedRituals, ir = placedImperfects;
        source.sendSuccess(() -> Component.literal(
                "Showcase placed: " + b + " blocks, " + it + " framed items, "
                        + a + " altar tiers, " + r + " rituals, " + ir + " imperfect rituals"), true);
        return Command.SINGLE_SUCCESS;
    }

    // ---------- discovery + filtering ----------

    private static List<Block> discoverBlocks() {
        List<Block> out = new ArrayList<>();
        BuiltInRegistries.BLOCK.listElements().forEach(ref -> {
            if (!ref.key().identifier().getNamespace().equals(NeoVitae.MODID)) return;
            if (ref.key().identifier().getPath().equals("test_energy_block")) return;
            Block block = ref.value();
            if (block instanceof LiquidBlock) return;
            if (block instanceof BlockShapedExplosive) return;
            if (block instanceof SpectralBlock) return;
            if (block == NVBlocks.SPATIAL_RIFT.block().get()) return;
            out.add(block);
        });
        out.sort(Comparator.comparing(b -> BuiltInRegistries.BLOCK.getKey(b).getPath()));
        return out;
    }

    private static List<Item> discoverFrameItems() {
        List<Item> out = new ArrayList<>();
        BuiltInRegistries.ITEM.listElements().forEach(ref -> {
            if (!ref.key().identifier().getNamespace().equals(NeoVitae.MODID)) return;
            String path = ref.key().identifier().getPath();
            if (path.startsWith("array_") || path.equals("spatial_rift")) return;
            Item item = ref.value();
            if (item instanceof BlockItem) return;
            out.add(item);
        });
        out.sort(Comparator.comparing(i -> BuiltInRegistries.ITEM.getKey(i).getPath()));
        return out;
    }

    private static List<Ritual> sortedRituals() {
        List<Ritual> out = new ArrayList<>(RitualRegistry.getAllRituals());
        out.sort(Comparator.comparing(r -> {
            var id = RitualRegistry.getId(r);
            return id == null ? "" : id.getPath();
        }));
        return out;
    }

    private static List<ImperfectRitual> sortedImperfects() {
        List<ImperfectRitual> out = new ArrayList<>(RitualRegistry.getAllImperfectRituals());
        out.sort(Comparator.comparing(r -> {
            var id = RitualRegistry.getId(r);
            return id == null ? "" : id.getPath();
        }));
        return out;
    }

    // ---------- block grid ----------

    private static int placeBlockGrid(ServerLevel level, List<Block> blocks, int startX, int y, int startZ) {
        int placed = 0;
        for (int i = 0; i < blocks.size(); i++) {
            int row = i / BLOCK_COLS;
            int col = i % BLOCK_COLS;
            BlockPos pos = new BlockPos(startX + col * BLOCK_STRIDE, y, startZ + row * BLOCK_STRIDE);
            try {
                level.setBlock(pos, blocks.get(i).defaultBlockState(), 2);
                placed++;
            } catch (Exception e) {
                NeoVitae.LOGGER.warn("Failed to place showcase block {}: {}",
                        BuiltInRegistries.BLOCK.getKey(blocks.get(i)), e.getMessage());
            }
        }
        return placed;
    }

    // ---------- item wall ----------

    private static int placeItemWall(ServerLevel level, List<Item> items, int startX, int baseY, int wallZ) {
        int placed = 0;
        BlockState backing = Blocks.OAK_PLANKS.defaultBlockState();
        int cols = (items.size() + WALL_HEIGHT - 1) / WALL_HEIGHT;

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < WALL_HEIGHT; y++) {
                level.setBlock(new BlockPos(startX + x, baseY + y, wallZ), backing, 2);
                level.setBlock(new BlockPos(startX + x, baseY + y, wallZ - 1), Blocks.AIR.defaultBlockState(), 2);
            }
        }

        for (int i = 0; i < items.size(); i++) {
            int row = i / cols;
            int col = i % cols;
            int yOffset = WALL_HEIGHT - 1 - row;
            BlockPos framePos = new BlockPos(startX + col, baseY + yOffset, wallZ - 1);

            Item item = items.get(i);
            try {
                ItemFrame frame = new ItemFrame(level, framePos, Direction.NORTH);
                frame.setItem(new ItemStack(item), false);
                level.addFreshEntity(frame);
                placed++;
            } catch (Exception e) {
                NeoVitae.LOGGER.warn("Failed to place showcase item {}: {}",
                        BuiltInRegistries.ITEM.getKey(item), e.getMessage());
            }
        }
        return placed;
    }

    // ---------- altars ----------

    private static int altarSectionWidth(AltarTier[] tiers) {
        if (tiers == null || tiers.length == 0) return 0;
        int width = 0;
        for (AltarTier tier : tiers) {
            if (tier == null) continue;
            int[] bb = tierBoundingBox(tier);
            width += (bb[3] - bb[0] + 1) + ALTAR_PADDING;
        }
        return Math.max(0, width - ALTAR_PADDING);
    }

    private static int altarSectionDepth(AltarTier[] tiers) {
        if (tiers == null || tiers.length == 0) return 0;
        int depth = 0;
        for (AltarTier tier : tiers) {
            if (tier == null) continue;
            int[] bb = tierBoundingBox(tier);
            depth = Math.max(depth, bb[5] - bb[2] + 1);
        }
        return depth + 2;
    }

    private static int placeAltarTiers(ServerLevel level, AltarTier[] tiers, RegistryAccess registries,
                                       int startX, int baseY, int startZ) {
        int placed = 0;
        int cursorX = startX;
        for (AltarTier tier : tiers) {
            if (tier == null) continue;
            int[] bb = tierBoundingBox(tier);
            int minX = bb[0], minY = bb[1], minZ = bb[2];
            int maxX = bb[3], maxZ = bb[5];
            int width = maxX - minX + 1;

            // Altar (0,0,0) sits at altarPos; lowest component lands at baseY+1
            int altarX = cursorX - minX;
            int altarY = baseY + 1 - minY;
            int altarZ = startZ - minZ;

            for (AltarComponent component : tier.components()) {
                BlockPos pos = new BlockPos(
                        altarX + component.pos().getX(),
                        altarY + component.pos().getY(),
                        altarZ + component.pos().getZ());
                BlockState state = pickAltarComponentState(component, registries);
                if (state != null) {
                    try {
                        level.setBlock(pos, state, 2);
                    } catch (Exception e) {
                        NeoVitae.LOGGER.warn("Failed to place altar component at {}: {}", pos, e.getMessage());
                    }
                }
            }

            BlockPos signPos = new BlockPos(cursorX, baseY + 1, startZ + (maxZ - minZ + 1) + 1);
            placeLabel(level, signPos, "Altar T" + (tier.tier() + 1));

            cursorX += width + ALTAR_PADDING;
            placed++;
        }
        return placed;
    }

    private static int[] tierBoundingBox(AltarTier tier) {
        int minX = 0, minY = 0, minZ = 0, maxX = 0, maxY = 0, maxZ = 0;
        for (AltarComponent c : tier.components()) {
            BlockPos p = c.pos();
            if (p.getX() < minX) minX = p.getX();
            if (p.getY() < minY) minY = p.getY();
            if (p.getZ() < minZ) minZ = p.getZ();
            if (p.getX() > maxX) maxX = p.getX();
            if (p.getY() > maxY) maxY = p.getY();
            if (p.getZ() > maxZ) maxZ = p.getZ();
        }
        return new int[]{minX, minY, minZ, maxX, maxY, maxZ};
    }

    private static BlockState pickAltarComponentState(AltarComponent component, RegistryAccess registries) {
        List<BlockState> states = NVMultiblock.getDisplayStates(component, registries);
        return states.isEmpty() ? null : states.get(0);
    }

    // ---------- rituals ----------

    private static int placeRituals(ServerLevel level, List<Ritual> rituals, int startX, int baseY, int startZ) {
        int placed = 0;
        for (int i = 0; i < rituals.size(); i++) {
            int row = i / RITUAL_COLS;
            int col = i % RITUAL_COLS;
            int cellOriginX = startX + col * RITUAL_CELL + RITUAL_CELL / 2;
            int cellOriginZ = startZ + row * RITUAL_CELL + RITUAL_CELL / 2;

            Ritual ritual = rituals.get(i);
            List<RitualComponent> components = RitualLayouts.get(level, ritual);

            BlockPos masterPos = new BlockPos(cellOriginX, baseY + 1, cellOriginZ);
            try {
                level.setBlock(masterPos, NVBlocks.MASTER_RITUAL_STONE.block().get().defaultBlockState(), 2);
            } catch (Exception e) {
                NeoVitae.LOGGER.warn("Failed to place master ritual stone for {}: {}",
                        RitualRegistry.getId(ritual), e.getMessage());
            }

            for (RitualComponent comp : components) {
                BlockPos runePos = masterPos.offset(comp.offset());
                Block runeBlock = ritualStoneFor(comp.runeType());
                try {
                    level.setBlock(runePos, runeBlock.defaultBlockState(), 2);
                } catch (Exception e) {
                    NeoVitae.LOGGER.warn("Failed to place rune {} at {}: {}", comp.runeType(), runePos, e.getMessage());
                }
            }

            var id = RitualRegistry.getId(ritual);
            String name = id == null ? "ritual" : id.getPath();
            BlockPos signPos = new BlockPos(cellOriginX, baseY + 1, startZ + row * RITUAL_CELL);
            placeLabel(level, signPos, name);

            placed++;
        }
        return placed;
    }

    private static Block ritualStoneFor(EnumRuneType rune) {
        return switch (rune) {
            case BLANK -> NVBlocks.BLANK_RITUAL_STONE.block().get();
            case WATER -> NVBlocks.WATER_RITUAL_STONE.block().get();
            case FIRE -> NVBlocks.FIRE_RITUAL_STONE.block().get();
            case EARTH -> NVBlocks.EARTH_RITUAL_STONE.block().get();
            case AIR -> NVBlocks.AIR_RITUAL_STONE.block().get();
            case DUSK -> NVBlocks.DUSK_RITUAL_STONE.block().get();
            case DAWN -> NVBlocks.DAWN_RITUAL_STONE.block().get();
        };
    }

    // ---------- imperfect rituals ----------

    private static int placeImperfectRituals(ServerLevel level, List<ImperfectRitual> imperfects,
                                             int startX, int baseY, int startZ) {
        int placed = 0;
        var registry = RitualRegistry.getImperfectRitualRegistry();

        for (int i = 0; i < imperfects.size(); i++) {
            int row = i / IMPERFECT_COLS;
            int col = i % IMPERFECT_COLS;
            int cellX = startX + col * IMPERFECT_CELL;
            int cellZ = startZ + row * IMPERFECT_CELL;

            ImperfectRitual ritual = imperfects.get(i);
            BlockPos stonePos = new BlockPos(cellX + 1, baseY + 1, cellZ + 1);

            try {
                level.setBlock(stonePos, NVBlocks.IMPERFECT_RITUAL_STONE.block().get().defaultBlockState(), 2);
            } catch (Exception e) {
                NeoVitae.LOGGER.warn("Failed to place imperfect ritual stone for {}: {}",
                        RitualRegistry.getId(ritual), e.getMessage());
            }

            Block catalyst = pickImperfectCatalyst(registry, ritual);
            if (catalyst != null) {
                try {
                    level.setBlock(stonePos.above(), catalyst.defaultBlockState(), 2);
                } catch (Exception e) {
                    NeoVitae.LOGGER.warn("Failed to place imperfect catalyst at {}: {}", stonePos.above(), e.getMessage());
                }
            }

            var id = RitualRegistry.getId(ritual);
            String name = id == null ? "imperfect" : id.getPath();
            BlockPos signPos = new BlockPos(cellX + 1, baseY + 1, cellZ);
            placeLabel(level, signPos, name);

            placed++;
        }
        return placed;
    }

    private static Block pickImperfectCatalyst(net.minecraft.core.Registry<ImperfectRitual> registry, ImperfectRitual ritual) {
        if (registry == null) return null;
        Holder<ImperfectRitual> holder = registry.wrapAsHolder(ritual);
        ImperfectRitualStats stats = holder.getData(NVDataMaps.IMPERFECT_RITUAL_STATS);
        if (stats == null) return null;
        if (stats.block().isPresent()) return stats.block().get();
        if (stats.blockTag().isPresent()) {
            for (Holder<Block> b : BuiltInRegistries.BLOCK.getTagOrEmpty(stats.blockTag().get())) {
                return b.value();
            }
        }
        return null;
    }

    // ---------- shared helpers ----------

    private static void floorPlatform(ServerLevel level, int x, int y, int z, int width, int depth) {
        BlockState stone = Blocks.SMOOTH_STONE.defaultBlockState();
        for (int dx = 0; dx < width; dx++) {
            for (int dz = 0; dz < depth; dz++) {
                level.setBlock(new BlockPos(x + dx, y, z + dz), stone, 2);
            }
        }
    }

    private static void placeLabel(ServerLevel level, BlockPos pos, String name) {
        try {
            level.setBlock(pos, Blocks.OAK_SIGN.defaultBlockState(), 2);
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SignBlockEntity sign) {
                SignText text = sign.getFrontText();
                String[] parts = name.split("/");
                if (parts.length >= 2) {
                    text = text.setMessage(0, Component.literal(parts[0]));
                    text = text.setMessage(1, Component.literal(parts[1]));
                } else if (name.length() > 15) {
                    text = text.setMessage(0, Component.literal(name.substring(0, 15)));
                    text = text.setMessage(1, Component.literal(name.substring(15, Math.min(name.length(), 30))));
                } else {
                    text = text.setMessage(0, Component.literal(name));
                }
                sign.setText(text, true);
                sign.setChanged();
            }
        } catch (Exception e) {
            NeoVitae.LOGGER.warn("Failed to place showcase sign at {}: {}", pos, e.getMessage());
        }
    }
}
