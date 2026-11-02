package com.breakinblocks.neovitae.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.menu.MasterRoutingNodeMenu;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MasterRoutingNodeScreen extends AbstractContainerScreen<MasterRoutingNodeMenu> {
    private static final ResourceLocation BACKGROUND = NeoVitae.rl("textures/gui/masterroutingnode.png");
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
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 146;
        this.titleLabelX = 38;
    }

    private void drawStat(GuiGraphics guiGraphics, int line, Component label, Component value) {
        int y = STATS_Y + line * STATS_LINE_HEIGHT;
        guiGraphics.drawString(this.font, label, STATS_X, y, 0x808080, false);
        guiGraphics.drawString(this.font, value, STATS_VALUE_RIGHT - font.width(value), y, 0xB0A0A4, false);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);

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
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        guiGraphics.blit(BACKGROUND, leftPos + WELL_X, topPos + WELL_Y,
                WELL_BLANK_U, WELL_BLANK_V, WELL_W, WELL_H);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        boolean slotEmpty = hoveredSlot == null || !hoveredSlot.hasItem();

        if (slotEmpty && isHovering(62, 15, 16, 16, mouseX, mouseY)) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.translatable("gui.neovitae.master_routing.stack_upgrade_slot"));
            lines.add(Component.translatable("gui.neovitae.master_routing.stack_upgrade_slot.desc"));
            guiGraphics.renderComponentTooltip(font, lines, mouseX, mouseY);
            return;
        }

        if (slotEmpty && isHovering(98, 15, 16, 16, mouseX, mouseY)) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.translatable("gui.neovitae.master_routing.speed_upgrade_slot"));
            lines.add(Component.translatable("gui.neovitae.master_routing.speed_upgrade_slot.desc"));
            guiGraphics.renderComponentTooltip(font, lines, mouseX, mouseY);
            return;
        }

        int statsHeight = 3 * STATS_LINE_HEIGHT;
        if (isHovering(STATS_X, STATS_Y, STATS_VALUE_RIGHT - STATS_X, statsHeight, mouseX, mouseY)) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.translatable("gui.neovitae.master_routing.caps.title"));
            lines.add(Component.translatable("gui.neovitae.master_routing.caps.desc"));
            lines.add(Component.translatable("gui.neovitae.master_routing.caps.pulse", menu.getTickRate()));
            lines.add(Component.translatable("gui.neovitae.master_routing.caps.faces"));
            lines.add(Component.translatable("gui.neovitae.master_routing.caps.destination"));
            lines.add(Component.translatable("gui.neovitae.master_routing.caps.altar"));
            lines.add(Component.translatable("gui.neovitae.master_routing.caps.altar_rate"));
            lines.add(Component.translatable("gui.neovitae.master_routing.caps.altar_runes"));
            guiGraphics.renderComponentTooltip(font, lines, mouseX, mouseY);
        }
    }
}
