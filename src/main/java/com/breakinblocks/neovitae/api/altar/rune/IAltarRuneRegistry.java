package com.breakinblocks.neovitae.api.altar.rune;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * Registry for altar rune types and block associations.
 *
 * <p>This registry manages both built-in and custom rune types, as well as
 * the associations between blocks and rune types. When the Blood Altar scans
 * its structure, it uses this registry to determine what bonuses each block provides.</p>
 *
 * <h2>Registration Timing</h2>
 * <p>Registrations should be done during mod initialization (FMLCommonSetupEvent or similar).
 * The registry is thread-safe for concurrent registrations.</p>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * // Register a custom rune type
 * IAltarRuneRegistry registry = NeoVitaeAPI.getInstance().getRuneRegistry();
 * registry.registerRuneType(MyRuneType.INSTANCE);
 *
 * // Associate a block with the rune type
 * registry.registerRuneBlock(MyBlocks.ARCANE_RUNE.get(), MyRuneType.INSTANCE, 1);
 *
 * // A block can provide multiple rune types
 * registry.registerRuneBlock(MyBlocks.HYBRID_RUNE.get(), EnumAltarRuneType.SPEED, 1);
 * registry.registerRuneBlock(MyBlocks.HYBRID_RUNE.get(), EnumAltarRuneType.CAPACITY, 1);
 * }</pre>
 */
public interface IAltarRuneRegistry {

    /**
     * Registers a custom rune type.
     *
     * <p>Built-in types from {@link EnumAltarRuneType} are automatically registered
     * and do not need to be re-registered.</p>
     *
     * @param runeType The rune type to register
     * @throws IllegalArgumentException if runeType is null, has a null ID, or is already registered
     */
    void registerRuneType(IAltarRuneType runeType);

    /**
     * Gets a registered rune type by its ID.
     *
     * @param id The rune type's resource location ID
     * @return The rune type, or null if not found
     */
    @Nullable
    IAltarRuneType getRuneType(ResourceLocation id);

    /**
     * Gets a registered rune type by its serialized name.
     *
     * @param name The serialized name
     * @return The rune type, or null if not found
     */
    @Nullable
    IAltarRuneType getRuneTypeByName(String name);

    /**
     * Gets all registered rune types.
     *
     * @return Unmodifiable collection of all rune types
     */
    Collection<IAltarRuneType> getAllRuneTypes();

    /**
     * Checks if a rune type is registered.
     *
     * @param id The rune type ID to check
     * @return True if registered
     */
    boolean isRegistered(ResourceLocation id);

    /**
     * Registers a block as providing one or more rune bonuses.
     *
     * <p>A single block can provide multiple rune types by calling this method
     * multiple times with different rune types.</p>
     *
     * @param block The block to register
     * @param runeType The rune type this block provides
     * @param amount The amount of bonus per block (usually 1)
     * @throws IllegalArgumentException if block or runeType is null, or amount is not positive
     */
    void registerRuneBlock(Block block, IAltarRuneType runeType, int amount);

    /**
     * Gets all rune types and amounts provided by a block.
     *
     * @param block The block to check
     * @return Unmodifiable map of rune types to amounts, or empty map if not a rune
     */
    Map<IAltarRuneType, Integer> getRunesForBlock(Block block);

    /**
     * Checks if a block is registered as providing any runes.
     *
     * @param block The block to check
     * @return True if the block provides any runes
     */
    boolean hasRunes(Block block);
}
