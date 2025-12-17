package com.breakinblocks.neovitae.common.block.dungeon;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.util.helper.BlockWithItemHolder;
import com.breakinblocks.neovitae.util.helper.BlockWithItemRegister;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registers all dungeon blocks using an enum-driven approach.
 * Each block type exists in 5 demon will variants (Raw, Corrosive, Destructive, Steadfast, Vengeful).
 */
public class DungeonBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(NeoVitae.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(NeoVitae.MODID);
    public static final BlockWithItemRegister REG = new BlockWithItemRegister(BLOCKS, ITEMS);

    // Block properties
    private static final BlockBehaviour.Properties DUNGEON_STONE_PROPS = BlockBehaviour.Properties.of()
            .strength(2.0F, 5.0F).sound(SoundType.STONE).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties DUNGEON_METAL_PROPS = BlockBehaviour.Properties.of()
            .strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties DUNGEON_EYE_PROPS = BlockBehaviour.Properties.of()
            .strength(2.0F, 5.0F).sound(SoundType.STONE).requiresCorrectToolForDrops().lightLevel(state -> 15);

    // Storage maps for variant blocks
    public static final Map<DungeonVariant, BlockWithItemHolder<Block, BlockItem>> DUNGEON_BRICK_1 = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<Block, BlockItem>> DUNGEON_BRICK_2 = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<Block, BlockItem>> DUNGEON_BRICK_3 = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<Block, BlockItem>> DUNGEON_STONE = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<Block, BlockItem>> DUNGEON_EYE = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<Block, BlockItem>> DUNGEON_POLISHED = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<Block, BlockItem>> DUNGEON_TILE = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<Block, BlockItem>> DUNGEON_SMALLBRICK = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<Block, BlockItem>> DUNGEON_TILESPECIAL = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<Block, BlockItem>> DUNGEON_METAL = new EnumMap<>(DungeonVariant.class);

    // Pillars
    public static final Map<DungeonVariant, BlockWithItemHolder<RotatedPillarBlock, BlockItem>> DUNGEON_PILLAR_CENTER = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<RotatedPillarBlock, BlockItem>> DUNGEON_PILLAR_SPECIAL = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<Block, BlockItem>> DUNGEON_PILLAR_CAP = new EnumMap<>(DungeonVariant.class);

    // Decorative - Stairs
    public static final Map<DungeonVariant, BlockWithItemHolder<StairBlock, BlockItem>> DUNGEON_BRICK_STAIRS = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<StairBlock, BlockItem>> DUNGEON_POLISHED_STAIRS = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<StairBlock, BlockItem>> DUNGEON_STONE_STAIRS = new EnumMap<>(DungeonVariant.class);

    // Decorative - Walls
    public static final Map<DungeonVariant, BlockWithItemHolder<WallBlock, BlockItem>> DUNGEON_BRICK_WALL = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<WallBlock, BlockItem>> DUNGEON_TILE_WALL = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<WallBlock, BlockItem>> DUNGEON_POLISHED_WALL = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<WallBlock, BlockItem>> DUNGEON_STONE_WALL = new EnumMap<>(DungeonVariant.class);

    // Decorative - Gates
    public static final Map<DungeonVariant, BlockWithItemHolder<FenceGateBlock, BlockItem>> DUNGEON_BRICK_GATE = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<FenceGateBlock, BlockItem>> DUNGEON_POLISHED_GATE = new EnumMap<>(DungeonVariant.class);

    // Decorative - Slabs
    public static final Map<DungeonVariant, BlockWithItemHolder<SlabBlock, BlockItem>> DUNGEON_BRICK_SLAB = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<SlabBlock, BlockItem>> DUNGEON_TILE_SLAB = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<SlabBlock, BlockItem>> DUNGEON_STONE_SLAB = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<SlabBlock, BlockItem>> DUNGEON_POLISHED_SLAB = new EnumMap<>(DungeonVariant.class);

    // Non-variant dungeon blocks
    public static BlockWithItemHolder<Block, BlockItem> DUNGEON_ORE;
    public static BlockWithItemHolder<Block, BlockItem> DUNGEON_BRICK_ASSORTED;

    // Functional dungeon blocks
    public static BlockWithItemHolder<BlockSpikes, BlockItem> SPIKES;
    public static BlockWithItemHolder<BlockSpikeTrap, BlockItem> SPIKE_TRAP;
    public static BlockWithItemHolder<BlockAlternator, BlockItem> ALTERNATOR;

    // Path blocks for incense altar (tagged with INCENSE_PATH_LEVEL_X)
    public static BlockWithItemHolder<Block, BlockItem> WOOD_BRICK_PATH;
    public static BlockWithItemHolder<Block, BlockItem> WOOD_TILE_PATH;
    public static BlockWithItemHolder<Block, BlockItem> STONE_BRICK_PATH;
    public static BlockWithItemHolder<Block, BlockItem> STONE_TILE_PATH;
    public static BlockWithItemHolder<Block, BlockItem> WORN_STONE_BRICK_PATH;
    public static BlockWithItemHolder<Block, BlockItem> WORN_STONE_TILE_PATH;
    public static BlockWithItemHolder<Block, BlockItem> OBSIDIAN_BRICK_PATH;
    public static BlockWithItemHolder<Block, BlockItem> OBSIDIAN_TILE_PATH;

    static {
        // Register non-variant blocks
        DUNGEON_ORE = REG.register("dungeon_ore", BlockBehaviour.Properties.of()
                .strength(3.0F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops(), new Item.Properties());
        DUNGEON_BRICK_ASSORTED = REG.register("dungeon_brick_assorted", BlockBehaviour.Properties.of()
                .strength(20.0F, 50.0F).sound(SoundType.STONE).requiresCorrectToolForDrops(), new Item.Properties());

        // Functional dungeon blocks
        SPIKES = REG.register("spikes", () -> new BlockSpikes(BlockBehaviour.Properties.of()
                .strength(2.0F, 5.0F).sound(SoundType.METAL).noOcclusion()));
        SPIKE_TRAP = REG.register("spike_trap", () -> new BlockSpikeTrap(BlockBehaviour.Properties.of()
                .strength(2.0F, 5.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()));
        ALTERNATOR = REG.register("alternator", () -> new BlockAlternator(BlockBehaviour.Properties.of()
                .strength(2.0F, 5.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()));

        // Path blocks for incense altar - path level is determined by tags
        BlockBehaviour.Properties pathProps = BlockBehaviour.Properties.of()
                .strength(2.0F, 5.0F).sound(SoundType.WOOD).requiresCorrectToolForDrops();
        BlockBehaviour.Properties stonePathProps = BlockBehaviour.Properties.of()
                .strength(2.0F, 5.0F).sound(SoundType.STONE).requiresCorrectToolForDrops();

        WOOD_BRICK_PATH = REG.register("wood_brick_path", pathProps, new Item.Properties());
        WOOD_TILE_PATH = REG.register("wood_tile_path", pathProps, new Item.Properties());
        STONE_BRICK_PATH = REG.register("stone_brick_path", stonePathProps, new Item.Properties());
        STONE_TILE_PATH = REG.register("stone_tile_path", stonePathProps, new Item.Properties());
        WORN_STONE_BRICK_PATH = REG.register("worn_stone_brick_path", stonePathProps, new Item.Properties());
        WORN_STONE_TILE_PATH = REG.register("worn_stone_tile_path", stonePathProps, new Item.Properties());
        OBSIDIAN_BRICK_PATH = REG.register("obsidian_brick_path", stonePathProps, new Item.Properties());
        OBSIDIAN_TILE_PATH = REG.register("obsidian_tile_path", stonePathProps, new Item.Properties());

        // Register all variant blocks
        for (DungeonVariant variant : DungeonVariant.values()) {
            registerVariantBlocks(variant);
        }
    }

    private static void registerVariantBlocks(DungeonVariant variant) {
        String suffix = variant.getSuffix();

        // Base blocks
        DUNGEON_BRICK_1.put(variant, registerSimple("dungeon_brick1" + suffix, DUNGEON_STONE_PROPS));
        DUNGEON_BRICK_2.put(variant, registerSimple("dungeon_brick2" + suffix, DUNGEON_STONE_PROPS));
        DUNGEON_BRICK_3.put(variant, registerSimple("dungeon_brick3" + suffix, DUNGEON_STONE_PROPS));
        DUNGEON_STONE.put(variant, registerSimple("dungeon_stone" + suffix, DUNGEON_STONE_PROPS));
        DUNGEON_EYE.put(variant, registerSimple("dungeon_eye" + suffix, DUNGEON_EYE_PROPS));
        DUNGEON_POLISHED.put(variant, registerSimple("dungeon_polished" + suffix, DUNGEON_STONE_PROPS));
        DUNGEON_TILE.put(variant, registerSimple("dungeon_tile" + suffix, DUNGEON_STONE_PROPS));
        DUNGEON_SMALLBRICK.put(variant, registerSimple("dungeon_smallbrick" + suffix, DUNGEON_STONE_PROPS));
        DUNGEON_TILESPECIAL.put(variant, registerSimple("dungeon_tilespecial" + suffix, DUNGEON_STONE_PROPS));
        DUNGEON_METAL.put(variant, registerSimple("dungeon_metal" + suffix, DUNGEON_METAL_PROPS));

        // Pillars
        DUNGEON_PILLAR_CENTER.put(variant, registerPillar("dungeon_pillar_center" + suffix));
        DUNGEON_PILLAR_SPECIAL.put(variant, registerPillar("dungeon_pillar_special" + suffix));
        DUNGEON_PILLAR_CAP.put(variant, registerSimple("dungeon_pillar_cap" + suffix, DUNGEON_STONE_PROPS));

        // Stairs - need to reference the base block
        BlockWithItemHolder<Block, BlockItem> brick1 = DUNGEON_BRICK_1.get(variant);
        BlockWithItemHolder<Block, BlockItem> polished = DUNGEON_POLISHED.get(variant);
        BlockWithItemHolder<Block, BlockItem> stone = DUNGEON_STONE.get(variant);

        DUNGEON_BRICK_STAIRS.put(variant, registerStairs("dungeon_brick_stairs" + suffix, brick1));
        DUNGEON_POLISHED_STAIRS.put(variant, registerStairs("dungeon_polished_stairs" + suffix, polished));
        DUNGEON_STONE_STAIRS.put(variant, registerStairs("dungeon_stone_stairs" + suffix, stone));

        // Walls
        DUNGEON_BRICK_WALL.put(variant, registerWall("dungeon_brick_wall" + suffix));
        DUNGEON_TILE_WALL.put(variant, registerWall("dungeon_tile_wall" + suffix));
        DUNGEON_POLISHED_WALL.put(variant, registerWall("dungeon_polished_wall" + suffix));
        DUNGEON_STONE_WALL.put(variant, registerWall("dungeon_stone_wall" + suffix));

        // Gates
        DUNGEON_BRICK_GATE.put(variant, registerGate("dungeon_brick_gate" + suffix));
        DUNGEON_POLISHED_GATE.put(variant, registerGate("dungeon_polished_gate" + suffix));

        // Slabs
        DUNGEON_BRICK_SLAB.put(variant, registerSlab("dungeon_brick_slab" + suffix));
        DUNGEON_TILE_SLAB.put(variant, registerSlab("dungeon_tile_slab" + suffix));
        DUNGEON_STONE_SLAB.put(variant, registerSlab("dungeon_stone_slab" + suffix));
        DUNGEON_POLISHED_SLAB.put(variant, registerSlab("dungeon_polished_slab" + suffix));
    }

    private static BlockWithItemHolder<Block, BlockItem> registerSimple(String name, BlockBehaviour.Properties props) {
        return REG.register(name, props, new Item.Properties());
    }

    private static BlockWithItemHolder<RotatedPillarBlock, BlockItem> registerPillar(String name) {
        return REG.register(name, () -> new RotatedPillarBlock(DUNGEON_STONE_PROPS));
    }

    private static BlockWithItemHolder<StairBlock, BlockItem> registerStairs(String name, BlockWithItemHolder<Block, BlockItem> baseBlock) {
        return REG.register(name, () -> new StairBlock(baseBlock.block().get().defaultBlockState(), DUNGEON_STONE_PROPS));
    }

    private static BlockWithItemHolder<WallBlock, BlockItem> registerWall(String name) {
        return REG.register(name, () -> new WallBlock(DUNGEON_STONE_PROPS));
    }

    private static BlockWithItemHolder<FenceGateBlock, BlockItem> registerGate(String name) {
        return REG.register(name, () -> new FenceGateBlock(WoodType.OAK, DUNGEON_STONE_PROPS));
    }

    private static BlockWithItemHolder<SlabBlock, BlockItem> registerSlab(String name) {
        return REG.register(name, () -> new SlabBlock(DUNGEON_STONE_PROPS));
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
    }

    /**
     * Convenience method to get a block by variant.
     * Example: DungeonBlocks.getBrick1(DungeonVariant.CORROSIVE)
     */
    public static Block getBrick1(DungeonVariant variant) {
        return DUNGEON_BRICK_1.get(variant).block().get();
    }

    public static Block getBrick2(DungeonVariant variant) {
        return DUNGEON_BRICK_2.get(variant).block().get();
    }

    public static Block getBrick3(DungeonVariant variant) {
        return DUNGEON_BRICK_3.get(variant).block().get();
    }
}
