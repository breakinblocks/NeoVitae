package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.anointment.AnointmentRegistrar;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.item.arc.ItemARCToolBase;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlask;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlaskLingering;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlaskThrowable;
import com.breakinblocks.neovitae.common.item.routing.ItemCompositeFilter;
import com.breakinblocks.neovitae.common.item.routing.ItemModFilter;
import com.breakinblocks.neovitae.common.item.routing.ItemNodeRouter;
import com.breakinblocks.neovitae.common.item.routing.ItemRouterFilter;
import com.breakinblocks.neovitae.common.item.routing.ItemTagFilter;
import com.breakinblocks.neovitae.common.item.sigil.ISigil;
import com.breakinblocks.neovitae.common.item.sigil.ItemSigilHolding;
import com.breakinblocks.neovitae.common.item.sigil.SigilItem;
import com.breakinblocks.neovitae.common.item.soul.MonsterSoulItem;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;
import com.breakinblocks.neovitae.common.item.soul.SentientAxeItem;
import com.breakinblocks.neovitae.common.item.soul.SentientPickaxeItem;
import com.breakinblocks.neovitae.common.item.soul.SentientScytheItem;
import com.breakinblocks.neovitae.common.item.soul.SentientShovelItem;
import com.breakinblocks.neovitae.common.item.soul.SentientSwordItem;
import com.breakinblocks.neovitae.common.item.soul.SoulSnareItem;
import com.breakinblocks.neovitae.ritual.EnumRuneType;

import java.util.function.Supplier;

public class BMItems {
    public static final DeferredRegister<Item> BASIC_ITEMS = DeferredRegister.createItems(NeoVitae.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(NeoVitae.MODID);
    public static final DeferredRegister<Item> WILL_ITEMS = DeferredRegister.createItems(NeoVitae.MODID);
    public static final DeferredRegister<Item> TAB_REQ = DeferredRegister.createItems(NeoVitae.MODID);

    // these go first for creative tab order
    public static final DeferredHolder<Item, ArmorItem> LIVING_HELMET = BASIC_ITEMS.register("living_helmet", makeLivingArmour(ArmorItem.Type.HELMET));
    public static final DeferredHolder<Item, LivingArmourItem> LIVING_PLATE = TAB_REQ.register("living_plate", LivingArmourItem::new);
    public static final DeferredHolder<Item, ArmorItem> LIVING_LEGGINGS = BASIC_ITEMS.register("living_leggings", makeLivingArmour(ArmorItem.Type.LEGGINGS));
    public static final DeferredHolder<Item, ArmorItem> LIVING_BOOTS = BASIC_ITEMS.register("living_boots", makeLivingArmour(ArmorItem.Type.BOOTS));
    public static final DeferredHolder<Item, UpgradeTomeItem> UPGRADE_TOME = TAB_REQ.register("upgrade_tome", UpgradeTomeItem::new);

    public static final DeferredHolder<Item, ScrapItem> UPGRADE_SCRAP = BASIC_ITEMS.register("upgrade_scrap", () -> new ScrapItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, ScrapItem> SYNTHETIC_POINT = BASIC_ITEMS.register("synthetic_point", () -> new ScrapItem(new Item.Properties().component(BMDataComponents.UPGRADE_SCRAP, 1)));

    public static final DeferredHolder<Item, ExperienceTomeItem> EXPERIENCE_TOME = BASIC_ITEMS.register("experience_tome", ExperienceTomeItem::new);

    public static final DeferredHolder<Item, TrainerItem> TRAINING_BRACELET = BASIC_ITEMS.register("training_bracelet", TrainerItem::new);

    public static final DeferredHolder<Item, BloodOrbItem> ORB_WEAK = BASIC_ITEMS.register("blood_orb_weak", BloodOrbItem::new);
    public static final DeferredHolder<Item, BloodOrbItem> ORB_APPRENTICE = BASIC_ITEMS.register("blood_orb_apprentice", BloodOrbItem::new);
    public static final DeferredHolder<Item, BloodOrbItem> ORB_MAGICIAN = BASIC_ITEMS.register("blood_orb_magician", BloodOrbItem::new);
    public static final DeferredHolder<Item, BloodOrbItem> ORB_MASTER = BASIC_ITEMS.register("blood_orb_master", BloodOrbItem::new);
    public static final DeferredHolder<Item, BloodOrbItem> ORB_ARCHMAGE = BASIC_ITEMS.register("blood_orb_archmage", BloodOrbItem::new);
    public static final DeferredHolder<Item, BloodOrbItem> ORB_TRANSCENDENT = BASIC_ITEMS.register("blood_orb_transcendent", BloodOrbItem::new);

    private static Supplier<ArmorItem> makeLivingArmour(ArmorItem.Type type) {
        return () -> new ArmorItem(BMMaterialsAndTiers.LIVING_ARMOUR_MATERIAL, type, new Item.Properties().durability(type.getDurability(33)));
    }

    /**
     * Helper to register a plain item with default properties.
     */
    private static DeferredHolder<Item, Item> plainItem(String name) {
        return BASIC_ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    public static final DeferredHolder<Item, SacrificialDaggerItem> SACRIFICIAL_DAGGER = ITEMS.register("sacrificial_dagger", SacrificialDaggerItem::new);

    public static final DeferredHolder<Item, RawSoulItem> RAW_WILL = WILL_ITEMS.register("raw_will", RawSoulItem::new);

    public static final DeferredHolder<Item, SoulGemItem> SOUL_GEM_PETTY = WILL_ITEMS.register("soul_gem_petty", SoulGemItem::new);
    public static final DeferredHolder<Item, SoulGemItem> SOUL_GEM_LESSER = WILL_ITEMS.register("soul_gem_lesser", SoulGemItem::new);
    public static final DeferredHolder<Item, SoulGemItem> SOUL_GEM_COMMON = WILL_ITEMS.register("soul_gem_common", SoulGemItem::new);
    public static final DeferredHolder<Item, SoulGemItem> SOUL_GEM_GREATER = WILL_ITEMS.register("soul_gem_greater", SoulGemItem::new);
    public static final DeferredHolder<Item, SoulGemItem> SOUL_GEM_GRAND = WILL_ITEMS.register("soul_gem_grand", SoulGemItem::new);

    // Monster Souls (dropped by mobs when killed with sentient weapons)
    public static final DeferredHolder<Item, MonsterSoulItem> MONSTER_SOUL_RAW = WILL_ITEMS.register("basemonstersoul", () -> new MonsterSoulItem(EnumWillType.DEFAULT));
    public static final DeferredHolder<Item, MonsterSoulItem> MONSTER_SOUL_CORROSIVE = WILL_ITEMS.register("basemonstersoul_corrosive", () -> new MonsterSoulItem(EnumWillType.CORROSIVE));
    public static final DeferredHolder<Item, MonsterSoulItem> MONSTER_SOUL_DESTRUCTIVE = WILL_ITEMS.register("basemonstersoul_destructive", () -> new MonsterSoulItem(EnumWillType.DESTRUCTIVE));
    public static final DeferredHolder<Item, MonsterSoulItem> MONSTER_SOUL_VENGEFUL = WILL_ITEMS.register("basemonstersoul_vengeful", () -> new MonsterSoulItem(EnumWillType.VENGEFUL));
    public static final DeferredHolder<Item, MonsterSoulItem> MONSTER_SOUL_STEADFAST = WILL_ITEMS.register("basemonstersoul_steadfast", () -> new MonsterSoulItem(EnumWillType.STEADFAST));

    // Slates
    public static final DeferredHolder<Item, Item> SLATE_BLANK = plainItem("blank_slate");
    public static final DeferredHolder<Item, Item> SLATE_REINFORCED = plainItem("reinforced_slate");
    public static final DeferredHolder<Item, Item> SLATE_IMBUED = plainItem("imbued_slate");
    public static final DeferredHolder<Item, Item> SLATE_DEMONIC = plainItem("demonic_slate");
    public static final DeferredHolder<Item, Item> SLATE_ETHEREAL = plainItem("ethereal_slate");

    // Sigils (data-driven via SigilType registry)
    public static final DeferredHolder<Item, SigilItem> SIGIL_DIVINATION = BASIC_ITEMS.register("sigil_divination", () -> new SigilItem(SigilTypeRegistry.key("divination")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_SEER = BASIC_ITEMS.register("sigil_seer", () -> new SigilItem(SigilTypeRegistry.key("seer")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_WATER = BASIC_ITEMS.register("sigil_water", () -> new SigilItem(SigilTypeRegistry.key("water")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_LAVA = BASIC_ITEMS.register("sigil_lava", () -> new SigilItem(SigilTypeRegistry.key("lava")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_VOID = BASIC_ITEMS.register("sigil_void", () -> new SigilItem(SigilTypeRegistry.key("void")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_GREEN_GROVE = BASIC_ITEMS.register("sigil_green_grove", () -> new SigilItem(SigilTypeRegistry.key("green_grove")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_AIR = BASIC_ITEMS.register("sigil_air", () -> new SigilItem(SigilTypeRegistry.key("air")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_BLOOD_LIGHT = BASIC_ITEMS.register("sigil_blood_light", () -> new SigilItem(SigilTypeRegistry.key("blood_light")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_FAST_MINER = BASIC_ITEMS.register("sigil_fast_miner", () -> new SigilItem(SigilTypeRegistry.key("fast_miner")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_MAGNETISM = BASIC_ITEMS.register("sigil_magnetism", () -> new SigilItem(SigilTypeRegistry.key("magnetism")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_FROST = BASIC_ITEMS.register("sigil_frost", () -> new SigilItem(SigilTypeRegistry.key("frost")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_SUPPRESSION = BASIC_ITEMS.register("sigil_suppression", () -> new SigilItem(SigilTypeRegistry.key("suppression")));
    public static final DeferredHolder<Item, ItemSigilHolding> SIGIL_HOLDING = BASIC_ITEMS.register("sigil_holding", ItemSigilHolding::new);
    public static final DeferredHolder<Item, SigilItem> SIGIL_TELEPOSITION = BASIC_ITEMS.register("sigil_teleposition", () -> new SigilItem(SigilTypeRegistry.key("teleposition")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_PHANTOM_BRIDGE = BASIC_ITEMS.register("sigil_phantom_bridge", () -> new SigilItem(SigilTypeRegistry.key("phantom_bridge")));

    // Alchemy items
    public static final DeferredHolder<Item, ItemArcaneAshes> ARCANE_ASHES = BASIC_ITEMS.register("arcane_ashes", ItemArcaneAshes::new);

    // Tau items (alchemy reagent oils - found in dungeon loot)
    public static final DeferredHolder<Item, Item> TAU_OIL = plainItem("tau_oil");

    // Reagents (used to make Sigils via Alchemy Array)
    public static final DeferredHolder<Item, Item> REAGENT_WATER = plainItem("reagent_water");
    public static final DeferredHolder<Item, Item> REAGENT_LAVA = plainItem("reagent_lava");
    public static final DeferredHolder<Item, Item> REAGENT_VOID = plainItem("reagent_void");
    public static final DeferredHolder<Item, Item> REAGENT_GROWTH = plainItem("reagent_growth");
    public static final DeferredHolder<Item, Item> REAGENT_FAST_MINER = plainItem("reagent_fast_miner");
    public static final DeferredHolder<Item, Item> REAGENT_MAGNETISM = plainItem("reagent_magnetism");
    public static final DeferredHolder<Item, Item> REAGENT_AIR = plainItem("reagent_air");
    public static final DeferredHolder<Item, Item> REAGENT_BLOOD_LIGHT = plainItem("reagent_blood_light");
    public static final DeferredHolder<Item, Item> REAGENT_SIGHT = plainItem("reagent_sight");
    public static final DeferredHolder<Item, Item> REAGENT_BINDING = plainItem("reagent_binding");
    public static final DeferredHolder<Item, Item> REAGENT_HOLDING = plainItem("reagent_holding");
    public static final DeferredHolder<Item, Item> REAGENT_SUPPRESSION = plainItem("reagent_suppression");
    public static final DeferredHolder<Item, Item> REAGENT_TELEPOSITION = plainItem("reagent_teleposition");
    public static final DeferredHolder<Item, Item> REAGENT_FROST = plainItem("reagent_frost");
    public static final DeferredHolder<Item, Item> REAGENT_PHANTOM_BRIDGE = plainItem("reagent_phantom_bridge");

    // Misc items
    public static final DeferredHolder<Item, SoulSnareItem> SOUL_SNARE = BASIC_ITEMS.register("soul_snare", SoulSnareItem::new);
    public static final DeferredHolder<Item, Item> WEAK_BLOOD_SHARD = plainItem("weak_blood_shard");
    public static final DeferredHolder<Item, Item> DAGGER_OF_SACRIFICE = ITEMS.register("dagger_of_sacrifice", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, ItemLavaCrystal> LAVA_CRYSTAL = ITEMS.register("lava_crystal", ItemLavaCrystal::new);

    // Teleposer Focus items
    public static final DeferredHolder<Item, TeleposerFocusItem> TELEPOSER_FOCUS = ITEMS.register("teleposer_focus", () -> new TeleposerFocusItem(0));
    public static final DeferredHolder<Item, TeleposerFocusItem> TELEPOSER_FOCUS_ENHANCED = ITEMS.register("enhanced_teleposer_focus", () -> new TeleposerFocusItem(1));
    public static final DeferredHolder<Item, TeleposerFocusItem> TELEPOSER_FOCUS_REINFORCED = ITEMS.register("reinforced_teleposer_focus", () -> new TeleposerFocusItem(2));

    // Fragments
    public static final DeferredHolder<Item, Item> IRON_FRAGMENT = plainItem("iron_fragment");
    public static final DeferredHolder<Item, Item> GOLD_FRAGMENT = plainItem("gold_fragment");
    public static final DeferredHolder<Item, Item> COPPER_FRAGMENT = plainItem("copper_fragment");
    public static final DeferredHolder<Item, Item> NETHERITE_SCRAP_FRAGMENT = plainItem("fragment_netherite_scrap");
    public static final DeferredHolder<Item, Item> DEMONITE_FRAGMENT = plainItem("demonite_fragment");

    // Gravels
    public static final DeferredHolder<Item, Item> IRON_GRAVEL = plainItem("iron_gravel");
    public static final DeferredHolder<Item, Item> GOLD_GRAVEL = plainItem("gold_gravel");
    public static final DeferredHolder<Item, Item> COPPER_GRAVEL = plainItem("copper_gravel");
    public static final DeferredHolder<Item, Item> NETHERITE_SCRAP_GRAVEL = plainItem("gravel_netherite_scrap");
    public static final DeferredHolder<Item, Item> DEMONITE_GRAVEL = plainItem("demonite_gravel");

    // Sands/Dusts
    public static final DeferredHolder<Item, Item> IRON_SAND = plainItem("iron_sand");
    public static final DeferredHolder<Item, Item> GOLD_SAND = plainItem("gold_sand");
    public static final DeferredHolder<Item, Item> COPPER_SAND = plainItem("copper_sand");
    public static final DeferredHolder<Item, Item> COAL_SAND = plainItem("coal_sand");
    public static final DeferredHolder<Item, Item> NETHERITE_SCRAP_SAND = plainItem("sand_netherite");
    public static final DeferredHolder<Item, Item> HELLFORGED_SAND = plainItem("sand_hellforged");
    public static final DeferredHolder<Item, Item> CORRUPTED_DUST = plainItem("corrupted_dust");
    public static final DeferredHolder<Item, Item> CORRUPTED_DUST_TINY = plainItem("corrupted_tiny_dust");

    // ARC Tools
    public static final DeferredHolder<Item, ItemARCToolBase> BASIC_CUTTING_FLUID = BASIC_ITEMS.register("basic_cutting_fluid", () -> new ItemARCToolBase(64, 1, EnumWillType.CORROSIVE));
    public static final DeferredHolder<Item, ItemARCToolBase> INTERMEDIATE_CUTTING_FLUID = BASIC_ITEMS.register("intermediate_cutting_fluid", () -> new ItemARCToolBase(256, 1.5, EnumWillType.CORROSIVE));
    public static final DeferredHolder<Item, ItemARCToolBase> ADVANCED_CUTTING_FLUID = BASIC_ITEMS.register("advanced_cutting_fluid", () -> new ItemARCToolBase(1024, 2, 2, EnumWillType.CORROSIVE));
    public static final DeferredHolder<Item, ItemARCToolBase> EXPLOSIVE_POWDER = BASIC_ITEMS.register("explosive_powder", () -> new ItemARCToolBase(64, 1, EnumWillType.DESTRUCTIVE));
    public static final DeferredHolder<Item, ItemARCToolBase> RESONATOR = BASIC_ITEMS.register("resonator", () -> new ItemARCToolBase(64, 1, EnumWillType.VENGEFUL));
    public static final DeferredHolder<Item, ItemARCToolBase> SANGUINE_REVERTER = BASIC_ITEMS.register("sanguine_reverter", () -> new ItemARCToolBase(32, 2, EnumWillType.STEADFAST));
    public static final DeferredHolder<Item, ItemARCToolBase> PRIMITIVE_FURNACE_CELL = BASIC_ITEMS.register("furnacecell_primitive", () -> new ItemARCToolBase(128, 3));
    public static final DeferredHolder<Item, ItemARCToolBase> PRIMITIVE_EXPLOSIVE_CELL = BASIC_ITEMS.register("primitive_explosive_cell", () -> new ItemARCToolBase(256, 1.5, EnumWillType.DESTRUCTIVE));
    public static final DeferredHolder<Item, ItemARCToolBase> PRIMITIVE_HYDRATION_CELL = BASIC_ITEMS.register("primitive_hydration_cell", () -> new ItemARCToolBase(128, 1.5));
    public static final DeferredHolder<Item, ItemARCToolBase> PRIMITIVE_CRYSTALLINE_RESONATOR = BASIC_ITEMS.register("primitive_crystalline_resonator", () -> new ItemARCToolBase(256, 1.5, EnumWillType.VENGEFUL));
    public static final DeferredHolder<Item, ItemARCToolBase> HELLFORGED_EXPLOSIVE_CELL = BASIC_ITEMS.register("hellforged_explosive_cell", () -> new ItemARCToolBase(1024, 2, EnumWillType.DESTRUCTIVE));
    public static final DeferredHolder<Item, ItemARCToolBase> HELLFORGED_RESONATOR = BASIC_ITEMS.register("hellforged_resonator", () -> new ItemARCToolBase(1024, 2, 2, EnumWillType.VENGEFUL));

    // Activation Crystals
    public static final DeferredHolder<Item, ItemActivationCrystal> ACTIVATION_CRYSTAL_WEAK = BASIC_ITEMS.register("activation_crystal_weak", () -> new ItemActivationCrystal(ItemActivationCrystal.CrystalType.WEAK));
    public static final DeferredHolder<Item, ItemActivationCrystal> ACTIVATION_CRYSTAL_AWAKENED = BASIC_ITEMS.register("activation_crystal_awakened", () -> new ItemActivationCrystal(ItemActivationCrystal.CrystalType.AWAKENED));
    public static final DeferredHolder<Item, ItemActivationCrystal> ACTIVATION_CRYSTAL_CREATIVE = BASIC_ITEMS.register("activation_crystal_creative", () -> new ItemActivationCrystal(ItemActivationCrystal.CrystalType.CREATIVE));

    // Inscription Tools
    public static final DeferredHolder<Item, ItemInscriptionTool> INSCRIPTION_TOOL_AIR = BASIC_ITEMS.register("air_scribe_tool", () -> new ItemInscriptionTool(EnumRuneType.AIR));
    public static final DeferredHolder<Item, ItemInscriptionTool> INSCRIPTION_TOOL_FIRE = BASIC_ITEMS.register("fire_scribe_tool", () -> new ItemInscriptionTool(EnumRuneType.FIRE));
    public static final DeferredHolder<Item, ItemInscriptionTool> INSCRIPTION_TOOL_WATER = BASIC_ITEMS.register("water_scribe_tool", () -> new ItemInscriptionTool(EnumRuneType.WATER));
    public static final DeferredHolder<Item, ItemInscriptionTool> INSCRIPTION_TOOL_EARTH = BASIC_ITEMS.register("earth_scribe_tool", () -> new ItemInscriptionTool(EnumRuneType.EARTH));
    public static final DeferredHolder<Item, ItemInscriptionTool> INSCRIPTION_TOOL_DUSK = BASIC_ITEMS.register("dusk_scribe_tool", () -> new ItemInscriptionTool(EnumRuneType.DUSK));

    // Ritual Diviners
    public static final DeferredHolder<Item, ItemRitualDiviner> RITUAL_DIVINER = BASIC_ITEMS.register("ritual_diviner", () -> new ItemRitualDiviner(0));
    public static final DeferredHolder<Item, ItemRitualDiviner> RITUAL_DIVINER_DUSK = BASIC_ITEMS.register("ritual_diviner_dusk", () -> new ItemRitualDiviner(1));
    public static final DeferredHolder<Item, ItemRitualReader> RITUAL_READER = BASIC_ITEMS.register("ritual_reader", ItemRitualReader::new);

    // Sentient Tools (will-powered weapons)
    public static final DeferredHolder<Item, SentientSwordItem> SENTIENT_SWORD = BASIC_ITEMS.register("soul_sword", SentientSwordItem::new);
    public static final DeferredHolder<Item, SentientAxeItem> SENTIENT_AXE = BASIC_ITEMS.register("soul_axe", SentientAxeItem::new);
    public static final DeferredHolder<Item, SentientPickaxeItem> SENTIENT_PICKAXE = BASIC_ITEMS.register("soul_pickaxe", SentientPickaxeItem::new);
    public static final DeferredHolder<Item, SentientShovelItem> SENTIENT_SHOVEL = BASIC_ITEMS.register("soul_shovel", SentientShovelItem::new);
    public static final DeferredHolder<Item, SentientScytheItem> SENTIENT_SCYTHE = BASIC_ITEMS.register("soul_scythe", SentientScytheItem::new);

    // Demon Will Items (crystal items - same naming as 1.20.1)
    public static final DeferredHolder<Item, DemonCrystalItem> RAW_CRYSTAL = BASIC_ITEMS.register("default_crystal", () -> new DemonCrystalItem(EnumWillType.DEFAULT));
    public static final DeferredHolder<Item, DemonCrystalItem> CORROSIVE_CRYSTAL = BASIC_ITEMS.register("corrosive_crystal", () -> new DemonCrystalItem(EnumWillType.CORROSIVE));
    public static final DeferredHolder<Item, DemonCrystalItem> DESTRUCTIVE_CRYSTAL = BASIC_ITEMS.register("destructive_crystal", () -> new DemonCrystalItem(EnumWillType.DESTRUCTIVE));
    public static final DeferredHolder<Item, DemonCrystalItem> VENGEFUL_CRYSTAL = BASIC_ITEMS.register("vengeful_crystal", () -> new DemonCrystalItem(EnumWillType.VENGEFUL));
    public static final DeferredHolder<Item, DemonCrystalItem> STEADFAST_CRYSTAL = BASIC_ITEMS.register("steadfast_crystal", () -> new DemonCrystalItem(EnumWillType.STEADFAST));
    public static final DeferredHolder<Item, DemonWillGaugeItem> DEMON_WILL_GAUGE = BASIC_ITEMS.register("demon_will_gauge", DemonWillGaugeItem::new);

    // Crystal Catalysts (used in soul forge recipes)
    public static final DeferredHolder<Item, Item> RAW_CRYSTAL_CATALYST = plainItem("raw_catalyst");
    public static final DeferredHolder<Item, Item> CORROSIVE_CRYSTAL_CATALYST = plainItem("corrosive_catalyst");
    public static final DeferredHolder<Item, Item> DESTRUCTIVE_CRYSTAL_CATALYST = plainItem("destructive_catalyst");
    public static final DeferredHolder<Item, Item> VENGEFUL_CRYSTAL_CATALYST = plainItem("vengeful_catalyst");
    public static final DeferredHolder<Item, Item> STEADFAST_CRYSTAL_CATALYST = plainItem("steadfast_catalyst");

    // Routing Node Items
    public static final DeferredHolder<Item, ItemNodeRouter> NODE_ROUTER = BASIC_ITEMS.register("node_router", ItemNodeRouter::new);
    public static final DeferredHolder<Item, Item> MASTER_NODE_UPGRADE = plainItem("master_core");
    public static final DeferredHolder<Item, Item> MASTER_NODE_UPGRADE_SPEED = plainItem("master_core_speed");

    // Throwing Daggers
    public static final DeferredHolder<Item, ItemThrowingDagger> THROWING_DAGGER = BASIC_ITEMS.register("throwing_dagger", () -> new ItemThrowingDagger());
    public static final DeferredHolder<Item, ItemThrowingDagger> THROWING_DAGGER_AMETHYST = BASIC_ITEMS.register("amethyst_throwing_dagger", () -> new ItemThrowingDagger());
    public static final DeferredHolder<Item, ItemThrowingDaggerSyringe> THROWING_DAGGER_SYRINGE = BASIC_ITEMS.register("throwing_dagger_syringe", () -> new ItemThrowingDaggerSyringe());
    public static final DeferredHolder<Item, ItemTippedThrowingDagger> THROWING_DAGGER_TIPPED = BASIC_ITEMS.register("tipped_throwing_dagger", () -> new ItemTippedThrowingDagger());

    // Dungeon/Key Items - unlock sealed doors in procedural dungeons
    // Keys match against room pool paths, e.g. "mini_dungeon" matches "neovitae:room_pools/tier1/mini_dungeon"
    public static final DeferredHolder<Item, com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonKey> SIMPLE_KEY = BASIC_ITEMS.register("simple_key",
            () -> new com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonKey("Simple", "mini_dungeon", "corridor", "hallway"));
    public static final DeferredHolder<Item, com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonKey> MINE_KEY = BASIC_ITEMS.register("mine_key",
            () -> new com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonKey("Mine", "mine_rooms", "mine_corridors", "mine_deadend"));
    public static final DeferredHolder<Item, com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonKey> MINE_ENTRANCE_KEY = BASIC_ITEMS.register("mine_entrance_key",
            () -> new com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonKey("Mine Entrance", "mine_entrances", "mine_key"));
    public static final DeferredHolder<Item, com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonKey> STANDARD_KEY = BASIC_ITEMS.register("standard_key",
            () -> new com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonKey("Standard", "standard_rooms", "connective_corridors", "standard_deadend"));
    public static final DeferredHolder<Item, com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonKey> BOSS_KEY = BASIC_ITEMS.register("boss_key",
            () -> new com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonKey("Boss", "boss", "special", "treasure"));

    // Dungeon Tester (debug item for testing dungeon generation)
    public static final DeferredHolder<Item, com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonTester> DUNGEON_TESTER = BASIC_ITEMS.register("dungeon_tester",
            com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonTester::new);

    // Misc WIP Items
    public static final DeferredHolder<Item, Item> DOUBT_SEED = plainItem("doubt_seed");

    // Simple Recipe Ingredients
    public static final DeferredHolder<Item, Item> SULFUR = plainItem("sulfur");
    public static final DeferredHolder<Item, Item> SALTPETER = plainItem("saltpeter");
    public static final DeferredHolder<Item, Item> PLANT_OIL = plainItem("plant_oil");
    public static final DeferredHolder<Item, Item> HELLFORGED_INGOT = plainItem("ingot_hellforged");

    // Alchemy Flask Items
    public static final DeferredHolder<Item, Item> SLATE_VIAL = BASIC_ITEMS.register("slate_vial", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, ItemAlchemyFlask> ALCHEMY_FLASK = BASIC_ITEMS.register("alchemy_flask", () -> new ItemAlchemyFlask());
    public static final DeferredHolder<Item, ItemAlchemyFlaskThrowable> ALCHEMY_FLASK_THROWABLE = BASIC_ITEMS.register("alchemy_flask_throwable", () -> new ItemAlchemyFlaskThrowable());
    public static final DeferredHolder<Item, ItemAlchemyFlaskLingering> ALCHEMY_FLASK_LINGERING = BASIC_ITEMS.register("alchemy_flask_lingering", () -> new ItemAlchemyFlaskLingering());

    // Anointment Items - weapon/tool coatings that provide temporary effects
    // Color constants for particle effects
    private static final int COLOR_MELEE = 0xCC3333;      // Red
    private static final int COLOR_SILK = 0x33CCCC;       // Cyan
    private static final int COLOR_FORTUNE = 0xFFD700;    // Gold
    private static final int COLOR_HOLY = 0xFFFFFF;       // White
    private static final int COLOR_KNOWLEDGE = 0x9933FF;  // Purple
    private static final int COLOR_QUICK_DRAW = 0x33FF33; // Green
    private static final int COLOR_LOOTING = 0xFF6600;    // Orange
    private static final int COLOR_BOW_POWER = 0x0066FF;  // Blue
    private static final int COLOR_WILL = 0x660066;       // Dark purple
    private static final int COLOR_SMELTING = 0xFF3300;   // Fire orange
    private static final int COLOR_VOIDING = 0x333333;    // Dark gray
    private static final int COLOR_BOW_VELOCITY = 0x66CCFF; // Light blue
    private static final int COLOR_REPAIR = 0x00FF66;     // Teal

    // Duration constants (uses before anointment expires)
    private static final int USES_BASE = 256;
    private static final int USES_L = 1024;     // Long duration
    private static final int USES_XL = 4096;    // Extra long duration

    /** Helper to reduce anointment registration boilerplate */
    private static DeferredHolder<Item, ItemAnointmentProvider> anointment(String name, String rlPath, int color, int level, int uses) {
        return BASIC_ITEMS.register(name, () -> new ItemAnointmentProvider(NeoVitae.rl(rlPath), color, level, uses));
    }

    // Base tier anointments (level 1, 256 uses)
    public static final DeferredHolder<Item, ItemAnointmentProvider> MELEE_DAMAGE_ANOINTMENT = anointment("melee_anointment", "melee_damage", COLOR_MELEE, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> SILK_TOUCH_ANOINTMENT = anointment("silk_touch_anointment", "silk_touch", COLOR_SILK, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> FORTUNE_ANOINTMENT = anointment("fortune_anointment", "fortune", COLOR_FORTUNE, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> HOLY_WATER_ANOINTMENT = anointment("holy_water_anointment", "holy_water", COLOR_HOLY, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> HIDDEN_KNOWLEDGE_ANOINTMENT = anointment("hidden_knowledge_anointment", "hidden_knowledge", COLOR_KNOWLEDGE, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> QUICK_DRAW_ANOINTMENT = anointment("quick_draw_anointment", "quick_draw", COLOR_QUICK_DRAW, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> LOOTING_ANOINTMENT = anointment("looting_anointment", "looting", COLOR_LOOTING, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> BOW_POWER_ANOINTMENT = anointment("bow_power_anointment", "bow_power", COLOR_BOW_POWER, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> WILL_POWER_ANOINTMENT = anointment("will_power_anointment", "will_power", COLOR_WILL, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> SMELTING_ANOINTMENT = anointment("smelting_anointment", "smelting", COLOR_SMELTING, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> VOIDING_ANOINTMENT = anointment("voiding_anointment", "voiding", COLOR_VOIDING, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> BOW_VELOCITY_ANOINTMENT = anointment("bow_velocity_anointment", "bow_velocity", COLOR_BOW_VELOCITY, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> WEAPON_REPAIR_ANOINTMENT = anointment("weapon_repair_anointment", "repairing", COLOR_REPAIR, 1, USES_BASE);

    // Anointment _L variants (level 1, 1024 uses - long duration)
    public static final DeferredHolder<Item, ItemAnointmentProvider> MELEE_DAMAGE_ANOINTMENT_L = anointment("melee_anointment_l", "melee_damage", COLOR_MELEE, 1, USES_L);
    public static final DeferredHolder<Item, ItemAnointmentProvider> SILK_TOUCH_ANOINTMENT_L = anointment("silk_touch_anointment_l", "silk_touch", COLOR_SILK, 1, USES_L);
    public static final DeferredHolder<Item, ItemAnointmentProvider> FORTUNE_ANOINTMENT_L = anointment("fortune_anointment_l", "fortune", COLOR_FORTUNE, 1, USES_L);
    public static final DeferredHolder<Item, ItemAnointmentProvider> HOLY_WATER_ANOINTMENT_L = anointment("holy_water_anointment_l", "holy_water", COLOR_HOLY, 1, USES_L);
    public static final DeferredHolder<Item, ItemAnointmentProvider> HIDDEN_KNOWLEDGE_ANOINTMENT_L = anointment("hidden_knowledge_anointment_l", "hidden_knowledge", COLOR_KNOWLEDGE, 1, USES_L);
    public static final DeferredHolder<Item, ItemAnointmentProvider> QUICK_DRAW_ANOINTMENT_L = anointment("quick_draw_anointment_l", "quick_draw", COLOR_QUICK_DRAW, 1, USES_L);
    public static final DeferredHolder<Item, ItemAnointmentProvider> LOOTING_ANOINTMENT_L = anointment("looting_anointment_l", "looting", COLOR_LOOTING, 1, USES_L);
    public static final DeferredHolder<Item, ItemAnointmentProvider> BOW_POWER_ANOINTMENT_L = anointment("bow_power_anointment_l", "bow_power", COLOR_BOW_POWER, 1, USES_L);
    public static final DeferredHolder<Item, ItemAnointmentProvider> SMELTING_ANOINTMENT_L = anointment("smelting_anointment_l", "smelting", COLOR_SMELTING, 1, USES_L);
    public static final DeferredHolder<Item, ItemAnointmentProvider> VOIDING_ANOINTMENT_L = anointment("voiding_anointment_l", "voiding", COLOR_VOIDING, 1, USES_L);
    public static final DeferredHolder<Item, ItemAnointmentProvider> BOW_VELOCITY_ANOINTMENT_L = anointment("bow_velocity_anointment_l", "bow_velocity", COLOR_BOW_VELOCITY, 1, USES_L);
    public static final DeferredHolder<Item, ItemAnointmentProvider> WEAPON_REPAIR_ANOINTMENT_L = anointment("weapon_repair_anointment_l", "repairing", COLOR_REPAIR, 1, USES_L);

    // Anointment _2 variants (level 2, 256 uses)
    public static final DeferredHolder<Item, ItemAnointmentProvider> MELEE_DAMAGE_ANOINTMENT_2 = anointment("melee_anointment_2", "melee_damage", COLOR_MELEE, 2, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> FORTUNE_ANOINTMENT_2 = anointment("fortune_anointment_2", "fortune", COLOR_FORTUNE, 2, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> HOLY_WATER_ANOINTMENT_2 = anointment("holy_water_anointment_2", "holy_water", COLOR_HOLY, 2, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> HIDDEN_KNOWLEDGE_ANOINTMENT_2 = anointment("hidden_knowledge_anointment_2", "hidden_knowledge", COLOR_KNOWLEDGE, 2, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> QUICK_DRAW_ANOINTMENT_2 = anointment("quick_draw_anointment_2", "quick_draw", COLOR_QUICK_DRAW, 2, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> LOOTING_ANOINTMENT_2 = anointment("looting_anointment_2", "looting", COLOR_LOOTING, 2, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> BOW_POWER_ANOINTMENT_2 = anointment("bow_power_anointment_2", "bow_power", COLOR_BOW_POWER, 2, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> BOW_POWER_ANOINTMENT_STRONG = anointment("bow_power_anointment_strong", "bow_power", COLOR_BOW_POWER, 4, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> BOW_VELOCITY_ANOINTMENT_2 = anointment("bow_velocity_anointment_2", "bow_velocity", COLOR_BOW_VELOCITY, 2, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> WEAPON_REPAIR_ANOINTMENT_2 = anointment("weapon_repair_anointment_2", "repairing", COLOR_REPAIR, 2, USES_BASE);

    // Anointment _XL variants (level 1, 4096 uses - extra long duration)
    public static final DeferredHolder<Item, ItemAnointmentProvider> MELEE_DAMAGE_ANOINTMENT_XL = anointment("melee_anointment_xl", "melee_damage", COLOR_MELEE, 1, USES_XL);
    public static final DeferredHolder<Item, ItemAnointmentProvider> SILK_TOUCH_ANOINTMENT_XL = anointment("silk_touch_anointment_xl", "silk_touch", COLOR_SILK, 1, USES_XL);
    public static final DeferredHolder<Item, ItemAnointmentProvider> FORTUNE_ANOINTMENT_XL = anointment("fortune_anointment_xl", "fortune", COLOR_FORTUNE, 1, USES_XL);
    public static final DeferredHolder<Item, ItemAnointmentProvider> HOLY_WATER_ANOINTMENT_XL = anointment("holy_water_anointment_xl", "holy_water", COLOR_HOLY, 1, USES_XL);
    public static final DeferredHolder<Item, ItemAnointmentProvider> HIDDEN_KNOWLEDGE_ANOINTMENT_XL = anointment("hidden_knowledge_anointment_xl", "hidden_knowledge", COLOR_KNOWLEDGE, 1, USES_XL);
    public static final DeferredHolder<Item, ItemAnointmentProvider> QUICK_DRAW_ANOINTMENT_XL = anointment("quick_draw_anointment_xl", "quick_draw", COLOR_QUICK_DRAW, 1, USES_XL);
    public static final DeferredHolder<Item, ItemAnointmentProvider> LOOTING_ANOINTMENT_XL = anointment("looting_anointment_xl", "looting", COLOR_LOOTING, 1, USES_XL);
    public static final DeferredHolder<Item, ItemAnointmentProvider> BOW_POWER_ANOINTMENT_XL = anointment("bow_power_anointment_xl", "bow_power", COLOR_BOW_POWER, 1, USES_XL);
    public static final DeferredHolder<Item, ItemAnointmentProvider> SMELTING_ANOINTMENT_XL = anointment("smelting_anointment_xl", "smelting", COLOR_SMELTING, 1, USES_XL);
    public static final DeferredHolder<Item, ItemAnointmentProvider> VOIDING_ANOINTMENT_XL = anointment("voiding_anointment_xl", "voiding", COLOR_VOIDING, 1, USES_XL);
    public static final DeferredHolder<Item, ItemAnointmentProvider> BOW_VELOCITY_ANOINTMENT_XL = anointment("bow_velocity_anointment_xl", "bow_velocity", COLOR_BOW_VELOCITY, 1, USES_XL);
    public static final DeferredHolder<Item, ItemAnointmentProvider> WEAPON_REPAIR_ANOINTMENT_XL = anointment("weapon_repair_anointment_xl", "repairing", COLOR_REPAIR, 1, USES_XL);

    // Anointment _3 variants (level 3, 256 uses)
    public static final DeferredHolder<Item, ItemAnointmentProvider> MELEE_DAMAGE_ANOINTMENT_3 = anointment("melee_anointment_3", "melee_damage", COLOR_MELEE, 3, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> FORTUNE_ANOINTMENT_3 = anointment("fortune_anointment_3", "fortune", COLOR_FORTUNE, 3, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> HOLY_WATER_ANOINTMENT_3 = anointment("holy_water_anointment_3", "holy_water", COLOR_HOLY, 3, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> HIDDEN_KNOWLEDGE_ANOINTMENT_3 = anointment("hidden_knowledge_anointment_3", "hidden_knowledge", COLOR_KNOWLEDGE, 3, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> QUICK_DRAW_ANOINTMENT_3 = anointment("quick_draw_anointment_3", "quick_draw", COLOR_QUICK_DRAW, 3, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> LOOTING_ANOINTMENT_3 = anointment("looting_anointment_3", "looting", COLOR_LOOTING, 3, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> BOW_POWER_ANOINTMENT_3 = anointment("bow_power_anointment_3", "bow_power", COLOR_BOW_POWER, 3, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> BOW_VELOCITY_ANOINTMENT_3 = anointment("bow_velocity_anointment_3", "bow_velocity", COLOR_BOW_VELOCITY, 3, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> WEAPON_REPAIR_ANOINTMENT_3 = anointment("weapon_repair_anointment_3", "repairing", COLOR_REPAIR, 3, USES_BASE);

    // Routing/Filter Items
    public static final DeferredHolder<Item, Item> FRAME_PARTS = plainItem("component_frame_parts");
    public static final DeferredHolder<Item, ItemRouterFilter> ITEM_ROUTER_FILTER = BASIC_ITEMS.register("item_router_filter_exact", ItemRouterFilter::new);
    public static final DeferredHolder<Item, ItemTagFilter> ITEM_TAG_FILTER = BASIC_ITEMS.register("item_router_filter_tag", ItemTagFilter::new);
    public static final DeferredHolder<Item, Item> ITEM_ENCHANT_FILTER = plainItem("item_router_filter_enchant"); // Placeholder - enchant filter not in original 1.20.1
    public static final DeferredHolder<Item, ItemModFilter> ITEM_MOD_FILTER = BASIC_ITEMS.register("item_router_filter_mod", ItemModFilter::new);
    public static final DeferredHolder<Item, ItemCompositeFilter> ITEM_COMPOSITE_FILTER = BASIC_ITEMS.register("item_router_filter_composite", ItemCompositeFilter::new);


    // Bleeding Edge Music Disc
    public static final DeferredHolder<Item, Item> BLEEDING_EDGE = BASIC_ITEMS.register("bleeding_edge", () -> new Item(
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(net.minecraft.world.item.Rarity.RARE)
                    .jukeboxPlayable(net.minecraft.resources.ResourceKey.create(
                            net.minecraft.core.registries.Registries.JUKEBOX_SONG,
                            com.breakinblocks.neovitae.NeoVitae.rl("bleeding_edge")
                    ))
    ));

    // Alchemy Catalysts (used in potion brewing system)
    public static final DeferredHolder<Item, Item> SIMPLE_CATALYST = plainItem("simple_catalyst");
    public static final DeferredHolder<Item, Item> STRENGTHENED_CATALYST = plainItem("strengthened_catalyst");
    public static final DeferredHolder<Item, Item> CYCLING_CATALYST = plainItem("cycling_catalyst");
    public static final DeferredHolder<Item, Item> COMBINATIONAL_CATALYST = plainItem("combinational_catalyst");
    public static final DeferredHolder<Item, Item> MUNDANE_LENGTHENING_CATALYST = plainItem("mundane_lengthening_catalyst");
    public static final DeferredHolder<Item, Item> MUNDANE_POWER_CATALYST = plainItem("mundane_power_catalyst");
    public static final DeferredHolder<Item, Item> AVERAGE_LENGTHENING_CATALYST = plainItem("average_lengthening_catalyst");
    public static final DeferredHolder<Item, Item> AVERAGE_POWER_CATALYST = plainItem("average_power_catalyst");

    // Filling Agents (used in potion brewing)
    public static final DeferredHolder<Item, Item> WEAK_FILLING_AGENT = plainItem("weak_filling_agent");
    public static final DeferredHolder<Item, Item> STANDARD_FILLING_AGENT = plainItem("standard_filling_agent");

    // Hellforged Parts (dropped from rune reversion)
    public static final DeferredHolder<Item, Item> HELLFORGED_PARTS = plainItem("hellforged_parts");

    // Blood Provider Items (provide LP directly to altar)
    public static final DeferredHolder<Item, ItemBloodProvider> SLATE_AMPOULE = BASIC_ITEMS.register("slate_ampoule", () -> new ItemBloodProvider("slate", 500));

    public static void register(IEventBus modBus) {
        BASIC_ITEMS.register(modBus);
        ITEMS.register(modBus);
        WILL_ITEMS.register(modBus);
        TAB_REQ.register(modBus);
    }
}
