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
import com.breakinblocks.neovitae.common.blockentity.TabulaVitaeBlockEntity;
import com.breakinblocks.neovitae.common.menu.TabulaVitaeMenu;
import com.breakinblocks.neovitae.compat.jei.NeoVitaeJEIPlugin;
import com.breakinblocks.neovitae.compat.jei.tabulavitae.TabulaVitaeRecipeCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TabulaVitaeScreen extends AbstractContainerScreen<TabulaVitaeMenu> {
    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(NeoVitae.MODID, "textures/gui/alchemytable.png");

    private SideConfigPanel sidePanel;

    public TabulaVitaeScreen(TabulaVitaeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 205);
        this.titleLabelX = 38;
        this.inventoryLabelY = 111;
    }

    @Override
    protected void init() {
        super.init();
        TabulaVitaeBlockEntity tile = menu.tile;
        sidePanel = new SideConfigPanel(tile, tile.getSideConfig(), () -> tile.activeSlot,
                TabulaVitaeScreen::slotName, TabulaVitaeBlockEntity.OUTPUT_SLOT, tile);
        sidePanel.create(this::addRenderableWidget, leftPos + 133, topPos + 50);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        sidePanel.tick();
    }

    private static Component slotName(int slot) {
        String key = switch (slot) {
            case TabulaVitaeBlockEntity.ORB_SLOT -> "gui.neovitae.tabula_vitae.slot.orb";
            case TabulaVitaeBlockEntity.OUTPUT_SLOT -> "gui.neovitae.tabula_vitae.slot.output";
            default -> "gui.neovitae.tabula_vitae.slot.input";
        };
        if (slot < TabulaVitaeBlockEntity.ORB_SLOT) {
            return Component.translatable(key, slot + 1).withStyle(ChatFormatting.YELLOW);
        }
        return Component.translatable(key).withStyle(ChatFormatting.YELLOW);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractTooltip(guiGraphics, mouseX, mouseY);
        if (isOverProgress(mouseX, mouseY)) {
            List<Component> lines = new ArrayList<>();
            TabulaVitaeBlockEntity.IdleReason reason = menu.getIdleReason();
            if (reason != TabulaVitaeBlockEntity.IdleReason.NONE) {
                lines.add(Component.translatable("gui.neovitae.tabula_vitae.idle." + reason.name().toLowerCase(Locale.ROOT))
                        .withStyle(ChatFormatting.RED));
            }
            lines.add(Component.translatable("gui.neovitae.show_recipes").withStyle(ChatFormatting.YELLOW));
            guiGraphics.setTooltipForNextFrame(this.font, lines, Optional.empty(), mouseX, mouseY);
            return;
        }
        if (isOverActiveSlot(mouseX, mouseY)) {
            int slot = menu.tile.activeSlot;
            Component line1 = Component.translatable("gui.neovitae.tabula_vitae.active_slot", slotName(slot)).withStyle(ChatFormatting.WHITE);
            Component line2 = Component.translatable("gui.neovitae.tabula_vitae.active_hint").withStyle(ChatFormatting.GRAY);
            List<Component> lines = new ArrayList<>();
            lines.add(line1);
            lines.add(line2);
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
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, i + 106, j + 14 + 90 - progress, 176f, (float)(90 - progress), 18, progress, 256, 256);

        int activeSlot = menu.tile.activeSlot;
        if (activeSlot != -1) {
            Slot slot = this.getMenu().getSlot(activeSlot);
            float highlightV = (activeSlot == TabulaVitaeBlockEntity.OUTPUT_SLOT) ? 37f : 19f;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, i + slot.x, j + slot.y, 195f, highlightV, 16, 16, 256, 256);
        }
    }

    private boolean isOverProgress(double mouseX, double mouseY) {
        int px = leftPos + 106;
        int py = topPos + 14;
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
                runtime.getRecipesGui().showTypes(List.of(TabulaVitaeRecipeCategory.RECIPE_TYPE));
                return true;
            }
        }
        return super.mouseClicked(event, dragging);
    }

    private int getCookProgressScaled(int scale) {
        double progress = menu.getProgress();
        return (int) (progress * scale);
    }
}
