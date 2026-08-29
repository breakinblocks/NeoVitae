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

    public final ModConfigSpec.IntValue DEMON_LANTERN_UPKEEP;

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
                .comment("Minimum spiritus in chunk before crystals start growing (200 = vanilla)")
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

        builder.comment("Demon Lantern Configuration");
        builder.push("demon_lantern");

        DEMON_LANTERN_UPKEEP = builder
                .comment("EV drained from the bound player's network each second while the Demon Lantern is running")
                .defineInRange("upkeep_per_second", 20, 0, 1000000);

        builder.pop();

        builder.comment("Blood Attribute Configuration",
                "Configure EV multipliers for Blood Siphon and Blood Shield attributes.");
        builder.push("blood_attributes");

        BLOOD_SIPHON_PLAYER_MULTIPLIER = builder
                .comment("EV multiplier when Blood Siphon drains from a player target")
                .defineInRange("siphon_player_multiplier", 100, 1, 10000);
        BLOOD_SIPHON_MOB_MULTIPLIER = builder
                .comment("EV multiplier when Blood Siphon drains from a non-player target")
                .defineInRange("siphon_mob_multiplier", 10, 1, 10000);
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
                "in the container below the array and an adjacent tank holding a #c:experience fluid.")
                .push("liquified_experience");

        LIQUIFIED_EXPERIENCE_FLUID = builder
                .comment("Fluid the array reads and writes, as modid:fluid_id.",
                        "Leave empty to use Neo Vitae's own Liquified Experience.",
                        "Set this if another mod in your pack provides the experience fluid you would rather use,",
                        "for example 'industrialforegoing:essence'. Unknown ids fall back to Neo Vitae's fluid.")
                .define("preferred_fluid", "");
        LIQUIFIED_EXPERIENCE_MB_PER_POINT = builder
                .comment("Millibuckets of experience fluid produced per point of stored experience.",
                        "Set this to match whichever mod provides the #c:experience fluid.")
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
