package com.breakinblocks.neovitae.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.item.ItemRitualDiviner;
import com.breakinblocks.neovitae.common.menu.RitualDivinerMenu;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualRegistry;

import java.util.ArrayList;
import java.util.List;

public class RitualDivinerScreen extends AbstractContainerScreen<RitualDivinerMenu> {

    private static final int PANEL_WIDTH = 200;
    private static final int TITLE_HEIGHT = 22;
    private static final int ROW_HEIGHT = 18;
    private static final int VISIBLE_ROWS = 9;
    private static final int LIST_X = 6;
    private static final int LIST_PAD_BOTTOM = 8;

    private final List<Entry> entries = new ArrayList<>();
    private int scrollOffset = 0;

    public RitualDivinerScreen(RitualDivinerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = PANEL_WIDTH;
        this.imageHeight = TITLE_HEIGHT + VISIBLE_ROWS * ROW_HEIGHT + LIST_PAD_BOTTOM;
    }

    @Override
    protected void init() {
        super.init();
        entries.clear();
        for (ResourceLocation id : menu.getRitualIds()) {
            Ritual ritual = RitualRegistry.getRitual(id);
            Component name = ritual != null
                    ? Component.translatable(ritual.getTranslationKey())
                    : Component.literal(id.toString());
            entries.add(new Entry(id, name));
        }
        scrollToCurrent();
        clampScroll();
    }

    private void scrollToCurrent() {
        String current = currentRitualId();
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).id.toString().equals(current)) {
                scrollOffset = i - VISIBLE_ROWS / 2;
                return;
            }
        }
    }

    private int maxScroll() {
        return Math.max(0, entries.size() - VISIBLE_ROWS);
    }

    private void clampScroll() {
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll()));
    }

    private String currentRitualId() {
        if (minecraft == null || minecraft.player == null) return "";
        ItemStack held = minecraft.player.getItemInHand(menu.getHand());
        if (held.getItem() instanceof ItemRitualDiviner diviner) {
            return diviner.getCurrentRitualId(held);
        }
        return "";
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xCC1A0A0A);
        g.fill(leftPos + 1, topPos + 1, leftPos + imageWidth - 1, topPos + imageHeight - 1, 0xCC2A1520);
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + 1, 0xFF6B1A1A);
        g.fill(leftPos, topPos + imageHeight - 1, leftPos + imageWidth, topPos + imageHeight, 0xFF6B1A1A);
        g.fill(leftPos, topPos, leftPos + 1, topPos + imageHeight, 0xFF6B1A1A);
        g.fill(leftPos + imageWidth - 1, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF6B1A1A);

        String current = currentRitualId();
        int listTop = topPos + TITLE_HEIGHT;
        for (int i = 0; i < VISIBLE_ROWS; i++) {
            int idx = scrollOffset + i;
            if (idx < 0 || idx >= entries.size()) continue;
            Entry e = entries.get(idx);
            int rowY = listTop + i * ROW_HEIGHT;
            boolean hovered = mouseX >= leftPos + LIST_X && mouseX <= leftPos + imageWidth - LIST_X
                    && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;
            boolean selected = e.id.toString().equals(current);
            int bg = selected ? 0xAA4A1A2A : (hovered ? 0x44FFFFFF : 0);
            if (bg != 0) {
                g.fill(leftPos + LIST_X, rowY, leftPos + imageWidth - LIST_X, rowY + ROW_HEIGHT - 1, bg);
            }
            int textColor = selected ? 0xFFE7B0C0 : (hovered ? 0xFFFFFFFF : 0xFFCFCFCF);
            g.drawString(font, e.name, leftPos + LIST_X + 5, rowY + (ROW_HEIGHT - 8) / 2, textColor, false);
        }

        if (maxScroll() > 0) {
            int trackX = leftPos + imageWidth - 4;
            int trackTop = listTop;
            int trackHeight = VISIBLE_ROWS * ROW_HEIGHT;
            int thumbHeight = Math.max(10, trackHeight * VISIBLE_ROWS / entries.size());
            int thumbY = trackTop + (trackHeight - thumbHeight) * scrollOffset / maxScroll();
            g.fill(trackX, trackTop, trackX + 2, trackTop + trackHeight, 0x40FFFFFF);
            g.fill(trackX, thumbY, trackX + 2, thumbY + thumbHeight, 0xFF8B3A3A);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawCenteredString(font, title, imageWidth / 2, 7, 0xCCA05050);
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int listTop = topPos + TITLE_HEIGHT;
            boolean inList = mouseX >= leftPos + LIST_X && mouseX <= leftPos + imageWidth - LIST_X
                    && mouseY >= listTop && mouseY < listTop + VISIBLE_ROWS * ROW_HEIGHT;
            if (inList) {
                int row = (int) ((mouseY - listTop) / ROW_HEIGHT);
                int idx = scrollOffset + row;
                if (idx >= 0 && idx < entries.size()
                        && minecraft != null && minecraft.gameMode != null) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, idx);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record Entry(ResourceLocation id, Component name) {}
}
