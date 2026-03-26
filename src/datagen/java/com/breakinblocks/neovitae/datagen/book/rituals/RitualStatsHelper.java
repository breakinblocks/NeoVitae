package com.breakinblocks.neovitae.datagen.book.rituals;

import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

/**
 * Generates formatted ritual stats text for book entries at datagen time.
 */
public class RitualStatsHelper {

    public static String generateStats(String ritualName) {
        Ritual ritual = RitualRegistry.getRitual(ritualName);
        if (ritual == null) return "Ritual data unavailable.";

        StringBuilder sb = new StringBuilder();

        // Activation cost
        int activationCost = ritual.getActivationCost();
        sb.append("[#](8B0000)Activation Cost:[#]() ").append(String.format("%,d", activationCost)).append(" EV\\\n");

        // Crystal level
        int crystalLevel = ritual.getCrystalLevel();
        String crystalName = switch (crystalLevel) {
            case 0 -> "Weak Activation Crystal";
            case 1 -> "Awakened Activation Crystal";
            case 2 -> "Creative Activation Crystal";
            default -> "Activation Crystal (Tier " + crystalLevel + ")";
        };
        sb.append("[#](8B0000)Crystal:[#]() ").append(crystalName).append("\\\n");

        // Refresh cost
        int refreshCost = ritual.getRefreshCost();
        int refreshTime = ritual.getRefreshTime();
        if (refreshCost > 0) {
            sb.append("[#](8B0000)Upkeep:[#]() ").append(String.format("%,d", refreshCost)).append(" EV");
            if (refreshTime != 20) {
                sb.append(" every ").append(refreshTime).append(" ticks");
            } else {
                sb.append("/sec");
            }
            sb.append("\\\n");
        }

        // Rune counts
        List<RitualComponent> components = new ArrayList<>();
        ritual.gatherComponents(components::add);

        if (!components.isEmpty()) {
            Map<EnumRuneType, Integer> runeCounts = new LinkedHashMap<>();
            for (RitualComponent comp : components) {
                runeCounts.merge(comp.runeType(), 1, Integer::sum);
            }

            sb.append("\\\n[#](8B0000)Rune Requirements:[#]()\\\n");
            for (Map.Entry<EnumRuneType, Integer> entry : runeCounts.entrySet()) {
                String runeName = formatRuneName(entry.getKey());
                sb.append("  ").append(entry.getValue()).append("x ").append(runeName).append("\\\n");
            }
            sb.append("  [#](8B0000)Total:[#]() ").append(components.size()).append(" runes");
        }

        return sb.toString();
    }

    private static String formatRuneName(EnumRuneType type) {
        return switch (type) {
            case BLANK -> "Blank Ritual Stone";
            case WATER -> "Water Ritual Stone";
            case FIRE -> "Fire Ritual Stone";
            case EARTH -> "Earth Ritual Stone";
            case AIR -> "Air Ritual Stone";
            case DUSK -> "Dusk Ritual Stone";
            case DAWN -> "Dawn Ritual Stone";
        };
    }
}
