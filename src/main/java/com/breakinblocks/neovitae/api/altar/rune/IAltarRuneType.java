package com.breakinblocks.neovitae.api.altar.rune;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

/**
 * Represents a type of altar rune that can provide bonuses to the Blood Altar.
 *
 * <p>Blood Magic provides built-in rune types via {@link EnumAltarRuneType}.
 * Addon mods can implement this interface to create custom rune types with
 * their own stat bonuses and behaviors.</p>
 *
 * <h2>Implementation Notes</h2>
 * <p>Custom implementations should:</p>
 * <ul>
 *   <li>Provide a unique {@link ResourceLocation} ID (e.g., "mymod:arcane")</li>
 *   <li>Provide a unique serialized name for NBT/config storage</li>
 *   <li>Register with the rune registry at mod initialization</li>
 * </ul>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * public class MyRuneType implements IAltarRuneType {
 *     public static final MyRuneType INSTANCE = new MyRuneType();
 *     private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("mymod", "arcane");
 *
 *     @Override
 *     public ResourceLocation getId() { return ID; }
 *
 *     @Override
 *     public String getSerializedName() { return "arcane"; }
 * }
 *
 * // During mod init:
 * NeoVitaeAPI.getInstance().getRuneRegistry().registerRuneType(MyRuneType.INSTANCE);
 * NeoVitaeAPI.getInstance().getRuneRegistry().registerRuneBlock(myBlock, MyRuneType.INSTANCE, 1);
 * }</pre>
 *
 * @see EnumAltarRuneType for built-in types
 * @see IAltarRuneRegistry for registration
 */
public interface IAltarRuneType extends StringRepresentable {

    /**
     * Gets the unique identifier for this rune type.
     *
     * @return The rune type's resource location ID
     */
    ResourceLocation getId();

    /**
     * Gets the serialized name for this rune type.
     * Used for NBT storage, data packs, and configuration.
     *
     * @return The serialized name (should be lowercase, no spaces)
     */
    @Override
    String getSerializedName();
}
