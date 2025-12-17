package com.breakinblocks.neovitae.api.altar.rune;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

/**
 * Unified registry for Blood Altar rune types and block associations.
 *
 * <p>This registry manages both built-in rune types ({@link EnumAltarRuneType}) and
 * custom rune types implemented via {@link IAltarRuneType}. All rune blocks should
 * be registered through this registry for proper altar functionality.</p>
 *
 * <p>Access via {@link com.breakinblocks.neovitae.api.NeoVitaeAPI#get()} and then
 * {@link com.breakinblocks.neovitae.api.INeoVitaeAPI#getRuneRegistry()}.</p>
 *
 * <p>Example usage for addon mods:</p>
 * <pre>{@code
 * // During mod initialization (FMLCommonSetupEvent)
 * IAltarRuneRegistry registry = NeoVitaeAPI.get().getRuneRegistry();
 *
 * // For custom rune types, register the type first
 * MyRuneType myRune = new MyRuneType();
 * registry.registerRuneType(myRune);
 *
 * // Register a block with the custom rune type
 * registry.registerRuneBlock(ModBlocks.MY_RUNE_BLOCK.get(), myRune, 1);
 *
 * // You can also register blocks with built-in rune types
 * registry.registerRuneBlock(ModBlocks.BETTER_SPEED_RUNE.get(), EnumAltarRuneType.SPEED, 2);
 * }</pre>
 *
 * @see IAltarRuneType
 * @see EnumAltarRuneType
 * @see com.breakinblocks.neovitae.api.event.AltarRuneEvent
 */
public interface IAltarRuneRegistry {

    // ========================================
    // Rune Type Registration
    // ========================================

    /**
     * Registers a custom rune type.
     *
     * <p>This should be called during mod initialization, ideally in a
     * {@code FMLCommonSetupEvent} handler using {@code enqueueWork()}.</p>
     *
     * <p>Note: Built-in {@link EnumAltarRuneType} values are automatically registered
     * and do not need to be registered manually.</p>
     *
     * @param runeType The rune type to register
     * @throws IllegalArgumentException If a rune type with the same ID is already registered
     */
    void registerRuneType(IAltarRuneType runeType);

    /**
     * Gets a registered rune type by its ID.
     *
     * <p>This returns both built-in and custom rune types.</p>
     *
     * @param id The resource location ID
     * @return The rune type, or null if not found
     */
    @Nullable
    IAltarRuneType getRuneType(ResourceLocation id);

    /**
     * Gets a registered rune type by its serialized name.
     *
     * <p>Note: If multiple rune types have the same serialized name (different namespaces),
     * this may return any one of them. Use {@link #getRuneType(ResourceLocation)} for
     * unambiguous lookups.</p>
     *
     * @param name The serialized name (e.g., "speed", "mana_rune")
     * @return The rune type, or null if not found
     */
    @Nullable
    IAltarRuneType getRuneTypeByName(String name);

    /**
     * Gets all registered rune types.
     *
     * <p>This includes both built-in {@link EnumAltarRuneType} values and
     * custom rune types registered via {@link #registerRuneType(IAltarRuneType)}.</p>
     *
     * @return An unmodifiable collection of all registered rune types
     */
    Collection<IAltarRuneType> getAllRuneTypes();

    /**
     * Checks if a rune type with the given ID is registered.
     *
     * @param id The resource location ID
     * @return True if registered, false otherwise
     */
    boolean isRegistered(ResourceLocation id);

    // ========================================
    // Block Registration
    // ========================================

    /**
     * Associates a block with a rune type.
     *
     * <p>When this block is placed as part of a Blood Altar structure, it will
     * contribute the specified amount to the rune type's count.</p>
     *
     * <p>This method works with both built-in and custom rune types:</p>
     * <pre>{@code
     * // Built-in rune type
     * registry.registerRuneBlock(myBlock, EnumAltarRuneType.SPEED, 1);
     *
     * // Custom rune type
     * registry.registerRuneBlock(myBlock, myCustomRuneType, 1);
     * }</pre>
     *
     * <p>A single block can provide multiple rune types by calling this method
     * multiple times with different rune types.</p>
     *
     * @param block The block to register as a rune
     * @param runeType The rune type (built-in or custom)
     * @param amount The amount this block contributes (typically 1)
     * @throws IllegalArgumentException If block is null, runeType is null, or amount is not positive
     */
    void registerRuneBlock(Block block, IAltarRuneType runeType, int amount);

    /**
     * Gets all rune associations for a block.
     *
     * <p>Returns a map of rune type to amount for all runes this block provides.</p>
     *
     * @param block The block to query
     * @return A map of rune type to amount, or empty map if no associations
     */
    Map<IAltarRuneType, Integer> getRunesForBlock(Block block);

    /**
     * Checks if a block has any rune associations.
     *
     * @param block The block to check
     * @return True if the block provides any rune types
     */
    boolean hasRunes(Block block);

    // ========================================
    // Convenience Methods
    // ========================================

    /**
     * Gets the rune count for a specific type from a block.
     *
     * @param block The block to query
     * @param runeType The rune type to check
     * @return The amount this block contributes, or 0 if none
     */
    default int getRuneAmount(Block block, IAltarRuneType runeType) {
        return getRunesForBlock(block).getOrDefault(runeType, 0);
    }

    /**
     * Checks if a block provides a specific rune type.
     *
     * @param block The block to check
     * @param runeType The rune type to check for
     * @return True if the block provides this rune type
     */
    default boolean hasRuneType(Block block, IAltarRuneType runeType) {
        return getRunesForBlock(block).containsKey(runeType);
    }
}
