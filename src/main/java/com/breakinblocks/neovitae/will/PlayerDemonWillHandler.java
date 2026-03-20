package com.breakinblocks.neovitae.will;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;

public class PlayerDemonWillHandler {

    public static NonNullList<ItemStack> getAllInventories(Player player) {
        NonNullList<ItemStack> inventory = NonNullList.create();
        inventory.addAll(player.getInventory().items);
        inventory.addAll(player.getInventory().armor);
        inventory.addAll(player.getInventory().offhand);

        return inventory;
    }

    public static double getTotalDemonWill(EnumWillType type, Player player) {
        NonNullList<ItemStack> inventory = getAllInventories(player);
        double souls = 0;

        for (ItemStack stack : inventory) {
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof IDemonWill will && will.getType(stack) == type) {
                souls += will.getWill(type, stack);
            } else if (stack.getItem() instanceof IDemonWillGem gem) {
                souls += gem.getWill(type, stack);
            }
        }

        return souls;
    }

    public static EnumWillType getLargestWillType(Player player) {
        EnumWillType type = EnumWillType.DEFAULT;
        double max = getTotalDemonWill(type, player);

        for (EnumWillType testType : EnumWillType.values()) {
            double value = getTotalDemonWill(testType, player);
            if (value > max) {
                max = value;
                type = testType;
            }
        }

        return type;
    }

    public static boolean isDemonWillFull(EnumWillType type, Player player) {
        NonNullList<ItemStack> inventory = getAllInventories(player);

        boolean hasGem = false;
        for (ItemStack stack : inventory) {
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof IDemonWillGem gem) {
                hasGem = true;
                if (gem.getWill(type, stack) < gem.getMaxWill(type, stack)) {
                    return false;
                }
            }
        }

        return hasGem;
    }

    public static double consumeDemonWill(EnumWillType type, Player player, double amount) {
        double consumed = 0;
        NonNullList<ItemStack> inventory = player.getInventory().items;

        for (int i = 0; i < inventory.size(); i++) {
            if (consumed >= amount) {
                return consumed;
            }

            ItemStack stack = inventory.get(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof IDemonWill will && will.getType(stack) == type) {
                consumed += will.drainWill(type, stack, amount - consumed);
                if (will.getWill(type, stack) <= 0) {
                    inventory.set(i, ItemStack.EMPTY);
                }
            } else if (stack.getItem() instanceof IDemonWillGem gem) {
                consumed += gem.drainWill(type, stack, amount - consumed, true);
            }
        }

        for (int i = 0; i < player.getInventory().offhand.size(); i++) {
            if (consumed >= amount) {
                return consumed;
            }

            ItemStack stack = player.getInventory().offhand.get(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof IDemonWill will && will.getType(stack) == type) {
                consumed += will.drainWill(type, stack, amount - consumed);
                if (will.getWill(type, stack) <= 0) {
                    player.getInventory().offhand.set(i, ItemStack.EMPTY);
                }
            } else if (stack.getItem() instanceof IDemonWillGem gem) {
                consumed += gem.drainWill(type, stack, amount - consumed, true);
            }
        }

        return consumed;
    }

    public static ItemStack addDemonWill(Player player, ItemStack willStack) {
        if (willStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        NonNullList<ItemStack> inventory = getAllInventories(player);

        for (ItemStack stack : inventory) {
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof IDemonWillGem gem) {
                ItemStack newStack = gem.fillDemonWillGem(stack, willStack);
                if (newStack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                willStack = newStack;
            }
        }

        return willStack;
    }

    public static double addDemonWill(EnumWillType type, Player player, double amount) {
        NonNullList<ItemStack> inventory = getAllInventories(player);
        double remaining = amount;

        for (ItemStack stack : inventory) {
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof IDemonWillGem gem) {
                remaining -= gem.fillWill(type, stack, remaining, true);
                if (remaining <= 0) {
                    break;
                }
            }
        }

        return amount - remaining;
    }

    public static double addDemonWill(EnumWillType type, Player player, double amount, ItemStack ignored) {
        NonNullList<ItemStack> inventory = getAllInventories(player);
        double remaining = amount;

        for (ItemStack stack : inventory) {
            if (stack.isEmpty() || stack.equals(ignored)) continue;

            if (stack.getItem() instanceof IDemonWillGem gem) {
                remaining -= gem.fillWill(type, stack, remaining, true);
                if (remaining <= 0) {
                    break;
                }
            }
        }

        return amount - remaining;
    }
}
