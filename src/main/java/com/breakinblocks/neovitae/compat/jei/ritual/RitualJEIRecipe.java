package com.breakinblocks.neovitae.compat.jei.ritual;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.RitualComponent;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Data holder for displaying rituals in JEI.
 *
 * @param ritualId        The ritual's registry ID
 * @param translationKey  Translation key prefix for the ritual
 * @param activationCost  EV cost to activate
 * @param refreshCost     EV cost per operation
 * @param crystalLevel    Required activation crystal tier (0 = weak, 1 = awakened)
 * @param components      List of rune components
 * @param runeCounts      Map of rune type to count
 */
public record RitualJEIRecipe(
        ResourceLocation ritualId,
        String translationKey,
        int activationCost,
        int refreshCost,
        int crystalLevel,
        List<RitualComponent> components,
        Map<EnumRuneType, Integer> runeCounts
) {
    public static RitualJEIRecipe create(ResourceLocation id, String translationKey, int activationCost,
                                          int refreshCost, int crystalLevel, List<RitualComponent> components) {
        Map<EnumRuneType, Integer> counts = new EnumMap<>(EnumRuneType.class);
        for (RitualComponent component : components) {
            counts.merge(component.runeType(), 1, Integer::sum);
        }
        return new RitualJEIRecipe(id, translationKey, activationCost, refreshCost, crystalLevel, components, counts);
    }

    public Component getRitualName() {
        return Component.translatable(translationKey);
    }

    public Component getDescription() {
        return Component.translatable(translationKey + ".info");
    }

    public int getTotalRunes() {
        return components.size();
    }

    public int getRuneCount(EnumRuneType type) {
        return runeCounts.getOrDefault(type, 0);
    }

    public Component getCrystalTierName() {
        return switch (crystalLevel) {
            case 0 -> Component.translatable("jei.neovitae.recipe.ritual.crystal.weak");
            case 1 -> Component.translatable("jei.neovitae.recipe.ritual.crystal.awakened");
            default -> Component.translatable("jei.neovitae.recipe.ritual.crystal.creative");
        };
    }
}
