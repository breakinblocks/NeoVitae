package com.breakinblocks.neovitae.api.altar.rune;

import net.minecraft.resources.ResourceLocation;

/**
 * Represents a type of Blood Altar rune.
 *
 * <p>Neo Vitae provides built-in rune types via {@link EnumAltarRuneType},
 * but other mods can implement this interface to create custom rune types with
 * unique behaviors.</p>
 *
 * <p>Custom rune types should be registered via the {@link IAltarRuneRegistry}.</p>
 *
 * <p>Example implementation:</p>
 * <pre>{@code
 * public class MyCustomRuneType implements IAltarRuneType {
 *     public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("mymod", "mana_rune");
 *
 *     @Override
 *     public ResourceLocation getId() {
 *         return ID;
 *     }
 *
 *     @Override
 *     public String getSerializedName() {
 *         return "mana_rune";
 *     }
 * }
 * }</pre>
 *
 * @see IAltarRuneRegistry
 * @see AltarRuneModifiers
 */
public interface IAltarRuneType {

    /**
     * Gets the unique identifier for this rune type.
     *
     * @return The resource location ID (e.g., "mymod:mana_rune")
     */
    ResourceLocation getId();

    /**
     * Gets the serialized name of this rune type for use in data files.
     *
     * @return The serialized name (e.g., "mana_rune")
     */
    String getSerializedName();
}
