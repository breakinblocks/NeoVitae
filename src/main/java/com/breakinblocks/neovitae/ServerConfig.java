package com.breakinblocks.neovitae;

import net.neoforged.neoforge.common.ModConfigSpec;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

import java.util.EnumMap;

public class ServerConfig {

    public final ModConfigSpec.ConfigValue<Integer> SELF_SACRIFICE_CONVERSION;
    public final ModConfigSpec.ConfigValue<Integer> DEFAULT_UPGRADE_POINTS;
    public final ModConfigSpec.ConfigValue<Integer> EVOLUTION_UPGRADE_POINTS;

    public final ModConfigSpec.DoubleValue SPIRITUS_MAX_RAW;
    public final ModConfigSpec.DoubleValue SPIRITUS_MAX_RUINA;
    public final ModConfigSpec.DoubleValue SPIRITUS_MAX_NIHILUM;
    public final ModConfigSpec.DoubleValue SPIRITUS_MAX_VINDICTA;
    public final ModConfigSpec.DoubleValue SPIRITUS_MAX_INVICTUS;
    public final ModConfigSpec.DoubleValue SPAWNER_CAPTURE_COST;

    public final ModConfigSpec.DoubleValue CRYSTAL_SPIRITUS_TO_FORM;
    public final ModConfigSpec.DoubleValue CRYSTAL_FORMATION_TIME;
    public final ModConfigSpec.DoubleValue CRYSTAL_SAME_SPIRITUS_RATE;
    public final ModConfigSpec.DoubleValue CRYSTAL_DIFFERENT_SPIRITUS_RATE;
    public final ModConfigSpec.DoubleValue CRYSTAL_WRONG_SPIRITUS_DELAY;
    public final ModConfigSpec.DoubleValue CRYSTAL_GROWTH_SPEED;
    public final ModConfigSpec.DoubleValue CRYSTAL_GROWTH_THRESHOLD;
    public final ModConfigSpec.IntValue CRYSTAL_MAX_COUNT;

    // Blood Mending config
    public final ModConfigSpec.IntValue BLOOD_MENDING_REPAIR_COST;

    public final ModConfigSpec.BooleanValue LANTERN_SPAWN_SUPPRESSION;
    public final ModConfigSpec.IntValue DEMON_LANTERN_UPKEEP;
    public final ModConfigSpec.IntValue ATHANOR_DEUS_TOOL_EV_PER_USE;

    // Blood Siphon / Blood Shield config
    public final ModConfigSpec.IntValue BLOOD_SIPHON_PLAYER_MULTIPLIER;
    public final ModConfigSpec.IntValue BLOOD_SIPHON_MOB_MULTIPLIER;
    public final ModConfigSpec.IntValue BLOOD_SHIELD_LP_COST_MULTIPLIER;
    public final ModConfigSpec.IntValue SANGUINE_WARD_DRAIN_PER_SECOND;
    public final ModConfigSpec.IntValue SANGUINE_WARD_MIN_EV;

    public final ModConfigSpec.ConfigValue<String> LIQUIFIED_EXPERIENCE_FLUID;
    public final ModConfigSpec.IntValue LIQUIFIED_EXPERIENCE_MB_PER_POINT;
    public final ModConfigSpec.IntValue NECROMANCY_MAX_SUMMONS;

    public final ModConfigSpec.IntValue TORMENT_NEXUS_EV_PER_KILL;
    public final ModConfigSpec.IntValue TORMENT_NEXUS_MAX_EV_PER_OPERATION;
    public final ModConfigSpec.IntValue TORMENT_NEXUS_EV_MODIFIER_PERCENT;
    public final ModConfigSpec.IntValue TORMENT_NEXUS_MAX_LOOT_ROLLS;
    public final ModConfigSpec.IntValue TORMENT_NEXUS_HORIZONTAL_RANGE;
    public final ModConfigSpec.IntValue TORMENT_NEXUS_VERTICAL_RANGE;
    public final ModConfigSpec.IntValue SANGUINE_CRAFTER_EV_PER_CRAFT;
    public final ModConfigSpec.IntValue SANGUINE_CRAFTER_CRAFTING_TICKS;
    public final ModConfigSpec.IntValue SANGUINE_CRAFTER_FORGE_TICKS;
    public final ModConfigSpec.DoubleValue SANGUINE_CRAFTER_BASE_SPEED;
    public final ModConfigSpec.DoubleValue SANGUINE_CRAFTER_SPEED_PER_SWIFT_CASING;
    public final ModConfigSpec.IntValue SANGUINE_CRAFTER_MAX_PARALLEL;
    public final ModConfigSpec.BooleanValue SANGUINE_CRAFTER_CHUNK_LOADING;
    public final ModConfigSpec.BooleanValue SANGUINE_CRAFTER_CHUNK_LOADING_OWNER_ONLINE_ONLY;
    public final ModConfigSpec.IntValue SANGUINE_CRAFTER_CHUNK_LOADING_MAX_PER_PLAYER;
    public final ModConfigSpec.IntValue RITUAL_ANCHOR_RANGE;

    public final ModConfigSpec.DoubleValue BLOOD_SIPHON_CAP;
    public final ModConfigSpec.DoubleValue BLOOD_SHIELD_CAP;
    public final ModConfigSpec.IntValue DEUS_SANGUINIS_COOLDOWN;
    public final ModConfigSpec.IntValue DEUS_SANGUINIS_MIN_EV;
    public final ModConfigSpec.DoubleValue DEUS_SANGUINIS_EXP_PER_CREDIT;

    public final ModConfigSpec.IntValue HELLFORGED_UPGRADE_POINTS;
    public final ModConfigSpec.DoubleValue HELLFORGED_TRAINING_MULTIPLIER;
    public final ModConfigSpec.DoubleValue HELLFORGED_ARMOR_HELMET;
    public final ModConfigSpec.DoubleValue HELLFORGED_ARMOR_CHESTPLATE;
    public final ModConfigSpec.DoubleValue HELLFORGED_ARMOR_LEGGINGS;
    public final ModConfigSpec.DoubleValue HELLFORGED_ARMOR_BOOTS;
    public final ModConfigSpec.DoubleValue HELLFORGED_ARMOR_TOUGHNESS;
    public final ModConfigSpec.DoubleValue HELLFORGED_KNOCKBACK_RESISTANCE;
    public final ModConfigSpec.DoubleValue HELLFORGED_DAMAGE_MULTIPLIER;
    public final ModConfigSpec.DoubleValue HELLFORGED_FLOOR_DAMAGE;
    public final ModConfigSpec.DoubleValue HELLFORGED_DRAIN_MULTIPLIER;
    public final ModConfigSpec.BooleanValue HELLFORGED_NETHERITE_FLOOR;
    public final ModConfigSpec.BooleanValue HELLFORGED_FIRE_RESISTANT;
    public final ModConfigSpec.DoubleValue HELLFORGED_LEX_GROWTH_MULTIPLIER;
    public final ModConfigSpec.BooleanValue HELLFORGED_LEX_SILK_TOGGLE;

    public final ModConfigSpec.DoubleValue SENTIENT_SPIRITUS_SOFT_CAP;
    public final ModConfigSpec.DoubleValue SENTIENT_SPIRITUS_MAX;
    public final ModConfigSpec.DoubleValue SENTIENT_OVERCHARGE_BONUS;
    public final ModConfigSpec.BooleanValue SENTIENT_OVERCHARGE_DRAIN;

    protected ServerConfig(ModConfigSpec.Builder builder) {
        SELF_SACRIFICE_CONVERSION = builder.define("self_sacrifice_conversion", 100);
        DEFAULT_UPGRADE_POINTS = builder.define("default_upgrade_points", 100);
        EVOLUTION_UPGRADE_POINTS = builder.define("evolution_upgrade_points", 300);

        builder.comment("Spiritus System Configuration",
                "These values define the base maximum spiritus that can be stored per chunk.",
                "Different aspects can have different maximum capacities.",
                "Rituals and other effects can add bonuses on top of these base values.");
        builder.push("spiritus");

        SPIRITUS_MAX_RAW = builder
                .comment("Base maximum Raw Spiritus per chunk")
                .defineInRange("max_raw", 100.0, 1.0, 10000.0);
        SPIRITUS_MAX_RUINA = builder
                .comment("Base maximum Spiritus Ruina per chunk")
                .defineInRange("max_ruina", 100.0, 1.0, 10000.0);
        SPIRITUS_MAX_NIHILUM = builder
                .comment("Base maximum Spiritus Nihilum per chunk")
                .defineInRange("max_nihilum", 100.0, 1.0, 10000.0);
        SPIRITUS_MAX_VINDICTA = builder
                .comment("Base maximum Spiritus Vindicta per chunk")
                .defineInRange("max_vindicta", 100.0, 1.0, 10000.0);
        SPIRITUS_MAX_INVICTUS = builder
                .comment("Base maximum Spiritus Invictus per chunk")
                .defineInRange("max_invictus", 100.0, 1.0, 10000.0);

        SPAWNER_CAPTURE_COST = builder
                .comment("Spiritus a Spiritus Gem spends to capture a spawner or trial spawner via sneak + right-click")
                .defineInRange("spawner_capture_cost", 200.0, 0.0, 100000.0);

        builder.pop();

        builder.comment("Spiritus Crystal Growth Configuration",
                "These values control how spiritus crystals form and grow.",
                "Crystals are created by Crystallarium Maleficums and grow based on chunk spiritus.");
        builder.push("spiritus_crystal");

        CRYSTAL_SPIRITUS_TO_FORM = builder
                .comment("Amount of spiritus required to form a new crystal")
                .defineInRange("spiritus_to_form", 99.0, 1.0, 1000.0);
        CRYSTAL_FORMATION_TIME = builder
                .comment("Total time (in ticks) for crystal formation in the Crystallizer")
                .defineInRange("formation_time", 1000.0, 100.0, 10000.0);
        CRYSTAL_SAME_SPIRITUS_RATE = builder
                .comment("Spiritus consumption rate when crystal type matches chunk dominant aspect")
                .defineInRange("same_spiritus_rate", 45.0, 1.0, 500.0);
        CRYSTAL_DIFFERENT_SPIRITUS_RATE = builder
                .comment("Spiritus consumption rate when crystal type differs from chunk dominant aspect")
                .defineInRange("different_spiritus_rate", 90.0, 1.0, 500.0);
        CRYSTAL_WRONG_SPIRITUS_DELAY = builder
                .comment("Growth speed multiplier when consuming non-matching aspect (0.0-1.0, lower = slower)")
                .defineInRange("wrong_spiritus_delay", 0.6, 0.0, 1.0);
        CRYSTAL_GROWTH_SPEED = builder
                .comment("Base growth speed multiplier (higher = faster crystal growth)")
                .defineInRange("growth_speed", 1.0, 0.1, 10.0);
        CRYSTAL_GROWTH_THRESHOLD = builder
                .comment("Reference saturation for the crystal growth rate curve. This is NOT a minimum:",
                        "a cluster grows in any chunk holding more than 0.5 spiritus of its aspect.",
                        "Rate is (1 / this) * sqrt(spiritus / this) progress per second and a segment",
                        "completes at 1.0, so a segment takes roughly this many seconds in a chunk sitting",
                        "at exactly this saturation, longer below it and shorter above.",
                        "Raise to slow all crystal growth, lower to speed it up.")
                .defineInRange("growth_threshold", 200.0, 1.0, 1000.0);
        CRYSTAL_MAX_COUNT = builder
                .comment("Maximum number of crystal segments per cluster")
                .defineInRange("max_count", 7, 1, 20);

        builder.pop();

        builder.comment("Blood Mending Configuration",
                "Controls costs for repairing items through Blood Mending.");
        builder.push("blood_mending");

        BLOOD_MENDING_REPAIR_COST = builder
                .comment("EV cost per point of durability restored")
                .defineInRange("repair_cost_per_durability", 100, 1, 100000);

        builder.pop();

        builder.push("lanterns");
        LANTERN_SPAWN_SUPPRESSION = builder
                .comment("Allow Blood and Demon Lanterns to suppress non-monster spawns.",
                        "Disable to bypass lantern indexing and spawn suppression. Demon Lantern hostile spawning is unaffected.")
                .define("spawn_suppression", true);
        builder.pop();

        builder.comment("Demon Lantern Configuration");
        builder.push("demon_lantern");

        DEMON_LANTERN_UPKEEP = builder
                .comment("EV drained from the bound player's network each second while the Demon Lantern is running")
                .defineInRange("upkeep_per_second", 20, 0, 1000000);

        builder.pop();

        builder.comment("Athanor Configuration");
        builder.push("athanor");

        ATHANOR_DEUS_TOOL_EV_PER_USE = builder
                .comment("EV drawn from the bound player's network each time a Deus tool completes an Athanor or Tabula Vitae operation")
                .defineInRange("deus_tool_ev_per_use", 50, 0, 1000000);

        builder.pop();

        builder.comment("Blood Attribute Configuration",
                "Configure EV multipliers for Blood Siphon and Blood Shield attributes.");
        builder.push("blood_attributes");

        BLOOD_SIPHON_PLAYER_MULTIPLIER = builder
                .comment("EV per point of damage converted by Blood Siphon against a player target, also drained from that player")
                .defineInRange("siphon_player_multiplier", 250, 1, 10000);
        BLOOD_SIPHON_MOB_MULTIPLIER = builder
                .comment("EV per point of damage converted by Blood Siphon against a non-player target")
                .defineInRange("siphon_mob_multiplier", 50, 1, 10000);
        BLOOD_SIPHON_CAP = builder
                .comment("Highest percentage of damage dealt that Blood Siphon can convert into EV. Each point of Blood Siphon is 1%.")
                .defineInRange("siphon_cap", 95.0, 0.0, 100.0);
        BLOOD_SHIELD_CAP = builder
                .comment("Highest percentage of incoming damage that Blood Shield can prevent, from any source. Each point of Blood Shield is 1%.")
                .defineInRange("shield_cap", 95.0, 0.0, 100.0);
        DEUS_SANGUINIS_COOLDOWN = builder
                .comment("Ticks between training credits for the Deus Sanguinis upgrade.")
                .defineInRange("deus_sanguinis_cooldown", 200, 1, 72000);
        DEUS_SANGUINIS_MIN_EV = builder
                .comment("EV the network must spend between credits for Deus Sanguinis to earn one.")
                .defineInRange("deus_sanguinis_min_ev", 500, 0, 100000000);
        DEUS_SANGUINIS_EXP_PER_CREDIT = builder
                .comment("Experience Deus Sanguinis gains per training credit.")
                .defineInRange("deus_sanguinis_exp_per_credit", 1.0, 0.0, 10000.0);
        BLOOD_SHIELD_LP_COST_MULTIPLIER = builder
                .comment("EV cost per point of damage prevented by Blood Shield")
                .defineInRange("shield_lp_cost_multiplier", 100, 1, 10000);
        SANGUINE_WARD_DRAIN_PER_SECOND = builder
                .comment("EV drained per second while the Sanguine Ward is active")
                .defineInRange("sanguine_ward_drain_per_second", 50, 0, 10000);
        SANGUINE_WARD_MIN_EV = builder
                .comment("Minimum EV required to activate the Sanguine Ward")
                .defineInRange("sanguine_ward_min_ev", 200, 0, 100000);

        builder.pop();

        builder.comment("Torment Nexus Configuration",
                "Controls the EV upkeep and EV-yield modifier for the Torment Nexus ritual,",
                "which simulates kills from caged spawners and trial spawners in its area.");
        builder.comment("Array of Liquified Experience: moves experience between Tomes of Peritia",
                "in the container below the array and an adjacent tank holding an experience fluid.")
                .push("liquified_experience");

        LIQUIFIED_EXPERIENCE_FLUID = builder
                .comment("Fluid the array reads and writes, as modid:fluid_id.",
                        "Leave empty to use Neo Vitae's own Liquified Experience.",
                        "Set this if another mod in your pack provides the experience fluid you would rather use,",
                        "for example 'industrialforegoing:essence'. Unknown ids fall back to Neo Vitae's fluid.")
                .define("preferred_fluid", "");
        LIQUIFIED_EXPERIENCE_MB_PER_POINT = builder
                .comment("Millibuckets of experience fluid produced per point of stored experience.",
                        "Set this to match whichever mod provides the experience fluid.")
                .defineInRange("mb_per_point", 20, 1, 10000);

        builder.pop();

        builder.push("sigil_of_necromancy");

        NECROMANCY_MAX_SUMMONS = builder
                .comment("Maximum summons a single player may have alive at once. 0 removes the per-player limit.")
                .defineInRange("max_summons_per_player", 10, 0, 1024);

        builder.pop();

        builder.push("torment_nexus");

        TORMENT_NEXUS_EV_PER_KILL = builder
                .comment("EV cost charged per simulated mob kill (paid out of the network each kill).")
                .defineInRange("ev_per_kill", 75, 0, 1000000);
        TORMENT_NEXUS_MAX_EV_PER_OPERATION = builder
                .comment("Maximum total EV cost charged from the network to run the ritual in a single operation.",
                        "Kills are never capped: once this cost ceiling is reached the remaining kills run free,",
                        "still producing loot, experience, and EV yield. 0 disables the cap.")
                .defineInRange("max_ev_per_operation", 8000, 0, 100000000);
        TORMENT_NEXUS_EV_MODIFIER_PERCENT = builder
                .comment("Percent modifier on the EV produced per simulated kill.",
                        "1 = 1% of the mob's natural sacrifice value, 100 = unchanged, 1000 = 10x.")
                .defineInRange("ev_modifier_percent", 100, 1, 1000);
        TORMENT_NEXUS_MAX_LOOT_ROLLS = builder
                .comment("Maximum loot table rolls per spawner per ritual operation. When more kills than this",
                        "occur in one operation, the sampled drops are scaled up to match the full kill count,",
                        "so total loot stays the same while loot tables (and other mods' loot modifiers) run",
                        "far less often. Higher values give more variety per operation at a performance cost.")
                .defineInRange("max_loot_rolls_per_spawner", 5, 1, 1024);
        TORMENT_NEXUS_HORIZONTAL_RANGE = builder
                .comment("Default horizontal radius (in blocks) of the spawner-scan area centered on the Master Ritual Stone.",
                        "5 produces an 11x11 area on the XZ plane.")
                .defineInRange("horizontal_range", 5, 1, 64);
        TORMENT_NEXUS_VERTICAL_RANGE = builder
                .comment("Default vertical radius (in blocks) of the spawner-scan area centered on the Master Ritual Stone.",
                        "5 produces an 11-block-tall area.")
                .defineInRange("vertical_range", 5, 1, 64);

        builder.pop();

        builder.comment("Sanguine Crafter Configuration");
        builder.push("sanguine_crafter");

        SANGUINE_CRAFTER_EV_PER_CRAFT = builder
                .comment("EV drawn from the owner's network for every item crafted, on top of any Tabula Vitae recipe cost.")
                .defineInRange("ev_per_craft", 20, 0, 1000000);
        SANGUINE_CRAFTER_CRAFTING_TICKS = builder
                .comment("Ticks one crafting table recipe takes before Swift Sanguine Casings are applied.")
                .defineInRange("crafting_ticks", 20, 1, 72000);
        SANGUINE_CRAFTER_FORGE_TICKS = builder
                .comment("Ticks one Hellfire Forge recipe takes before Swift Sanguine Casings are applied.",
                        "Tabula Vitae recipes use their own crafting time.")
                .defineInRange("forge_ticks", 100, 1, 72000);
        SANGUINE_CRAFTER_BASE_SPEED = builder
                .comment("Speed multiplier on every craft. 5.0 crafts five times faster than the base times above.")
                .defineInRange("base_speed", 5.0, 0.1, 100.0);
        SANGUINE_CRAFTER_SPEED_PER_SWIFT_CASING = builder
                .comment("Extra crafting speed per Swift Sanguine Casing. 0.25 means each casing adds 25% speed.")
                .defineInRange("speed_per_swift_casing", 0.25, 0.0, 10.0);
        SANGUINE_CRAFTER_MAX_PARALLEL = builder
                .comment("Most crafts one Sanguine Crafter performs at once. Each Hellforged Sanguine Casing adds one, up to this cap.")
                .defineInRange("max_parallel", 16, 1, 64);
        SANGUINE_CRAFTER_CHUNK_LOADING = builder
                .comment("Whether a formed, running Sanguine Crafter keeps the chunks it stands in loaded.")
                .define("chunk_loading", true);
        SANGUINE_CRAFTER_CHUNK_LOADING_OWNER_ONLINE_ONLY = builder
                .comment("Only keep a crafter's chunks loaded while the player who built it is online.",
                        "Set to false to let crafters keep their chunks loaded while their owner is offline.")
                .define("chunk_loading_owner_online_only", true);
        SANGUINE_CRAFTER_CHUNK_LOADING_MAX_PER_PLAYER = builder
                .comment("Most Sanguine Crafters a single player may have keeping chunks loaded at once. 0 removes the limit.")
                .defineInRange("chunk_loading_max_per_player", 10, 0, 1024);

        builder.pop();

        builder.comment("Ritual Anchor Configuration");
        builder.push("ritual_anchor");

        RITUAL_ANCHOR_RANGE = builder
                .comment("Furthest a Ritual Anchor may be from the Master Ritual Stone it is linked to, in blocks.")
                .defineInRange("range", 64, 1, 128);

        builder.pop();

        builder.comment("Hellforged Equipment Configuration",
                "Applies to Sentient Armor, Sentient tools and the Lex Vitae upgraded by the Ritual of Hellforging.");
        builder.push("hellforged");

        HELLFORGED_UPGRADE_POINTS = builder
                .comment("Upgrade Points of capacity a Hellforged chestplate gains on top of the Deus Sentient Evolution cap.")
                .defineInRange("upgrade_points", 100, 0, 10000);
        HELLFORGED_TRAINING_MULTIPLIER = builder
                .comment("Multiplier on experience a Hellforged chestplate gains from training. Tomes and rituals are not affected.")
                .defineInRange("training_multiplier", 1.5, 0.0, 100.0);
        HELLFORGED_ARMOR_HELMET = builder
                .comment("Armor added to a Hellforged Sentient helmet.")
                .defineInRange("armor_helmet", 1.0, 0.0, 100.0);
        HELLFORGED_ARMOR_CHESTPLATE = builder
                .comment("Armor added to a Hellforged Sentient chestplate.")
                .defineInRange("armor_chestplate", 2.0, 0.0, 100.0);
        HELLFORGED_ARMOR_LEGGINGS = builder
                .comment("Armor added to Hellforged Sentient leggings.")
                .defineInRange("armor_leggings", 1.0, 0.0, 100.0);
        HELLFORGED_ARMOR_BOOTS = builder
                .comment("Armor added to Hellforged Sentient boots.")
                .defineInRange("armor_boots", 1.0, 0.0, 100.0);
        HELLFORGED_ARMOR_TOUGHNESS = builder
                .comment("Armor toughness added to each Hellforged Sentient armor piece.")
                .defineInRange("armor_toughness", 3.0, 0.0, 100.0);
        HELLFORGED_KNOCKBACK_RESISTANCE = builder
                .comment("Knockback resistance added to each Hellforged Sentient armor piece.")
                .defineInRange("knockback_resistance", 0.1, 0.0, 1.0);
        HELLFORGED_DAMAGE_MULTIPLIER = builder
                .comment("Multiplier on attack damage while holding a Hellforged Sentient tool or Lex Vitae.")
                .defineInRange("damage_multiplier", 1.3, 0.0, 100.0);
        HELLFORGED_FLOOR_DAMAGE = builder
                .comment("Flat attack damage added to Hellforged Sentient tools, before the damage multiplier. Requires netherite_floor.")
                .defineInRange("floor_damage", 2.0, 0.0, 100.0);
        HELLFORGED_DRAIN_MULTIPLIER = builder
                .comment("Multiplier on the Spiritus a Hellforged Sentient tool or Lex Vitae drains per swing.")
                .defineInRange("drain_multiplier", 0.75, 0.0, 100.0);
        HELLFORGED_NETHERITE_FLOOR = builder
                .comment("Hellforged Sentient tools mine, drop and last like Netherite even without Spiritus. Applied when the ritual runs.")
                .define("netherite_floor", true);
        HELLFORGED_FIRE_RESISTANT = builder
                .comment("Hellforged items do not burn in fire or lava. Applied when the ritual runs.")
                .define("fire_resistant", true);
        HELLFORGED_LEX_GROWTH_MULTIPLIER = builder
                .comment("Multiplier on growth experience a Hellforged Lex Vitae gains.")
                .defineInRange("lex_growth_multiplier", 1.5, 0.0, 100.0);
        HELLFORGED_LEX_SILK_TOGGLE = builder
                .comment("A Hellforged Lex Vitae can switch between Fortune and Silk Touch with sneak and the Cycle Mode key.")
                .define("lex_silk_toggle", true);

        builder.pop();

        builder.comment("Hellforged Sentient Tool Overcharge",
                "Above the soft cap, Hellforged Sentient tool and Lex Vitae bonuses keep growing with diminishing returns until the maximum.",
                "Each doubling of carried Spiritus adds the same amount.");
        builder.push("sentient_tools");

        SENTIENT_SPIRITUS_SOFT_CAP = builder
                .comment("Carried Spiritus where overcharge scaling begins.")
                .defineInRange("spiritus_soft_cap", 4000.0, 1.0, 1.0E9);
        SENTIENT_SPIRITUS_MAX = builder
                .comment("Carried Spiritus where overcharge scaling stops.")
                .defineInRange("spiritus_max", 32000.0, 1.0, 1.0E9);
        SENTIENT_OVERCHARGE_BONUS = builder
                .comment("Extra fraction of each bonus gained at the maximum. 0.5 means +50% of the top bracket's value.")
                .defineInRange("overcharge_bonus", 0.5, 0.0, 100.0);
        SENTIENT_OVERCHARGE_DRAIN = builder
                .comment("Spiritus drain per swing rises with overcharge too.")
                .define("overcharge_drain", true);

        builder.pop();
    }

    public double getBaseMaxSpiritus(SpiritusType type) {
        return switch (type) {
            case RAW -> SPIRITUS_MAX_RAW.get();
            case RUINA -> SPIRITUS_MAX_RUINA.get();
            case NIHILUM -> SPIRITUS_MAX_NIHILUM.get();
            case VINDICTA -> SPIRITUS_MAX_VINDICTA.get();
            case INVICTUS -> SPIRITUS_MAX_INVICTUS.get();
        };
    }

    public EnumMap<SpiritusType, Double> getAllBaseMaxSpiritus() {
        EnumMap<SpiritusType, Double> result = new EnumMap<>(SpiritusType.class);
        for (SpiritusType type : SpiritusType.values()) {
            result.put(type, getBaseMaxSpiritus(type));
        }
        return result;
    }
}
