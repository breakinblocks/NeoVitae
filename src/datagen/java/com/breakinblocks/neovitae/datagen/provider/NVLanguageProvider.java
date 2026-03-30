package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonVariant;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.datagen.content.LivingUpgrades;
import com.breakinblocks.neovitae.util.helper.BlockWithItemHolder;

public class NVLanguageProvider extends LanguageProvider implements com.klikli_dev.modonomicon.api.datagen.ModonomiconLanguageProvider {

    public NVLanguageProvider(PackOutput output) {
        super(output, NeoVitae.MODID, "en_us");
    }

    @Override
    public void accept(String key, String value) {
        this.add(key, value);
    }

    @Override
    protected void addTranslations() {
        add(NVFluids.ESSENTIA_VITAE_TYPE.get().getDescriptionId(), "Essentia Vitae");
        add(NVFluids.ESSENTIA_VITAE_BUCKET.get(), "Bucket of Essentia Vitae");
        add(NVFluids.ESSENTIA_VITAE_BLOCK.get(), "Essentia Vitae");

        add(NVItems.ORB_WEAK.get(), "Novicius Orb of Vitae");
        add(NVItems.ORB_APPRENTICE.get(), "Discipulus Orb of Vitae");
        add(NVItems.ORB_MAGICIAN.get(), "Veneficus Orb of Vitae");
        add(NVItems.ORB_MASTER.get(), "Magus Orb of Vitae");
        add(NVItems.ORB_ARCHMAGE.get(), "Dominus Orb of Vitae");
        add(NVItems.ORB_TRANSCENDENT.get(), "Divinus Orb of Vitae");

        addTooltip("current_owner", "Current Owner: %s");
        addTooltip("no_owner", "Not bound yet");
        addTooltip("orb.fluid", "Essentia Vitae: %s / %s mB");
        addTooltip("orb.tier", "Tier %s");

        // Death messages
        add("death.attack.spikes", "%1$s was impaled by spikes");
        add("death.attack.spikes.player", "%1$s was impaled by spikes whilst fighting %2$s");
        add("death.attack.sacrifice", "%1$s was sacrificed");
        add("death.attack.sacrifice.player", "%1$s was sacrificed by %2$s");
        add("death.attack.self_sacrifice", "%1$s sacrificed too much of their own blood");
        add("death.attack.self_sacrifice.player", "%1$s sacrificed too much of their own blood whilst fighting %2$s");
        add("death.attack.ritual", "%1$s was killed by dark ritual magic");
        add("death.attack.ritual.player", "%1$s was killed by dark ritual magic whilst fighting %2$s");

        // Attributes
        add("attribute.neovitae.player.self_sacrifice", "Self Sacrifice Multiplier");
        add("attribute.neovitae.bonus_sacrifice", "Bonus Sacrifice");
        add("attribute.neovitae.bonus_self_sacrifice", "Bonus Self Sacrifice");
        add("attribute.neovitae.bonus_spiritus", "Bonus Spiritus");
        add("attribute.neovitae.sigil_cost_reduction", "Sigil Cost Reduction");
        add("attribute.neovitae.blood_siphon", "Blood Siphon");
        add("attribute.neovitae.blood_shield", "Blood Shield");

        add(NVBlocks.ARA_VITAE, "Ara Vitae");
        add(NVItems.SACRIFICIAL_DAGGER.get(), "Sacrificial Dagger");

        add(NVBlocks.RUNE_BLANK, "Blank Rune");

        add(NVBlocks.RUNE_SACRIFICE, "Rune of Sacrifice");
        add(NVBlocks.RUNE_SELF_SACRIFICE, "Rune of Self Sacrifice");
        add(NVBlocks.RUNE_SPEED, "Speed Rune");
        add(NVBlocks.RUNE_ACCELERATION, "Acceleration Rune");
        add(NVBlocks.RUNE_DISLOCATION, "Displacement Rune");
        add(NVBlocks.RUNE_CAPACITY, "Capacity Rune");
        add(NVBlocks.RUNE_CAPACITY_AUGMENTED, "Augmented Capacity Rune");
        add(NVBlocks.RUNE_CHARGING, "Charging Rune");
        add(NVBlocks.RUNE_ORB, "Rune of the Orb");
        add(NVBlocks.RUNE_EFFICIENCY, "Rune of Efficiency");

        add(NVBlocks.RUNE_2_SACRIFICE, "Reinforced Rune of Sacrifice");
        add(NVBlocks.RUNE_2_SELF_SACRIFICE, "Reinforced Rune of Self Sacrifice");
        add(NVBlocks.RUNE_2_SPEED, "Reinforced Speed Rune");
        add(NVBlocks.RUNE_2_ACCELERATION, "Reinforced Acceleration Rune");
        add(NVBlocks.RUNE_2_DISLOCATION, "Reinforced Displacement Rune");
        add(NVBlocks.RUNE_2_CAPACITY, "Reinforced Capacity Rune");
        add(NVBlocks.RUNE_2_CAPACITY_AUGMENTED, "Reinforced Augmented Capacity Rune");
        add(NVBlocks.RUNE_2_CHARGING, "Reinforced Charging Rune");
        add(NVBlocks.RUNE_2_ORB, "Reinforced Rune of the Orb");
        add(NVBlocks.RUNE_2_EFFICIENCY, "Reinforced Rune of Efficiency");

        add(NVBlocks.BLOODSTONE, "Polished Bloodstone");
        add(NVBlocks.BLOODSTONE_BRICK, "Bloodstone Brick");

        add(NVBlocks.HELLFORGED_BLOCK, "Hellforged Block");

        add(NVBlocks.CRYSTAL_CLUSTER, "Crystal Cluster");
        add(NVBlocks.CRYSTAL_CLUSTER_BRICK, "Crystal Cluster Brick");

        // Spiritus Blocks
        add(NVBlocks.VAS_MALEFICUM, "Vas Maleficum");
        add(NVBlocks.CRYSTALLARIUM_MALEFICUM, "Crystallarium Maleficum");
        add(NVBlocks.SPIRA_INFERNALIS, "Spira Infernalis");

        // Demon Crystal Blocks
        add(NVBlocks.RAW_SPIRITUS_CRYSTAL, "Raw Crystal Cluster");
        add(NVBlocks.CORROSIVE_SPIRITUS_CRYSTAL, "Corrosive Crystal Cluster");
        add(NVBlocks.DESTRUCTIVE_SPIRITUS_CRYSTAL, "Destructive Crystal Cluster");
        add(NVBlocks.VENGEFUL_SPIRITUS_CRYSTAL, "Vengeful Crystal Cluster");
        add(NVBlocks.STEADFAST_SPIRITUS_CRYSTAL, "Steadfast Crystal Cluster");

        // Routing Node Blocks
        add(NVBlocks.ROUTING_NODE, "Item Routing Node");
        add(NVBlocks.INPUT_ROUTING_NODE, "Input Routing Node");
        add(NVBlocks.OUTPUT_ROUTING_NODE, "Output Routing Node");
        add(NVBlocks.MASTER_ROUTING_NODE, "Master Routing Node");

        // Tau Blocks
        add(NVBlocks.WEAK_TAU, "Weak Tau");
        add(NVBlocks.STRONG_TAU, "Strong Tau");

        // Ritual Stones
        add(NVBlocks.BLANK_RITUAL_STONE, "Ritual Stone");
        add(NVBlocks.AIR_RITUAL_STONE, "Air Ritual Stone");
        add(NVBlocks.WATER_RITUAL_STONE, "Water Ritual Stone");
        add(NVBlocks.FIRE_RITUAL_STONE, "Fire Ritual Stone");
        add(NVBlocks.EARTH_RITUAL_STONE, "Earth Ritual Stone");
        add(NVBlocks.DUSK_RITUAL_STONE, "Dusk Ritual Stone");
        add(NVBlocks.DAWN_RITUAL_STONE, "Dawn Ritual Stone");
        add(NVBlocks.MASTER_RITUAL_STONE, "Master Ritual Stone");
        add(NVBlocks.INVERTED_MASTER_RITUAL_STONE, "Inverted Master Ritual Stone");
        add(NVBlocks.IMPERFECT_RITUAL_STONE, "Imperfect Ritual Stone");
        addTooltip("imperfectRitualStone.desc", "Simple ritual stone for quick effects");
        addTooltip("imperfectRitualStone.hint", "Place a block above and right-click to activate");
        addTooltip("decoration.safe", "Safe for Decoration");
        addTooltip("masterRitualStone.inverted", "Inverted - requires redstone signal to operate");

        addTooltip("save_for_decoration", "Save for Decoration");

        add(NVFluids.ANIMATED_SPIRITUS_TYPE.get().getDescriptionId(), "Animated Spiritus Essence");
        add(NVFluids.ANIMATED_SPIRITUS_BUCKET.get(), "Bucket of Animated Spiritus");
        add(NVFluids.ANIMATED_SPIRITUS_BLOCK.get(), "Animated Spiritus Essence");

        add(NVBlocks.ATHANOR_BLOCK, "Athanor");

        add(NVBlocks.BLOOD_TANK, "Blood Tank");
        addTooltip("container_tier_missing", "No Tier found!");
        addTooltip("container_tier", "Current Tier: %s");
        addTooltip("fluid_content_empty", "Empty");
        addTooltip("fluid_content", "Contains: %smB of %s");

        add(NVBlocks.HELLFIRE_FORGE, "Hellfire Forge");
        add(NVItems.RAW_SPIRITUS.get(), "Raw Spiritus");

        // Spiritus Essence (dropped from mobs with sentient weapons)
        add(NVItems.MONSTER_SOUL_RAW.get(), "Spiritus Essence");
        add(NVItems.MONSTER_SOUL_CORROSIVE.get(), "Corrosive Spiritus Essence");
        add(NVItems.MONSTER_SOUL_DESTRUCTIVE.get(), "Destructive Spiritus Essence");
        add(NVItems.MONSTER_SOUL_VENGEFUL.get(), "Vengeful Spiritus Essence");
        add(NVItems.MONSTER_SOUL_STEADFAST.get(), "Steadfast Spiritus Essence");

        add(NVItems.SPIRITUS_GEM_PETTY.get(), "Petty Spiritus Gem");
        add(NVItems.SPIRITUS_GEM_LESSER.get(), "Lesser Spiritus Gem");
        add(NVItems.SPIRITUS_GEM_COMMON.get(), "Common Spiritus Gem");
        add(NVItems.SPIRITUS_GEM_GREATER.get(), "Greater Spiritus Gem");
        add(NVItems.SPIRITUS_GEM_GRAND.get(), "Grand Spiritus Gem");
        addGemDesc(NVItems.SPIRITUS_GEM_PETTY, "a little");
        addGemDesc(NVItems.SPIRITUS_GEM_LESSER, "some");
        addGemDesc(NVItems.SPIRITUS_GEM_COMMON, "more");
        addGemDesc(NVItems.SPIRITUS_GEM_GREATER, "a greater amount of");
        addGemDesc(NVItems.SPIRITUS_GEM_GRAND, "a large amount of");

        // Slates
        add(NVItems.SLATE_BLANK.get(), "Blank Slate");
        add(NVItems.SLATE_REINFORCED.get(), "Reinforced Slate");
        add(NVItems.SLATE_IMBUED.get(), "Imbued Slate");
        add(NVItems.SLATE_DEMONIC.get(), "Demonic Slate");
        add(NVItems.SLATE_ETHEREAL.get(), "Ethereal Slate");

        // Sigils
        add(NVItems.SIGIL_DIVINATION.get(), "Divination Sigil");
        add(NVItems.SIGIL_SEER.get(), "Seer's Sigil");
        add(NVItems.SIGIL_WATER.get(), "Water Sigil");
        add(NVItems.SIGIL_LAVA.get(), "Lava Sigil");
        add(NVItems.SIGIL_VOID.get(), "Void Sigil");
        add(NVItems.SIGIL_GREEN_GROVE.get(), "Sigil of the Green Grove");
        add(NVItems.SIGIL_AIR.get(), "Air Sigil");
        add(NVItems.SIGIL_BLOOD_LIGHT.get(), "Sigil of the Blood Lamp");
        add(NVItems.SIGIL_FAST_MINER.get(), "Sigil of the Fast Miner");
        add(NVItems.SIGIL_MAGNETISM.get(), "Sigil of Magnetism");
        add(NVItems.SIGIL_FROST.get(), "Sigil of the Frost Walker");
        add(NVItems.SIGIL_SUPPRESSION.get(), "Sigil of Suppression");
        add(NVItems.SIGIL_HOLDING.get(), "Sigil of Holding");
        add(NVItems.SIGIL_TELEPOSITION.get(), "Sigil of Teleposition");
        add(NVItems.SIGIL_PHANTOM_BRIDGE.get(), "Sigil of the Phantom Bridge");

        // Alchemy & Misc
        add(NVItems.ARCANE_ASHES.get(), "Arcane Ashes");
        addTooltip("arcaneAshes", "Draws an alchemy circle when placed");

        // Reagents
        add(NVItems.REAGENT_WATER.get(), "Reagent Water");
        add(NVItems.REAGENT_LAVA.get(), "Reagent Lava");
        add(NVItems.REAGENT_VOID.get(), "Reagent Void");
        add(NVItems.REAGENT_GROWTH.get(), "Reagent Growth");
        add(NVItems.REAGENT_FAST_MINER.get(), "Reagent Fast Miner");
        add(NVItems.REAGENT_MAGNETISM.get(), "Reagent Magnetism");
        add(NVItems.REAGENT_AIR.get(), "Reagent Air");
        add(NVItems.REAGENT_BLOOD_LIGHT.get(), "Reagent Blood Light");
        add(NVItems.REAGENT_SIGHT.get(), "Reagent Sight");
        add(NVItems.REAGENT_BINDING.get(), "Reagent Binding");
        add(NVItems.REAGENT_HOLDING.get(), "Reagent Holding");
        add(NVItems.REAGENT_SUPPRESSION.get(), "Reagent Suppression");
        add(NVItems.REAGENT_TELEPOSITION.get(), "Reagent Teleposition");
        add(NVItems.REAGENT_FROST.get(), "Reagent Frost");
        add(NVItems.REAGENT_PHANTOM_BRIDGE.get(), "Reagent Phantom Bridge");

        // Alchemy Array and Table Blocks
        add(NVBlocks.ALCHEMY_ARRAY.get(), "Alchemy Array");
        add(NVBlocks.TABULA_VITAE, "Tabula Vitae");

        // Blood Light, Spectral Blocks, and Phantom Bridge (placed by sigils, no block item)
        add(NVBlocks.BLOOD_LIGHT.get().getDescriptionId(), "Blood Light");
        add(NVBlocks.SPECTRAL_BLOCK.get().getDescriptionId(), "Spectral Block");
        add(NVBlocks.PHANTOM_BRIDGE_BLOCK.get().getDescriptionId(), "Phantom Bridge");

        // Incense Altar
        add(NVBlocks.INCENSE_ALTAR, "Incense Altar");

        add(NVItems.SPIRITUS_SNARE.get(), "Soul Snare");
        addTooltip("soulSnare.desc", "Throw at weakened mobs to extract their soul");
        add(NVItems.WEAK_BLOOD_SHARD.get(), "Weak Blood Shard");
        add(NVItems.DAGGER_OF_SACRIFICE.get(), "Dagger of Sacrifice");
        add(NVItems.LAVA_CRYSTAL.get(), "Lava Crystal");
        addTooltip("lavaCrystal.desc", "Place fire, bindable furnace fuel");
        add("chat.neovitae.notEnoughLP", "Not enough Essentia Vitae!");

        // Crystal Items
        add(NVItems.RAW_CRYSTAL.get(), "Spiritus Crystal");
        add(NVItems.CORROSIVE_CRYSTAL.get(), "Corrosive Spiritus Crystal");
        add(NVItems.DESTRUCTIVE_CRYSTAL.get(), "Destructive Spiritus Crystal");
        add(NVItems.VENGEFUL_CRYSTAL.get(), "Vengeful Spiritus Crystal");
        add(NVItems.STEADFAST_CRYSTAL.get(), "Steadfast Spiritus Crystal");
        add(NVItems.SPIRITUS_GAUGE.get(), "Spiritus Aura Gauge");
        addTooltip("spiritus_gauge", "Shows the current spiritus level in the area");

        // Crystal Catalysts
        add(NVItems.RAW_CRYSTAL_CATALYST.get(), "Raw Crystal Catalyst");
        add(NVItems.CORROSIVE_CRYSTAL_CATALYST.get(), "Corrosive Crystal Catalyst");
        add(NVItems.DESTRUCTIVE_CRYSTAL_CATALYST.get(), "Destructive Crystal Catalyst");
        add(NVItems.VENGEFUL_CRYSTAL_CATALYST.get(), "Vengeful Crystal Catalyst");
        add(NVItems.STEADFAST_CRYSTAL_CATALYST.get(), "Steadfast Crystal Catalyst");
        add("tooltip.neovitae.crystal_catalyst.desc", "Right-click a Spiritus Crystal to accelerate its growth");
        add("tooltip.neovitae.crystal_catalyst.aspect", "Aspect: %s");

        // Will Type Names
        add("will.neovitae.default", "Raw");
        add("will.neovitae.corrosive", "Corrosive");
        add("will.neovitae.destructive", "Destructive");
        add("will.neovitae.vengeful", "Vengeful");
        add("will.neovitae.steadfast", "Steadfast");

        // Sentient Tools
        add(NVItems.SENTIENT_SWORD.get(), "Sentient Sword");
        add(NVItems.SENTIENT_AXE.get(), "Sentient Axe");
        add(NVItems.SENTIENT_PICKAXE.get(), "Sentient Pickaxe");
        add(NVItems.SENTIENT_SHOVEL.get(), "Sentient Shovel");
        add(NVItems.SENTIENT_SCYTHE.get(), "Sentient Scythe");
        addTooltip("sentientSword.desc", "Empowered by spiritus in your inventory");
        addTooltip("sentientAxe.desc", "Empowered by spiritus in your inventory");
        addTooltip("sentientPickaxe.desc", "Empowered by spiritus in your inventory");
        addTooltip("sentientShovel.desc", "Empowered by spiritus in your inventory");
        addTooltip("sentientScythe.desc", "Area damage empowered by spiritus");

        // Routing Items
        add(NVItems.NODE_ROUTER.get(), "Node Router");
        add(NVItems.MASTER_NODE_UPGRADE.get(), "Master Routing Node Core");
        add(NVItems.MASTER_NODE_UPGRADE_SPEED.get(), "Speed Core");
        addTooltip("noderouter.coords", "Stored Position: %d, %d, %d");
        add("chat.neovitae.routing.remove", "Stored position cleared.");
        add("chat.neovitae.routing.set", "Position stored.");
        add("chat.neovitae.routing.distance", "Nodes are too far apart! Maximum distance is 16 blocks.");
        add("chat.neovitae.routing.same", "Cannot link a node to itself!");
        add("chat.neovitae.routing.link.master", "Node linked to Master Routing Node.");
        add("chat.neovitae.routing.link", "Nodes linked together.");

        // Throwing Daggers
        add(NVItems.THROWING_DAGGER.get(), "Throwing Dagger");
        add(NVItems.THROWING_DAGGER_AMETHYST.get(), "Amethyst Throwing Dagger");
        add(NVItems.THROWING_DAGGER_SYRINGE.get(), "Syringe Throwing Dagger");
        add(NVItems.THROWING_DAGGER_TIPPED.get(), "Tipped Throwing Dagger");
        add("tooltip.neovitae.throwing_dagger.desc", "Throw at enemies for damage");
        add("entity.neovitae.throwing_dagger", "Throwing Dagger");
        add("entity.neovitae.throwing_dagger_syringe", "Syringe Throwing Dagger");
        add("entity.neovitae.blood_shield", "Sanguine Ward");

        // Daemonium Ignis
        add("entity.neovitae.daemonium_ignis", "Daemonium Ignis");
        add(NVItems.DAEMONIUM_IGNIS_SPAWN_EGG.get(), "Daemonium Ignis Spawn Egg");

        // Daemonium Glaciaris
        add("entity.neovitae.daemonium_glaciaris", "Daemonium Glaciaris");
        add(NVItems.DAEMONIUM_GLACIARIS_SPAWN_EGG.get(), "Daemonium Glaciaris Spawn Egg");

        // Misc WIP Items
        add(NVItems.ANIMATED_SPIRITUS.get(), "Animated Spiritus");

        // Simple Recipe Ingredients
        add(NVItems.SULFUR.get(), "Sulfur");
        add(NVItems.SALTPETER.get(), "Saltpeter");
        add(NVItems.PLANT_OIL.get(), "Plant Oil");
        add(NVItems.HELLFORGED_INGOT.get(), "Hellforged Ingot");

        // Explosive Charges
        add(NVBlocks.SHAPED_CHARGE, "Shaped Charge");
        add(NVBlocks.DEFORESTER_CHARGE, "Deforester Charge");
        add(NVBlocks.VEINMINE_CHARGE, "Veinmine Charge");
        add(NVBlocks.FUNGAL_CHARGE, "Fungal Charge");
        add(NVBlocks.AUG_SHAPED_CHARGE, "Augmented Shaped Charge");
        add(NVBlocks.DEFORESTER_CHARGE_2, "Reinforced Deforester Charge");
        add(NVBlocks.VEINMINE_CHARGE_2, "Reinforced Veinmine Charge");
        add(NVBlocks.FUNGAL_CHARGE_2, "Reinforced Fungal Charge");
        add(NVBlocks.SHAPED_CHARGE_DEEP, "Deep Shaped Charge");

        // Mimic Block
        add(NVBlocks.MIMIC, "Mimic");
        add(NVBlocks.ETHEREAL_MIMIC, "Ethereal Mimic");
        add(NVBlocks.INVERSION_PILLAR, "Inversion Pillar");
        add(NVBlocks.INVERSION_PILLAR_CAP, "Inversion Pillar Cap");

        add(NVBlocks.SANDS_OF_VITAE, "Sands of Vitae");
        add(NVBlocks.BLOOD_STAINED_GLASS, "Blood Stained Glass");
        add(NVBlocks.BLOOD_STAINED_GLASS_PANE, "Blood Stained Glass Pane");

        // Dungeon Control Blocks
        add(NVBlocks.DUNGEON_CONTROLLER.block().get(), "Dungeon Controller");
        add(NVBlocks.DUNGEON_SEAL.block().get(), "Dungeon Seal");
        add("chat.neovitae.mimic.potionSpawnRadius.down", "Potion Spawn Radius: %d");
        add("chat.neovitae.mimic.potionSpawnRadius.up", "Potion Spawn Radius: %d");
        add("chat.neovitae.mimic.detectRadius.down", "Detection Radius: %d");
        add("chat.neovitae.mimic.detectRadius.up", "Detection Radius: %d");
        add("chat.neovitae.mimic.potionInterval.down", "Potion Interval: %d ticks");
        add("chat.neovitae.mimic.potionInterval.up", "Potion Interval: %d ticks");

        // Alchemy Flask Items
        add(NVItems.SLATE_VIAL.get(), "Slate Vial");
        add(NVItems.ALCHEMY_FLASK.get(), "Alchemy Flask");
        add(NVItems.ALCHEMY_FLASK_THROWABLE.get(), "Throwable Alchemy Flask");
        add(NVItems.ALCHEMY_FLASK_LINGERING.get(), "Lingering Alchemy Flask");

        // Blood Provider Items
        add(NVItems.SLATE_AMPOULE.get(), "Slate Ampoule");
        add("tooltip.neovitae.blood_provider.slate.desc", "A simple ampoule containing 500 EV.");

        // Anointment Items - Base tier (using 1.20.1 thematic names)
        add(NVItems.MELEE_DAMAGE_ANOINTMENT.get(), "Honing Oil");
        add(NVItems.SILK_TOUCH_ANOINTMENT.get(), "Soft Grip");
        add(NVItems.FORTUNE_ANOINTMENT.get(), "Fortuna Extract");
        add(NVItems.HOLY_WATER_ANOINTMENT.get(), "Holy Water");
        add(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT.get(), "Miner's Secrets");
        add(NVItems.QUICK_DRAW_ANOINTMENT.get(), "Dexterity Alkahest");
        add(NVItems.LOOTING_ANOINTMENT.get(), "Plunderer's Glint");
        add(NVItems.BOW_POWER_ANOINTMENT.get(), "Iron Tip");
        add(NVItems.WILL_POWER_ANOINTMENT.get(), "Will Empowerment");
        add(NVItems.SMELTING_ANOINTMENT.get(), "Slow-burning Oil");
        add(NVItems.VOIDING_ANOINTMENT.get(), "Voiding Essence");
        add(NVItems.BOW_VELOCITY_ANOINTMENT.get(), "Archer's Polish");
        add(NVItems.WEAPON_REPAIR_ANOINTMENT.get(), "Mending Balm");

        // Anointment Items - L variants (extended duration)
        add(NVItems.MELEE_DAMAGE_ANOINTMENT_L.get(), "Honing Oil L");
        add(NVItems.SILK_TOUCH_ANOINTMENT_L.get(), "Soft Grip L");
        add(NVItems.FORTUNE_ANOINTMENT_L.get(), "Fortuna Extract L");
        add(NVItems.HOLY_WATER_ANOINTMENT_L.get(), "Holy Water L");
        add(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT_L.get(), "Miner's Secrets L");
        add(NVItems.QUICK_DRAW_ANOINTMENT_L.get(), "Dexterity Alkahest L");
        add(NVItems.LOOTING_ANOINTMENT_L.get(), "Plunderer's Glint L");
        add(NVItems.BOW_POWER_ANOINTMENT_L.get(), "Iron Tip L");
        add(NVItems.SMELTING_ANOINTMENT_L.get(), "Slow-burning Oil L");
        add(NVItems.VOIDING_ANOINTMENT_L.get(), "Voiding Essence L");
        add(NVItems.BOW_VELOCITY_ANOINTMENT_L.get(), "Archer's Polish L");
        add(NVItems.WEAPON_REPAIR_ANOINTMENT_L.get(), "Mending Balm L");

        // Anointment Items - 2 variants (level 2)
        add(NVItems.MELEE_DAMAGE_ANOINTMENT_2.get(), "Honing Oil II");
        add(NVItems.FORTUNE_ANOINTMENT_2.get(), "Fortuna Extract II");
        add(NVItems.HOLY_WATER_ANOINTMENT_2.get(), "Holy Water II");
        add(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT_2.get(), "Miner's Secrets II");
        add(NVItems.QUICK_DRAW_ANOINTMENT_2.get(), "Dexterity Alkahest II");
        add(NVItems.LOOTING_ANOINTMENT_2.get(), "Plunderer's Glint II");
        add(NVItems.BOW_POWER_ANOINTMENT_2.get(), "Iron Tip II");
        add(NVItems.BOW_POWER_ANOINTMENT_STRONG.get(), "Iron Tip II");
        add(NVItems.BOW_VELOCITY_ANOINTMENT_2.get(), "Archer's Polish II");
        add(NVItems.WEAPON_REPAIR_ANOINTMENT_2.get(), "Mending Balm II");

        // Anointment Items - XL variants (extra long duration)
        add(NVItems.MELEE_DAMAGE_ANOINTMENT_XL.get(), "Honing Oil XL");
        add(NVItems.SILK_TOUCH_ANOINTMENT_XL.get(), "Soft Grip XL");
        add(NVItems.FORTUNE_ANOINTMENT_XL.get(), "Fortuna Extract XL");
        add(NVItems.HOLY_WATER_ANOINTMENT_XL.get(), "Holy Water XL");
        add(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT_XL.get(), "Miner's Secrets XL");
        add(NVItems.QUICK_DRAW_ANOINTMENT_XL.get(), "Dexterity Alkahest XL");
        add(NVItems.LOOTING_ANOINTMENT_XL.get(), "Plunderer's Glint XL");
        add(NVItems.BOW_POWER_ANOINTMENT_XL.get(), "Iron Tip XL");
        add(NVItems.SMELTING_ANOINTMENT_XL.get(), "Slow-burning Oil XL");
        add(NVItems.VOIDING_ANOINTMENT_XL.get(), "Voiding Essence XL");
        add(NVItems.BOW_VELOCITY_ANOINTMENT_XL.get(), "Archer's Polish XL");
        add(NVItems.WEAPON_REPAIR_ANOINTMENT_XL.get(), "Mending Balm XL");

        // Anointment Items - 3 variants (level 3)
        add(NVItems.MELEE_DAMAGE_ANOINTMENT_3.get(), "Honing Oil III");
        add(NVItems.FORTUNE_ANOINTMENT_3.get(), "Fortuna Extract III");
        add(NVItems.HOLY_WATER_ANOINTMENT_3.get(), "Holy Water III");
        add(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT_3.get(), "Miner's Secrets III");
        add(NVItems.QUICK_DRAW_ANOINTMENT_3.get(), "Dexterity Alkahest III");
        add(NVItems.LOOTING_ANOINTMENT_3.get(), "Plunderer's Glint III");
        add(NVItems.BOW_POWER_ANOINTMENT_3.get(), "Iron Tip III");
        add(NVItems.BOW_VELOCITY_ANOINTMENT_3.get(), "Archer's Polish III");
        add(NVItems.WEAPON_REPAIR_ANOINTMENT_3.get(), "Mending Balm III");

        // Routing/Filter Items
        add(NVItems.FRAME_PARTS.get(), "Frame Parts");
        add(NVItems.ITEM_ROUTER_FILTER.get(), "Standard Filter");
        add(NVItems.ITEM_TAG_FILTER.get(), "Tag Filter");
        add(NVItems.ITEM_ENCHANT_FILTER.get(), "Enchantment Filter");
        add(NVItems.ITEM_MOD_FILTER.get(), "Mod Filter");
        add(NVItems.ITEM_COMPOSITE_FILTER.get(), "Composite Filter");

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

        add(NVItems.BLOOD_SWEAT_AND_TEARS.get(), "Music Disc");
        add("jukebox_song.neovitae.blood_sweat_and_tears", "Saereth - Blood, Sweat & Tears");
        add(NVBlocks.RAW_DEMONITE_BLOCK, "Raw Demonite Block");

        // Alchemy Catalysts
        add(NVItems.SIMPLE_CATALYST.get(), "Simple Catalyst");
        add(NVItems.STRENGTHENED_CATALYST.get(), "Strengthened Catalyst");
        add(NVItems.CYCLING_CATALYST.get(), "Cycling Catalyst");
        add(NVItems.COMBINATIONAL_CATALYST.get(), "Combinational Catalyst");
        add(NVItems.MUNDANE_LENGTHENING_CATALYST.get(), "Mundane Lengthening Catalyst");
        add(NVItems.MUNDANE_POWER_CATALYST.get(), "Mundane Power Catalyst");
        add(NVItems.AVERAGE_LENGTHENING_CATALYST.get(), "Average Lengthening Catalyst");
        add(NVItems.AVERAGE_POWER_CATALYST.get(), "Average Power Catalyst");

        // Filling Agents
        add(NVItems.WEAK_FILLING_AGENT.get(), "Weak Filling Agent");
        add(NVItems.STANDARD_FILLING_AGENT.get(), "Standard Filling Agent");

        // Hellforged Parts
        add(NVItems.HELLFORGED_PARTS.get(), "Hellforged Parts");

        // Teleposer Block
        add(NVBlocks.TELEPOSER, "Teleposer");

        // Teleposer Focus Items
        add(NVItems.TELEPOSER_FOCUS.get(), "Teleposer Focus");
        add(NVItems.TELEPOSER_FOCUS_ENHANCED.get(), "Enhanced Teleposer Focus");
        add(NVItems.TELEPOSER_FOCUS_REINFORCED.get(), "Reinforced Teleposer Focus");
        addTooltip("telepositionfocus.coords", "Coordinates: %s, %s, %s");
        addTooltip("telepositionfocus.world", "Dimension: %s");

        // Activation Crystals
        add(NVItems.ACTIVATION_CRYSTAL_WEAK.get(), "Weak Activation Crystal");
        add(NVItems.ACTIVATION_CRYSTAL_AWAKENED.get(), "Awakened Activation Crystal");
        add(NVItems.ACTIVATION_CRYSTAL_CREATIVE.get(), "Creative Activation Crystal");
        addTooltip("activationcrystal.weak", "Activates low-level rituals.");
        addTooltip("activationcrystal.awakened", "Activates more powerful rituals.");
        addTooltip("activationcrystal.creative", "Creative Only - Activates any ritual.");

        // Inscription Tools
        add(NVItems.INSCRIPTION_TOOL_AIR.get(), "Inscription Tool: Air");
        add(NVItems.INSCRIPTION_TOOL_FIRE.get(), "Inscription Tool: Fire");
        add(NVItems.INSCRIPTION_TOOL_WATER.get(), "Inscription Tool: Water");
        add(NVItems.INSCRIPTION_TOOL_EARTH.get(), "Inscription Tool: Earth");
        add(NVItems.INSCRIPTION_TOOL_DUSK.get(), "Inscription Tool: Dusk");
        addTooltip("inscriber.desc", "The writing is on the wall...");

        // Ritual Diviners
        add(NVItems.RITUAL_DIVINER.get(), "Ritual Diviner");
        add(NVItems.RITUAL_DIVINER_DUSK.get(), "Ritual Diviner [Dusk]");
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
        add(NVItems.RITUAL_READER.get(), "Ritual Reader");
        addTooltip("reader.desc", "Used to configure ritual areas.");
        addTooltip("reader.currentState", "Mode: %s");
        addTooltip("reader.currentRange", "Range: %s");
        addTooltip("reader.state.information", "Information");
        addTooltip("reader.state.set_area_corner_1", "Set Area Corner 1");
        addTooltip("reader.state.set_area_corner_2", "Set Area Corner 2");
        addTooltip("reader.state.set_will_config", "Set Spiritus Configuration");
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
        add("chat.neovitae.imperfect.notEnoughLP", "Not enough Essentia Vitae! Requires %d EV.");
        add("chat.neovitae.imperfect.noMatch", "No imperfect ritual matches that block.");

        // Master Ritual Stone activation messages
        add("chat.neovitae.crystal.notBound", "The crystal is not bound to a player!");
        add("chat.neovitae.ritual.activated", "%s has been activated!");
        add("chat.neovitae.ritual.noMatch", "No ritual found at this location.");
        add("chat.neovitae.ritual.deactivated", "Ritual has been deactivated.");
        add("chat.neovitae.ritual.notActive", "No ritual is currently active.");

        // Ritual failure messages
        add("chat.neovitae.ritual.notEnoughLP", "Not enough Essentia Vitae! Requires %d EV.");
        add("chat.neovitae.ritual.noAnima", "You must bind an Orb of Vitae first!");
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
        add(NVItems.SIMPLE_KEY.get(), "Simple Dungeon Key");
        add(NVItems.MINE_KEY.get(), "Mine Dungeon Key");
        add(NVItems.MINE_ENTRANCE_KEY.get(), "Mine Entrance Key");
        add(NVItems.STANDARD_KEY.get(), "Standard Dungeon Key");
        add(NVItems.BOSS_KEY.get(), "Boss Key");
        add("tooltip.neovitae.dungeon_key.type", "Key Type: %s");
        add("tooltip.neovitae.dungeon_key.desc", "Use on sealed dungeon doors");

        // Dungeon Tester (debug item)
        add(NVItems.DUNGEON_TESTER.get(), "Dungeon Tester");

        // Ritual activation status messages
        add("ritual.neovitae.crystalLevel.insufficient", "Crystal tier is too low to activate this ritual.");
        add("ritual.neovitae.structure.invalid", "Ritual structure is incomplete or invalid.");
        add("ritual.neovitae.activation.insufficient", "Not enough Essentia Vitae to activate this ritual.");
        add("ritual.neovitae.offset.info", "Offset: X=%d, Y=%d, Z=%d");

        // Tau Oil
        add(NVItems.TAU_OIL.get(), "Tau Oil");

        // Anointments (using 1.20.1 thematic names)
        addAnointment("melee_damage", "Whetstone");
        addAnointment("silk_touch", "Soft Touch");
        addAnointment("fortune", "Fortunate");
        addAnointment("holy_water", "Holy Light");
        addAnointment("hidden_knowledge", "Miner's Secrets");
        addAnointment("quick_draw", "Deft Hands");
        addAnointment("looting", "Plundering");
        addAnointment("bow_power", "Heavy Shot");
        addAnointment("will_power", "Spiritus Drain");
        addAnointment("smelting", "Heated Tool");
        addAnointment("voiding", "Voiding");
        addAnointment("bow_velocity", "Sniping");
        addAnointment("repairing", "Regular Maintenance");

        // Anointment tooltips
        addTooltip("anointment.level", "Level: %s");
        addTooltip("anointment.uses", "Uses: %s");
        addTooltip("anointment.shift_for_details", "Hold Shift for details");
        addTooltip("anointment.melee_damage.desc", "Increases melee damage dealt");
        addTooltip("anointment.silk_touch.desc", "Harvests blocks with Silk Touch");
        addTooltip("anointment.fortune.desc", "Increases block drops (Fortune)");
        addTooltip("anointment.holy_water.desc", "Deals extra damage to undead");
        addTooltip("anointment.hidden_knowledge.desc", "Increases XP from mining blocks");
        addTooltip("anointment.quick_draw.desc", "Decreases bow draw time");
        addTooltip("anointment.looting.desc", "Increases mob drops (Looting)");
        addTooltip("anointment.bow_power.desc", "Increases arrow damage");
        addTooltip("anointment.will_power.desc", "Arrows drain Spiritus on hit");
        addTooltip("anointment.smelting.desc", "Auto-smelts drops from mining");
        addTooltip("anointment.voiding.desc", "Destroys unwanted drops from mining");
        addTooltip("anointment.bow_velocity.desc", "Increases arrow velocity");
        addTooltip("anointment.repairing.desc", "Repairs weapon using XP on hit");

        // Athanor Items
        add(NVItems.BASIC_CUTTING_FLUID.get(), "Basic Cutting Fluid");
        add(NVItems.INTERMEDIATE_CUTTING_FLUID.get(), "Intermediate Cutting Fluid");
        add(NVItems.ADVANCED_CUTTING_FLUID.get(), "Advanced Cutting Fluid");
        add(NVItems.EXPLOSIVE_POWDER.get(), "Explosive Powder");
        add(NVItems.RESONATOR.get(), "Crystal Resonator");
        add(NVItems.PRIMITIVE_CRYSTALLINE_RESONATOR.get(), "Reinforced Resonator");
        add(NVItems.HELLFORGED_RESONATOR.get(), "Hellforged Resonator");
        add(NVItems.PRIMITIVE_FURNACE_CELL.get(), "Primitive Fuel Cell");
        add(NVItems.PRIMITIVE_HYDRATION_CELL.get(), "Primitive Hydration Cell");
        add(NVItems.PRIMITIVE_EXPLOSIVE_CELL.get(), "Reinforced Explosive Cell");
        add(NVItems.HELLFORGED_EXPLOSIVE_CELL.get(), "Hellforged Explosive Cell");
        add(NVItems.SANGUINE_REVERTER.get(), "Sanguine Reverter");
        add(NVItems.GUIDE_BOOK.get(), "Scriptura Vitae");

        // Dagger tooltips
        addTooltip("sacrificial_dagger.desc", "The Gift of Vitae");
        addTooltip("dagger_of_sacrifice.desc", "Theft of Vitae");

        // Rune tooltips
        addTooltip("rune.blank", "A basic rune with no special effect");
        addTooltip("rune.speed", "Increases Ara Vitae crafting speed");
        addTooltip("rune.sacrifice", "Increases EV gained from mob sacrifice");
        addTooltip("rune.self_sacrifice", "Increases EV gained from self-sacrifice");
        addTooltip("rune.capacity", "Increases Ara Vitae EV capacity");
        addTooltip("rune.capacity_augmented", "Multiplicative increase to Ara Vitae EV capacity");
        addTooltip("rune.dislocation", "Increases fluid transfer rate to/from the Ara Vitae");
        addTooltip("rune.orb", "Increases Orb of Vitae capacity while in the Ara Vitae");
        addTooltip("rune.charging", "Pre-charges EV for faster crafting");
        addTooltip("rune.acceleration", "Reduces ticks between Ara Vitae operations");
        addTooltip("rune.efficiency", "Reduces EV loss when the Ara Vitae runs out mid-craft");
        addTooltip("rune.reinforced", "Reinforced: double the effect of the base rune");

        // Flask tooltips
        addTooltip("flask.combination", "Combination potion - see the Scriptura Vitae for details");

        // Athanor Tool tooltips
        addTooltip("arctool.uses", "Uses Remaining: %s");
        addTooltip("arctool.craftspeed", "Crafting Speed: %sx");
        addTooltip("arctool.additionaldrops", "Additional Output Chance: %sx");

        // Ore Processing Items
        add(NVItems.IRON_FRAGMENT.get(), "Iron Fragment");
        add(NVItems.IRON_GRAVEL.get(), "Iron Gravel");
        add(NVItems.IRON_DUST.get(), "Iron Dust");
        add(NVItems.GOLD_FRAGMENT.get(), "Gold Fragment");
        add(NVItems.GOLD_GRAVEL.get(), "Gold Gravel");
        add(NVItems.GOLD_DUST.get(), "Gold Dust");
        add(NVItems.COPPER_FRAGMENT.get(), "Copper Fragment");
        add(NVItems.COPPER_GRAVEL.get(), "Copper Gravel");
        add(NVItems.COPPER_DUST.get(), "Copper Dust");
        add(NVItems.COAL_DUST.get(), "Coal Dust");
        add(NVItems.DEMONITE_RAW.get(), "Raw Demonite");
        add(NVItems.DEMONITE_FRAGMENT.get(), "Demonite Fragment");
        add(NVItems.DEMONITE_GRAVEL.get(), "Demonite Gravel");
        add(NVItems.NETHERITE_SCRAP_FRAGMENT.get(), "Ancient Debris Fragment");
        add(NVItems.NETHERITE_SCRAP_GRAVEL.get(), "Ancient Debris Gravel");
        add(NVItems.NETHERITE_SCRAP_DUST.get(), "Netherite Scrap Dust");
        add(NVItems.HELLFORGED_DUST.get(), "Hellforged Dust");
        add(NVItems.CORRUPTED_DUST.get(), "Corrupted Dust");
        add(NVItems.CORRUPTED_DUST_TINY.get(), "Tiny Corrupted Dust");

        addTooltip("will", "Spiritus: %s");
        for (SpiritusType type : SpiritusType.values()) {
            addTooltip("current_type." + type.getSerializedName(), String.format("Contains: %s Spiritus", type.toCapitalized()));
        }
        add("item_group.neovitae.main", "Neo Vitae");
        add("item_group.neovitae.tomes", "Neo Vitae Upgrade Tomes");
        add("item_group.neovitae.trainers", "Neo Vitae Trainer Tomes");

        // Blood light messages
        add("tooltip.neovitae.sigil.blood_light.brightness", "Light Level: %s");
        add("tooltip.neovitae.sigil.blood_light.color", "Color: %s");
        add("tooltip.neovitae.sigil.blood_light.rainbow", "Rainbow");
        add("message.neovitae.blood_light.brightness", "Blood Light: Brightness %s");
        add("message.neovitae.blood_light.redstone_on", "Redstone Control: Enabled");
        add("message.neovitae.blood_light.redstone_off", "Redstone Control: Disabled");
        add("message.neovitae.sigil.blood_light.brightness", "Sigil Brightness: %s");

        // Material generation messages
        add("message.neovitae.materials.generated", "[Neo Vitae] New ore materials have been detected and added to the config.");
        add("message.neovitae.materials.restart_required", "[Neo Vitae] A game restart is required for the new material items to appear.");
        add("command.neovitae.generate.scanning", "Scanning c:ores tags...");
        add("command.neovitae.generate.no_new", "No new ore materials found. %s already configured.");
        add("command.neovitae.generate.added", "Added %s new materials: %s");
        add("command.neovitae.generate.skipped", "%s ore types already configured or skipped.");
        add("command.neovitae.generate.restart", "Restart the game for new items to appear.");
        add("command.neovitae.setorbfill.success", "Set orb fill to %s / %s mB");
        add("command.neovitae.setorbfill.not_orb", "You must be holding an Orb of Vitae");

        add(NVItems.LIVING_HELMET.get(), "Living Helmet");
        add(NVItems.LIVING_PLATE.get(), "Living Plate");
        add(NVItems.LIVING_LEGGINGS.get(), "Living Leggings");
        add(NVItems.LIVING_BOOTS.get(), "Living Boots");
        add(NVItems.UPGRADE_TOME.get(), "Upgrade Tome");
        add(NVItems.EXPERIENCE_TOME.get(), "Tome of Peritia");
        addTooltip("experience_tome.stored", "Stored XP: %s");
        addTooltip("experience_tome.sneak_use", "Sneak + Use: Store XP");
        addTooltip("experience_tome.use", "Use: Retrieve XP");

        add(NVItems.UPGRADE_SCRAP.get(), "Upgrade Tome Scrap");
        add(NVItems.SYNTHETIC_POINT.get(), "Synthetic Upgrade Points");
        addTooltip("scrap", "Contained Upgrade Points: %s");

        add(NVItems.TRAINING_BRACELET.get(), "Living Training Bracelet");
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
        addCommand("ritual.info.refresh_cost", "Refresh Cost: %d EV");
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
        addCommand("imperfect_ritual.failed", "Imperfect ritual %s failed to activate (insufficient EV?).");
        addCommand("imperfect_ritual.placed", "Placed block for imperfect ritual %s: %s");
        addCommand("imperfect_ritual.list.header", "=== Available Imperfect Rituals ===");

        addTooltip("upgrade_points", "Upgrade Points: %s/%s");

        // Sigil descriptions
        addTooltip("sigil.divination.desc", "Reveals the state of an Ara Vitae or your Anima");
        addTooltip("sigil.seer.desc", "Reveals detailed knowledge of an Ara Vitae or your Anima");
        addTooltip("sigil.air.desc", "Launches you into the air");
        addTooltip("sigil.blood_light.desc", "Creates a configurable colored light source");
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
        addTooltip("sigil.divination.currentEV", "Current Essentia Vitae: %s");
        addTooltip("sigil.divination.currentAltarCapacity", "Altar Capacity: %s EV");
        addTooltip("sigil.divination.currentNetworkLP", "Anima: %s EV");
        addTooltip("sigil.divination.otherNetwork", "Viewing network of: %s");
        addTooltip("sigil.seer.currentAltarTier", "Current Altar Tier: %s");
        addTooltip("sigil.seer.currentEV", "Current Essentia Vitae: %s");
        addTooltip("sigil.seer.currentAltarCapacity", "Altar Capacity: %s EV");
        addTooltip("sigil.seer.otherNetwork", "Viewing network of: %s");
        addTooltip("sigil.seer.currentAltarProgress", "Crafting Progress: %s%%");
        addTooltip("sigil.seer.currentAltarConsumption", "Consumption Rate: %s EV/t");

        // Creative mode detailed altar stats
        addTooltip("sigil.divination.creative.capacityMod", "Capacity Multiplier: %sx");
        addTooltip("sigil.divination.creative.speedMod", "Speed Bonus: +%s");
        addTooltip("sigil.divination.creative.tickRate", "Tick Rate: %s ticks");
        addTooltip("sigil.divination.creative.sacrificeMod", "Sacrifice Bonus: +%s");
        addTooltip("sigil.divination.creative.selfSacMod", "Self-Sacrifice Bonus: +%s");
        addTooltip("sigil.divination.creative.dislocationMod", "Dislocation Multiplier: %sx");
        addTooltip("sigil.divination.creative.orbCapMod", "Orb Capacity Bonus: +%s");
        addTooltip("sigil.divination.creative.efficiencyMod", "Efficiency: %sx");
        addTooltip("sigil.divination.creative.chargingRate", "Charging Rate: %s EV/tick");
        addTooltip("sigil.divination.creative.incense", "Incense: %s");

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
        addJei("recipe.altar", "Ara Vitae");
        addJei("recipe.hellfire_forge", "Hellfire Forge");
        addJei("recipe.alchemyarraycrafting", "Alchemy Array");
        addJei("recipe.tabulavitae", "Tabula Vitae");
        addJei("recipe.requiredtier", "Required Tier: %s");
        addJei("recipe.requiredlp", "Required Essentia Vitae: %s");
        addJei("recipe.consumptionrate", "Consumption Rate: %s EV/t");
        addJei("recipe.drainrate", "Drain Rate: %s EV/t");
        addJei("recipe.componentTransfer", "Preserves Components");
        addJei("recipe.minimumsouls", "Minimum Spiritus: %s");
        addJei("recipe.soulsdrained", "Spiritus Drained: %s");
        addJei("recipe.will", "Spiritus");
        addJei("recipe.info", "Hover for info");
        addJei("recipe.lp", "EV");
        addJei("recipe.lpDrained", "EV Drained: %s");
        addJei("recipe.ticksRequired", "Ticks: %s");
        addJei("recipe.meteor", "Meteor Ritual");
        addJei("recipe.meteor.fill", "Fill Block");
        addJei("recipe.meteor.weight", "Weight: %s");
        addJei("recipe.meteor.estimate", "Est: %s blocks (~%s%%)");
        addJei("recipe.arc", "Athanor");
        addJei("recipe.athanor.chance", "Chance: %s%%");
        addJei("recipe.flask", "Flask Brewing");
        addJei("recipe.flask_combination", "Flask Combinations");
        addJei("recipe.imperfect_ritual", "Imperfect Ritual");
        addJei("recipe.ritual", "Ritual");

        // Ritual JEI category
        addJei("recipe.ritual.activation", "Activation Cost:");
        addJei("recipe.ritual.refresh", "Refresh Cost:");
        addJei("recipe.ritual.total_runes", "Total Runes: %s");
        addJei("recipe.ritual.crystal.weak", "Tier: Weak");
        addJei("recipe.ritual.crystal.awakened", "Tier: Awakened");
        addJei("recipe.ritual.crystal.creative", "Tier: Creative");

        // Jade integration
        add("config.jade.plugin_neovitae.block_info", "Neo Vitae Block Info");

        // Blood Tank
        add("jei.neovitae.blood_tank.upgrade_info", "Blood Tanks can be upgraded by placing them in a crafting grid surrounded by Glass and Bloodstone. Each upgrade doubles the tank's capacity. Tanks retain their stored fluid when upgraded. The initial tier holds 16,000 mB and tier 16 holds 524,288,000 mB.");

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
        addJei("effect.elevator.name", "Teleposition Array");
        addJei("effect.elevator.desc", "Teleports you to aligned arrays above or below");

        // Array effect dummy items (JEI searchable)
        add(NVItems.ARRAY_BOUNCE.get(), "Bounce Array");
        add(NVItems.ARRAY_SPIKE.get(), "Spike Array");
        add(NVItems.ARRAY_UPDRAFT.get(), "Updraft Array");
        add(NVItems.ARRAY_MOVEMENT.get(), "Movement Array");
        add(NVItems.ARRAY_DAY.get(), "Sunrise Array");
        add(NVItems.ARRAY_NIGHT.get(), "Moonrise Array");
        add(NVItems.ARRAY_ELEVATOR.get(), "Teleposition Array");
        addTooltip("array_effect.bounce", "Bounces entities high into the air. Crouch to disable.");
        addTooltip("array_effect.spike", "Damages any entity that steps on the array.");
        addTooltip("array_effect.updraft", "Launches entities upward with a gust of wind.");
        addTooltip("array_effect.movement", "Accelerates entities in the direction the array faces.");
        addTooltip("array_effect.day", "Advances the time to dawn. Consumed on use.");
        addTooltip("array_effect.night", "Advances the time to night. Consumed on use.");
        addTooltip("array_effect.elevator", "Jump to teleport up, crouch to teleport down. Searches up to 64 blocks.");

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
        add("effect.neovitae.sacrificiallamb", "Sacrificial Lamb");
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

        // Advancements
        addAdvancement("root", "Neo Vitae", "Obtain an Ara Vitae");
        addAdvancement("weak_blood_orb", "Novicius Orb of Vitae", "Craft your first Orb of Vitae");
        addAdvancement("apprentice_blood_orb", "Discipulus Orb of Vitae", "Upgrade to a Tier 1 Orb of Vitae");
        addAdvancement("magician_blood_orb", "Veneficus Orb of Vitae", "Upgrade to a Tier 2 Orb of Vitae");
        addAdvancement("master_blood_orb", "Magus Orb of Vitae", "Upgrade to a Tier 3 Orb of Vitae");
        addAdvancement("archmage_blood_orb", "Dominus Orb of Vitae", "Upgrade to a Tier 4 Orb of Vitae");
        addAdvancement("transcendent_blood_orb", "Divinus Orb of Vitae", "Achieve the ultimate Orb of Vitae");
        addAdvancement("blank_slate", "Blank Slate", "Inscribe your first slate");
        addAdvancement("reinforced_slate", "Reinforced Slate", "Craft a Reinforced Slate");
        addAdvancement("imbued_slate", "Imbued Slate", "Craft an Imbued Slate");
        addAdvancement("demonic_slate", "Demonic Slate", "Craft a Demonic Slate");
        addAdvancement("ethereal_slate", "Ethereal Slate", "Craft an Ethereal Slate");
        addAdvancement("tabula_vitae", "The Alchemy Table", "Craft a Tabula Vitae");
        addAdvancement("athanor", "Industrial Alchemy", "Craft an Athanor");
        addAdvancement("incense_altar", "Sacred Incense", "Craft an Incense Altar");
        addAdvancement("first_sigil", "First Sigil", "Craft your first sigil");
        addAdvancement("ritual_diviner", "Ritual Diviner", "Craft a Ritual Diviner");
        addAdvancement("master_ritual_stone", "Ritual Master", "Place a Master Ritual Stone");
        addAdvancement("imperfect_ritual", "Imperfect Beginnings", "Activate an Imperfect Ritual");
        addAdvancement("first_ritual", "Ritual Awakening", "Activate your first ritual");
        addAdvancement("well_of_suffering", "Well of Suffering", "Activate the Well of Suffering");
        addAdvancement("edge_of_hidden_realm", "Edge of the Hidden Realm", "Venture into the Endless Realm");
        addAdvancement("crack_of_fractured_crystal", "Crack of the Fractured Crystal", "Activate the Resonance of the Faceted Crystal");
        addAdvancement("serenade_of_nether", "Serenade of the Nether", "Activate the Serenade of the Nether");
        addAdvancement("master_of_ceremonies", "Master of Ceremonies", "Complete all ritual achievements");
        addAdvancement("meteor", "METEO!", "Summon the Mark of the Falling Tower");
        addAdvancement("teleposer", "Displacement", "Craft a Teleposer");
        addAdvancement("spiritus_snare", "Soul Snare", "Craft a Soul Snare");
        addAdvancement("spiritus", "First Spiritus", "Obtain raw Spiritus");
        addAdvancement("spiritus_gem_petty", "Petty Spiritus Gem", "Craft a Petty Spiritus Gem");
        addAdvancement("spiritus_gem_lesser", "Lesser Spiritus Gem", "Upgrade to a Lesser Gem");
        addAdvancement("spiritus_gem_common", "Common Spiritus Gem", "Upgrade to a Common Gem");
        addAdvancement("spiritus_gem_greater", "Greater Spiritus Gem", "Upgrade to a Greater Gem");
        addAdvancement("spiritus_gem_grand", "Grand Spiritus Gem", "Upgrade to a Grand Gem");
        addAdvancement("sentient_sword", "Sentient Blade", "Craft a Sentient Sword");
        addAdvancement("hellfire_forge", "Hellfire Forge", "Craft a Hellfire Forge");
        addAdvancement("vas_maleficum", "Vas Maleficum", "Craft a Vas Maleficum");
        addAdvancement("living_armor", "Living Armor", "Craft Living Armor");
        addAdvancement("self_sacrifice", "Blood Pact", "Craft a Sacrificial Dagger");
        addAdvancement("sacrifice", "The Greater Sacrifice", "Craft a Dagger of Sacrifice");
        addAdvancement("blood_sweat_and_tears", "Blood, Sweat & Tears", "Craft the legendary record at a Tier 6 altar");
    }

    public void addRitual(String key, String name) {
        add("ritual.neovitae." + key, name);
        add("ritual.neovitae." + key + ".info", "A NeoVitae ritual.");
    }

    public void addCommand(String key, String value) {
        add("commands.neovitae." + key, value);
    }

    public void addGemDesc(DeferredHolder holder, String desc) {
        addTooltip("spiritus_gem." + holder.getId().getPath(), String.format("A gem used to contain %s Spiritus.", desc));
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
