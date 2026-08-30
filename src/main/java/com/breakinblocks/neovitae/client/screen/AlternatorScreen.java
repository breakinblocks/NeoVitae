package com.breakinblocks.neovitae.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonAlternatorBlockEntity;
import com.breakinblocks.neovitae.common.menu.AlternatorMenu;
import com.breakinblocks.neovitae.common.network.AlternatorConfigPayload;

public class AlternatorScreen extends AbstractContainerScreen<AlternatorMenu> {

    private static final int PANEL_WIDTH = 176;
    private static final int PANEL_HEIGHT = 122;

    private EditBox delayBox;
    private Button redstoneButton;
    private int lastSyncedDelay = -1;

    public AlternatorScreen(AlternatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, PANEL_WIDTH, PANEL_HEIGHT);
    }

    @Override
    protected void init() {
        super.init();

        delayBox = new EditBox(font, leftPos + 38, topPos + 36, 100, 16,
                Component.translatable("gui.neovitae.alternator.delay"));
        delayBox.setMaxLength(5);
        delayBox.setFilter(text -> text.isEmpty() || text.chars().allMatch(Character::isDigit));
        delayBox.setValue(String.valueOf(menu.getDelay()));
        delayBox.setResponder(this::onDelayChanged);
        addRenderableWidget(delayBox);

        redstoneButton = Button.builder(redstoneLabel(), button ->
                        ClientPacketDistributor.sendToServer(new AlternatorConfigPayload(menu.getPos(), parsedDelay(), !menu.stopsOnRedstone())))
                .bounds(leftPos + 8, topPos + 66, 160, 20).build();
        addRenderableWidget(redstoneButton);
    }

    private Component redstoneLabel() {
        return Component.translatable(menu.stopsOnRedstone()
                ? "gui.neovitae.alternator.redstone.on"
                : "gui.neovitae.alternator.redstone.off");
    }

    private int parsedDelay() {
        try {
            return Math.min(DungeonAlternatorBlockEntity.MAX_DELAY, Integer.parseInt(delayBox.getValue()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void onDelayChanged(String text) {
        int delay = parsedDelay();
        if (delay != menu.getDelay()) {
            ClientPacketDistributor.sendToServer(new AlternatorConfigPayload(menu.getPos(), delay, menu.stopsOnRedstone()));
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (menu.getDelay() != lastSyncedDelay) {
            lastSyncedDelay = menu.getDelay();
            if (!delayBox.isFocused()) {
                delayBox.setValue(String.valueOf(lastSyncedDelay));
            }
        }
        redstoneButton.setMessage(redstoneLabel());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xCC1A0A0A);
        guiGraphics.fill(leftPos + 1, topPos + 1, leftPos + imageWidth - 1, topPos + imageHeight - 1, 0xCC2A1520);

        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + 1, 0xFF6B1A1A);
        guiGraphics.fill(leftPos, topPos + imageHeight - 1, leftPos + imageWidth, topPos + imageHeight, 0xFF6B1A1A);
        guiGraphics.fill(leftPos, topPos, leftPos + 1, topPos + imageHeight, 0xFF6B1A1A);
        guiGraphics.fill(leftPos + imageWidth - 1, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF6B1A1A);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        Component titleComponent = Component.translatable("container.neovitae.alternator");
        guiGraphics.text(font, titleComponent, (imageWidth - font.width(titleComponent)) / 2, 8, 0xFFCCA050);
        guiGraphics.text(font, Component.translatable("gui.neovitae.alternator.delay"),
                8, 26, 0xFFC0C0C0);
        guiGraphics.text(font, Component.translatable("gui.neovitae.alternator.delay.hint"),
                8, 55, 0xFF808080);
        guiGraphics.text(font, Component.translatable("gui.neovitae.alternator.receivers",
                menu.getReceiverCount(), menu.getMaxReceivers()), 8, 94, 0xFFC0C0C0);
        guiGraphics.text(font, Component.translatable("gui.neovitae.alternator.receivers.hint"),
                8, 106, 0xFF808080);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
