package com.breakinblocks.neovitae.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.menu.AthanorMenu;
import com.breakinblocks.neovitae.util.helper.RenderHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AthanorScreen extends AbstractContainerScreen<AthanorMenu> {
    private final ResourceLocation background = ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "textures/gui/container/athanor_gui.png");
    private final ResourceLocation progress = ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "container/athanor/progress");
    private final ResourceLocation gauge = ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "container/athanor/gauge");

    private static final SpiritusType[] TYPES = SpiritusType.values();
    private static final int[] TYPE_COLORS = {
            0xFFAA3333, // DEFAULT - dark red
            0xFF33AA33, // CORROSIVE - green
            0xFFDD8822, // DESTRUCTIVE - orange
            0xFF3355BB, // STEADFAST - blue
            0xFFAA33CC  // VENGEFUL - purple
    };
    private static final String[] TYPE_NAMES = {"Raw", "Corrosive", "Destructive", "Steadfast", "Vengeful"};

    private static final int GAUGE_X = 50;
    private static final int GAUGE_Y = 76;
    private static final int BAR_WIDTH = 40;
    private static final int BAR_HEIGHT = 4;
    private static final int BAR_SPACING = 6;

    public AthanorScreen(AthanorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 208;
        this.inventoryLabelY = imageHeight - 94;
    }

    private int inputX;
    private int inputY;
    private int outputX;
    private int outputY;

    @Override
    protected void init() {
        super.init();
        this.inputX = leftPos + 8;
        this.inputY = topPos + 43;
        this.outputX = leftPos + 152;
        this.outputY = topPos + 18;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (!this.menu.tile.inputTank.isEmpty()) {
            int fluidHeight = 63 * this.menu.tile.inputTank.getFluidAmount() / this.menu.tile.inputTank.getCapacity();
            RenderHelper.renderGuiFluid(guiGraphics, this.menu.tile.inputTank.getFluid().getFluid(), inputX, inputY + (63 - fluidHeight), 16, fluidHeight);
        }
        if (!this.menu.tile.outputTank.isEmpty()) {
            int fluidHeight = 63 * this.menu.tile.outputTank.getFluidAmount() / this.menu.tile.outputTank.getCapacity();
            RenderHelper.renderGuiFluid(guiGraphics, this.menu.tile.outputTank.getFluid().getFluid(), outputX, outputY + (63 - fluidHeight), 16, fluidHeight);
        }
        guiGraphics.blitSprite(gauge, inputX, inputY, 16, 57);
        guiGraphics.blitSprite(gauge, outputX, outputY, 16, 57);

        renderSpiritusGauge(guiGraphics, mouseX, mouseY);

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderSpiritusGauge(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Map<SpiritusType, Double> costs = menu.tile.getCurrentRecipeWillCost();
        int gx = leftPos + GAUGE_X;
        int gy = topPos + GAUGE_Y;

        for (int i = 0; i < TYPES.length; i++) {
            SpiritusType type = TYPES[i];
            int barY = gy + i * BAR_SPACING;
            double current = menu.tile.getChunkWill(type);
            double max = menu.tile.getChunkWillMax(type);
            if (max <= 0) max = 100.0;

            // Background (dark gray)
            guiGraphics.fill(gx, barY, gx + BAR_WIDTH, barY + BAR_HEIGHT, 0xFF222222);

            // Current fill
            int fillWidth = (int) (BAR_WIDTH * Math.min(1.0, current / max));
            if (fillWidth > 0) {
                guiGraphics.fill(gx, barY, gx + fillWidth, barY + BAR_HEIGHT, TYPE_COLORS[i]);
            }

            // Required threshold marker if recipe has a cost for this type
            Double required = costs.get(type);
            if (required != null && required > 0) {
                int requiredX = (int) (BAR_WIDTH * Math.min(1.0, required / max));
                if (current < required) {
                    guiGraphics.fill(gx + fillWidth, barY, gx + requiredX, barY + BAR_HEIGHT, 0x88FF2222);
                }
                guiGraphics.fill(gx + requiredX, barY - 1, gx + requiredX + 1, barY + BAR_HEIGHT + 1, 0xFFFFFFFF);
            }

            // Type initial label (left of bar)
            String label = TYPE_NAMES[i].substring(0, 1);
            guiGraphics.drawString(font, label, gx - 8, barY - 1, TYPE_COLORS[i] | 0xFF000000, true);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);

        if (x > inputX && x < inputX + 16 && y > inputY && y < inputY + 63) {
            List<Component> tip = new ArrayList<>();
            if (!this.menu.tile.inputTank.isEmpty()) {
                tip.add(this.menu.tile.inputTank.getFluid().getHoverName());
                tip.add(Component.literal(this.menu.tile.inputTank.getFluidAmount() + " / " + this.menu.tile.inputTank.getCapacity() + " mB"));
            } else {
                tip.add(Component.literal("Empty"));
            }
            guiGraphics.renderComponentTooltip(this.font, tip, x, y);
        }

        if (x > outputX && x < outputX + 16 && y > outputY && y < outputY + 63) {
            List<Component> tip = new ArrayList<>();
            if (!this.menu.tile.outputTank.isEmpty()) {
                tip.add(this.menu.tile.outputTank.getFluid().getHoverName());
                tip.add(Component.literal(this.menu.tile.outputTank.getFluidAmount() + " / " + this.menu.tile.outputTank.getCapacity() + " mB"));
            } else {
                tip.add(Component.literal("Empty"));
            }
            guiGraphics.renderComponentTooltip(this.font, tip, x, y);
        }

        // Spiritus gauge tooltip
        Map<SpiritusType, Double> costs = menu.tile.getCurrentRecipeWillCost();
        int gx = leftPos + GAUGE_X;
        int gy = topPos + GAUGE_Y;
        for (int i = 0; i < TYPES.length; i++) {
            SpiritusType type = TYPES[i];
            int barY = gy + i * BAR_SPACING;
            if (x >= gx - 8 && x <= gx + BAR_WIDTH && y >= barY - 1 && y <= barY + BAR_HEIGHT + 1) {
                double current = menu.tile.getChunkWill(type);
                double max = menu.tile.getChunkWillMax(type);
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.literal(TYPE_NAMES[i] + " Spiritus"));
                tooltip.add(Component.literal(String.format("%.1f / %.1f", current, max)));
                Double required = costs.get(type);
                if (required != null && required > 0) {
                    tooltip.add(Component.literal(String.format("Required: %.1f", required)));
                    if (current < required) {
                        tooltip.add(Component.literal("Insufficient!").withStyle(style -> style.withColor(0xFF5555)));
                    }
                }
                guiGraphics.renderComponentTooltip(this.font, tooltip, x, y);
                break;
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(background, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        guiGraphics.blitSprite(progress, leftPos + 63, topPos + 47, menu.tile.getProgressForGui(), 23);
    }
}
