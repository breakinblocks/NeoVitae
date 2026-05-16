package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.menu.TabulaVitaeMenu;
import com.breakinblocks.neovitae.compat.jei.NeoVitaeJEIPlugin;
import com.breakinblocks.neovitae.compat.jei.tabulavitae.TabulaVitaeRecipeCategory;

import java.util.List;

public class TabulaVitaeScreen extends AbstractContainerScreen<TabulaVitaeMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "textures/gui/alchemytable.png");

    public TabulaVitaeScreen(TabulaVitaeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 205;
        this.titleLabelX = 38;
        this.inventoryLabelY = 111;
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
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        if (isOverProgress(mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, Component.literal("Show Recipes").withStyle(ChatFormatting.YELLOW), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);

        int progress = getCookProgressScaled(90);
        guiGraphics.blit(BACKGROUND, i + 106, j + 14 + 90 - progress, 176, 90 - progress, 18, progress);
    }

    private boolean isOverProgress(double mouseX, double mouseY) {
        int px = leftPos + 106;
        int py = topPos + 14;
        return mouseX >= px && mouseX < px + 18 && mouseY >= py && mouseY < py + 90;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isOverProgress(mouseX, mouseY)) {
            var runtime = NeoVitaeJEIPlugin.jeiRuntime;
            if (runtime != null) {
                runtime.getRecipesGui().showTypes(List.of(TabulaVitaeRecipeCategory.RECIPE_TYPE));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private int getCookProgressScaled(int scale) {
        double progress = menu.tile.getProgressForGui();
        return (int) (progress * scale);
    }
}
