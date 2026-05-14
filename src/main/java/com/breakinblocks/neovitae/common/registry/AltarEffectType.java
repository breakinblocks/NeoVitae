package com.breakinblocks.neovitae.common.registry;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/**
 * Visual style applied to a set of capstone offsets per altar tier.
 *
 * <ul>
 *   <li>{@link #CAP_ORBIT_LIFE_PULSE} - 4-cap synchronised orbit then a life-pulse stream into the altar core.</li>
 *   <li>{@link #CAP_ORBIT_SPIRAL_STAGGERED} - per-cap orbit with staggered phases, alternating colour, ending in a spiralling stream.</li>
 *   <li>{@link #CAP_BURST} - low-rate ambient particle bursts at each origin.</li>
 *   <li>{@link #CAP_CRYSTAL_CASCADE} - downward cascading crystal shower above each origin.</li>
 *   <li>{@link #CAP_RENDER_HOVER_ARRAY} - client-side hovering rotating texture array (alchemy array textures) above each origin. Server skips this type.</li>
 * </ul>
 */
public enum AltarEffectType implements StringRepresentable {
    CAP_ORBIT_LIFE_PULSE("cap_orbit_life_pulse"),
    CAP_ORBIT_SPIRAL_STAGGERED("cap_orbit_spiral_staggered"),
    CAP_BURST("cap_burst"),
    CAP_CRYSTAL_CASCADE("cap_crystal_cascade"),
    CAP_RENDER_HOVER_ARRAY("cap_render_hover_array");

    public static final Codec<AltarEffectType> CODEC = StringRepresentable.fromEnum(AltarEffectType::values);

    private final String name;

    AltarEffectType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
