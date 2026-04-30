package com.breakinblocks.neovitae.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.network.PacketDistributor;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.menu.RoutingNodeMenu;
import com.breakinblocks.neovitae.common.network.RoutingNodePayload;
import com.breakinblocks.neovitae.common.network.RoutingNodeSetFluidGhostPayload;
import com.breakinblocks.neovitae.common.network.RoutingNodeSetGhostPayload;
import com.breakinblocks.neovitae.common.routing.FilterMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoutingNodeScreen extends AbstractContainerScreen<RoutingNodeMenu> {
    private static final ResourceLocation BACKGROUND = NeoVitae.rl("textures/gui/routingnode.png");

    private static final String[] DIRECTION_NAMES = {"Down", "Up", "North", "South", "West", "East"};

    /** Client-side UI tab - server never needs to know which tab the player is viewing. */
    private enum Tab { ITEMS, FLUIDS }

    private Tab activeTab = Tab.ITEMS;

    private Button[] directionButtons = new Button[6];
    private Button priorityUpButton;
    private Button priorityDownButton;
    private Button enableButton;
    private Button modeButton;
    private Button tabButton;

    public RoutingNodeScreen(RoutingNodeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 187;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        directionButtons[Direction.UP.get3DDataValue()]    = createDirectionButton(135, 11, Direction.UP.get3DDataValue(),    "U");
        directionButtons[Direction.DOWN.get3DDataValue()]  = createDirectionButton(155, 51, Direction.DOWN.get3DDataValue(),  "D");
        directionButtons[Direction.NORTH.get3DDataValue()] = createDirectionButton(135, 31, Direction.NORTH.get3DDataValue(), "N");
        directionButtons[Direction.SOUTH.get3DDataValue()] = createDirectionButton(135, 51, Direction.SOUTH.get3DDataValue(), "S");
        directionButtons[Direction.EAST.get3DDataValue()]  = createDirectionButton(155, 31, Direction.EAST.get3DDataValue(),  "E");
        directionButtons[Direction.WEST.get3DDataValue()]  = createDirectionButton(115, 31, Direction.WEST.get3DDataValue(),  "W");

        for (Button btn : directionButtons) {
            this.addRenderableWidget(btn);
        }

        enableButton = Button.builder(Component.literal("Disabled"), btn -> {
            PacketDistributor.sendToServer(new RoutingNodePayload(
                    menu.tile.getBlockPos(),
                    RoutingNodePayload.ACTION_TOGGLE_SIDE_ENABLED,
                    0));
        }).bounds(leftPos + 8, topPos + 18, 50, 16).build();
        this.addRenderableWidget(enableButton);

        tabButton = Button.builder(Component.literal("Items"), btn -> {
            activeTab = activeTab == Tab.ITEMS ? Tab.FLUIDS : Tab.ITEMS;
        }).bounds(leftPos + 8, topPos + 36, 50, 16).build();
        this.addRenderableWidget(tabButton);

        modeButton = Button.builder(Component.literal("Whitelist"), btn -> {
            int action = activeTab == Tab.ITEMS
                    ? RoutingNodePayload.ACTION_TOGGLE_SIDE_ITEM_MODE
                    : RoutingNodePayload.ACTION_TOGGLE_SIDE_FLUID_MODE;
            PacketDistributor.sendToServer(new RoutingNodePayload(
                    menu.tile.getBlockPos(),
                    action,
                    0));
        }).bounds(leftPos + 8, topPos + 54, 50, 16).build();
        this.addRenderableWidget(modeButton);

        priorityDownButton = Button.builder(Component.literal("-"), btn -> {
            menu.decrementPriority();
            PacketDistributor.sendToServer(new RoutingNodePayload(menu.tile.getBlockPos(), RoutingNodePayload.ACTION_DECREMENT_PRIORITY, 0));
        }).bounds(leftPos + 61, topPos + 76, 16, 16).build();
        this.addRenderableWidget(priorityDownButton);

        priorityUpButton = Button.builder(Component.literal("+"), btn -> {
            menu.incrementPriority();
            PacketDistributor.sendToServer(new RoutingNodePayload(menu.tile.getBlockPos(), RoutingNodePayload.ACTION_INCREMENT_PRIORITY, 0));
        }).bounds(leftPos + 99, topPos + 76, 16, 16).build();
        this.addRenderableWidget(priorityUpButton);
    }

    private Button createDirectionButton(int x, int y, int dirIndex, String label) {
        return Button.builder(Component.literal(label), btn -> {
            menu.selectSlot(dirIndex);
            PacketDistributor.sendToServer(new RoutingNodePayload(menu.tile.getBlockPos(), RoutingNodePayload.ACTION_SELECT_SLOT, dirIndex));
        }).bounds(leftPos + x, topPos + y, 16, 16).build();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Direction button active state
        int currentSlot = menu.getCurrentSlot();
        for (int i = 0; i < 6; i++) {
            directionButtons[i].active = (i != currentSlot);
        }
        boolean enabled = menu.isSideEnabled(currentSlot);
        enableButton.setMessage(Component.literal(enabled ? "Enabled" : "Disabled")
                .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED));

        tabButton.setMessage(Component.literal(activeTab == Tab.ITEMS ? "Items" : "Fluids"));
        tabButton.active = enabled;

        modeButton.active = enabled;
        if (activeTab == Tab.ITEMS) {
            FilterMode mode = menu.getSideItemMode(currentSlot);
            modeButton.setMessage(Component.literal(mode == FilterMode.WHITELIST ? "Whitelist" : "Blacklist")
                    .withStyle(mode == FilterMode.WHITELIST ? ChatFormatting.GREEN : ChatFormatting.RED));
        } else {
            FilterMode mode = menu.getSideFluidMode(currentSlot);
            String label = switch (mode) {
                case WHITELIST -> "Whitelist";
                case BLACKLIST -> "Blacklist";
                case AUTO_MATCH -> "Auto-Match";
            };
            ChatFormatting color = switch (mode) {
                case WHITELIST -> ChatFormatting.GREEN;
                case BLACKLIST -> ChatFormatting.RED;
                case AUTO_MATCH -> ChatFormatting.AQUA;
            };
            modeButton.setMessage(Component.literal(label).withStyle(color));
        }

        if (activeTab == Tab.FLUIDS) {
            renderFluidGhosts(guiGraphics);
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    /** Overwrites the item-ghost cells (already drawn by super.render) with fluid sprites. */
    private void renderFluidGhosts(GuiGraphics guiGraphics) {
        for (int i = 0; i < RoutingNodeMenu.GHOST_SLOT_COUNT; i++) {
            Slot slot = menu.slots.get(i);
            guiGraphics.fill(leftPos + slot.x, topPos + slot.y, leftPos + slot.x + 16, topPos + slot.y + 16, 0xFF8B8B8B);

            FluidStack fluid = menu.getCurrentFluidGhost(i);
            if (fluid.isEmpty()) continue;

            drawFluidSprite(guiGraphics, leftPos + slot.x, topPos + slot.y, fluid);
        }
    }

    private void drawFluidSprite(GuiGraphics guiGraphics, int x, int y, FluidStack fluid) {
        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation stillTex = ext.getStillTexture(fluid);
        TextureAtlasSprite sprite = this.minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTex);
        int tint = ext.getTintColor(fluid);
        float r = ((tint >> 16) & 0xFF) / 255.0F;
        float g = ((tint >> 8) & 0xFF) / 255.0F;
        float b = (tint & 0xFF) / 255.0F;
        float a = ((tint >> 24) & 0xFF) / 255.0F;
        if (a == 0.0F) a = 1.0F;

        RenderSystem.setShaderColor(r, g, b, a);
        guiGraphics.blit(x, y, 0, 16, 16, sprite);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);

        int currentSlot = menu.getCurrentSlot();
        if (currentSlot < 0 || currentSlot >= 6) return;

        int priority = menu.getCurrentPriority();
        String priorityStr = "P: " + priority;
        int textWidth = font.width(priorityStr);
        guiGraphics.drawString(font, priorityStr, 88 - textWidth / 2, 80, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if (activeTab == Tab.FLUIDS && this.hoveredSlot != null
                && this.hoveredSlot.index < RoutingNodeMenu.GHOST_SLOT_COUNT) {
            FluidStack fluid = menu.getCurrentFluidGhost(this.hoveredSlot.index);
            List<Component> tooltip = new ArrayList<>();
            if (fluid.isEmpty()) {
                tooltip.add(Component.literal("Empty").withStyle(ChatFormatting.DARK_GRAY));
                tooltip.add(Component.literal("Left-click with a bucket to set").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            } else {
                tooltip.add(fluid.getHoverName());
                tooltip.add(Component.literal("Right-click to clear").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }
            guiGraphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
            return;
        }

        for (int i = 0; i < 6; i++) {
            if (directionButtons[i].isHovered()) {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.literal(DIRECTION_NAMES[i] + " Face"));

                if (menu.tile != null) {
                    Direction dir = Direction.from3DDataValue(i);
                    String neighborName = menu.tile.getNeighborName(dir);
                    boolean hasInv = menu.tile.hasInventoryNeighbor(dir);

                    if (hasInv) {
                        tooltip.add(Component.literal("Inventory: " + neighborName).withStyle(ChatFormatting.GREEN));
                    } else {
                        tooltip.add(Component.literal("Block: " + neighborName).withStyle(ChatFormatting.GRAY));
                    }

                    int priority = menu.getPriority(dir);
                    tooltip.add(Component.literal("Priority: " + priority).withStyle(ChatFormatting.YELLOW));

                    boolean sideEnabled = menu.isSideEnabled(i);
                    tooltip.add(Component.literal(sideEnabled ? "Enabled" : "Disabled")
                            .withStyle(sideEnabled ? ChatFormatting.GREEN : ChatFormatting.RED));

                    int currentSlot = menu.getCurrentSlot();
                    if (i != currentSlot) {
                        tooltip.add(Component.literal("Right-click to swap priority").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
                    }
                }

                guiGraphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
                return;
            }
        }

        if (enableButton.isHovered()) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("Toggle routing for this side"));
            tooltip.add(Component.literal("Disabled sides block all transfer").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            guiGraphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
            return;
        }
        if (tabButton.isHovered()) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("Switch filter target"));
            tooltip.add(Component.literal("Items: per-item whitelist/blacklist").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("Fluids: whitelist/blacklist/auto-match").withStyle(ChatFormatting.GRAY));
            guiGraphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
            return;
        }
        if (modeButton.isHovered()) {
            List<Component> tooltip = new ArrayList<>();
            if (activeTab == Tab.ITEMS) {
                tooltip.add(Component.literal("Item filter mode"));
                tooltip.add(Component.literal("Whitelist + empty = nothing passes").withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.literal("Blacklist + empty = everything passes").withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(Component.literal("Fluid filter mode"));
                tooltip.add(Component.literal("Whitelist / Blacklist: explicit control").withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.literal("Auto-Match: mirrors the neighbor tank").withStyle(ChatFormatting.GRAY));
            }
            guiGraphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
            return;
        }
        if (priorityUpButton.isHovered()) {
            guiGraphics.renderTooltip(font, Component.literal("Increase Priority"), mouseX, mouseY);
        }
        if (priorityDownButton.isHovered()) {
            guiGraphics.renderTooltip(font, Component.literal("Decrease Priority"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Right-click on a direction button swaps priorities with the current slot.
        if (button == 1) {
            for (int i = 0; i < 6; i++) {
                if (directionButtons[i].isHovered() && i != menu.getCurrentSlot()) {
                    if (menu.tile != null) {
                        menu.tile.swapPriorityWith(i);
                        PacketDistributor.sendToServer(new RoutingNodePayload(
                                menu.tile.getBlockPos(),
                                RoutingNodePayload.ACTION_SWAP_PRIORITY,
                                i
                        ));
                    }
                    return true;
                }
            }
        }

        if (this.hoveredSlot != null && this.hoveredSlot.index < RoutingNodeMenu.GHOST_SLOT_COUNT) {
            int slotIdx = this.hoveredSlot.index;
            ItemStack carried = menu.getCarried();

            if (activeTab == Tab.ITEMS) {
                if (button == 0) {
                    if (!carried.isEmpty()) {
                        PacketDistributor.sendToServer(new RoutingNodeSetGhostPayload(
                                menu.tile.getBlockPos(),
                                slotIdx,
                                carried.copyWithCount(1)
                        ));
                        menu.clicked(slotIdx, 0, ClickType.PICKUP, this.minecraft.player);
                    }
                    return true;
                } else if (button == 1) {
                    PacketDistributor.sendToServer(new RoutingNodeSetGhostPayload(
                            menu.tile.getBlockPos(),
                            slotIdx,
                            ItemStack.EMPTY
                    ));
                    menu.clicked(slotIdx, 1, ClickType.PICKUP, this.minecraft.player);
                    return true;
                }
            } else {
                if (button == 0) {
                    if (!carried.isEmpty()) {
                        FluidStack extracted = FluidUtil.getFluidContained(carried).orElse(FluidStack.EMPTY);
                        if (!extracted.isEmpty()) {
                            FluidStack ghost = extracted.copy();
                            ghost.setAmount(1);
                            PacketDistributor.sendToServer(new RoutingNodeSetFluidGhostPayload(
                                    menu.tile.getBlockPos(),
                                    slotIdx,
                                    ghost
                            ));
                        }
                    }
                    return true;
                } else if (button == 1) {
                    PacketDistributor.sendToServer(new RoutingNodeSetFluidGhostPayload(
                            menu.tile.getBlockPos(),
                            slotIdx,
                            FluidStack.EMPTY
                    ));
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
