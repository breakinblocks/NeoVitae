package com.breakinblocks.neovitae.compat.patchouli.processors;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import com.breakinblocks.neovitae.common.item.BMItems;
import com.breakinblocks.neovitae.common.item.ItemRitualDiviner;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualRegistry;

/**
 * Patchouli processor for ritual information pages.
 *
 * Example Page: Info Page
 * {
 *   "type": "neovitae:ritual_info",
 *   "ritual": "ritual_id",
 *   "text_overrides": [
 *     ["text 1" , "formatting_code_1"],
 *     ["text 2" , "formatting_code_2"]
 *   ],
 *   "text": "Extra text."
 * }
 *
 * Example Page: Data Page
 * {
 *   "type": "neovitae:ritual_data",
 *   "ritual": "ritual_id",
 *   "page_type": "page_type_key",
 *   "text_overrides": [...],
 *   "heading_override": "heading",
 *   "item_override": "item_id",
 *   "text": "Extra text."
 * }
 *
 * Page types: "info", "raw", "corrosive", "destructive", "steadfast", "vengeful",
 * or any custom range key.
 */
public class RitualInfoProcessor implements IComponentProcessor {
    private Ritual ritual;
    private HolderLookup.Provider registries;
    private String pageType;
    private String extraText = "";
    private String heading;
    private ItemStack item;
    private String infoBlurb = "";
    private static final String LANGUAGE_BASE = "guide.patchouli.neovitae.ritual_info.";
    private static final String DIVINER_BASE = ItemRitualDiviner.TOOLTIP_BASE;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        this.registries = level.registryAccess();
        this.item = new ItemStack(BMItems.RITUAL_READER.get());

        String id = variables.get("ritual", registries).asString();
        ritual = RitualRegistry.getRitual(id);
        if (ritual == null) {
            LogManager.getLogger().warn("Guidebook given invalid Ritual ID {}", id);
            return;
        }

        if (variables.has("page_type")) {
            pageType = variables.get("page_type", registries).asString();
        } else {
            pageType = "info";
        }

        // Get, Format, and Set Info Blurb.
        // Also sets default heading and Item.
        boolean infoAlreadySet = false;
        String rangeInfo = "";
        switch (pageType) {
            case "info":
                infoBlurb = I18n.get(ritual.getTranslationKey() + ".info");
                infoAlreadySet = true;
                break;
            case "raw":
                infoBlurb = I18n.get(ritual.getTranslationKey() + ".default.info");
                heading = getAndRemoveLeadTitle();
                item = new ItemStack(BMItems.RAW_CRYSTAL.get());
                infoAlreadySet = true;
                break;
            case "corrosive":
                item = new ItemStack(BMItems.CORROSIVE_CRYSTAL.get());
                break;
            case "destructive":
                item = new ItemStack(BMItems.DESTRUCTIVE_CRYSTAL.get());
                break;
            case "steadfast":
                item = new ItemStack(BMItems.STEADFAST_CRYSTAL.get());
                break;
            case "vengeful":
                item = new ItemStack(BMItems.VENGEFUL_CRYSTAL.get());
                break;
            default:
                int volume = ritual.getMaxVolumeForRange(pageType);
                int horizontal = ritual.getMaxHorizontalRadiusForRange(pageType);
                int vertical = ritual.getMaxVerticalRadiusForRange(pageType);
                rangeInfo = I18n.get("guide.patchouli.neovitae.ritual_info.range_formatter",
                        volume == Integer.MAX_VALUE
                                ? I18n.get("guide.patchouli.neovitae.ritual_info.full_range")
                                : volume, horizontal, vertical);
        }

        if (!infoAlreadySet) {
            infoBlurb = I18n.get(ritual.getTranslationKey() + "." + pageType + ".info");
            heading = getAndRemoveLeadTitle();
        }
        infoBlurb += rangeInfo;

        if (variables.has("text_overrides")) {
            List<IVariable> varOverridePairs = variables.get("text_overrides", registries).asList(registries);
            List<List<String>> overrideTable = new ArrayList<>();
            for (IVariable varPair : varOverridePairs) {
                List<String> pair = new ArrayList<>();
                varPair.asStream(registries).forEach(p -> pair.add(p.asString()));
                overrideTable.add(pair);
            }
            for (List<String> pair : overrideTable) {
                String text = pair.get(0);
                String code = pair.get(1);
                infoBlurb = infoBlurb.replaceAll(
                        String.format("\\b%s\\b", text),
                        I18n.get("guide.patchouli.neovitae.ritual_info.text_override_formatter", code, text));
            }
        }

        if (variables.has("heading_override")) {
            heading = variables.get("heading_override", registries).asString();
        }

        if (variables.has("item_override")) {
            item = variables.get("item_override", registries).as(ItemStack.class);
        }

        if (variables.has("text")) {
            extraText = variables.get("text", registries).asString();
        }
    }

    @Override
    public IVariable process(Level level, String key) {
        if (ritual == null) {
            return null;
        }

        switch (key) {
            case "auto_text":
                StringBuilder outputText = new StringBuilder();
                if (pageType.equals("info")) {
                    outputText.append(infoPageSetup());
                } else {
                    outputText.append(infoBlurb);
                }
                outputText.append(I18n.get("guide.patchouli.neovitae.common.double_new_line", extraText));
                return IVariable.wrap(outputText.toString(), registries);

            case "heading":
                return IVariable.wrap(heading, registries);

            case "item":
                return IVariable.from(item, registries);
        }
        return null;
    }

    /**
     * For entries that start with "(title) ...", extract the title to use as the
     * default heading, and remove it from the body text.
     */
    private String getAndRemoveLeadTitle() {
        if (!infoBlurb.isEmpty() && infoBlurb.charAt(0) == '(') {
            int closeIndex = infoBlurb.indexOf(")");
            if (closeIndex > 1) {
                String leadTitle = infoBlurb.substring(1, closeIndex);
                infoBlurb = infoBlurb.replaceFirst("^\\(" + leadTitle + "\\) ", "");
                return leadTitle;
            }
        }
        return "";
    }

    /**
     * Sets up the info page information (description, rune counts, crystal,
     * activation cost, and upkeep cost).
     */
    private String infoPageSetup() {
        StringBuilder runeCounts = new StringBuilder();
        Map<EnumRuneType, Integer> runeMap = countRunes(ritual);
        int totalRunes = 0;

        for (EnumRuneType type : EnumRuneType.values()) {
            int count = runeMap.getOrDefault(type, 0);
            totalRunes += count;
            if (count > 0) {
                runeCounts.append(I18n.get(LANGUAGE_BASE + "counter_formatter",
                        type.bookColor,
                        I18n.get(DIVINER_BASE + type.translationKey, count)));
            }
        }

        String totalRuneCount = I18n.get(DIVINER_BASE + "totalRune", totalRunes);

        String crystalLevel;
        switch (ritual.getCrystalLevel()) {
            case 0:
                crystalLevel = I18n.get(LANGUAGE_BASE + "weak_activation_crystal_link",
                        I18n.get("item.neovitae.activation_crystal_weak"));
                break;
            case 1:
                crystalLevel = I18n.get(LANGUAGE_BASE + "awakened_activation_crystal_link",
                        I18n.get("item.neovitae.activation_crystal_awakened"));
                break;
            default:
                crystalLevel = I18n.get("item.neovitae.activation_crystal_creative");
        }

        String activationCost = I18n.get(LANGUAGE_BASE + "activation_cost", ritual.getActivationCost());

        String upkeepCost = "";
        if (ritual.getRefreshCost() != 0) {
            upkeepCost = I18n.get(LANGUAGE_BASE + "upkeep_cost", ritual.getRefreshCost(), ritual.getRefreshTime());
        }

        return I18n.get(LANGUAGE_BASE + "info_formatter", infoBlurb, runeCounts.toString(),
                totalRuneCount, crystalLevel, activationCost, upkeepCost);
    }

    /**
     * Counts the runes required for this ritual.
     */
    private Map<EnumRuneType, Integer> countRunes(Ritual ritual) {
        Map<EnumRuneType, Integer> counts = new EnumMap<>(EnumRuneType.class);
        List<RitualComponent> components = Lists.newArrayList();
        ritual.gatherComponents(components::add);
        for (RitualComponent component : components) {
            counts.merge(component.runeType(), 1, Integer::sum);
        }
        return counts;
    }
}
