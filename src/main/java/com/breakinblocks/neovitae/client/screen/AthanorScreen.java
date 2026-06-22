package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
    private final Identifier background = Identifier.fromNamespaceAndPath(NeoVitae.MODID, "textures/gui/container/athanor_gui.png");
    private final Identifier progress = Identifier.fromNamespaceAndPath(NeoVitae.MODID, "container/athanor/progress");
    private final Identifier gauge = Identifier.fromNamespaceAndPath(NeoVitae.MODID, "container/athanor/gauge");
    private static final Identifier BARS_TEXTURE = NeoVitae.rl("textures/hud/bars.png");

    private static final SpiritusType[] ORDERED_TYPES = {
            SpiritusType.RAW, SpiritusType.RUINA,
            SpiritusType.INVICTUS, SpiritusType.NIHILUM, SpiritusType.VINDICTA
    };
    private static final String[] TYPE_KEYS = {
            "will.neovitae.raw",
            "will.neovitae.ruina",
            "will.neovitae.invictus",
            "will.neovitae.nihilum",
            "will.neovitae.vindicta"
    };

    private static final int GAUGE_X = 35;
    private static final int GAUGE_Y = 76;
    private static final int[] BAR_X_OFFSETS = {2, 0, 0, 0, 2};
    private static final int[] BAR_WIDTHS = {52, 56, 58, 56, 52};

    public AthanorScreen(AthanorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 208);
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
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        if (!this.menu.tile.inputTank.isEmpty()) {
            int fluidHeight = 63 * this.menu.tile.inputTank.getFluidAmount() / this.menu.tile.inputTank.getCapacity();
            RenderHelper.renderGuiFluid(guiGraphics, this.menu.tile.inputTank.getFluid().getFluid(), inputX, inputY + (63 - fluidHeight), 16, fluidHeight);
        }
        if (!this.menu.tile.outputTank.isEmpty()) {
            int fluidHeight = 63 * this.menu.tile.outputTank.getFluidAmount() / this.menu.tile.outputTank.getCapacity();
            RenderHelper.renderGuiFluid(guiGraphics, this.menu.tile.outputTank.getFluid().getFluid(), outputX, outputY + (63 - fluidHeight), 16, fluidHeight);
        }
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, gauge, inputX, inputY, 16, 57);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, gauge, outputX, outputY, 16, 57);

        renderSpiritusGauge(guiGraphics);
    }

    private void renderSpiritusGauge(GuiGraphicsExtractor guiGraphics) {
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

            int textureXOffset = (i > 3) ? (i - 3) : (3 - i);
            int textureX = 2 * textureXOffset + 84;
            int textureY = 4 * i + 220;

            int fillWidth = (int) (fullBarWidth * ratio);
            if (fillWidth > 0) {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BARS_TEXTURE, barX, barY, (float) textureX, (float) textureY, fillWidth, barHeight, 256, 256);
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
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int x, int y) {
        super.extractTooltip(guiGraphics, x, y);

        if (x > inputX && x < inputX + 16 && y > inputY && y < inputY + 63) {
            List<Component> tip = new ArrayList<>();
            if (!this.menu.tile.inputTank.isEmpty()) {
                tip.add(this.menu.tile.inputTank.getFluid().getHoverName());
                tip.add(Component.translatable("gui.neovitae.athanor.tank_amount", this.menu.tile.inputTank.getFluidAmount(), this.menu.tile.inputTank.getCapacity()));
            } else {
                tip.add(Component.translatable("gui.neovitae.athanor.slot.empty"));
            }
            guiGraphics.setComponentTooltipForNextFrame(this.font, tip, x, y);
        }

        if (x > outputX && x < outputX + 16 && y > outputY && y < outputY + 63) {
            List<Component> tip = new ArrayList<>();
            if (!this.menu.tile.outputTank.isEmpty()) {
                tip.add(this.menu.tile.outputTank.getFluid().getHoverName());
                tip.add(Component.translatable("gui.neovitae.athanor.tank_amount", this.menu.tile.outputTank.getFluidAmount(), this.menu.tile.outputTank.getCapacity()));
            } else {
                tip.add(Component.translatable("gui.neovitae.athanor.slot.empty"));
            }
            guiGraphics.setComponentTooltipForNextFrame(this.font, tip, x, y);
        }

        if (this.hoveredSlot != null && !this.hoveredSlot.hasItem() && this.hoveredSlot.index <= 13) {
            int slotIdx = this.hoveredSlot.index;
            if (slotIdx == 0) {
                guiGraphics.setTooltipForNextFrame(this.font, Component.translatable("gui.neovitae.athanor.slot.tool").withStyle(ChatFormatting.GRAY), x, y);
            } else if (slotIdx >= 1 && slotIdx <= 6) {
                guiGraphics.setTooltipForNextFrame(this.font, Component.translatable("gui.neovitae.athanor.slot.input").withStyle(ChatFormatting.GRAY), x, y);
            } else if (slotIdx == 7) {
                guiGraphics.setTooltipForNextFrame(this.font, Component.translatable("gui.neovitae.athanor.slot.fluid_input").withStyle(ChatFormatting.GRAY), x, y);
            } else if (slotIdx == 8) {
                guiGraphics.setTooltipForNextFrame(this.font, Component.translatable("gui.neovitae.athanor.slot.fluid_output").withStyle(ChatFormatting.GRAY), x, y);
            } else if (slotIdx >= 9 && slotIdx <= 13) {
                guiGraphics.setTooltipForNextFrame(this.font, Component.translatable("gui.neovitae.athanor.slot.output").withStyle(ChatFormatting.GRAY), x, y);
            }
        }

        if (isOverProgressArrow(x, y)) {
            guiGraphics.setTooltipForNextFrame(this.font, Component.translatable("gui.neovitae.show_recipes").withStyle(ChatFormatting.YELLOW), x, y);
        }

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
                tooltip.add(Component.translatable("gui.neovitae.athanor.type_spiritus", Component.translatable(TYPE_KEYS[idx])));
                tooltip.add(Component.translatable("gui.neovitae.athanor.spiritus_progress", String.format("%.1f", current), String.format("%.1f", max)));
                Double required = costs.get(type);
                if (required != null && required > 0) {
                    tooltip.add(Component.translatable("gui.neovitae.athanor.spiritus_required", String.format("%.1f", required)));
                    if (current < required) {
                        tooltip.add(Component.translatable("gui.neovitae.athanor.insufficient").withStyle(style -> style.withColor(0xFF5555)));
                    }
                }
                guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, x, y);
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
    public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
        if (event.button() == 0 && isOverProgressArrow(event.x(), event.y())) {
            var runtime = NeoVitaeJEIPlugin.jeiRuntime;
            if (runtime != null) {
                runtime.getRecipesGui().showTypes(List.of(AthanorRecipeCategory.RECIPE_TYPE));
                return true;
            }
        }
        return super.mouseClicked(event, dragging);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, background, leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256);
        int progressWidth = menu.tile.getProgressForGui();
        if (progressWidth > 0) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, progress, 38, 23, 0, 4, leftPos + 63, topPos + 51, progressWidth, 19);
        }
    }
}
