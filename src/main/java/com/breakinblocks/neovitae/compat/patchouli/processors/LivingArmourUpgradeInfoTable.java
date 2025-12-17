package com.breakinblocks.neovitae.compat.patchouli.processors;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import com.breakinblocks.neovitae.common.living.LivingUpgrade;
import com.breakinblocks.neovitae.common.registry.BMRegistries;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Patchouli processor for Living Armour upgrade information tables.
 *
 * Example Page:
 * {
 *   "type": "neovitae:living_armour_upgrade_table",
 *   "upgrade": "neovitae:melee_damage",
 *   "text": "Extra text."
 * }
 */
public class LivingArmourUpgradeInfoTable implements IComponentProcessor {
    private LivingUpgrade upgrade;
    private String extraText = "";
    private HolderLookup.Provider registries;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        this.registries = level.registryAccess();
        ResourceLocation id = ResourceLocation.parse(variables.get("upgrade", registries).asString());

        // Get the living upgrades registry
        Optional<Registry<LivingUpgrade>> registryOpt = level.registryAccess()
                .registry(BMRegistries.Keys.LIVING_UPGRADES);

        if (registryOpt.isPresent()) {
            Registry<LivingUpgrade> registry = registryOpt.get();
            LivingUpgrade found = registry.get(id);
            if (found != null) {
                this.upgrade = found;
            } else {
                LogManager.getLogger().warn("Guidebook given invalid Living Armour Upgrade ID {}", id);
            }
        } else {
            LogManager.getLogger().warn("Living Upgrades registry not available");
        }

        if (variables.has("text")) {
            extraText = variables.get("text", registries).asString();
        }
    }

    @Override
    public IVariable process(Level level, String key) {
        if (this.upgrade == null) {
            return null;
        }

        if (key.equals("table")) {
            StringBuilder output = new StringBuilder();
            String i18nLevel = Component.translatable("guide.patchouli.neovitae.living_armour_upgrade_table.level").getString();
            String i18nUpgradePoints = Component.translatable("guide.patchouli.neovitae.living_armour_upgrade_table.upgrade_points").getString();

            LivingUpgrade.Levels levels = upgrade.levels();
            TreeMap<Integer, Integer> expToLevel = levels.expToLevel();
            Map<Integer, Integer> levelToCost = levels.levelToCost();

            if (!expToLevel.isEmpty()) {
                int maxLevel = expToLevel.lastEntry().getValue();
                int maxLevelLength = Integer.toString(maxLevel).length();
                int maxUpgradePoints = levelToCost.values().stream().mapToInt(Integer::intValue).max().orElse(0);
                int maxUpgradePointsLength = Integer.toString(maxUpgradePoints).length();

                for (Map.Entry<Integer, Integer> entry : expToLevel.entrySet()) {
                    int upgradeLevel = entry.getValue();
                    int upgradePoints = levelToCost.getOrDefault(upgradeLevel, 0);

                    String formatStr = String.format("%s %%%dd: %%%dd %s$(br)", i18nLevel, maxLevelLength, maxUpgradePointsLength, i18nUpgradePoints);
                    output.append(String.format(formatStr, upgradeLevel, upgradePoints));
                }
            }

            output.append(String.format("%s%s", "$(br2)", extraText));

            return IVariable.wrap(output.toString(), registries);
        }
        return null;
    }
}
