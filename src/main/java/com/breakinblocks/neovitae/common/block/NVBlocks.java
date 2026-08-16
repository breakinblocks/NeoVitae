package com.breakinblocks.neovitae.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.DemonLanternItem;
import com.breakinblocks.neovitae.common.item.block.ItemBlockTabulaVitae;
import com.breakinblocks.neovitae.common.item.block.RuneBlockItem;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;
import com.breakinblocks.neovitae.util.helper.BlockWithItemHolder;
import com.breakinblocks.neovitae.util.helper.BlockWithItemRegister;

import java.util.List;
import java.util.function.Supplier;

public class NVBlocks {
    public static final DeferredRegister<Block> BASIC_BLOCKS = DeferredRegister.createBlocks(NeoVitae.MODID);
    public static final DeferredRegister<Item> BASIC_BLOCK_ITEMS = DeferredRegister.createItems(NeoVitae.MODID);
    public static final BlockWithItemRegister BASIC_REG = new BlockWithItemRegister(BASIC_BLOCKS, BASIC_BLOCK_ITEMS);

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(NeoVitae.MODID);
    public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.createItems(NeoVitae.MODID);
    public static final BlockWithItemRegister BLOCK_REG = new BlockWithItemRegister(BLOCKS, BLOCK_ITEMS);

    public static final BlockWithItemHolder<AraVitaeBlock, BlockItem> ARA_VITAE = BLOCK_REG.register("ara_vitae", AraVitaeBlock::new);
    public static final BlockWithItemHolder<BloodTankBlock, BlockItem> BLOOD_TANK = BLOCK_REG.register("blood_tank", BloodTankBlock::new, block -> new BlockItem(block, new Item.Properties().component(NVDataComponents.CONTAINER_TIER, 1)));
    public static final BlockWithItemHolder<BloodBatteryBlock, BlockItem> BLOOD_BATTERY = BLOCK_REG.register("blood_battery", BloodBatteryBlock::new);
    public static final BlockWithItemHolder<HellfireForgeBlock, BlockItem> HELLFIRE_FORGE = BLOCK_REG.register("hellfire_forge", HellfireForgeBlock::new);
    public static final BlockWithItemHolder<AthanorBlock, BlockItem> ATHANOR_BLOCK = BLOCK_REG.register("athanor", AthanorBlock::new);
    public static final BlockWithItemHolder<TeleposerBlock, BlockItem> TELEPOSER = BLOCK_REG.register("teleposer", TeleposerBlock::new);
    public static final BlockWithItemHolder<SpiritCacheBlock, BlockItem> SPIRIT_CACHE = BLOCK_REG.register("spirit_cache", SpiritCacheBlock::new);
    public static final BlockWithItemHolder<SpiritAccumulatorBlock, BlockItem> SPIRIT_ACCUMULATOR = BLOCK_REG.register("spirit_accumulator", SpiritAccumulatorBlock::new);
    public static final BlockWithItemHolder<VitaeLinkBlock, BlockItem> VITAE_LINK = BLOCK_REG.register("vitae_link", VitaeLinkBlock::new);
    public static final BlockWithItemHolder<OrbFillingLinkBlock, BlockItem> ORB_FILLING_LINK = BLOCK_REG.register("orb_filling_link", OrbFillingLinkBlock::new);

    private static final BlockBehaviour.Properties rune_properties = BlockBehaviour.Properties.of().strength(2.0F, 5.0F).sound(SoundType.STONE).requiresCorrectToolForDrops().lightLevel(state -> 10);
    private static final Item.Properties rune_item_properties = new Item.Properties();

    private static RuneBlockItem runeItem(Block block, String... tooltipSuffixes) {
        String[] keys = new String[tooltipSuffixes.length];
        for (int i = 0; i < tooltipSuffixes.length; i++) {
            keys[i] = "tooltip.neovitae.rune." + tooltipSuffixes[i];
        }
        return new RuneBlockItem(block, rune_item_properties, keys);
    }

    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_BLANK = BASIC_REG.register("rune_blank", rune_properties, b -> runeItem(b, "blank"));

    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_SACRIFICE = BASIC_REG.register("rune_sacrifice", rune_properties, b -> runeItem(b, "sacrifice"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_SELF_SACRIFICE = BASIC_REG.register("rune_sacrifice_self", rune_properties, b -> runeItem(b, "self_sacrifice"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_CAPACITY = BASIC_REG.register("rune_capacity", rune_properties, b -> runeItem(b, "capacity"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_CAPACITY_AUGMENTED = BASIC_REG.register("rune_capacity_augmented", rune_properties, b -> runeItem(b, "capacity_augmented"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_CHARGING = BASIC_REG.register("rune_charging", rune_properties, b -> runeItem(b, "charging"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_SPEED = BASIC_REG.register("rune_speed", rune_properties, b -> runeItem(b, "speed"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_ACCELERATION = BASIC_REG.register("rune_acceleration", rune_properties, b -> runeItem(b, "acceleration"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_DISLOCATION = BASIC_REG.register("rune_dislocation", rune_properties, b -> runeItem(b, "dislocation"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_ORB = BASIC_REG.register("rune_orb", rune_properties, b -> runeItem(b, "orb"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_EFFICIENCY = BASIC_REG.register("rune_efficiency", rune_properties, b -> runeItem(b, "efficiency"));

    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_SACRIFICE = BASIC_REG.register("rune_2_sacrifice", rune_properties, b -> runeItem(b, "sacrifice", "reinforced"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_SELF_SACRIFICE = BASIC_REG.register("rune_2_sacrifice_self", rune_properties, b -> runeItem(b, "self_sacrifice", "reinforced"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_CAPACITY = BASIC_REG.register("rune_2_capacity", rune_properties, b -> runeItem(b, "capacity", "reinforced"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_CAPACITY_AUGMENTED = BASIC_REG.register("rune_2_capacity_augmented", rune_properties, b -> runeItem(b, "capacity_augmented", "reinforced"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_CHARGING = BASIC_REG.register("rune_2_charging", rune_properties, b -> runeItem(b, "charging", "reinforced"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_SPEED = BASIC_REG.register("rune_2_speed", rune_properties, b -> runeItem(b, "speed", "reinforced"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_ACCELERATION = BASIC_REG.register("rune_2_acceleration", rune_properties, b -> runeItem(b, "acceleration", "reinforced"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_DISLOCATION = BASIC_REG.register("rune_2_dislocation", rune_properties, b -> runeItem(b, "dislocation", "reinforced"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_ORB = BASIC_REG.register("rune_2_orb", rune_properties, b -> runeItem(b, "orb", "reinforced"));
    public static final BlockWithItemHolder<Block, RuneBlockItem> RUNE_2_EFFICIENCY = BASIC_REG.register("rune_2_efficiency", rune_properties, b -> runeItem(b, "efficiency", "reinforced"));

    public static final BlockWithItemHolder<Block, BlockItem> BLOODSTONE = BASIC_REG.register("bloodstone", rune_properties, rune_item_properties);
    public static final BlockWithItemHolder<Block, BlockItem> BLOODSTONE_BRICK = BASIC_REG.register("bloodstone_brick", rune_properties, rune_item_properties);

    private static final BlockBehaviour.Properties metal_block_properties = BlockBehaviour.Properties.of().strength(5, 6).sound(SoundType.METAL).requiresCorrectToolForDrops();
    public static final BlockWithItemHolder<Block, BlockItem> HELLFORGED_BLOCK = BASIC_REG.register("hellforged_block", metal_block_properties, new Item.Properties());
    public static final BlockWithItemHolder<Block, BlockItem> RAW_DEMONITE_BLOCK = BASIC_REG.register("raw_demonite_block", metal_block_properties, new Item.Properties());

    public static final BlockWithItemHolder<EnchantingPowerBlock, BlockItem> CRYSTAL_CLUSTER = BASIC_REG.register("crystal_cluster", () -> new EnchantingPowerBlock(rune_properties, 2.5F), rune_item_properties);
    public static final BlockWithItemHolder<EnchantingPowerBlock, BlockItem> CRYSTAL_CLUSTER_BRICK = BASIC_REG.register("crystal_cluster_brick", () -> new EnchantingPowerBlock(rune_properties, 2.5F), rune_item_properties);

    public static final DeferredHolder<Block, AlchemyArrayBlock> ALCHEMY_ARRAY = BLOCKS.register("alchemy_array", (Supplier<AlchemyArrayBlock>) AlchemyArrayBlock::new);

    public static final DeferredHolder<Block, BloodLightBlock> BLOOD_LIGHT = BLOCKS.register("blood_light", BloodLightBlock::new);

    public static final BlockWithItemHolder<BloodLanternBlock, BlockItem> BLOOD_LANTERN =
            BLOCK_REG.register("blood_lantern", () -> new BloodLanternBlock(BloodLanternBlock.defaultProperties()));

    public static final BlockWithItemHolder<DemonLanternBlock, DemonLanternItem> DEMON_LANTERN =
            BLOCK_REG.register("demon_lantern", () -> new DemonLanternBlock(DemonLanternBlock.defaultProperties()),
                    block -> new DemonLanternItem(block, new Item.Properties()));

    public static final DeferredHolder<Block, SpectralBlock> SPECTRAL_BLOCK = BLOCKS.register("spectral_block", SpectralBlock::new);

    public static final DeferredHolder<Block, PhantomBridgeBlock> PHANTOM_BRIDGE_BLOCK = BLOCKS.register("phantom_bridge", PhantomBridgeBlock::new);

    public static final BlockWithItemHolder<TabulaVitaeBlock, ItemBlockTabulaVitae> TABULA_VITAE = BLOCK_REG.register("tabula_vitae", TabulaVitaeBlock::new, block -> new ItemBlockTabulaVitae(block, new Item.Properties()));

    public static final BlockWithItemHolder<BlockIncenseAltar, BlockItem> INCENSE_ALTAR = BLOCK_REG.register("incense_altar", BlockIncenseAltar::new);

    private static final BlockBehaviour.Properties tau_properties = BlockBehaviour.Properties.of().noCollission().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY).randomTicks();
    public static final BlockWithItemHolder<BlockTau, BlockItem> STRONG_TAU = BLOCK_REG.register("strong_tau", () -> new BlockTau(tau_properties, true));
    public static final BlockWithItemHolder<BlockTau, BlockItem> WEAK_TAU = BLOCK_REG.register("weak_tau", () -> {
        BlockTau weakTau = new BlockTau(tau_properties, false);
        weakTau.setStrongTauSupplier(() -> STRONG_TAU.block().get());
        return weakTau;
    });

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

    public static final BlockWithItemHolder<VasMaleficumBlock, BlockItem> VAS_MALEFICUM = BLOCK_REG.register("vas_maleficum", VasMaleficumBlock::new);
    public static final BlockWithItemHolder<CrystallariumMaleficumBlock, BlockItem> CRYSTALLARIUM_MALEFICUM = BLOCK_REG.register("crystallarium_maleficum", CrystallariumMaleficumBlock::new);
    public static final BlockWithItemHolder<SpiraInfernalisBlock, BlockItem> SPIRA_INFERNALIS = BLOCK_REG.register("spira_infernalis", SpiraInfernalisBlock::new);

    private static final BlockBehaviour.Properties crystal_block_properties = BlockBehaviour.Properties.of().strength(3.0F, 3.0F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops().lightLevel(state -> 7).noOcclusion();
    public static final BlockWithItemHolder<BlockSpiritusCrystal, BlockItem> RAW_SPIRITUS_CRYSTAL = BLOCK_REG.register("raw_spiritus_crystal", () -> new BlockSpiritusCrystal(SpiritusType.RAW, crystal_block_properties));
    public static final BlockWithItemHolder<BlockSpiritusCrystal, BlockItem> SPIRITUS_RUINA_CRYSTAL = BLOCK_REG.register("spiritus_ruina_crystal", () -> new BlockSpiritusCrystal(SpiritusType.RUINA, crystal_block_properties));
    public static final BlockWithItemHolder<BlockSpiritusCrystal, BlockItem> SPIRITUS_NIHILUM_CRYSTAL = BLOCK_REG.register("spiritus_nihilum_crystal", () -> new BlockSpiritusCrystal(SpiritusType.NIHILUM, crystal_block_properties));
    public static final BlockWithItemHolder<BlockSpiritusCrystal, BlockItem> SPIRITUS_VINDICTA_CRYSTAL = BLOCK_REG.register("spiritus_vindicta_crystal", () -> new BlockSpiritusCrystal(SpiritusType.VINDICTA, crystal_block_properties));
    public static final BlockWithItemHolder<BlockSpiritusCrystal, BlockItem> SPIRITUS_INVICTUS_CRYSTAL = BLOCK_REG.register("spiritus_invictus_crystal", () -> new BlockSpiritusCrystal(SpiritusType.INVICTUS, crystal_block_properties));

    private static final BlockBehaviour.Properties routing_node_properties = BlockBehaviour.Properties.of().strength(2.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion();
    public static final BlockWithItemHolder<BlockRoutingConduit, BlockItem> ROUTING_CONDUIT = BLOCK_REG.register("routing_conduit", () -> new BlockRoutingConduit(routing_node_properties));
    public static final BlockWithItemHolder<BlockInputRoutingNode, BlockItem> INPUT_ROUTING_NODE = BLOCK_REG.register("input_routing_node", () -> new BlockInputRoutingNode(routing_node_properties));
    public static final BlockWithItemHolder<BlockOutputRoutingNode, BlockItem> OUTPUT_ROUTING_NODE = BLOCK_REG.register("output_routing_node", () -> new BlockOutputRoutingNode(routing_node_properties));
    public static final BlockWithItemHolder<BlockMasterRoutingNode, BlockItem> MASTER_ROUTING_NODE = BLOCK_REG.register("master_routing_node", () -> new BlockMasterRoutingNode(routing_node_properties));

    private static final BlockBehaviour.Properties charge_properties = BlockBehaviour.Properties.of().strength(2.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion();

    public static final BlockWithItemHolder<BlockShapedExplosive, BlockItem> SHAPED_CHARGE = BLOCK_REG.register("shaped_charge", () -> new BlockShapedExplosive(3, charge_properties));
    public static final BlockWithItemHolder<BlockShapedExplosive, BlockItem> AUG_SHAPED_CHARGE = BLOCK_REG.register("aug_shaped_charge", () -> new BlockShapedExplosive(5, charge_properties));
    public static final BlockWithItemHolder<BlockShapedExplosiveDeep, BlockItem> SHAPED_CHARGE_DEEP = BLOCK_REG.register("shaped_charge_deep", () -> new BlockShapedExplosiveDeep(3, charge_properties));

    public static final BlockWithItemHolder<BlockDeforesterCharge, BlockItem> DEFORESTER_CHARGE = BLOCK_REG.register("deforester_charge", () -> new BlockDeforesterCharge(128, charge_properties));
    public static final BlockWithItemHolder<BlockDeforesterCharge, BlockItem> DEFORESTER_CHARGE_2 = BLOCK_REG.register("deforester_charge_2", () -> new BlockDeforesterCharge(256, charge_properties));

    public static final BlockWithItemHolder<BlockVeinMineCharge, BlockItem> VEINMINE_CHARGE = BLOCK_REG.register("veinmine_charge", () -> new BlockVeinMineCharge(128, charge_properties));
    public static final BlockWithItemHolder<BlockVeinMineCharge, BlockItem> VEINMINE_CHARGE_2 = BLOCK_REG.register("veinmine_charge_2", () -> new BlockVeinMineCharge(256, charge_properties));

    public static final BlockWithItemHolder<BlockFungalCharge, BlockItem> FUNGAL_CHARGE = BLOCK_REG.register("fungal_charge", () -> new BlockFungalCharge(128, charge_properties));
    public static final BlockWithItemHolder<BlockFungalCharge, BlockItem> FUNGAL_CHARGE_2 = BLOCK_REG.register("fungal_charge_2", () -> new BlockFungalCharge(256, charge_properties));

    private static final BlockBehaviour.Properties mimic_properties = BlockBehaviour.Properties.of().strength(2.0F, 5.0F).sound(SoundType.STONE).noOcclusion();
    public static final BlockWithItemHolder<BlockMimic, BlockItem> MIMIC = BLOCK_REG.register("mimic", () -> new BlockMimic(mimic_properties));
    private static final BlockBehaviour.Properties ethereal_mimic_properties = BlockBehaviour.Properties.of().strength(2.0F, 5.0F).sound(SoundType.STONE).noOcclusion().noCollission();
    public static final BlockWithItemHolder<BlockMimic, BlockItem> ETHEREAL_MIMIC = BLOCK_REG.register("ethereal_mimic", () -> new BlockMimic(ethereal_mimic_properties));

    private static final BlockBehaviour.Properties inversion_pillar_properties = BlockBehaviour.Properties.of().strength(2.0F, 5.0F).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion();
    public static final BlockWithItemHolder<BlockInversionPillar, BlockItem> INVERSION_PILLAR = BLOCK_REG.register("inversion_pillar", () -> new BlockInversionPillar(inversion_pillar_properties));
    public static final BlockWithItemHolder<BlockInversionPillarEnd, BlockItem> INVERSION_PILLAR_CAP = BLOCK_REG.register("inversion_pillar_cap", () -> new BlockInversionPillarEnd(inversion_pillar_properties));
    public static final BlockWithItemHolder<BlockSpatialRift, BlockItem> SPATIAL_RIFT = BLOCK_REG.register("spatial_rift", BlockSpatialRift::new);

    public static final BlockWithItemHolder<BlockDungeonController, BlockItem> DUNGEON_CONTROLLER = BLOCK_REG.register("dungeon_controller", BlockDungeonController::new);
    public static final BlockWithItemHolder<BlockDungeonSeal, BlockItem> DUNGEON_SEAL = BLOCK_REG.register("dungeon_seal", BlockDungeonSeal::new);
    public static final BlockWithItemHolder<BlockDungeonSealInaccessible, BlockItem> DUNGEON_SEAL_INACCESSIBLE = BLOCK_REG.register("dungeon_seal_inaccessible", BlockDungeonSealInaccessible::new);

    public static final BlockWithItemHolder<SandsOfVitaeBlock, BlockItem> SANDS_OF_VITAE = BLOCK_REG.register("sands_of_vitae", SandsOfVitaeBlock::new);

    public static final BlockWithItemHolder<BloodStainedGlassBlock, BlockItem> BLOOD_STAINED_GLASS = BLOCK_REG.register("blood_stained_glass", BloodStainedGlassBlock::new);

    public static final BlockWithItemHolder<IronBarsBlock, BlockItem> BLOOD_STAINED_GLASS_PANE = BLOCK_REG.register("blood_stained_glass_pane",
            () -> new IronBarsBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> 15)));

    public static void register(IEventBus modBus) {
        BASIC_BLOCKS.register(modBus);
        BASIC_BLOCK_ITEMS.register(modBus);
        BLOCKS.register(modBus);
        BLOCK_ITEMS.register(modBus);
    }
}
