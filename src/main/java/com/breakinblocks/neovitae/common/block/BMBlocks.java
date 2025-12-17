package com.breakinblocks.neovitae.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.item.block.ItemBlockAlchemyTable;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;
import com.breakinblocks.neovitae.util.helper.BlockWithItemHolder;
import com.breakinblocks.neovitae.util.helper.BlockWithItemRegister;

import java.util.List;
import java.util.function.Supplier;

public class BMBlocks {
    public static final DeferredRegister<Block> BASIC_BLOCKS = DeferredRegister.createBlocks(NeoVitae.MODID);
    public static final DeferredRegister<Item> BASIC_BLOCK_ITEMS = DeferredRegister.createItems(NeoVitae.MODID);
    public static final BlockWithItemRegister BASIC_REG = new BlockWithItemRegister(BASIC_BLOCKS, BASIC_BLOCK_ITEMS);

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(NeoVitae.MODID);
    public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.createItems(NeoVitae.MODID);
    public static final BlockWithItemRegister BLOCK_REG = new BlockWithItemRegister(BLOCKS, BLOCK_ITEMS);

    public static final BlockWithItemHolder<BloodAltarBlock, BlockItem> BLOOD_ALTAR = BLOCK_REG.register("blood_altar", BloodAltarBlock::new);
    public static final BlockWithItemHolder<BloodTankBlock, BlockItem> BLOOD_TANK = BLOCK_REG.register("blood_tank", BloodTankBlock::new, block -> new BlockItem(block, new Item.Properties().component(BMDataComponents.CONTAINER_TIER, 1)));
    public static final BlockWithItemHolder<HellfireForgeBlock, BlockItem> HELLFIRE_FORGE = BLOCK_REG.register("hellfire_forge", HellfireForgeBlock::new);
    public static final BlockWithItemHolder<ARCBlock, BlockItem> ARC_BLOCK = BLOCK_REG.register("arc", ARCBlock::new);
    public static final BlockWithItemHolder<TeleposerBlock, BlockItem> TELEPOSER = BLOCK_REG.register("teleposer", TeleposerBlock::new);

    private static final BlockBehaviour.Properties rune_properties = BlockBehaviour.Properties.of().strength(2.0F, 5.0F).sound(SoundType.STONE).requiresCorrectToolForDrops();
    private static final ItemLore save_decoration = new ItemLore(List.of(BlockEntityHelper.translatableHover("tooltip.neovitae.save_for_decoration").withStyle(ChatFormatting.ITALIC)));
    private static final Item.Properties decoration_item_properties = new Item.Properties().component(DataComponents.LORE, save_decoration);

    public static final BlockWithItemHolder<Block, BlockItem> RUNE_BLANK = BASIC_REG.register("rune_blank", rune_properties, decoration_item_properties);

    public static final BlockWithItemHolder<Block, BlockItem> RUNE_SACRIFICE = BASIC_REG.register("rune_sacrifice", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> RUNE_SELF_SACRIFICE = BASIC_REG.register("rune_sacrifice_self", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> RUNE_CAPACITY = BASIC_REG.register("rune_capacity", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> RUNE_CAPACITY_AUGMENTED = BASIC_REG.register("rune_capacity_augmented", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> RUNE_CHARGING = BASIC_REG.register("rune_charging", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> RUNE_SPEED = BASIC_REG.register("rune_speed", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> RUNE_ACCELERATION = BASIC_REG.register("rune_acceleration", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> RUNE_DISLOCATION = BASIC_REG.register("rune_dislocation", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> RUNE_ORB = BASIC_REG.register("rune_orb", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> RUNE_EFFICIENCY = BASIC_REG.register("rune_efficiency", rune_properties, decoration_item_properties);

    public static final BlockWithItemHolder<Block, BlockItem> RUNE_2_SACRIFICE = BASIC_REG.register("rune_2_sacrifice", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> RUNE_2_SELF_SACRIFICE = BASIC_REG.register("rune_2_sacrifice_self", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> RUNE_2_CAPACITY = BASIC_REG.register("rune_2_capacity", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> RUNE_2_CAPACITY_AUGMENTED = BASIC_REG.register("rune_2_capacity_augmented", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> RUNE_2_CHARGING = BASIC_REG.register("rune_2_charging", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> RUNE_2_SPEED = BASIC_REG.register("rune_2_speed", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> RUNE_2_ACCELERATION = BASIC_REG.register("rune_2_acceleration", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> RUNE_2_DISLOCATION = BASIC_REG.register("rune_2_dislocation", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> RUNE_2_ORB = BASIC_REG.register("rune_2_orb", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> RUNE_2_EFFICIENCY = BASIC_REG.register("rune_2_efficiency", rune_properties, decoration_item_properties);

    public static final BlockWithItemHolder<Block, BlockItem> BLOODSTONE = BASIC_REG.register("bloodstone", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> BLOODSTONE_BRICK = BASIC_REG.register("bloodstone_brick", rune_properties, decoration_item_properties);

    // Metal storage blocks
    private static final BlockBehaviour.Properties metal_block_properties = BlockBehaviour.Properties.of().strength(5, 6).sound(SoundType.METAL).requiresCorrectToolForDrops();
    public static final BlockWithItemHolder<Block, BlockItem> HELLFORGED_BLOCK = BASIC_REG.register("hellforged_block", metal_block_properties, new Item.Properties());
    public static final BlockWithItemHolder<Block, BlockItem> RAW_DEMONITE_BLOCK = BASIC_REG.register("raw_demonite_block", metal_block_properties, new Item.Properties());

    public static final BlockWithItemHolder<Block, BlockItem> CRYSTAL_CLUSTER = BASIC_REG.register("crystal_cluster", rune_properties, decoration_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> CRYSTAL_CLUSTER_BRICK = BASIC_REG.register("crystal_cluster_brick", rune_properties, decoration_item_properties);

    // Alchemy Array - placed by Arcane Ashes, no block item needed
    public static final DeferredHolder<Block, AlchemyArrayBlock> ALCHEMY_ARRAY = BLOCKS.register("alchemy_array", (Supplier<AlchemyArrayBlock>) AlchemyArrayBlock::new);

    // Blood Light - placed by Blood Light Sigil, no block item needed
    public static final DeferredHolder<Block, BloodLightBlock> BLOOD_LIGHT = BLOCKS.register("blood_light", BloodLightBlock::new);

    // Spectral Block - placed by Sigil of Suppression to temporarily replace fluids
    public static final DeferredHolder<Block, SpectralBlock> SPECTRAL_BLOCK = BLOCKS.register("spectral_block", SpectralBlock::new);

    // Phantom Bridge Block - placed by Sigil of the Phantom Bridge and Ritual of the Phantom Bridge
    public static final DeferredHolder<Block, PhantomBridgeBlock> PHANTOM_BRIDGE_BLOCK = BLOCKS.register("phantom_bridge", PhantomBridgeBlock::new);

    // Alchemy Table
    public static final BlockWithItemHolder<AlchemyTableBlock, ItemBlockAlchemyTable> ALCHEMY_TABLE = BLOCK_REG.register("alchemy_table", AlchemyTableBlock::new, block -> new ItemBlockAlchemyTable(block, new Item.Properties()));

    // Incense Altar - boosts self-sacrifice when path blocks are built around it
    public static final BlockWithItemHolder<BlockIncenseAltar, BlockItem> INCENSE_ALTAR = BLOCK_REG.register("incense_altar", BlockIncenseAltar::new);

    // Tau Blocks - blood-powered crops that grow by damaging nearby entities
    private static final BlockBehaviour.Properties tau_properties = BlockBehaviour.Properties.of().noCollission().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY).randomTicks();
    public static final BlockWithItemHolder<BlockTau, BlockItem> STRONG_TAU = BLOCK_REG.register("strong_tau", () -> new BlockTau(tau_properties, true));
    public static final BlockWithItemHolder<BlockTau, BlockItem> WEAK_TAU = BLOCK_REG.register("weak_tau", () -> {
        BlockTau weakTau = new BlockTau(tau_properties, false);
        weakTau.setStrongTauSupplier(() -> STRONG_TAU.block().get());
        return weakTau;
    });

    // Ritual Stones
    public static final BlockWithItemHolder<BlockRitualStone, BlockItem> BLANK_RITUAL_STONE = BLOCK_REG.register("ritual_stone", () -> new BlockRitualStone(EnumRuneType.BLANK));
    public static final BlockWithItemHolder<BlockRitualStone, BlockItem> AIR_RITUAL_STONE = BLOCK_REG.register("air_ritual_stone", () -> new BlockRitualStone(EnumRuneType.AIR));
    public static final BlockWithItemHolder<BlockRitualStone, BlockItem> WATER_RITUAL_STONE = BLOCK_REG.register("water_ritual_stone", () -> new BlockRitualStone(EnumRuneType.WATER));
    public static final BlockWithItemHolder<BlockRitualStone, BlockItem> FIRE_RITUAL_STONE = BLOCK_REG.register("fire_ritual_stone", () -> new BlockRitualStone(EnumRuneType.FIRE));
    public static final BlockWithItemHolder<BlockRitualStone, BlockItem> EARTH_RITUAL_STONE = BLOCK_REG.register("earth_ritual_stone", () -> new BlockRitualStone(EnumRuneType.EARTH));
    public static final BlockWithItemHolder<BlockRitualStone, BlockItem> DUSK_RITUAL_STONE = BLOCK_REG.register("dusk_ritual_stone", () -> new BlockRitualStone(EnumRuneType.DUSK));
    public static final BlockWithItemHolder<BlockRitualStone, BlockItem> DAWN_RITUAL_STONE = BLOCK_REG.register("dawn_ritual_stone", () -> new BlockRitualStone(EnumRuneType.DAWN));
    public static final BlockWithItemHolder<BlockMasterRitualStone, BlockItem> MASTER_RITUAL_STONE = BLOCK_REG.register("master_ritual_stone", () -> new BlockMasterRitualStone(false));
    public static final BlockWithItemHolder<BlockMasterRitualStone, BlockItem> INVERTED_MASTER_RITUAL_STONE = BLOCK_REG.register("inverted_master_ritual_stone", () -> new BlockMasterRitualStone(true));
    public static final BlockWithItemHolder<BlockImperfectRitualStone, BlockItem> IMPERFECT_RITUAL_STONE = BLOCK_REG.register("imperfect_ritual_stone", BlockImperfectRitualStone::new);

    // Demon Will Blocks - aura network components
    public static final BlockWithItemHolder<DemonCrucibleBlock, BlockItem> DEMON_CRUCIBLE = BLOCK_REG.register("demon_crucible", DemonCrucibleBlock::new);
    public static final BlockWithItemHolder<DemonCrystallizerBlock, BlockItem> DEMON_CRYSTALLIZER = BLOCK_REG.register("demon_crystallizer", DemonCrystallizerBlock::new);
    public static final BlockWithItemHolder<DemonPylonBlock, BlockItem> DEMON_PYLON = BLOCK_REG.register("demon_pylon", DemonPylonBlock::new);

    // Crystal Blocks - grown by demon crystallizer from will aura
    // These blocks have an AGE property (0-6) representing crystal count and grow from will aura
    private static final BlockBehaviour.Properties crystal_block_properties = BlockBehaviour.Properties.of().strength(3.0F, 3.0F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops().lightLevel(state -> 7).noOcclusion();
    public static final BlockWithItemHolder<BlockDemonCrystal, BlockItem> RAW_DEMON_CRYSTAL = BLOCK_REG.register("raw_demon_crystal", () -> new BlockDemonCrystal(com.breakinblocks.neovitae.common.datacomponent.EnumWillType.DEFAULT, crystal_block_properties));
    public static final BlockWithItemHolder<BlockDemonCrystal, BlockItem> CORROSIVE_DEMON_CRYSTAL = BLOCK_REG.register("corrosive_demon_crystal", () -> new BlockDemonCrystal(com.breakinblocks.neovitae.common.datacomponent.EnumWillType.CORROSIVE, crystal_block_properties));
    public static final BlockWithItemHolder<BlockDemonCrystal, BlockItem> DESTRUCTIVE_DEMON_CRYSTAL = BLOCK_REG.register("destructive_demon_crystal", () -> new BlockDemonCrystal(com.breakinblocks.neovitae.common.datacomponent.EnumWillType.DESTRUCTIVE, crystal_block_properties));
    public static final BlockWithItemHolder<BlockDemonCrystal, BlockItem> VENGEFUL_DEMON_CRYSTAL = BLOCK_REG.register("vengeful_demon_crystal", () -> new BlockDemonCrystal(com.breakinblocks.neovitae.common.datacomponent.EnumWillType.VENGEFUL, crystal_block_properties));
    public static final BlockWithItemHolder<BlockDemonCrystal, BlockItem> STEADFAST_DEMON_CRYSTAL = BLOCK_REG.register("steadfast_demon_crystal", () -> new BlockDemonCrystal(com.breakinblocks.neovitae.common.datacomponent.EnumWillType.STEADFAST, crystal_block_properties));

    // Routing Nodes
    private static final BlockBehaviour.Properties routing_node_properties = BlockBehaviour.Properties.of().strength(2.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion();
    public static final BlockWithItemHolder<BlockRoutingNode, BlockItem> ROUTING_NODE = BLOCK_REG.register("item_routing_node", () -> new BlockRoutingNode(routing_node_properties));
    public static final BlockWithItemHolder<BlockInputRoutingNode, BlockItem> INPUT_ROUTING_NODE = BLOCK_REG.register("input_routing_node", () -> new BlockInputRoutingNode(routing_node_properties));
    public static final BlockWithItemHolder<BlockOutputRoutingNode, BlockItem> OUTPUT_ROUTING_NODE = BLOCK_REG.register("output_routing_node", () -> new BlockOutputRoutingNode(routing_node_properties));
    public static final BlockWithItemHolder<BlockMasterRoutingNode, BlockItem> MASTER_ROUTING_NODE = BLOCK_REG.register("master_routing_node", () -> new BlockMasterRoutingNode(routing_node_properties));

    // Explosive Charges - shaped charges explode in a directed pattern, others mine connected blocks
    private static final BlockBehaviour.Properties charge_properties = BlockBehaviour.Properties.of().strength(2.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion();

    // Shaped charges - 3x7 (radius 3, depth 7), 5x11, and 3x9 (deep) explosion patterns
    public static final BlockWithItemHolder<BlockShapedExplosive, BlockItem> SHAPED_CHARGE = BLOCK_REG.register("shaped_charge", () -> new BlockShapedExplosive(3, charge_properties));
    public static final BlockWithItemHolder<BlockShapedExplosive, BlockItem> AUG_SHAPED_CHARGE = BLOCK_REG.register("aug_shaped_charge", () -> new BlockShapedExplosive(5, charge_properties));
    public static final BlockWithItemHolder<BlockShapedExplosiveDeep, BlockItem> SHAPED_CHARGE_DEEP = BLOCK_REG.register("shaped_charge_deep", () -> new BlockShapedExplosiveDeep(3, charge_properties));

    // Deforester charges - harvest up to 128/256 connected logs and leaves
    public static final BlockWithItemHolder<BlockDeforesterCharge, BlockItem> DEFORESTER_CHARGE = BLOCK_REG.register("deforester_charge", () -> new BlockDeforesterCharge(128, charge_properties));
    public static final BlockWithItemHolder<BlockDeforesterCharge, BlockItem> DEFORESTER_CHARGE_2 = BLOCK_REG.register("deforester_charge_2", () -> new BlockDeforesterCharge(256, charge_properties));

    // Vein mine charges - mine up to 128/256 connected ore blocks
    public static final BlockWithItemHolder<BlockVeinMineCharge, BlockItem> VEINMINE_CHARGE = BLOCK_REG.register("veinmine_charge", () -> new BlockVeinMineCharge(128, charge_properties));
    public static final BlockWithItemHolder<BlockVeinMineCharge, BlockItem> VEINMINE_CHARGE_2 = BLOCK_REG.register("veinmine_charge_2", () -> new BlockVeinMineCharge(256, charge_properties));

    // Fungal charges - harvest up to 128/256 connected mushroom blocks (nether stems/hyphae)
    public static final BlockWithItemHolder<BlockFungalCharge, BlockItem> FUNGAL_CHARGE = BLOCK_REG.register("fungal_charge", () -> new BlockFungalCharge(128, charge_properties));
    public static final BlockWithItemHolder<BlockFungalCharge, BlockItem> FUNGAL_CHARGE_2 = BLOCK_REG.register("fungal_charge_2", () -> new BlockFungalCharge(256, charge_properties));

    // Mimic Block - copies appearance of other blocks for hidden passages
    private static final BlockBehaviour.Properties mimic_properties = BlockBehaviour.Properties.of().strength(2.0F, 5.0F).sound(SoundType.STONE).noOcclusion();
    public static final BlockWithItemHolder<BlockMimic, BlockItem> MIMIC = BLOCK_REG.register("mimic", () -> new BlockMimic(mimic_properties));
    // Ethereal Mimic - pass-through mimic that players can walk through
    private static final BlockBehaviour.Properties ethereal_mimic_properties = BlockBehaviour.Properties.of().strength(2.0F, 5.0F).sound(SoundType.STONE).noOcclusion().noCollission();
    public static final BlockWithItemHolder<BlockMimic, BlockItem> ETHEREAL_MIMIC = BLOCK_REG.register("ethereal_mimic", () -> new BlockMimic(ethereal_mimic_properties));

    // Inversion Pillar - teleporter portal for dungeon system
    private static final BlockBehaviour.Properties inversion_pillar_properties = BlockBehaviour.Properties.of().strength(2.0F, 5.0F).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion();
    public static final BlockWithItemHolder<BlockInversionPillar, BlockItem> INVERSION_PILLAR = BLOCK_REG.register("inversion_pillar", () -> new BlockInversionPillar(inversion_pillar_properties));
    public static final BlockWithItemHolder<BlockInversionPillarEnd, BlockItem> INVERSION_PILLAR_CAP = BLOCK_REG.register("inversion_pillar_cap", () -> new BlockInversionPillarEnd(inversion_pillar_properties));

    // Dungeon Controller and Seal - procedural dungeon management blocks
    public static final BlockWithItemHolder<BlockDungeonController, BlockItem> DUNGEON_CONTROLLER = BLOCK_REG.register("dungeon_controller", BlockDungeonController::new);
    public static final BlockWithItemHolder<BlockDungeonSeal, BlockItem> DUNGEON_SEAL = BLOCK_REG.register("dungeon_seal", BlockDungeonSeal::new);

    public static void register(IEventBus modBus) {
        BASIC_BLOCKS.register(modBus);
        BASIC_BLOCK_ITEMS.register(modBus);
        BLOCKS.register(modBus);
        BLOCK_ITEMS.register(modBus);
    }
}
