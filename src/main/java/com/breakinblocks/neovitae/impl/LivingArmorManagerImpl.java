package com.breakinblocks.neovitae.impl;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.living.ILivingArmorManager;
import com.breakinblocks.neovitae.api.living.ILivingArmorManager.UpgradeInfo;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.LivingStats;
import com.breakinblocks.neovitae.common.living.LivingHelper;
import com.breakinblocks.neovitae.common.living.LivingUpgrade;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Living Armor API.
 */
public class LivingArmorManagerImpl implements ILivingArmorManager {

    public static final LivingArmorManagerImpl INSTANCE = new LivingArmorManagerImpl();

    private LivingArmorManagerImpl() {
        // Private constructor - use INSTANCE
    }

    @Override
    public boolean hasFullSet(Player player) {
        return LivingHelper.hasFullSet(player);
    }

    @Override
    public ItemStack getChestPiece(Player player) {
        return LivingHelper.getChest(player);
    }

    @Override
    public List<UpgradeInfo> getUpgrades(Player player) {
        if (!hasFullSet(player)) {
            return List.of();
        }

        List<UpgradeInfo> result = new ArrayList<>();
        List<LivingHelper.UpgradeInstance> upgrades = LivingHelper.getUpgrades(player);

        for (LivingHelper.UpgradeInstance instance : upgrades) {
            Holder<LivingUpgrade> upgradeHolder = instance.upgrade();
            int level = instance.level();

            if (upgradeHolder.isBound() && upgradeHolder.unwrapKey().isPresent()) {
                ResourceLocation id = upgradeHolder.unwrapKey().get().location();
                LivingUpgrade upgrade = upgradeHolder.value();
                float experience = getUpgradeExperience(player, id);
                int pointCost = upgrade.levels().levelToCost().getOrDefault(level, 0);

                result.add(new UpgradeInfo(id, level, experience, pointCost));
            }
        }

        return result;
    }

    @Override
    public int getUpgradeLevel(Player player, ResourceLocation upgradeId) {
        if (!hasFullSet(player)) {
            return 0;
        }

        List<LivingHelper.UpgradeInstance> upgrades = LivingHelper.getUpgrades(player);
        for (LivingHelper.UpgradeInstance instance : upgrades) {
            if (instance.upgrade().unwrapKey().isPresent() &&
                instance.upgrade().unwrapKey().get().location().equals(upgradeId)) {
                return instance.level();
            }
        }
        return 0;
    }

    @Override
    public boolean grantUpgradeExperience(Player player, ResourceLocation upgradeId, float amount) {
        if (!hasFullSet(player)) {
            return false;
        }

        // LivingHelper.applyExp handles the experience addition
        // We need to find the upgrade holder first
        List<LivingHelper.UpgradeInstance> upgrades = LivingHelper.getUpgrades(player);
        for (LivingHelper.UpgradeInstance instance : upgrades) {
            if (instance.upgrade().unwrapKey().isPresent() &&
                instance.upgrade().unwrapKey().get().location().equals(upgradeId)) {
                LivingHelper.applyExp(player, instance.upgrade(), amount);
                return true;
            }
        }
        return false;
    }

    @Override
    public float getUpgradeExperience(Player player, ResourceLocation upgradeId) {
        if (!hasFullSet(player)) {
            return 0;
        }

        ItemStack chest = getChestPiece(player);
        if (chest.isEmpty()) {
            return 0;
        }

        LivingStats stats = chest.get(BMDataComponents.UPGRADES.get());
        if (stats == null) {
            return 0;
        }

        // Find the upgrade by its ResourceLocation
        for (Object2FloatMap.Entry<Holder<LivingUpgrade>> entry : stats.upgrades().object2FloatEntrySet()) {
            if (entry.getKey().unwrapKey().isPresent() &&
                entry.getKey().unwrapKey().get().location().equals(upgradeId)) {
                return entry.getFloatValue();
            }
        }
        return 0;
    }

    @Override
    public int getUsedUpgradePoints(Player player) {
        if (!hasFullSet(player)) {
            return 0;
        }

        int totalPoints = 0;
        List<LivingHelper.UpgradeInstance> upgrades = LivingHelper.getUpgrades(player);
        for (LivingHelper.UpgradeInstance instance : upgrades) {
            LivingUpgrade upgrade = instance.upgrade().value();
            totalPoints += upgrade.levels().levelToCost().getOrDefault(instance.level(), 0);
        }
        return totalPoints;
    }

    @Override
    public int getMaxUpgradePoints() {
        return NeoVitae.SERVER_CONFIG.DEFAULT_UPGRADE_POINTS.get();
    }

    @Override
    public int getMaxUpgradePoints(Player player) {
        if (!hasFullSet(player)) {
            return getMaxUpgradePoints();
        }

        ItemStack chest = getChestPiece(player);
        if (chest.isEmpty()) {
            return getMaxUpgradePoints();
        }

        Integer maxPoints = chest.get(BMDataComponents.CURRENT_MAX_UPGRADE_POINTS.get());
        return maxPoints != null ? maxPoints : getMaxUpgradePoints();
    }

    @Override
    public int getAvailableUpgradePoints(Player player) {
        return getMaxUpgradePoints(player) - getUsedUpgradePoints(player);
    }
}
