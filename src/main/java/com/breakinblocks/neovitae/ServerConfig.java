package com.breakinblocks.neovitae;

import net.neoforged.neoforge.common.ModConfigSpec;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;

import java.util.EnumMap;

public class ServerConfig {

    public final ModConfigSpec.ConfigValue<Integer> SELF_SACRIFICE_CONVERSION;
    public final ModConfigSpec.ConfigValue<Integer> DEFAULT_UPGRADE_POINTS;
    public final ModConfigSpec.ConfigValue<Integer> EVOLUTION_UPGRADE_POINTS;

    public final ModConfigSpec.DoubleValue WILL_MAX_DEFAULT;
    public final ModConfigSpec.DoubleValue WILL_MAX_CORROSIVE;
    public final ModConfigSpec.DoubleValue WILL_MAX_DESTRUCTIVE;
    public final ModConfigSpec.DoubleValue WILL_MAX_VENGEFUL;
    public final ModConfigSpec.DoubleValue WILL_MAX_STEADFAST;

    public final ModConfigSpec.DoubleValue CRYSTAL_WILL_TO_FORM;
    public final ModConfigSpec.DoubleValue CRYSTAL_FORMATION_TIME;
    public final ModConfigSpec.DoubleValue CRYSTAL_SAME_WILL_RATE;
    public final ModConfigSpec.DoubleValue CRYSTAL_DIFFERENT_WILL_RATE;
    public final ModConfigSpec.DoubleValue CRYSTAL_WRONG_WILL_DELAY;
    public final ModConfigSpec.DoubleValue CRYSTAL_GROWTH_SPEED;
    public final ModConfigSpec.DoubleValue CRYSTAL_GROWTH_THRESHOLD;
    public final ModConfigSpec.IntValue CRYSTAL_MAX_COUNT;

    // Blood Siphon / Blood Shield config
    public final ModConfigSpec.IntValue BLOOD_SIPHON_PLAYER_MULTIPLIER;
    public final ModConfigSpec.IntValue BLOOD_SIPHON_MOB_MULTIPLIER;
    public final ModConfigSpec.IntValue BLOOD_SHIELD_LP_COST_MULTIPLIER;

    protected ServerConfig(ModConfigSpec.Builder builder) {
        SELF_SACRIFICE_CONVERSION = builder.define("self_sacrifice_conversion", 100);
        DEFAULT_UPGRADE_POINTS = builder.define("default_upgrade_points", 100);
        EVOLUTION_UPGRADE_POINTS = builder.define("evolution_upgrade_points", 300);

        builder.comment("Demon Will System Configuration",
                "These values define the base maximum demon will that can be stored per chunk.",
                "Different will types can have different maximum capacities.",
                "Rituals and other effects can add bonuses on top of these base values.");
        builder.push("demon_will");

        WILL_MAX_DEFAULT = builder
                .comment("Base maximum Raw (Default) demon will per chunk")
                .defineInRange("max_default_will", 100.0, 1.0, 10000.0);
        WILL_MAX_CORROSIVE = builder
                .comment("Base maximum Corrosive demon will per chunk")
                .defineInRange("max_corrosive_will", 100.0, 1.0, 10000.0);
        WILL_MAX_DESTRUCTIVE = builder
                .comment("Base maximum Destructive demon will per chunk")
                .defineInRange("max_destructive_will", 100.0, 1.0, 10000.0);
        WILL_MAX_VENGEFUL = builder
                .comment("Base maximum Vengeful demon will per chunk")
                .defineInRange("max_vengeful_will", 100.0, 1.0, 10000.0);
        WILL_MAX_STEADFAST = builder
                .comment("Base maximum Steadfast demon will per chunk")
                .defineInRange("max_steadfast_will", 100.0, 1.0, 10000.0);

        builder.pop();

        builder.comment("Demon Crystal Growth Configuration",
                "These values control how demon crystals form and grow.",
                "Crystals are created by Crystallarium Maleficums and grow based on chunk will.");
        builder.push("demon_crystal");

        CRYSTAL_WILL_TO_FORM = builder
                .comment("Amount of demon will required to form a new crystal")
                .defineInRange("will_to_form", 99.0, 1.0, 1000.0);
        CRYSTAL_FORMATION_TIME = builder
                .comment("Total time (in ticks) for crystal formation in the Crystallizer")
                .defineInRange("formation_time", 1000.0, 100.0, 10000.0);
        CRYSTAL_SAME_WILL_RATE = builder
                .comment("Will consumption rate when crystal type matches chunk dominant will")
                .defineInRange("same_will_rate", 45.0, 1.0, 500.0);
        CRYSTAL_DIFFERENT_WILL_RATE = builder
                .comment("Will consumption rate when crystal type differs from chunk dominant will")
                .defineInRange("different_will_rate", 90.0, 1.0, 500.0);
        CRYSTAL_WRONG_WILL_DELAY = builder
                .comment("Growth speed multiplier when consuming non-matching will type (0.0-1.0, lower = slower)")
                .defineInRange("wrong_will_delay", 0.6, 0.0, 1.0);
        CRYSTAL_GROWTH_SPEED = builder
                .comment("Base growth speed multiplier (higher = faster crystal growth)")
                .defineInRange("growth_speed", 1.0, 0.1, 10.0);
        CRYSTAL_GROWTH_THRESHOLD = builder
                .comment("Minimum will in chunk before crystals start growing (200 = vanilla)")
                .defineInRange("growth_threshold", 200.0, 1.0, 1000.0);
        CRYSTAL_MAX_COUNT = builder
                .comment("Maximum number of crystal segments per cluster")
                .defineInRange("max_count", 7, 1, 20);

        builder.pop();

        builder.comment("Blood Attribute Configuration",
                "Configure LP multipliers for Blood Siphon and Blood Shield attributes.");
        builder.push("blood_attributes");

        BLOOD_SIPHON_PLAYER_MULTIPLIER = builder
                .comment("LP multiplier when Blood Siphon drains from a player target")
                .defineInRange("siphon_player_multiplier", 100, 1, 10000);
        BLOOD_SIPHON_MOB_MULTIPLIER = builder
                .comment("LP multiplier when Blood Siphon drains from a non-player target")
                .defineInRange("siphon_mob_multiplier", 10, 1, 10000);
        BLOOD_SHIELD_LP_COST_MULTIPLIER = builder
                .comment("LP cost per point of damage prevented by Blood Shield")
                .defineInRange("shield_lp_cost_multiplier", 100, 1, 10000);

        builder.pop();
    }

    public double getBaseMaxWill(EnumWillType type) {
        return switch (type) {
            case DEFAULT -> WILL_MAX_DEFAULT.get();
            case CORROSIVE -> WILL_MAX_CORROSIVE.get();
            case DESTRUCTIVE -> WILL_MAX_DESTRUCTIVE.get();
            case VENGEFUL -> WILL_MAX_VENGEFUL.get();
            case STEADFAST -> WILL_MAX_STEADFAST.get();
        };
    }

    public EnumMap<EnumWillType, Double> getAllBaseMaxWill() {
        EnumMap<EnumWillType, Double> result = new EnumMap<>(EnumWillType.class);
        for (EnumWillType type : EnumWillType.values()) {
            result.put(type, getBaseMaxWill(type));
        }
        return result;
    }
}
