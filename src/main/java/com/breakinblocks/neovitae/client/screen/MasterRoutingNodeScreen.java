package com.breakinblocks.neovitae.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.menu.MasterRoutingNodeMenu;
import com.breakinblocks.neovitae.common.network.MasterRoutingNodeEnergyRatePayload;

public class MasterRoutingNodeScreen extends AbstractContainerScreen<MasterRoutingNodeMenu> {
    private static final ResourceLocation BACKGROUND = NeoVitae.rl("textures/gui/masterroutingnode.png");

    private static final int ENERGY_BOX_X = 80;
    private static final int ENERGY_BOX_Y = 38;
    private static final int ENERGY_BOX_W = 56;
    private static final int ENERGY_BOX_H = 14;

    private EditBox energyRateBox;
    private int lastSyncedEnergyRate = -1;
    private int pendingEnergyRate = -1;

    public MasterRoutingNodeScreen(MasterRoutingNodeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 146;
        this.titleLabelX = 38;
        this.inventoryLabelY = 52;
    }

    @Override
    protected void init() {
        super.init();

        int initialRate = menu.getEnergyRate();
        if (initialRate <= 0) initialRate = MasterRoutingNodeBlockEntity.ENERGY_RATE_DEFAULT;
        lastSyncedEnergyRate = initialRate;

        energyRateBox = new EditBox(
                this.font,
                leftPos + ENERGY_BOX_X,
                topPos + ENERGY_BOX_Y,
                ENERGY_BOX_W,
                ENERGY_BOX_H,
                Component.literal("Energy Rate")
        );
        energyRateBox.setMaxLength(7);
        energyRateBox.setFilter(s -> s.isEmpty() || s.chars().allMatch(Character::isDigit));
        energyRateBox.setValue(String.valueOf(initialRate));
        this.addRenderableWidget(energyRateBox);
        this.setInitialFocus(energyRateBox);

        this.addRenderableWidget(Button.builder(Component.literal("Set"), b -> commitEnergyRate())
                .bounds(leftPos + ENERGY_BOX_X + ENERGY_BOX_W + 2, topPos + ENERGY_BOX_Y, 26, ENERGY_BOX_H)
                .build());
    }

    private void commitEnergyRate() {
        if (energyRateBox == null) return;
        String value = energyRateBox.getValue();
        if (value.isEmpty()) return;
        int parsed;
        try {
            parsed = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return;
        }
        int clamped = Math.max(MasterRoutingNodeBlockEntity.ENERGY_RATE_MIN,
                Math.min(MasterRoutingNodeBlockEntity.ENERGY_RATE_MAX, parsed));
        energyRateBox.setValue(String.valueOf(clamped));
        energyRateBox.setFocused(false);
        pendingEnergyRate = clamped;
        lastSyncedEnergyRate = clamped;
        PacketDistributor.sendToServer(new MasterRoutingNodeEnergyRatePayload(
                menu.tile.getBlockPos(),
                clamped
        ));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if ((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)
                && energyRateBox != null && energyRateBox.isFocused()) {
            commitEnergyRate();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int serverRate = menu.getEnergyRate();
        if (pendingEnergyRate >= 0) {
            if (serverRate == pendingEnergyRate) pendingEnergyRate = -1;
        } else if (serverRate != lastSyncedEnergyRate && boxShowsSyncedValue()) {
            lastSyncedEnergyRate = serverRate;
            energyRateBox.setValue(String.valueOf(serverRate));
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private boolean boxShowsSyncedValue() {
        if (energyRateBox == null) return false;
        String value = energyRateBox.getValue();
        try {
            return !value.isEmpty() && Integer.parseInt(value) == lastSyncedEnergyRate;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("Energy (FE/t):"), 8, ENERGY_BOX_Y + 3, 0x404040, false);

        int ceiling = menu.getEnergyCeiling();
        int configured = menu.getEnergyRate();
        // Red when the configured throttle exceeds the upgrade-derived ceiling.
        int color = configured > ceiling ? 0xC00000 : 0x808080;
        guiGraphics.drawString(this.font, "Max: " + ceiling + " FE/t", ENERGY_BOX_X, ENERGY_BOX_Y + ENERGY_BOX_H + 1, color, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        boolean slotEmpty = hoveredSlot == null || !hoveredSlot.hasItem();

        if (slotEmpty && isHovering(62, 15, 16, 16, mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, Component.literal("Stack Upgrade Slot"), mouseX, mouseY);
        }

        if (slotEmpty && isHovering(98, 15, 16, 16, mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, Component.literal("Speed Upgrade Slot"), mouseX, mouseY);
        }

        if (isHovering(ENERGY_BOX_X, ENERGY_BOX_Y, ENERGY_BOX_W, ENERGY_BOX_H, mouseX, mouseY)) {
            int ceiling = menu.getEnergyCeiling();
            int configured = menu.getEnergyRate();
            java.util.List<net.minecraft.util.FormattedCharSequence> lines = new java.util.ArrayList<>();
            lines.add(Component.literal("Energy Transfer Rate").getVisualOrderText());
            lines.add(Component.literal("Requested FE/t per pulse (throttle)").getVisualOrderText());
            lines.add(Component.literal("Current upgrade ceiling: " + ceiling + " FE/t").getVisualOrderText());
            if (configured > ceiling) {
                lines.add(Component.literal("Throttle exceeds ceiling; effective rate: " + ceiling).getVisualOrderText());
            }
            lines.add(Component.literal("Install more Stack Upgrades to raise the ceiling.").getVisualOrderText());
            guiGraphics.renderTooltip(font, lines, mouseX, mouseY);
        }
    }
}
