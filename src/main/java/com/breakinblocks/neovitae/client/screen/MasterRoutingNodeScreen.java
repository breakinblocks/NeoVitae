package com.breakinblocks.neovitae.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.menu.MasterRoutingNodeMenu;

/**
 * Screen for the Master Routing Node.
 * Displays network status and upgrade slots.
 */
public class MasterRoutingNodeScreen extends AbstractContainerScreen<MasterRoutingNodeMenu> {
    // Use 1.20.1 texture filename (no underscores)
    private static final ResourceLocation BACKGROUND = NeoVitae.rl("textures/gui/masterroutingnode.png");

    public MasterRoutingNodeScreen(MasterRoutingNodeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 121;  // Match 1.20.1 (was 166)
        this.inventoryLabelY = 27;  // Match 1.20.1 position
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        // Tooltip for stack upgrade slot (slot index 0) - position matches 1.20.1
        if (isHovering(62, 15, 16, 16, mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, Component.literal("Stack Upgrade Slot"), mouseX, mouseY);
        }

        // Tooltip for speed upgrade slot (slot index 1) - position matches 1.20.1
        if (isHovering(98, 15, 16, 16, mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, Component.literal("Speed Upgrade Slot"), mouseX, mouseY);
        }
    }
}
