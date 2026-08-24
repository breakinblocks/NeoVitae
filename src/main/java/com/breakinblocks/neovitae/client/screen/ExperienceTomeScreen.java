package com.breakinblocks.neovitae.client.screen;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.ExperienceTomeItem;
import com.breakinblocks.neovitae.common.menu.ExperienceTomeMenu;
import com.breakinblocks.neovitae.common.network.ExperienceTomeTransferPayload;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class ExperienceTomeScreen extends AbstractContainerScreen<ExperienceTomeMenu> {

    private static final Identifier BACKGROUND = NeoVitae.rl("textures/gui/experience_tome.png");

    private static final int[] AMOUNTS = {1, 5, 10, 50, 100, -1};
    private static final int BUTTON_W = 26;
    private static final int BUTTON_H = 14;
    private static final int BUTTON_GAP = 2;
    private static final int PANEL_W = 240;
    private static final int PANEL_H = 104;
    private static final int ROW_W = AMOUNTS.length * BUTTON_W + (AMOUNTS.length - 1) * BUTTON_GAP;
    private static final int BUTTON_LEFT = (PANEL_W - ROW_W) / 2;
    private static final int DEPOSIT_Y = 50;
    private static final int WITHDRAW_Y = 80;
    private static final int READOUT_Y = 21;
    private static final int READOUT_INSET = 20;

    private static final int TEXT = 0xFFC8B8B8;
    private static final int HEADING = 0xFFA8323C;
    private static final int TEXT_HOVER = 0xFFFFE0E0;

    public ExperienceTomeScreen(ExperienceTomeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, PANEL_W, PANEL_H);
        this.titleLabelX = 8;
        this.titleLabelY = 5;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256);
        renderRow(guiGraphics, DEPOSIT_Y, mouseX, mouseY);
        renderRow(guiGraphics, WITHDRAW_Y, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, HEADING, false);

        ItemStack tome = menu.getTome(minecraft.player);
        int stored = ExperienceTomeItem.getStoredXp(tome);
        Component readout = Component.translatable("gui.neovitae.experience_tome.stored",
                ExperienceTomeItem.getLevelForXp(stored), stored);
        int readoutWidth = this.font.width(readout);
        int available = imageWidth - READOUT_INSET * 2;
        if (readoutWidth <= available) {
            guiGraphics.text(this.font, readout,
                    (imageWidth - readoutWidth) / 2, READOUT_Y, TEXT, false);
        } else {
            float scale = (float) available / readoutWidth;
            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(READOUT_INSET,
                    READOUT_Y + (this.font.lineHeight * (1 - scale)) / 2f);
            guiGraphics.pose().scale(scale, scale);
            guiGraphics.text(this.font, readout, 0, 0, TEXT, false);
            guiGraphics.pose().popMatrix();
        }

        guiGraphics.text(this.font, Component.translatable("gui.neovitae.experience_tome.deposit"),
                BUTTON_LEFT, DEPOSIT_Y - 11, HEADING, false);
        guiGraphics.text(this.font, Component.translatable("gui.neovitae.experience_tome.withdraw"),
                BUTTON_LEFT, WITHDRAW_Y - 11, HEADING, false);
    }

    private void renderRow(GuiGraphicsExtractor guiGraphics, int rowY, int mouseX, int mouseY) {
        for (int i = 0; i < AMOUNTS.length; i++) {
            int x = leftPos + buttonX(i);
            int y = topPos + rowY;
            boolean hovered = isOver(mouseX, mouseY, x, y);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y,
                    hovered ? 28f : 0f, 120f, BUTTON_W, BUTTON_H, 256, 256);

            Component label = AMOUNTS[i] < 0
                    ? Component.translatable("gui.neovitae.experience_tome.all")
                    : Component.literal(Integer.toString(AMOUNTS[i]));
            guiGraphics.text(this.font, label,
                    x + (BUTTON_W - this.font.width(label)) / 2, y + 3, hovered ? TEXT_HOVER : TEXT, false);
        }
    }

    private static int buttonX(int index) {
        return BUTTON_LEFT + index * (BUTTON_W + BUTTON_GAP);
    }

    private static boolean isOver(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x && mouseX < x + BUTTON_W && mouseY >= y && mouseY < y + BUTTON_H;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
        if (event.button() == 0) {
            for (int row = 0; row < 2; row++) {
                int rowY = topPos + (row == 0 ? DEPOSIT_Y : WITHDRAW_Y);
                for (int i = 0; i < AMOUNTS.length; i++) {
                    if (isOver((int) event.x(), (int) event.y(), leftPos + buttonX(i), rowY)) {
                        ClientPacketDistributor.sendToServer(
                                new ExperienceTomeTransferPayload(row == 0, AMOUNTS[i]));
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(event, dragging);
    }
}
