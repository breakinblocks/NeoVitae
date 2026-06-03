package com.breakinblocks.neovitae.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.item.ItemRitualDiviner;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualRegistry;

import java.util.ArrayList;
import java.util.List;

public class RitualDivinerMenu extends AbstractContainerMenu {

    private final InteractionHand hand;
    private final List<Identifier> ritualIds;

    public RitualDivinerMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        super(NVMenus.RITUAL_DIVINER.get(), containerId);
        this.hand = buf.readEnum(InteractionHand.class);
        this.ritualIds = buf.readList(b -> Identifier.parse(b.readUtf()));
    }

    public RitualDivinerMenu(int containerId, Inventory playerInv, InteractionHand hand, List<Identifier> ritualIds) {
        super(NVMenus.RITUAL_DIVINER.get(), containerId);
        this.hand = hand;
        this.ritualIds = new ArrayList<>(ritualIds);
    }

    public InteractionHand getHand() {
        return hand;
    }

    public List<Identifier> getRitualIds() {
        return ritualIds;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id < 0 || id >= ritualIds.size()) return false;

        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof ItemRitualDiviner diviner)) return false;

        Identifier ritualId = ritualIds.get(id);
        Ritual ritual = RitualRegistry.getRitual(ritualId);
        if (ritual == null) return false;

        diviner.setCurrentRitual(stack, ritualId.toString());
        player.sendOverlayMessage(Component.translatable(ritual.getTranslationKey()));

        if (player instanceof ServerPlayer sp) {
            sp.inventoryMenu.broadcastChanges();
            sp.closeContainer();
        }
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getItemInHand(hand).getItem() instanceof ItemRitualDiviner;
    }
}
