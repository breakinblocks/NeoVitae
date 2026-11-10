package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.widgets.SideConfigPanel;
import com.breakinblocks.neovitae.common.blockentity.HellfireForgeBlockEntity;
import com.breakinblocks.neovitae.common.menu.HellfireForgeMenu;
import com.breakinblocks.neovitae.compat.jei.NeoVitaeJEIPlugin;
import com.breakinblocks.neovitae.compat.jei.forge.HellfireForgeRecipeCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HellfireForgeScreen extends AbstractContainerScreen<HellfireForgeMenu> {
    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(NeoVitae.MODID, "textures/gui/hellfire_forge.png");
    private static final int BLOCKED_OVERLAY = 0x55FF3030;
    private static final int BLOCKED_BORDER = 0xAAFF3030;
    private static final int PROGRESS_X = 106;
    private static final int PROGRESS_Y = 14;

    private SideConfigPanel sidePanel;

    public HellfireForgeScreen(HellfireForgeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 205);
        this.titleLabelX = 38;
        this.inventoryLabelY = 111;
    }

    @Override
    protected void init() {
        super.init();
        HellfireForgeBlockEntity tile = menu.tile;
        sidePanel = new SideConfigPanel(tile, tile.getSideConfig(), () -> tile.activeSlot,
                HellfireForgeScreen::slotName, HellfireForgeBlockEntity.OUTPUT_SLOT, tile);
        sidePanel.create(this::addRenderableWidget, leftPos + 133, topPos + 50);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        sidePanel.tick();
    }

    private static Component slotName(int slot) {
        return switch (slot) {
            case HellfireForgeBlockEntity.GEM_SLOT ->
                    Component.translatable("gui.neovitae.hellfire_forge.slot.gem").withStyle(ChatFormatting.YELLOW);
            case HellfireForgeBlockEntity.OUTPUT_SLOT ->
                    Component.translatable("gui.neovitae.tabula_vitae.slot.output").withStyle(ChatFormatting.YELLOW);
            default -> Component.translatable("gui.neovitae.tabula_vitae.slot.input", slot + 1).withStyle(ChatFormatting.YELLOW);
        };
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractTooltip(guiGraphics, mouseX, mouseY);
        if (isOverProgress(mouseX, mouseY)) {
            List<Component> lines = new ArrayList<>();
            if (isBlockedOnSpiritus()) {
                lines.add(Component.translatable("gui.neovitae.hellfire_forge.needs_spiritus",
                        menu.tile.dataAccess.get(HellfireForgeBlockEntity.DATA_STORED_SPIRITUS),
                        menu.tile.dataAccess.get(HellfireForgeBlockEntity.DATA_REQUIRED_SPIRITUS)).withStyle(ChatFormatting.RED));
                lines.add(Component.translatable("gui.neovitae.hellfire_forge.needs_spiritus.hint").withStyle(ChatFormatting.GRAY));
            }
            lines.add(Component.translatable("gui.neovitae.show_recipes").withStyle(ChatFormatting.YELLOW));
            guiGraphics.setTooltipForNextFrame(this.font, lines, Optional.empty(), mouseX, mouseY);
            return;
        }
        if (isOverActiveSlot(mouseX, mouseY)) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.translatable("gui.neovitae.tabula_vitae.active_slot", slotName(menu.tile.activeSlot)).withStyle(ChatFormatting.WHITE));
            lines.add(Component.translatable("gui.neovitae.tabula_vitae.active_hint").withStyle(ChatFormatting.GRAY));
            guiGraphics.setTooltipForNextFrame(this.font, lines, Optional.empty(), mouseX, mouseY);
        }
    }

    private boolean isOverActiveSlot(double mouseX, double mouseY) {
        int slot = menu.tile.activeSlot;
        if (slot < 0) return false;
        Slot s = this.getMenu().getSlot(slot);
        int x = leftPos + s.x;
        int y = topPos + s.y;
        return mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16;
    }

    private boolean isBlockedOnSpiritus() {
        return menu.tile.dataAccess.get(HellfireForgeBlockEntity.DATA_STATUS) == HellfireForgeBlockEntity.STATUS_NEEDS_SPIRITUS;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF404040, false);
        guiGraphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF404040, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, i, j, 0f, 0f, this.imageWidth, this.imageHeight, 256, 256);

        int progress = getCookProgressScaled(90);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, i + PROGRESS_X, j + PROGRESS_Y + 90 - progress, 176f, (float)(90 - progress), 18, progress, 256, 256);

        if (isBlockedOnSpiritus()) {
            int x = i + PROGRESS_X;
            int y = j + PROGRESS_Y;
            guiGraphics.fill(x, y, x + 18, y + 90, BLOCKED_OVERLAY);
            guiGraphics.fill(x, y, x + 18, y + 1, BLOCKED_BORDER);
            guiGraphics.fill(x, y + 89, x + 18, y + 90, BLOCKED_BORDER);
            guiGraphics.fill(x, y, x + 1, y + 90, BLOCKED_BORDER);
            guiGraphics.fill(x + 17, y, x + 18, y + 90, BLOCKED_BORDER);
        }

        int activeSlot = menu.tile.activeSlot;
        if (activeSlot != -1) {
            Slot slot = this.getMenu().getSlot(activeSlot);
            float highlightV = (activeSlot == HellfireForgeBlockEntity.OUTPUT_SLOT) ? 37f : 19f;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, i + slot.x, j + slot.y, 195f, highlightV, 16, 16, 256, 256);
        }
    }

    private boolean isOverProgress(double mouseX, double mouseY) {
        int px = leftPos + PROGRESS_X;
        int py = topPos + PROGRESS_Y;
        return mouseX >= px && mouseX < px + 18 && mouseY >= py && mouseY < py + 90;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (sidePanel.keyPressed(event, getFocused())) {
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
        if (sidePanel.mouseClicked(event)) {
            return true;
        }
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
        int progress = menu.tile.dataAccess.get(HellfireForgeBlockEntity.DATA_PROGRESS);
        return progress * scale / HellfireForgeBlockEntity.MAX_PROGRESS;
    }
}
