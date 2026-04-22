package com.breakinblocks.neovitae.client.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import java.util.function.Function;
import net.minecraft.client.input.InputWithModifiers;

public class MultiIconButton extends AbstractButton {

    private final Identifier[] icons;
    private final Component[] tooltips;
    private final OnPress onPress;
    public MultiIconButton(int x, int y, int width, int height, Component[] tooltips, Identifier[] icons, OnPress onPress) {
        super(x, y, width, height, Component.literal(""));
        this.onPress = onPress;
        this.icons = icons;
        this.tooltips = tooltips;
    }

    public MultiIconButton(Builder builder) {
        this(builder.x, builder.y, builder.width, builder.height, builder.tooltips, builder.icons, builder.onPress);
    }

    private int state = 0;
    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        onPress.onPress(this);
    }

    public Component getHoverText() {
        return tooltips[state % tooltips.length];
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, icons[state % icons.length], this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    public static Builder builder(OnPress onPress) {
        return new Builder(onPress);
    }

    public static class Builder {
        private final OnPress onPress;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;
        private Identifier[] icons;
        private Component[] tooltips;

        public Builder(OnPress onPress) {
            this.onPress = onPress;
        }

        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder bounds(int x, int y, int width, int height) {
            return this.pos(x, y).size(width, height);
        }

        public Builder tooltips(Component... tooltips) {
            this.tooltips = tooltips;
            return this;
        }

        public Builder icons(Identifier... icons) {
            this.icons = icons;
            return this;
        }

        public MultiIconButton build() {
            return build(MultiIconButton::new);
        }

        public MultiIconButton build(Function<Builder, MultiIconButton> builder) {
            return builder.apply(this);
        }
    }

    @FunctionalInterface
    public interface OnPress {
        void onPress(MultiIconButton button);
    }
}
