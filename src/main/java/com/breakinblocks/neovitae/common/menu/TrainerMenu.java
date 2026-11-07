package com.breakinblocks.neovitae.common.menu;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.ArrayList;
import java.util.List;

public class TrainerMenu extends AbstractContainerMenu {

    private final Player player;

    public TrainerMenu(int containerId, Inventory playerInv, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInv);
    }

    public TrainerMenu(int containerId, Inventory playerInv) {
        super(NVMenus.TRAINER.get(), containerId);
        this.player = playerInv.player;
    }

    public static List<Holder<SentientUpgrade>> trainable(RegistryAccess access) {
        List<Holder<SentientUpgrade>> list = new ArrayList<>();
        access.registryOrThrow(NVRegistries.Keys.SENTIENT_UPGRADES).getTag(NVTags.Sentient.TRAINABLE)
                .ifPresent(set -> set.forEach(list::add));
        return list;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return !SentientHelper.isNeverValid(SentientHelper.getChest(player));
    }

    public Player getPlayer() {
        return player;
    }
}
