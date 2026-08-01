package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.HellfireForgeBlockEntity;
import com.breakinblocks.neovitae.common.menu.HellfireForgeMenu;
import com.breakinblocks.neovitae.compat.jei.NeoVitaeJEIPlugin;
import com.breakinblocks.neovitae.compat.jei.forge.HellfireForgeRecipeCategory;

import java.util.List;

public class HellfireForgeScreen extends AbstractContainerScreen<HellfireForgeMenu> {
    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(NeoVitae.MODID, "textures/gui/hellfire_forge.png");

    public HellfireForgeScreen(HellfireForgeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 205);
        this.titleLabelX = 38;
        this.inventoryLabelY = 111;
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractTooltip(guiGraphics, mouseX, mouseY);
        if (isOverProgress(mouseX, mouseY)) {
            guiGraphics.setTooltipForNextFrame(this.font, Component.translatable("gui.neovitae.show_recipes").withStyle(ChatFormatting.YELLOW), mouseX, mouseY);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF404040, false);
        guiGraphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF404040, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, i, j, 0f, 0f, this.imageWidth, this.imageHeight, 256, 256);

        int progress = getCookProgressScaled(90);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, i + 115, j + 14 + 90 - progress, 176f, (float)(90 - progress), 18, progress, 256, 256);
    }

    private boolean isOverProgress(double mouseX, double mouseY) {
        int px = leftPos + 115;
        int py = topPos + 14;
        return mouseX >= px && mouseX < px + 18 && mouseY >= py && mouseY < py + 90;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
        if (event.button() == 0 && isOverProgress(event.x(), event.y())) {
            var runtime = NeoVitaeJEIPlugin.jeiRuntime;
            if (runtime != null) {
                runtime.getRecipesGui().showTypes(List.of(HellfireForgeRecipeCategory.RECIPE_TYPE));
                return true;
            }
        }
        return super.mouseClicked(event, dragging);
    }

    private int getCookProgressScaled(int scale) {
        int progress = menu.tile.dataAccess.get(0);
        return progress * scale / HellfireForgeBlockEntity.MAX_PROGRESS;
    }
}
