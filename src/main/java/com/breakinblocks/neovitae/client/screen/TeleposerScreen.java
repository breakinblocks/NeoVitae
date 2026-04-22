package com.breakinblocks.neovitae.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.menu.TeleposerMenu;

public class TeleposerScreen extends AbstractContainerScreen<TeleposerMenu> {
    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(NeoVitae.MODID, "textures/gui/teleposer.png");

    public TeleposerScreen(TeleposerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 121);
        this.titleLabelX = 38;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }
}
