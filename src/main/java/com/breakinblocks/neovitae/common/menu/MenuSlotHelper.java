package com.breakinblocks.neovitae.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public final class MenuSlotHelper {

    private MenuSlotHelper() {}

    public static final int PLAYER_INV_X = 8;
    public static final int SLOT_SIZE = 18;

    public static void addPlayerInventory(java.util.function.Consumer<Slot> slotAdder,
                                          Inventory playerInventory,
                                          int inventoryY, int hotbarY) {
        addPlayerInventory(slotAdder, playerInventory, PLAYER_INV_X, inventoryY, hotbarY);
    }

    public static void addPlayerInventory(java.util.function.Consumer<Slot> slotAdder,
                                          Inventory playerInventory,
                                          int x, int inventoryY, int hotbarY) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                slotAdder.accept(new Slot(playerInventory, col + row * 9 + 9,
                        x + col * SLOT_SIZE, inventoryY + row * SLOT_SIZE));
            }
        }

        for (int col = 0; col < 9; col++) {
            slotAdder.accept(new Slot(playerInventory, col, x + col * SLOT_SIZE, hotbarY));
        }
    }

    public static int hotbarY(int inventoryY) {
        return inventoryY + 3 * SLOT_SIZE + 4;
    }

    public static final int INV_Y_166 = 84;   // Standard 166-height GUI
    public static final int INV_Y_187 = 105;  // 187-height GUI (routing nodes)
    public static final int INV_Y_205 = 123;  // 205-height GUI (alchemy table, soul forge)
    public static final int INV_Y_208 = 126;  // 208-height GUI (ARC)

    public static final int HOTBAR_Y_166 = 142;  // Standard 166-height GUI
    public static final int HOTBAR_Y_187 = 163;  // 187-height GUI (routing nodes)
    public static final int HOTBAR_Y_205 = 181;  // 205-height GUI (alchemy table, soul forge)
    public static final int HOTBAR_Y_208 = 184;  // 208-height GUI (ARC)
}
