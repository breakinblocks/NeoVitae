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
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.menu.RoutingNodeMenu;
import com.breakinblocks.neovitae.common.network.RoutingNodePayload;
import com.breakinblocks.neovitae.common.network.RoutingNodeSetFluidGhostPayload;
import com.breakinblocks.neovitae.common.network.RoutingNodeSetGhostPayload;
import com.breakinblocks.neovitae.common.routing.FilterMode;

import java.util.ArrayList;
import java.util.List;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class RoutingNodeScreen extends AbstractContainerScreen<RoutingNodeMenu> {
    private static final Identifier BACKGROUND = NeoVitae.rl("textures/gui/routingnode.png");

    private static final String[] DIRECTION_KEYS = {
            "gui.neovitae.routing.direction.down",
            "gui.neovitae.routing.direction.up",
            "gui.neovitae.routing.direction.north",
            "gui.neovitae.routing.direction.south",
            "gui.neovitae.routing.direction.west",
            "gui.neovitae.routing.direction.east"
    };

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
        super(menu, playerInventory, title, 176, 187);
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

        enableButton = Button.builder(Component.translatable("gui.neovitae.routing.disabled"), btn -> {
            ClientPacketDistributor.sendToServer(new RoutingNodePayload(
                    menu.tile.getBlockPos(),
                    RoutingNodePayload.ACTION_TOGGLE_SIDE_ENABLED,
                    0));
        }).bounds(leftPos + 8, topPos + 18, 50, 16).build();
        this.addRenderableWidget(enableButton);

        tabButton = Button.builder(Component.translatable("gui.neovitae.routing.items"), btn -> {
            activeTab = activeTab == Tab.ITEMS ? Tab.FLUIDS : Tab.ITEMS;
        }).bounds(leftPos + 8, topPos + 36, 50, 16).build();
        this.addRenderableWidget(tabButton);

        modeButton = Button.builder(Component.translatable("gui.neovitae.routing.whitelist"), btn -> {
            int action = activeTab == Tab.ITEMS
                    ? RoutingNodePayload.ACTION_TOGGLE_SIDE_ITEM_MODE
                    : RoutingNodePayload.ACTION_TOGGLE_SIDE_FLUID_MODE;
            ClientPacketDistributor.sendToServer(new RoutingNodePayload(
                    menu.tile.getBlockPos(),
                    action,
                    0));
        }).bounds(leftPos + 8, topPos + 54, 50, 16).build();
        this.addRenderableWidget(modeButton);

        priorityDownButton = Button.builder(Component.literal("-"), btn -> {
            menu.decrementPriority();
            ClientPacketDistributor.sendToServer(new RoutingNodePayload(menu.tile.getBlockPos(), RoutingNodePayload.ACTION_DECREMENT_PRIORITY, 0));
        }).bounds(leftPos + 61, topPos + 76, 16, 16).build();
        this.addRenderableWidget(priorityDownButton);

        priorityUpButton = Button.builder(Component.literal("+"), btn -> {
            menu.incrementPriority();
            ClientPacketDistributor.sendToServer(new RoutingNodePayload(menu.tile.getBlockPos(), RoutingNodePayload.ACTION_INCREMENT_PRIORITY, 0));
        }).bounds(leftPos + 99, topPos + 76, 16, 16).build();
        this.addRenderableWidget(priorityUpButton);
    }

    private Button createDirectionButton(int x, int y, int dirIndex, String label) {
        return Button.builder(Component.literal(label), btn -> {
            menu.selectSlot(dirIndex);
            ClientPacketDistributor.sendToServer(new RoutingNodePayload(menu.tile.getBlockPos(), RoutingNodePayload.ACTION_SELECT_SLOT, dirIndex));
        }).bounds(leftPos + x, topPos + y, 16, 16).build();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        int currentSlot = menu.getCurrentSlot();
        for (int i = 0; i < 6; i++) {
            directionButtons[i].active = (i != currentSlot);
        }
        boolean enabled = menu.isSideEnabled(currentSlot);
        enableButton.setMessage(Component.translatable(enabled ? "gui.neovitae.routing.enabled" : "gui.neovitae.routing.disabled")
                .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED));

        tabButton.setMessage(Component.translatable(activeTab == Tab.ITEMS ? "gui.neovitae.routing.items" : "gui.neovitae.routing.fluids"));
        tabButton.active = enabled;

        modeButton.active = enabled;
        if (activeTab == Tab.ITEMS) {
            FilterMode mode = menu.getSideItemMode(currentSlot);
            modeButton.setMessage(Component.translatable(mode == FilterMode.WHITELIST ? "gui.neovitae.routing.whitelist" : "gui.neovitae.routing.blacklist")
                    .withStyle(mode == FilterMode.WHITELIST ? ChatFormatting.GREEN : ChatFormatting.RED));
        } else {
            FilterMode mode = menu.getSideFluidMode(currentSlot);
            String key = switch (mode) {
                case WHITELIST -> "gui.neovitae.routing.whitelist";
                case BLACKLIST -> "gui.neovitae.routing.blacklist";
                case AUTO_MATCH -> "gui.neovitae.routing.auto_match";
            };
            ChatFormatting color = switch (mode) {
                case WHITELIST -> ChatFormatting.GREEN;
                case BLACKLIST -> ChatFormatting.RED;
                case AUTO_MATCH -> ChatFormatting.AQUA;
            };
            modeButton.setMessage(Component.translatable(key).withStyle(color));
        }

        if (activeTab == Tab.FLUIDS) {
            renderFluidGhosts(guiGraphics);
        }
    }

    private void renderFluidGhosts(GuiGraphicsExtractor guiGraphics) {
        for (int i = 0; i < RoutingNodeMenu.GHOST_SLOT_COUNT; i++) {
            Slot slot = menu.slots.get(i);
            int x = leftPos + slot.x;
            int y = topPos + slot.y;
            guiGraphics.fill(x, y, x + 16, y + 16, 0xFF8B8B8B);
            net.neoforged.neoforge.fluids.FluidStack ghost = menu.getCurrentFluidGhost(i);
            if (!ghost.isEmpty()) {
                com.breakinblocks.neovitae.util.helper.RenderHelper.renderGuiFluid(guiGraphics, ghost.getFluid(), x, y, 16, 16);
            }
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF404040);
        guiGraphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF404040);

        int currentSlot = menu.getCurrentSlot();
        if (currentSlot < 0 || currentSlot >= 6) return;

        Component dirName = Component.translatable(DIRECTION_KEYS[currentSlot]);
        guiGraphics.text(font, dirName, 79, 6, 0xFF404040);

        int priority = menu.getCurrentPriority();
        Component priorityStr = Component.translatable("gui.neovitae.routing.priority_short", priority);
        int textWidth = font.width(priorityStr);
        guiGraphics.text(font, priorityStr, 88 - textWidth / 2, 80, 0xFF404040);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractTooltip(guiGraphics, mouseX, mouseY);

        if (activeTab == Tab.FLUIDS && this.hoveredSlot != null
                && this.hoveredSlot.index < RoutingNodeMenu.GHOST_SLOT_COUNT) {
            FluidStack fluid = menu.getCurrentFluidGhost(this.hoveredSlot.index);
            List<Component> tooltip = new ArrayList<>();
            if (fluid.isEmpty()) {
                tooltip.add(Component.translatable("gui.neovitae.routing.slot.empty").withStyle(ChatFormatting.DARK_GRAY));
                tooltip.add(Component.translatable("gui.neovitae.routing.bucket.set").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            } else {
                tooltip.add(fluid.getHoverName());
                tooltip.add(Component.translatable("gui.neovitae.routing.bucket.clear").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }
            guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            return;
        }

        for (int i = 0; i < 6; i++) {
            if (directionButtons[i].isHovered()) {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.translatable("gui.neovitae.routing.face", Component.translatable(DIRECTION_KEYS[i])));

                if (menu.tile != null) {
                    Direction dir = Direction.from3DDataValue(i);
                    String neighborName = menu.tile.getNeighborName(dir);
                    boolean hasInv = menu.tile.hasInventoryNeighbor(dir);

                    if (hasInv) {
                        tooltip.add(Component.translatable("gui.neovitae.routing.inventory_neighbor", neighborName).withStyle(ChatFormatting.GREEN));
                    } else {
                        tooltip.add(Component.translatable("gui.neovitae.routing.block_neighbor", neighborName).withStyle(ChatFormatting.GRAY));
                    }

                    int priority = menu.getPriority(dir);
                    tooltip.add(Component.translatable("gui.neovitae.routing.priority_value", priority).withStyle(ChatFormatting.YELLOW));

                    boolean sideEnabled = menu.isSideEnabled(i);
                    tooltip.add(Component.translatable(sideEnabled ? "gui.neovitae.routing.enabled" : "gui.neovitae.routing.disabled")
                            .withStyle(sideEnabled ? ChatFormatting.GREEN : ChatFormatting.RED));

                    int currentSlot = menu.getCurrentSlot();
                    if (i != currentSlot) {
                        tooltip.add(Component.translatable("gui.neovitae.routing.swap_priority").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
                    }
                }

                guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
                return;
            }
        }

        if (enableButton.isHovered()) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("gui.neovitae.routing.side.toggle"));
            tooltip.add(Component.translatable("gui.neovitae.routing.side.disabled_blocks").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            return;
        }
        if (tabButton.isHovered()) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("gui.neovitae.routing.filter.switch"));
            tooltip.add(Component.translatable("gui.neovitae.routing.filter.items_desc").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("gui.neovitae.routing.filter.fluids_desc").withStyle(ChatFormatting.GRAY));
            guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            return;
        }
        if (modeButton.isHovered()) {
            List<Component> tooltip = new ArrayList<>();
            if (activeTab == Tab.ITEMS) {
                tooltip.add(Component.translatable("gui.neovitae.routing.filter.item_mode"));
                tooltip.add(Component.translatable("gui.neovitae.routing.filter.whitelist_empty").withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("gui.neovitae.routing.filter.blacklist_empty").withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(Component.translatable("gui.neovitae.routing.filter.fluid_mode"));
                tooltip.add(Component.translatable("gui.neovitae.routing.filter.fluid_explicit").withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("gui.neovitae.routing.filter.fluid_automatch").withStyle(ChatFormatting.GRAY));
            }
            guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            return;
        }
        if (priorityUpButton.isHovered()) {
            guiGraphics.setTooltipForNextFrame(this.font, Component.translatable("gui.neovitae.routing.priority.increase"), mouseX, mouseY);
        }
        if (priorityDownButton.isHovered()) {
            guiGraphics.setTooltipForNextFrame(this.font, Component.translatable("gui.neovitae.routing.priority.decrease"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
        int button = event.button();
        // Right-click on a direction button swaps priorities with the current slot.
        if (button == 1) {
            for (int i = 0; i < 6; i++) {
                if (directionButtons[i].isHovered() && i != menu.getCurrentSlot()) {
                    if (menu.tile != null) {
                        menu.tile.swapPriorityWith(i);
                        ClientPacketDistributor.sendToServer(new RoutingNodePayload(
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
                        ClientPacketDistributor.sendToServer(new RoutingNodeSetGhostPayload(
                                menu.tile.getBlockPos(),
                                slotIdx,
                                carried.copyWithCount(1)
                        ));
                        menu.clicked(slotIdx, 0, ContainerInput.PICKUP, this.minecraft.player);
                    }
                    return true;
                } else if (button == 1) {
                    ClientPacketDistributor.sendToServer(new RoutingNodeSetGhostPayload(
                            menu.tile.getBlockPos(),
                            slotIdx,
                            ItemStack.EMPTY
                    ));
                    menu.clicked(slotIdx, 1, ContainerInput.PICKUP, this.minecraft.player);
                    return true;
                }
            } else {
                if (button == 0) {
                    if (!carried.isEmpty()) {
                        FluidStack extracted = FluidUtil.getFluidContained(carried).orElse(FluidStack.EMPTY);
                        if (!extracted.isEmpty()) {
                            FluidStack ghost = extracted.copy();
                            ghost.setAmount(1);
                            ClientPacketDistributor.sendToServer(new RoutingNodeSetFluidGhostPayload(
                                    menu.tile.getBlockPos(),
                                    slotIdx,
                                    ghost
                            ));
                        }
                    }
                    return true;
                } else if (button == 1) {
                    ClientPacketDistributor.sendToServer(new RoutingNodeSetFluidGhostPayload(
                            menu.tile.getBlockPos(),
                            slotIdx,
                            FluidStack.EMPTY
                    ));
                    return true;
                }
            }
        }

        return super.mouseClicked(event, dragging);
    }
}
