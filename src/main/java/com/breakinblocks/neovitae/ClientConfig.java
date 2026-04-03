package com.breakinblocks.neovitae;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public final ModConfigSpec.BooleanValue USE_SIMPLE_EFFECTS;

    protected ClientConfig(ModConfigSpec.Builder builder) {
        builder.comment("Visual Effects Configuration");
        builder.push("effects");

        USE_SIMPLE_EFFECTS = builder
                .comment("Replace custom stream and particle effects with vanilla-style particles.",
                         "Enable this for better performance or if you prefer simpler visuals.")
                .define("use_simple_effects", false);

        builder.pop();
    }
}
