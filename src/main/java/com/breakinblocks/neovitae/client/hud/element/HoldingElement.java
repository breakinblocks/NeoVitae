package com.breakinblocks.neovitae.client.hud.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.client.hud.HUDElement;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.sigil.ItemSigilHolding;

public class HoldingElement extends HUDElement {

    private static final int SLOT = 20;

    public HoldingElement() {
        super(ItemSigilHolding.INVENTORY_SIZE * SLOT + 2, SLOT + 2);
    }

    private static ItemStack findHolding(Minecraft mc) {
        if (mc.player == null) {
            return ItemStack.EMPTY;
        }
        ItemStack main = mc.player.getMainHandItem();
        if (main.getItem() == NVItems.SIGIL_HOLDING.get()) {
            return main;
        }
        ItemStack off = mc.player.getOffhandItem();
        if (off.getItem() == NVItems.SIGIL_HOLDING.get()) {
            return off;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean shouldRender(Minecraft mc) {
        return !findHolding(mc).isEmpty();
    }

    @Override
    public void draw(GuiGraphics guiGraphics, float partialTicks, int drawX, int drawY) {
        ItemStack holding = findHolding(Minecraft.getInstance());
        if (holding.isEmpty()) {
            return;
        }
        guiGraphics.fill(drawX, drawY, drawX + getWidth(), drawY + getHeight(), 0x80000000);

        NonNullList<ItemStack> inv = ItemSigilHolding.getInternalInventory(holding);
        int current = ItemSigilHolding.getCurrentItemOrdinal(holding);
        for (int i = 0; i < inv.size(); i++) {
            int x = drawX + 2 + i * SLOT;
            int y = drawY + 2;
            if (i == current) {
                guiGraphics.fill(x - 1, y - 1, x + 17, y + 17, 0x80FFFFFF);
            }
            ItemStack stack = inv.get(i);
            if (!stack.isEmpty()) {
                guiGraphics.renderItem(stack, x, y);
            }
        }
    }
}
