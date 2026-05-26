package com.breakinblocks.neovitae.client.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;

public class SideToggleButton extends AbstractButton {

    private static final int FILL_ALLOWED = 0xFF2E7D32;
    private static final int FILL_BLOCKED = 0xFF6B1414;
    private static final int BORDER_HOVER = 0xFFFFFFFF;
    private static final int BORDER_IDLE = 0xFF202020;
    private static final int TEXT_COLOR = 0xFFFFFFFF;

    private final Direction direction;
    private final BooleanSupplier allowed;
    private final OnPress onPress;

    public SideToggleButton(int x, int y, Direction direction, Component label,
                            BooleanSupplier allowed, OnPress onPress) {
        super(x, y, 18, 18, label);
        this.direction = direction;
        this.allowed = allowed;
        this.onPress = onPress;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public void onPress() {
        onPress.onPress(this);
    }

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();

        int fill = allowed.getAsBoolean() ? FILL_ALLOWED : FILL_BLOCKED;
        int border = isHoveredOrFocused() ? BORDER_HOVER : BORDER_IDLE;

        g.fill(x + 1, y + 1, x + w - 1, y + h - 1, fill);
        g.fill(x, y, x + w, y + 1, border);
        g.fill(x, y + h - 1, x + w, y + h, border);
        g.fill(x, y, x + 1, y + h, border);
        g.fill(x + w - 1, y, x + w, y + h, border);

        Font font = Minecraft.getInstance().font;
        g.drawCenteredString(font, getMessage(), x + w / 2, y + (h - 8) / 2, TEXT_COLOR);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput out) {
        defaultButtonNarrationText(out);
    }

    @FunctionalInterface
    public interface OnPress {
        void onPress(SideToggleButton button);
    }
}
