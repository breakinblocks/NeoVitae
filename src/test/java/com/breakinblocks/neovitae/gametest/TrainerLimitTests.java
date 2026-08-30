package com.breakinblocks.neovitae.gametest;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeLimits;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.TrainerItem;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeTome;
import com.breakinblocks.neovitae.common.menu.GhostItemHandler;
import com.breakinblocks.neovitae.common.menu.TrainerMenu;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.datacomponent.SentientStats;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;


@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class TrainerLimitTests {

    private static Holder<SentientUpgrade> anyUpgrade(GameTestHelper helper) {
        return helper.getLevel().registryAccess()
                .lookupOrThrow(NVRegistries.Keys.SENTIENT_UPGRADES)
                .listElements()
                .filter(h -> !h.value().levels().expToLevel().isEmpty())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No sentient upgrades registered"));
    }

    private static Player equippedPlayer(GameTestHelper helper, ItemStack chest) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemSlot(EquipmentSlot.CHEST, chest);
        return player;
    }

    private static float storedExp(Player player, Holder<SentientUpgrade> upgrade) {
        return SentientHelper.getChest(player)
                .getOrDefault(NVDataComponents.UPGRADES, SentientStats.EMPTY)
                .upgrades().getOrDefault(upgrade, 0.0f);
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void levelZeroCostsZeroExp(GameTestHelper helper) {
        Holder<SentientUpgrade> upgrade = anyUpgrade(helper);
        int exp = SentientHelper.getExpForLevel(upgrade, 0);
        if (exp != 0) {
            helper.fail("Exp for level 0 should be 0, got " + exp);
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void zeroLimitBlocksPassiveTraining(GameTestHelper helper) {
        Holder<SentientUpgrade> upgrade = anyUpgrade(helper);

        Player control = equippedPlayer(helper, new ItemStack(NVItems.SENTIENT_PLATE.get()));
        SentientHelper.applyExp(control, upgrade, 50.0f, false);
        if (storedExp(control, upgrade) <= 0) {
            helper.fail("Control setup broken: uncapped passive training gained no exp");
        }

        ItemStack cappedChest = new ItemStack(NVItems.SENTIENT_PLATE.get());
        Object2FloatOpenHashMap<Holder<SentientUpgrade>> limits = new Object2FloatOpenHashMap<>();
        limits.put(upgrade, SentientHelper.getExpForLevel(upgrade, 0));
        cappedChest.set(NVDataComponents.LIMITS, new UpgradeLimits(true, limits));
        Player capped = equippedPlayer(helper, cappedChest);

        SentientHelper.applyExp(capped, upgrade, 50.0f, false);
        float gained = storedExp(capped, upgrade);
        if (gained > 0) {
            helper.fail("Upgrade capped at level 0 gained " + gained + " exp from passive training");
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void denyBlocksUnlistedUpgrades(GameTestHelper helper) {
        Holder<SentientUpgrade> upgrade = anyUpgrade(helper);

        ItemStack denyChest = new ItemStack(NVItems.SENTIENT_PLATE.get());
        denyChest.set(NVDataComponents.LIMITS, new UpgradeLimits(false, new Object2FloatOpenHashMap<>()));
        Player denied = equippedPlayer(helper, denyChest);

        SentientHelper.applyExp(denied, upgrade, 50.0f, false);
        float gained = storedExp(denied, upgrade);
        if (gained > 0) {
            helper.fail("Deny-others mode let an unlisted upgrade gain " + gained + " exp");
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void trainerGuiDenyToggleSaves(GameTestHelper helper) {
        Holder<SentientUpgrade> upgrade = anyUpgrade(helper);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack chest = new ItemStack(NVItems.SENTIENT_PLATE.get());
        player.setItemSlot(EquipmentSlot.CHEST, chest);

        GhostItemHandler handler = new GhostItemHandler(16);
        ItemStack tomeStack = new ItemStack(NVItems.UPGRADE_TOME);
        tomeStack.set(NVDataComponents.UPGRADE_TOME_DATA, new UpgradeTome(upgrade, 0.0f));
        handler.setStackInSlot(0, tomeStack);

        TrainerMenu menu = new TrainerMenu(1, player.getInventory(),
                handler, new TrainerItem.TrainerData(handler, chest), 0);
        menu.setData(3, 0);
        menu.clickMenuButton(player, 3);
        menu.clickMenuButton(player, 4);

        UpgradeLimits saved = chest.getOrDefault(NVDataComponents.LIMITS, UpgradeLimits.EMPTY);
        if (saved.allowOthers()) {
            helper.fail("Deny toggle plus save left allowOthers=true on the chestplate");
        }
        float limit = saved.getLimit(upgrade);
        if (limit != 0) {
            helper.fail("Tome capped at level 0 saved a limit of " + limit + " instead of 0");
        }
        helper.succeed();
    }
}
