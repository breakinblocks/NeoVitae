package com.breakinblocks.neovitae;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public enum SiphonStreams { ORB_ONLY, ALL, NEVER }

    public final ModConfigSpec.BooleanValue USE_SIMPLE_EFFECTS;
    public final ModConfigSpec.EnumValue<SiphonStreams> BLOOD_SIPHON_STREAMS;

    protected ClientConfig(ModConfigSpec.Builder builder) {
        builder.comment("Visual Effects Configuration");
        builder.push("effects");

        USE_SIMPLE_EFFECTS = builder
                .comment("Replace custom stream and particle effects with vanilla-style particles.",
                         "Enable this for better performance or if you prefer simpler visuals.")
                .define("use_simple_effects", false);

        BLOOD_SIPHON_STREAMS = builder
                .comment("Which Blood Siphon streams to draw.",
                         "ORB_ONLY: only kills siphoned into an Orb of Vitae held in the offhand.",
                         "ALL: also the Blood Siphon attribute, from any source.",
                         "NEVER: draw no Blood Siphon streams.")
                .defineEnum("blood_siphon_streams", SiphonStreams.ORB_ONLY);

        builder.pop();
    }
}
