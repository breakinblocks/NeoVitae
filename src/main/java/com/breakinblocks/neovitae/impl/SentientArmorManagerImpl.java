package com.breakinblocks.neovitae.impl;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.sentient.ISentientArmorManager;
import com.breakinblocks.neovitae.api.sentient.ISentientArmorManager.UpgradeInfo;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SentientStats;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;

import java.util.ArrayList;
import java.util.List;

public class SentientArmorManagerImpl implements ISentientArmorManager {

    public static final SentientArmorManagerImpl INSTANCE = new SentientArmorManagerImpl();

    private SentientArmorManagerImpl() {}


    @Override
    public boolean hasFullSet(Player player) {
        return SentientHelper.hasFullSet(player);
    }

    @Override
    public ItemStack getChestPiece(Player player) {
        return SentientHelper.getChest(player);
    }

    @Override
    public List<UpgradeInfo> getUpgrades(Player player) {
        if (!hasFullSet(player)) {
            return List.of();
        }

        List<UpgradeInfo> result = new ArrayList<>();
        List<SentientHelper.UpgradeInstance> upgrades = SentientHelper.getUpgrades(player);

        for (SentientHelper.UpgradeInstance instance : upgrades) {
            Holder<SentientUpgrade> upgradeHolder = instance.upgrade();
            int level = instance.level();

            if (upgradeHolder.isBound() && upgradeHolder.unwrapKey().isPresent()) {
                ResourceLocation id = upgradeHolder.unwrapKey().get().location();
                SentientUpgrade upgrade = upgradeHolder.value();
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

        List<SentientHelper.UpgradeInstance> upgrades = SentientHelper.getUpgrades(player);
        for (SentientHelper.UpgradeInstance instance : upgrades) {
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

        List<SentientHelper.UpgradeInstance> upgrades = SentientHelper.getUpgrades(player);
        for (SentientHelper.UpgradeInstance instance : upgrades) {
            if (instance.upgrade().unwrapKey().isPresent() &&
                instance.upgrade().unwrapKey().get().location().equals(upgradeId)) {
                SentientHelper.applyExp(player, instance.upgrade(), amount);
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

        SentientStats stats = chest.get(NVDataComponents.UPGRADES.get());
        if (stats == null) {
            return 0;
        }

        for (Object2FloatMap.Entry<Holder<SentientUpgrade>> entry : stats.upgrades().object2FloatEntrySet()) {
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
        List<SentientHelper.UpgradeInstance> upgrades = SentientHelper.getUpgrades(player);
        for (SentientHelper.UpgradeInstance instance : upgrades) {
            SentientUpgrade upgrade = instance.upgrade().value();
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

        Integer maxPoints = chest.get(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS.get());
        return maxPoints != null ? maxPoints : getMaxUpgradePoints();
    }

    @Override
    public int getAvailableUpgradePoints(Player player) {
        return getMaxUpgradePoints(player) - getUsedUpgradePoints(player);
    }
}
