package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.ItemRitualReader;
import com.breakinblocks.neovitae.ritual.EnumFillMode;
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

    private static final int KEEP_ROW_H = 18;
    private static final int FILL_LABEL_H = 14;
    private static final int FILL_ROW_H = 20;
    private static final int FILL_COLS = 3;
    private static final int FILL_ROWS = 2;

    private final List<RangeInfo> ranges;
    private final boolean usesKeepCount;
    private final boolean usesFillMode;
    private int selectedAspect;
    private int keepCount;
    private EnumFillMode fillMode;

    public RitualConfiguratorScreen(RitualConfiguratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.ranges = menu.getRanges();
        this.selectedAspect = menu.getInitialAspect();
        this.usesKeepCount = menu.usesKeepCount();
        this.keepCount = menu.getInitialKeepCount();
        this.imageWidth = PANEL_WIDTH;
        this.usesFillMode = menu.usesFillMode();
        this.fillMode = menu.getInitialFillMode();
        this.imageHeight = TITLE_HEIGHT + ranges.size() * ROW_HEIGHT
                + ASPECT_LABEL_H + ASPECT_ROW_H + (usesKeepCount ? GAP + KEEP_ROW_H : 0)
                + (usesFillMode ? GAP + FILL_LABEL_H + FILL_ROWS * FILL_ROW_H : 0)
                + GAP + BUTTON_H + PAD_BOTTOM;
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

    private int keepRowY() {
        return aspectRowY() + ASPECT_ROW_H + GAP;
    }

    private int keepStepperX() {
        return leftPos + imageWidth - LIST_X - 48;
    }

    private int afterKeepY() {
        int afterAspect = aspectRowY() + ASPECT_ROW_H;
        return usesKeepCount ? keepRowY() + KEEP_ROW_H : afterAspect;
    }

    private int fillLabelY() {
        return afterKeepY() + GAP;
    }

    private int fillRowY() {
        return fillLabelY() + FILL_LABEL_H;
    }

    private int fillButtonWidth() {
        return (imageWidth - LIST_X * 2) / FILL_COLS;
    }

    private int fillButtonX(int index) {
        return leftPos + LIST_X + (index % FILL_COLS) * fillButtonWidth();
    }

    private int fillButtonY(int index) {
        return fillRowY() + (index / FILL_COLS) * FILL_ROW_H;
    }

    private boolean overFillButton(int index, double mouseX, double mouseY) {
        int bx = fillButtonX(index);
        int by = fillButtonY(index);
        return mouseX >= bx && mouseX < bx + fillButtonWidth() - 1 && mouseY >= by && mouseY < by + FILL_ROW_H - 1;
    }

    private int editButtonY() {
        return (usesFillMode ? fillRowY() + FILL_ROWS * FILL_ROW_H : afterKeepY()) + GAP;
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
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
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
            g.drawString(font, rangeLabel(r.key()), leftPos + LIST_X + 5, rowY + (ROW_HEIGHT - 8) / 2, textColor, false);
            String dims = r.sizeX() + "×" + r.sizeY() + "×" + r.sizeZ();
            int dimX = leftPos + imageWidth - LIST_X - 5 - font.width(dims);
            g.drawString(font, dims, dimX, rowY + (ROW_HEIGHT - 8) / 2, 0xFF9A8088, false);
        }

        g.drawString(font, Component.translatable("gui.neovitae.configurator.aspect"),
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
                int c = ASPECT_COLORS[i];
                g.fill(bx, by, bx + btnW - 1, by + 2, c);
            }
            Component label = Component.translatable("gui.neovitae.configurator.aspect." + SpiritusType.values()[i].getSerializedName());
            int lw = font.width(label);
            int color = isSelected ? ASPECT_COLORS[i] : (hovered ? 0xFFFFFFFF : 0xFFB0A0A4);
            g.drawString(font, label, bx + (btnW - lw) / 2, by + (ASPECT_ROW_H - 8) / 2, color, false);
        }

        if (usesFillMode) {
            g.drawString(font, Component.translatable("gui.neovitae.configurator.fill_mode"),
                    leftPos + LIST_X + 2, fillLabelY() + 3, 0xFFA05050, false);
            EnumFillMode[] modes = EnumFillMode.values();
            int fbw = fillButtonWidth();
            for (int i = 0; i < modes.length; i++) {
                int bx = fillButtonX(i);
                int by = fillButtonY(i);
                boolean hovered = overFillButton(i, mouseX, mouseY);
                boolean isSelected = modes[i] == fillMode;
                int fill = isSelected ? 0xAA3A2030 : (hovered ? 0x33FFFFFF : 0x22000000);
                g.fill(bx, by, bx + fbw - 1, by + FILL_ROW_H - 1, fill);
                if (isSelected) {
                    g.fill(bx, by, bx + fbw - 1, by + 2, 0xFFB8860B);
                }
                Component label = Component.translatable(modes[i].translationKey());
                int lw = font.width(label);
                int color = isSelected ? 0xFFB8860B : (hovered ? 0xFFFFFFFF : 0xFFB0A0A4);
                g.drawString(font, label, bx + (fbw - lw) / 2, by + (FILL_ROW_H - 8) / 2, color, false);
            }
        }

        if (usesKeepCount) {
            int ky = keepRowY();
            g.drawString(font, Component.translatable("gui.neovitae.configurator.keep"),
                    leftPos + LIST_X + 2, ky + (KEEP_ROW_H - 8) / 2, 0xFFA05050, false);
            int sx = keepStepperX();
            boolean minusHover = mouseX >= sx && mouseX < sx + 14 && mouseY >= ky && mouseY < ky + KEEP_ROW_H;
            boolean plusHover = mouseX >= sx + 34 && mouseX < sx + 48 && mouseY >= ky && mouseY < ky + KEEP_ROW_H;
            g.fill(sx, ky, sx + 14, ky + KEEP_ROW_H, minusHover ? 0x55FFFFFF : 0x33000000);
            g.fill(sx + 34, ky, sx + 48, ky + KEEP_ROW_H, plusHover ? 0x55FFFFFF : 0x33000000);
            g.drawString(font, "-", sx + 5, ky + (KEEP_ROW_H - 8) / 2, 0xFFE0C0C8, false);
            g.drawString(font, "+", sx + 39, ky + (KEEP_ROW_H - 8) / 2, 0xFFE0C0C8, false);
            String val = String.valueOf(keepCount);
            g.drawString(font, val, sx + 14 + (20 - font.width(val)) / 2, ky + (KEEP_ROW_H - 8) / 2, 0xFFFFFFFF, false);
        }

        int eby = editButtonY();
        boolean editHovered = mouseX >= leftPos + LIST_X && mouseX < leftPos + imageWidth - LIST_X
                && mouseY >= eby && mouseY < eby + BUTTON_H;
        g.fill(leftPos + LIST_X, eby, leftPos + imageWidth - LIST_X, eby + BUTTON_H, editHovered ? 0xAA5A1A2A : 0xAA3A1520);
        Component editLabel = Component.translatable("gui.neovitae.configurator.edit_area");
        g.drawString(font, editLabel, leftPos + (imageWidth - font.width(editLabel)) / 2, eby + (BUTTON_H - 8) / 2,
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
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        if (menu.isRitualActive()) {
            g.drawCenteredString(font, Component.translatable(menu.getRitualKey()), imageWidth / 2, 7, 0xFFE7B0C0);
        } else {
            g.drawCenteredString(font, Component.translatable(menu.getRitualKey()), imageWidth / 2, 3, 0xFFE7B0C0);
            g.drawCenteredString(font, Component.translatable("gui.neovitae.configurator.not_active"),
                    imageWidth / 2, 13, 0xFFB8860B);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && minecraft != null && minecraft.gameMode != null) {
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

            if (usesKeepCount) {
                int ky = keepRowY();
                int sx = keepStepperX();
                if (mouseY >= ky && mouseY < ky + KEEP_ROW_H) {
                    if (mouseX >= sx && mouseX < sx + 14 && keepCount > 0) {
                        keepCount--;
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, RitualConfiguratorMenu.KEEP_MINUS_BUTTON);
                        return true;
                    }
                    if (mouseX >= sx + 34 && mouseX < sx + 48 && keepCount < 64) {
                        keepCount++;
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, RitualConfiguratorMenu.KEEP_PLUS_BUTTON);
                        return true;
                    }
                }
            }

            if (usesFillMode) {
                EnumFillMode[] modes = EnumFillMode.values();
                for (int i = 0; i < modes.length; i++) {
                    if (overFillButton(i, mouseX, mouseY)) {
                        fillMode = modes[i];
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId,
                                RitualConfiguratorMenu.FILL_MODE_BUTTON_BASE + i);
                        return true;
                    }
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
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        for (int i = 0; i < ranges.size(); i++) {
            if (inRow(mouseX, mouseY, listTop() + i * ROW_HEIGHT)) {
                g.renderComponentTooltip(font, rangeTooltip(ranges.get(i)), mouseX, mouseY);
                return;
            }
        }
        int innerW = imageWidth - LIST_X * 2;
        int btnW = innerW / SpiritusType.values().length;
        for (int i = 0; i < SpiritusType.values().length; i++) {
            int bx = leftPos + LIST_X + i * btnW;
            int by = aspectRowY();
            if (mouseX >= bx && mouseX < bx + btnW - 1 && mouseY >= by && mouseY < by + ASPECT_ROW_H - 1) {
                g.renderComponentTooltip(font, aspectTooltip(SpiritusType.values()[i]), mouseX, mouseY);
                return;
            }
        }
        if (usesFillMode) {
            EnumFillMode[] modes = EnumFillMode.values();
            for (int i = 0; i < modes.length; i++) {
                if (overFillButton(i, mouseX, mouseY)) {
                    g.renderComponentTooltip(font, fillTooltip(modes[i]), mouseX, mouseY);
                    return;
                }
            }
        }
    }

    private List<Component> fillTooltip(EnumFillMode mode) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable(mode.translationKey()));
        lines.add(Component.translatable(mode.translationKey() + ".desc"));
        return lines;
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
