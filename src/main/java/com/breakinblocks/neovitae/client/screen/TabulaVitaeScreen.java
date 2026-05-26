package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.TabulaVitaeBlockEntity;
import com.breakinblocks.neovitae.common.menu.TabulaVitaeMenu;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import com.breakinblocks.neovitae.common.network.SetSideConfigPayload;
import com.breakinblocks.neovitae.common.sideconfig.SlotSideConfig;
import com.breakinblocks.neovitae.compat.jei.NeoVitaeJEIPlugin;
import com.breakinblocks.neovitae.compat.jei.tabulavitae.TabulaVitaeRecipeCategory;

import java.util.List;

public class TabulaVitaeScreen extends AbstractContainerScreen<TabulaVitaeMenu> {
    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(NeoVitae.MODID, "textures/gui/alchemytable.png");

    public TabulaVitaeScreen(TabulaVitaeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 205);
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
        ClientPacketDistributor.sendToServer(new SetSideConfigPayload(tile.getBlockPos(), slot, direction.get3DDataValue(), newState));
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractTooltip(guiGraphics, mouseX, mouseY);
        if (isOverProgress(mouseX, mouseY)) {
            guiGraphics.setTooltipForNextFrame(this.font, Component.translatable("gui.neovitae.show_recipes").withStyle(ChatFormatting.YELLOW), mouseX, mouseY);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF404040);
        guiGraphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF404040);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, i, j, 0f, 0f, this.imageWidth, this.imageHeight, 256, 256);

        int progress = getCookProgressScaled(90);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, i + 106, j + 14 + 90 - progress, 176f, (float)(90 - progress), 18, progress, 256, 256);

        int activeSlot = menu.tile.activeSlot;
        if (activeSlot != -1) {
            Slot slot = this.getMenu().getSlot(activeSlot);
            float highlightV = (activeSlot == TabulaVitaeBlockEntity.OUTPUT_SLOT) ? 37f : 19f;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, i + slot.x, j + slot.y, 195f, highlightV, 16, 16, 256, 256);

            SlotSideConfig config = menu.tile.getSideConfig();
            for (int buttonId = 0; buttonId < 6; buttonId++) {
                int xOffset = (buttonId % 2) * 18 + 133;
                int yOffset = (buttonId / 2) * 18 + 50;
                float v = config.isAllowed(activeSlot, Direction.from3DDataValue(buttonId)) ? 18f : 0f;
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, i + xOffset, j + yOffset, 212f, v, 18, 18, 256, 256);
            }
        }
    }

    private boolean isOverProgress(double mouseX, double mouseY) {
        int px = leftPos + 106;
        int py = topPos + 14;
        return mouseX >= px && mouseX < px + 18 && mouseY >= py && mouseY < py + 90;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
        if (event.button() == 0 && isOverProgress(event.x(), event.y())) {
            var runtime = NeoVitaeJEIPlugin.jeiRuntime;
            if (runtime != null) {
                runtime.getRecipesGui().showTypes(List.of(TabulaVitaeRecipeCategory.RECIPE_TYPE));
                return true;
            }
        }
        return super.mouseClicked(event, dragging);
    }

    private int getCookProgressScaled(int scale) {
        double progress = menu.tile.getProgressForGui();
        return (int) (progress * scale);
    }
}
