package com.breakinblocks.neovitae.impl;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.altar.rune.EnumAltarRuneType;
import com.breakinblocks.neovitae.api.altar.rune.IAltarRuneRegistry;
import com.breakinblocks.neovitae.api.altar.rune.IAltarRuneType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the unified Altar Rune Registry.
 *
 * <p>This registry manages both built-in and custom rune types, as well as
 * block-to-rune associations. Built-in {@link EnumAltarRuneType} values are
 * pre-registered during construction.</p>
 */
public class AltarRuneRegistryImpl implements IAltarRuneRegistry {

    public static final AltarRuneRegistryImpl INSTANCE = new AltarRuneRegistryImpl();

    // Thread-safe maps for runtime registration
    private final Map<ResourceLocation, IAltarRuneType> runeTypesById = new ConcurrentHashMap<>();
    private final Map<String, IAltarRuneType> runeTypesByName = new ConcurrentHashMap<>();
    private final Map<Block, Map<IAltarRuneType, Integer>> blockToRunes = new ConcurrentHashMap<>();

    private AltarRuneRegistryImpl() {
        // Pre-register all built-in rune types
        for (EnumAltarRuneType builtIn : EnumAltarRuneType.values()) {
            runeTypesById.put(builtIn.getId(), builtIn);
            runeTypesByName.put(builtIn.getSerializedName(), builtIn);
        }
        NeoVitae.LOGGER.debug("Registered {} built-in altar rune types", EnumAltarRuneType.values().length);
    }

    // ========================================
    // Rune Type Registration
    // ========================================

    @Override
    public void registerRuneType(IAltarRuneType runeType) {
        if (runeType == null) {
            throw new IllegalArgumentException("Rune type cannot be null");
        }
        if (runeType.getId() == null) {
            throw new IllegalArgumentException("Rune type ID cannot be null");
        }
        if (runeType.getSerializedName() == null || runeType.getSerializedName().isEmpty()) {
            throw new IllegalArgumentException("Rune type serialized name cannot be null or empty");
        }

        // Check if this is a built-in type being re-registered (not an error, just skip)
        if (runeType instanceof EnumAltarRuneType) {
            NeoVitae.LOGGER.debug("Skipping registration of built-in rune type: {}", runeType.getId());
            return;
        }

        ResourceLocation id = runeType.getId();
        if (runeTypesById.containsKey(id)) {
            throw new IllegalArgumentException("A rune type with ID '" + id + "' is already registered");
        }

        String name = runeType.getSerializedName();
        if (runeTypesByName.containsKey(name)) {
            NeoVitae.LOGGER.warn("Registering rune type '{}' with serialized name '{}' that already exists. " +
                    "Name lookups may return unexpected results.", id, name);
        }

        runeTypesById.put(id, runeType);
        runeTypesByName.put(name, runeType);

        NeoVitae.LOGGER.debug("Registered custom altar rune type: {}", id);
    }

    @Override
    @Nullable
    public IAltarRuneType getRuneType(ResourceLocation id) {
        return runeTypesById.get(id);
    }

    @Override
    @Nullable
    public IAltarRuneType getRuneTypeByName(String name) {
        return runeTypesByName.get(name);
    }

    @Override
    public Collection<IAltarRuneType> getAllRuneTypes() {
        return Collections.unmodifiableCollection(runeTypesById.values());
    }

    @Override
    public boolean isRegistered(ResourceLocation id) {
        return runeTypesById.containsKey(id);
    }

    // ========================================
    // Block Registration
    // ========================================

    @Override
    public void registerRuneBlock(Block block, IAltarRuneType runeType, int amount) {
        if (block == null) {
            throw new IllegalArgumentException("Block cannot be null");
        }
        if (runeType == null) {
            throw new IllegalArgumentException("Rune type cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        // Verify the rune type is registered (should always pass for EnumAltarRuneType)
        if (!isRegistered(runeType.getId())) {
            NeoVitae.LOGGER.warn("Registering block {} with unregistered rune type {}. " +
                    "Consider registering the rune type first.", block, runeType.getId());
        }

        blockToRunes.computeIfAbsent(block, k -> new ConcurrentHashMap<>()).put(runeType, amount);

        NeoVitae.LOGGER.info("Registered block {} (hash={}) as rune {} with amount {}",
                block, System.identityHashCode(block), runeType.getId(), amount);
    }

    @Override
    public Map<IAltarRuneType, Integer> getRunesForBlock(Block block) {
        Map<IAltarRuneType, Integer> runes = blockToRunes.get(block);
        if (runes == null) {
            // Only log at warn level if registry is non-empty (i.e. it should have been found)
            if (!blockToRunes.isEmpty()) {
                NeoVitae.LOGGER.warn("getRunesForBlock: No runes found for {} (hash={}), registry has {} entries",
                        block, System.identityHashCode(block), blockToRunes.size());
            }
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(runes);
    }

    @Override
    public boolean hasRunes(Block block) {
        Map<IAltarRuneType, Integer> runes = blockToRunes.get(block);
        return runes != null && !runes.isEmpty();
    }

    // ========================================
    // Internal Methods
    // ========================================

    /**
     * Gets all registered block-to-rune associations.
     * Used internally for structure scanning.
     *
     * @return Unmodifiable view of all block associations
     */
    public Map<Block, Map<IAltarRuneType, Integer>> getAllBlockAssociations() {
        Map<Block, Map<IAltarRuneType, Integer>> result = new HashMap<>();
        for (Map.Entry<Block, Map<IAltarRuneType, Integer>> entry : blockToRunes.entrySet()) {
            result.put(entry.getKey(), Collections.unmodifiableMap(entry.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }

    /**
     * Clears all registrations except built-in types. Used for testing purposes only.
     */
    void clearCustomRegistrations() {
        // Re-initialize with only built-in types
        runeTypesById.clear();
        runeTypesByName.clear();
        blockToRunes.clear();

        for (EnumAltarRuneType builtIn : EnumAltarRuneType.values()) {
            runeTypesById.put(builtIn.getId(), builtIn);
            runeTypesByName.put(builtIn.getSerializedName(), builtIn);
        }
    }
}
