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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DungeonBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NeoVitae.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NeoVitae.MODID);
    public static final BlockWithItemRegister REG = new BlockWithItemRegister(BLOCKS, ITEMS);

    private static BlockBehaviour.Properties dungeonStoneProps() {
        return BlockBehaviour.Properties.of()
                .strength(2.0F, 5.0F).sound(SoundType.STONE).requiresCorrectToolForDrops();
    }
    private static BlockBehaviour.Properties dungeonMetalProps() {
        return BlockBehaviour.Properties.of()
                .strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops();
    }
    private static BlockBehaviour.Properties dungeonEyeProps() {
        return BlockBehaviour.Properties.of()
                .strength(2.0F, 5.0F).sound(SoundType.STONE).requiresCorrectToolForDrops().lightLevel(state -> 15);
    }

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

    public static final Map<DungeonVariant, BlockWithItemHolder<RotatedPillarBlock, BlockItem>> DUNGEON_PILLAR_CENTER = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<RotatedPillarBlock, BlockItem>> DUNGEON_PILLAR_SPECIAL = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<Block, BlockItem>> DUNGEON_PILLAR_CAP = new EnumMap<>(DungeonVariant.class);

    public static final Map<DungeonVariant, BlockWithItemHolder<StairBlock, BlockItem>> DUNGEON_BRICK_STAIRS = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<StairBlock, BlockItem>> DUNGEON_POLISHED_STAIRS = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<StairBlock, BlockItem>> DUNGEON_STONE_STAIRS = new EnumMap<>(DungeonVariant.class);

    public static final Map<DungeonVariant, BlockWithItemHolder<WallBlock, BlockItem>> DUNGEON_BRICK_WALL = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<WallBlock, BlockItem>> DUNGEON_TILE_WALL = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<WallBlock, BlockItem>> DUNGEON_POLISHED_WALL = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<WallBlock, BlockItem>> DUNGEON_STONE_WALL = new EnumMap<>(DungeonVariant.class);

    public static final Map<DungeonVariant, BlockWithItemHolder<FenceGateBlock, BlockItem>> DUNGEON_BRICK_GATE = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<FenceGateBlock, BlockItem>> DUNGEON_POLISHED_GATE = new EnumMap<>(DungeonVariant.class);

    public static final Map<DungeonVariant, BlockWithItemHolder<SlabBlock, BlockItem>> DUNGEON_BRICK_SLAB = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<SlabBlock, BlockItem>> DUNGEON_TILE_SLAB = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<SlabBlock, BlockItem>> DUNGEON_STONE_SLAB = new EnumMap<>(DungeonVariant.class);
    public static final Map<DungeonVariant, BlockWithItemHolder<SlabBlock, BlockItem>> DUNGEON_POLISHED_SLAB = new EnumMap<>(DungeonVariant.class);

    public static BlockWithItemHolder<Block, BlockItem> DUNGEON_ORE;
    public static BlockWithItemHolder<BlockPrismaticDemonite, BlockItem> PRISMATIC_DEMONITE;
    public static BlockWithItemHolder<Block, BlockItem> DUNGEON_BRICK_ASSORTED;

    public static BlockWithItemHolder<BlockSpikes, BlockItem> SPIKES;
    public static BlockWithItemHolder<BlockSpikeTrap, BlockItem> SPIKE_TRAP;
    public static BlockWithItemHolder<BlockAlternator, BlockItem> ALTERNATOR;

    public static BlockWithItemHolder<Block, BlockItem> WOOD_BRICK_PATH;
    public static BlockWithItemHolder<Block, BlockItem> WOOD_TILE_PATH;
    public static BlockWithItemHolder<Block, BlockItem> STONE_BRICK_PATH;
    public static BlockWithItemHolder<Block, BlockItem> STONE_TILE_PATH;
    public static BlockWithItemHolder<Block, BlockItem> WORN_STONE_BRICK_PATH;
    public static BlockWithItemHolder<Block, BlockItem> WORN_STONE_TILE_PATH;
    public static BlockWithItemHolder<Block, BlockItem> OBSIDIAN_BRICK_PATH;
    public static BlockWithItemHolder<Block, BlockItem> OBSIDIAN_TILE_PATH;

    static {
        DUNGEON_ORE = REG.register("dungeon_ore", BlockBehaviour.Properties.of()
                .strength(3.0F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops(), new Item.Properties());
        PRISMATIC_DEMONITE = REG.register("prismatic_demonite", BlockPrismaticDemonite::new, BlockBehaviour.Properties.of());
        DUNGEON_BRICK_ASSORTED = REG.register("dungeon_brick_assorted", BlockBehaviour.Properties.of()
                .strength(20.0F, 50.0F).sound(SoundType.STONE).requiresCorrectToolForDrops(), new Item.Properties());

        SPIKES = REG.register("spikes", BlockSpikes::new,
                BlockBehaviour.Properties.of().strength(2.0F, 5.0F).sound(SoundType.METAL).noOcclusion());
        SPIKE_TRAP = REG.register("spike_trap", BlockSpikeTrap::new,
                BlockBehaviour.Properties.of().strength(2.0F, 5.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
        ALTERNATOR = REG.register("alternator", BlockAlternator::new,
                BlockBehaviour.Properties.of().strength(2.0F, 5.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

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

        for (DungeonVariant variant : DungeonVariant.values()) {
            registerVariantBlocks(variant);
        }
    }

    private static void registerVariantBlocks(DungeonVariant variant) {
        String suffix = variant.getSuffix();

        DUNGEON_BRICK_1.put(variant, registerSimple("dungeon_brick1" + suffix, dungeonStoneProps()));
        DUNGEON_BRICK_2.put(variant, registerSimple("dungeon_brick2" + suffix, dungeonStoneProps()));
        DUNGEON_BRICK_3.put(variant, registerSimple("dungeon_brick3" + suffix, dungeonStoneProps()));
        DUNGEON_STONE.put(variant, registerSimple("dungeon_stone" + suffix, dungeonStoneProps()));
        DUNGEON_EYE.put(variant, registerSimple("dungeon_eye" + suffix, dungeonEyeProps()));
        DUNGEON_POLISHED.put(variant, registerSimple("dungeon_polished" + suffix, dungeonStoneProps()));
        DUNGEON_TILE.put(variant, registerSimple("dungeon_tile" + suffix, dungeonStoneProps()));
        DUNGEON_SMALLBRICK.put(variant, registerSimple("dungeon_smallbrick" + suffix, dungeonStoneProps()));
        DUNGEON_TILESPECIAL.put(variant, registerSimple("dungeon_tilespecial" + suffix, dungeonStoneProps()));
        DUNGEON_METAL.put(variant, registerSimple("dungeon_metal" + suffix, dungeonMetalProps()));

        DUNGEON_PILLAR_CENTER.put(variant, registerPillar("dungeon_pillar_center" + suffix));
        DUNGEON_PILLAR_SPECIAL.put(variant, registerPillar("dungeon_pillar_special" + suffix));
        DUNGEON_PILLAR_CAP.put(variant, registerSimple("dungeon_pillar_cap" + suffix, dungeonStoneProps()));

        BlockWithItemHolder<Block, BlockItem> brick1 = DUNGEON_BRICK_1.get(variant);
        BlockWithItemHolder<Block, BlockItem> polished = DUNGEON_POLISHED.get(variant);
        BlockWithItemHolder<Block, BlockItem> stone = DUNGEON_STONE.get(variant);

        DUNGEON_BRICK_STAIRS.put(variant, registerStairs("dungeon_brick_stairs" + suffix, brick1));
        DUNGEON_POLISHED_STAIRS.put(variant, registerStairs("dungeon_polished_stairs" + suffix, polished));
        DUNGEON_STONE_STAIRS.put(variant, registerStairs("dungeon_stone_stairs" + suffix, stone));

        DUNGEON_BRICK_WALL.put(variant, registerWall("dungeon_brick_wall" + suffix));
        DUNGEON_TILE_WALL.put(variant, registerWall("dungeon_tile_wall" + suffix));
        DUNGEON_POLISHED_WALL.put(variant, registerWall("dungeon_polished_wall" + suffix));
        DUNGEON_STONE_WALL.put(variant, registerWall("dungeon_stone_wall" + suffix));

        DUNGEON_BRICK_GATE.put(variant, registerGate("dungeon_brick_gate" + suffix));
        DUNGEON_POLISHED_GATE.put(variant, registerGate("dungeon_polished_gate" + suffix));

        DUNGEON_BRICK_SLAB.put(variant, registerSlab("dungeon_brick_slab" + suffix));
        DUNGEON_TILE_SLAB.put(variant, registerSlab("dungeon_tile_slab" + suffix));
        DUNGEON_STONE_SLAB.put(variant, registerSlab("dungeon_stone_slab" + suffix));
        DUNGEON_POLISHED_SLAB.put(variant, registerSlab("dungeon_polished_slab" + suffix));
    }

    private static BlockWithItemHolder<Block, BlockItem> registerSimple(String name, BlockBehaviour.Properties props) {
        return REG.register(name, props, new Item.Properties());
    }

    private static BlockWithItemHolder<RotatedPillarBlock, BlockItem> registerPillar(String name) {
        return REG.register(name, RotatedPillarBlock::new, dungeonStoneProps());
    }

    private static BlockWithItemHolder<StairBlock, BlockItem> registerStairs(String name, BlockWithItemHolder<Block, BlockItem> baseBlock) {
        return REG.register(name,
                props -> new StairBlock(baseBlock.block().get().defaultBlockState(), props),
                dungeonStoneProps());
    }

    private static BlockWithItemHolder<WallBlock, BlockItem> registerWall(String name) {
        return REG.register(name, WallBlock::new, dungeonStoneProps());
    }

    private static BlockWithItemHolder<FenceGateBlock, BlockItem> registerGate(String name) {
        return REG.register(name, props -> new FenceGateBlock(WoodType.OAK, props), dungeonStoneProps());
    }

    private static BlockWithItemHolder<SlabBlock, BlockItem> registerSlab(String name) {
        return REG.register(name, SlabBlock::new, dungeonStoneProps());
    }

    private static Set<Block> dungeonBlockSet;
    private static Set<Block> dungeonStoneSet;

    public static boolean isDungeonBlock(Block block) {
        if (dungeonBlockSet == null) {
            dungeonBlockSet = new HashSet<>();
            for (var entry : BLOCKS.getEntries()) {
                dungeonBlockSet.add(entry.get());
            }
        }
        return dungeonBlockSet.contains(block);
    }

    public static boolean isDungeonStone(Block block) {
        if (dungeonStoneSet == null) {
            dungeonStoneSet = new HashSet<>();
            for (var holder : DUNGEON_STONE.values()) {
                dungeonStoneSet.add(holder.block().get());
            }
        }
        return dungeonStoneSet.contains(block);
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
    }

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
