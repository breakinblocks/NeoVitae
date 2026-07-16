package com.breakinblocks.neovitae.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.menu.MasterRoutingNodeMenu;
import com.breakinblocks.neovitae.common.network.MasterRoutingNodeEnergyRatePayload;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.lwjgl.glfw.GLFW;

public class MasterRoutingNodeScreen extends AbstractContainerScreen<MasterRoutingNodeMenu> {
    private static final Identifier BACKGROUND = NeoVitae.rl("textures/gui/masterroutingnode.png");

    private static final int ENERGY_BOX_X = 80;
    private static final int ENERGY_BOX_Y = 38;
    private static final int ENERGY_BOX_W = 56;
    private static final int ENERGY_BOX_H = 14;

    private EditBox energyRateBox;
    private int lastSyncedEnergyRate = -1;
    private int pendingEnergyRate = -1;

    public MasterRoutingNodeScreen(MasterRoutingNodeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 146);
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
                Component.translatable("gui.neovitae.master_routing.energy_rate")
        );
        energyRateBox.setMaxLength(7);
        energyRateBox.setFilter(s -> s.isEmpty() || s.chars().allMatch(Character::isDigit));
        energyRateBox.setValue(String.valueOf(initialRate));
        this.addRenderableWidget(energyRateBox);
        this.setInitialFocus(energyRateBox);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.neovitae.master_routing.set"), b -> commitEnergyRate())
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
        ClientPacketDistributor.sendToServer(new MasterRoutingNodeEnergyRatePayload(
                menu.tile.getBlockPos(),
                clamped
        ));
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if ((event.key() == GLFW.GLFW_KEY_ENTER || event.key() == GLFW.GLFW_KEY_KP_ENTER)
                && energyRateBox != null && energyRateBox.isFocused()) {
            commitEnergyRate();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        int serverRate = menu.getEnergyRate();
        if (pendingEnergyRate >= 0) {
            if (serverRate == pendingEnergyRate) pendingEnergyRate = -1;
        } else if (serverRate != lastSyncedEnergyRate && energyRateBox != null && !energyRateBox.isFocused()) {
            lastSyncedEnergyRate = serverRate;
            energyRateBox.setValue(String.valueOf(serverRate));
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF404040);
        guiGraphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF404040);
        guiGraphics.text(this.font, Component.translatable("gui.neovitae.master_routing.energy_rate_label"), 8, ENERGY_BOX_Y + 3, 0xFF404040);

        int ceiling = menu.getEnergyCeiling();
        int configured = menu.getEnergyRate();
        int color = configured > ceiling ? 0xFFC00000 : 0xFF808080;
        guiGraphics.text(this.font, "Max: " + ceiling + " FE/t", ENERGY_BOX_X, ENERGY_BOX_Y + ENERGY_BOX_H + 1, color);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractTooltip(guiGraphics, mouseX, mouseY);

        if (isHovering(62, 15, 16, 16, mouseX, mouseY)) {
            guiGraphics.setTooltipForNextFrame(this.font, Component.translatable("gui.neovitae.master_routing.stack_upgrade_slot"), mouseX, mouseY);
        }

        if (isHovering(98, 15, 16, 16, mouseX, mouseY)) {
            guiGraphics.setTooltipForNextFrame(this.font, Component.translatable("gui.neovitae.master_routing.speed_upgrade_slot"), mouseX, mouseY);
        }

        if (isHovering(ENERGY_BOX_X, ENERGY_BOX_Y, ENERGY_BOX_W, ENERGY_BOX_H, mouseX, mouseY)) {
            int ceiling = menu.getEnergyCeiling();
            int configured = menu.getEnergyRate();
            List<Component> lines = new ArrayList<>();
            lines.add(Component.translatable("gui.neovitae.master_routing.transfer_rate.title"));
            lines.add(Component.translatable("gui.neovitae.master_routing.transfer_rate.desc"));
            lines.add(Component.translatable("gui.neovitae.routing.master.upgrade_ceiling", ceiling));
            if (configured > ceiling) {
                lines.add(Component.translatable("gui.neovitae.routing.master.throttle_exceeds", ceiling));
            }
            lines.add(Component.translatable("gui.neovitae.master_routing.transfer_rate.install_more"));
            guiGraphics.setComponentTooltipForNextFrame(this.font, lines, mouseX, mouseY);
        }
    }
}
