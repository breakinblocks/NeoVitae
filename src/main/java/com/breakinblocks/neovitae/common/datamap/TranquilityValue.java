package com.breakinblocks.neovitae.common.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import com.breakinblocks.neovitae.incense.EnumTranquilityType;

/**
 * Data-driven tranquility values for blocks used in the Incense Altar system.
 *
 * <p>Defines the tranquility type and value contribution of blocks when
 * calculating incense bonuses around an Incense Altar.</p>
 *
 * <h2>Priority System</h2>
 * <p>When looking up a block's tranquility value, the system checks in order:</p>
 * <ol>
 *   <li>Specific block entry (e.g., {@code minecraft:oak_log})</li>
 *   <li>Block tag entries (e.g., {@code #minecraft:logs}) - highest value wins</li>
 *   <li>Fluid state detection (water, lava)</li>
 *   <li>Built-in defaults (fire, crops via instanceof)</li>
 * </ol>
 *
 * <h2>Multiple Tag Matching</h2>
 * <p>If a block matches multiple tags with different tranquility values, the
 * entry with the <b>highest value</b> is used. This allows broad tag-based
 * defaults while supporting specific overrides.</p>
 *
 * <h2>Example Datapack</h2>
 * <pre>{@code
 * // data/neovitae/data_maps/block/tranquility.json
 * {
 *   "values": {
 *     "minecraft:oak_log": { "type": "tree", "value": 1.5 },
 *     "#minecraft:logs": { "type": "tree", "value": 1.0 },
 *     "#minecraft:dirt": { "type": "earthen", "value": 0.5 },
 *     "#neovitae:tranquility_plant": { "type": "plant", "value": 1.0 }
 *   }
 * }
 * }</pre>
 *
 * <h2>Tranquility Types</h2>
 * <ul>
 *   <li>{@code plant} - Flowers, grass, ferns</li>
 *   <li>{@code crop} - Wheat, carrots, potatoes, etc.</li>
 *   <li>{@code tree} - Logs and leaves</li>
 *   <li>{@code earthen} - Dirt, sand, gravel, clay</li>
 *   <li>{@code water} - Water source and flowing</li>
 *   <li>{@code fire} - Fire and soul fire</li>
 *   <li>{@code lava} - Lava source and flowing</li>
 * </ul>
 *
 * @param type  The tranquility type this block provides
 * @param value The tranquility value contribution (typically 0.5 to 2.0)
 */
public record TranquilityValue(
        EnumTranquilityType type,
        double value
) {
    /**
     * Default tranquility value for standard blocks.
     */
    public static final double DEFAULT_VALUE = 1.0;

    /**
     * Codec for EnumTranquilityType using lowercase names.
     */
    public static final Codec<EnumTranquilityType> TYPE_CODEC = Codec.STRING.xmap(
            EnumTranquilityType::getType,
            type -> type.name().toLowerCase()
    );

    public static final Codec<TranquilityValue> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TYPE_CODEC.fieldOf("type").forGetter(TranquilityValue::type),
            Codec.DOUBLE.optionalFieldOf("value", DEFAULT_VALUE).forGetter(TranquilityValue::value)
    ).apply(instance, TranquilityValue::new));

    /**
     * Creates a tranquility value with the default value of 1.0.
     */
    public static TranquilityValue of(EnumTranquilityType type) {
        return new TranquilityValue(type, DEFAULT_VALUE);
    }

    /**
     * Creates a tranquility value with a specific value.
     */
    public static TranquilityValue of(EnumTranquilityType type, double value) {
        return new TranquilityValue(type, value);
    }

    /**
     * Compares this tranquility value to another, returning the one with higher value.
     * Used when a block matches multiple tags.
     *
     * @param other The other tranquility value to compare
     * @return The TranquilityValue with the higher value
     */
    public TranquilityValue max(TranquilityValue other) {
        if (other == null) return this;
        return this.value >= other.value ? this : other;
    }
}
