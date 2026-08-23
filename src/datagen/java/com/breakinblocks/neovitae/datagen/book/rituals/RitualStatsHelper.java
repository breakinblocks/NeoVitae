package com.breakinblocks.neovitae.datagen.book.rituals;

import com.breakinblocks.neovitae.common.datamap.RitualStats;
import com.breakinblocks.neovitae.datagen.provider.RitualStatsProvider;
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

    private static Map<ResourceLocation, RitualStats> statsTable;

    private static Map<ResourceLocation, RitualStats> stats() {
        if (statsTable == null) {
            Map<ResourceLocation, RitualStats> table = new HashMap<>();
            RitualStatsProvider.declare((holder, stats) -> table.put(holder.getId(), stats));
            statsTable = table;
        }
        return statsTable;
    }

    public static String generateStats(String ritualName) {
        Ritual ritual = RitualRegistry.getRitual(ritualName);
        if (ritual == null) return "Ritual data unavailable.";

        ResourceLocation id = RitualRegistry.getId(ritual);
        RitualStats stats = id == null ? null : stats().get(id);

        StringBuilder sb = new StringBuilder();

        // Activation cost
        int activationCost = stats != null ? stats.activationCost() : ritual.getActivationCost();
        sb.append("[#](8B0000)Activation Cost:[#]() ").append(String.format("%,d", activationCost)).append(" EV\\\n");

        // Crystal level
        int crystalLevel = stats != null ? stats.crystalLevel() : ritual.getCrystalLevel();
        String crystalName = switch (crystalLevel) {
            case 0 -> "Weak Activation Crystal";
            case 1 -> "Awakened Activation Crystal";
            case 2 -> "Creative Activation Crystal";
            default -> "Activation Crystal (Tier " + crystalLevel + ")";
        };
        sb.append("[#](8B0000)Crystal:[#]() ").append(crystalName).append("\\\n");

        // Refresh cost
        int refreshCost = stats != null ? stats.refreshCost() : ritual.getRefreshCost();
        int refreshTime = stats != null ? stats.refreshTime() : ritual.getRefreshTime();
        if (refreshCost > 0) {
            boolean perOperation = stats != null && stats.perOperation();
            sb.append(perOperation ? "[#](8B0000)Cost:[#]() " : "[#](8B0000)Upkeep:[#]() ")
                    .append(String.format("%,d", refreshCost)).append(" EV");
            if (perOperation) {
                sb.append(" per use");
            } else if (refreshTime == 1) {
                sb.append("/tick");
            } else if (refreshTime == 20) {
                sb.append("/sec");
            } else {
                sb.append(" every ").append(refreshTime).append(" ticks");
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
            case TENEBRAE -> "Tenebrae Ritual Stone";
            case DEUS -> "Deus Ritual Stone";
        };
    }
}
