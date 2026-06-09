package com.breakinblocks.neovitae.client.hud.element;

import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.sigil.ItemSigilHolding;

import java.util.ArrayList;
import java.util.List;

public abstract class ElementDivinedInformation<T extends BlockEntity> extends ElementTileInformation<T> {

    public enum SigilGate {
        SIMPLE,
        ADVANCED,
        EITHER
    }

    private final SigilGate gate;

    public ElementDivinedInformation(int lines, SigilGate gate, Class<T> tileClass) {
        super(110, lines, tileClass);
        this.gate = gate;
    }

    @Override
    public boolean shouldRender(Minecraft minecraft) {
        if (targetTile(minecraft) == null) {
            return false;
        }
        Player player = minecraft.player;
        if (player == null) {
            return false;
        }

        boolean hasDivination = false;
        boolean hasSeer = false;
        for (ItemStack stack : activeInventory(player)) {
            ItemStack effective = stack;
            if (stack.getItem() instanceof ItemSigilHolding) {
                NonNullList<ItemStack> inv = ItemSigilHolding.getInternalInventory(stack);
                int slot = ItemSigilHolding.getCurrentItemOrdinal(stack);
                if (inv != null && slot >= 0 && slot < inv.size() && !inv.get(slot).isEmpty()) {
                    effective = inv.get(slot);
                }
            }
            if (effective.getItem() == NVItems.SIGIL_SEER.get()) {
                hasSeer = true;
            } else if (effective.getItem() == NVItems.SIGIL_DIVINATION.get()) {
                hasDivination = true;
            }
        }

        return switch (gate) {
            case SIMPLE -> hasDivination && !hasSeer;
            case ADVANCED -> hasSeer;
            case EITHER -> hasDivination || hasSeer;
        };
    }

    private static List<ItemStack> activeInventory(Player player) {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            list.add(player.getInventory().getItem(i));
        }
        list.add(player.getOffhandItem());
        return list;
    }
}
