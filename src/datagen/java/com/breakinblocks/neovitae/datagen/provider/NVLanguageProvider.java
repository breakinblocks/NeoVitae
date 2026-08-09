package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconLanguageProvider;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonVariant;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.datagen.content.SentientUpgrades;
import com.breakinblocks.neovitae.util.helper.BlockWithItemHolder;

public class NVLanguageProvider extends LanguageProvider implements ModonomiconLanguageProvider {

    private final LanguageProviderCache langCache;

    public NVLanguageProvider(PackOutput output, LanguageProviderCache langCache) {
        super(output, NeoVitae.MODID, "en_us");
        this.langCache = langCache;
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
        addTooltip("orb.anima_max", "Raises Anima Maximum to %s");

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
        add(NVBlocks.SPIRITUS_RUINA_CRYSTAL, "Spiritus Ruina Crystal Cluster");
        add(NVBlocks.SPIRITUS_NIHILUM_CRYSTAL, "Spiritus Nihilum Crystal Cluster");
        add(NVBlocks.SPIRITUS_VINDICTA_CRYSTAL, "Spiritus Vindicta Crystal Cluster");
        add(NVBlocks.SPIRITUS_INVICTUS_CRYSTAL, "Spiritus Invictus Crystal Cluster");

        // Routing Node Blocks
        add(NVBlocks.ROUTING_CONDUIT, "Routing Conduit");
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
        add("menu.neovitae.athanor", "Athanor");
        add("menu.neovitae.teleposer", "Teleposer");

        add(NVBlocks.BLOOD_LANTERN, "Blood Lantern");
        add(NVBlocks.DEMON_LANTERN, "Demon Lantern");
        addTooltip("blood_lantern.desc", "Wards a 16-block radius against passive and ambient mob spawns.");

        add("gui.neovitae.tabula_vitae.side_button", "%s side of %s");
        add("book.neovitae.altar_tier_info.header.tier", "Tier");
        add("book.neovitae.altar_tier_info.header.runes", "Runes");
        add("gui.neovitae.tabula_vitae.side_allowed", "Allowed");
        add("gui.neovitae.tabula_vitae.side_blocked", "Blocked");
        add("gui.neovitae.tabula_vitae.side_hint", "Click to toggle hopper/pipe access");
        add("gui.neovitae.tabula_vitae.active_slot", "Configuring: %s");
        add("gui.neovitae.tabula_vitae.active_hint", "Click an empty slot again to deselect");
        add("gui.neovitae.tabula_vitae.slot.input", "Input slot %s");
        add("gui.neovitae.tabula_vitae.slot.orb", "Orb slot");
        add("gui.neovitae.tabula_vitae.slot.output", "Output slot");
        add("gui.neovitae.side.down", "Bottom");
        add("gui.neovitae.side.up", "Top");
        add("gui.neovitae.side.north", "North");
        add("gui.neovitae.side.south", "South");
        add("gui.neovitae.side.west", "West");
        add("gui.neovitae.side.east", "East");

        add(NVBlocks.BLOOD_TANK, "Blood Tank");
        addTooltip("container_tier_missing", "No Tier found!");
        addTooltip("container_tier", "Current Tier: %s");
        addTooltip("fluid_content_empty", "Empty");
        addTooltip("fluid_content", "Contains: %smB of %s");

        add(NVBlocks.BLOOD_BATTERY, "Blood Battery");
        addTooltip("blood_battery.capacity", "Capacity: %s FE");
        addTooltip("blood_battery.creative", "Creative Only");

        add(NVBlocks.HELLFIRE_FORGE, "Hellfire Forge");
        add(NVItems.RAW_SPIRITUS.get(), "Raw Spiritus");

        // Spiritus Essence (dropped from mobs with sentient weapons)
        add(NVItems.MONSTER_SOUL_RAW.get(), "Spiritus Essence");
        add(NVItems.MONSTER_SOUL_RUINA.get(), "Spiritus Ruina Essence");
        add(NVItems.MONSTER_SOUL_NIHILUM.get(), "Spiritus Nihilum Essence");
        add(NVItems.MONSTER_SOUL_VINDICTA.get(), "Spiritus Vindicta Essence");
        add(NVItems.MONSTER_SOUL_INVICTUS.get(), "Spiritus Invictus Essence");

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
        add(NVItems.TABULA_RASA.get(), "Tabula Rasa");
        add(NVItems.TABULA_ROBUR.get(), "Tabula Robur");
        add(NVItems.TABULA_ANIMATA.get(), "Tabula Animata");
        add(NVItems.TABULA_SPIRITUS.get(), "Tabula Spiritus");
        add(NVItems.TABULA_AETHEREA.get(), "Tabula Aetherea");

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
        add(NVItems.SIGIL_NECROMANCY.get(), "Sigil of Necromancy");
        add(NVItems.SIGIL_BOUND_TREASURES.get(), "Sigil of Bound Treasures");
        add("tooltip.neovitae.enchantment_amplifier", "Can act as an Enchanting Table amplifier");
        add("tooltip.neovitae.bound_treasures.linked", "Container linked");
        add("tooltip.neovitae.bound_treasures.not_linked", "No container linked. Shift right-click a container to bind.");
        add("tooltip.neovitae.bound_treasures.unloaded", "Linked container is in an unloaded area");
        add("tooltip.neovitae.bound_treasures.missing", "Linked container no longer exists");

        // Alchemy & Misc
        add(NVItems.ARCANE_SCRIBE_TOOL.get(), "Arcane Scribe Tool");
        addTooltip("arcane_scribe_tool", "Inscribes an alchemy array when used on a surface");
        add("tooltip.neovitae.arcane_scribe_tool.color", "Color: %s");

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

        add(NVItems.WEAK_BLOOD_SHARD.get(), "Weak Blood Shard");

        add(NVItems.LAVA_CRYSTAL.get(), "Lava Crystal");
        addTooltip("lavaCrystal.desc", "Place fire, bindable furnace fuel");
        add("chat.neovitae.notEnoughLP", "Not enough Essentia Vitae!");

        // Crystal Items
        add(NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get(), "Spiritus Crystal");
        add(NVItems.SPIRITUS_RUINA_CRYSTAL_ITEM.get(), "Spiritus Ruina Crystal");
        add(NVItems.SPIRITUS_NIHILUM_CRYSTAL_ITEM.get(), "Spiritus Nihilum Crystal");
        add(NVItems.SPIRITUS_VINDICTA_CRYSTAL_ITEM.get(), "Spiritus Vindicta Crystal");
        add(NVItems.SPIRITUS_INVICTUS_CRYSTAL_ITEM.get(), "Spiritus Invictus Crystal");
        add(NVItems.SPIRITUS_GAUGE.get(), "Spiritus Aura Gauge");
        addTooltip("spiritus_gauge", "Shows the current spiritus level in the area");

        // Crystal Catalysts
        add(NVItems.RAW_SPIRITUS_CATALYST.get(), "Raw Crystal Catalyst");
        add(NVItems.SPIRITUS_RUINA_CATALYST.get(), "Spiritus Ruina Catalyst");
        add(NVItems.SPIRITUS_NIHILUM_CATALYST.get(), "Spiritus Nihilum Catalyst");
        add(NVItems.SPIRITUS_VINDICTA_CATALYST.get(), "Spiritus Vindicta Catalyst");
        add(NVItems.SPIRITUS_INVICTUS_CATALYST.get(), "Spiritus Invictus Catalyst");
        add("tooltip.neovitae.crystal_catalyst.desc", "Right-click a same-aspect Spiritus Crystal to accelerate its growth, or a fully-grown Raw cluster to transmute it (consumes one Animus Mote)");
        add("tooltip.neovitae.crystal_catalyst.aspect", "Aspect: %s");
        add("chat.neovitae.crystal_catalyst.notMature", "The Raw cluster must be fully grown before it can be transmuted");
        add("chat.neovitae.crystal_catalyst.needsAnimus", "Transmutation requires one Animus Mote in your inventory");
        add("tooltip.neovitae.blood_mending", "Enchanted with Blood Mending");
        add("tooltip.neovitae.spiritus_stored", "Spiritus: %s / %s");

        // Spiritus Aspect Names
        add("spiritus.neovitae.raw", "Raw Spiritus");
        add("spiritus.neovitae.ruina", "Spiritus Ruina");
        add("spiritus.neovitae.nihilum", "Spiritus Nihilum");
        add("spiritus.neovitae.vindicta", "Spiritus Vindicta");
        add("spiritus.neovitae.invictus", "Spiritus Invictus");

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

        add(NVItems.LEX_VITAE.get(), "Lex Vitae");
        addTooltip("lexVitae.desc", "Sentient multitool: chops, mines, digs, tills. Sneak-right-click to toggle. Sneak+scroll to set mining radius. The Cycle Mode key switches the beam between mining, damage, and both. Mined blocks are sent straight to your inventory.");
        addTooltip("lexVitae.dormant", "Dormant");
        addTooltip("lexVitae.active", "Active");
        addTooltip("lexVitae.radius", "Radius: %sx%s");
        addTooltip("lexVitae.mode", "Mode: %s");
        addTooltip("lexVitae.mode.both", "Mining & Damage");
        addTooltip("lexVitae.mode.mining", "Mining");
        addTooltip("lexVitae.mode.damaging", "Damage");
        add("message.neovitae.lex_vitae.radius", "Lex Vitae mining radius: %sx%s");

        // Spiritus type tooltip header + per-type display names (color-coded at runtime)
        addTooltip("spiritus.type", "Attuned: %s");
        addTooltip("spiritus.raw", "Raw");
        addTooltip("spiritus.ruina", "Spiritus Ruina");
        addTooltip("spiritus.nihilum", "Spiritus Nihilum");
        addTooltip("spiritus.vindicta", "Spiritus Vindicta");
        addTooltip("spiritus.invictus", "Spiritus Invictus");

        // Spiritus headline numerics + Shift expand
        addTooltip("spiritus.level", "Level %s (%s will)");
        addTooltip("spiritus.damage_bonus", "+%s bonus damage");
        addTooltip("spiritus.mining_speed", "+%s mining speed");
        addTooltip("spiritus.aoe_radius", "%s block AoE radius");
        addTooltip("spiritus.hold_shift", "Hold Shift for details");

        // Universal riders for type-specific effects with computed numerics
        addTooltip("spiritus.rider.ruina", "Wither %s for %ss on hit");
        addTooltip("spiritus.rider.ruina.inactive", "Insufficient Spiritus Ruina to inflict Wither");
        addTooltip("spiritus.rider.invictus", "Absorption hearts for %ss on kill");
        addTooltip("spiritus.rider.invictus.inactive", "Insufficient Spiritus Invictus to grant Absorption");

        // Per-tool riders for type-specific behavior without universal numerics
        addTooltip("sentientSword.rider.raw", "Damage scales with stored Raw spiritus");
        addTooltip("sentientSword.rider.nihilum", "Heavy damage scaling; slower attack speed");
        addTooltip("sentientSword.rider.vindicta", "Faster attacks and bonus movement speed");

        addTooltip("sentientAxe.rider.raw", "Damage scales with stored Raw spiritus");
        addTooltip("sentientAxe.rider.nihilum", "Heavy damage scaling");
        addTooltip("sentientAxe.rider.vindicta", "Lighter, quicker swings");

        addTooltip("sentientPickaxe.rider.raw", "Mining speed and damage scale with Raw spiritus");
        addTooltip("sentientPickaxe.rider.nihilum", "Heavy damage when used as a weapon");
        addTooltip("sentientPickaxe.rider.vindicta", "Quicker strikes; light in hand");

        addTooltip("sentientShovel.rider.raw", "Mining speed and damage scale with Raw spiritus");
        addTooltip("sentientShovel.rider.nihilum", "Heavy damage when used as a weapon");
        addTooltip("sentientShovel.rider.vindicta", "Quicker strikes; light in hand");

        addTooltip("sentientScythe.rider.raw", "Sweeping damage scales with stored Raw spiritus");
        addTooltip("sentientScythe.rider.nihilum", "Devastating sweeping damage");
        addTooltip("sentientScythe.rider.vindicta", "Fast, lightweight sweeps");

        addTooltip("lexVitae.rider.raw", "All actions empowered by stored Raw spiritus");
        addTooltip("lexVitae.rider.nihilum", "Devastating damage and mining bonuses");
        addTooltip("lexVitae.rider.vindicta", "Faster swings and channels");

        // Routing Items
        add(NVItems.NODE_ROUTER.get(), "Node Router");
        add(NVItems.MASTER_NODE_UPGRADE.get(), "Routing Stack Upgrade");
        add(NVItems.MASTER_NODE_UPGRADE_SPEED.get(), "Routing Speed Upgrade");
        addTooltip("noderouter.coords", "Stored Position: %d, %d, %d");
        add("chat.neovitae.routing.remove", "Stored position cleared.");
        add("chat.neovitae.routing.set", "Position stored.");
        add("chat.neovitae.routing.distance", "Nodes are too far apart! Maximum distance is 16 blocks.");
        add("chat.neovitae.routing.same", "Cannot link a node to itself!");
        add("chat.neovitae.routing.link.master", "Node linked to Master Routing Node.");
        add("chat.neovitae.routing.link", "Nodes linked together.");
        add("chat.neovitae.undertow.upward", "Undertow Array: bubble column now pushes upward.");
        add("chat.neovitae.undertow.downward", "Undertow Array: bubble column now drags downward.");

        // Throwing Daggers
        add(NVItems.THROWING_DAGGER.get(), "Throwing Dagger");
        add(NVItems.THROWING_DAGGER_AMETHYST.get(), "Amethyst Throwing Dagger");
        add(NVItems.THROWING_DAGGER_SYRINGE.get(), "Syringe Throwing Dagger");
        add(NVItems.THROWING_DAGGER_TIPPED.get(), "Tipped Throwing Dagger");
        add("tooltip.neovitae.throwing_dagger.desc", "Throw at enemies for damage");
        add("entity.neovitae.throwing_dagger", "Throwing Dagger");
        add("entity.neovitae.throwing_dagger_syringe", "Syringe Throwing Dagger");
        add("entity.neovitae.blood_shield", "Sanguine Ward");
        add("entity.neovitae.blood_light", "Blood Light");

        add("entity.neovitae.necromancy_summon", "Undead Servant");
        add("entity.neovitae.necromancy_summon_husk", "Desiccated Servant");
        add("entity.neovitae.necromancy_summon_skeleton", "Skeletal Servant");
        add("entity.neovitae.necromancy_summon_stray", "Frozen Servant");

        // Slime of Vitae
        add("entity.neovitae.slime_vitae", "Slime of Vitae");
        add(NVItems.SLIME_VITAE_SPAWN_EGG.get(), "Slime of Vitae Spawn Egg");

        // Daemonium Ignis
        add("entity.neovitae.daemonium_ignis", "Daemonium Ignis");
        add(NVItems.DAEMONIUM_IGNIS_SPAWN_EGG.get(), "Daemonium Ignis Spawn Egg");

        // Daemonium Cruoris
        add("entity.neovitae.daemonium_cruoris", "Daemonium Cruoris");
        add(NVItems.DAEMONIUM_CRUORIS_SPAWN_EGG.get(), "Daemonium Cruoris Spawn Egg");

        // Daemonium Corrodis
        add("entity.neovitae.daemonium_corrodis", "Daemonium Corrodis");
        add(NVItems.DAEMONIUM_CORRODIS_SPAWN_EGG.get(), "Daemonium Corrodis Spawn Egg");

        // Daemonium Glaciaris
        add("entity.neovitae.daemonium_glaciaris", "Daemonium Glaciaris");
        add(NVItems.DAEMONIUM_GLACIARIS_SPAWN_EGG.get(), "Daemonium Glaciaris Spawn Egg");

        // Daemonium Rancoris
        add("entity.neovitae.daemonium_rancoris", "Daemonium Rancoris");
        add(NVItems.DAEMONIUM_RANCORIS_SPAWN_EGG.get(), "Daemonium Rancoris Spawn Egg");

        // Daemonium Pestis
        add("entity.neovitae.daemonium_pestis", "Daemonium Pestis");
        add(NVItems.DAEMONIUM_PESTIS_SPAWN_EGG.get(), "Daemonium Pestis Spawn Egg");

        // Daemonium Voraxis
        add("entity.neovitae.daemonium_voraxis", "Daemonium Voraxis");
        add(NVItems.DAEMONIUM_VORAXIS_SPAWN_EGG.get(), "Daemonium Voraxis Spawn Egg");

        // Daemonium Doloris
        add("entity.neovitae.daemonium_doloris", "Daemonium Doloris");
        add("entity.neovitae.daemonium_doloris.foreman", "The Foreman");
        add(NVItems.DAEMONIUM_DOLORIS_SPAWN_EGG.get(), "Daemonium Doloris Spawn Egg");

        // Daemonium Fervidis
        add("entity.neovitae.daemonium_fervidis", "Daemonium Fervidis");
        add(NVItems.DAEMONIUM_FERVIDIS_SPAWN_EGG.get(), "Daemonium Fervidis Spawn Egg");

        // Daemonium Animaris
        add("entity.neovitae.daemonium_animaris", "Daemonium Animaris");
        add(NVItems.DAEMONIUM_ANIMARIS_SPAWN_EGG.get(), "Daemonium Animaris Spawn Egg");

        // Misc WIP Items
        add(NVItems.ANIMATED_SPIRITUS.get(), "Animated Spiritus");

        // Demon drop materials
        add(NVItems.GORE_CLOTTED_FANG.get(), "Gore-Clotted Fang");
        add(NVItems.BLIGHT_MARROW.get(), "Blight Marrow");
        add(NVItems.VENOMGLAND_SAC.get(), "Venomgland Sac");
        add(NVItems.HOLLOW_GUT.get(), "Hollow Gut");
        add(NVItems.ECTOPLASMIC_RESIDUE.get(), "Ectoplasmic Residue");
        add(NVItems.ANIMUS_MOTE.get(), "Animus Mote");
        add(NVItems.REVENANT_PLATE.get(), "Revenant Plate");
        add(NVItems.FROZEN_MARROW_SHARD.get(), "Frozen Marrow Shard");
        add(NVItems.CINDER_HEART_FRAGMENT.get(), "Cinder Heart Fragment");
        add(NVItems.PERMAFROST_CORE.get(), "Permafrost Core");
        add(NVItems.DEMONITE_TRIM_INGOT.get(), "Hellforged Trim Ingot");
        add(NVItems.BLIGHT_WHETSTONE.get(), "Blight Whetstone");
        add(NVItems.TAINTED_FLESH.get(), "Tainted Flesh");
        add(NVItems.VITAE_MORSEL.get(), "Vitae Morsel");
        add(NVItems.BOTTLED_SPITE.get(), "Bottled Spite");
        add(NVItems.SIGIL_DAMNED.get(), "Sigil of the Damned");

        // Sigil of the Damned tooltips
        add("tooltip.neovitae.sigil.damned.desc", "The blood of the fallen empowers the bearer.");
        add("tooltip.neovitae.sigil.damned.spiritus", "+50 Bonus Spiritus Collection");
        add("tooltip.neovitae.sigil.damned.sacrifice", "+25 Bonus Sacrifice");
        add("tooltip.neovitae.sigil.damned.siphon", "+10 Blood Siphon (heal on kill)");

        // Trim material
        add("trim_material.neovitae.demonite", "Hellforged");

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
        add(NVBlocks.SPATIAL_RIFT, "Spatial Rift");

        add(NVBlocks.SANDS_OF_VITAE, "Sands of Vitae");
        add(NVBlocks.BLOOD_STAINED_GLASS, "Blood Stained Glass");
        add(NVBlocks.BLOOD_STAINED_GLASS_PANE, "Blood Stained Glass Pane");

        // Dungeon Control Blocks
        add(NVBlocks.DUNGEON_CONTROLLER.block().get(), "Dungeon Controller");
        add(NVBlocks.DUNGEON_SEAL.block().get(), "Dungeon Seal");
        add(NVBlocks.DUNGEON_SEAL_INACCESSIBLE.block().get(), "Spent Dungeon Seal");
        add("chat.neovitae.mimic.potionSpawnRadius.down", "Potion Spawn Radius: %d");
        add("chat.neovitae.mimic.potionSpawnRadius.up", "Potion Spawn Radius: %d");
        add("chat.neovitae.mimic.detectRadius.down", "Detection Radius: %d");
        add("chat.neovitae.mimic.detectRadius.up", "Detection Radius: %d");
        add("chat.neovitae.mimic.potionInterval.down", "Potion Interval: %d ticks");
        add("chat.neovitae.mimic.potionInterval.up", "Potion Interval: %d ticks");

        // Alchemy Flask Items
        add(NVItems.TABULA_VIAL.get(), "Tabula Vial");
        add(NVItems.ALCHEMY_FLASK.get(), "Alchemy Flask");
        add(NVItems.ALCHEMY_FLASK_THROWABLE.get(), "Throwable Alchemy Flask");
        add(NVItems.ALCHEMY_FLASK_LINGERING.get(), "Lingering Alchemy Flask");

        // Blood Provider Items
        add(NVItems.TABULA_AMPOULE.get(), "Tabula Ampoule");
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
        add(NVItems.SPIRITUS_DRAIN_ANOINTMENT.get(), "Spiritus Drain Anointment");
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

        // Routing Items
        add(NVItems.FRAME_PARTS.get(), "Frame Parts");

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

        // Spirit Cache
        add(NVBlocks.SPIRIT_CACHE, "Spirit Cache");
        add(NVBlocks.VITAE_LINK, "Vitae Link");
        add(NVBlocks.ORB_FILLING_LINK, "Orb Vitae Link");

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
        addTooltip("diviner.cycleHint", "Right-click in air to select a ritual");
        addTooltip("diviner.stat.activation", "Awakening: %s EV");
        addTooltip("diviner.stat.upkeep", "Upkeep: %s EV per cycle");
        addTooltip("diviner.stat.cycle", "Cycle: every %s ticks");
        addTooltip("diviner.stat.crystal", "Demands the %s");
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
        add("chat.neovitae.binding.bound", "Bound to %s");
        add("chat.neovitae.diviner.noRituals", "No rituals available for this diviner.");
        add("chat.neovitae.diviner.noRitualSelected", "No ritual selected. Right-click in air to select.");
        add("chat.neovitae.diviner.ritualComplete", "Ritual structure complete!");

        // Ritual Configurator
        add(NVItems.RITUAL_READER.get(), "Ritual Configurator");
        add(NVItems.RITUAL_DESIGNER.get(), "Ritual Designer");
        addTooltip("reader.desc", "Used to configure ritual areas.");
        addTooltip("reader.currentState", "Mode: %s");
        addTooltip("reader.currentRange", "Range: %s");
        addTooltip("reader.state.information", "Information");
        addTooltip("reader.state.set_area_corner_1", "Set Area Corner 1");
        addTooltip("reader.state.set_area_corner_2", "Set Area Corner 2");
        addTooltip("reader.help.1", "Right-click a Master Ritual Stone to open the configurator");
        addTooltip("reader.help.2", "Pick a working area or chest slot, then choose Edit area in world");
        addTooltip("reader.help.3", "Click two corners to set an area, or one block for a single-block slot");
        addTooltip("reader.help.4", "Sneak + right-click to cancel an area edit");
        add("chat.neovitae.reader.cancelled", "Area edit cancelled.");
        add("container.neovitae.ritual_configurator", "Ritual Configurator");
        add("gui.neovitae.configurator.aspect", "Aspect");
        add("gui.neovitae.configurator.keep", "Keep per species:");
        add("gui.neovitae.configurator.not_active", "Not yet activated");
        add("gui.neovitae.configurator.aspect.raw", "Raw");
        add("gui.neovitae.configurator.aspect.ruina", "Ruina");
        add("gui.neovitae.configurator.aspect.nihilum", "Nihl");
        add("gui.neovitae.configurator.aspect.vindicta", "Vind");
        add("gui.neovitae.configurator.aspect.invictus", "Invc");
        add("gui.neovitae.configurator.aspect.none", "Not influenced by Spiritus aspect.");
        add("gui.neovitae.configurator.edit_area", "Edit area in world");
        add("gui.neovitae.configurator.tooltip.size", "Size: %s x %s x %s");
        add("gui.neovitae.configurator.tooltip.volume", "Volume: %s / %s");
        add("gui.neovitae.configurator.tooltip.reach", "Max reach: %s wide, %s tall");
        add("gui.neovitae.configurator.tooltip.single", "Single block slot, placed with one click");
        add("gui.neovitae.configurator.tooltip.area", "Resized by clicking two corners");
        add("chat.neovitae.reader.noRitual", "No ritual active on this Master Ritual Stone.");
        add("chat.neovitae.reader.noMRS", "No active Master Ritual Stone found nearby.");
        add("chat.neovitae.reader.noRangeSelected", "No range selected. Click on an active MRS first.");
        add("chat.neovitae.reader.currentRange", "Current range: %s");
        add("chat.neovitae.reader.rangeSelected", "Range selected: %s");
        add("chat.neovitae.reader.corner1Set", "Corner 1 set at %d, %d, %d");
        add("chat.neovitae.reader.areaSet", "Area '%s' updated successfully.");
        add("chat.neovitae.reader.areaRetry", " Corner kept; click another block to try again.");
        add("chat.neovitae.reader.invalidRange", "Invalid range key.");
        add("chat.neovitae.reader.spiritusType", "Spiritus type set to: %s");
        add("chat.neovitae.gem.spawner_no_spiritus", "Not enough Spiritus. Capturing a spawner needs %s.");
        add("chat.neovitae.gem.spawner_captured", "Spawner captured.");
        add("ritual.neovitae.blockRange.noRange", "No range with that key.");
        add("ritual.neovitae.blockRange.tooBig", "Area volume exceeds limit of %d blocks.");
        add("ritual.neovitae.blockRange.tooFar", "Area extends beyond limits (vertical: %d, horizontal: %d).");

        // Imperfect Ritual Stone messages
        add("chat.neovitae.imperfect.noBlock", "Place a block above the ritual stone!");
        add("chat.neovitae.imperfect.activated", "%s activated!");
        add("chat.neovitae.imperfect.notEnoughLP", "Not enough Essentia Vitae! Requires %d EV.");
        add("chat.neovitae.imperfect.noMatch", "No imperfect ritual matches that block.");

        // Arcane Scribe Tool messages
        add("chat.neovitae.scribe.bound", "Arcane Scribe Tool bound to %s");

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
        add("container.neovitae.dungeon_seal", "Choose Your Path");
        add("container.neovitae.ritual_diviner", "Select a Ritual");
        add("chat.neovitae.dungeon.seal.opened", "The seal has been broken. A new path opens...");
        add("chat.neovitae.dungeon.seal.failed", "The seal remains firmly shut.");
        add("chat.neovitae.dungeon.seal.collapsed", "The seal dims and dies. Nothing lies beyond it.");
        add("chat.neovitae.dungeon.seal.inaccessible", "This seal is spent. The way beyond is gone.");
        add("chat.neovitae.dungeon.seal.wrongKey", "This key doesn't fit this seal.");
        add("chat.neovitae.dungeon.seal.noKeys", "You don't have any keys that fit this seal.");
        add("chat.neovitae.dungeon.threshold.mine_entrance", "Strange noises and creatures begin to stir in the depths...");
        add("chat.neovitae.dungeon.threshold.mine_key", "A monstrous roar echoes through the corridors...");
        add("chat.neovitae.dungeon.spatial_distortion", "You sense a spatial distortion in this area...");
        add("chat.neovitae.dungeon.rift_opened", "A spatial rift tears open near the exit portal...");

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
        add("ritual.neovitae.dungeon.no_space", "Not enough space: clear a %sx%sx%s area around the Master Ritual Stone.");

        add("hud.neovitae.altar.tier", "Tier: %s");
        add("hud.neovitae.altar.ev", "Altar EV: %s / %s");
        add("hud.neovitae.altar.progress", "Crafting: %s / %s");
        add("hud.neovitae.altar.inactive", "Idle");
        add("hud.neovitae.altar.consumption", "EV/tick: %s");
        add("hud.neovitae.altar.charge", "Charge: %s");
        add("hud.neovitae.incense.tranquility", "Tranquility: %s");
        add("hud.neovitae.incense.bonus", "Bonus: %s%%");
        add("gui.neovitae.hud.title", "Edit HUD Layout");
        add("gui.neovitae.hud.default", "Default");
        add("gui.neovitae.hud.save", "Save");
        add("gui.neovitae.hud.cancel", "Cancel");
        add("key.neovitae.edit_hud", "Edit HUD Layout");
        add("key.neovitae.lex_beam", "Lex Vitae: Fire Beam");
        add("key.neovitae.lex_mode", "Lex Vitae: Cycle Mode");
        add("key.neovitae.blood_shield", "Blood Shield (Orb of Vitae)");
        add("key.neovitae.open_guide", "Open Scriptura Vitae for Hovered Item");
        add("key.category.neovitae.neovitae", "Neo Vitae");
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
        addAnointment("spiritus_drain", "Spiritus Drain");
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
        addTooltip("anointment.spiritus_drain.desc", "Arrows drain Spiritus on hit");
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
        addTooltip("arctool.usage", "Used in the Athanor");
        addTooltip("arctool.usage.cutting_fluid", "Used in the Athanor and Tabula Vitae");
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
        add("item.neovitae.fragment_netherite_scrap", "Ancient Debris Fragment");
        add("item.neovitae.gravel_netherite_scrap", "Ancient Debris Gravel");
        add(NVItems.HELLFORGED_DUST.get(), "Hellforged Dust");
        add(NVItems.CORRUPTED_DUST.get(), "Corrupted Dust");
        add(NVItems.CORRUPTED_DUST_TINY.get(), "Tiny Corrupted Dust");
        add(NVItems.BLOOD_PEARL.get(), "Blood Pearl");

        addTooltip("spiritus", "Spiritus: %s");
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
        add("message.neovitae.too_far_from_altar", "You are too far from an Ara Vitae");
        add("message.neovitae.altar_draws_blood", "The altar draws the blood it needs...");

        add("message.neovitae.vitae_link.tier", "Craft Tier: %s / %s");
        add("message.neovitae.vitae_link.locked", "Cannot change tier while crafting");
        add("jade.neovitae.vitae_link.tier", "Craft Tier: %s / %s");
        add("jade.neovitae.vitae_link.crafting", "Crafting...");
        add("jade.neovitae.vitae_link.unlinked", "Unlinked");
        add("jade.neovitae.orb_link.linked", "Linked");
        add("jade.neovitae.orb_link.unlinked", "Unlinked");
        add("jade.neovitae.orb_link.network", "Network: %s%%");

        // Material generation messages
        add("message.neovitae.materials.generated", "[Neo Vitae] New ore materials have been detected and added to the config.");
        add("message.neovitae.materials.restart_required", "[Neo Vitae] A game restart is required for the new material items to appear.");
        add("command.neovitae.generate.dedicated_unsupported", "/nvgenerate cannot run on a dedicated server: ore color sampling requires client-side block textures. Run it once in single-player to produce materials.json, then ship that file to the dedicated server.");
        add("command.neovitae.generate.scanning", "Scanning c:ores tags...");
        add("command.neovitae.generate.no_new", "No new ore materials found. %s already configured.");
        add("command.neovitae.generate.added", "Added %s new materials: %s");
        add("command.neovitae.generate.skipped", "%s ore types already configured or skipped.");
        add("command.neovitae.generate.restart", "Restart the game for new items to appear.");
        add("command.neovitae.setorbfill.success", "Set orb fill to %s / %s mB");
        add("command.neovitae.setorbfill.not_orb", "You must be holding an Orb of Vitae");

        add(NVItems.SENTIENT_HELMET.get(), "Sentient Helmet");
        add(NVItems.SENTIENT_PLATE.get(), "Sentient Plate");
        add(NVItems.SENTIENT_LEGGINGS.get(), "Sentient Leggings");
        add(NVItems.SENTIENT_BOOTS.get(), "Sentient Boots");
        add(NVItems.UPGRADE_TOME.get(), "Upgrade Tome");
        add(NVItems.EXPERIENCE_TOME.get(), "Tome of Peritia");
        addTooltip("experience_tome.stored", "Stored XP: %s");
        addTooltip("experience_tome.sneak_use", "Sneak + Use: Store XP");
        addTooltip("experience_tome.use", "Use: Retrieve XP");

        add(NVItems.UPGRADE_SCRAP.get(), "Upgrade Tome Scrap");
        add(NVItems.SYNTHETIC_POINT.get(), "Synthetic Upgrade Points");
        addTooltip("scrap", "Contained Upgrade Points: %s");

        add(NVItems.TRAINING_BRACELET.get(), "Sentient Training Bracelet");
        add("trainer.neovitae.allow_others", "Allow Others");
        add("trainer.neovitae.deny_others", "Deny Others");
        add("trainer.neovitae.save", "Save");

        add("item.neovitae.sentient_plate.dead", "Formerly Sentient Plate");
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
        addCommand("meteor.not_catalyst", "That item is not a valid meteor catalyst.");
        addCommand("meteor.success", "Summoned a %s meteor falling from %s, %s, %s.");

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
        addTooltip("sigil.bound_treasures.desc", "Opens a bound container from anywhere. Shift right-click a container to bind.");
        addTooltip("sigil.necromancy.desc", "Reanimates the bones of fallen mobs into temporary skeletal allies under your command.");

        addTooltip("sigil.teleposition.unbound", "This sigil is not bound to a Teleposer. Right-click a Teleposer to bind.");
        addTooltip("sigil.teleposition.invalid_dimension", "The bound Teleposer's dimension is not loaded.");
        addTooltip("sigil.teleposition.no_teleposer", "The bound location no longer has a Teleposer.");
        addTooltip("sigil.teleposition.bound", "Bound to Teleposer at %s, %s, %s.");

        // Sigil tooltips - Divination/Seer info messages
        addTooltip("sigil.divination.currentAltarTier", "Current Ara Vitae Tier: %s");
        addTooltip("sigil.divination.currentEV", "Current Essentia Vitae: %s");
        addTooltip("sigil.divination.currentAltarCapacity", "Ara Vitae Capacity: %s EV");
        addTooltip("sigil.divination.currentNetworkLP", "Anima: %s EV");
        addTooltip("sigil.divination.otherNetwork", "Viewing network of: %s");
        addTooltip("sigil.seer.currentAltarTier", "Current Ara Vitae Tier: %s");
        addTooltip("sigil.seer.currentEV", "Current Essentia Vitae: %s");
        addTooltip("sigil.seer.currentAltarCapacity", "Ara Vitae Capacity: %s EV");
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
        addTooltip("sigil.upkeep", "Upkeep: %s EV / %ss");

        // Sigil holding
        addTooltip("sigil.holding.sigilInSlot", "Slot %s: %s");

        // Current owner/binding
        addTooltip("currentOwner", "Bound to: %s");

        add("chat.neovitae.sentient_upgrade.level_up", "%s has levelled up to %s!");

        SentientUpgrades.translations(this::add);

        // JEI Integration
        addJei("recipe.altar", "Ara Vitae");
        addJei("recipe.hellfire_forge", "Hellfire Forge");
        addJei("recipe.array_crafting", "Array Crafting");
        addJei("recipe.array_effects", "Array Effects");
        addJei("recipe.tabulavitae", "Tabula Vitae");
        addJei("recipe.requiredtier", "Required Tier: %s");
        addJei("recipe.requiredlp", "Required Essentia Vitae: %s");
        addJei("recipe.consumptionrate", "Consumption Rate: %s EV/t");
        addJei("recipe.drainrate", "Drain Rate: %s EV/t");
        addJei("recipe.componentTransfer", "Preserves Components");
        addJei("recipe.minimum_spiritus", "Minimum Spiritus: %s");
        addJei("recipe.spiritus_drained", "Spiritus Drained: %s");
        addJei("recipe.spiritus", "Spiritus");
        addJei("recipe.info", "Hover for info");
        addJei("recipe.lp", "EV");
        addJei("recipe.lpDrained", "EV Drained: %s");
        addJei("recipe.ticksRequired", "Ticks: %s");
        addJei("recipe.meteor", "Meteor Ritual");
        addJei("recipe.meteor.fill", "Fill Block");
        addJei("recipe.meteor.weight", "Weight: %s");
        addJei("recipe.meteor.estimate", "Est: %s blocks (~%s%%)");
        addJei("recipe.meteor.random_pool", "Random pick from %s blocks");
        addJei("recipe.arc", "Athanor");
        addJei("recipe.athanor.chance", "Chance: %s%%");
        addJei("recipe.athanor.spiritus_cost", "Spiritus Cost:");
        addJei("recipe.flask", "Flask Brewing");
        addJei("recipe.flask_combination", "Flask Combinations");
        addJei("recipe.blood_tank_upgrade", "Blood Tank Upgrade");
        addJei("recipe.disenchant", "Disenchanting");
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

        // Blood Orb
        add("jei.neovitae.orb.info", "Orbs of Vitae serve three purposes:\n\nMain Hand: Right-click to sacrifice one heart, channeling 200 EV into your Anima.\n\nOff-Hand (Shield): Hold the use key to raise a Sanguine Ward that blocks all frontal damage. Costs 50 EV/second to maintain. Requires at least 200 EV to activate.\n\nOff-Hand (Harvest): Slay any creature while holding an orb in your off-hand to fill the orb's internal reservoir with Essentia Vitae (10 EV per point of max health). Place the orb on an Ara Vitae to drain its reservoir into the basin at 10x speed.\n\nThe orb glows with an enchanted sheen when its internal tank is full.");
        add("jei.neovitae.disenchant.info", "With a Sanguine Reverter in the Athanor's tool slot, you can disenchant items.\n\nPlace a stack of Books and one enchanted item (gear, a tool, or an enchanted book) in the inputs. Each operation lifts one enchantment onto a book and removes it from the item, costing 5 raw spiritus and 100 mB of Essentia Vitae.\n\nWhen the item has no enchantments left, it moves to the output. It runs only while you have books to fill and room in the output to hold them.");
        add("jei.neovitae.disenchant.any_item", "Any Enchanted Item");
        add("jei.neovitae.disenchant.per_enchant", "Per enchantment:");
        add("jei.neovitae.disenchant.spiritus", "5 Raw Spiritus");
        add("jei.neovitae.disenchant.ev", "100 mB Essentia Vitae");

        // Blood Mending Upgrade
        addJei("recipe.hellfire_forge_upgrade", "Hellfire Forge Upgrade");
        addJei("recipe.upgrade_hint", "Any damageable item will be converted to have Blood Mending");


        String tomeObtain = "\n\nObtain the tome from dungeon loot (The Mines and the Foreman's hoard), the Sentient Extraction ritual, or by combining two duplicate tomes at a crafting table.";
        String downgradeApply = "\n\nImposed by the Sentient Downgrade ritual, which trades unwanted upgrade levels for this curse and frees Upgrade Points to spend elsewhere.";

        add("jei.neovitae.upgrade_tome.physical_protect.info", "Reduces incoming non-projectile damage, such as melee and explosions, as it levels.\n\nTrained: take non-projectile damage while wearing the full Sentient set." + tomeObtain);
        add("jei.neovitae.upgrade_tome.arrow_protect.info", "Reduces incoming projectile damage as it levels.\n\nTrained: take projectile damage while wearing the full Sentient set." + tomeObtain);
        add("jei.neovitae.upgrade_tome.fall_protect.info", "Reduces fall damage as it levels.\n\nTrained: take fall damage while wearing the full Sentient set." + tomeObtain);
        add("jei.neovitae.upgrade_tome.self_sacrifice.info", "Increases the Essentia Vitae gained from self-sacrifice.\n\nTrained: take self-sacrifice damage by bleeding into your blood orb or from an altar while wearing the full Sentient set." + tomeObtain);
        add("jei.neovitae.upgrade_tome.health.info", "Increases maximum health.\n\nTrained: restore health through regeneration, potions, or vitaemantic healing while wearing the full Sentient set." + tomeObtain);
        add("jei.neovitae.upgrade_tome.experienced.info", "Increases the experience gained from orbs.\n\nTrained: pick up experience while wearing the full Sentient set." + tomeObtain);
        add("jei.neovitae.upgrade_tome.melee_damage.info", "Increases melee attack damage.\n\nTrained: deal melee damage while wearing the full Sentient set." + tomeObtain);
        add("jei.neovitae.upgrade_tome.sprint_attack.info", "Adds bonus damage and knockback to sprint attacks.\n\nTrained: deal damage while sprinting and wearing the full Sentient set." + tomeObtain);
        add("jei.neovitae.upgrade_tome.digging.info", "Increases mining speed.\n\nTrained: break blocks while wearing the full Sentient set." + tomeObtain);
        add("jei.neovitae.upgrade_tome.fire_resist.info", "Periodically grants Fire Resistance.\n\nTrained: spend time on fire while wearing the full Sentient set." + tomeObtain);
        add("jei.neovitae.upgrade_tome.poison_resist.info", "Periodically cleanses Poison.\n\nTrained: spend time poisoned while wearing the full Sentient set." + tomeObtain);
        add("jei.neovitae.upgrade_tome.speed.info", "Increases movement speed.\n\nTrained: travel across the ground while wearing the full Sentient set." + tomeObtain);
        add("jei.neovitae.upgrade_tome.jump.info", "Increases jump height and reduces fall damage.\n\nTrained: rise through the air by jumping while wearing the full Sentient set." + tomeObtain);
        add("jei.neovitae.upgrade_tome.knockback_resist.info", "Increases knockback resistance, and at higher levels maximum health.\n\nTrained: eat food while wearing the full Sentient set." + tomeObtain);
        add("jei.neovitae.upgrade_tome.repair.info", "Slowly mends the chestplate's durability over time.\n\nTrained: as the chestplate's durability is restored while you wear the full Sentient set." + tomeObtain);
        add("jei.neovitae.upgrade_tome.netherite_protect.info", "Adds armor and armor toughness.\n\nNot trained through activity; apply the tome directly to a worn Sentient set.\n\nInscribe this tome in the Tabula Vitae from a diamond, a written book, a netherite ingot, and a shulker shell (Master orb, 10,000 Essentia Vitae).");
        add("jei.neovitae.upgrade_tome.elytra.info", "Grants elytra-style gliding from the chestplate.\n\nNot trained through activity; apply the tome directly to a worn Sentient set." + tomeObtain);
        add("jei.neovitae.upgrade_tome.curios_socket.info", "Adds Curios accessory sockets to your Sentient set (requires the Curios mod).\n\nNot trained through activity; apply the tome directly to a worn Sentient set.\n\nInscribe this tome in the Tabula Vitae from a written book, a shulker shell, an ender pearl, and a gold ingot (Tier 3, 5,000 Essentia Vitae).");
        add("jei.neovitae.upgrade_tome.gilded.info", "Piglins treat you as neutral, as though you wore gold.\n\nNot trained through activity; apply the tome directly to a worn Sentient set." + tomeObtain);
        add("jei.neovitae.upgrade_tome.luck.info", "Increases the Luck attribute, improving loot rolls.\n\nNot trained through activity; apply the tome directly to a worn Sentient set." + tomeObtain);
        add("jei.neovitae.upgrade_tome.battle_hungry.info", "If you go too long without combat, the armor saps your hunger." + downgradeApply);
        add("jei.neovitae.upgrade_tome.swim_decrease.info", "Reduces your swimming speed." + downgradeApply);
        add("jei.neovitae.upgrade_tome.crippled_arm.info", "Locks your off-hand, leaving only one arm usable." + downgradeApply);
        add("jei.neovitae.upgrade_tome.melee_decrease.info", "Reduces your melee attack damage." + downgradeApply);
        add("jei.neovitae.upgrade_tome.dig_slowdown.info", "Reduces your mining speed." + downgradeApply);
        add("jei.neovitae.upgrade_tome.speed_decrease.info", "Reduces your movement speed." + downgradeApply);
        add("jei.neovitae.upgrade_tome.quenched.info", "Prevents you from drinking potions." + downgradeApply);
        add("jei.neovitae.upgrade_tome.slow_heal.info", "Reduces the healing you receive." + downgradeApply);
        add("jei.neovitae.upgrade_tome.storm_trooper.info", "Spoils your aim, scattering the projectiles you fire." + downgradeApply);

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
        addJei("effect.repulsion.name", "Repulsion Array");
        addJei("effect.repulsion.desc", "Pushes hostile mobs away from the array");
        addJei("effect.collection.name", "Collection Array");
        addJei("effect.collection.desc", "Pulls nearby items toward the array center");
        addJei("effect.light.name", "Light Array");
        addJei("effect.light.desc", "Emits light in a radius without placing torches");
        addJei("effect.furnace.name", "Furnace Array");
        addJei("effect.furnace.desc", "Smelts items dropped on the ground nearby");
        addJei("effect.rain.name", "Tempest Array");
        addJei("effect.rain.desc", "Toggles rain on or off. Costs EV to activate.");
        addJei("effect.growth.name", "Growth Array");
        addJei("effect.growth.desc", "Accelerates crop growth in a small radius");
        addJei("effect.freeze.name", "Freeze Array");
        addJei("effect.freeze.desc", "Converts water to ice and adds snow layers");
        addJei("effect.signal.name", "Signal Array");
        addJei("effect.signal.desc", "Outputs redstone proportional to owner's EV");
        addJei("effect.trigger.name", "Trigger Array");
        addJei("effect.trigger.desc", "Emits a redstone pulse when an entity steps on it");
        addJei("effect.spirit_siphon.name", "Spirit Siphon Array");
        addJei("effect.spirit_siphon.desc", "Damages mobs and releases raw spiritus into the chunk");
        addJei("effect.deflection.name", "Deflection Array");
        addJei("effect.deflection.desc", "Reflects projectiles that pass through the column above");
        addJei("effect.endless_fountain.name", "Endless Fountain Array");
        addJei("effect.endless_fountain.desc", "Fills adjacent fluid tanks with water every few ticks");
        addJei("effect.undertow.name", "Undertow Array");
        addJei("effect.undertow.desc", "Creates a bubble column in water; right-click to reverse");
        addJei("effect.loyal_friends.name", "Array of Loyal Friends");
        addJei("effect.loyal_friends.desc", "Summons and revives your tamed companions");
        addJei("effect.vortex.name", "Vortex Array");
        addJei("effect.vortex.desc", "Pulls nearby entities toward the array and stops endermen from teleporting away");
        addJei("effect.imprisonment.name", "Array of Imprisonment");
        addJei("effect.imprisonment.desc", "Placed atop a mob spawner; the next mob kill within 11x11x11 reassigns the spawner to that mob, consuming the array");

        // Array effect dummy items (JEI searchable)
        add(NVItems.ARRAY_BOUNCE.get(), "Bounce Array");
        add(NVItems.ARRAY_SPIKE.get(), "Spike Array");
        add(NVItems.ARRAY_UPDRAFT.get(), "Updraft Array");
        add(NVItems.ARRAY_MOVEMENT.get(), "Movement Array");
        add(NVItems.ARRAY_DAY.get(), "Sunrise Array");
        add(NVItems.ARRAY_NIGHT.get(), "Moonrise Array");
        add(NVItems.ARRAY_ELEVATOR.get(), "Teleposition Array");
        add(NVItems.ARRAY_REPULSION.get(), "Repulsion Array");
        add(NVItems.ARRAY_COLLECTION.get(), "Collection Array");
        add(NVItems.ARRAY_LIGHT.get(), "Light Array");
        add(NVItems.ARRAY_FURNACE.get(), "Furnace Array");
        add(NVItems.ARRAY_RAIN.get(), "Tempest Array");
        add(NVItems.ARRAY_GROWTH.get(), "Growth Array");
        add(NVItems.ARRAY_FREEZE.get(), "Freeze Array");
        add(NVItems.ARRAY_SIGNAL.get(), "Signal Array");
        add(NVItems.ARRAY_TRIGGER.get(), "Trigger Array");
        add(NVItems.ARRAY_SPIRIT_SIPHON.get(), "Spirit Siphon Array");
        add(NVItems.ARRAY_DEFLECTION.get(), "Deflection Array");
        add(NVItems.ARRAY_ENDLESS_FOUNTAIN.get(), "Endless Fountain Array");
        add(NVItems.ARRAY_UNDERTOW.get(), "Undertow Array");
        add(NVItems.ARRAY_LOYAL_FRIENDS.get(), "Array of Loyal Friends");
        add(NVItems.ARRAY_VORTEX.get(), "Vortex Array");
        add(NVItems.ARRAY_IMPRISONMENT.get(), "Array of Imprisonment");
        addTooltip("array_effect.bounce", "Bounces entities high into the air. Crouch to disable.");
        addTooltip("array_effect.spike", "Damages any entity that steps on the array.");
        addTooltip("array_effect.updraft", "Launches entities upward with a gust of wind.");
        addTooltip("array_effect.movement", "Accelerates entities in the direction the array faces.");
        addTooltip("array_effect.day", "Advances the time to dawn. Consumed on use.");
        addTooltip("array_effect.night", "Advances the time to night. Consumed on use.");
        addTooltip("array_effect.elevator", "Requires another Teleposition Array above or below. Jump to teleport up, sneak to go down. Range: 64 blocks.");
        addTooltip("array_effect.repulsion", "Pushes hostile mobs away in a 5-block radius.");
        addTooltip("array_effect.collection", "Pulls dropped items within 2 blocks. Place over a chest to auto-collect.");
        addTooltip("array_effect.light", "Places invisible light sources in a radius above the array.");
        addTooltip("array_effect.furnace", "Smelts items on the ground using furnace recipes. 10 EV per stack. Prevents item despawn.");
        addTooltip("array_effect.rain", "Toggles rain on or off. Costs 500 EV. Consumed on use.");
        addTooltip("array_effect.growth", "Accelerates crop and plant growth in a 2-block radius.");
        addTooltip("array_effect.freeze", "Freezes water sources to ice and covers ground in snow. Consumed on use.");
        addTooltip("array_effect.signal", "Outputs redstone signal 0-15 based on the owner's EV level.");
        addTooltip("array_effect.trigger", "Emits a redstone pulse when a mob or player steps on the array.");
        addTooltip("array_effect.spirit_siphon", "Damages non-player mobs and releases raw spiritus into the chunk.");
        addTooltip("array_effect.deflection", "Reflects projectiles passing through a column above the array.");
        addTooltip("array_effect.endless_fountain", "Fills adjacent fluid tanks with up to 6 buckets of water every 5 ticks.");
        addTooltip("array_effect.undertow", "Drives a bubble column through the water above. Right-click to flip between upward (push) and downward (drag).");
        addTooltip("array_effect.loyal_friends", "Summons and revives your tamed companions near the array.");
        addTooltip("array_effect.vortex", "Pulls nearby entities toward the array and stops endermen from teleporting away.");
        addTooltip("array_effect.imprisonment", "Place atop a mob spawner. The next mob killed within 11x11x11 becomes the spawner's new mob, and the array is consumed.");

        // Rituals
        addRitual("water", "Ritual of the Full Spring", "Places water source blocks in the area; with Raw Spiritus, also fills any fluid tank above the master stone (1 Raw per 1,000 mB).");
        addRitual("lava", "Serenade of the Nether", "Places lava source blocks within the area.");
        addRitual("green_grove", "Ritual of Overgrowth", "Suffuses the earth with life; bonemeals nearby crops and saplings. Spiritus aspects extend it to farmland hydration, Plant Leech, or scaled chance.");
        add("ritual.neovitae.green_grove.spiritus.raw", "Raw Spiritus: Hastens the refresh rate (20 ticks down to 10 as raw rises).");
        add("ritual.neovitae.green_grove.spiritus.invictus", "Spiritus Invictus: Hydrates nearby farmland to full moisture.");
        add("ritual.neovitae.green_grove.spiritus.ruina", "Spiritus Ruina: Applies Plant Leech to nearby mobs.");
        add("ritual.neovitae.green_grove.spiritus.vindicta", "Spiritus Vindicta: Scales growth success chance up to 100%%.");
        addRitual("well_of_suffering", "Well of Suffering", "Damages every hostile creature in range and channels their pain into Essentia Vitae at the master stone.");
        addRitual("feathered_knife", "Ritual of the Willing Sacrifice", "Wounds the practitioner standing on the master stone, converting their health into EV for the altar.");
        addRitual("harvest", "Ritual of Harvest", "Reaps every mature crop in range and replants the seeds. Place a chest atop the Master Ritual Stone to collect the yield; otherwise it drops where it grew.");
        addRitual("regeneration", "Ritual of Regeneration", "Applies Regeneration to practitioners in range; with Spiritus Ruina present, also drains nearby mobs to heal you.");
        addRitual("speed", "Ritual of Speed", "Propels non-sneaking entities in the master stone's facing direction. Sneak inside the area to receive Speed II for 30 minutes instead. Aspects modulate velocity, target filtering, and add Soft Fall.");
        addRitual("magnetism", "The Endless Quarry", "Reaps ore blocks from the volume below the master stone (loading unloaded chunks as needed). Inserts as items into a chest at the configured chest position (directly above the master stone by default) if one is present; otherwise places the ore as a block in a 3x3x3 volume above. Scan radius scales with the foundation block: iron 7, gold 15, diamond 31, netherite 63, anything else 3. 50 EV per ore moved; up to 3 ores and 100 checks per refresh, scan reaches bedrock.");
        addRitual("shepherd", "Ritual of the Shepherd", "Tends the animals in range: it hastens the growth of the young and coaxes ready adults into breeding, spending a little Essentia Vitae to feed them so no food or chest is required. Raw Spiritus quickens its pulse.");
        addRitual("butchering", "Ritual of Butchering", "Slaughters adult animals in range and gathers their drops into an adjacent chest, always leaving enough of each species alive to keep breeding. Set how many of each to spare with the Ritual Configurator.");
        addRitual("felling", "Ritual of Fallen Trees", "Fells every tree in range and drops the logs into an adjacent chest.");
        addRitual("suppression", "Dome of Suppression", "Replaces fluid source blocks in range with air, restoring them when the ritual stops.");
        addRitual("containment", "Ritual of Containment", "Pushes any creature trying to leave the area back toward the center; an invisible cage.");
        addRitual("expulsion", "Ritual of Expulsion", "Drives every creature outward from the ritual center.");
        addRitual("zephyr", "The Gathering", "Persistent wind that gathers loose items and XP, depositing them with the master stone or a nearby player.");
        addRitual("pump", "Hymn of Siphoning", "Draws fluid source blocks into a fluid tank at the configured tank position (directly above the master stone by default).");
        addRitual("phantom_bridge", "Ritual of the Phantom Bridge", "Weaves spectral platforms beneath the feet of practitioners in range.");
        addRitual("crystallum_fractura", "Crystallum Fractura", "Auto-harvests Spiritus Crystal clusters in range, doubles their growth speed, and biases the chunk's aspect via the Ritual Configurator.");
        add("ritual.neovitae.crystallum_fractura.aspect_effect", "Biases the growth aura's Spiritus injection toward the chosen aspect's crystals. Raw leaves the aura unbiased.");
        addRitual("downgrade", "Sentient Extraction", "Throw a piece of Sentient Armor onto the small zone above the master stone; the ritual extracts every upgrade as a separate Upgrade Tome.");
        addRitual("meteor", "Ritual of Meteo", "Consumes a catalyst item dropped within the area and crashes a corresponding meteor from above. Catalysts are defined by meteor recipes.");
        addRitual("forsaken_soul", "The Ritual of Lost Souls", "Watches the 21x21x21 area for non-player mob deaths and drops a charged Raw Spiritus item at each death position.");
        addRitual("full_stomach", "Ritual of the Satiated Stomach", "Feeds every practitioner in range from food stored in an adjacent chest.");

        // Dusk Tier Rituals
        addRitual("condor", "Soaring Skies", "Bestows true flight upon every practitioner within the circle. Dusk-tier.");
        addRitual("grounding", "The Sinner's Burden", "Imposes heavy gravity within the area; entities that try to fly are dragged back to the earth. Spiritus aspects shift between Heavy Heart, Suspended, and Levitation effects.");
        addRitual("placer", "Ritual of the Mason", "Places blocks drawn from an adjacent chest across the configured area.");
        addRitual("sphere", "Dawn of the New Moon", "Scoops the ellipsoidal volume of terrain below the master stone and lifts it upward into a floating moon. Foundation block beneath the master sets the size: iron 41, gold 49, diamond 57, netherite 65 across, anything else 33. 10 EV per block moved; ~100 checks per refresh.");
        addRitual("armour_evolve", "Ritual of Sentient Evolution", "Stand on the master stone in Sentient Armor; the ritual expands its upgrade capacity beyond the former limit.");
        addRitual("upgrade_remove", "Tabula Rasa", "Wipes every upgrade from worn Sentient Armor and resets used points to zero; no tomes are produced.");
        addRitual("crafting", "Rhythm of the Beating Anvil", "Automates crafting through an adjacent inventory. Spiritus Invictus routes through a Hellfire Forge, Spiritus Ruina through a Tabula Vitae. Dusk-tier.");
        add("ritual.neovitae.crafting.spiritus.invictus", "Spiritus Invictus: Attempts a Hellfire Forge recipe first, falling back to vanilla crafting if none match.");
        add("ritual.neovitae.crafting.spiritus.ruina", "Spiritus Ruina: Attempts a Tabula Vitae recipe first, falling back to vanilla crafting if none match.");
        addRitual("yawning_void", "All Consuming Void", "Erases blocks in a small box directly beneath the master ritual stone, one block per refresh. No drops. Spiritus Invictus moves the block to a placement volume above instead of consuming it; Spiritus Ruina limits consumption to blocks matching items in the chest above; Raw Spiritus accelerates the refresh rate.");
        add("ritual.neovitae.yawning_void.spiritus.raw", "Raw Spiritus: Accelerates the refresh rate; with enough raw aura, approaches one block per tick.");
        add("ritual.neovitae.yawning_void.spiritus.invictus", "Spiritus Invictus: Moves the cleared block into a 3x3x3 placement volume above the master stone instead of erasing it.");
        add("ritual.neovitae.yawning_void.spiritus.ruina", "Spiritus Ruina: Only consumes blocks whose item form matches the whitelist chest directly above the master stone.");
        addRitual("torment_nexus", "The Torment Nexus",
                "Binds every spawner and trial spawner in range, suppressing their natural spawns and harvesting"
                + " an equivalent stream of EV from the simulated kills. Loot funnels into a chest atop the master"
                + " stone; an Experience Tome in the chest soaks up the kills' XP. Requires an Awakened Activation Crystal.");

        // Dungeon Rituals (snake_case to match ritual constructors)
        addRitual("simple_dungeon", "Breaching the Edge of Demon Realm",
                "One-shot rite: consumes a large EV pool and assembles a complete Starter tier dungeon structure at the master ritual stone.");
        addRitual("standard_dungeon", "Highway to Hell",
                "Opens a permanent gateway to the Endless tier dungeon: a vast procedural dungeon (Mines, Foreman fight, aspected loot) that goes on forever and can be returned to as often as you like. Consumes a very large EV pool.");

        // Dimension
        add("dimension.neovitae.dungeon", "The Demon Realm");

        // Mob Effects
        add("effect.neovitae.soulsnare", "Soul Snare");
        add("effect.neovitae.firefuse", "Fire Fuse");
        add("effect.neovitae.soulfray", "Soul Fray");
        add("effect.neovitae.blessed_sacrifice", "Blessed Sacrifice");
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

        // Jade tooltips
        addJade("array_effect.bounce", "Bounce Array");
        addJade("array_effect.spike", "Spike Array");
        addJade("array_effect.updraft", "Updraft Array");
        addJade("array_effect.undertow", "Undertow Array");
        addJade("array_effect.movement", "Movement Array");
        addJade("array_effect.day", "Daybreak Array");
        addJade("array_effect.night", "Nightfall Array");
        addJade("array_effect.elevator", "Elevator Array");
        addJade("array_effect.generic", "Alchemy Array");
        addJade("array_direction", "%s %s");
        addJade("array_accel", "Acceleration: %s");
        addJade("array_max_vel", "Max Velocity: %s");
        addJade("altar_tier", "Tier %s");
        addJade("crafting", "Crafting...");
        addJade("inactive", "Inactive");
        addJade("tank_ev", "%s / %s mB");
        addJade("light_color", "Color: %s");
        addJade("progress", "Progress: %s%%");
        addJade("tranquility", "Tranquility: %s");
        addJade("incense_bonus", "Incense Bonus: +%s%%");

        // Advancements
        addAdvancement("root", "Neo Vitae", "Obtain an Ara Vitae");
        addAdvancement("weak_blood_orb", "Novicius Orb of Vitae", "Craft your first Orb of Vitae");
        addAdvancement("apprentice_blood_orb", "Discipulus Orb of Vitae", "Upgrade to a Tier 1 Orb of Vitae");
        addAdvancement("magician_blood_orb", "Veneficus Orb of Vitae", "Upgrade to a Tier 2 Orb of Vitae");
        addAdvancement("master_blood_orb", "Magus Orb of Vitae", "Upgrade to a Tier 3 Orb of Vitae");
        addAdvancement("archmage_blood_orb", "Dominus Orb of Vitae", "Upgrade to a Tier 4 Orb of Vitae");
        addAdvancement("transcendent_blood_orb", "Divinus Orb of Vitae", "Achieve the ultimate Orb of Vitae");
        addAdvancement("tabula_rasa", "Tabula Rasa", "Inscribe your first slate");
        addAdvancement("tabula_robur", "Tabula Robur", "Craft a Tabula Robur");
        addAdvancement("tabula_animata", "Tabula Animata", "Craft a Tabula Animata");
        addAdvancement("tabula_spiritus", "Tabula Spiritus", "Craft a Tabula Spiritus");
        addAdvancement("tabula_aetherea", "Tabula Aetherea", "Craft a Tabula Aetherea");
        addAdvancement("tabula_vitae", "The Tabula Vitae", "Craft a Tabula Vitae");
        addAdvancement("athanor", "Industrial Alchemy", "Craft an Athanor");
        addAdvancement("incense_altar", "Sacred Incense", "Craft an Incense Altar");
        addAdvancement("first_sigil", "First Sigil", "Craft your first sigil");
        addAdvancement("ritual_diviner", "Ritual Diviner", "Craft a Ritual Diviner");
        addAdvancement("master_ritual_stone", "Ritual Master", "Place a Master Ritual Stone");
        addAdvancement("imperfect_ritual", "Imperfect Beginnings", "Activate an Imperfect Ritual");
        addAdvancement("first_ritual", "Ritual Awakening", "Activate your first ritual");
        addAdvancement("well_of_suffering", "Well of Suffering", "Activate the Well of Suffering");
        addAdvancement("edge_of_hidden_realm", "Breaching the Edge of Demon Realm", "Venture into the Demon Realm");
        addAdvancement("crystallum_fractura", "Crystallum Fractura", "Activate the unified harvest ritual; let the aura split crystals from Spiritus itself");
        addAdvancement("transmute_ruina", "First Fracture", "Transmute a fully-grown Raw cluster into Spiritus Ruina with a catalyst and an Animus Mote");
        addAdvancement("transmute_nihilum", "Aspect of Ruin", "Transmute a Raw cluster into Spiritus Nihilum");
        addAdvancement("transmute_vindicta", "Aspect of Vengeance", "Transmute a Raw cluster into Spiritus Vindicta");
        addAdvancement("transmute_invictus", "Aspect of Endurance", "Transmute a Raw cluster into Spiritus Invictus");
        addAdvancement("aspectum_omnia", "Aspectum Omnia", "Transmute a Raw cluster into each of the four aspects");
        addAdvancement("serenade_of_nether", "Serenade of the Nether", "Activate the Serenade of the Nether");
        addAdvancement("master_of_ceremonies", "Master of Ceremonies", "Complete all ritual achievements");
        addAdvancement("meteor", "METEO!", "Summon the Ritual of Meteo");
        addAdvancement("teleposer", "Displacement", "Craft a Teleposer");
        addAdvancement("throwing_dagger", "First Strike", "Craft a Throwing Dagger");
        addAdvancement("spiritus", "First Spiritus", "Obtain raw Spiritus");
        addAdvancement("spiritus_gem_petty", "Petty Spiritus Gem", "Craft a Petty Spiritus Gem");
        addAdvancement("spiritus_gem_lesser", "Lesser Spiritus Gem", "Upgrade to a Lesser Gem");
        addAdvancement("spiritus_gem_common", "Common Spiritus Gem", "Upgrade to a Common Gem");
        addAdvancement("spiritus_gem_greater", "Greater Spiritus Gem", "Upgrade to a Greater Gem");
        addAdvancement("spiritus_gem_grand", "Grand Spiritus Gem", "Upgrade to a Grand Gem");
        addAdvancement("sentient_sword", "Sentient Blade", "Craft a Sentient Sword");
        addAdvancement("hellfire_forge", "Hellfire Forge", "Craft a Hellfire Forge");
        addAdvancement("blood_mending", "Self-Repairing", "Imbue an item with Blood Mending at the Hellfire Forge");
        addAdvancement("vas_maleficum", "Vas Maleficum", "Craft a Vas Maleficum");
        addAdvancement("sentient_armor", "Sentient Armor", "Craft Sentient Armor");
        addAdvancement("sentient_evolution", "Sentient Evolution", "Activate the Ritual of Sentient Evolution to push your Sentient Armor's upgrade capacity beyond its limit");
        addAdvancement("self_sacrifice", "Blood Pact", "Forge your first Orb of Vitae");

        addAdvancement("arcane_scribe", "Circle of Intent", "Craft an Arcane Scribe Tool to inscribe and activate Alchemy Arrays");
        addAdvancement("demonite", "Forged in the Pit", "Unearth a sliver of raw Demonite from the Endless Realm");
        addAdvancement("hellforged_ingot", "Hellforged", "Smelt raw Demonite into a Hellforged Ingot");

        addAdvancement("blood_sweat_and_tears", "Blood, Sweat & Tears", "Craft the legendary record at a Tier 6 Ara Vitae");

        // Commands (note: singular `command.` prefix, distinct from the `commands.` plural used by addCommand)
        add("command.neovitae.player_only", "This command must be run by a player");
        add("command.neovitae.anima.set", "Set %s's anima to %s");
        add("command.neovitae.anima.add", "Added %s anima to %s");
        add("command.neovitae.anima.get", "%s's anima: %s");
        add("command.neovitae.aura.invalid_will_type", "Unknown spiritus type: %s");
        add("command.neovitae.aura.set_all", "Set all spiritus aspects to %s");
        add("command.neovitae.aura.set_type", "Set %s spiritus to %s");
        add("command.neovitae.aura.cleared_chunk", "Cleared spiritus aura for this chunk");
        add("command.neovitae.routing_rescan.no_master", "No master routing node within %s blocks");
        add("command.neovitae.routing_rescan.result", "Master at [%s, %s, %s]: re-linked %s nodes within %s blocks");
        add("command.neovitae.stream_test.no_block", "Look at a block to target the stream");
        add("command.neovitae.stream_test.spawned", "Spawned %s preset at %s");
        add("command.neovitae.stream_test.activated", "Activated altar at %s");
        add("command.neovitae.stream_test.no_altar", "No master ritual stone at %s (found: %s)");
        add("command.neovitae.stream_test.unknown_preset", "Unknown stream preset: %s");
        add("command.neovitae.stream_test.fired", "Fired %s preset toward %s");
        add("command.neovitae.dungeon_showcase.no_structures", "No dungeon structures available to place");
        add("command.neovitae.dungeon_showcase.placing", "Placing %s dungeon structures...");
        add("command.neovitae.dungeon_showcase.placed", "Placed %s structures");
        add("command.neovitae.dungeon_showcase.preconfigured", "Use this command in a flat creative world for clean previews");
        add("command.neovitae.dungeon_showcase.save_hint", "Use /save-all to capture the showcase region");
        add("command.neovitae.upgrade.no_armour", "%s is not wearing a piece of sentient armor");

        // GUI - Athanor
        add("gui.neovitae.athanor.slot.empty", "Empty");
        add("gui.neovitae.athanor.slot.tool", "Tool slot");
        add("gui.neovitae.athanor.slot.input", "Ingredient slot");
        add("gui.neovitae.athanor.slot.output", "Output slot");
        add("gui.neovitae.athanor.slot.fluid_input", "Fluid input slot");
        add("gui.neovitae.athanor.slot.fluid_output", "Fluid output slot");
        add("gui.neovitae.athanor.tank_amount", "%s / %s mB");
        add("gui.neovitae.athanor.type_spiritus", "%s Spiritus");
        add("gui.neovitae.athanor.spiritus_progress", "%s / %s stored");
        add("gui.neovitae.athanor.spiritus_required", "Recipe requires %s");
        add("gui.neovitae.athanor.insufficient", "Not enough spiritus");
        add("gui.neovitae.show_recipes", "Click to view recipes (JEI)");

        // GUI - Dungeon Seal
        add("gui.neovitae.dungeon_seal.key_name", "%s");

        // GUI - Master Routing Node
        add("gui.neovitae.master_routing.energy_rate", "Transfer Rate");
        add("gui.neovitae.master_routing.energy_rate_label", "Rate");
        add("gui.neovitae.master_routing.set", "Set");
        add("gui.neovitae.master_routing.stack_upgrade_slot", "Stack upgrade slot");
        add("gui.neovitae.master_routing.speed_upgrade_slot", "Speed upgrade slot");
        add("gui.neovitae.master_routing.transfer_rate.title", "Transfer Rate");
        add("gui.neovitae.master_routing.transfer_rate.desc", "Items routed per cycle, fluid mB per tick");
        add("gui.neovitae.master_routing.transfer_rate.install_more", "Install upgrade modules to raise the ceiling");

        // GUI - Routing
        add("gui.neovitae.routing.disabled", "Disabled");
        add("gui.neovitae.routing.enabled", "Enabled");
        add("gui.neovitae.routing.items", "Items");
        add("gui.neovitae.routing.fluids", "Fluids");
        add("gui.neovitae.routing.whitelist", "Whitelist");
        add("gui.neovitae.routing.blacklist", "Blacklist");
        add("gui.neovitae.routing.auto_match", "Auto-Match");
        add("gui.neovitae.routing.direction.down", "Down");
        add("gui.neovitae.routing.direction.up", "Up");
        add("gui.neovitae.routing.direction.north", "North");
        add("gui.neovitae.routing.direction.south", "South");
        add("gui.neovitae.routing.direction.west", "West");
        add("gui.neovitae.routing.direction.east", "East");
        add("gui.neovitae.routing.priority_short", "P:%s");
        add("gui.neovitae.routing.priority_value", "Priority: %s");
        add("gui.neovitae.routing.priority.increase", "Increase priority");
        add("gui.neovitae.routing.priority.decrease", "Decrease priority");
        add("gui.neovitae.routing.swap_priority", "Right-click to swap priority order");
        add("gui.neovitae.routing.face", "Face: %s");
        add("gui.neovitae.routing.inventory_neighbor", "Connected inventory: %s");
        add("gui.neovitae.routing.block_neighbor", "Block: %s");
        add("gui.neovitae.routing.slot.empty", "Filter slot empty");
        add("gui.neovitae.routing.bucket.set", "Right-click with a bucket to set fluid filter");
        add("gui.neovitae.routing.bucket.clear", "Right-click with empty bucket to clear");
        add("gui.neovitae.routing.side.toggle", "Click to toggle side");
        add("gui.neovitae.routing.side.disabled_blocks", "Disabled sides do not push or pull");
        add("gui.neovitae.routing.filter.switch", "Switch filter mode");
        add("gui.neovitae.routing.filter.items_desc", "Items: filter by item stack");
        add("gui.neovitae.routing.filter.fluids_desc", "Fluids: filter by fluid type");
        add("gui.neovitae.routing.filter.item_mode", "Item mode");
        add("gui.neovitae.routing.filter.whitelist_empty", "Whitelist with no entries: nothing matches");
        add("gui.neovitae.routing.filter.blacklist_empty", "Blacklist with no entries: everything matches");
        add("gui.neovitae.routing.filter.fluid_mode", "Fluid mode");
        add("gui.neovitae.routing.filter.fluid_explicit", "Explicit: only the listed fluids");
        add("gui.neovitae.routing.filter.fluid_automatch", "Auto-match: any fluid present in the network");
        add("gui.neovitae.routing.master.upgrade_ceiling", "Upgrade ceiling: %s");
        add("gui.neovitae.routing.master.throttle_exceeds", "Throttled below ceiling of %s");
        add("gui.neovitae.routing.keep", "Keep: %s");
        add("gui.neovitae.routing.keep_mb", "Keep: %s mB");
        add("gui.neovitae.routing.keep_unlimited", "Keep: Unlimited");
        add("gui.neovitae.routing.keep_scroll", "Scroll to set (Shift x10, Ctrl x64)");
        add("gui.neovitae.routing.keep_scroll_fluid", "Scroll to set (Shift x10k, Ctrl x250)");
        add("gui.neovitae.routing.match_components", "Match Components:");
        add("gui.neovitae.hellfire_forge.needs_spiritus", "Not enough Spiritus: %s / %s");
        add("gui.neovitae.hellfire_forge.needs_spiritus.hint", "A gem in a crafting slot fuels the craft instead of the gem slot");
        add("gui.neovitae.routing.matching_components", "Matching components:");
        add("gui.neovitae.routing.shift_match", "Shift-Right-Click: match components");
        add("gui.neovitae.routing.page", "Filter Page %s of %s");
        add("gui.neovitae.routing.page_hint", "A new page opens as you fill the last");

        // JEI
        add("jei.neovitae.athanor.mb", "%s mB");
        add("jei.neovitae.athanor.spiritus_requirement", "Requires %s %s spiritus");
        add("jei.neovitae.ritual.rune_count", "%s× %s");

        // System messages
        add("message.neovitae.divination.altar_stats_header", "===== Altar Diagnostics =====");
        add("message.neovitae.divination.altar_stats_footer", "==============================");
        add("message.neovitae.dungeon_seal.debug.header", "===== Dungeon Seal Debug =====");
        add("message.neovitae.dungeon_seal.debug.synth_null", "No active synthesizer at this seal");
        add("message.neovitae.dungeon_seal.foreman_not_found", "Foreman could not be located");
        add("message.neovitae.dungeon_seal.placements_since_special", "Placements since last special: %s");
        add("message.neovitae.dungeon_seal.seals_none", "No seals registered");
        add("message.neovitae.dungeon_seal.special_buffer.empty", "Special buffer: empty");
        add("message.neovitae.dungeon_seal.special_buffer.label", "Special buffer (%s):");
        add("message.neovitae.dungeon_tester.generated", "Test dungeon generated at %s");
        add("message.neovitae.dungeon_tester.failed", "Test dungeon failed to generate");

        // Resource pack
        add("pack.neovitae.materials", "NeoVitae Generated Materials");

        // Sentient tool rider lines (ruina/invictus aspects fall through to the per-tool keys)
        add("tooltip.neovitae.sentientAxe.rider.ruina", "Withers struck targets");
        add("tooltip.neovitae.sentientAxe.rider.invictus", "Grants Absorption on kills");
        add("tooltip.neovitae.sentientPickaxe.rider.ruina", "Withers struck targets");
        add("tooltip.neovitae.sentientPickaxe.rider.invictus", "Grants Absorption on kills");
        add("tooltip.neovitae.sentientScythe.rider.ruina", "Withers swept targets");
        add("tooltip.neovitae.sentientScythe.rider.invictus", "Grants Absorption on kills");
        add("tooltip.neovitae.sentientShovel.rider.ruina", "Withers struck targets");
        add("tooltip.neovitae.sentientShovel.rider.invictus", "Grants Absorption on kills");
        add("tooltip.neovitae.sentientSword.rider.ruina", "Withers struck targets");
        add("tooltip.neovitae.sentientSword.rider.invictus", "Grants Absorption on kills");

        // Missing alchemy array tooltips (matches the JEI effect descriptions)
        addTooltip("array_effect.binding", "Binds items to the owner's soul network.");
        addTooltip("array_effect.crafting", "Transforms items into new forms.");

        // Merge book-generated translations from modonomicon cache
        this.langCache.data().forEach(this::add);
    }

    public void addRitual(String key, String name) {
        add("ritual.neovitae." + key, name);
        add("ritual.neovitae." + key + ".info", "A NeoVitae ritual.");
    }

    public void addRitual(String key, String name, String desc) {
        add("ritual.neovitae." + key, name);
        add("ritual.neovitae." + key + ".info", desc);
        add("ritual.neovitae." + key + ".desc", desc);
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

    public void addJade(String name, String value) {
        add("jade.neovitae." + name, value);
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
        add(DungeonBlocks.DUNGEON_ORE, "Demonite Ore");
        add(DungeonBlocks.PRISMATIC_DEMONITE, "Prismatic Demonite Ore");
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
