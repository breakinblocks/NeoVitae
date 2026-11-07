package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import com.breakinblocks.neovitae.common.blockentity.AthanorBlockEntity;
import net.minecraft.network.chat.Component;
import java.util.Locale;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.widgets.SideConfigPanel;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.menu.AthanorMenu;
import com.breakinblocks.neovitae.compat.jei.NeoVitaeJEIPlugin;
import com.breakinblocks.neovitae.compat.jei.athanor.AthanorRecipeCategory;
import com.breakinblocks.neovitae.util.helper.RenderHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class AthanorScreen extends AbstractContainerScreen<AthanorMenu> {
    private final ResourceLocation background = ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "textures/gui/container/athanor_gui.png");
    private final ResourceLocation progress = ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "container/athanor/progress");
    private final ResourceLocation gauge = ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "container/athanor/gauge");
    private static final ResourceLocation BARS_TEXTURE = NeoVitae.rl("textures/hud/bars.png");
    private static final ResourceLocation SLOT_HIGHLIGHT = NeoVitae.rl("textures/gui/hellfire_forge.png");
    private static final int SIDE_PANEL_WIDTH = 44;
    private static final int SIDE_PANEL_HEIGHT = 62;

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
    private SideConfigPanel sidePanel;

    @Override
    protected void init() {
        super.init();
        this.inputX = leftPos + 8;
        this.inputY = topPos + 43;
        this.outputX = leftPos + 152;
        this.outputY = topPos + 18;
        AthanorBlockEntity tile = menu.tile;
        sidePanel = new SideConfigPanel(tile, tile.getSideConfig(), () -> tile.activeGroup,
                AthanorScreen::groupName, AthanorBlockEntity.GROUP_OUTPUT, tile);
        sidePanel.create(this::addRenderableWidget, sidePanelX() + 4, sidePanelY() + 4);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        sidePanel.tick();
    }

    private int sidePanelX() {
        return leftPos + imageWidth + 2;
    }

    private int sidePanelY() {
        return topPos + 14;
    }

    @Nullable
    public Rect2i sidePanelArea() {
        if (menu.tile.activeGroup < 0) return null;
        return new Rect2i(sidePanelX(), sidePanelY(), SIDE_PANEL_WIDTH, SIDE_PANEL_HEIGHT);
    }

    private static Component groupName(int group) {
        String key = switch (group) {
            case AthanorBlockEntity.GROUP_INPUT -> "input";
            case AthanorBlockEntity.GROUP_INPUT_BUCKET -> "input_bucket";
            case AthanorBlockEntity.GROUP_OUTPUT_BUCKET -> "output_bucket";
            case AthanorBlockEntity.GROUP_OUTPUT -> "output";
            default -> "tool";
        };
        return Component.translatable("gui.neovitae.athanor.group." + key).withStyle(ChatFormatting.YELLOW);
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
        if (this.hoveredSlot != null && !this.hoveredSlot.hasItem() && this.hoveredSlot.index <= 13) {
            int slotIdx = this.hoveredSlot.index;
            if (AthanorBlockEntity.groupOf(slotIdx) == menu.tile.activeGroup) {
                List<Component> lines = new ArrayList<>();
                lines.add(Component.translatable("gui.neovitae.tabula_vitae.active_slot", groupName(menu.tile.activeGroup)).withStyle(ChatFormatting.WHITE));
                lines.add(Component.translatable("gui.neovitae.tabula_vitae.active_hint").withStyle(ChatFormatting.GRAY));
                guiGraphics.renderComponentTooltip(this.font, lines, x, y);
            } else if (slotIdx == 0) {
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
            List<Component> lines = new ArrayList<>();
            AthanorBlockEntity.IdleReason reason = menu.getIdleReason();
            if (reason != AthanorBlockEntity.IdleReason.NONE) {
                lines.add(Component.translatable("gui.neovitae.athanor.idle." + reason.name().toLowerCase(Locale.ROOT))
                        .withStyle(ChatFormatting.RED));
            }
            lines.add(Component.translatable("gui.neovitae.show_recipes").withStyle(ChatFormatting.YELLOW));
            guiGraphics.renderComponentTooltip(this.font, lines, x, y);
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (sidePanel.keyPressed(keyCode, getFocused())) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (sidePanel.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
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

        int group = menu.tile.activeGroup;
        if (group >= 0) {
            int px = sidePanelX();
            int py = sidePanelY();
            guiGraphics.fill(px, py, px + SIDE_PANEL_WIDTH, py + SIDE_PANEL_HEIGHT, 0xFF000000);
            guiGraphics.fill(px + 1, py + 1, px + SIDE_PANEL_WIDTH - 1, py + SIDE_PANEL_HEIGHT - 1, 0xFF2B2228);
            float highlightV = group == AthanorBlockEntity.GROUP_OUTPUT ? 37f : 19f;
            for (int slotIdx = 0; slotIdx < AthanorBlockEntity.OUTPUT_SLOT + AthanorBlockEntity.NUM_OUTPUTS; slotIdx++) {
                if (AthanorBlockEntity.groupOf(slotIdx) != group) continue;
                Slot slot = menu.getSlot(slotIdx);
                guiGraphics.blit(SLOT_HIGHLIGHT, leftPos + slot.x, topPos + slot.y, 195f, highlightV, 16, 16, 256, 256);
            }
        }
    }
}
