package com.breakinblocks.neovitae.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/**
 * Helper class for adding player inventory slots to menus.
 * Reduces code duplication across all menu classes.
 */
public final class MenuSlotHelper {

    private MenuSlotHelper() {
        // Utility class - no instantiation
    }

    /**
     * Standard X offset for player inventory slots.
     */
    public static final int PLAYER_INV_X = 8;

    /**
     * Standard slot spacing.
     */
    public static final int SLOT_SIZE = 18;

    /**
     * Adds standard player inventory (27 slots) and hotbar (9 slots) to a menu.
     * Call from within a menu class using: MenuSlotHelper.addPlayerInventory(this::addSlot, playerInventory, invY, hotbarY);
     *
     * @param slotAdder Function that adds a slot to the menu (use this::addSlot)
     * @param playerInventory The player's inventory
     * @param inventoryY Y position for the main inventory (3 rows)
     * @param hotbarY Y position for the hotbar
     */
    public static void addPlayerInventory(java.util.function.Consumer<Slot> slotAdder,
                                          Inventory playerInventory,
                                          int inventoryY, int hotbarY) {
        addPlayerInventory(slotAdder, playerInventory, PLAYER_INV_X, inventoryY, hotbarY);
    }

    /**
     * Adds standard player inventory (27 slots) and hotbar (9 slots) with custom X offset.
     * Call from within a menu class using: MenuSlotHelper.addPlayerInventory(this::addSlot, playerInventory, x, invY, hotbarY);
     *
     * @param slotAdder Function that adds a slot to the menu (use this::addSlot)
     * @param playerInventory The player's inventory
     * @param x X position for all slots
     * @param inventoryY Y position for the main inventory (3 rows)
     * @param hotbarY Y position for the hotbar
     */
    public static void addPlayerInventory(java.util.function.Consumer<Slot> slotAdder,
                                          Inventory playerInventory,
                                          int x, int inventoryY, int hotbarY) {
        // Main inventory (3 rows of 9 slots)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                slotAdder.accept(new Slot(playerInventory, col + row * 9 + 9,
                        x + col * SLOT_SIZE, inventoryY + row * SLOT_SIZE));
            }
        }

        // Hotbar (1 row of 9 slots)
        for (int col = 0; col < 9; col++) {
            slotAdder.accept(new Slot(playerInventory, col, x + col * SLOT_SIZE, hotbarY));
        }
    }

    /**
     * Calculates the hotbar Y position given standard spacing from inventory.
     * Standard gap between inventory bottom and hotbar is 4 pixels.
     *
     * @param inventoryY Y position of the main inventory
     * @return Y position for the hotbar
     */
    public static int hotbarY(int inventoryY) {
        return inventoryY + 3 * SLOT_SIZE + 4;
    }

    /**
     * Common inventory Y positions for different GUI heights.
     */
    public static final int INV_Y_166 = 84;   // Standard 166-height GUI
    public static final int INV_Y_187 = 105;  // 187-height GUI (routing nodes)
    public static final int INV_Y_205 = 123;  // 205-height GUI (alchemy table, soul forge)
    public static final int INV_Y_208 = 126;  // 208-height GUI (ARC)

    /**
     * Common hotbar Y positions for different GUI heights.
     */
    public static final int HOTBAR_Y_166 = 142;  // Standard 166-height GUI
    public static final int HOTBAR_Y_187 = 163;  // 187-height GUI (routing nodes)
    public static final int HOTBAR_Y_205 = 181;  // 205-height GUI (alchemy table, soul forge)
    public static final int HOTBAR_Y_208 = 184;  // 208-height GUI (ARC)
}
