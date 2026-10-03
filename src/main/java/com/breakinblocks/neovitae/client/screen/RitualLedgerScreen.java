package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import com.breakinblocks.neovitae.common.menu.RitualLedgerMenu;
import com.breakinblocks.neovitae.common.world.ActiveRituals.LedgerEntry;
import com.breakinblocks.neovitae.common.world.ActiveRituals.Status;

import java.util.ArrayList;
import java.util.List;

public class RitualLedgerScreen extends AbstractContainerScreen<RitualLedgerMenu> {

    private static final int PANEL_WIDTH = 240;
    private static final int TITLE_HEIGHT = 22;
    private static final int ROW_HEIGHT = 18;
    private static final int VISIBLE_ROWS = 9;
    private static final int LIST_X = 6;
    private static final int LIST_PAD_BOTTOM = 8;

    private int scrollOffset = 0;

    public RitualLedgerScreen(RitualLedgerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, PANEL_WIDTH, TITLE_HEIGHT + VISIBLE_ROWS * ROW_HEIGHT + LIST_PAD_BOTTOM);
    }

    private List<LedgerEntry> entries() {
        return menu.getEntries();
    }

    private int maxScroll() {
        return Math.max(0, entries().size() - VISIBLE_ROWS);
    }

    private void clampScroll() {
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll()));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xCC1A0A0A);
        g.fill(leftPos + 1, topPos + 1, leftPos + imageWidth - 1, topPos + imageHeight - 1, 0xCC2A1520);
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + 1, 0xFF6B1A1A);
        g.fill(leftPos, topPos + imageHeight - 1, leftPos + imageWidth, topPos + imageHeight, 0xFF6B1A1A);
        g.fill(leftPos, topPos, leftPos + 1, topPos + imageHeight, 0xFF6B1A1A);
        g.fill(leftPos + imageWidth - 1, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF6B1A1A);

        int listTop = topPos + TITLE_HEIGHT;
        if (entries().isEmpty()) {
            g.centeredText(font, Component.translatable("chat.neovitae.ritual_ledger.none"),
                    leftPos + imageWidth / 2, listTop + ROW_HEIGHT, 0xFFCFCFCF);
            return;
        }

        int right = leftPos + imageWidth - LIST_X;
        for (int i = 0; i < VISIBLE_ROWS; i++) {
            int idx = scrollOffset + i;
            if (idx < 0 || idx >= entries().size()) continue;
            LedgerEntry e = entries().get(idx);
            int rowY = listTop + i * ROW_HEIGHT;
            boolean hovered = mouseX >= leftPos + LIST_X && mouseX <= right
                    && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;
            if (hovered) {
                g.fill(leftPos + LIST_X, rowY, right, rowY + ROW_HEIGHT - 1, 0x44FFFFFF);
            }
            int textY = rowY + (ROW_HEIGHT - 8) / 2;
            String coords = e.coordinates();
            int coordsWidth = font.width(coords);
            g.text(font, coords, right - 6 - coordsWidth, textY, statusColor(e.status(), 0xFF9FC79F), false);

            int nameWidth = right - 6 - coordsWidth - (leftPos + LIST_X + 5) - 6;
            String name = font.plainSubstrByWidth(e.name().getString(), nameWidth);
            int nameColor = hovered ? 0xFFFFFFFF : statusColor(e.status(), 0xFFCFCFCF);
            g.text(font, name, leftPos + LIST_X + 5, textY, nameColor, false);
        }

        if (maxScroll() > 0) {
            int trackX = leftPos + imageWidth - 4;
            int trackHeight = VISIBLE_ROWS * ROW_HEIGHT;
            int thumbHeight = Math.max(10, trackHeight * VISIBLE_ROWS / entries().size());
            int thumbY = listTop + (trackHeight - thumbHeight) * scrollOffset / maxScroll();
            g.fill(trackX, listTop, trackX + 2, listTop + trackHeight, 0x40FFFFFF);
            g.fill(trackX, thumbY, trackX + 2, thumbY + thumbHeight, 0xFF8B3A3A);
        }
    }

    private static int statusColor(Status status, int activeColor) {
        return switch (status) {
            case ACTIVE -> activeColor;
            case PAUSED -> 0xFFE0C060;
            case UNLOADED -> 0xFF808080;
        };
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        Component heading = entries().isEmpty() ? title
                : Component.translatable("chat.neovitae.ritual_ledger.header", entries().size());
        g.centeredText(font, heading, imageWidth / 2, 7, 0xCCA05050);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY != 0 && maxScroll() > 0) {
            scrollOffset -= (int) Math.signum(scrollY);
            clampScroll();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
        if (event.button() == 0) {
            int idx = hoveredIndex(event.x(), event.y());
            if (idx >= 0 && minecraft != null && minecraft.player != null) {
                LedgerEntry e = entries().get(idx);
                minecraft.keyboardHandler.setClipboard(e.coordinates());
                minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.4f, 1.0f);
                minecraft.player.sendOverlayMessage(Component.translatable("gui.neovitae.ritual_ledger.copied", e.coordinates()));
                return true;
            }
        }
        return super.mouseClicked(event, dragging);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        super.extractTooltip(g, mouseX, mouseY);
        int idx = hoveredIndex(mouseX, mouseY);
        if (idx >= 0) {
            g.setComponentTooltipForNextFrame(font, buildTooltip(entries().get(idx)), mouseX, mouseY);
        }
    }

    private int hoveredIndex(double mouseX, double mouseY) {
        int listTop = topPos + TITLE_HEIGHT;
        if (mouseX < leftPos + LIST_X || mouseX > leftPos + imageWidth - LIST_X) return -1;
        if (mouseY < listTop || mouseY >= listTop + VISIBLE_ROWS * ROW_HEIGHT) return -1;
        int idx = scrollOffset + (int) ((mouseY - listTop) / ROW_HEIGHT);
        return (idx >= 0 && idx < entries().size()) ? idx : -1;
    }

    private List<Component> buildTooltip(LedgerEntry e) {
        List<Component> lines = new ArrayList<>();
        lines.add(e.name().copy().withStyle(ChatFormatting.GOLD));
        lines.add(Component.literal(e.coordinates() + "  " + e.dimension()).withStyle(ChatFormatting.GRAY));
        lines.add(e.costLine().copy().withStyle(ChatFormatting.DARK_RED));
        if (e.status() == Status.PAUSED) {
            lines.add(Component.translatable("chat.neovitae.ritual_ledger.paused").withStyle(ChatFormatting.YELLOW));
        } else if (e.status() == Status.UNLOADED) {
            lines.add(Component.translatable("chat.neovitae.ritual_ledger.unloaded").withStyle(ChatFormatting.DARK_GRAY));
        }
        lines.add(Component.translatable("chat.neovitae.ritual_ledger.copy").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        return lines;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
