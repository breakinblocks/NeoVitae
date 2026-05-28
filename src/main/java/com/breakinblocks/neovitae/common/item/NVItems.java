package com.breakinblocks.neovitae.common.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.anointment.AnointmentRegistrar;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.entity.NVEntities;
import com.breakinblocks.neovitae.common.item.athanor.ItemAthanorToolBase;
import com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonKey;
import com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonTester;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlask;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlaskLingering;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlaskThrowable;
import com.breakinblocks.neovitae.common.item.routing.ItemNodeRouter;
import com.breakinblocks.neovitae.common.material.MaterialRegistry;
import com.breakinblocks.neovitae.common.item.sigil.ISigil;
import com.breakinblocks.neovitae.common.item.sigil.ItemSigilDamned;
import com.breakinblocks.neovitae.common.item.sigil.ItemSigilHolding;
import com.breakinblocks.neovitae.common.item.sigil.SigilItem;
import com.breakinblocks.neovitae.common.item.soul.SpiritusEssenceItem;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;
import com.breakinblocks.neovitae.common.item.soul.LexVitaeItem;
import com.breakinblocks.neovitae.common.item.soul.SentientAxeItem;
import com.breakinblocks.neovitae.common.item.soul.SentientPickaxeItem;
import com.breakinblocks.neovitae.common.item.soul.SentientScytheItem;
import com.breakinblocks.neovitae.common.item.soul.SentientShovelItem;
import com.breakinblocks.neovitae.common.item.soul.SentientSwordItem;
import com.breakinblocks.neovitae.ritual.EnumRuneType;

import java.util.function.Supplier;

public class NVItems {
    public static final DeferredRegister<Item> BASIC_ITEMS = DeferredRegister.createItems(NeoVitae.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(NeoVitae.MODID);
    public static final DeferredRegister<Item> SPIRITUS_ITEMS = DeferredRegister.createItems(NeoVitae.MODID);
    public static final DeferredRegister<Item> TAB_REQ = DeferredRegister.createItems(NeoVitae.MODID);

    public static final DeferredHolder<Item, ArmorItem> SENTIENT_HELMET = BASIC_ITEMS.register("sentient_helmet", makeSentientArmour(ArmorItem.Type.HELMET));
    public static final DeferredHolder<Item, SentientArmourItem> SENTIENT_PLATE = TAB_REQ.register("sentient_plate", SentientArmourItem::new);
    public static final DeferredHolder<Item, ArmorItem> SENTIENT_LEGGINGS = BASIC_ITEMS.register("sentient_leggings", makeSentientArmour(ArmorItem.Type.LEGGINGS));
    public static final DeferredHolder<Item, ArmorItem> SENTIENT_BOOTS = BASIC_ITEMS.register("sentient_boots", makeSentientArmour(ArmorItem.Type.BOOTS));
    public static final DeferredHolder<Item, UpgradeTomeItem> UPGRADE_TOME = TAB_REQ.register("upgrade_tome", UpgradeTomeItem::new);

    public static final DeferredHolder<Item, ScrapItem> UPGRADE_SCRAP = BASIC_ITEMS.register("upgrade_scrap", () -> new ScrapItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, ScrapItem> SYNTHETIC_POINT = BASIC_ITEMS.register("synthetic_point", () -> new ScrapItem(new Item.Properties().component(NVDataComponents.UPGRADE_SCRAP, 1)));

    public static final DeferredHolder<Item, ExperienceTomeItem> EXPERIENCE_TOME = BASIC_ITEMS.register("experience_tome", ExperienceTomeItem::new);

    public static final DeferredHolder<Item, TrainerItem> TRAINING_BRACELET = BASIC_ITEMS.register("training_bracelet", TrainerItem::new);

    public static final DeferredHolder<Item, BloodOrbItem> ORB_WEAK = BASIC_ITEMS.register("blood_orb_weak", BloodOrbItem::new);
    public static final DeferredHolder<Item, BloodOrbItem> ORB_APPRENTICE = BASIC_ITEMS.register("blood_orb_apprentice", BloodOrbItem::new);
    public static final DeferredHolder<Item, BloodOrbItem> ORB_MAGICIAN = BASIC_ITEMS.register("blood_orb_magician", BloodOrbItem::new);
    public static final DeferredHolder<Item, BloodOrbItem> ORB_MASTER = BASIC_ITEMS.register("blood_orb_master", BloodOrbItem::new);
    public static final DeferredHolder<Item, BloodOrbItem> ORB_ARCHMAGE = BASIC_ITEMS.register("blood_orb_archmage", BloodOrbItem::new);
    public static final DeferredHolder<Item, BloodOrbItem> ORB_TRANSCENDENT = BASIC_ITEMS.register("blood_orb_transcendent", BloodOrbItem::new);

    private static Supplier<ArmorItem> makeSentientArmour(ArmorItem.Type type) {
        return () -> new ArmorItem(NVMaterialsAndTiers.SENTIENT_ARMOUR_MATERIAL, type, new Item.Properties().durability(type.getDurability(33)));
    }

    private static DeferredHolder<Item, Item> plainItem(String name) {
        return BASIC_ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    public static final DeferredHolder<Item, SacrificialDaggerItem> SACRIFICIAL_DAGGER = ITEMS.register("sacrificial_dagger", SacrificialDaggerItem::new);

    public static final DeferredHolder<Item, RawSpiritusItem> RAW_SPIRITUS = SPIRITUS_ITEMS.register("raw_spiritus", RawSpiritusItem::new);

    public static final DeferredHolder<Item, SpiritusGemItem> SPIRITUS_GEM_PETTY = SPIRITUS_ITEMS.register("spiritus_gem_petty", SpiritusGemItem::new);
    public static final DeferredHolder<Item, SpiritusGemItem> SPIRITUS_GEM_LESSER = SPIRITUS_ITEMS.register("spiritus_gem_lesser", SpiritusGemItem::new);
    public static final DeferredHolder<Item, SpiritusGemItem> SPIRITUS_GEM_COMMON = SPIRITUS_ITEMS.register("spiritus_gem_common", SpiritusGemItem::new);
    public static final DeferredHolder<Item, SpiritusGemItem> SPIRITUS_GEM_GREATER = SPIRITUS_ITEMS.register("spiritus_gem_greater", SpiritusGemItem::new);
    public static final DeferredHolder<Item, SpiritusGemItem> SPIRITUS_GEM_GRAND = SPIRITUS_ITEMS.register("spiritus_gem_grand", SpiritusGemItem::new);

    public static final DeferredHolder<Item, SpiritusEssenceItem> MONSTER_SOUL_RAW = SPIRITUS_ITEMS.register("base_spiritus_soul_raw", () -> new SpiritusEssenceItem(SpiritusType.RAW));
    public static final DeferredHolder<Item, SpiritusEssenceItem> MONSTER_SOUL_RUINA = SPIRITUS_ITEMS.register("base_spiritus_soul_ruina", () -> new SpiritusEssenceItem(SpiritusType.RUINA));
    public static final DeferredHolder<Item, SpiritusEssenceItem> MONSTER_SOUL_NIHILUM = SPIRITUS_ITEMS.register("base_spiritus_soul_nihilum", () -> new SpiritusEssenceItem(SpiritusType.NIHILUM));
    public static final DeferredHolder<Item, SpiritusEssenceItem> MONSTER_SOUL_VINDICTA = SPIRITUS_ITEMS.register("base_spiritus_soul_vindicta", () -> new SpiritusEssenceItem(SpiritusType.VINDICTA));
    public static final DeferredHolder<Item, SpiritusEssenceItem> MONSTER_SOUL_INVICTUS = SPIRITUS_ITEMS.register("base_spiritus_soul_invictus", () -> new SpiritusEssenceItem(SpiritusType.INVICTUS));

    public static final DeferredHolder<Item, Item> TABULA_RASA = plainItem("tabula_rasa");
    public static final DeferredHolder<Item, Item> TABULA_ROBUR = plainItem("tabula_robur");
    public static final DeferredHolder<Item, Item> TABULA_ANIMATA = plainItem("tabula_animata");
    public static final DeferredHolder<Item, Item> TABULA_SPIRITUS = plainItem("tabula_spiritus");
    public static final DeferredHolder<Item, Item> TABULA_AETHEREA = plainItem("tabula_aetherea");

    public static final DeferredHolder<Item, SigilItem> SIGIL_DIVINATION = BASIC_ITEMS.register("sigil_divination", () -> new SigilItem(SigilTypeRegistry.key("divination")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_SEER = BASIC_ITEMS.register("sigil_seer", () -> new SigilItem(SigilTypeRegistry.key("seer")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_WATER = BASIC_ITEMS.register("sigil_water", () -> new SigilItem(SigilTypeRegistry.key("water")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_LAVA = BASIC_ITEMS.register("sigil_lava", () -> new SigilItem(SigilTypeRegistry.key("lava")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_VOID = BASIC_ITEMS.register("sigil_void", () -> new SigilItem(SigilTypeRegistry.key("void")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_GREEN_GROVE = BASIC_ITEMS.register("sigil_green_grove", () -> new SigilItem(SigilTypeRegistry.key("green_grove")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_AIR = BASIC_ITEMS.register("sigil_air", () -> new SigilItem(SigilTypeRegistry.key("air")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_BLOOD_LIGHT = BASIC_ITEMS.register("sigil_blood_light", () -> new SigilItem(SigilTypeRegistry.key("blood_light"),
            new Item.Properties()
                    .component(NVDataComponents.BLOOD_LIGHT_BRIGHTNESS.get(), 15)
                    .component(NVDataComponents.BLOOD_LIGHT_COLOR.get(), DyeColor.RED)));
    public static final DeferredHolder<Item, SigilItem> SIGIL_FAST_MINER = BASIC_ITEMS.register("sigil_fast_miner", () -> new SigilItem(SigilTypeRegistry.key("fast_miner")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_MAGNETISM = BASIC_ITEMS.register("sigil_magnetism", () -> new SigilItem(SigilTypeRegistry.key("magnetism")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_FROST = BASIC_ITEMS.register("sigil_frost", () -> new SigilItem(SigilTypeRegistry.key("frost")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_SUPPRESSION = BASIC_ITEMS.register("sigil_suppression", () -> new SigilItem(SigilTypeRegistry.key("suppression")));
    public static final DeferredHolder<Item, ItemSigilHolding> SIGIL_HOLDING = BASIC_ITEMS.register("sigil_holding", ItemSigilHolding::new);
    public static final DeferredHolder<Item, SigilItem> SIGIL_TELEPOSITION = BASIC_ITEMS.register("sigil_teleposition", () -> new SigilItem(SigilTypeRegistry.key("teleposition")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_PHANTOM_BRIDGE = BASIC_ITEMS.register("sigil_phantom_bridge", () -> new SigilItem(SigilTypeRegistry.key("phantom_bridge")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_NECROMANCY = BASIC_ITEMS.register("sigil_necromancy", () -> new SigilItem(SigilTypeRegistry.key("necromancy")));
    public static final DeferredHolder<Item, SigilItem> SIGIL_BOUND_TREASURES = BASIC_ITEMS.register("sigil_bound_treasures", () -> new SigilItem(SigilTypeRegistry.key("bound_treasures")));

    public static final DeferredHolder<Item, ItemArcaneScribeTool> ARCANE_SCRIBE_TOOL = BASIC_ITEMS.register("arcane_scribe_tool", ItemArcaneScribeTool::new);

    public static final DeferredHolder<Item, Item> TAU_OIL = plainItem("tau_oil");

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

    public static final DeferredHolder<Item, Item> WEAK_BLOOD_SHARD = plainItem("weak_blood_shard");

    public static final DeferredHolder<Item, ItemLavaCrystal> LAVA_CRYSTAL = ITEMS.register("lava_crystal", ItemLavaCrystal::new);

    public static final DeferredHolder<Item, TeleposerFocusItem> TELEPOSER_FOCUS = ITEMS.register("teleposer_focus", () -> new TeleposerFocusItem(0));
    public static final DeferredHolder<Item, TeleposerFocusItem> TELEPOSER_FOCUS_ENHANCED = ITEMS.register("enhanced_teleposer_focus", () -> new TeleposerFocusItem(1));
    public static final DeferredHolder<Item, TeleposerFocusItem> TELEPOSER_FOCUS_REINFORCED = ITEMS.register("reinforced_teleposer_focus", () -> new TeleposerFocusItem(2));

    // Material items registered by MaterialRegistry from config/neovitae/materials.json
    // These accessors delegate to MaterialRegistry for backwards compatibility
    public static final DeferredHolder<Item, Item> IRON_FRAGMENT = MaterialRegistry.getFragment("iron");
    public static final DeferredHolder<Item, Item> GOLD_FRAGMENT = MaterialRegistry.getFragment("gold");
    public static final DeferredHolder<Item, Item> COPPER_FRAGMENT = MaterialRegistry.getFragment("copper");
    public static final DeferredHolder<Item, Item> NETHERITE_SCRAP_FRAGMENT = MaterialRegistry.getFragment("netherite_scrap");
    public static final DeferredHolder<Item, Item> DEMONITE_RAW = plainItem("raw_demonite");
    public static final DeferredHolder<Item, Item> DEMONITE_FRAGMENT = MaterialRegistry.getFragment("demonite");

    public static final DeferredHolder<Item, Item> IRON_GRAVEL = MaterialRegistry.getGravel("iron");
    public static final DeferredHolder<Item, Item> GOLD_GRAVEL = MaterialRegistry.getGravel("gold");
    public static final DeferredHolder<Item, Item> COPPER_GRAVEL = MaterialRegistry.getGravel("copper");
    public static final DeferredHolder<Item, Item> NETHERITE_SCRAP_GRAVEL = MaterialRegistry.getGravel("netherite_scrap");
    public static final DeferredHolder<Item, Item> DEMONITE_GRAVEL = MaterialRegistry.getGravel("demonite");

    public static final DeferredHolder<Item, Item> IRON_DUST = MaterialRegistry.getDust("iron");
    public static final DeferredHolder<Item, Item> GOLD_DUST = MaterialRegistry.getDust("gold");
    public static final DeferredHolder<Item, Item> COPPER_DUST = MaterialRegistry.getDust("copper");
    public static final DeferredHolder<Item, Item> COAL_DUST = MaterialRegistry.getDust("coal");
    public static final DeferredHolder<Item, Item> NETHERITE_SCRAP_DUST = MaterialRegistry.getDust("netherite_scrap");
    public static final DeferredHolder<Item, Item> HELLFORGED_DUST = MaterialRegistry.getDust("hellforged");
    public static final DeferredHolder<Item, Item> CORRUPTED_DUST = plainItem("corrupted_dust");
    public static final DeferredHolder<Item, Item> CORRUPTED_DUST_TINY = plainItem("corrupted_tiny_dust");
    public static final DeferredHolder<Item, Item> BLOOD_PEARL = plainItem("blood_pearl");

    public static final DeferredHolder<Item, ItemAthanorToolBase> BASIC_CUTTING_FLUID = BASIC_ITEMS.register("basic_cutting_fluid", () -> new ItemAthanorToolBase(64, 1, SpiritusType.RUINA));
    public static final DeferredHolder<Item, ItemAthanorToolBase> INTERMEDIATE_CUTTING_FLUID = BASIC_ITEMS.register("intermediate_cutting_fluid", () -> new ItemAthanorToolBase(512, 1.5, SpiritusType.RUINA));
    public static final DeferredHolder<Item, ItemAthanorToolBase> ADVANCED_CUTTING_FLUID = BASIC_ITEMS.register("advanced_cutting_fluid", () -> new ItemAthanorToolBase(4096, 2, 2, SpiritusType.RUINA));
    public static final DeferredHolder<Item, ItemAthanorToolBase> EXPLOSIVE_POWDER = BASIC_ITEMS.register("explosive_powder", () -> new ItemAthanorToolBase(64, 1, SpiritusType.NIHILUM));
    public static final DeferredHolder<Item, ItemAthanorToolBase> RESONATOR = BASIC_ITEMS.register("resonator", () -> new ItemAthanorToolBase(64, 1, SpiritusType.VINDICTA));
    public static final DeferredHolder<Item, NVGuideBookItem> GUIDE_BOOK = BASIC_ITEMS.register("guide_book", NVGuideBookItem::new);
    public static final DeferredHolder<Item, ItemAthanorToolBase> SANGUINE_REVERTER = BASIC_ITEMS.register("sanguine_reverter", () -> new ItemAthanorToolBase(32, 2, SpiritusType.INVICTUS));
    public static final DeferredHolder<Item, ItemAthanorToolBase> PRIMITIVE_FURNACE_CELL = BASIC_ITEMS.register("furnacecell_primitive", () -> new ItemAthanorToolBase(128, 3));
    public static final DeferredHolder<Item, ItemAthanorToolBase> PRIMITIVE_EXPLOSIVE_CELL = BASIC_ITEMS.register("primitive_explosive_cell", () -> new ItemAthanorToolBase(256, 1.5, SpiritusType.NIHILUM));
    public static final DeferredHolder<Item, ItemAthanorToolBase> PRIMITIVE_HYDRATION_CELL = BASIC_ITEMS.register("primitive_hydration_cell", () -> new ItemAthanorToolBase(128, 1.5));
    public static final DeferredHolder<Item, ItemAthanorToolBase> PRIMITIVE_CRYSTALLINE_RESONATOR = BASIC_ITEMS.register("primitive_crystalline_resonator", () -> new ItemAthanorToolBase(256, 1.5, SpiritusType.VINDICTA));
    public static final DeferredHolder<Item, ItemAthanorToolBase> HELLFORGED_EXPLOSIVE_CELL = BASIC_ITEMS.register("hellforged_explosive_cell", () -> new ItemAthanorToolBase(1024, 2, SpiritusType.NIHILUM));
    public static final DeferredHolder<Item, ItemAthanorToolBase> HELLFORGED_RESONATOR = BASIC_ITEMS.register("hellforged_resonator", () -> new ItemAthanorToolBase(1024, 2, 2, SpiritusType.VINDICTA));

    public static final DeferredHolder<Item, ItemActivationCrystal> ACTIVATION_CRYSTAL_WEAK = BASIC_ITEMS.register("activation_crystal_weak", () -> new ItemActivationCrystal(ItemActivationCrystal.CrystalType.WEAK));
    public static final DeferredHolder<Item, ItemActivationCrystal> ACTIVATION_CRYSTAL_AWAKENED = BASIC_ITEMS.register("activation_crystal_awakened", () -> new ItemActivationCrystal(ItemActivationCrystal.CrystalType.AWAKENED));
    public static final DeferredHolder<Item, ItemActivationCrystal> ACTIVATION_CRYSTAL_CREATIVE = BASIC_ITEMS.register("activation_crystal_creative", () -> new ItemActivationCrystal(ItemActivationCrystal.CrystalType.CREATIVE));

    public static final DeferredHolder<Item, ItemInscriptionTool> INSCRIPTION_TOOL_AIR = BASIC_ITEMS.register("air_scribe_tool", () -> new ItemInscriptionTool(EnumRuneType.AIR));
    public static final DeferredHolder<Item, ItemInscriptionTool> INSCRIPTION_TOOL_FIRE = BASIC_ITEMS.register("fire_scribe_tool", () -> new ItemInscriptionTool(EnumRuneType.FIRE));
    public static final DeferredHolder<Item, ItemInscriptionTool> INSCRIPTION_TOOL_WATER = BASIC_ITEMS.register("water_scribe_tool", () -> new ItemInscriptionTool(EnumRuneType.WATER));
    public static final DeferredHolder<Item, ItemInscriptionTool> INSCRIPTION_TOOL_EARTH = BASIC_ITEMS.register("earth_scribe_tool", () -> new ItemInscriptionTool(EnumRuneType.EARTH));
    public static final DeferredHolder<Item, ItemInscriptionTool> INSCRIPTION_TOOL_DUSK = BASIC_ITEMS.register("dusk_scribe_tool", () -> new ItemInscriptionTool(EnumRuneType.DUSK));

    public static final DeferredHolder<Item, ItemRitualDiviner> RITUAL_DIVINER = BASIC_ITEMS.register("ritual_diviner", () -> new ItemRitualDiviner(0));
    public static final DeferredHolder<Item, ItemRitualDiviner> RITUAL_DIVINER_DUSK = BASIC_ITEMS.register("ritual_diviner_dusk", () -> new ItemRitualDiviner(1));
    public static final DeferredHolder<Item, ItemRitualReader> RITUAL_READER = BASIC_ITEMS.register("ritual_reader", ItemRitualReader::new);
    public static final DeferredHolder<Item, ItemRitualDesigner> RITUAL_DESIGNER = BASIC_ITEMS.register("ritual_designer", () -> new ItemRitualDesigner(new Item.Properties()));

    public static final DeferredHolder<Item, SentientSwordItem> SENTIENT_SWORD = BASIC_ITEMS.register("sentient_sword", SentientSwordItem::new);
    public static final DeferredHolder<Item, SentientAxeItem> SENTIENT_AXE = BASIC_ITEMS.register("sentient_axe", SentientAxeItem::new);
    public static final DeferredHolder<Item, SentientPickaxeItem> SENTIENT_PICKAXE = BASIC_ITEMS.register("sentient_pickaxe", SentientPickaxeItem::new);
    public static final DeferredHolder<Item, SentientShovelItem> SENTIENT_SHOVEL = BASIC_ITEMS.register("sentient_shovel", SentientShovelItem::new);
    public static final DeferredHolder<Item, SentientScytheItem> SENTIENT_SCYTHE = BASIC_ITEMS.register("sentient_scythe", SentientScytheItem::new);
    public static final DeferredHolder<Item, LexVitaeItem> LEX_VITAE = BASIC_ITEMS.register("lex_vitae", LexVitaeItem::new);

    public static final DeferredHolder<Item, SpiritusCrystalItem> RAW_SPIRITUS_CRYSTAL_ITEM = BASIC_ITEMS.register("raw_crystal_shard", () -> new SpiritusCrystalItem(SpiritusType.RAW));
    public static final DeferredHolder<Item, SpiritusCrystalItem> SPIRITUS_RUINA_CRYSTAL_ITEM = BASIC_ITEMS.register("spiritus_ruina_shard", () -> new SpiritusCrystalItem(SpiritusType.RUINA));
    public static final DeferredHolder<Item, SpiritusCrystalItem> SPIRITUS_NIHILUM_CRYSTAL_ITEM = BASIC_ITEMS.register("spiritus_nihilum_shard", () -> new SpiritusCrystalItem(SpiritusType.NIHILUM));
    public static final DeferredHolder<Item, SpiritusCrystalItem> SPIRITUS_VINDICTA_CRYSTAL_ITEM = BASIC_ITEMS.register("spiritus_vindicta_shard", () -> new SpiritusCrystalItem(SpiritusType.VINDICTA));
    public static final DeferredHolder<Item, SpiritusCrystalItem> SPIRITUS_INVICTUS_CRYSTAL_ITEM = BASIC_ITEMS.register("spiritus_invictus_shard", () -> new SpiritusCrystalItem(SpiritusType.INVICTUS));
    public static final DeferredHolder<Item, SpiritusGaugeItem> SPIRITUS_GAUGE = BASIC_ITEMS.register("spiritus_gauge", SpiritusGaugeItem::new);

    public static final DeferredHolder<Item, CrystalCatalystItem> RAW_SPIRITUS_CATALYST = BASIC_ITEMS.register("raw_spiritus_catalyst", () -> new CrystalCatalystItem(SpiritusType.RAW, 200, 10, 25, 400));
    public static final DeferredHolder<Item, CrystalCatalystItem> SPIRITUS_RUINA_CATALYST = BASIC_ITEMS.register("spiritus_ruina_catalyst", () -> new CrystalCatalystItem(SpiritusType.RUINA, 200, 10, 25, 400));
    public static final DeferredHolder<Item, CrystalCatalystItem> SPIRITUS_NIHILUM_CATALYST = BASIC_ITEMS.register("spiritus_nihilum_catalyst", () -> new CrystalCatalystItem(SpiritusType.NIHILUM, 200, 10, 25, 400));
    public static final DeferredHolder<Item, CrystalCatalystItem> SPIRITUS_VINDICTA_CATALYST = BASIC_ITEMS.register("spiritus_vindicta_catalyst", () -> new CrystalCatalystItem(SpiritusType.VINDICTA, 200, 10, 25, 400));
    public static final DeferredHolder<Item, CrystalCatalystItem> SPIRITUS_INVICTUS_CATALYST = BASIC_ITEMS.register("spiritus_invictus_catalyst", () -> new CrystalCatalystItem(SpiritusType.INVICTUS, 200, 10, 25, 400));

    public static final DeferredHolder<Item, ItemNodeRouter> NODE_ROUTER = BASIC_ITEMS.register("node_router", ItemNodeRouter::new);
    public static final DeferredHolder<Item, Item> MASTER_NODE_UPGRADE = plainItem("master_core");
    public static final DeferredHolder<Item, Item> MASTER_NODE_UPGRADE_SPEED = plainItem("master_core_speed");

    public static final DeferredHolder<Item, ItemThrowingDagger> THROWING_DAGGER = BASIC_ITEMS.register("throwing_dagger", () -> new ItemThrowingDagger());
    public static final DeferredHolder<Item, ItemThrowingDagger> THROWING_DAGGER_AMETHYST = BASIC_ITEMS.register("amethyst_throwing_dagger", () -> new ItemThrowingDagger());
    public static final DeferredHolder<Item, ItemThrowingDaggerSyringe> THROWING_DAGGER_SYRINGE = BASIC_ITEMS.register("throwing_dagger_syringe", () -> new ItemThrowingDaggerSyringe());
    public static final DeferredHolder<Item, ItemTippedThrowingDagger> THROWING_DAGGER_TIPPED = BASIC_ITEMS.register("tipped_throwing_dagger", () -> new ItemTippedThrowingDagger());

    public static final DeferredHolder<Item, ItemDungeonKey> SIMPLE_KEY = BASIC_ITEMS.register("simple_key",
            () -> new ItemDungeonKey("Simple", "mini_dungeon", "connective_corridors"));
    public static final DeferredHolder<Item, ItemDungeonKey> MINE_KEY = BASIC_ITEMS.register("mine_key",
            () -> new ItemDungeonKey("Mine", "mine_rooms", "mine_corridors", "mine_deadend"));
    public static final DeferredHolder<Item, ItemDungeonKey> MINE_ENTRANCE_KEY = BASIC_ITEMS.register("mine_entrance_key",
            () -> new ItemDungeonKey("Mine Entrance", "mine_entrances", "mine_key"));
    public static final DeferredHolder<Item, ItemDungeonKey> STANDARD_KEY = BASIC_ITEMS.register("standard_key",
            () -> new ItemDungeonKey("Standard", "standard_rooms", "connective_corridors", "standard_deadend"));
    public static final DeferredHolder<Item, ItemDungeonKey> BOSS_KEY = BASIC_ITEMS.register("boss_key",
            () -> new ItemDungeonKey("Boss", "boss", "special", "treasure"));

    public static final DeferredHolder<Item, ItemDungeonTester> DUNGEON_TESTER = BASIC_ITEMS.register("dungeon_tester",
            ItemDungeonTester::new);

    public static final DeferredHolder<Item, Item> ANIMATED_SPIRITUS = plainItem("animated_spiritus");

    // Demon drop materials
    public static final DeferredHolder<Item, Item> GORE_CLOTTED_FANG = plainItem("gore_clotted_fang");
    public static final DeferredHolder<Item, Item> BLIGHT_MARROW = plainItem("blight_marrow");
    public static final DeferredHolder<Item, Item> VENOMGLAND_SAC = plainItem("venomgland_sac");
    public static final DeferredHolder<Item, Item> HOLLOW_GUT = plainItem("hollow_gut");
    public static final DeferredHolder<Item, Item> ECTOPLASMIC_RESIDUE = plainItem("ectoplasmic_residue");
    public static final DeferredHolder<Item, Item> ANIMUS_MOTE = plainItem("animus_mote");
    public static final DeferredHolder<Item, Item> REVENANT_PLATE = plainItem("revenant_plate");
    public static final DeferredHolder<Item, Item> FROZEN_MARROW_SHARD = plainItem("frozen_marrow_shard");
    public static final DeferredHolder<Item, Item> CINDER_HEART_FRAGMENT = plainItem("cinder_heart_fragment");
    public static final DeferredHolder<Item, Item> PERMAFROST_CORE = plainItem("permafrost_core");
    public static final DeferredHolder<Item, Item> DEMONITE_TRIM_INGOT = plainItem("demonite_trim_ingot");
    public static final DeferredHolder<Item, Item> BLIGHT_WHETSTONE = BASIC_ITEMS.register("blight_whetstone",
            () -> new Item(new Item.Properties().stacksTo(1)));

    // Demon food items
    public static final DeferredHolder<Item, NVFoodItem> TAINTED_FLESH = BASIC_ITEMS.register("tainted_flesh",
            () -> NVFoodItem.builder(4, 0.1f)
                    .effect(() -> MobEffects.WITHER, 100, 0, 0.3f)
                    .build());
    public static final DeferredHolder<Item, NVFoodItem> VITAE_MORSEL = BASIC_ITEMS.register("vitae_morsel",
            () -> NVFoodItem.builder(6, 0.8f).alwaysEdible()
                    .effect(() -> MobEffects.REGENERATION, 200, 0)
                    .build());
    public static final DeferredHolder<Item, NVFoodItem> BOTTLED_SPITE = BASIC_ITEMS.register("bottled_spite",
            () -> NVFoodItem.builder(0, 0f).alwaysEdible().stacksTo(16).drinkable()
                    .effect(() -> MobEffects.DAMAGE_BOOST, 600, 1)
                    .effect(() -> MobEffects.DIG_SLOWDOWN, 300, 0)
                    .build());

    // Sigil of the Damned
    public static final DeferredHolder<Item, ItemSigilDamned> SIGIL_DAMNED = BASIC_ITEMS.register("sigil_damned", ItemSigilDamned::new);

    public static final DeferredHolder<Item, Item> SULFUR = plainItem("sulfur");
    public static final DeferredHolder<Item, Item> SALTPETER = plainItem("saltpeter");
    public static final DeferredHolder<Item, Item> PLANT_OIL = plainItem("plant_oil");
    public static final DeferredHolder<Item, Item> HELLFORGED_INGOT = plainItem("ingot_hellforged");

    public static final DeferredHolder<Item, Item> TABULA_VIAL = BASIC_ITEMS.register("tabula_vial", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<Item, ItemAlchemyFlask> ALCHEMY_FLASK = BASIC_ITEMS.register("alchemy_flask", () -> new ItemAlchemyFlask());
    public static final DeferredHolder<Item, ItemAlchemyFlaskThrowable> ALCHEMY_FLASK_THROWABLE = BASIC_ITEMS.register("alchemy_flask_throwable", () -> new ItemAlchemyFlaskThrowable());
    public static final DeferredHolder<Item, ItemAlchemyFlaskLingering> ALCHEMY_FLASK_LINGERING = BASIC_ITEMS.register("alchemy_flask_lingering", () -> new ItemAlchemyFlaskLingering());

    private static final int COLOR_MELEE = 0xCC3333;      // Red
    private static final int COLOR_SILK = 0x33CCCC;       // Cyan
    private static final int COLOR_FORTUNE = 0xFFD700;    // Gold
    private static final int COLOR_HOLY = 0xFFFFFF;       // White
    private static final int COLOR_KNOWLEDGE = 0x9933FF;  // Purple
    private static final int COLOR_QUICK_DRAW = 0x33FF33; // Green
    private static final int COLOR_LOOTING = 0xFF6600;    // Orange
    private static final int COLOR_BOW_POWER = 0x0066FF;  // Blue
    private static final int COLOR_SPIRITUS = 0x660066;       // Dark purple
    private static final int COLOR_SMELTING = 0xFF3300;   // Fire orange
    private static final int COLOR_VOIDING = 0x333333;    // Dark gray
    private static final int COLOR_BOW_VELOCITY = 0x66CCFF; // Light blue
    private static final int COLOR_REPAIR = 0x00FF66;     // Teal

    private static final int USES_BASE = 256;
    private static final int USES_L = 1024;     // Long duration
    private static final int USES_XL = 4096;    // Extra long duration

    private static DeferredHolder<Item, ItemAnointmentProvider> anointment(String name, String rlPath, int color, int level, int uses) {
        return BASIC_ITEMS.register(name, () -> new ItemAnointmentProvider(NeoVitae.rl(rlPath), color, level, uses));
    }

    public static final DeferredHolder<Item, ItemAnointmentProvider> MELEE_DAMAGE_ANOINTMENT = anointment("melee_anointment", "melee_damage", COLOR_MELEE, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> SILK_TOUCH_ANOINTMENT = anointment("silk_touch_anointment", "silk_touch", COLOR_SILK, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> FORTUNE_ANOINTMENT = anointment("fortune_anointment", "fortune", COLOR_FORTUNE, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> HOLY_WATER_ANOINTMENT = anointment("holy_water_anointment", "holy_water", COLOR_HOLY, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> HIDDEN_KNOWLEDGE_ANOINTMENT = anointment("hidden_knowledge_anointment", "hidden_knowledge", COLOR_KNOWLEDGE, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> QUICK_DRAW_ANOINTMENT = anointment("quick_draw_anointment", "quick_draw", COLOR_QUICK_DRAW, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> LOOTING_ANOINTMENT = anointment("looting_anointment", "looting", COLOR_LOOTING, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> BOW_POWER_ANOINTMENT = anointment("bow_power_anointment", "bow_power", COLOR_BOW_POWER, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> SPIRITUS_DRAIN_ANOINTMENT = anointment("spiritus_drain_anointment", "spiritus_drain", COLOR_SPIRITUS, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> SMELTING_ANOINTMENT = anointment("smelting_anointment", "smelting", COLOR_SMELTING, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> VOIDING_ANOINTMENT = anointment("voiding_anointment", "voiding", COLOR_VOIDING, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> BOW_VELOCITY_ANOINTMENT = anointment("bow_velocity_anointment", "bow_velocity", COLOR_BOW_VELOCITY, 1, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> WEAPON_REPAIR_ANOINTMENT = anointment("weapon_repair_anointment", "repairing", COLOR_REPAIR, 1, USES_BASE);

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

    public static final DeferredHolder<Item, ItemAnointmentProvider> MELEE_DAMAGE_ANOINTMENT_3 = anointment("melee_anointment_3", "melee_damage", COLOR_MELEE, 3, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> FORTUNE_ANOINTMENT_3 = anointment("fortune_anointment_3", "fortune", COLOR_FORTUNE, 3, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> HOLY_WATER_ANOINTMENT_3 = anointment("holy_water_anointment_3", "holy_water", COLOR_HOLY, 3, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> HIDDEN_KNOWLEDGE_ANOINTMENT_3 = anointment("hidden_knowledge_anointment_3", "hidden_knowledge", COLOR_KNOWLEDGE, 3, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> QUICK_DRAW_ANOINTMENT_3 = anointment("quick_draw_anointment_3", "quick_draw", COLOR_QUICK_DRAW, 3, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> LOOTING_ANOINTMENT_3 = anointment("looting_anointment_3", "looting", COLOR_LOOTING, 3, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> BOW_POWER_ANOINTMENT_3 = anointment("bow_power_anointment_3", "bow_power", COLOR_BOW_POWER, 3, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> BOW_VELOCITY_ANOINTMENT_3 = anointment("bow_velocity_anointment_3", "bow_velocity", COLOR_BOW_VELOCITY, 3, USES_BASE);
    public static final DeferredHolder<Item, ItemAnointmentProvider> WEAPON_REPAIR_ANOINTMENT_3 = anointment("weapon_repair_anointment_3", "repairing", COLOR_REPAIR, 3, USES_BASE);

    public static final DeferredHolder<Item, Item> FRAME_PARTS = plainItem("component_frame_parts");


    public static final DeferredHolder<Item, Item> BLOOD_SWEAT_AND_TEARS = BASIC_ITEMS.register("blood_sweat_and_tears", () -> new Item(
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(
                            Registries.JUKEBOX_SONG,
                            NeoVitae.rl("blood_sweat_and_tears")
                    ))
    ));

    public static final DeferredHolder<Item, Item> SIMPLE_CATALYST = plainItem("simple_catalyst");
    public static final DeferredHolder<Item, Item> STRENGTHENED_CATALYST = plainItem("strengthened_catalyst");
    public static final DeferredHolder<Item, Item> CYCLING_CATALYST = plainItem("cycling_catalyst");
    public static final DeferredHolder<Item, Item> COMBINATIONAL_CATALYST = plainItem("combinational_catalyst");
    public static final DeferredHolder<Item, Item> MUNDANE_LENGTHENING_CATALYST = plainItem("mundane_lengthening_catalyst");
    public static final DeferredHolder<Item, Item> MUNDANE_POWER_CATALYST = plainItem("mundane_power_catalyst");
    public static final DeferredHolder<Item, Item> AVERAGE_LENGTHENING_CATALYST = plainItem("average_lengthening_catalyst");
    public static final DeferredHolder<Item, Item> AVERAGE_POWER_CATALYST = plainItem("average_power_catalyst");

    public static final DeferredHolder<Item, Item> WEAK_FILLING_AGENT = plainItem("weak_filling_agent");
    public static final DeferredHolder<Item, Item> STANDARD_FILLING_AGENT = plainItem("standard_filling_agent");

    public static final DeferredHolder<Item, Item> HELLFORGED_PARTS = plainItem("hellforged_parts");

    public static final DeferredHolder<Item, ItemBloodProvider> TABULA_AMPOULE = BASIC_ITEMS.register("tabula_ampoule", () -> new ItemBloodProvider("slate", 500));

    public static final DeferredHolder<Item, SpawnEggItem> DAEMONIUM_IGNIS_SPAWN_EGG = BASIC_ITEMS.register("daemonium_ignis_spawn_egg",
            () -> new DeferredSpawnEggItem(
                    NVEntities.DAEMONIUM_IGNIS, 0x2D0A0A, 0xFF4500,
                    new Item.Properties()));

    public static final DeferredHolder<Item, SpawnEggItem> DAEMONIUM_CRUORIS_SPAWN_EGG = BASIC_ITEMS.register("daemonium_cruoris_spawn_egg",
            () -> new DeferredSpawnEggItem(
                    NVEntities.DAEMONIUM_CRUORIS, 0x3A1010, 0x8B0000,
                    new Item.Properties()));

    public static final DeferredHolder<Item, SpawnEggItem> DAEMONIUM_CORRODIS_SPAWN_EGG = BASIC_ITEMS.register("daemonium_corrodis_spawn_egg",
            () -> new DeferredSpawnEggItem(
                    NVEntities.DAEMONIUM_CORRODIS, 0x2A1A2A, 0x6B3FA0,
                    new Item.Properties()));

    public static final DeferredHolder<Item, SpawnEggItem> DAEMONIUM_GLACIARIS_SPAWN_EGG = BASIC_ITEMS.register("daemonium_glaciaris_spawn_egg",
            () -> new DeferredSpawnEggItem(
                    NVEntities.DAEMONIUM_GLACIARIS, 0xA0D4E8, 0x4FC3F7,
                    new Item.Properties()));

    public static final DeferredHolder<Item, SpawnEggItem> DAEMONIUM_PESTIS_SPAWN_EGG = BASIC_ITEMS.register("daemonium_pestis_spawn_egg",
            () -> new DeferredSpawnEggItem(
                    NVEntities.DAEMONIUM_PESTIS, 0x0D0F1E, 0x2A2656,
                    new Item.Properties()));

    public static final DeferredHolder<Item, SpawnEggItem> DAEMONIUM_VORAXIS_SPAWN_EGG = BASIC_ITEMS.register("daemonium_voraxis_spawn_egg",
            () -> new DeferredSpawnEggItem(
                    NVEntities.DAEMONIUM_VORAXIS, 0x0D0D14, 0x4A1A5C,
                    new Item.Properties()));

    public static final DeferredHolder<Item, SpawnEggItem> DAEMONIUM_DOLORIS_SPAWN_EGG = BASIC_ITEMS.register("daemonium_doloris_spawn_egg",
            () -> new DeferredSpawnEggItem(
                    NVEntities.DAEMONIUM_DOLORIS, 0x1A1A1A, 0x4A6B3A,
                    new Item.Properties()));

    public static final DeferredHolder<Item, SpawnEggItem> DAEMONIUM_FERVIDIS_SPAWN_EGG = BASIC_ITEMS.register("daemonium_fervidis_spawn_egg",
            () -> new DeferredSpawnEggItem(
                    NVEntities.DAEMONIUM_FERVIDIS, 0x3A2A1A, 0x6B4423,
                    new Item.Properties()));

    public static final DeferredHolder<Item, SpawnEggItem> DAEMONIUM_ANIMARIS_SPAWN_EGG = BASIC_ITEMS.register("daemonium_animaris_spawn_egg",
            () -> new DeferredSpawnEggItem(
                    NVEntities.DAEMONIUM_ANIMARIS, 0x2A2A2A, 0x8B1A1A,
                    new Item.Properties()));

    public static final DeferredHolder<Item, SpawnEggItem> DAEMONIUM_RANCORIS_SPAWN_EGG = BASIC_ITEMS.register("daemonium_rancoris_spawn_egg",
            () -> new DeferredSpawnEggItem(
                    NVEntities.DAEMONIUM_RANCORIS, 0x1A1A2E, 0x7B9EBF,
                    new Item.Properties()));

    public static final DeferredHolder<Item, SpawnEggItem> SLIME_VITAE_SPAWN_EGG = BASIC_ITEMS.register("slime_vitae_spawn_egg",
            () -> new DeferredSpawnEggItem(
                    NVEntities.SLIME_VITAE, 0x8B0000, 0xFF2222,
                    new Item.Properties()));

    // Array effect dummy items for JEI visibility
    public static final DeferredRegister<Item> ARRAY_ITEMS = DeferredRegister.createItems(NeoVitae.MODID);
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_BOUNCE = ARRAY_ITEMS.register("array_bounce", () -> new ArrayEffectItem(AlchemyArrayEffectType.BOUNCE));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_SPIKE = ARRAY_ITEMS.register("array_spike", () -> new ArrayEffectItem(AlchemyArrayEffectType.SPIKE));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_UPDRAFT = ARRAY_ITEMS.register("array_updraft", () -> new ArrayEffectItem(AlchemyArrayEffectType.UPDRAFT));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_MOVEMENT = ARRAY_ITEMS.register("array_movement", () -> new ArrayEffectItem(AlchemyArrayEffectType.MOVEMENT));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_DAY = ARRAY_ITEMS.register("array_day", () -> new ArrayEffectItem(AlchemyArrayEffectType.DAY));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_NIGHT = ARRAY_ITEMS.register("array_night", () -> new ArrayEffectItem(AlchemyArrayEffectType.NIGHT));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_ELEVATOR = ARRAY_ITEMS.register("array_elevator", () -> new ArrayEffectItem(AlchemyArrayEffectType.ELEVATOR));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_REPULSION = ARRAY_ITEMS.register("array_repulsion", () -> new ArrayEffectItem(AlchemyArrayEffectType.REPULSION));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_COLLECTION = ARRAY_ITEMS.register("array_collection", () -> new ArrayEffectItem(AlchemyArrayEffectType.COLLECTION));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_LIGHT = ARRAY_ITEMS.register("array_light", () -> new ArrayEffectItem(AlchemyArrayEffectType.LIGHT));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_FURNACE = ARRAY_ITEMS.register("array_furnace", () -> new ArrayEffectItem(AlchemyArrayEffectType.FURNACE));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_RAIN = ARRAY_ITEMS.register("array_rain", () -> new ArrayEffectItem(AlchemyArrayEffectType.RAIN));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_GROWTH = ARRAY_ITEMS.register("array_growth", () -> new ArrayEffectItem(AlchemyArrayEffectType.GROWTH));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_FREEZE = ARRAY_ITEMS.register("array_freeze", () -> new ArrayEffectItem(AlchemyArrayEffectType.FREEZE));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_SIGNAL = ARRAY_ITEMS.register("array_signal", () -> new ArrayEffectItem(AlchemyArrayEffectType.SIGNAL));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_TRIGGER = ARRAY_ITEMS.register("array_trigger", () -> new ArrayEffectItem(AlchemyArrayEffectType.TRIGGER));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_SPIRIT_SIPHON = ARRAY_ITEMS.register("array_spirit_siphon", () -> new ArrayEffectItem(AlchemyArrayEffectType.SPIRIT_SIPHON));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_DEFLECTION = ARRAY_ITEMS.register("array_deflection", () -> new ArrayEffectItem(AlchemyArrayEffectType.DEFLECTION));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_ENDLESS_FOUNTAIN = ARRAY_ITEMS.register("array_endless_fountain", () -> new ArrayEffectItem(AlchemyArrayEffectType.ENDLESS_FOUNTAIN));
    public static final DeferredHolder<Item, ArrayEffectItem> ARRAY_UNDERTOW = ARRAY_ITEMS.register("array_undertow", () -> new ArrayEffectItem(AlchemyArrayEffectType.UNDERTOW));

    public static void register(IEventBus modBus) {
        BASIC_ITEMS.register(modBus);
        ITEMS.register(modBus);
        SPIRITUS_ITEMS.register(modBus);
        TAB_REQ.register(modBus);
        ARRAY_ITEMS.register(modBus);
    }
}
