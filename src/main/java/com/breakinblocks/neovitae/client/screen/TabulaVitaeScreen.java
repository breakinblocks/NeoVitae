package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.TabulaVitaeBlockEntity;
import com.breakinblocks.neovitae.common.menu.TabulaVitaeMenu;
import com.breakinblocks.neovitae.common.network.NVPayloads;
import com.breakinblocks.neovitae.common.network.SetSideConfigPayload;
import com.breakinblocks.neovitae.common.sideconfig.SlotSideConfig;
import com.breakinblocks.neovitae.compat.jei.NeoVitaeJEIPlugin;
import com.breakinblocks.neovitae.compat.jei.tabulavitae.TabulaVitaeRecipeCategory;

import java.util.List;

public class TabulaVitaeScreen extends AbstractContainerScreen<TabulaVitaeMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "textures/gui/alchemytable.png");

    public TabulaVitaeScreen(TabulaVitaeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 205;
        this.titleLabelX = 38;
        this.inventoryLabelY = 111;
    }

    @Override
    protected void init() {
        super.init();

        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;

        addDirectionalButton(left + 135, top + 52, "D", Direction.DOWN);
        addDirectionalButton(left + 153, top + 52, "U", Direction.UP);
        addDirectionalButton(left + 135, top + 70, "N", Direction.NORTH);
        addDirectionalButton(left + 153, top + 70, "S", Direction.SOUTH);
        addDirectionalButton(left + 135, top + 88, "W", Direction.WEST);
        addDirectionalButton(left + 153, top + 88, "E", Direction.EAST);
    }

    private void addDirectionalButton(int x, int y, String label, Direction direction) {
        this.addRenderableWidget(Button.builder(Component.literal(label), btn -> onDirectionButton(direction))
                .bounds(x, y, 14, 14)
                .build());
    }

    private void onDirectionButton(Direction direction) {
        TabulaVitaeBlockEntity tile = menu.tile;
        int slot = tile.activeSlot;
        if (slot < 0) return;
        SlotSideConfig config = tile.getSideConfig();
        boolean newState = !config.isAllowed(slot, direction);
        config.setAllowed(slot, direction, newState);
        NVPayloads.sendToServer(new SetSideConfigPayload(tile.getBlockPos(), slot, direction.get3DDataValue(), newState));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        if (isOverProgress(mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, Component.literal("Show Recipes").withStyle(ChatFormatting.YELLOW), mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);

        int progress = getCookProgressScaled(90);
        guiGraphics.blit(BACKGROUND, i + 106, j + 14 + 90 - progress, 176, 90 - progress, 18, progress);

        int activeSlot = menu.tile.activeSlot;
        if (activeSlot != -1) {
            Slot slot = this.getMenu().getSlot(activeSlot);
            int highlightV = (activeSlot == TabulaVitaeBlockEntity.OUTPUT_SLOT) ? 37 : 19;
            guiGraphics.blit(BACKGROUND, i + slot.x, j + slot.y, 195, highlightV, 16, 16);

            SlotSideConfig config = menu.tile.getSideConfig();
            for (int buttonId = 0; buttonId < 6; buttonId++) {
                int xOffset = (buttonId % 2) * 18 + 133;
                int yOffset = (buttonId / 2) * 18 + 50;
                int v = config.isAllowed(activeSlot, Direction.from3DDataValue(buttonId)) ? 18 : 0;
                guiGraphics.blit(BACKGROUND, i + xOffset, j + yOffset, 212, v, 18, 18);
            }
        }
    }

    private boolean isOverProgress(double mouseX, double mouseY) {
        int px = leftPos + 106;
        int py = topPos + 14;
        return mouseX >= px && mouseX < px + 18 && mouseY >= py && mouseY < py + 90;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isOverProgress(mouseX, mouseY)) {
            var runtime = NeoVitaeJEIPlugin.jeiRuntime;
            if (runtime != null) {
                runtime.getRecipesGui().showTypes(List.of(TabulaVitaeRecipeCategory.RECIPE_TYPE));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private int getCookProgressScaled(int scale) {
        double progress = menu.tile.getProgressForGui();
        return (int) (progress * scale);
    }
}
