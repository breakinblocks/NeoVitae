package com.breakinblocks.neovitae.client.hud;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class GuiEditHUD extends Screen {

    @Nullable private final Screen parent;
    private final Map<ResourceLocation, Vec2> overrides = new HashMap<>();
    @Nullable private HUDElement dragged;

    public GuiEditHUD(@Nullable Screen parent) {
        super(Component.translatable("gui.neovitae.hud.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.translatable("gui.neovitae.hud.default"), b -> {
            overrides.clear();
            ElementRegistry.resetPositions();
        }).pos(width / 2 - 110, height - 30).size(70, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.neovitae.hud.save"), b -> {
            overrides.forEach(ElementRegistry::setPosition);
            ElementRegistry.save();
            onClose();
        }).pos(width / 2 - 35, height - 30).size(70, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.neovitae.hud.cancel"), b -> {
            overrides.clear();
            onClose();
        }).pos(width / 2 + 40, height - 30).size(70, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.drawCenteredString(font, getTitle(), width / 2, 14, 0xFFFFFFFF);

        Window window = minecraft.getWindow();
        for (HUDElement element : ElementRegistry.getElements()) {
            if (element == dragged) {
                continue;
            }
            ResourceLocation key = ElementRegistry.getKey(element);
            Vec2 position = overrides.getOrDefault(key, ElementRegistry.getPosition(key));
            int xPos = (int) (window.getGuiScaledWidth() * position.x);
            int yPos = (int) (window.getGuiScaledHeight() * position.y);
            drawWithBox(guiGraphics, element, partialTicks, xPos, yPos);
        }

        if (dragged != null) {
            int[] bounded = getBoundedDrag(window, mouseX, mouseY);
            drawWithBox(guiGraphics, dragged, partialTicks, bounded[0], bounded[1]);
        }
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && dragged == null) {
            HUDElement element = getHoveredElement(mouseX, mouseY);
            if (element != null) {
                dragged = element;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragged != null) {
            Window window = minecraft.getWindow();
            int[] bounded = getBoundedDrag(window, mouseX, mouseY);
            overrides.put(ElementRegistry.getKey(dragged), new Vec2(
                    (float) bounded[0] / window.getGuiScaledWidth(),
                    (float) bounded[1] / window.getGuiScaledHeight()));
            dragged = null;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Nullable
    private HUDElement getHoveredElement(double mouseX, double mouseY) {
        Window window = minecraft.getWindow();
        for (HUDElement element : ElementRegistry.getElements()) {
            ResourceLocation key = ElementRegistry.getKey(element);
            Vec2 position = overrides.getOrDefault(key, ElementRegistry.getPosition(key));
            int xPos = (int) (window.getGuiScaledWidth() * position.x);
            int yPos = (int) (window.getGuiScaledHeight() * position.y);
            int w = Math.max(element.getWidth(), 8);
            int h = Math.max(element.getHeight(), 8);
            if (mouseX >= xPos && mouseX <= xPos + w && mouseY >= yPos && mouseY <= yPos + h) {
                return element;
            }
        }
        return null;
    }

    private int[] getBoundedDrag(Window window, double mouseX, double mouseY) {
        int w = Math.max(dragged.getWidth(), 8);
        int h = Math.max(dragged.getHeight(), 8);
        int drawX = (int) (mouseX - w / 2.0);
        drawX = Math.max(0, Math.min(drawX, window.getGuiScaledWidth() - w));
        int drawY = (int) (mouseY - h / 2.0);
        drawY = Math.max(0, Math.min(drawY, window.getGuiScaledHeight() - h));
        return new int[]{drawX, drawY};
    }

    private void drawWithBox(GuiGraphics guiGraphics, HUDElement element, float partialTicks, int drawX, int drawY) {
        int color = ElementRegistry.getColor(ElementRegistry.getKey(element));
        int w = Math.max(element.getWidth(), 8);
        int h = Math.max(element.getHeight(), 8);
        guiGraphics.fill(drawX, drawY, drawX + w, drawY + h, color);
        guiGraphics.renderOutline(drawX, drawY, w, h, 0xFFFFFFFF);
        element.draw(guiGraphics, partialTicks, drawX, drawY);
    }
}
