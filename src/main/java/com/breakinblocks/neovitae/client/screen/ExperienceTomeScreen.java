package com.breakinblocks.neovitae.client.screen;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.ExperienceTomeItem;
import com.breakinblocks.neovitae.common.menu.ExperienceTomeMenu;
import com.breakinblocks.neovitae.common.network.ExperienceTomeTransferPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class ExperienceTomeScreen extends AbstractContainerScreen<ExperienceTomeMenu> {

    private static final ResourceLocation BACKGROUND = NeoVitae.rl("textures/gui/experience_tome.png");

    private static final int[] AMOUNTS = {1, 5, 10, 50, 100, -1};
    private static final int BUTTON_W = 26;
    private static final int BUTTON_H = 14;
    private static final int BUTTON_GAP = 2;
    private static final int BUTTON_LEFT = 6;
    private static final int DEPOSIT_Y = 50;
    private static final int WITHDRAW_Y = 80;

    private static final int TEXT = 0xC8B8B8;
    private static final int HEADING = 0xA8323C;

    public ExperienceTomeScreen(ExperienceTomeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 104;
        this.titleLabelX = 8;
        this.titleLabelY = 5;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, HEADING, false);

        ItemStack tome = menu.getTome(minecraft.player);
        int stored = ExperienceTomeItem.getStoredXp(tome);
        Component readout = Component.translatable("gui.neovitae.experience_tome.stored",
                ExperienceTomeItem.getLevelForXp(stored), stored);
        guiGraphics.drawString(this.font, readout,
                (imageWidth - this.font.width(readout)) / 2, 21, TEXT, false);

        Component deposit = Component.translatable("gui.neovitae.experience_tome.deposit");
        Component withdraw = Component.translatable("gui.neovitae.experience_tome.withdraw");
        guiGraphics.drawString(this.font, deposit, BUTTON_LEFT, DEPOSIT_Y - 11, HEADING, false);
        guiGraphics.drawString(this.font, withdraw, BUTTON_LEFT, WITHDRAW_Y - 11, HEADING, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        renderRow(guiGraphics, DEPOSIT_Y, mouseX, mouseY);
        renderRow(guiGraphics, WITHDRAW_Y, mouseX, mouseY);
    }

    private void renderRow(GuiGraphics guiGraphics, int rowY, int mouseX, int mouseY) {
        for (int i = 0; i < AMOUNTS.length; i++) {
            int x = leftPos + buttonX(i);
            int y = topPos + rowY;
            boolean hovered = isOver(mouseX, mouseY, x, y);
            guiGraphics.blit(BACKGROUND, x, y, hovered ? 28 : 0, 120, BUTTON_W, BUTTON_H);

            Component label = AMOUNTS[i] < 0
                    ? Component.translatable("gui.neovitae.experience_tome.all")
                    : Component.literal(Integer.toString(AMOUNTS[i]));
            guiGraphics.drawString(this.font, label,
                    x + (BUTTON_W - this.font.width(label)) / 2, y + 3, hovered ? 0xFFE0E0 : TEXT, false);
        }
    }

    private static int buttonX(int index) {
        return BUTTON_LEFT + index * (BUTTON_W + BUTTON_GAP);
    }

    private static boolean isOver(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x && mouseX < x + BUTTON_W && mouseY >= y && mouseY < y + BUTTON_H;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int row = 0; row < 2; row++) {
                int rowY = topPos + (row == 0 ? DEPOSIT_Y : WITHDRAW_Y);
                for (int i = 0; i < AMOUNTS.length; i++) {
                    if (isOver((int) mouseX, (int) mouseY, leftPos + buttonX(i), rowY)) {
                        PacketDistributor.sendToServer(
                                new ExperienceTomeTransferPayload(row == 0, AMOUNTS[i]));
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
