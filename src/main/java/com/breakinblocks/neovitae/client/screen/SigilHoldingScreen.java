package com.breakinblocks.neovitae.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.sigil.ItemSigilHolding;
import com.breakinblocks.neovitae.common.menu.SigilHoldingMenu;
import com.breakinblocks.neovitae.common.network.NVPayloads;
import com.breakinblocks.neovitae.common.network.SigilHoldingSelectionPayload;

public class SigilHoldingScreen extends AbstractContainerScreen<SigilHoldingMenu> {

    private static final ResourceLocation BACKGROUND = NeoVitae.rl("textures/gui/sigil_holding.png");

    private static final int SLOT_START_X = 8;
    private static final int SLOT_Y = 17;
    private static final int SLOT_SPACING = 36;
    private static final int SLOT_SIZE = 16;

    public SigilHoldingScreen(SigilHoldingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 121;
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelX = 53;
        this.titleLabelY = 4;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int selectedSlot = menu.getSelectedSlot();
        int selectionX = leftPos + 4 + selectedSlot * SLOT_SPACING;
        int selectionY = topPos + 13;
        guiGraphics.blit(BACKGROUND, selectionX, selectionY, 0, 123, 24, 24);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int i = 0; i < ItemSigilHolding.INVENTORY_SIZE; i++) {
                if (isMouseOverSlot((int) mouseX, (int) mouseY, i)) {
                    selectSlot(i);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isMouseOverSlot(int mouseX, int mouseY, int slotIndex) {
        int slotX = leftPos + SLOT_START_X + slotIndex * SLOT_SPACING;
        int slotY = topPos + SLOT_Y;
        return mouseX >= slotX && mouseX < slotX + SLOT_SIZE
                && mouseY >= slotY && mouseY < slotY + SLOT_SIZE;
    }

    private void selectSlot(int slot) {
        menu.setSelectedSlot(slot);
        NVPayloads.sendToServer(new SigilHoldingSelectionPayload(slot));
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
    }
}
