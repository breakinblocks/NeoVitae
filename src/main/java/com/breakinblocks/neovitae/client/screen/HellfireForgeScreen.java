package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.HellfireForgeBlockEntity;
import com.breakinblocks.neovitae.common.menu.HellfireForgeMenu;
import com.breakinblocks.neovitae.compat.jei.NeoVitaeJEIPlugin;
import com.breakinblocks.neovitae.compat.jei.forge.HellfireForgeRecipeCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HellfireForgeScreen extends AbstractContainerScreen<HellfireForgeMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "textures/gui/hellfire_forge.png");
    private static final int BLOCKED_OVERLAY = 0x55FF3030;
    private static final int BLOCKED_BORDER = 0xAAFF3030;

    public HellfireForgeScreen(HellfireForgeMenu menu, Inventory playerInventory, Component title) {
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
            List<Component> lines = new ArrayList<>();
            if (isBlockedOnSpiritus()) {
                lines.add(Component.translatable("gui.neovitae.hellfire_forge.needs_spiritus",
                        menu.tile.dataAccess.get(HellfireForgeBlockEntity.DATA_STORED_SPIRITUS),
                        menu.tile.dataAccess.get(HellfireForgeBlockEntity.DATA_REQUIRED_SPIRITUS)).withStyle(ChatFormatting.RED));
                lines.add(Component.translatable("gui.neovitae.hellfire_forge.needs_spiritus.hint").withStyle(ChatFormatting.GRAY));
            }
            lines.add(Component.translatable("gui.neovitae.show_recipes").withStyle(ChatFormatting.YELLOW));
            guiGraphics.renderTooltip(this.font, lines, Optional.empty(), mouseX, mouseY);
        }
    }

    private boolean isBlockedOnSpiritus() {
        return menu.tile.dataAccess.get(HellfireForgeBlockEntity.DATA_STATUS) == HellfireForgeBlockEntity.STATUS_NEEDS_SPIRITUS;
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
        guiGraphics.blit(BACKGROUND, i + 115, j + 14 + 90 - progress, 176, 90 - progress, 18, progress);

        if (isBlockedOnSpiritus()) {
            int x = i + 115;
            int y = j + 14;
            guiGraphics.fill(x, y, x + 18, y + 90, BLOCKED_OVERLAY);
            guiGraphics.fill(x, y, x + 18, y + 1, BLOCKED_BORDER);
            guiGraphics.fill(x, y + 89, x + 18, y + 90, BLOCKED_BORDER);
            guiGraphics.fill(x, y, x + 1, y + 90, BLOCKED_BORDER);
            guiGraphics.fill(x + 17, y, x + 18, y + 90, BLOCKED_BORDER);
        }
    }

    private boolean isOverProgress(double mouseX, double mouseY) {
        int px = leftPos + 115;
        int py = topPos + 14;
        return mouseX >= px && mouseX < px + 18 && mouseY >= py && mouseY < py + 90;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isOverProgress(mouseX, mouseY)) {
            var runtime = NeoVitaeJEIPlugin.jeiRuntime;
            if (runtime != null) {
                runtime.getRecipesGui().showTypes(List.of(HellfireForgeRecipeCategory.RECIPE_TYPE));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private int getCookProgressScaled(int scale) {
        int progress = menu.tile.dataAccess.get(HellfireForgeBlockEntity.DATA_PROGRESS);
        return progress * scale / HellfireForgeBlockEntity.MAX_PROGRESS;
    }
}
