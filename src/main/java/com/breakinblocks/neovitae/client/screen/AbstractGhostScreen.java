package com.breakinblocks.neovitae.client.screen;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.widgets.MultiIconButton;
import com.breakinblocks.neovitae.common.menu.AbstractGhostMenu;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGhostScreen<T extends AbstractGhostMenu<?>> extends AbstractContainerScreen<T> {
    public static final Identifier SELECTED = NeoVitae.rl("container/ghost_selected");

    public AbstractGhostScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    public AbstractGhostScreen(T menu, Inventory playerInventory, Component title, int imageWidth, int imageHeight) {
        super(menu, playerInventory, title, imageWidth, imageHeight);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = this.imageHeight - 94;
    }

    private final List<Pair<MultiIconButton, Integer>> updateButtons = new ArrayList<>();
    public void addMultiIconButton(int dataIndex, MultiIconButton button) {
        updateButtons.add(Pair.of(button, dataIndex));
        addRenderableWidget(button);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateButtons.forEach(pair -> pair.left().setState(this.menu.getData(pair.right())));
    }

    public abstract Identifier background();

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int x, int y) {
        super.extractTooltip(guiGraphics, x, y);
        updateButtons.forEach(pair -> {
            MultiIconButton button = pair.left();
            if (button.isHovered()) {
                guiGraphics.setTooltipForNextFrame(this.font, button.getHoverText(), x, y);
            }
        });
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.background(), leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256);
        int lastIdx = this.menu.getLastGhostSlotClicked();
        if (lastIdx >= 0) {
            Slot lastSlot = this.menu.getSlot(lastIdx);
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SELECTED, leftPos + lastSlot.x - 4, topPos + lastSlot.y - 4, 24, 24);
        }
    }
}
