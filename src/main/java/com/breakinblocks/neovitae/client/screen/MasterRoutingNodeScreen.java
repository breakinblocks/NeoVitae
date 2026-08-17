package com.breakinblocks.neovitae.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.menu.MasterRoutingNodeMenu;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MasterRoutingNodeScreen extends AbstractContainerScreen<MasterRoutingNodeMenu> {
    private static final Identifier BACKGROUND = NeoVitae.rl("textures/gui/masterroutingnode.png");
    private static final DecimalFormat FORMAT = new DecimalFormat("#,###");

    private static final int WELL_X = 79;
    private static final int WELL_Y = 37;
    private static final int WELL_W = 58;
    private static final int WELL_H = 16;
    private static final int WELL_BLANK_U = 8;
    private static final int WELL_BLANK_V = 35;

    private static final int STATS_X = 8;
    private static final int STATS_Y = 33;
    private static final int STATS_LINE_HEIGHT = 9;
    private static final int STATS_VALUE_RIGHT = 168;

    public MasterRoutingNodeScreen(MasterRoutingNodeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 146);
        this.titleLabelX = 38;
    }

    private void drawStat(GuiGraphicsExtractor guiGraphics, int line, Component label, Component value) {
        int y = STATS_Y + line * STATS_LINE_HEIGHT;
        guiGraphics.text(this.font, label, STATS_X, y, 0xFF808080, false);
        guiGraphics.text(this.font, value, STATS_VALUE_RIGHT - font.width(value), y, 0xFFB0A0A4, false);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF404040, false);

        drawStat(guiGraphics, 0,
                Component.translatable("gui.neovitae.master_routing.stat.items"),
                Component.translatable("gui.neovitae.master_routing.stat.items_value", FORMAT.format(menu.getMaxTransfer())));
        drawStat(guiGraphics, 1,
                Component.translatable("gui.neovitae.master_routing.stat.fluid"),
                Component.translatable("gui.neovitae.master_routing.stat.fluid_value", FORMAT.format(menu.getMaxFluidTransfer())));
        drawStat(guiGraphics, 2,
                Component.translatable("gui.neovitae.master_routing.stat.energy"),
                Component.translatable("gui.neovitae.master_routing.stat.energy_value", FORMAT.format(menu.getMaxEnergyTransfer())));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos + WELL_X, topPos + WELL_Y,
                WELL_BLANK_U, WELL_BLANK_V, WELL_W, WELL_H, 256, 256);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractTooltip(guiGraphics, mouseX, mouseY);

        boolean slotEmpty = hoveredSlot == null || !hoveredSlot.hasItem();

        if (slotEmpty && isHovering(62, 15, 16, 16, mouseX, mouseY)) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.translatable("gui.neovitae.master_routing.stack_upgrade_slot"));
            lines.add(Component.translatable("gui.neovitae.master_routing.stack_upgrade_slot.desc"));
            guiGraphics.setComponentTooltipForNextFrame(this.font, lines, mouseX, mouseY);
            return;
        }

        if (slotEmpty && isHovering(98, 15, 16, 16, mouseX, mouseY)) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.translatable("gui.neovitae.master_routing.speed_upgrade_slot"));
            lines.add(Component.translatable("gui.neovitae.master_routing.speed_upgrade_slot.desc"));
            guiGraphics.setComponentTooltipForNextFrame(this.font, lines, mouseX, mouseY);
            return;
        }

        int statsHeight = 3 * STATS_LINE_HEIGHT;
        if (isHovering(STATS_X, STATS_Y, STATS_VALUE_RIGHT - STATS_X, statsHeight, mouseX, mouseY)) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.translatable("gui.neovitae.master_routing.caps.title"));
            lines.add(Component.translatable("gui.neovitae.master_routing.caps.desc"));
            lines.add(Component.translatable("gui.neovitae.master_routing.caps.pulse", menu.getTickRate()));
            lines.add(Component.translatable("gui.neovitae.master_routing.caps.faces"));
            guiGraphics.setComponentTooltipForNextFrame(this.font, lines, mouseX, mouseY);
        }
    }
}
