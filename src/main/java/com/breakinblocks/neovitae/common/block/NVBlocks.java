package com.breakinblocks.neovitae.common.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.AraVitaeBlockItem;
import com.breakinblocks.neovitae.common.item.DemonLanternItem;
import com.breakinblocks.neovitae.common.item.block.BloodTankBlockItem;
import com.breakinblocks.neovitae.common.item.block.SpiritAccumulatorBlockItem;
import com.breakinblocks.neovitae.common.item.block.ItemBlockTabulaVitae;
import com.breakinblocks.neovitae.common.item.block.RuneBlockItem;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.util.helper.BlockWithItemHolder;
import com.breakinblocks.neovitae.util.helper.BlockWithItemRegister;
import java.util.function.Supplier;

public class NVBlocks {
    public static final DeferredRegister.Blocks BASIC_BLOCKS = DeferredRegister.createBlocks(NeoVitae.MODID);
    public static final DeferredRegister.Items BASIC_BLOCK_ITEMS = DeferredRegister.createItems(NeoVitae.MODID);
    public static final BlockWithItemRegister BASIC_REG = new BlockWithItemRegister(BASIC_BLOCKS, BASIC_BLOCK_ITEMS);

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NeoVitae.MODID);
    public static final DeferredRegister.Items BLOCK_ITEMS = DeferredRegister.createItems(NeoVitae.MODID);
    public static final BlockWithItemRegister BLOCK_REG = new BlockWithItemRegister(BLOCKS, BLOCK_ITEMS);

    // Default Properties used when a block class already internally chains the details it needs.
    private static BlockBehaviour.Properties defaultBlockProps() { return BlockBehaviour.Properties.of(); }
    private static Item.Properties defaultItemProps() { return new Item.Properties(); }

    public static final BlockWithItemHolder<AraVitaeBlock, AraVitaeBlockItem> ARA_VITAE =
            BLOCK_REG.register("ara_vitae", AraVitaeBlock::new, defaultBlockProps(),
                    (block, itemProps) -> new AraVitaeBlockItem(block, itemProps));
    public static final BlockWithItemHolder<BloodTankBlock, BlockItem> BLOOD_TANK =
            BLOCK_REG.register("blood_tank", BloodTankBlock::new, defaultBlockProps(),
                    (block, itemProps) -> new BloodTankBlockItem(block, itemProps.component(NVDataComponents.CONTAINER_TIER, 1)));
    public static final BlockWithItemHolder<BloodBatteryBlock, BlockItem> BLOOD_BATTERY =
            BLOCK_REG.register("blood_battery", BloodBatteryBlock::new, defaultBlockProps());
    public static final BlockWithItemHolder<HellfireForgeBlock, BlockItem> HELLFIRE_FORGE =
            BLOCK_REG.register("hellfire_forge", HellfireForgeBlock::new, defaultBlockProps());
    public static final BlockWithItemHolder<AthanorBlock, BlockItem> ATHANOR_BLOCK =
            BLOCK_REG.register("athanor", AthanorBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE));
    public static final BlockWithItemHolder<TeleposerBlock, BlockItem> TELEPOSER =
            BLOCK_REG.register("teleposer", TeleposerBlock::new, defaultBlockProps());
    public static final BlockWithItemHolder<SpiritCacheBlock, BlockItem> SPIRIT_CACHE =
            BLOCK_REG.register("spirit_cache", SpiritCacheBlock::new, defaultBlockProps());
    public static final BlockWithItemHolder<SpiritAccumulatorBlock, BlockItem> SPIRIT_ACCUMULATOR =
            BLOCK_REG.register("spirit_accumulator", SpiritAccumulatorBlock::new, defaultBlockProps(),
                    SpiritAccumulatorBlockItem::new, new Item.Properties());
    public static final BlockWithItemHolder<VitaeLinkBlock, BlockItem> VITAE_LINK =
            BLOCK_REG.register("vitae_link", VitaeLinkBlock::new, defaultBlockProps());
    public static final BlockWithItemHolder<OrbFillingLinkBlock, BlockItem> ORB_FILLING_LINK =
            BLOCK_REG.register("orb_filling_link", OrbFillingLinkBlock::new, defaultBlockProps());

    private static final BlockBehaviour.Properties rune_properties_src() {
        return BlockBehaviour.Properties.of().strength(2.0F, 5.0F).sound(SoundType.STONE).requiresCorrectToolForDrops().lightLevel(state -> 10);
    }
    private static final Item.Properties rune_item_properties_src() {
        return new Item.Properties();
    }

    private static RuneBlockItem runeItem(Block block, Item.Properties itemProps, String... tooltipSuffixes) {
        String[] keys = new String[tooltipSuffixes.length];
        for (int i = 0; i < tooltipSuffixes.length; i++) {
            keys[i] = "tooltip.neovitae.rune." + tooltipSuffixes[i];
        }
        return new RuneBlockItem(block, itemProps, keys);
    }

    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_BLANK = BASIC_REG.register("rune_blank", rune_properties_src(), (b, p) -> runeItem(b, p, "blank"));

    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_SACRIFICE = BASIC_REG.register("rune_sacrifice", rune_properties_src(), (b, p) -> runeItem(b, p, "sacrifice"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_SELF_SACRIFICE = BASIC_REG.register("rune_sacrifice_self", rune_properties_src(), (b, p) -> runeItem(b, p, "self_sacrifice"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_CAPACITY = BASIC_REG.register("rune_capacity", rune_properties_src(), (b, p) -> runeItem(b, p, "capacity"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_CAPACITY_AUGMENTED = BASIC_REG.register("rune_capacity_augmented", rune_properties_src(), (b, p) -> runeItem(b, p, "capacity_augmented"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_CHARGING = BASIC_REG.register("rune_charging", rune_properties_src(), (b, p) -> runeItem(b, p, "charging"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_SPEED = BASIC_REG.register("rune_speed", rune_properties_src(), (b, p) -> runeItem(b, p, "speed"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_ACCELERATION = BASIC_REG.register("rune_acceleration", rune_properties_src(), (b, p) -> runeItem(b, p, "acceleration"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_DISLOCATION = BASIC_REG.register("rune_dislocation", rune_properties_src(), (b, p) -> runeItem(b, p, "dislocation"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_ORB = BASIC_REG.register("rune_orb", rune_properties_src(), (b, p) -> runeItem(b, p, "orb"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_EFFICIENCY = BASIC_REG.register("rune_efficiency", rune_properties_src(), (b, p) -> runeItem(b, p, "efficiency"));

    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_SACRIFICE = BASIC_REG.register("rune_2_sacrifice", rune_properties_src(), (b, p) -> runeItem(b, p, "sacrifice", "reinforced"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_SELF_SACRIFICE = BASIC_REG.register("rune_2_sacrifice_self", rune_properties_src(), (b, p) -> runeItem(b, p, "self_sacrifice", "reinforced"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_CAPACITY = BASIC_REG.register("rune_2_capacity", rune_properties_src(), (b, p) -> runeItem(b, p, "capacity", "reinforced"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_CAPACITY_AUGMENTED = BASIC_REG.register("rune_2_capacity_augmented", rune_properties_src(), (b, p) -> runeItem(b, p, "capacity_augmented", "reinforced"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_CHARGING = BASIC_REG.register("rune_2_charging", rune_properties_src(), (b, p) -> runeItem(b, p, "charging", "reinforced"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_SPEED = BASIC_REG.register("rune_2_speed", rune_properties_src(), (b, p) -> runeItem(b, p, "speed", "reinforced"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_ACCELERATION = BASIC_REG.register("rune_2_acceleration", rune_properties_src(), (b, p) -> runeItem(b, p, "acceleration", "reinforced"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_DISLOCATION = BASIC_REG.register("rune_2_dislocation", rune_properties_src(), (b, p) -> runeItem(b, p, "dislocation", "reinforced"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_ORB = BASIC_REG.register("rune_2_orb", rune_properties_src(), (b, p) -> runeItem(b, p, "orb", "reinforced"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_EFFICIENCY = BASIC_REG.register("rune_2_efficiency", rune_properties_src(), (b, p) -> runeItem(b, p, "efficiency", "reinforced"));

    public static final BlockWithItemHolder<Block, BlockItem> BLOODSTONE = BASIC_REG.register("bloodstone", rune_properties_src(), rune_item_properties_src());
    public static final BlockWithItemHolder<Block, BlockItem> BLOODSTONE_BRICK = BASIC_REG.register("bloodstone_brick", rune_properties_src(), rune_item_properties_src());

    private static BlockBehaviour.Properties metal_block_properties_src() {
        return BlockBehaviour.Properties.of().strength(5, 6).sound(SoundType.METAL).requiresCorrectToolForDrops();
    }
    public static final BlockWithItemHolder<Block, BlockItem> HELLFORGED_BLOCK = BASIC_REG.register("hellforged_block", metal_block_properties_src(), new Item.Properties());
    public static final BlockWithItemHolder<Block, BlockItem> RAW_DEMONITE_BLOCK = BASIC_REG.register("raw_demonite_block", metal_block_properties_src(), new Item.Properties());

    public static final BlockWithItemHolder<EnchantingPowerBlock, BlockItem> CRYSTAL_CLUSTER = BASIC_REG.register("crystal_cluster", props -> new EnchantingPowerBlock(props, 2.5F), rune_properties_src(), rune_item_properties_src());
    public static final BlockWithItemHolder<EnchantingPowerBlock, BlockItem> CRYSTAL_CLUSTER_BRICK = BASIC_REG.register("crystal_cluster_brick", props -> new EnchantingPowerBlock(props, 2.5F), rune_properties_src(), rune_item_properties_src());

    public static final DeferredBlock<AlchemyArrayBlock> ALCHEMY_ARRAY = BLOCKS.registerBlock("alchemy_array", AlchemyArrayBlock::new, (Supplier<BlockBehaviour.Properties>) NVBlocks::defaultBlockProps);
    public static final DeferredBlock<BloodLightBlock> BLOOD_LIGHT = BLOCKS.registerBlock("blood_light", BloodLightBlock::new, (Supplier<BlockBehaviour.Properties>) NVBlocks::defaultBlockProps);
    public static final BlockWithItemHolder<BloodLanternBlock, BlockItem> BLOOD_LANTERN =
            BLOCK_REG.register("blood_lantern", BloodLanternBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.SOUL_LANTERN));
    public static final BlockWithItemHolder<DemonLanternBlock, DemonLanternItem> DEMON_LANTERN =
            BLOCK_REG.register("demon_lantern", DemonLanternBlock::new, DemonLanternBlock.defaultProperties(),
                    (block, itemProps) -> new DemonLanternItem(block, itemProps));
    public static final DeferredBlock<SpectralBlock> SPECTRAL_BLOCK = BLOCKS.registerBlock("spectral_block", SpectralBlock::new, (Supplier<BlockBehaviour.Properties>) NVBlocks::defaultBlockProps);
    public static final DeferredBlock<PhantomBridgeBlock> PHANTOM_BRIDGE_BLOCK = BLOCKS.registerBlock("phantom_bridge", PhantomBridgeBlock::new, (Supplier<BlockBehaviour.Properties>) NVBlocks::defaultBlockProps);

    public static final BlockWithItemHolder<TabulaVitaeBlock, ItemBlockTabulaVitae> TABULA_VITAE =
            BLOCK_REG.register("tabula_vitae", TabulaVitaeBlock::new, defaultBlockProps(),
                    (block, itemProps) -> new ItemBlockTabulaVitae(block, itemProps));

    public static final BlockWithItemHolder<BlockIncenseAltar, BlockItem> INCENSE_ALTAR =
            BLOCK_REG.register("incense_altar", BlockIncenseAltar::new, defaultBlockProps());

    private static BlockBehaviour.Properties tau_properties_src() {
        return BlockBehaviour.Properties.of().noCollision().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY).randomTicks();
    }
    public static final BlockWithItemHolder<BlockTau, BlockItem> STRONG_TAU =
            BLOCK_REG.register("strong_tau", props -> new BlockTau(props, true), tau_properties_src());
    public static final BlockWithItemHolder<BlockTau, BlockItem> WEAK_TAU =
            BLOCK_REG.register("weak_tau", props -> {
                BlockTau weakTau = new BlockTau(props, false);
                weakTau.setStrongTauSupplier(() -> STRONG_TAU.block().get());
                return weakTau;
            }, tau_properties_src());

    public static final BlockWithItemHolder<BlockRitualStone, BlockItem> BLANK_RITUAL_STONE = BLOCK_REG.register("ritual_stone", props -> new BlockRitualStone(props, EnumRuneType.BLANK), defaultBlockProps());
    public static final BlockWithItemHolder<BlockRitualStone, BlockItem> AIR_RITUAL_STONE = BLOCK_REG.register("air_ritual_stone", props -> new BlockRitualStone(props, EnumRuneType.AIR), defaultBlockProps());
    public static final BlockWithItemHolder<BlockRitualStone, BlockItem> WATER_RITUAL_STONE = BLOCK_REG.register("water_ritual_stone", props -> new BlockRitualStone(props, EnumRuneType.WATER), defaultBlockProps());
    public static final BlockWithItemHolder<BlockRitualStone, BlockItem> FIRE_RITUAL_STONE = BLOCK_REG.register("fire_ritual_stone", props -> new BlockRitualStone(props, EnumRuneType.FIRE), defaultBlockProps());
    public static final BlockWithItemHolder<BlockRitualStone, BlockItem> EARTH_RITUAL_STONE = BLOCK_REG.register("earth_ritual_stone", props -> new BlockRitualStone(props, EnumRuneType.EARTH), defaultBlockProps());
    public static final BlockWithItemHolder<BlockRitualStone, BlockItem> TENEBRAE_RITUAL_STONE = BLOCK_REG.register("tenebrae_ritual_stone", props -> new BlockRitualStone(props, EnumRuneType.TENEBRAE), defaultBlockProps());
    public static final BlockWithItemHolder<BlockRitualStone, BlockItem> DEUS_RITUAL_STONE = BLOCK_REG.register("deus_ritual_stone", props -> new BlockRitualStone(props, EnumRuneType.DEUS), defaultBlockProps());
    public static final BlockWithItemHolder<BlockMasterRitualStone, BlockItem> MASTER_RITUAL_STONE = BLOCK_REG.register("master_ritual_stone", props -> new BlockMasterRitualStone(props, false), defaultBlockProps());
    public static final BlockWithItemHolder<BlockMasterRitualStone, BlockItem> INVERTED_MASTER_RITUAL_STONE = BLOCK_REG.register("inverted_master_ritual_stone", props -> new BlockMasterRitualStone(props, true), defaultBlockProps());
    public static final BlockWithItemHolder<BlockImperfectRitualStone, BlockItem> IMPERFECT_RITUAL_STONE = BLOCK_REG.register("imperfect_ritual_stone", BlockImperfectRitualStone::new, defaultBlockProps());

    public static final BlockWithItemHolder<VasMaleficumBlock, BlockItem> VAS_MALEFICUM = BLOCK_REG.register("vas_maleficum", VasMaleficumBlock::new, defaultBlockProps());
    public static final BlockWithItemHolder<CrystallariumMaleficumBlock, BlockItem> CRYSTALLARIUM_MALEFICUM = BLOCK_REG.register("crystallarium_maleficum", CrystallariumMaleficumBlock::new, defaultBlockProps());
    public static final BlockWithItemHolder<SpiraInfernalisBlock, BlockItem> SPIRA_INFERNALIS = BLOCK_REG.register("spira_infernalis", SpiraInfernalisBlock::new, defaultBlockProps());

    private static BlockBehaviour.Properties crystal_block_properties_src() {
        return BlockBehaviour.Properties.of().strength(3.0F, 3.0F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops().lightLevel(state -> 7).noOcclusion();
    }
    public static final BlockWithItemHolder<BlockSpiritusCrystal, BlockItem> RAW_SPIRITUS_CRYSTAL = BLOCK_REG.register("raw_spiritus_crystal", props -> new BlockSpiritusCrystal(SpiritusType.RAW, props), crystal_block_properties_src());
    public static final BlockWithItemHolder<BlockSpiritusCrystal, BlockItem> SPIRITUS_RUINA_CRYSTAL = BLOCK_REG.register("spiritus_ruina_crystal", props -> new BlockSpiritusCrystal(SpiritusType.RUINA, props), crystal_block_properties_src());
    public static final BlockWithItemHolder<BlockSpiritusCrystal, BlockItem> SPIRITUS_NIHILUM_CRYSTAL = BLOCK_REG.register("spiritus_nihilum_crystal", props -> new BlockSpiritusCrystal(SpiritusType.NIHILUM, props), crystal_block_properties_src());
    public static final BlockWithItemHolder<BlockSpiritusCrystal, BlockItem> SPIRITUS_VINDICTA_CRYSTAL = BLOCK_REG.register("spiritus_vindicta_crystal", props -> new BlockSpiritusCrystal(SpiritusType.VINDICTA, props), crystal_block_properties_src());
    public static final BlockWithItemHolder<BlockSpiritusCrystal, BlockItem> SPIRITUS_INVICTUS_CRYSTAL = BLOCK_REG.register("spiritus_invictus_crystal", props -> new BlockSpiritusCrystal(SpiritusType.INVICTUS, props), crystal_block_properties_src());

    private static BlockBehaviour.Properties routing_node_properties_src() {
        return BlockBehaviour.Properties.of().strength(2.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion();
    }
    public static final BlockWithItemHolder<BlockRoutingConduit, BlockItem> ROUTING_CONDUIT = BLOCK_REG.register("routing_conduit", BlockRoutingConduit::new, routing_node_properties_src());
    public static final BlockWithItemHolder<BlockInputRoutingNode, BlockItem> INPUT_ROUTING_NODE = BLOCK_REG.register("input_routing_node", BlockInputRoutingNode::new, routing_node_properties_src());
    public static final BlockWithItemHolder<BlockOutputRoutingNode, BlockItem> OUTPUT_ROUTING_NODE = BLOCK_REG.register("output_routing_node", BlockOutputRoutingNode::new, routing_node_properties_src());
    public static final BlockWithItemHolder<BlockOmniRoutingNode, BlockItem> OMNI_ROUTING_NODE = BLOCK_REG.register("omni_routing_node", BlockOmniRoutingNode::new, routing_node_properties_src());
    public static final BlockWithItemHolder<BlockMasterRoutingNode, BlockItem> MASTER_ROUTING_NODE = BLOCK_REG.register("master_routing_node", BlockMasterRoutingNode::new, routing_node_properties_src());

    private static BlockBehaviour.Properties charge_properties_src() {
        return BlockBehaviour.Properties.of().strength(2.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion();
    }

    public static final BlockWithItemHolder<BlockShapedExplosive, BlockItem> SHAPED_CHARGE = BLOCK_REG.register("shaped_charge", props -> new BlockShapedExplosive(3, props), charge_properties_src());
    public static final BlockWithItemHolder<BlockShapedExplosive, BlockItem> AUG_SHAPED_CHARGE = BLOCK_REG.register("aug_shaped_charge", props -> new BlockShapedExplosive(5, props), charge_properties_src());
    public static final BlockWithItemHolder<BlockShapedExplosiveDeep, BlockItem> SHAPED_CHARGE_DEEP = BLOCK_REG.register("shaped_charge_deep", props -> new BlockShapedExplosiveDeep(3, props), charge_properties_src());

    public static final BlockWithItemHolder<BlockDeforesterCharge, BlockItem> DEFORESTER_CHARGE = BLOCK_REG.register("deforester_charge", props -> new BlockDeforesterCharge(128, props), charge_properties_src());
    public static final BlockWithItemHolder<BlockDeforesterCharge, BlockItem> DEFORESTER_CHARGE_2 = BLOCK_REG.register("deforester_charge_2", props -> new BlockDeforesterCharge(256, props), charge_properties_src());

    public static final BlockWithItemHolder<BlockVeinMineCharge, BlockItem> VEINMINE_CHARGE = BLOCK_REG.register("veinmine_charge", props -> new BlockVeinMineCharge(128, props), charge_properties_src());
    public static final BlockWithItemHolder<BlockVeinMineCharge, BlockItem> VEINMINE_CHARGE_2 = BLOCK_REG.register("veinmine_charge_2", props -> new BlockVeinMineCharge(256, props), charge_properties_src());

    public static final BlockWithItemHolder<BlockFungalCharge, BlockItem> FUNGAL_CHARGE = BLOCK_REG.register("fungal_charge", props -> new BlockFungalCharge(128, props), charge_properties_src());
    public static final BlockWithItemHolder<BlockFungalCharge, BlockItem> FUNGAL_CHARGE_2 = BLOCK_REG.register("fungal_charge_2", props -> new BlockFungalCharge(256, props), charge_properties_src());

    private static BlockBehaviour.Properties mimic_properties_src() {
        return BlockBehaviour.Properties.of().strength(2.0F, 5.0F).sound(SoundType.STONE).noOcclusion();
    }
    public static final BlockWithItemHolder<BlockMimic, BlockItem> MIMIC = BLOCK_REG.register("mimic", BlockMimic::new, mimic_properties_src());
    private static BlockBehaviour.Properties ethereal_mimic_properties_src() {
        return BlockBehaviour.Properties.of().strength(2.0F, 5.0F).sound(SoundType.STONE).noOcclusion().noCollision();
    }
    public static final BlockWithItemHolder<BlockMimic, BlockItem> ETHEREAL_MIMIC = BLOCK_REG.register("ethereal_mimic", BlockMimic::new, ethereal_mimic_properties_src());

    private static BlockBehaviour.Properties inversion_pillar_properties_src() {
        return BlockBehaviour.Properties.of().strength(2.0F, 5.0F).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion();
    }
    public static final BlockWithItemHolder<BlockInversionPillar, BlockItem> INVERSION_PILLAR = BLOCK_REG.register("inversion_pillar", BlockInversionPillar::new, inversion_pillar_properties_src());
    public static final BlockWithItemHolder<BlockInversionPillarEnd, BlockItem> INVERSION_PILLAR_CAP = BLOCK_REG.register("inversion_pillar_cap", BlockInversionPillarEnd::new, inversion_pillar_properties_src());
    public static final BlockWithItemHolder<BlockSpatialRift, BlockItem> SPATIAL_RIFT = BLOCK_REG.register("spatial_rift", BlockSpatialRift::new, defaultBlockProps());

    public static final BlockWithItemHolder<BlockDungeonController, BlockItem> DUNGEON_CONTROLLER = BLOCK_REG.register("dungeon_controller", BlockDungeonController::new, defaultBlockProps());
    public static final BlockWithItemHolder<BlockDungeonSeal, BlockItem> DUNGEON_SEAL = BLOCK_REG.register("dungeon_seal", BlockDungeonSeal::new, defaultBlockProps());
    public static final BlockWithItemHolder<BlockDungeonSealInaccessible, BlockItem> DUNGEON_SEAL_INACCESSIBLE = BLOCK_REG.register("dungeon_seal_inaccessible", BlockDungeonSealInaccessible::new, defaultBlockProps());

    public static final BlockWithItemHolder<SandsOfVitaeBlock, BlockItem> SANDS_OF_VITAE = BLOCK_REG.register("sands_of_vitae", SandsOfVitaeBlock::new, defaultBlockProps());

    public static final BlockWithItemHolder<BloodStainedGlassBlock, BlockItem> BLOOD_STAINED_GLASS = BLOCK_REG.register("blood_stained_glass", BloodStainedGlassBlock::new, defaultBlockProps());

    public static final BlockWithItemHolder<IronBarsBlock, BlockItem> BLOOD_STAINED_GLASS_PANE = BLOCK_REG.register("blood_stained_glass_pane",
            IronBarsBlock::new,
            BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> 15));

    public static void register(IEventBus modBus) {
        BASIC_BLOCKS.register(modBus);
        BASIC_BLOCK_ITEMS.register(modBus);
        BLOCKS.register(modBus);
        BLOCK_ITEMS.register(modBus);
    }
}
