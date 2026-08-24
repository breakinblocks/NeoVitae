package com.breakinblocks.neovitae.compat.emi.recipe;

import com.breakinblocks.neovitae.compat.emi.NVEmiCategories;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.text.DecimalFormat;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RitualEmiRecipe extends BasicEmiRecipe {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###");
    private static final int LINE = 10;
    private static final int COLUMN_TOP = 16;
    private static final int LEFT_COL = 4;
    private static final int RIGHT_COL = 88;
    private static final int WIDTH = 170;
    private static final int HEIGHT = COLUMN_TOP + LINE + EnumRuneType.values().length * LINE + 4;

    private final String translationKey;
    private final int activationCost;
    private final int refreshCost;
    private final int crystalLevel;
    private final int totalRunes;
    private final Map<EnumRuneType, Integer> runeCounts;

    public RitualEmiRecipe(ResourceLocation ritualId, String translationKey, int activationCost, int refreshCost,
                           int crystalLevel, List<RitualComponent> components) {
        super(NVEmiCategories.RITUAL, NVEmiCategories.synthetic(ritualId), WIDTH, HEIGHT);
        this.translationKey = translationKey;
        this.activationCost = activationCost;
        this.refreshCost = refreshCost;
        this.crystalLevel = crystalLevel;
        this.totalRunes = components.size();
        this.runeCounts = new EnumMap<>(EnumRuneType.class);
        for (RitualComponent component : components) {
            runeCounts.merge(component.runeType(), 1, Integer::sum);
        }
        this.inputs = List.of();
        this.outputs = List.of();
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        var font = Minecraft.getInstance().font;

        Component ritualName = Component.translatable(translationKey);
        widgets.addText(ritualName, (WIDTH - font.width(ritualName)) / 2, 2, 0x404040, false);

        int leftY = COLUMN_TOP;
        widgets.addText(crystalTierName(), LEFT_COL, leftY, 0x606060, false);
        leftY += 12;
        widgets.addText(Component.translatable("jei.neovitae.recipe.ritual.activation"), LEFT_COL, leftY, 0x606060, false);
        leftY += 10;
        widgets.addText(Component.literal(DECIMAL_FORMAT.format(activationCost) + " EV"), LEFT_COL + 4, leftY, 0x808080, false);
        leftY += 12;
        widgets.addText(Component.translatable("jei.neovitae.recipe.ritual.refresh"), LEFT_COL, leftY, 0x606060, false);
        leftY += 10;
        widgets.addText(Component.literal(DECIMAL_FORMAT.format(refreshCost) + " EV/op"), LEFT_COL + 4, leftY, 0x808080, false);

        int rightY = COLUMN_TOP;
        widgets.addText(Component.translatable("jei.neovitae.recipe.ritual.total_runes", totalRunes), RIGHT_COL, rightY, 0x606060, false);
        rightY += LINE;

        for (EnumRuneType runeType : EnumRuneType.values()) {
            int count = runeCounts.getOrDefault(runeType, 0);
            if (count == 0) continue;
            String runeName = capitalize(runeType.getSerializedName());
            Integer color = runeType.colorCode.getColor();
            widgets.addText(Component.literal(count + "x " + runeName).withStyle(runeType.colorCode),
                    RIGHT_COL + 4, rightY, color != null ? color : 0x808080, false);
            rightY += LINE;
        }
    }

    private Component crystalTierName() {
        return switch (crystalLevel) {
            case 0 -> Component.translatable("jei.neovitae.recipe.ritual.crystal.weak");
            case 1 -> Component.translatable("jei.neovitae.recipe.ritual.crystal.awakened");
            default -> Component.translatable("jei.neovitae.recipe.ritual.crystal.creative");
        };
    }

    private static String capitalize(String value) {
        if (value == null || value.isEmpty()) return value;
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
    }
}
