package com.breakinblocks.neovitae.client.screen;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.sigil.ItemSigilHolding;
import com.breakinblocks.neovitae.common.menu.SigilHoldingMenu;
import com.breakinblocks.neovitae.common.network.SigilHoldingSelectionPayload;

public class SigilHoldingScreen extends AbstractContainerScreen<SigilHoldingMenu> {

    private static final Identifier BACKGROUND = NeoVitae.rl("textures/gui/sigil_holding.png");

    private static final int SLOT_START_X = 8;
    private static final int SLOT_Y = 17;
    private static final int SLOT_SPACING = 36;
    private static final int SLOT_SIZE = 16;

    public SigilHoldingScreen(SigilHoldingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 121);
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelX = 53;
        this.titleLabelY = 4;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256);

        int selectedSlot = menu.getSelectedSlot();
        int selectionX = leftPos + 4 + selectedSlot * SLOT_SPACING;
        int selectionY = topPos + 13;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, selectionX, selectionY, 0f, 123f, 24, 24, 256, 256);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
        if (event.button() == 0) {
            for (int i = 0; i < ItemSigilHolding.INVENTORY_SIZE; i++) {
                if (isMouseOverSlot((int) event.x(), (int) event.y(), i)) {
                    selectSlot(i);
                    return true;
                }
            }
        }
        return super.mouseClicked(event, dragging);
    }

    private boolean isMouseOverSlot(int mouseX, int mouseY, int slotIndex) {
        int slotX = leftPos + SLOT_START_X + slotIndex * SLOT_SPACING;
        int slotY = topPos + SLOT_Y;
        return mouseX >= slotX && mouseX < slotX + SLOT_SIZE
                && mouseY >= slotY && mouseY < slotY + SLOT_SIZE;
    }

    private void selectSlot(int slot) {
        menu.setSelectedSlot(slot);
        ClientPacketDistributor.sendToServer(new SigilHoldingSelectionPayload(slot));
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF404040, false);
    }
}
