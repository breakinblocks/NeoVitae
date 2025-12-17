package com.breakinblocks.neovitae.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.routing.ItemRouterFilter;
import com.breakinblocks.neovitae.common.item.routing.ItemTagFilter;
import com.breakinblocks.neovitae.common.menu.FilterMenu;
import com.breakinblocks.neovitae.common.network.FilterGhostSlotPayload;
import com.breakinblocks.neovitae.util.GhostItemHelper;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Screen for configuring item routing filters.
 * Based on the 1.20.1 ScreenFilter implementation.
 */
public class FilterScreen extends AbstractContainerScreen<FilterMenu> {
    private static final ResourceLocation BACKGROUND = NeoVitae.rl("textures/gui/routingfilter.png");

    private EditBox textBox;
    private int selectedSlot = -1;

    public FilterScreen(FilterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 187;
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = this.imageHeight - 94;

        // Text box for entering ghost item amounts
        this.textBox = new EditBox(Minecraft.getInstance().font, leftPos + 23, topPos + 19, 70, 12, Component.literal(""));
        this.textBox.setBordered(false);
        this.textBox.setMaxLength(50);
        this.textBox.setVisible(true);
        this.textBox.setTextColor(0xFFFFFF);
        this.textBox.setValue("");

        // Blacklist/Whitelist toggle button
        addRenderableWidget(Button.builder(Component.literal(""), button -> {
            Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, ItemRouterFilter.BUTTON_BWLIST);
        }).pos(leftPos + 7, topPos + 32).size(20, 20).build());

        // Tag button (only for tag filters)
        if (menu.isTag) {
            addRenderableWidget(Button.builder(Component.literal(""), button -> {
                if (selectedSlot >= 0) {
                    // Cycle to next tag via server
                    Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, ItemRouterFilter.BUTTON_TAG);
                }
            }).pos(leftPos + 27, topPos + 32).size(20, 20).build());
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        // EditBox no longer has tick() in 1.21.1
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.textBox.isFocused()) {
            if ((keyCode == 259 || keyCode == 261) && selectedSlot != -1) { // Backspace or Delete
                String str = this.textBox.getValue();
                if (str != null && str.length() > 0) {
                    str = str.substring(0, str.length() - 1);
                    this.textBox.setValue(str);
                    int amount = 0;
                    if (str.length() > 0) {
                        try {
                            amount = Integer.parseInt(str);
                        } catch (NumberFormatException e) {
                            // Ignore
                        }
                    }
                    setValueOfGhostItemInSlot(selectedSlot, amount);
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void setValueOfGhostItemInSlot(int slot, int amount) {
        ItemStack stack = menu.getSlot(slot).getItem();
        if (!stack.isEmpty()) {
            GhostItemHelper.setItemGhostAmount(stack, amount);
            PacketDistributor.sendToServer(new FilterGhostSlotPayload(slot, stack));
        }
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        try {
            Integer.parseInt(String.valueOf(typedChar));
            if (this.textBox.charTyped(typedChar, keyCode)) {
                if (selectedSlot != -1) {
                    String str = this.textBox.getValue();
                    int amount = 0;
                    if (!str.isEmpty()) {
                        try {
                            amount = Integer.parseInt(str);
                        } catch (NumberFormatException e) {
                            // Ignore
                        }
                    }
                    setValueOfGhostItemInSlot(selectedSlot, amount);
                }
                return true;
            }
        } catch (NumberFormatException e) {
            // Not a number, ignore
        }
        return super.charTyped(typedChar, keyCode);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean result = super.mouseClicked(mouseX, mouseY, mouseButton);

        // Handle text box focus
        if (selectedSlot != -1) {
            if (this.textBox.mouseClicked(mouseX, mouseY, mouseButton)) {
                this.textBox.setFocused(true);
                return true;
            }
            if (this.textBox.isMouseOver(mouseX, mouseY) && mouseButton == 1) { // Right click clears
                this.textBox.setValue("");
                setValueOfGhostItemInSlot(selectedSlot, 0);
                this.textBox.setFocused(true);
                return true;
            }
        }
        this.textBox.setFocused(false);

        // Check if clicked on a ghost slot
        for (int i = 0; i < ItemRouterFilter.INVENTORY_SIZE; i++) {
            Slot slot = menu.getSlot(i);
            if (isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
                selectedSlot = i;
                ItemStack stack = slot.getItem();
                if (!stack.isEmpty()) {
                    int amount = GhostItemHelper.getItemGhostAmount(stack);
                    this.textBox.setValue(amount == 0 ? "" : String.valueOf(amount));
                } else {
                    this.textBox.setValue("");
                }
                break;
            }
        }

        return result;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.textBox.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        // Render button tooltips
        List<Component> tooltip = new ArrayList<>();
        if (mouseY >= topPos + 32 && mouseY < topPos + 52) {
            if (mouseX >= leftPos + 7 && mouseX < leftPos + 27) {
                int bwState = menu.getData(ItemRouterFilter.DATA_BWLIST);
                tooltip.add(Component.translatable(bwState == 0 ? "filter.neovitae.whitelist" : "filter.neovitae.blacklist"));
            }
            if (menu.isTag && mouseX >= leftPos + 27 && mouseX < leftPos + 47) {
                tooltip.addAll(getTagText());
            }
        }

        if (!tooltip.isEmpty()) {
            guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    private List<Component> getTagText() {
        List<Component> componentList = new ArrayList<>();
        if (selectedSlot == -1) {
            componentList.add(Component.translatable("filter.neovitae.novalidtag"));
            return componentList;
        }

        ItemStack ghostStack = menu.getSlot(selectedSlot).getItem();
        if (ghostStack.isEmpty()) {
            componentList.add(Component.translatable("filter.neovitae.novalidtag"));
            return componentList;
        }

        int buttonState = menu.getData(ItemRouterFilter.DATA_TAG + selectedSlot);
        if (buttonState == 0) {
            Stream<TagKey<Item>> stream = ghostStack.getTags();
            List<ResourceLocation> locations = new ArrayList<>();
            stream.forEach(a -> locations.add(a.location()));

            if (!locations.isEmpty()) {
                componentList.add(Component.translatable("filter.neovitae.anytag"));
                for (ResourceLocation rl : locations) {
                    componentList.add(Component.literal(rl.toString()));
                }
            } else {
                componentList.add(Component.translatable("filter.neovitae.novalidtag"));
            }
        } else {
            if (menu.filterStack.getItem() instanceof ItemTagFilter tagFilter) {
                ResourceLocation rl = tagFilter.getItemTagResource(menu.filterStack, selectedSlot);
                if (rl != null) {
                    componentList.add(Component.translatable("filter.neovitae.specifiedtag"));
                    componentList.add(Component.literal(rl.toString()));
                }
            }
        }

        return componentList;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);

        // Draw button overlays from texture
        int bwState = menu.getData(ItemRouterFilter.DATA_BWLIST);
        guiGraphics.blit(BACKGROUND, 7, 32, 176, bwState == 0 ? 0 : 20, 20, 20);

        if (menu.isTag && selectedSlot >= 0) {
            int tagState = menu.getData(ItemRouterFilter.DATA_TAG + selectedSlot);
            guiGraphics.blit(BACKGROUND, 27, 32, 196, tagState == 0 ? 20 : 0, 20, 20);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Highlight selected ghost slot
        if (selectedSlot >= 0) {
            int x = 110 + (selectedSlot % 3) * 21;
            int y = 15 + (selectedSlot / 3) * 21;
            guiGraphics.blit(BACKGROUND, leftPos + x - 4, topPos + y - 4, 0, 187, 24, 24);
        }
    }
}
