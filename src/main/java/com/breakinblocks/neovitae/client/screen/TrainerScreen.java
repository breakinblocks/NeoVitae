package com.breakinblocks.neovitae.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.widgets.MultiIconButton;
import com.breakinblocks.neovitae.common.menu.TrainerMenu;

public class TrainerScreen extends AbstractGhostScreen<TrainerMenu> {

    public TrainerScreen(TrainerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 187;
    }

    private static final Component allowTooltip = Component.translatable("trainer.neovitae.allow_others");
    private static final Component denyTooltip = Component.translatable("trainer.neovitae.deny_others");
    @Override
    protected void init() {
        super.init();

        addRenderableWidget(Button.builder(Component.literal("<"), button -> sendButtonClick(1))
                .pos(leftPos + 16, topPos + 34)
                .size(8, 20)
                .build()
        );
        addRenderableWidget(Button.builder(Component.literal(">"), button -> sendButtonClick(2))
                .pos(leftPos + 44, topPos + 34)
                .size(8, 20)
                .build()
        );

        addMultiIconButton(1, MultiIconButton.builder(button -> sendButtonClick(3))
                .icons(allow, deny)
                .tooltips(allowTooltip, denyTooltip)
                .pos(leftPos + 24, topPos + 55)
                .size(20, 20)
                .build()
        );

        addRenderableWidget(Button.builder(Component.translatable("trainer.neovitae.save"), button -> sendButtonClick(4))
                .pos(leftPos + 50, topPos + 55)
                .size(30, 20)
                .build()
        );
    }

    private void sendButtonClick(int buttonId) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
    }

    public String getLevelString() {
        return "" + getMenu().getData(3 + this.menu.getLastGhostSlotClicked());
    }

    private static final ResourceLocation allow = ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "container/trainer/allow_others");
    private static final ResourceLocation deny = ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "container/trainer/deny_others");
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        String level = getLevelString();
        if (!level.isEmpty()) {
            int xOff = -3 * level.length();
            guiGraphics.drawString(this.font, Component.literal(level), 34 + xOff, 40, 0xFFFFFF /*4210752*/, false);
        }
    }

    @Override
    public ResourceLocation background() {
        return NeoVitae.rl("textures/gui/container/training_bracelet.png");
    }
}
