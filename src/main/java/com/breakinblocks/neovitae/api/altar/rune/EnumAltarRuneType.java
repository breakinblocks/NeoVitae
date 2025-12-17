package com.breakinblocks.neovitae.api.altar.rune;

import net.minecraft.resources.ResourceLocation;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Built-in altar rune types provided by Blood Magic.
 *
 * <p>These rune types are automatically registered with the rune registry
 * and provide the standard altar bonuses:</p>
 *
 * <ul>
 *   <li><b>SPEED</b> - Increases crafting speed (+20% per rune)</li>
 *   <li><b>EFFICIENCY</b> - Reduces LP drain when not crafting (15% reduction per rune)</li>
 *   <li><b>SACRIFICE</b> - Increases LP from entity sacrifice (+10% per rune)</li>
 *   <li><b>SELF_SACRIFICE</b> - Increases LP from self-sacrifice (+10% per rune)</li>
 *   <li><b>DISPLACEMENT</b> - Increases fluid I/O rate (1.2x multiplier per rune)</li>
 *   <li><b>CAPACITY</b> - Increases blood capacity (+20% per rune)</li>
 *   <li><b>AUGMENTED_CAPACITY</b> - Multiplies capacity bonus (1.075x per rune, compounds)</li>
 *   <li><b>ORB</b> - Increases soul network fill rate (+20% per rune)</li>
 *   <li><b>ACCELERATION</b> - Reduces tick rate for all operations (-1 tick per rune)</li>
 *   <li><b>CHARGING</b> - Enables and increases charge storage for burst crafting</li>
 * </ul>
 */
public enum EnumAltarRuneType implements IAltarRuneType {
    SPEED("speed"),
    EFFICIENCY("efficiency"),
    SACRIFICE("sacrifice"),
    SELF_SACRIFICE("self_sacrifice"),
    DISPLACEMENT("displacement"),
    CAPACITY("capacity"),
    AUGMENTED_CAPACITY("augmented_capacity"),
    ORB("orb"),
    ACCELERATION("acceleration"),
    CHARGING("charging");

    private final String serializedName;
    private final ResourceLocation id;

    EnumAltarRuneType(String serializedName) {
        this.serializedName = serializedName;
        this.id = ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, serializedName);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    /**
     * Gets a rune type by its serialized name.
     *
     * @param name The serialized name to look up
     * @return The matching rune type, or null if not found
     */
    public static EnumAltarRuneType fromSerializedName(String name) {
        for (EnumAltarRuneType type : values()) {
            if (type.serializedName.equals(name)) {
                return type;
            }
        }
        return null;
    }
}
