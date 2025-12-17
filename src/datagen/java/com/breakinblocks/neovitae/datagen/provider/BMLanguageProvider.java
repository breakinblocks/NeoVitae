package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonVariant;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.fluid.BMFluids;
import com.breakinblocks.neovitae.common.item.BMItems;
import com.breakinblocks.neovitae.datagen.content.LivingUpgrades;
import com.breakinblocks.neovitae.util.helper.BlockWithItemHolder;

public class BMLanguageProvider extends LanguageProvider {

    public BMLanguageProvider(PackOutput output) {
        super(output, NeoVitae.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(BMFluids.LIFE_ESSENCE_TYPE.get().getDescriptionId(), "Life Essence");
        add(BMFluids.LIFE_ESSENCE_BUCKET.get(), "Bucket of Life");
        add(BMFluids.LIFE_ESSENCE_BLOCK.get(), "Life Essence");

        add(BMItems.ORB_WEAK.get(), "Weak Blood Orb");
        add(BMItems.ORB_APPRENTICE.get(), "Apprentice Blood Orb");
        add(BMItems.ORB_MAGICIAN.get(), "Magician Blood Orb");
        add(BMItems.ORB_MASTER.get(), "Master Blood Orb");
        add(BMItems.ORB_ARCHMAGE.get(), "Archmage Blood Orb");
        add(BMItems.ORB_TRANSCENDENT.get(), "Transcendent Blood Orb");

        addTooltip("current_owner", "Current Owner: %s");
        addTooltip("no_owner", "Not bound yet");

        // Death messages
        add("death.attack.spikes", "%1$s was impaled by spikes");
        add("death.attack.spikes.player", "%1$s was impaled by spikes whilst fighting %2$s");

        add(BMBlocks.BLOOD_ALTAR, "Blood Altar");
        add(BMItems.SACRIFICIAL_DAGGER.get(), "Sacrificial Dagger");

        add(BMBlocks.RUNE_BLANK, "Blank Rune");

        add(BMBlocks.RUNE_SACRIFICE, "Rune of Sacrifice");
        add(BMBlocks.RUNE_SELF_SACRIFICE, "Rune of Self Sacrifice");
        add(BMBlocks.RUNE_SPEED, "Speed Rune");
        add(BMBlocks.RUNE_ACCELERATION, "Acceleration Rune");
        add(BMBlocks.RUNE_DISLOCATION, "Displacement Rune");
        add(BMBlocks.RUNE_CAPACITY, "Capacity Rune");
        add(BMBlocks.RUNE_CAPACITY_AUGMENTED, "Augmented Capacity Rune");
        add(BMBlocks.RUNE_CHARGING, "Charging Rune");
        add(BMBlocks.RUNE_ORB, "Rune of the Orb");
        add(BMBlocks.RUNE_EFFICIENCY, "Rune of Efficiency");

        add(BMBlocks.RUNE_2_SACRIFICE, "Reinforced Rune of Sacrifice");
        add(BMBlocks.RUNE_2_SELF_SACRIFICE, "Reinforced Rune of Self Sacrifice");
        add(BMBlocks.RUNE_2_SPEED, "Reinforced Speed Rune");
        add(BMBlocks.RUNE_2_ACCELERATION, "Reinforced Acceleration Rune");
        add(BMBlocks.RUNE_2_DISLOCATION, "Reinforced Displacement Rune");
        add(BMBlocks.RUNE_2_CAPACITY, "Reinforced Capacity Rune");
        add(BMBlocks.RUNE_2_CAPACITY_AUGMENTED, "Reinforced Augmented Capacity Rune");
        add(BMBlocks.RUNE_2_CHARGING, "Reinforced Charging Rune");
        add(BMBlocks.RUNE_2_ORB, "Reinforced Rune of the Orb");
        add(BMBlocks.RUNE_2_EFFICIENCY, "Reinforced Rune of Efficiency");

        add(BMBlocks.BLOODSTONE, "Polished Bloodstone");
        add(BMBlocks.BLOODSTONE_BRICK, "Bloodstone Brick");

        add(BMBlocks.HELLFORGED_BLOCK, "Hellforged Block");

        add(BMBlocks.CRYSTAL_CLUSTER, "Crystal Cluster");
        add(BMBlocks.CRYSTAL_CLUSTER_BRICK, "Crystal Cluster Brick");

        // Demon Will Blocks
        add(BMBlocks.DEMON_CRUCIBLE, "Demon Crucible");
        add(BMBlocks.DEMON_CRYSTALLIZER, "Demon Crystallizer");
        add(BMBlocks.DEMON_PYLON, "Demon Pylon");

        // Demon Crystal Blocks
        add(BMBlocks.RAW_DEMON_CRYSTAL, "Raw Crystal Cluster");
        add(BMBlocks.CORROSIVE_DEMON_CRYSTAL, "Corrosive Crystal Cluster");
        add(BMBlocks.DESTRUCTIVE_DEMON_CRYSTAL, "Destructive Crystal Cluster");
        add(BMBlocks.VENGEFUL_DEMON_CRYSTAL, "Vengeful Crystal Cluster");
        add(BMBlocks.STEADFAST_DEMON_CRYSTAL, "Steadfast Crystal Cluster");

        // Routing Node Blocks
        add(BMBlocks.ROUTING_NODE, "Item Routing Node");
        add(BMBlocks.INPUT_ROUTING_NODE, "Input Routing Node");
        add(BMBlocks.OUTPUT_ROUTING_NODE, "Output Routing Node");
        add(BMBlocks.MASTER_ROUTING_NODE, "Master Routing Node");

        // Tau Blocks
        add(BMBlocks.WEAK_TAU, "Weak Tau");
        add(BMBlocks.STRONG_TAU, "Strong Tau");

        // Ritual Stones
        add(BMBlocks.BLANK_RITUAL_STONE, "Ritual Stone");
        add(BMBlocks.AIR_RITUAL_STONE, "Air Ritual Stone");
        add(BMBlocks.WATER_RITUAL_STONE, "Water Ritual Stone");
        add(BMBlocks.FIRE_RITUAL_STONE, "Fire Ritual Stone");
        add(BMBlocks.EARTH_RITUAL_STONE, "Earth Ritual Stone");
        add(BMBlocks.DUSK_RITUAL_STONE, "Dusk Ritual Stone");
        add(BMBlocks.DAWN_RITUAL_STONE, "Dawn Ritual Stone");
        add(BMBlocks.MASTER_RITUAL_STONE, "Master Ritual Stone");
        add(BMBlocks.INVERTED_MASTER_RITUAL_STONE, "Inverted Master Ritual Stone");
        add(BMBlocks.IMPERFECT_RITUAL_STONE, "Imperfect Ritual Stone");
        addTooltip("imperfectRitualStone.desc", "Simple ritual stone for quick effects");
        addTooltip("imperfectRitualStone.hint", "Place a block above and right-click to activate");
        addTooltip("decoration.safe", "Safe for Decoration");
        addTooltip("masterRitualStone.inverted", "Inverted - requires redstone signal to operate");

        addTooltip("save_for_decoration", "Save for Decoration");

        add(BMFluids.DOUBT_TYPE.get().getDescriptionId(), "Liquid Doubt");
        add(BMFluids.DOUBT_BUCKET.get(), "Doubt Bucket");
        add(BMFluids.DOUBT_BLOCK.get(), "Liquid Doubt");

        add(BMBlocks.ARC_BLOCK, "Alchemical Reaction Chamber");

        add(BMBlocks.BLOOD_TANK, "Blood Tank");
        addTooltip("container_tier_missing", "No Tier found!");
        addTooltip("container_tier", "Current Tier: %s");
        addTooltip("fluid_content_empty", "Empty");
        addTooltip("fluid_content", "Contains: %smB of %s");

        add(BMBlocks.HELLFIRE_FORGE, "Hellfire Forge");
        add(BMItems.RAW_WILL.get(), "Raw Will");

        // Monster Souls (dropped from mobs with sentient weapons)
        add(BMItems.MONSTER_SOUL_RAW.get(), "Monster Soul");
        add(BMItems.MONSTER_SOUL_CORROSIVE.get(), "Corrosive Monster Soul");
        add(BMItems.MONSTER_SOUL_DESTRUCTIVE.get(), "Destructive Monster Soul");
        add(BMItems.MONSTER_SOUL_VENGEFUL.get(), "Vengeful Monster Soul");
        add(BMItems.MONSTER_SOUL_STEADFAST.get(), "Steadfast Monster Soul");

        add(BMItems.SOUL_GEM_PETTY.get(), "Petty Tartaric Gem");
        add(BMItems.SOUL_GEM_LESSER.get(), "Lesser Tartaric Gem");
        add(BMItems.SOUL_GEM_COMMON.get(), "Common Tartaric Gem");
        add(BMItems.SOUL_GEM_GREATER.get(), "Greater Tartaric Gem");
        add(BMItems.SOUL_GEM_GRAND.get(), "Grand Tartaric Gem");
        addGemDesc(BMItems.SOUL_GEM_PETTY, "a little");
        addGemDesc(BMItems.SOUL_GEM_LESSER, "some");
        addGemDesc(BMItems.SOUL_GEM_COMMON, "more");
        addGemDesc(BMItems.SOUL_GEM_GREATER, "a greater amount of");
        addGemDesc(BMItems.SOUL_GEM_GRAND, "a large amount of");

        // Slates
        add(BMItems.SLATE_BLANK.get(), "Blank Slate");
        add(BMItems.SLATE_REINFORCED.get(), "Reinforced Slate");
        add(BMItems.SLATE_IMBUED.get(), "Imbued Slate");
        add(BMItems.SLATE_DEMONIC.get(), "Demonic Slate");
        add(BMItems.SLATE_ETHEREAL.get(), "Ethereal Slate");

        // Sigils
        add(BMItems.SIGIL_DIVINATION.get(), "Divination Sigil");
        add(BMItems.SIGIL_SEER.get(), "Seer's Sigil");
        add(BMItems.SIGIL_WATER.get(), "Water Sigil");
        add(BMItems.SIGIL_LAVA.get(), "Lava Sigil");
        add(BMItems.SIGIL_VOID.get(), "Void Sigil");
        add(BMItems.SIGIL_GREEN_GROVE.get(), "Sigil of the Green Grove");
        add(BMItems.SIGIL_AIR.get(), "Air Sigil");
        add(BMItems.SIGIL_BLOOD_LIGHT.get(), "Sigil of the Blood Lamp");
        add(BMItems.SIGIL_FAST_MINER.get(), "Sigil of the Fast Miner");
        add(BMItems.SIGIL_MAGNETISM.get(), "Sigil of Magnetism");
        add(BMItems.SIGIL_FROST.get(), "Sigil of the Frost Walker");
        add(BMItems.SIGIL_SUPPRESSION.get(), "Sigil of Suppression");
        add(BMItems.SIGIL_HOLDING.get(), "Sigil of Holding");
        add(BMItems.SIGIL_TELEPOSITION.get(), "Sigil of Teleposition");
        add(BMItems.SIGIL_PHANTOM_BRIDGE.get(), "Sigil of the Phantom Bridge");

        // Alchemy & Misc
        add(BMItems.ARCANE_ASHES.get(), "Arcane Ashes");
        addTooltip("arcaneAshes", "Draws an alchemy circle when placed");

        // Reagents
        add(BMItems.REAGENT_WATER.get(), "Reagent Water");
        add(BMItems.REAGENT_LAVA.get(), "Reagent Lava");
        add(BMItems.REAGENT_VOID.get(), "Reagent Void");
        add(BMItems.REAGENT_GROWTH.get(), "Reagent Growth");
        add(BMItems.REAGENT_FAST_MINER.get(), "Reagent Fast Miner");
        add(BMItems.REAGENT_MAGNETISM.get(), "Reagent Magnetism");
        add(BMItems.REAGENT_AIR.get(), "Reagent Air");
        add(BMItems.REAGENT_BLOOD_LIGHT.get(), "Reagent Blood Light");
        add(BMItems.REAGENT_SIGHT.get(), "Reagent Sight");
        add(BMItems.REAGENT_BINDING.get(), "Reagent Binding");
        add(BMItems.REAGENT_HOLDING.get(), "Reagent Holding");
        add(BMItems.REAGENT_SUPPRESSION.get(), "Reagent Suppression");
        add(BMItems.REAGENT_TELEPOSITION.get(), "Reagent Teleposition");
        add(BMItems.REAGENT_FROST.get(), "Reagent Frost");
        add(BMItems.REAGENT_PHANTOM_BRIDGE.get(), "Reagent Phantom Bridge");

        // Alchemy Array and Table Blocks
        add(BMBlocks.ALCHEMY_ARRAY.get(), "Alchemy Array");
        add(BMBlocks.ALCHEMY_TABLE, "Alchemy Table");

        // Blood Light, Spectral Blocks, and Phantom Bridge (placed by sigils, no block item)
        add(BMBlocks.BLOOD_LIGHT.get().getDescriptionId(), "Blood Light");
        add(BMBlocks.SPECTRAL_BLOCK.get().getDescriptionId(), "Spectral Block");
        add(BMBlocks.PHANTOM_BRIDGE_BLOCK.get().getDescriptionId(), "Phantom Bridge");

        // Incense Altar
        add(BMBlocks.INCENSE_ALTAR, "Incense Altar");

        add(BMItems.SOUL_SNARE.get(), "Soul Snare");
        addTooltip("soulSnare.desc", "Throw at weakened mobs to extract their soul");
        add(BMItems.WEAK_BLOOD_SHARD.get(), "Weak Blood Shard");
        add(BMItems.DAGGER_OF_SACRIFICE.get(), "Dagger of Sacrifice");
        add(BMItems.LAVA_CRYSTAL.get(), "Lava Crystal");
        addTooltip("lavaCrystal.desc", "Place fire, bindable furnace fuel");
        add("chat.neovitae.notEnoughLP", "Not enough Life Points!");

        // Crystal Items (bloodmagic crystals)
        add(BMItems.RAW_CRYSTAL.get(), "Demon Will Crystal");
        add(BMItems.CORROSIVE_CRYSTAL.get(), "Corrosive Will Crystal");
        add(BMItems.DESTRUCTIVE_CRYSTAL.get(), "Destructive Will Crystal");
        add(BMItems.VENGEFUL_CRYSTAL.get(), "Vengeful Will Crystal");
        add(BMItems.STEADFAST_CRYSTAL.get(), "Steadfast Will Crystal");
        add(BMItems.DEMON_WILL_GAUGE.get(), "Demon Will Aura Gauge");
        addTooltip("demon_will_gauge", "Shows the current demon will level in the area");

        // Crystal Catalysts
        add(BMItems.RAW_CRYSTAL_CATALYST.get(), "Raw Crystal Catalyst");
        add(BMItems.CORROSIVE_CRYSTAL_CATALYST.get(), "Corrosive Crystal Catalyst");
        add(BMItems.DESTRUCTIVE_CRYSTAL_CATALYST.get(), "Destructive Crystal Catalyst");
        add(BMItems.VENGEFUL_CRYSTAL_CATALYST.get(), "Vengeful Crystal Catalyst");
        add(BMItems.STEADFAST_CRYSTAL_CATALYST.get(), "Steadfast Crystal Catalyst");

        // Will Type Names
        add("will.neovitae.default", "Raw");
        add("will.neovitae.corrosive", "Corrosive");
        add("will.neovitae.destructive", "Destructive");
        add("will.neovitae.vengeful", "Vengeful");
        add("will.neovitae.steadfast", "Steadfast");

        // Sentient Tools
        add(BMItems.SENTIENT_SWORD.get(), "Sentient Sword");
        add(BMItems.SENTIENT_AXE.get(), "Sentient Axe");
        add(BMItems.SENTIENT_PICKAXE.get(), "Sentient Pickaxe");
        add(BMItems.SENTIENT_SHOVEL.get(), "Sentient Shovel");
        add(BMItems.SENTIENT_SCYTHE.get(), "Sentient Scythe");
        addTooltip("sentientSword.desc", "Empowered by demon will in your inventory");
        addTooltip("sentientAxe.desc", "Empowered by demon will in your inventory");
        addTooltip("sentientPickaxe.desc", "Empowered by demon will in your inventory");
        addTooltip("sentientShovel.desc", "Empowered by demon will in your inventory");
        addTooltip("sentientScythe.desc", "Area damage empowered by demon will");

        // Routing Items
        add(BMItems.NODE_ROUTER.get(), "Node Router");
        add(BMItems.MASTER_NODE_UPGRADE.get(), "Master Routing Node Core");
        add(BMItems.MASTER_NODE_UPGRADE_SPEED.get(), "Speed Core");
        addTooltip("noderouter.coords", "Stored Position: %d, %d, %d");
        add("chat.neovitae.routing.remove", "Stored position cleared.");
        add("chat.neovitae.routing.set", "Position stored.");
        add("chat.neovitae.routing.distance", "Nodes are too far apart! Maximum distance is 16 blocks.");
        add("chat.neovitae.routing.same", "Cannot link a node to itself!");
        add("chat.neovitae.routing.link.master", "Node linked to Master Routing Node.");
        add("chat.neovitae.routing.link", "Nodes linked together.");

        // Throwing Daggers
        add(BMItems.THROWING_DAGGER.get(), "Throwing Dagger");
        add(BMItems.THROWING_DAGGER_AMETHYST.get(), "Amethyst Throwing Dagger");
        add(BMItems.THROWING_DAGGER_SYRINGE.get(), "Syringe Throwing Dagger");
        add(BMItems.THROWING_DAGGER_TIPPED.get(), "Tipped Throwing Dagger");
        add("tooltip.neovitae.throwing_dagger.desc", "Throw at enemies for damage");
        add("entity.neovitae.throwing_dagger", "Throwing Dagger");
        add("entity.neovitae.throwing_dagger_syringe", "Syringe Throwing Dagger");

        // Misc WIP Items
        add(BMItems.DOUBT_SEED.get(), "Seeds of Doubt");

        // Simple Recipe Ingredients
        add(BMItems.SULFUR.get(), "Sulfur");
        add(BMItems.SALTPETER.get(), "Saltpeter");
        add(BMItems.PLANT_OIL.get(), "Plant Oil");
        add(BMItems.HELLFORGED_INGOT.get(), "Hellforged Ingot");

        // Explosive Charges
        add(BMBlocks.SHAPED_CHARGE, "Shaped Charge");
        add(BMBlocks.DEFORESTER_CHARGE, "Deforester Charge");
        add(BMBlocks.VEINMINE_CHARGE, "Veinmine Charge");
        add(BMBlocks.FUNGAL_CHARGE, "Fungal Charge");
        add(BMBlocks.AUG_SHAPED_CHARGE, "Augmented Shaped Charge");
        add(BMBlocks.DEFORESTER_CHARGE_2, "Reinforced Deforester Charge");
        add(BMBlocks.VEINMINE_CHARGE_2, "Reinforced Veinmine Charge");
        add(BMBlocks.FUNGAL_CHARGE_2, "Reinforced Fungal Charge");
        add(BMBlocks.SHAPED_CHARGE_DEEP, "Deep Shaped Charge");

        // Mimic Block
        add(BMBlocks.MIMIC, "Mimic");
        add(BMBlocks.ETHEREAL_MIMIC, "Ethereal Mimic");
        add(BMBlocks.INVERSION_PILLAR, "Inversion Pillar");
        add(BMBlocks.INVERSION_PILLAR_CAP, "Inversion Pillar Cap");

        // Dungeon Control Blocks
        add(BMBlocks.DUNGEON_CONTROLLER.block().get(), "Dungeon Controller");
        add(BMBlocks.DUNGEON_SEAL.block().get(), "Dungeon Seal");
        add("chat.neovitae.mimic.potionSpawnRadius.down", "Potion Spawn Radius: %d");
        add("chat.neovitae.mimic.potionSpawnRadius.up", "Potion Spawn Radius: %d");
        add("chat.neovitae.mimic.detectRadius.down", "Detection Radius: %d");
        add("chat.neovitae.mimic.detectRadius.up", "Detection Radius: %d");
        add("chat.neovitae.mimic.potionInterval.down", "Potion Interval: %d ticks");
        add("chat.neovitae.mimic.potionInterval.up", "Potion Interval: %d ticks");

        // Alchemy Flask Items
        add(BMItems.SLATE_VIAL.get(), "Slate Vial");
        add(BMItems.ALCHEMY_FLASK.get(), "Alchemy Flask");
        add(BMItems.ALCHEMY_FLASK_THROWABLE.get(), "Throwable Alchemy Flask");
        add(BMItems.ALCHEMY_FLASK_LINGERING.get(), "Lingering Alchemy Flask");

        // Blood Provider Items
        add(BMItems.SLATE_AMPOULE.get(), "Slate Ampoule");
        add("tooltip.neovitae.blood_provider.slate.desc", "A simple ampoule containing 500LP.");

        // Anointment Items - Base tier (using 1.20.1 thematic names)
        add(BMItems.MELEE_DAMAGE_ANOINTMENT.get(), "Honing Oil");
        add(BMItems.SILK_TOUCH_ANOINTMENT.get(), "Soft Grip");
        add(BMItems.FORTUNE_ANOINTMENT.get(), "Fortuna Extract");
        add(BMItems.HOLY_WATER_ANOINTMENT.get(), "Holy Water");
        add(BMItems.HIDDEN_KNOWLEDGE_ANOINTMENT.get(), "Miner's Secrets");
        add(BMItems.QUICK_DRAW_ANOINTMENT.get(), "Dexterity Alkahest");
        add(BMItems.LOOTING_ANOINTMENT.get(), "Plunderer's Glint");
        add(BMItems.BOW_POWER_ANOINTMENT.get(), "Iron Tip");
        add(BMItems.WILL_POWER_ANOINTMENT.get(), "Will Empowerment");
        add(BMItems.SMELTING_ANOINTMENT.get(), "Slow-burning Oil");
        add(BMItems.VOIDING_ANOINTMENT.get(), "Voiding Essence");
        add(BMItems.BOW_VELOCITY_ANOINTMENT.get(), "Archer's Polish");
        add(BMItems.WEAPON_REPAIR_ANOINTMENT.get(), "Mending Balm");

        // Anointment Items - L variants (extended duration)
        add(BMItems.MELEE_DAMAGE_ANOINTMENT_L.get(), "Honing Oil L");
        add(BMItems.SILK_TOUCH_ANOINTMENT_L.get(), "Soft Grip L");
        add(BMItems.FORTUNE_ANOINTMENT_L.get(), "Fortuna Extract L");
        add(BMItems.HOLY_WATER_ANOINTMENT_L.get(), "Holy Water L");
        add(BMItems.HIDDEN_KNOWLEDGE_ANOINTMENT_L.get(), "Miner's Secrets L");
        add(BMItems.QUICK_DRAW_ANOINTMENT_L.get(), "Dexterity Alkahest L");
        add(BMItems.LOOTING_ANOINTMENT_L.get(), "Plunderer's Glint L");
        add(BMItems.BOW_POWER_ANOINTMENT_L.get(), "Iron Tip L");
        add(BMItems.SMELTING_ANOINTMENT_L.get(), "Slow-burning Oil L");
        add(BMItems.VOIDING_ANOINTMENT_L.get(), "Voiding Essence L");
        add(BMItems.BOW_VELOCITY_ANOINTMENT_L.get(), "Archer's Polish L");
        add(BMItems.WEAPON_REPAIR_ANOINTMENT_L.get(), "Mending Balm L");

        // Anointment Items - 2 variants (level 2)
        add(BMItems.MELEE_DAMAGE_ANOINTMENT_2.get(), "Honing Oil II");
        add(BMItems.FORTUNE_ANOINTMENT_2.get(), "Fortuna Extract II");
        add(BMItems.HOLY_WATER_ANOINTMENT_2.get(), "Holy Water II");
        add(BMItems.HIDDEN_KNOWLEDGE_ANOINTMENT_2.get(), "Miner's Secrets II");
        add(BMItems.QUICK_DRAW_ANOINTMENT_2.get(), "Dexterity Alkahest II");
        add(BMItems.LOOTING_ANOINTMENT_2.get(), "Plunderer's Glint II");
        add(BMItems.BOW_POWER_ANOINTMENT_2.get(), "Iron Tip II");
        add(BMItems.BOW_POWER_ANOINTMENT_STRONG.get(), "Iron Tip II");
        add(BMItems.BOW_VELOCITY_ANOINTMENT_2.get(), "Archer's Polish II");
        add(BMItems.WEAPON_REPAIR_ANOINTMENT_2.get(), "Mending Balm II");

        // Anointment Items - XL variants (extra long duration)
        add(BMItems.MELEE_DAMAGE_ANOINTMENT_XL.get(), "Honing Oil XL");
        add(BMItems.SILK_TOUCH_ANOINTMENT_XL.get(), "Soft Grip XL");
        add(BMItems.FORTUNE_ANOINTMENT_XL.get(), "Fortuna Extract XL");
        add(BMItems.HOLY_WATER_ANOINTMENT_XL.get(), "Holy Water XL");
        add(BMItems.HIDDEN_KNOWLEDGE_ANOINTMENT_XL.get(), "Miner's Secrets XL");
        add(BMItems.QUICK_DRAW_ANOINTMENT_XL.get(), "Dexterity Alkahest XL");
        add(BMItems.LOOTING_ANOINTMENT_XL.get(), "Plunderer's Glint XL");
        add(BMItems.BOW_POWER_ANOINTMENT_XL.get(), "Iron Tip XL");
        add(BMItems.SMELTING_ANOINTMENT_XL.get(), "Slow-burning Oil XL");
        add(BMItems.VOIDING_ANOINTMENT_XL.get(), "Voiding Essence XL");
        add(BMItems.BOW_VELOCITY_ANOINTMENT_XL.get(), "Archer's Polish XL");
        add(BMItems.WEAPON_REPAIR_ANOINTMENT_XL.get(), "Mending Balm XL");

        // Anointment Items - 3 variants (level 3)
        add(BMItems.MELEE_DAMAGE_ANOINTMENT_3.get(), "Honing Oil III");
        add(BMItems.FORTUNE_ANOINTMENT_3.get(), "Fortuna Extract III");
        add(BMItems.HOLY_WATER_ANOINTMENT_3.get(), "Holy Water III");
        add(BMItems.HIDDEN_KNOWLEDGE_ANOINTMENT_3.get(), "Miner's Secrets III");
        add(BMItems.QUICK_DRAW_ANOINTMENT_3.get(), "Dexterity Alkahest III");
        add(BMItems.LOOTING_ANOINTMENT_3.get(), "Plunderer's Glint III");
        add(BMItems.BOW_POWER_ANOINTMENT_3.get(), "Iron Tip III");
        add(BMItems.BOW_VELOCITY_ANOINTMENT_3.get(), "Archer's Polish III");
        add(BMItems.WEAPON_REPAIR_ANOINTMENT_3.get(), "Mending Balm III");

        // Routing/Filter Items
        add(BMItems.FRAME_PARTS.get(), "Frame Parts");
        add(BMItems.ITEM_ROUTER_FILTER.get(), "Standard Filter");
        add(BMItems.ITEM_TAG_FILTER.get(), "Tag Filter");
        add(BMItems.ITEM_ENCHANT_FILTER.get(), "Enchantment Filter");
        add(BMItems.ITEM_MOD_FILTER.get(), "Mod Filter");
        add(BMItems.ITEM_COMPOSITE_FILTER.get(), "Composite Filter");

        // Filter GUI translations
        add("filter.neovitae.whitelist", "Whitelist Mode");
        add("filter.neovitae.blacklist", "Blacklist Mode");
        add("filter.neovitae.anytag", "Match Any Tag:");
        add("filter.neovitae.specifiedtag", "Specified Tag:");
        add("filter.neovitae.novalidtag", "No valid tag");

        // Filter GUI display names (used by getDisplayName())
        add("gui.neovitae.filter.exact", "Exact Filter");
        add("gui.neovitae.filter.mod", "Mod Filter");
        add("gui.neovitae.filter.tag", "Tag Filter");
        add("gui.neovitae.filter.composite", "Composite Filter");

        // Filter tooltip descriptions
        add("tooltip.neovitae.modfilter.desc", "Filters items by their mod namespace");
        add("tooltip.neovitae.tagfilter.desc", "Filters items by their tags");
        add("tooltip.neovitae.compositefilter.desc", "Combines multiple filters together");
        add("tooltip.neovitae.filter.whitelist", "Whitelist: Only matching items pass");
        add("tooltip.neovitae.filter.blacklist", "Blacklist: Non-matching items pass");
        add("tooltip.neovitae.filter.from_mod", "Items from %s");
        add("tooltip.neovitae.filter.count", "%d x %s");
        add("tooltip.neovitae.filter.all", "All %s");
        add("tooltip.neovitae.filter.anytag", "Any tag on %s");
        add("tooltip.neovitae.extraInfo", "Hold SHIFT for details");
        add("tooltip.neovitae.contained_filters", "Contained Filters:");

        // Bleeding Edge Music Disc
        add(BMItems.BLEEDING_EDGE.get(), "Music Disc");
        add("jukebox_song.neovitae.bleeding_edge", "WayOfTime - Bleeding Edge");
        add(BMBlocks.RAW_DEMONITE_BLOCK, "Raw Demonite Block");

        // Alchemy Catalysts
        add(BMItems.SIMPLE_CATALYST.get(), "Simple Catalyst");
        add(BMItems.STRENGTHENED_CATALYST.get(), "Strengthened Catalyst");
        add(BMItems.CYCLING_CATALYST.get(), "Cycling Catalyst");
        add(BMItems.COMBINATIONAL_CATALYST.get(), "Combinational Catalyst");
        add(BMItems.MUNDANE_LENGTHENING_CATALYST.get(), "Mundane Lengthening Catalyst");
        add(BMItems.MUNDANE_POWER_CATALYST.get(), "Mundane Power Catalyst");
        add(BMItems.AVERAGE_LENGTHENING_CATALYST.get(), "Average Lengthening Catalyst");
        add(BMItems.AVERAGE_POWER_CATALYST.get(), "Average Power Catalyst");

        // Filling Agents
        add(BMItems.WEAK_FILLING_AGENT.get(), "Weak Filling Agent");
        add(BMItems.STANDARD_FILLING_AGENT.get(), "Standard Filling Agent");

        // Hellforged Parts
        add(BMItems.HELLFORGED_PARTS.get(), "Hellforged Parts");

        // Teleposer Block
        add(BMBlocks.TELEPOSER, "Teleposer");

        // Teleposer Focus Items
        add(BMItems.TELEPOSER_FOCUS.get(), "Teleposer Focus");
        add(BMItems.TELEPOSER_FOCUS_ENHANCED.get(), "Enhanced Teleposer Focus");
        add(BMItems.TELEPOSER_FOCUS_REINFORCED.get(), "Reinforced Teleposer Focus");
        addTooltip("telepositionfocus.coords", "Coordinates: %s, %s, %s");
        addTooltip("telepositionfocus.world", "Dimension: %s");

        // Activation Crystals
        add(BMItems.ACTIVATION_CRYSTAL_WEAK.get(), "Weak Activation Crystal");
        add(BMItems.ACTIVATION_CRYSTAL_AWAKENED.get(), "Awakened Activation Crystal");
        add(BMItems.ACTIVATION_CRYSTAL_CREATIVE.get(), "Creative Activation Crystal");
        addTooltip("activationcrystal.weak", "Activates low-level rituals.");
        addTooltip("activationcrystal.awakened", "Activates more powerful rituals.");
        addTooltip("activationcrystal.creative", "Creative Only - Activates any ritual.");

        // Inscription Tools
        add(BMItems.INSCRIPTION_TOOL_AIR.get(), "Inscription Tool: Air");
        add(BMItems.INSCRIPTION_TOOL_FIRE.get(), "Inscription Tool: Fire");
        add(BMItems.INSCRIPTION_TOOL_WATER.get(), "Inscription Tool: Water");
        add(BMItems.INSCRIPTION_TOOL_EARTH.get(), "Inscription Tool: Earth");
        add(BMItems.INSCRIPTION_TOOL_DUSK.get(), "Inscription Tool: Dusk");
        addTooltip("inscriber.desc", "The writing is on the wall...");

        // Ritual Diviners
        add(BMItems.RITUAL_DIVINER.get(), "Ritual Diviner");
        add(BMItems.RITUAL_DIVINER_DUSK.get(), "Ritual Diviner [Dusk]");
        addTooltip("diviner.desc", "Used to build rituals.");
        addTooltip("diviner.currentRitual", "Current Ritual: %s");
        addTooltip("diviner.currentDirection", "Current Direction: %s");
        addTooltip("diviner.noRitual", "No ritual selected");
        addTooltip("diviner.cycleHint", "Sneak + right-click air to select ritual");
        addTooltip("diviner.blankRune", "Blank Runes: %d");
        addTooltip("diviner.airRune", "Air Runes: %d");
        addTooltip("diviner.waterRune", "Water Runes: %d");
        addTooltip("diviner.fireRune", "Fire Runes: %d");
        addTooltip("diviner.earthRune", "Earth Runes: %d");
        addTooltip("diviner.duskRune", "Dusk Runes: %d");
        addTooltip("diviner.dawnRune", "Dawn Runes: %d");
        addTooltip("diviner.totalRune", "Total Runes: %d");
        addTooltip("diviner.extraInfo", "Press shift for extra info.");
        addTooltip("diviner.extraExtraInfo", "-Hold shift + alt for augmentation info-");
        add("chat.neovitae.diviner.blockedBuild", "Unable to replace block at %d, %d, %d.");
        add("chat.neovitae.diviner.noRituals", "No rituals available for this diviner.");
        add("chat.neovitae.diviner.noRitualSelected", "No ritual selected. Sneak + right-click in air to select.");
        add("chat.neovitae.diviner.ritualComplete", "Ritual structure complete!");

        // Ritual Reader
        add(BMItems.RITUAL_READER.get(), "Ritual Reader");
        addTooltip("reader.desc", "Used to configure ritual areas.");
        addTooltip("reader.currentState", "Mode: %s");
        addTooltip("reader.currentRange", "Range: %s");
        addTooltip("reader.state.information", "Information");
        addTooltip("reader.state.set_area_corner_1", "Set Area Corner 1");
        addTooltip("reader.state.set_area_corner_2", "Set Area Corner 2");
        addTooltip("reader.state.set_will_config", "Set Will Configuration");
        addTooltip("reader.help.1", "Click MRS for ritual info");
        addTooltip("reader.help.2", "Sneak + click MRS to cycle modes");
        addTooltip("reader.help.3", "Sneak + click air to cycle ranges");
        add("chat.neovitae.reader.noRitual", "No ritual active on this Master Ritual Stone.");
        add("chat.neovitae.reader.noMRS", "No active Master Ritual Stone found nearby.");
        add("chat.neovitae.reader.noRangeSelected", "No range selected. Click on an active MRS first.");
        add("chat.neovitae.reader.currentRange", "Current range: %s");
        add("chat.neovitae.reader.rangeSelected", "Range selected: %s");
        add("chat.neovitae.reader.corner1Set", "Corner 1 set at %d, %d, %d");
        add("chat.neovitae.reader.areaSet", "Area '%s' updated successfully.");
        add("chat.neovitae.reader.invalidRange", "Invalid range key.");
        add("chat.neovitae.reader.willType", "Will type set to: %s");
        add("ritual.neovitae.blockRange.noRange", "No range with that key.");
        add("ritual.neovitae.blockRange.tooBig", "Area volume exceeds limit of %d blocks.");
        add("ritual.neovitae.blockRange.tooFar", "Area extends beyond limits (vertical: %d, horizontal: %d).");

        // Imperfect Ritual Stone messages
        add("chat.neovitae.imperfect.noBlock", "Place a block above the ritual stone!");
        add("chat.neovitae.imperfect.activated", "%s activated!");
        add("chat.neovitae.imperfect.notEnoughLP", "Not enough LP! Requires %d LP.");
        add("chat.neovitae.imperfect.noMatch", "No imperfect ritual matches that block.");

        // Master Ritual Stone activation messages
        add("chat.neovitae.crystal.notBound", "The crystal is not bound to a player!");
        add("chat.neovitae.ritual.activated", "%s has been activated!");
        add("chat.neovitae.ritual.noMatch", "No ritual found at this location.");
        add("chat.neovitae.ritual.deactivated", "Ritual has been deactivated.");
        add("chat.neovitae.ritual.notActive", "No ritual is currently active.");

        // Ritual failure messages
        add("chat.neovitae.ritual.notEnoughLP", "Not enough LP! Requires %d LP.");
        add("chat.neovitae.ritual.noSoulNetwork", "You must bind a Blood Orb first!");
        add("chat.neovitae.ritual.eventCancelled", "Ritual activation was blocked.");
        add("chat.neovitae.ritual.activationFailed", "Ritual activation failed.");
        add("chat.neovitae.ritual.missingItem", "Required item not found.");
        add("chat.neovitae.ritual.missingCondition", "Ritual conditions not met.");
        add("chat.neovitae.ritual.clientSide", "Cannot activate on client.");
        add("chat.neovitae.ritual.unknownFailure", "Ritual failed for unknown reason.");
        add("chat.neovitae.ritual.disabled", "This ritual has been disabled.");

        // Dungeon Seal messages
        add("chat.neovitae.dungeon.seal.opened", "The seal has been broken. A new path opens...");
        add("chat.neovitae.dungeon.seal.failed", "The seal remains firmly shut.");
        add("chat.neovitae.dungeon.seal.wrongKey", "This key doesn't fit this seal.");

        // Dungeon Key items
        add(BMItems.SIMPLE_KEY.get(), "Simple Dungeon Key");
        add(BMItems.MINE_KEY.get(), "Mine Dungeon Key");
        add(BMItems.MINE_ENTRANCE_KEY.get(), "Mine Entrance Key");
        add(BMItems.STANDARD_KEY.get(), "Standard Dungeon Key");
        add(BMItems.BOSS_KEY.get(), "Boss Key");
        add("tooltip.neovitae.dungeon_key.type", "Key Type: %s");
        add("tooltip.neovitae.dungeon_key.desc", "Use on sealed dungeon doors");

        // Dungeon Tester (debug item)
        add(BMItems.DUNGEON_TESTER.get(), "Dungeon Tester");

        // Ritual activation status messages
        add("ritual.neovitae.crystalLevel.insufficient", "Crystal tier is too low to activate this ritual.");
        add("ritual.neovitae.structure.invalid", "Ritual structure is incomplete or invalid.");
        add("ritual.neovitae.activation.insufficient", "Not enough LP to activate this ritual.");
        add("ritual.neovitae.offset.info", "Offset: X=%d, Y=%d, Z=%d");

        // Tau Oil
        add(BMItems.TAU_OIL.get(), "Tau Oil");

        // Anointments (using 1.20.1 thematic names)
        addAnointment("melee_damage", "Whetstone");
        addAnointment("silk_touch", "Soft Touch");
        addAnointment("fortune", "Fortunate");
        addAnointment("holy_water", "Holy Light");
        addAnointment("hidden_knowledge", "Miner's Secrets");
        addAnointment("quick_draw", "Deft Hands");
        addAnointment("looting", "Plundering");
        addAnointment("bow_power", "Heavy Shot");
        addAnointment("will_power", "Will Power");
        addAnointment("smelting", "Heated Tool");
        addAnointment("voiding", "Voiding");
        addAnointment("bow_velocity", "Sniping");
        addAnointment("repairing", "Regular Maintenance");

        // Anointment tooltips
        addTooltip("anointment.level", "Level: %s");
        addTooltip("anointment.uses", "Uses: %s");
        addTooltip("anointment.shift_for_details", "Hold Shift for details");

        // ARC Items
        add(BMItems.BASIC_CUTTING_FLUID.get(), "Basic Cutting Fluid");
        add(BMItems.INTERMEDIATE_CUTTING_FLUID.get(), "Intermediate Cutting Fluid");
        add(BMItems.ADVANCED_CUTTING_FLUID.get(), "Advanced Cutting Fluid");
        add(BMItems.EXPLOSIVE_POWDER.get(), "Explosive Powder");
        add(BMItems.RESONATOR.get(), "Crystal Resonator");
        add(BMItems.PRIMITIVE_CRYSTALLINE_RESONATOR.get(), "Reinforced Resonator");
        add(BMItems.HELLFORGED_RESONATOR.get(), "Hellforged Resonator");
        add(BMItems.PRIMITIVE_FURNACE_CELL.get(), "Primitive Fuel Cell");
        add(BMItems.PRIMITIVE_HYDRATION_CELL.get(), "Primitive Hydration Cell");
        add(BMItems.PRIMITIVE_EXPLOSIVE_CELL.get(), "Reinforced Explosive Cell");
        add(BMItems.HELLFORGED_EXPLOSIVE_CELL.get(), "Hellforged Explosive Cell");
        add(BMItems.SANGUINE_REVERTER.get(), "Sanguine Reverter");

        // ARC Tool tooltips
        addTooltip("arctool.uses", "Uses Remaining: %s");
        addTooltip("arctool.craftspeed", "Crafting Speed: %sx");
        addTooltip("arctool.additionaldrops", "Additional Output Chance: %sx");

        // Ore Processing Items
        add(BMItems.IRON_FRAGMENT.get(), "Iron Fragment");
        add(BMItems.IRON_GRAVEL.get(), "Iron Gravel");
        add(BMItems.IRON_SAND.get(), "Iron Sand");
        add(BMItems.GOLD_FRAGMENT.get(), "Gold Fragment");
        add(BMItems.GOLD_GRAVEL.get(), "Gold Gravel");
        add(BMItems.GOLD_SAND.get(), "Gold Sand");
        add(BMItems.COPPER_FRAGMENT.get(), "Copper Fragment");
        add(BMItems.COPPER_GRAVEL.get(), "Copper Gravel");
        add(BMItems.COPPER_SAND.get(), "Copper Sand");
        add(BMItems.COAL_SAND.get(), "Coal Sand");
        add(BMItems.DEMONITE_FRAGMENT.get(), "Demonite Fragment");
        add(BMItems.DEMONITE_GRAVEL.get(), "Demonite Gravel");
        add(BMItems.NETHERITE_SCRAP_FRAGMENT.get(), "Ancient Debris Fragment");
        add(BMItems.NETHERITE_SCRAP_GRAVEL.get(), "Ancient Debris Gravel");
        add(BMItems.NETHERITE_SCRAP_SAND.get(), "Netherite Scrap Sand");
        add(BMItems.HELLFORGED_SAND.get(), "Hellforged Sand");
        add(BMItems.CORRUPTED_DUST.get(), "Corrupted Dust");
        add(BMItems.CORRUPTED_DUST_TINY.get(), "Tiny Corrupted Dust");

        addTooltip("will", "Will Quality: %s");
        for (EnumWillType type : EnumWillType.values()) {
            addTooltip("current_type." + type.getSerializedName(), String.format("Contains: %s Will", type.toCapitalized()));
        }
        add("item_group.neovitae.main", "Neo Vitae");
        add("item_group.neovitae.tomes", "Neo Vitae Upgrade Tomes");
        add("item_group.neovitae.trainers", "Neo Vitae Trainer Tomes");

        add(BMItems.LIVING_HELMET.get(), "Living Helmet");
        add(BMItems.LIVING_PLATE.get(), "Living Plate");
        add(BMItems.LIVING_LEGGINGS.get(), "Living Leggings");
        add(BMItems.LIVING_BOOTS.get(), "Living Boots");
        add(BMItems.UPGRADE_TOME.get(), "Upgrade Tome");
        add(BMItems.EXPERIENCE_TOME.get(), "Tome of Peritia");
        addTooltip("experience_tome.stored", "Stored XP: %s");
        addTooltip("experience_tome.sneak_use", "Sneak + Use: Store XP");
        addTooltip("experience_tome.use", "Use: Retrieve XP");

        add(BMItems.UPGRADE_SCRAP.get(), "Upgrade Tome Scrap");
        add(BMItems.SYNTHETIC_POINT.get(), "Synthetic Upgrade Points");
        addTooltip("scrap", "Contained Upgrade Points: %s");

        add(BMItems.TRAINING_BRACELET.get(), "Living Training Bracelet");
        add("trainer.neovitae.allow_others", "Allow Others");
        add("trainer.neovitae.deny_others", "Deny Others");
        add("trainer.neovitae.save", "Save");

        add("item.neovitae.living_plate.dead", "Formerly Living Plate");
        addTooltip("has_living_stats", "Theres some kind of notes, but you cant decipher them");

        addCommand("upgrade.get", "%s has the following upgrades:\n");
        addCommand("upgrade.set", "Set %s to %s exp for %s");
        addCommand("upgrade.no_armour", "The chestplate %s is wearing does not have a neovitae:required_set component set. Upgrades cannot take effect like this");
        addCommand("cap.success", "Set max upgrade points to %s");
        addCommand("recalc.success", "Upgrades use up %s points");
        addCommand("limit.get", "%s is in '%s' mode and has the following limits:\n");
        addCommand("limit.set", "Set limit of %s to %s exp for %s");
        addCommand("limit.mode.allow", "allow others");
        addCommand("limit.mode.deny", "deny others");

        // Ritual commands
        addCommand("ritual.not_mrs", "Target block is not a Master Ritual Stone.");
        addCommand("ritual.unknown", "Unknown ritual: %s");
        addCommand("ritual.none_active", "No ritual is currently active.");
        addCommand("ritual.info.inactive", "No ritual is active on this Master Ritual Stone.");
        addCommand("ritual.info.header", "=== Ritual Information ===");
        addCommand("ritual.info.name", "Ritual: %s");
        addCommand("ritual.info.running_time", "Running Time: %d ticks");
        addCommand("ritual.info.cooldown", "Cooldown: %d ticks");
        addCommand("ritual.info.owner", "Owner: %s");
        addCommand("ritual.info.refresh_cost", "Refresh Cost: %d LP");
        addCommand("ritual.info.direction", "Direction: %s");
        addCommand("ritual.stopped", "Ritual %s has been stopped.");
        addCommand("ritual.set", "Ritual set to %s.");
        addCommand("ritual.cooldown_set", "Cooldown set to %d ticks.");
        addCommand("ritual.list.header", "=== Available Rituals ===");

        // Imperfect ritual command
        addCommand("imperfect_ritual.not_irs", "Target block is not an Imperfect Ritual Stone.");
        addCommand("imperfect_ritual.unknown", "Unknown imperfect ritual: %s");
        addCommand("imperfect_ritual.no_block", "Imperfect ritual %s has no block requirement in DataMap.");
        addCommand("imperfect_ritual.activated", "Imperfect ritual %s activated.");
        addCommand("imperfect_ritual.failed", "Imperfect ritual %s failed to activate (insufficient LP?).");
        addCommand("imperfect_ritual.placed", "Placed block for imperfect ritual %s: %s");
        addCommand("imperfect_ritual.list.header", "=== Available Imperfect Rituals ===");

        addTooltip("upgrade_points", "Upgrade Points: %s/%s");

        // Sigil descriptions
        addTooltip("sigil.divination.desc", "Use on altar for info, or in the air for network LP");
        addTooltip("sigil.seer.desc", "Use on altar for detailed info, or in the air for network LP");
        addTooltip("sigil.air.desc", "Launches you into the air");
        addTooltip("sigil.bloodlight.desc", "Creates a light source where you click");
        addTooltip("sigil.fast_miner.desc", "Increases mining speed while active");
        addTooltip("sigil.frost.desc", "Freezes water beneath your feet");
        addTooltip("sigil.suppression.desc", "Pushes away nearby fluids");
        addTooltip("sigil.phantom_bridge.desc", "Creates a phantom bridge beneath you");
        addTooltip("sigil.magnetism.desc", "Pulls nearby items towards you");
        addTooltip("sigil.teleposition.desc", "Teleports you to a bound location");
        addTooltip("sigil.holding.desc", "Holds up to 5 sigils - scroll to switch");
        addTooltip("sigil.void.desc", "Voids fluids in front of you");
        addTooltip("sigil.green_grove.desc", "Accelerates plant growth nearby");
        addTooltip("sigil.water.desc", "Places water source blocks");
        addTooltip("sigil.lava.desc", "Places lava source blocks");

        // Sigil tooltips - Divination/Seer info messages
        addTooltip("sigil.divination.currentAltarTier", "Current Altar Tier: %s");
        addTooltip("sigil.divination.currentEssence", "Current Life Essence: %s");
        addTooltip("sigil.divination.currentAltarCapacity", "Altar Capacity: %s Life Essence");
        addTooltip("sigil.divination.currentNetworkLP", "Current LP: %s");
        addTooltip("sigil.divination.otherNetwork", "Viewing network of: %s");
        addTooltip("sigil.seer.currentAltarTier", "Current Altar Tier: %s");
        addTooltip("sigil.seer.currentEssence", "Current Life Essence: %s");
        addTooltip("sigil.seer.currentAltarCapacity", "Altar Capacity: %s Life Essence");
        addTooltip("sigil.seer.otherNetwork", "Viewing network of: %s");
        addTooltip("sigil.seer.currentAltarProgress", "Crafting Progress: %s%%");
        addTooltip("sigil.seer.currentAltarConsumption", "Consumption Rate: %s Life Essence/t");

        // Creative mode detailed altar stats
        addTooltip("sigil.divination.creative.capacityMod", "Capacity Multiplier: %sx");
        addTooltip("sigil.divination.creative.speedMod", "Speed Bonus: +%s");
        addTooltip("sigil.divination.creative.tickRate", "Tick Rate: %s ticks");
        addTooltip("sigil.divination.creative.sacrificeMod", "Sacrifice Bonus: +%s");
        addTooltip("sigil.divination.creative.selfSacMod", "Self-Sacrifice Bonus: +%s");
        addTooltip("sigil.divination.creative.dislocationMod", "Dislocation Multiplier: %sx");
        addTooltip("sigil.divination.creative.orbCapMod", "Orb Capacity Bonus: +%s");
        addTooltip("sigil.divination.creative.efficiencyMod", "Efficiency: %sx");
        addTooltip("sigil.divination.creative.chargingRate", "Charging Rate: %s Life Essence/tick");

        // Sigil activated/deactivated states
        addTooltip("activated", "Activated");
        addTooltip("deactivated", "Deactivated");

        // Sigil holding
        addTooltip("sigil.holding.sigilInSlot", "Slot %s: %s");

        // Current owner/binding
        addTooltip("currentOwner", "Bound to: %s");

        // Current will type (currentType variant for sentient tools)
        addTooltip("currentType.default", "Type: Raw");
        addTooltip("currentType.corrosive", "Type: Corrosive");
        addTooltip("currentType.destructive", "Type: Destructive");
        addTooltip("currentType.vengeful", "Type: Vengeful");
        addTooltip("currentType.steadfast", "Type: Steadfast");

        add("chat.neovitae.living_upgrade.level_up", "%s has levelled up to %s!");

        LivingUpgrades.translations(this::add);

        // JEI Integration
        addJei("recipe.altar", "Blood Altar");
        addJei("recipe.soulforge", "Hellfire Forge");
        addJei("recipe.alchemyarraycrafting", "Alchemy Array");
        addJei("recipe.alchemytable", "Alchemy Table");
        addJei("recipe.requiredtier", "Required Tier: %s");
        addJei("recipe.requiredlp", "Required Life Essence: %s");
        addJei("recipe.consumptionrate", "Consumption Rate: %s Life Essence/t");
        addJei("recipe.drainrate", "Drain Rate: %s LP/t");
        addJei("recipe.componentTransfer", "Preserves Components");
        addJei("recipe.minimumsouls", "Minimum Souls: %s");
        addJei("recipe.soulsdrained", "Souls Drained: %s");
        addJei("recipe.will", "Will");
        addJei("recipe.info", "Hover for info");
        addJei("recipe.lp", "LP");
        addJei("recipe.lpDrained", "LP Drained: %s");
        addJei("recipe.ticksRequired", "Ticks: %s");
        addJei("recipe.meteor", "Meteor Ritual");
        addJei("recipe.meteor.fill", "Fill Block");
        addJei("recipe.meteor.weight", "Weight: %s");
        addJei("recipe.meteor.estimate", "Est: %s blocks (~%s%%)");
        addJei("recipe.arc", "Alchemical Reaction Chamber");
        addJei("recipe.arc.chance", "Chance: %s%%");
        addJei("recipe.flask", "Flask Brewing");
        addJei("recipe.imperfect_ritual", "Imperfect Ritual");
        addJei("recipe.ritual", "Ritual");

        // Ritual JEI category
        addJei("recipe.ritual.activation", "Activation Cost:");
        addJei("recipe.ritual.refresh", "Refresh Cost:");
        addJei("recipe.ritual.total_runes", "Total Runes: %s");
        addJei("recipe.ritual.crystal.weak", "Tier: Weak");
        addJei("recipe.ritual.crystal.awakened", "Tier: Awakened");
        addJei("recipe.ritual.crystal.creative", "Tier: Creative");

        // Alchemy Array Effect Types (for JEI tooltips)
        addJei("effect.crafting.name", "Crafting Array");
        addJei("effect.crafting.desc", "Transforms items into new forms");
        addJei("effect.binding.name", "Binding Array");
        addJei("effect.binding.desc", "Binds items to the owner's soul network");
        addJei("effect.bounce.name", "Bounce Array");
        addJei("effect.bounce.desc", "Bounces entities high into the air");
        addJei("effect.spike.name", "Spike Array");
        addJei("effect.spike.desc", "Creates damaging spikes that harm entities");
        addJei("effect.updraft.name", "Updraft Array");
        addJei("effect.updraft.desc", "Creates an upward gust of wind");
        addJei("effect.movement.name", "Movement Array");
        addJei("effect.movement.desc", "Accelerates entities in a direction");
        addJei("effect.day.name", "Sunrise Array");
        addJei("effect.day.desc", "Sets the time to dawn");
        addJei("effect.night.name", "Moonrise Array");
        addJei("effect.night.desc", "Sets the time to night");

        // Rituals
        addRitual("water", "Ritual of the Full Spring");
        addRitual("lava", "Serenade of the Nether");
        addRitual("green_grove", "Ritual of the Green Grove");
        addRitual("well_of_suffering", "Well of Suffering");
        addRitual("feathered_knife", "Ritual of the Feathered Knife");
        addRitual("harvest", "Reap of the Harvest Moon");
        addRitual("regeneration", "Ritual of Regeneration");
        addRitual("speed", "Ritual of Speed");
        addRitual("jumping", "Ritual of the High Jump");
        addRitual("magnetism", "Ritual of Magnetism");
        addRitual("animal_growth", "Ritual of the Shepherd");
        addRitual("crushing", "Crushing Ritual");
        addRitual("felling", "Ritual of the Felling Tree");
        addRitual("suppression", "Dome of Suppression");
        addRitual("containment", "Ritual of Binding");
        addRitual("expulsion", "Aura of Expulsion");
        addRitual("zephyr", "Call of the Zephyr");
        addRitual("pump", "Hymn of Siphoning");
        addRitual("phantom_bridge", "Ritual of the Phantom Bridge");
        addRitual("crystal_harvest", "Crystalline Harvest");
        addRitual("downgrade", "Ritual of Living Evolution");
        addRitual("meteor", "Mark of the Falling Tower");
        addRitual("forsaken_soul", "Cry of the Forsaken Soul");
        addRitual("full_stomach", "Ritual of the Satiated Stomach");

        // Dusk Tier Rituals
        addRitual("condor", "Reverence of the Condor");
        addRitual("grounding", "The Sinner's Burden");
        addRitual("placer", "Ritual of the Mason");
        addRitual("geode", "Geode Resonance");
        addRitual("ellipse", "Ellipsoid Manifestation");
        addRitual("sphere", "Spherical Manifestation");
        addRitual("armour_evolve", "Ritual of Living Evolution");
        addRitual("upgrade_remove", "Sound of the Cleansing Soul");
        addRitual("crystal_split", "Resonance of the Faceted Crystal");
        addRitual("crafting", "Rhythm of the Beating Anvil");
        addRitual("yawning_void", "Yawning of the Void");

        // Dungeon Rituals (snake_case to match ritual constructors)
        add("ritual.neovitae.simple_dungeon", "Edge of the Hidden Realm");
        add("ritual.neovitae.simple_dungeon.info", "Opens a portal to a small dungeon pocket dimension.");
        add("ritual.neovitae.standard_dungeon", "Pathway to the Endless Realm");
        add("ritual.neovitae.standard_dungeon.info", "Opens a portal to a full procedural dungeon dimension.");

        // Dimension
        add("dimension.neovitae.dungeon", "The Demon Realm");

        // Mob Effects
        add("effect.neovitae.soulsnare", "Soul Snare");
        add("effect.neovitae.firefuse", "Fire Fuse");
        add("effect.neovitae.soulfray", "Soul Fray");
        add("effect.neovitae.plantleech", "Plant Leech");
        add("effect.neovitae.sacrificallamb", "Sacrificial Lamb");
        add("effect.neovitae.passivity", "Passivity");
        add("effect.neovitae.flight", "Flight");
        add("effect.neovitae.spectral_sight", "Spectral Sight");
        add("effect.neovitae.gravity", "Gravity");
        add("effect.neovitae.heavy_heart", "Heavy Heart");
        add("effect.neovitae.grounded", "Grounded");
        add("effect.neovitae.suspended", "Suspended");
        add("effect.neovitae.bounce", "Bounce");
        add("effect.neovitae.soft_fall", "Soft Fall");
        add("effect.neovitae.obsidian_cloak", "Obsidian Cloak");
        add("effect.neovitae.hard_cloak", "Hard Cloak");

        // Imperfect Rituals
        add("ritual.neovitae.imperfect.night", "Turn Day to Night");
        add("ritual.neovitae.imperfect.night.desc", "Turns day into night");
        add("ritual.neovitae.imperfect.rain", "Make it Rain");
        add("ritual.neovitae.imperfect.rain.desc", "Summons a thunderstorm");
        add("ritual.neovitae.imperfect.zombie", "Strong Zombie");
        add("ritual.neovitae.imperfect.zombie.desc", "Spawns a reinforced zombie");
        add("ritual.neovitae.imperfect.resistance", "Fire Resistance");
        add("ritual.neovitae.imperfect.resistance.desc", "Grants Fire Resistance II");

        // Dungeon Blocks
        addDungeonBlocks();

        // Patchouli Guidebook
        add("guide.neovitae.name", "Sanguine Scientiem");
        add("guide.neovitae.subtitle", "Neo Vitae Edition");
        add("guide.neovitae.landing_text", "Welcome to $(6)Neo Vitae$()! $(br2)$(l:neovitae:utility/nyi)A lot of stuff$() isn't yet implemented, so please excuse our dust. $(br2)Click $(l:neovitae:utility/getting_started)HERE$() to get started. If you find any bugs, please report them on our $(l:https://github.com/breakinblocks/NeoVitae/issues)Github$().");

        // Patchouli page components
        add("guide.patchouli.neovitae.common.double_new_line", "$(br2)%s");
        add("guide.patchouli.neovitae.arc_processor.fluid", "%dmb of %s");
        add("guide.patchouli.neovitae.arc_processor.no_fluid", "None");
        add("guide.patchouli.neovitae.living_armour_upgrade_table.level", "Level");
        add("guide.patchouli.neovitae.living_armour_upgrade_table.upgrade_points", "Upgrade Points");
        add("guide.patchouli.neovitae.ritual_info.activation_cost", "$(br)Activation Cost: $(blood)%d LP$()");
        add("guide.patchouli.neovitae.ritual_info.upkeep_cost", "$(br)Base Usage Cost: $(blood)%d LP$()$(br)Base Interval: %d Ticks");
        add("guide.patchouli.neovitae.ritual_info.weak_activation_crystal_link", "$(l:neovitae:rituals/activation_crystals#weak)%s$(/l)");
        add("guide.patchouli.neovitae.ritual_info.awakened_activation_crystal_link", "$(l:neovitae:rituals/activation_crystals#awakened)%s$(/l)");
        add("guide.patchouli.neovitae.ritual_info.counter_formatter", "$(br)%s%s$()");
        add("guide.patchouli.neovitae.ritual_info.text_override_formatter", "\\$(%s)%s\\$()");
        add("guide.patchouli.neovitae.ritual_info.info_formatter", "%s$(br)%s$(br2)%s$(br)%s%s%s");
        add("guide.patchouli.neovitae.ritual_info.range_formatter", "$(br) $(li)Max Volume: %s$(li)Horizontal Radius: %s$(li)Vertical Radius: %s");
        add("guide.patchouli.neovitae.ritual_info.full_range", "Full Range");

        // Advancements
        addAdvancement("root", "Neo Vitae", "Obtain a Blood Altar");
        addAdvancement("weak_blood_orb", "Weak Blood Orb", "Craft your first Blood Orb");
        addAdvancement("apprentice_blood_orb", "Apprentice Blood Orb", "Upgrade to a Tier 2 orb");
        addAdvancement("magician_blood_orb", "Magician Blood Orb", "Upgrade to a Tier 3 orb");
        addAdvancement("master_blood_orb", "Master Blood Orb", "Upgrade to a Tier 4 orb");
        addAdvancement("archmage_blood_orb", "Archmage Blood Orb", "Upgrade to a Tier 5 orb");
        addAdvancement("soul_snare", "Soul Snare", "Craft a Soul Snare");
        addAdvancement("demon_will", "Demon Will", "Obtain a Demon Will");
        addAdvancement("soul_sword", "Sentient Sword", "Craft a Sentient Sword");
        addAdvancement("soul_gem_petty", "Petty Tartaric Gem", "Craft a Petty Tartaric Gem");
        addAdvancement("soul_gem_lesser", "Lesser Tartaric Gem", "Upgrade to a Lesser Gem");
        addAdvancement("soul_gem_common", "Common Tartaric Gem", "Upgrade to a Common Gem");
        addAdvancement("soul_gem_greater", "Greater Tartaric Gem", "Upgrade to a Greater Gem");
        addAdvancement("soul_gem_grand", "Grand Tartaric Gem", "Upgrade to a Grand Gem");
        addAdvancement("ritual_diviner", "Ritual Diviner", "Craft a Ritual Diviner");
        addAdvancement("living_armor", "Living Armor", "Craft a Living Chestplate");
        addAdvancement("hellfire_forge", "Hellfire Forge", "Craft a Hellfire Forge");
        addAdvancement("demon_crucible", "Demon Crucible", "Craft a Demon Crucible");
    }

    public void addRitual(String key, String name) {
        add("ritual.neovitae." + key, name);
        add("ritual.neovitae." + key + ".info", "A Blood Magic ritual.");
    }

    public void addCommand(String key, String value) {
        add("commands.neovitae." + key, value);
    }

    public void addGemDesc(DeferredHolder holder, String desc) {
        addTooltip("soul_gem." + holder.getId().getPath(), String.format("A gem used to contain %s will.", desc));
    }

    public void add(BlockWithItemHolder<? extends Block, ? extends BlockItem> block, String name) {
        add(block.block().get().getDescriptionId(), name);
    }

    public void addTooltip(String name, String value) {
        add("tooltip.neovitae." + name, value);
    }

    public void addJei(String name, String value) {
        add("jei.neovitae." + name, value);
    }

    public void addAnointment(String key, String name) {
        add("anointment.neovitae." + key, name);
    }

    public void addAdvancement(String key, String title, String description) {
        add("advancements.neovitae." + key + ".title", title);
        add("advancements.neovitae." + key + ".description", description);
    }

    private void addDungeonBlocks() {
        // Non-variant dungeon blocks
        add(DungeonBlocks.DUNGEON_ORE, "Dungeon Ore");
        add(DungeonBlocks.DUNGEON_BRICK_ASSORTED, "Assorted Dungeon Brick");

        // Functional dungeon blocks
        add(DungeonBlocks.SPIKES, "Spikes");
        add(DungeonBlocks.SPIKE_TRAP, "Spike Trap");
        add(DungeonBlocks.ALTERNATOR, "Dungeon Alternator");

        // Path blocks
        add(DungeonBlocks.WOOD_BRICK_PATH, "Wood Brick Path");
        add(DungeonBlocks.WOOD_TILE_PATH, "Wood Tile Path");
        add(DungeonBlocks.STONE_BRICK_PATH, "Stone Brick Path");
        add(DungeonBlocks.STONE_TILE_PATH, "Stone Tile Path");
        add(DungeonBlocks.WORN_STONE_BRICK_PATH, "Worn Stone Brick Path");
        add(DungeonBlocks.WORN_STONE_TILE_PATH, "Worn Stone Tile Path");
        add(DungeonBlocks.OBSIDIAN_BRICK_PATH, "Obsidian Brick Path");
        add(DungeonBlocks.OBSIDIAN_TILE_PATH, "Obsidian Tile Path");

        // Variant dungeon blocks
        for (DungeonVariant variant : DungeonVariant.values()) {
            String prefix = variant == DungeonVariant.RAW ? "" : variant.getName().substring(0, 1).toUpperCase() + variant.getName().substring(1) + " ";

            // Base blocks
            add(DungeonBlocks.DUNGEON_BRICK_1.get(variant), prefix + "Dungeon Brick");
            add(DungeonBlocks.DUNGEON_BRICK_2.get(variant), prefix + "Dungeon Brick 2");
            add(DungeonBlocks.DUNGEON_BRICK_3.get(variant), prefix + "Dungeon Brick 3");
            add(DungeonBlocks.DUNGEON_STONE.get(variant), prefix + "Dungeon Stone");
            add(DungeonBlocks.DUNGEON_EYE.get(variant), prefix + "Dungeon Eye");
            add(DungeonBlocks.DUNGEON_POLISHED.get(variant), prefix + "Polished Dungeon Stone");
            add(DungeonBlocks.DUNGEON_TILE.get(variant), prefix + "Dungeon Tile");
            add(DungeonBlocks.DUNGEON_SMALLBRICK.get(variant), prefix + "Small Dungeon Brick");
            add(DungeonBlocks.DUNGEON_TILESPECIAL.get(variant), prefix + "Special Dungeon Tile");
            add(DungeonBlocks.DUNGEON_METAL.get(variant), prefix + "Dungeon Metal");

            // Pillars
            add(DungeonBlocks.DUNGEON_PILLAR_CENTER.get(variant), prefix + "Dungeon Pillar");
            add(DungeonBlocks.DUNGEON_PILLAR_SPECIAL.get(variant), prefix + "Special Dungeon Pillar");
            add(DungeonBlocks.DUNGEON_PILLAR_CAP.get(variant), prefix + "Dungeon Pillar Cap");

            // Stairs
            add(DungeonBlocks.DUNGEON_BRICK_STAIRS.get(variant), prefix + "Dungeon Brick Stairs");
            add(DungeonBlocks.DUNGEON_POLISHED_STAIRS.get(variant), prefix + "Polished Dungeon Stone Stairs");
            add(DungeonBlocks.DUNGEON_STONE_STAIRS.get(variant), prefix + "Dungeon Stone Stairs");

            // Walls
            add(DungeonBlocks.DUNGEON_BRICK_WALL.get(variant), prefix + "Dungeon Brick Wall");
            add(DungeonBlocks.DUNGEON_TILE_WALL.get(variant), prefix + "Dungeon Tile Wall");
            add(DungeonBlocks.DUNGEON_POLISHED_WALL.get(variant), prefix + "Polished Dungeon Stone Wall");
            add(DungeonBlocks.DUNGEON_STONE_WALL.get(variant), prefix + "Dungeon Stone Wall");

            // Slabs
            add(DungeonBlocks.DUNGEON_BRICK_SLAB.get(variant), prefix + "Dungeon Brick Slab");
            add(DungeonBlocks.DUNGEON_TILE_SLAB.get(variant), prefix + "Dungeon Tile Slab");
            add(DungeonBlocks.DUNGEON_STONE_SLAB.get(variant), prefix + "Dungeon Stone Slab");
            add(DungeonBlocks.DUNGEON_POLISHED_SLAB.get(variant), prefix + "Polished Dungeon Stone Slab");

            // Gates
            add(DungeonBlocks.DUNGEON_BRICK_GATE.get(variant), prefix + "Dungeon Brick Gate");
            add(DungeonBlocks.DUNGEON_POLISHED_GATE.get(variant), prefix + "Polished Dungeon Stone Gate");
        }
    }
}
