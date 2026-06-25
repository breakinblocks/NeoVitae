package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.ItemRitualReader;
import com.breakinblocks.neovitae.common.menu.RitualConfiguratorMenu;
import com.breakinblocks.neovitae.common.menu.RitualConfiguratorMenu.RangeInfo;

import java.util.ArrayList;
import java.util.List;

public class RitualConfiguratorScreen extends AbstractContainerScreen<RitualConfiguratorMenu> {

    private static final int PANEL_WIDTH = 216;
    private static final int TITLE_HEIGHT = 22;
    private static final int ROW_HEIGHT = 18;
    private static final int LIST_X = 6;
    private static final int ASPECT_LABEL_H = 14;
    private static final int ASPECT_ROW_H = 20;
    private static final int GAP = 6;
    private static final int BUTTON_H = 20;
    private static final int PAD_BOTTOM = 8;

    private static final int BORDER = 0xFF6B1A1A;
    private static final int BG_OUTER = 0xCC1A0A0A;
    private static final int BG_INNER = 0xCC2A1520;

    private static final int[] ASPECT_COLORS = {
            0xFFCFCFCF, 0xFFB23A3A, 0xFF6A4A8A, 0xFF3A8B5A, 0xFFB8860B
    };

    private final List<RangeInfo> ranges;
    private int selectedAspect;

    public RitualConfiguratorScreen(RitualConfiguratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, PANEL_WIDTH,
                TITLE_HEIGHT + menu.getRanges().size() * ROW_HEIGHT
                        + ASPECT_LABEL_H + ASPECT_ROW_H + GAP + BUTTON_H + PAD_BOTTOM);
        this.ranges = menu.getRanges();
        this.selectedAspect = menu.getInitialAspect();
    }

    private int listTop() {
        return topPos + TITLE_HEIGHT;
    }

    private int aspectLabelY() {
        return listTop() + ranges.size() * ROW_HEIGHT;
    }

    private int aspectRowY() {
        return aspectLabelY() + ASPECT_LABEL_H;
    }

    private int editButtonY() {
        return aspectRowY() + ASPECT_ROW_H + GAP;
    }

    private String selectedRangeKey() {
        if (minecraft == null || minecraft.player == null) return "";
        ItemStack held = minecraft.player.getItemInHand(menu.getHand());
        if (held.getItem() instanceof ItemRitualReader reader) {
            return reader.getRangeKey(held);
        }
        return "";
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, BG_OUTER);
        g.fill(leftPos + 1, topPos + 1, leftPos + imageWidth - 1, topPos + imageHeight - 1, BG_INNER);
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + 1, BORDER);
        g.fill(leftPos, topPos + imageHeight - 1, leftPos + imageWidth, topPos + imageHeight, BORDER);
        g.fill(leftPos, topPos, leftPos + 1, topPos + imageHeight, BORDER);
        g.fill(leftPos + imageWidth - 1, topPos, leftPos + imageWidth, topPos + imageHeight, BORDER);

        String selected = selectedRangeKey();
        for (int i = 0; i < ranges.size(); i++) {
            RangeInfo r = ranges.get(i);
            int rowY = listTop() + i * ROW_HEIGHT;
            boolean hovered = inRow(mouseX, mouseY, rowY);
            boolean isSelected = r.key().equals(selected);
            int bg = isSelected ? 0xAA4A1A2A : (hovered ? 0x44FFFFFF : 0);
            if (bg != 0) {
                g.fill(leftPos + LIST_X, rowY, leftPos + imageWidth - LIST_X, rowY + ROW_HEIGHT - 1, bg);
            }
            int textColor = isSelected ? 0xFFE7B0C0 : (hovered ? 0xFFFFFFFF : 0xFFCFCFCF);
            g.text(font, rangeLabel(r.key()), leftPos + LIST_X + 5, rowY + (ROW_HEIGHT - 8) / 2, textColor, false);
            String dims = r.sizeX() + "×" + r.sizeY() + "×" + r.sizeZ();
            int dimX = leftPos + imageWidth - LIST_X - 5 - font.width(dims);
            g.text(font, dims, dimX, rowY + (ROW_HEIGHT - 8) / 2, 0xFF9A8088, false);
        }

        g.text(font, Component.translatable("gui.neovitae.configurator.aspect"),
                leftPos + LIST_X + 2, aspectLabelY() + 3, 0xFFA05050, false);

        int innerW = imageWidth - LIST_X * 2;
        int btnW = innerW / SpiritusType.values().length;
        for (int i = 0; i < SpiritusType.values().length; i++) {
            int bx = leftPos + LIST_X + i * btnW;
            int by = aspectRowY();
            boolean hovered = mouseX >= bx && mouseX < bx + btnW - 1 && mouseY >= by && mouseY < by + ASPECT_ROW_H - 1;
            boolean isSelected = i == selectedAspect;
            int fill = isSelected ? 0xAA3A2030 : (hovered ? 0x33FFFFFF : 0x22000000);
            g.fill(bx, by, bx + btnW - 1, by + ASPECT_ROW_H - 1, fill);
            if (isSelected) {
                g.fill(bx, by, bx + btnW - 1, by + 2, ASPECT_COLORS[i]);
            }
            Component label = Component.translatable("gui.neovitae.configurator.aspect." + SpiritusType.values()[i].getSerializedName());
            int lw = font.width(label);
            int color = isSelected ? ASPECT_COLORS[i] : (hovered ? 0xFFFFFFFF : 0xFFB0A0A4);
            g.text(font, label, bx + (btnW - lw) / 2, by + (ASPECT_ROW_H - 8) / 2, color, false);
        }

        int eby = editButtonY();
        boolean editHovered = mouseX >= leftPos + LIST_X && mouseX < leftPos + imageWidth - LIST_X
                && mouseY >= eby && mouseY < eby + BUTTON_H;
        g.fill(leftPos + LIST_X, eby, leftPos + imageWidth - LIST_X, eby + BUTTON_H, editHovered ? 0xAA5A1A2A : 0xAA3A1520);
        Component editLabel = Component.translatable("gui.neovitae.configurator.edit_area");
        g.text(font, editLabel, leftPos + (imageWidth - font.width(editLabel)) / 2, eby + (BUTTON_H - 8) / 2,
                editHovered ? 0xFFFFE0E0 : 0xFFCFB0B8, false);
    }

    private boolean inRow(int mouseX, int mouseY, int rowY) {
        return mouseX >= leftPos + LIST_X && mouseX <= leftPos + imageWidth - LIST_X
                && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;
    }

    private Component rangeLabel(String key) {
        return Component.literal(capitalize(key));
    }

    private static String capitalize(String s) {
        if (s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        if (menu.isRitualActive()) {
            g.centeredText(font, Component.translatable(menu.getRitualKey()), imageWidth / 2, 7, 0xFFE7B0C0);
        } else {
            g.centeredText(font, Component.translatable(menu.getRitualKey()), imageWidth / 2, 3, 0xFFE7B0C0);
            g.centeredText(font, Component.translatable("gui.neovitae.configurator.not_active"),
                    imageWidth / 2, 13, 0xFFB8860B);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
        if (event.button() == 0 && minecraft != null && minecraft.gameMode != null) {
            double mouseX = event.x();
            double mouseY = event.y();
            for (int i = 0; i < ranges.size(); i++) {
                if (inRow((int) mouseX, (int) mouseY, listTop() + i * ROW_HEIGHT)) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, i);
                    return true;
                }
            }

            int innerW = imageWidth - LIST_X * 2;
            int btnW = innerW / SpiritusType.values().length;
            for (int i = 0; i < SpiritusType.values().length; i++) {
                int bx = leftPos + LIST_X + i * btnW;
                int by = aspectRowY();
                if (mouseX >= bx && mouseX < bx + btnW - 1 && mouseY >= by && mouseY < by + ASPECT_ROW_H - 1) {
                    selectedAspect = i;
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId,
                            RitualConfiguratorMenu.ASPECT_BUTTON_BASE + i);
                    return true;
                }
            }

            int eby = editButtonY();
            if (mouseX >= leftPos + LIST_X && mouseX < leftPos + imageWidth - LIST_X
                    && mouseY >= eby && mouseY < eby + BUTTON_H) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId,
                        RitualConfiguratorMenu.EDIT_IN_WORLD_BUTTON);
                return true;
            }
        }
        return super.mouseClicked(event, dragging);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        super.extractTooltip(g, mouseX, mouseY);
        for (int i = 0; i < ranges.size(); i++) {
            if (inRow(mouseX, mouseY, listTop() + i * ROW_HEIGHT)) {
                g.setComponentTooltipForNextFrame(font, rangeTooltip(ranges.get(i)), mouseX, mouseY);
                return;
            }
        }
        int innerW = imageWidth - LIST_X * 2;
        int btnW = innerW / SpiritusType.values().length;
        for (int i = 0; i < SpiritusType.values().length; i++) {
            int bx = leftPos + LIST_X + i * btnW;
            int by = aspectRowY();
            if (mouseX >= bx && mouseX < bx + btnW - 1 && mouseY >= by && mouseY < by + ASPECT_ROW_H - 1) {
                g.setComponentTooltipForNextFrame(font, aspectTooltip(SpiritusType.values()[i]), mouseX, mouseY);
                return;
            }
        }
    }

    private List<Component> aspectTooltip(SpiritusType type) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("spiritus.neovitae." + type.getSerializedName()).withStyle(ChatFormatting.LIGHT_PURPLE));

        String ritualKey = menu.getRitualKey();
        String perAspect = ritualKey + ".aspect." + type.getSerializedName();
        String perRitual = ritualKey + ".aspect_effect";
        String effectKey = I18n.exists(perAspect) ? perAspect
                : I18n.exists(perRitual) ? perRitual
                : "gui.neovitae.configurator.aspect.none";

        addWrapped(lines, Component.translatable(effectKey).getString(), 160, ChatFormatting.GRAY);
        return lines;
    }

    private void addWrapped(List<Component> lines, String text, int maxWidth, ChatFormatting style) {
        StringBuilder line = new StringBuilder();
        for (String word : text.split(" ")) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (font.width(test) > maxWidth && line.length() > 0) {
                lines.add(Component.literal(line.toString()).withStyle(style));
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(test);
            }
        }
        if (line.length() > 0) {
            lines.add(Component.literal(line.toString()).withStyle(style));
        }
    }

    private List<Component> rangeTooltip(RangeInfo r) {
        List<Component> lines = new ArrayList<>();
        lines.add(rangeLabel(r.key()).copy().withStyle(ChatFormatting.GOLD));
        int volume = r.sizeX() * r.sizeY() * r.sizeZ();
        lines.add(Component.translatable("gui.neovitae.configurator.tooltip.size",
                r.sizeX(), r.sizeY(), r.sizeZ()).withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("gui.neovitae.configurator.tooltip.volume",
                volume, r.maxVolume()).withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("gui.neovitae.configurator.tooltip.reach",
                r.maxHorizontal(), r.maxVertical()).withStyle(ChatFormatting.DARK_GRAY));
        lines.add(Component.empty());
        lines.add(Component.translatable(r.maxVolume() == 1
                ? "gui.neovitae.configurator.tooltip.single"
                : "gui.neovitae.configurator.tooltip.area").withStyle(ChatFormatting.BLUE));
        return lines;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
