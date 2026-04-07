package com.breakinblocks.neovitae.will;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

public class PlayerSpiritusHandler {

    public static NonNullList<ItemStack> getAllInventories(Player player) {
        NonNullList<ItemStack> inventory = NonNullList.create();
        inventory.addAll(player.getInventory().items);
        inventory.addAll(player.getInventory().armor);
        inventory.addAll(player.getInventory().offhand);
        return inventory;
    }

    public static double getTotalSpiritus(SpiritusType type, Player player) {
        NonNullList<ItemStack> inventory = getAllInventories(player);
        double souls = 0;

        for (ItemStack stack : inventory) {
            if (stack.isEmpty()) continue;
            if (SpiritusHelper.hasSpiritus(stack)) {
                souls += SpiritusHelper.getWill(stack, type);
            }
        }

        return souls;
    }

    public static SpiritusType getLargestSpiritusType(Player player) {
        SpiritusType type = SpiritusType.DEFAULT;
        double max = getTotalSpiritus(type, player);

        for (SpiritusType testType : SpiritusType.values()) {
            double value = getTotalSpiritus(testType, player);
            if (value > max) {
                max = value;
                type = testType;
            }
        }

        return type;
    }

    public static boolean isSpiritusFull(SpiritusType type, Player player) {
        NonNullList<ItemStack> inventory = getAllInventories(player);

        boolean hasRechargeable = false;
        for (ItemStack stack : inventory) {
            if (stack.isEmpty()) continue;
            if (SpiritusHelper.isRechargeable(stack)) {
                hasRechargeable = true;
                if (SpiritusHelper.getWill(stack, type) < SpiritusHelper.resolveMaxWill(stack, type)) {
                    return false;
                }
            }
        }

        return hasRechargeable;
    }

    public static double consumeSpiritus(SpiritusType type, Player player, double amount) {
        double consumed = 0;
        consumed += consumeFromList(type, player.getInventory().items, amount - consumed);
        consumed += consumeFromList(type, player.getInventory().offhand, amount - consumed);
        return consumed;
    }

    private static double consumeFromList(SpiritusType type, NonNullList<ItemStack> list, double amount) {
        double consumed = 0;
        for (int i = 0; i < list.size(); i++) {
            if (consumed >= amount) break;

            ItemStack stack = list.get(i);
            if (stack.isEmpty() || !SpiritusHelper.hasSpiritus(stack)) continue;

            consumed += SpiritusHelper.drainWill(stack, type, amount - consumed, true);
            if (SpiritusHelper.getWill(stack, type) <= 0 && !SpiritusHelper.isRechargeable(stack)) {
                list.set(i, ItemStack.EMPTY);
            }
        }
        return consumed;
    }

    public static ItemStack addSpiritus(Player player, ItemStack willStack) {
        if (willStack.isEmpty()) return ItemStack.EMPTY;

        NonNullList<ItemStack> inventory = getAllInventories(player);

        for (ItemStack stack : inventory) {
            if (stack.isEmpty() || !SpiritusHelper.isRechargeable(stack)) continue;

            if (stack.getItem() instanceof ISpiritusGem gem) {
                ItemStack newStack = gem.fillSpiritusGem(stack, willStack);
                if (newStack.isEmpty()) return ItemStack.EMPTY;
                willStack = newStack;
            }
        }

        return willStack;
    }

    public static double addSpiritus(SpiritusType type, Player player, double amount) {
        return addSpiritus(type, player, amount, ItemStack.EMPTY);
    }

    public static double addSpiritus(SpiritusType type, Player player, double amount, ItemStack ignored) {
        NonNullList<ItemStack> inventory = getAllInventories(player);
        double remaining = amount;

        for (ItemStack stack : inventory) {
            if (stack.isEmpty() || stack.equals(ignored)) continue;
            if (!SpiritusHelper.isRechargeable(stack)) continue;

            remaining -= SpiritusHelper.fillWill(stack, type, remaining, true);
            if (remaining <= 0) break;
        }

        return amount - remaining;
    }
}
