package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.menu.AthanorMenu;
import com.breakinblocks.neovitae.compat.jei.NeoVitaeJEIPlugin;
import com.breakinblocks.neovitae.compat.jei.athanor.AthanorRecipeCategory;
import com.breakinblocks.neovitae.util.helper.RenderHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AthanorScreen extends AbstractContainerScreen<AthanorMenu> {
    private final ResourceLocation background = ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "textures/gui/container/athanor_gui.png");
    private final ResourceLocation progress = ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "container/athanor/progress");
    private final ResourceLocation gauge = ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "container/athanor/gauge");
    private static final ResourceLocation BARS_TEXTURE = NeoVitae.rl("textures/hud/bars.png");

    private static final SpiritusType[] ORDERED_TYPES = {
            SpiritusType.RAW, SpiritusType.RUINA,
            SpiritusType.INVICTUS, SpiritusType.NIHILUM, SpiritusType.VINDICTA
    };

    private static final int GAUGE_X = 35;
    private static final int GAUGE_Y = 76;
    private static final int[] BAR_X_OFFSETS = {2, 0, 0, 0, 2};
    private static final int[] BAR_WIDTHS = {52, 56, 58, 56, 52};

    public AthanorScreen(AthanorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 208;
        this.titleLabelX = 38;
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

        renderSpiritusGauge(guiGraphics);

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderSpiritusGauge(GuiGraphics guiGraphics) {
        Map<SpiritusType, Double> costs = menu.tile.getCurrentRecipeSpiritusCost();
        int gx = leftPos + GAUGE_X;
        int gy = topPos + GAUGE_Y;

        for (int idx = 0; idx < ORDERED_TYPES.length; idx++) {
            SpiritusType type = ORDERED_TYPES[idx];
            int i = idx + 1;

            double current = menu.tile.getChunkSpiritus(type);
            double max = menu.tile.getChunkSpiritusMax(type);
            if (max <= 0) max = 100.0;
            double ratio = Math.max(0, Math.min(1, current / max));

            int fullBarWidth = BAR_WIDTHS[idx];
            int barX = gx + BAR_X_OFFSETS[idx];
            int barY = gy + 4 * i;
            int barHeight = 2;

            // UV from bars.png texture (uses HUD formula for the color strip source)
            int textureXOffset = (i > 3) ? (i - 3) : (3 - i);
            int textureX = 2 * textureXOffset + 84;
            int textureY = 4 * i + 220;

            int fillWidth = (int) (fullBarWidth * ratio);
            if (fillWidth > 0) {
                guiGraphics.blit(BARS_TEXTURE, barX, barY, textureX, textureY, fillWidth, barHeight);
            }

            Double required = costs.get(type);
            if (required != null && required > 0) {
                int requiredWidth = (int) (fullBarWidth * Math.min(1.0, required / max));
                if (current < required) {
                    guiGraphics.fill(barX + fillWidth, barY, barX + requiredWidth, barY + barHeight, 0xAAFF2222);
                }
                int markerX = barX + requiredWidth;
                guiGraphics.fill(markerX, barY - 1, markerX + 1, barY + barHeight + 1, 0xFFFFFFFF);
            }
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

        // Slot area tooltips when hovering empty slots
        if (this.hoveredSlot != null && !this.hoveredSlot.hasItem()) {
            int slotIdx = this.hoveredSlot.getSlotIndex();
            if (slotIdx == 0) {
                guiGraphics.renderTooltip(this.font, Component.literal("Tool").withStyle(ChatFormatting.GRAY), x, y);
            } else if (slotIdx >= 1 && slotIdx <= 6) {
                guiGraphics.renderTooltip(this.font, Component.literal("Input").withStyle(ChatFormatting.GRAY), x, y);
            } else if (slotIdx == 7) {
                guiGraphics.renderTooltip(this.font, Component.literal("Fluid Input").withStyle(ChatFormatting.GRAY), x, y);
            } else if (slotIdx == 8) {
                guiGraphics.renderTooltip(this.font, Component.literal("Fluid Output").withStyle(ChatFormatting.GRAY), x, y);
            } else if (slotIdx >= 9 && slotIdx <= 13) {
                guiGraphics.renderTooltip(this.font, Component.literal("Output").withStyle(ChatFormatting.GRAY), x, y);
            }
        }

        // Progress arrow tooltip
        if (isOverProgressArrow(x, y)) {
            guiGraphics.renderTooltip(this.font, Component.literal("Show Recipes").withStyle(ChatFormatting.YELLOW), x, y);
        }

        // Spiritus gauge tooltip
        Map<SpiritusType, Double> costs = menu.tile.getCurrentRecipeSpiritusCost();
        int gx = leftPos + GAUGE_X;
        int gy = topPos + GAUGE_Y;
        for (int idx = 0; idx < ORDERED_TYPES.length; idx++) {
            SpiritusType type = ORDERED_TYPES[idx];
            int i = idx + 1;
            int fullBarWidth = BAR_WIDTHS[idx];
            int barX = gx + BAR_X_OFFSETS[idx];
            int barY = gy + 4 * i;

            if (x >= barX && x <= barX + fullBarWidth && y >= barY - 1 && y <= barY + 3) {
                double current = menu.tile.getChunkSpiritus(type);
                double max = menu.tile.getChunkSpiritusMax(type);
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.literal(type.toCapitalized() + " Spiritus"));
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

    private boolean isOverProgressArrow(double mouseX, double mouseY) {
        int ax = leftPos + 63;
        int ay = topPos + 51;
        return mouseX >= ax && mouseX < ax + 38 && mouseY >= ay && mouseY < ay + 23;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isOverProgressArrow(mouseX, mouseY)) {
            var runtime = NeoVitaeJEIPlugin.jeiRuntime;
            if (runtime != null) {
                runtime.getRecipesGui().showTypes(List.of(AthanorRecipeCategory.RECIPE_TYPE));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(background, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        int progressWidth = menu.tile.getProgressForGui();
        if (progressWidth > 0) {
            guiGraphics.blitSprite(progress, 38, 23, 0, 4, leftPos + 63, topPos + 51, progressWidth, 19);
        }
    }
}
