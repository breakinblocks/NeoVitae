package com.breakinblocks.neovitae.api.altar.rune;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Built-in Blood Altar rune types that affect altar behavior.
 *
 * <p>These are the standard rune types provided by Neo Vitae. They implement
 * {@link IAltarRuneType} so they can be used uniformly with custom rune types
 * in the unified rune registry system.</p>
 *
 * <p>Each rune type affects the altar in a specific way:</p>
 * <ul>
 *   <li><b>SPEED</b> - Increases LP consumption rate during crafting</li>
 *   <li><b>SACRIFICE</b> - Increases LP gained from mob sacrifice</li>
 *   <li><b>SELF_SACRIFICE</b> - Increases LP gained from player self-sacrifice</li>
 *   <li><b>DISPLACEMENT</b> - Increases fluid I/O rate for piping</li>
 *   <li><b>CAPACITY</b> - Increases altar blood capacity (additive)</li>
 *   <li><b>AUGMENTED_CAPACITY</b> - Increases altar blood capacity (multiplicative)</li>
 *   <li><b>ORB</b> - Increases soul network capacity bonus when filling orbs</li>
 *   <li><b>ACCELERATION</b> - Reduces ticks between altar operations</li>
 *   <li><b>CHARGING</b> - Enables pre-charging LP for instant crafting</li>
 *   <li><b>EFFICIENCY</b> - Reduces LP loss when altar runs out mid-craft</li>
 * </ul>
 *
 * @see IAltarRuneType
 * @see IAltarRuneRegistry
 */
public enum EnumAltarRuneType implements IAltarRuneType, StringRepresentable {
    /** Increases LP consumption rate during crafting */
    SPEED,
    /** Increases LP gained from mob sacrifice */
    SACRIFICE,
    /** Increases LP gained from player self-sacrifice */
    SELF_SACRIFICE,
    /** Increases fluid I/O rate for piping */
    DISPLACEMENT,
    /** Increases altar blood capacity (additive) */
    CAPACITY,
    /** Increases altar blood capacity (multiplicative, applied after CAPACITY) */
    AUGMENTED_CAPACITY,
    /** Increases soul network capacity bonus when filling orbs */
    ORB,
    /** Reduces ticks between altar operations */
    ACCELERATION,
    /** Enables pre-charging LP for instant crafting */
    CHARGING,
    /** Reduces LP loss when altar runs out mid-craft */
    EFFICIENCY;

    /** Codec for serialization */
    public static final Codec<EnumAltarRuneType> CODEC = StringRepresentable.fromEnum(EnumAltarRuneType::values);

    /** The Neo Vitae namespace for built-in rune types */
    private static final String NAMESPACE = "neovitae";

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, getSerializedName());
    }

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    /**
     * Gets a rune type by its serialized name.
     *
     * @param name The serialized name (case-insensitive)
     * @return The rune type, or null if not found
     */
    public static EnumAltarRuneType byName(String name) {
        for (EnumAltarRuneType type : values()) {
            if (type.getSerializedName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Gets a built-in rune type by its resource location ID.
     *
     * @param id The resource location ID (e.g., "neovitae:speed")
     * @return The rune type, or null if not found or not a Neo Vitae ID
     */
    public static EnumAltarRuneType byId(ResourceLocation id) {
        if (id == null || !NAMESPACE.equals(id.getNamespace())) {
            return null;
        }
        return byName(id.getPath());
    }
}
