package com.breakinblocks.neovitae.will;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;

/**
 * Helper methods for handling demon will for a player.
 * This refers to the Soul System, meaning Monster Souls and Soul Gems.
 */
public class PlayerDemonWillHandler {

    /**
     * Gets all items from all player inventories (main inventory + hands).
     *
     * @param player - The player to get items from
     * @return - NonNullList of all items
     */
    public static NonNullList<ItemStack> getAllInventories(Player player) {
        NonNullList<ItemStack> inventory = NonNullList.create();

        // Add main inventory
        inventory.addAll(player.getInventory().items);

        // Add armor
        inventory.addAll(player.getInventory().armor);

        // Add offhand
        inventory.addAll(player.getInventory().offhand);

        return inventory;
    }

    /**
     * Gets the total amount of Will a player contains in their inventory.
     *
     * @param type   - The type of Will to check for
     * @param player - The player to check the will of
     * @return - The amount of will the player contains
     */
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

    /**
     * Gets the will type with the largest amount in the player's inventory.
     *
     * @param player - The player to check
     * @return - The will type with the most stored
     */
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

    /**
     * Checks if the player's Tartaric gems are completely full.
     *
     * @param type   - The type of Will to check for
     * @param player - The player to check the Will of
     * @return - True if all Will containers are full, false if not.
     */
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

    /**
     * Consumes Will from the inventory of a given player.
     *
     * @param type   - The type of Will to consume
     * @param player - The player to consume the will of
     * @param amount - The amount of will to consume
     * @return - The amount of will consumed.
     */
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

        // Also check offhand
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

    /**
     * Adds an IDemonWill contained in an ItemStack to one of the Soul Gems in the
     * player's inventory.
     *
     * @param player    - The player to add will to
     * @param willStack - ItemStack that contains an IDemonWill to be added
     * @return - The modified willStack (empty if fully absorbed)
     */
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

    /**
     * Adds will of a specific type to Soul Gems in the player's inventory.
     *
     * @param type   - The type of Will to add
     * @param player - The player to add will to
     * @param amount - The amount of will to add
     * @return - The amount of will added
     */
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

    /**
     * Adds will of a specific type to Soul Gems in the player's inventory,
     * ignoring a specific stack.
     *
     * @param type    - The type of Will to add
     * @param player  - The player to add will to
     * @param amount  - The amount of will to add
     * @param ignored - A stack to ignore
     * @return - The amount of will added
     */
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
