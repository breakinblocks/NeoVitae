package com.breakinblocks.neovitae.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.item.ItemRitualLedger;
import com.breakinblocks.neovitae.common.world.ActiveRituals.LedgerEntry;

import java.util.ArrayList;
import java.util.List;

public class RitualLedgerMenu extends AbstractContainerMenu {

    private final InteractionHand hand;
    private final List<LedgerEntry> entries;

    public RitualLedgerMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        super(NVMenus.RITUAL_LEDGER.get(), containerId);
        this.hand = buf.readEnum(InteractionHand.class);
        this.entries = buf.readList(LedgerEntry::read);
    }

    public RitualLedgerMenu(int containerId, Inventory playerInv, InteractionHand hand, List<LedgerEntry> entries) {
        super(NVMenus.RITUAL_LEDGER.get(), containerId);
        this.hand = hand;
        this.entries = new ArrayList<>(entries);
    }

    public static void write(FriendlyByteBuf buf, InteractionHand hand, List<LedgerEntry> entries) {
        buf.writeEnum(hand);
        buf.writeCollection(entries, (b, entry) -> entry.write(b));
    }

    public List<LedgerEntry> getEntries() {
        return entries;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getItemInHand(hand).getItem() instanceof ItemRitualLedger;
    }
}
