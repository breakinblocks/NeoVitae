package com.breakinblocks.neovitae.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonKey;
import com.breakinblocks.neovitae.common.menu.DungeonSealMenu;

import java.util.ArrayList;
import java.util.List;

public class DungeonSealScreen extends AbstractContainerScreen<DungeonSealMenu> {

    private static final int PANEL_WIDTH = 180;
    private static final int BUTTON_WIDTH = 160;
    private static final int BUTTON_HEIGHT = 24;
    private static final int BUTTON_SPACING = 28;
    private static final int TITLE_HEIGHT = 30;
    private static final int PADDING = 10;

    private final List<KeyButton> keyButtons = new ArrayList<>();

    public DungeonSealScreen(DungeonSealMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        int validCount = Integer.bitCount(menu.getValidKeyMask());
        this.imageWidth = PANEL_WIDTH;
        this.imageHeight = TITLE_HEIGHT + validCount * BUTTON_SPACING + PADDING;
    }

    @Override
    protected void init() {
        super.init();
        keyButtons.clear();

        int y = topPos + TITLE_HEIGHT;
        for (int i = 0; i < DungeonSealMenu.KEY_TYPES.size(); i++) {
            if (!menu.isKeyValid(i)) continue;

            final int keyIndex = i;
            ItemDungeonKey keyItem = DungeonSealMenu.KEY_TYPES.get(i).get();
            Button btn = Button.builder(
                    Component.literal(keyItem.getKeyType() + " Key"),
                    button -> {
                        Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, keyIndex);
                    }
            ).pos(leftPos + (PANEL_WIDTH - BUTTON_WIDTH) / 2, y).size(BUTTON_WIDTH, BUTTON_HEIGHT).build();
            btn.active = menu.getKeyCount(i) > 0;
            addRenderableWidget(btn);
            keyButtons.add(new KeyButton(btn, i, new ItemStack(keyItem)));
            y += BUTTON_SPACING;
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        for (KeyButton kb : keyButtons) {
            kb.button.active = menu.getKeyCount(kb.keyIndex) > 0;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xCC1A0A0A);
        guiGraphics.fill(leftPos + 1, topPos + 1, leftPos + imageWidth - 1, topPos + imageHeight - 1, 0xCC2A1520);

        // Border
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + 1, 0xFF6B1A1A);
        guiGraphics.fill(leftPos, topPos + imageHeight - 1, leftPos + imageWidth, topPos + imageHeight, 0xFF6B1A1A);
        guiGraphics.fill(leftPos, topPos, leftPos + 1, topPos + imageHeight, 0xFF6B1A1A);
        guiGraphics.fill(leftPos + imageWidth - 1, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF6B1A1A);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawCenteredString(this.font,
                Component.translatable("container.neovitae.dungeon_seal"),
                imageWidth / 2, 8, 0xCCA05050);

        for (KeyButton kb : keyButtons) {
            int relX = kb.button.getX() - leftPos;
            int relY = kb.button.getY() - topPos;

            // Key icon
            guiGraphics.renderItem(kb.stack, relX + 3, relY + 4);

            // Key count
            int count = menu.getKeyCount(kb.keyIndex);
            String countStr = "x" + count;
            int countColor = count > 0 ? 0xFFFFFF : 0x808080;
            guiGraphics.drawString(this.font, countStr,
                    relX + BUTTON_WIDTH - font.width(countStr) - 6, relY + 8, countColor, false);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record KeyButton(Button button, int keyIndex, ItemStack stack) {}
}
