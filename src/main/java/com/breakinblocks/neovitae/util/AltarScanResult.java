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
 */
public record AltarScanResult(
        Map<IAltarRuneType, Integer> runeCounts,
        List<RuneInstance> runeInstances
) {
    public static AltarScanResult empty() {
        return new AltarScanResult(Collections.emptyMap(), Collections.emptyList());
    }

    public int getRuneCount(IAltarRuneType type) {
        return runeCounts.getOrDefault(type, 0);
    }

    public boolean hasRunes() {
        return !runeInstances.isEmpty();
    }

    public int getTotalRuneCount() {
        return runeInstances.size();
    }
}
