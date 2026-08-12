package com.breakinblocks.neovitae.common.menu;

import com.breakinblocks.neovitae.common.item.ExperienceTomeItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class ExperienceTomeMenu extends AbstractContainerMenu {

    private final int tomeSlot;

    public ExperienceTomeMenu(int containerId, Inventory playerInventory, int tomeSlot) {
        super(NVMenus.EXPERIENCE_TOME.get(), containerId);
        this.tomeSlot = tomeSlot;
    }

    public ExperienceTomeMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readInt());
    }

    public int getTomeSlot() {
        return tomeSlot;
    }

    public ItemStack getTome(Player player) {
        return player.getInventory().getItem(tomeSlot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getInventory().getItem(tomeSlot).getItem() instanceof ExperienceTomeItem;
    }
}
