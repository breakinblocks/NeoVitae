package com.breakinblocks.neovitae.util;

import com.breakinblocks.neovitae.api.altar.rune.IAltarRuneType;
import com.breakinblocks.neovitae.api.altar.rune.RuneInstance;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Result of scanning an altar structure for runes.
 *
 * <p>Contains both the aggregated rune counts (for stat calculation) and
 * the individual rune instances (for addon mods that need to inspect
 * specific rune blocks).</p>
 *
 * @param runeCounts Map of rune types to their total counts
 * @param runeInstances List of all individual rune instances found
 */
public record AltarScanResult(
        Map<IAltarRuneType, Integer> runeCounts,
        List<RuneInstance> runeInstances
) {
    /**
     * Creates an empty scan result (no runes found).
     */
    public static AltarScanResult empty() {
        return new AltarScanResult(Collections.emptyMap(), Collections.emptyList());
    }

    /**
     * Gets the count of a specific rune type.
     *
     * @param type The rune type to check
     * @return The count, or 0 if not present
     */
    public int getRuneCount(IAltarRuneType type) {
        return runeCounts.getOrDefault(type, 0);
    }

    /**
     * Checks if any runes were found.
     *
     * @return True if at least one rune was found
     */
    public boolean hasRunes() {
        return !runeInstances.isEmpty();
    }

    /**
     * Gets the total number of rune blocks found.
     *
     * @return The number of rune instances
     */
    public int getTotalRuneCount() {
        return runeInstances.size();
    }
}
