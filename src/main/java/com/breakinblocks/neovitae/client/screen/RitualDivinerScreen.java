package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datamap.RitualStats;
import com.breakinblocks.neovitae.common.item.ItemRitualDiviner;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.menu.RitualDivinerMenu;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
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
        super(menu, playerInventory, title, PANEL_WIDTH, TITLE_HEIGHT + VISIBLE_ROWS * ROW_HEIGHT + LIST_PAD_BOTTOM);
    }

    @Override
    protected void init() {
        super.init();
        entries.clear();
        for (Identifier id : menu.getRitualIds()) {
            Ritual ritual = RitualRegistry.getRitual(id);
            Component name = ritual != null
                    ? Component.translatable(ritual.getTranslationKey())
                    : Component.literal(id.toString());
            entries.add(new Entry(id, name));
        }
        entries.sort(Comparator.comparing(e -> e.name().getString(), String.CASE_INSENSITIVE_ORDER));
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
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
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
            g.text(font, e.name, leftPos + LIST_X + 5, rowY + (ROW_HEIGHT - 8) / 2, textColor, false);
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
    protected void extractLabels(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        g.centeredText(font, title, imageWidth / 2, 7, 0xCCA05050);
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
            double mouseX = event.x();
            double mouseY = event.y();
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
        return super.mouseClicked(event, dragging);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        super.extractTooltip(g, mouseX, mouseY);
        int idx = hoveredIndex(mouseX, mouseY);
        if (idx >= 0) {
            g.setComponentTooltipForNextFrame(font, buildTooltip(entries.get(idx)), mouseX, mouseY);
        }
    }

    private int hoveredIndex(double mouseX, double mouseY) {
        int listTop = topPos + TITLE_HEIGHT;
        if (mouseX < leftPos + LIST_X || mouseX > leftPos + imageWidth - LIST_X) return -1;
        if (mouseY < listTop || mouseY >= listTop + VISIBLE_ROWS * ROW_HEIGHT) return -1;
        int idx = scrollOffset + (int) ((mouseY - listTop) / ROW_HEIGHT);
        return (idx >= 0 && idx < entries.size()) ? idx : -1;
    }

    private List<Component> buildTooltip(Entry e) {
        List<Component> lines = new ArrayList<>();
        lines.add(e.name.copy().withStyle(ChatFormatting.GOLD));

        Ritual ritual = RitualRegistry.getRitual(e.id);
        if (ritual == null) return lines;

        addWrapped(lines, Component.translatable(ritual.getTranslationKey() + ".info").getString(),
                170, ChatFormatting.GRAY, ChatFormatting.ITALIC);

        RitualStats stats = RitualRegistry.getStats(ritual);
        int activation = stats != null ? stats.activationCost() : ritual.getActivationCost();
        int upkeep = stats != null ? stats.refreshCost() : ritual.getRefreshCost();
        int cycle = stats != null ? stats.refreshTime() : ritual.getRefreshTime();
        int crystal = stats != null ? stats.crystalLevel() : ritual.getCrystalLevel();

        lines.add(Component.empty());
        lines.add(Component.translatable("tooltip.neovitae.diviner.stat.activation", fmt(activation))
                .withStyle(ChatFormatting.DARK_RED));
        lines.add(Component.translatable("tooltip.neovitae.diviner.stat.upkeep", fmt(upkeep))
                .withStyle(ChatFormatting.DARK_RED));
        lines.add(Component.translatable("tooltip.neovitae.diviner.stat.cycle", cycle)
                .withStyle(ChatFormatting.GRAY));
        Component crystalName = crystalName(crystal);
        if (crystalName != null) {
            lines.add(Component.translatable("tooltip.neovitae.diviner.stat.crystal", crystalName)
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
        return lines;
    }

    private void addWrapped(List<Component> lines, String text, int maxWidth, ChatFormatting... styles) {
        StringBuilder line = new StringBuilder();
        for (String word : text.split(" ")) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (font.width(test) > maxWidth && line.length() > 0) {
                lines.add(Component.literal(line.toString()).withStyle(styles));
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(test);
            }
        }
        if (line.length() > 0) {
            lines.add(Component.literal(line.toString()).withStyle(styles));
        }
    }

    @Nullable
    private Component crystalName(int level) {
        return switch (level) {
            case 1 -> new ItemStack(NVItems.ACTIVATION_CRYSTAL_AWAKENED.get()).getHoverName();
            case 2 -> new ItemStack(NVItems.ACTIVATION_CRYSTAL_CREATIVE.get()).getHoverName();
            default -> null;
        };
    }

    private static String fmt(int value) {
        return String.format("%,d", value);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record Entry(Identifier id, Component name) {}
}
