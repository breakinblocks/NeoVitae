package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
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
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.menu.RoutingNodeMenu;
import com.breakinblocks.neovitae.common.network.RoutingNodePayload;
import com.breakinblocks.neovitae.common.network.RoutingNodeSetAmountPayload;
import com.breakinblocks.neovitae.common.network.RoutingNodeSetFluidGhostPayload;
import com.breakinblocks.neovitae.common.network.RoutingNodeSetGhostPayload;
import com.breakinblocks.neovitae.common.routing.FilterMode;
import com.breakinblocks.neovitae.util.helper.KeyboardHelper;
import com.breakinblocks.neovitae.util.helper.RenderHelper;

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
    private Button pagePrevButton;
    private Button pageNextButton;

    public RoutingNodeScreen(RoutingNodeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, RoutingNodeMenu.IMAGE_WIDTH, RoutingNodeMenu.IMAGE_HEIGHT);
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelY = RoutingNodeMenu.INVENTORY_Y - 11;
    }

    @Override
    protected void init() {
        super.init();

        directionButtons[Direction.UP.get3DDataValue()]    = createDirectionButton(135, 18, Direction.UP.get3DDataValue(),    "U");
        directionButtons[Direction.NORTH.get3DDataValue()] = createDirectionButton(135, 38, Direction.NORTH.get3DDataValue(), "N");
        directionButtons[Direction.WEST.get3DDataValue()]  = createDirectionButton(115, 38, Direction.WEST.get3DDataValue(),  "W");
        directionButtons[Direction.EAST.get3DDataValue()]  = createDirectionButton(155, 38, Direction.EAST.get3DDataValue(),  "E");
        directionButtons[Direction.SOUTH.get3DDataValue()] = createDirectionButton(135, 58, Direction.SOUTH.get3DDataValue(), "S");
        directionButtons[Direction.DOWN.get3DDataValue()]  = createDirectionButton(155, 58, Direction.DOWN.get3DDataValue(),  "D");

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
            menu.setShowItemGhosts(activeTab == Tab.ITEMS);
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

        pagePrevButton = Button.builder(Component.literal("<"), btn -> menu.setPage(menu.getCurrentPage() - 1))
                .bounds(leftPos + 62, topPos + 18, 14, 16).build();
        this.addRenderableWidget(pagePrevButton);

        pageNextButton = Button.builder(Component.literal(">"), btn -> menu.setPage(menu.getCurrentPage() + 1))
                .bounds(leftPos + 98, topPos + 18, 14, 16).build();
        this.addRenderableWidget(pageNextButton);

        priorityDownButton = Button.builder(Component.literal("-"), btn -> {
            menu.decrementPriority();
            ClientPacketDistributor.sendToServer(new RoutingNodePayload(menu.tile.getBlockPos(), RoutingNodePayload.ACTION_DECREMENT_PRIORITY, 0));
        }).bounds(leftPos + 62, topPos + 40, 14, 16).build();
        this.addRenderableWidget(priorityDownButton);

        priorityUpButton = Button.builder(Component.literal("+"), btn -> {
            menu.incrementPriority();
            ClientPacketDistributor.sendToServer(new RoutingNodePayload(menu.tile.getBlockPos(), RoutingNodePayload.ACTION_INCREMENT_PRIORITY, 0));
        }).bounds(leftPos + 98, topPos + 40, 14, 16).build();
        this.addRenderableWidget(priorityUpButton);
    }

    private Button createDirectionButton(int x, int y, int dirIndex, String label) {
        return Button.builder(Component.literal(label), btn -> {
            menu.selectSlot(dirIndex);
            ClientPacketDistributor.sendToServer(new RoutingNodePayload(menu.tile.getBlockPos(), RoutingNodePayload.ACTION_SELECT_SLOT, dirIndex));
        }).bounds(leftPos + x, topPos + y, 16, 16).build();
    }

    public boolean isItemsTab() {
        return activeTab == Tab.ITEMS;
    }

    public boolean isFluidsTab() {
        return activeTab == Tab.FLUIDS;
    }

    public void setItemGhostFromJei(int ghostSlot, ItemStack stack) {
        if (stack.isEmpty() || menu.tile == null) return;
        ClientPacketDistributor.sendToServer(new RoutingNodeSetGhostPayload(
                menu.tile.getBlockPos(), menu.absoluteSlot(ghostSlot), stack.copyWithCount(1)));
        menu.setVisibleItemGhostLocal(ghostSlot, stack);
    }

    public void setFluidGhostFromJei(int ghostSlot, FluidStack stack) {
        if (stack.isEmpty() || menu.tile == null) return;
        FluidStack ghost = stack.copy();
        ghost.setAmount(1);
        ClientPacketDistributor.sendToServer(new RoutingNodeSetFluidGhostPayload(
                menu.tile.getBlockPos(), menu.absoluteSlot(ghostSlot), ghost));
        menu.setVisibleFluidGhostLocal(ghostSlot, ghost);
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

        int pageCount = menu.getPageCount();
        if (menu.getCurrentPage() > pageCount - 1) {
            menu.setPage(pageCount - 1);
        }
        pagePrevButton.active = menu.getCurrentPage() > 0;
        pageNextButton.active = menu.getCurrentPage() < pageCount - 1;

        if (activeTab == Tab.FLUIDS) {
            renderFluidGhosts(guiGraphics);
        }
        renderGhostAmounts(guiGraphics);
    }

    /** Draws fluid sprites into the (item-free) ghost cells on the Fluids tab. */
    private void renderFluidGhosts(GuiGraphicsExtractor guiGraphics) {
        for (int i = 0; i < RoutingNodeMenu.GHOST_SLOT_COUNT; i++) {
            FluidStack ghost = menu.getCurrentFluidGhost(i);
            if (ghost.isEmpty()) continue;
            Slot slot = menu.slots.get(i);
            RenderHelper.renderGuiFluid(guiGraphics, ghost.getFluid(), leftPos + slot.x, topPos + slot.y, 16, 16);
        }
    }

    /** Draws the per-slot keep amount in the corner of each whitelisted ghost cell. */
    private void renderGhostAmounts(GuiGraphicsExtractor guiGraphics) {
        boolean items = activeTab == Tab.ITEMS;
        FilterMode mode = items ? menu.getSideItemMode(menu.getCurrentSlot()) : menu.getSideFluidMode(menu.getCurrentSlot());
        if (mode != FilterMode.WHITELIST) return;

        for (int i = 0; i < RoutingNodeMenu.GHOST_SLOT_COUNT; i++) {
            boolean present = items
                    ? !menu.slots.get(i).getItem().isEmpty()
                    : !menu.getCurrentFluidGhost(i).isEmpty();
            int amount = items ? menu.getCurrentItemAmount(i) : menu.getCurrentFluidAmount(i);
            if (!present || amount <= 0) continue;

            Slot slot = menu.slots.get(i);
            Component str = Component.literal(formatAmount(amount));
            int x = leftPos + slot.x + 17 - font.width(str);
            int y = topPos + slot.y + 9;
            guiGraphics.text(font, str, x + 1, y + 1, 0xFF3F3F3F);
            guiGraphics.text(font, str, x, y, 0xFFFFFFFF);
        }
    }

    private static String formatAmount(int amount) {
        if (amount >= 1_000_000) return (amount / 1_000_000) + "M";
        if (amount >= 1000) return (amount / 1000) + "k";
        return Integer.toString(amount);
    }

    private int amountStep(boolean items) {
        if (KeyboardHelper.isControlDown()) return items ? 64 : 250;
        if (KeyboardHelper.isShiftDown()) return items ? 10 : 10_000;
        return items ? 1 : 1000;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF404040);
        guiGraphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF404040);

        Component pageStr = Component.literal((menu.getCurrentPage() + 1) + "/" + menu.getPageCount());
        guiGraphics.text(font, pageStr, 87 - font.width(pageStr) / 2, 22, 0xFF404040);

        int currentSlot = menu.getCurrentSlot();
        if (currentSlot < 0 || currentSlot >= 6) return;

        int priority = menu.getCurrentPriority();
        Component priorityStr = Component.translatable("gui.neovitae.routing.priority_short", priority);
        guiGraphics.text(font, priorityStr, 87 - font.width(priorityStr) / 2, 44, 0xFF404040);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if (activeTab == Tab.ITEMS && this.hoveredSlot != null
                && this.hoveredSlot.index < RoutingNodeMenu.GHOST_SLOT_COUNT
                && !this.hoveredSlot.getItem().isEmpty()) {
            ItemStack ghost = this.hoveredSlot.getItem();
            List<Component> tooltip = new ArrayList<>(Screen.getTooltipFromItem(this.minecraft, ghost));
            if (menu.getSideItemMode(menu.getCurrentSlot()) == FilterMode.WHITELIST) {
                int amount = menu.getCurrentItemAmount(this.hoveredSlot.index);
                tooltip.add((amount > 0
                        ? Component.translatable("gui.neovitae.routing.keep", amount)
                        : Component.translatable("gui.neovitae.routing.keep_unlimited")).withStyle(ChatFormatting.AQUA));
                tooltip.add(Component.translatable("gui.neovitae.routing.keep_scroll").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }
            guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            return;
        }

        if (activeTab == Tab.FLUIDS && this.hoveredSlot != null
                && this.hoveredSlot.index < RoutingNodeMenu.GHOST_SLOT_COUNT) {
            FluidStack fluid = menu.getCurrentFluidGhost(this.hoveredSlot.index);
            List<Component> tooltip = new ArrayList<>();
            if (fluid.isEmpty()) {
                tooltip.add(Component.translatable("gui.neovitae.routing.slot.empty").withStyle(ChatFormatting.DARK_GRAY));
                tooltip.add(Component.translatable("gui.neovitae.routing.bucket.set").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            } else {
                tooltip.add(fluid.getHoverName());
                if (menu.getSideFluidMode(menu.getCurrentSlot()) == FilterMode.WHITELIST) {
                    int amount = menu.getCurrentFluidAmount(this.hoveredSlot.index);
                    tooltip.add((amount > 0
                            ? Component.translatable("gui.neovitae.routing.keep_mb", amount)
                            : Component.translatable("gui.neovitae.routing.keep_unlimited")).withStyle(ChatFormatting.AQUA));
                    tooltip.add(Component.translatable("gui.neovitae.routing.keep_scroll_fluid").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
                }
                tooltip.add(Component.translatable("gui.neovitae.routing.bucket.clear").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }
            guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            return;
        }

        super.extractTooltip(guiGraphics, mouseX, mouseY);

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
        if (pagePrevButton.isHovered() || pageNextButton.isHovered()) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("gui.neovitae.routing.page", menu.getCurrentPage() + 1, menu.getPageCount()));
            tooltip.add(Component.translatable("gui.neovitae.routing.page_hint").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
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
                                menu.absoluteSlot(slotIdx),
                                carried.copyWithCount(1)
                        ));
                        menu.clicked(slotIdx, 0, ContainerInput.PICKUP, this.minecraft.player);
                    }
                    return true;
                } else if (button == 1) {
                    ClientPacketDistributor.sendToServer(new RoutingNodeSetGhostPayload(
                            menu.tile.getBlockPos(),
                            menu.absoluteSlot(slotIdx),
                            ItemStack.EMPTY
                    ));
                    menu.clicked(slotIdx, 1, ContainerInput.PICKUP, this.minecraft.player);
                    return true;
                }
            } else {
                if (button == 0) {
                    if (!carried.isEmpty()) {
                        FluidStack extracted = FluidUtil.getFirstStackContained(carried);
                        if (!extracted.isEmpty()) {
                            FluidStack ghost = extracted.copy();
                            ghost.setAmount(1);
                            ClientPacketDistributor.sendToServer(new RoutingNodeSetFluidGhostPayload(
                                    menu.tile.getBlockPos(),
                                    menu.absoluteSlot(slotIdx),
                                    ghost
                            ));
                        }
                    }
                    return true;
                } else if (button == 1) {
                    ClientPacketDistributor.sendToServer(new RoutingNodeSetFluidGhostPayload(
                            menu.tile.getBlockPos(),
                            menu.absoluteSlot(slotIdx),
                            FluidStack.EMPTY
                    ));
                    return true;
                }
            }
        }

        return super.mouseClicked(event, dragging);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY != 0 && this.hoveredSlot != null && this.hoveredSlot.index < RoutingNodeMenu.GHOST_SLOT_COUNT) {
            int slotIdx = this.hoveredSlot.index;
            boolean items = activeTab == Tab.ITEMS;
            FilterMode mode = items ? menu.getSideItemMode(menu.getCurrentSlot()) : menu.getSideFluidMode(menu.getCurrentSlot());
            boolean present = items
                    ? !this.hoveredSlot.getItem().isEmpty()
                    : !menu.getCurrentFluidGhost(slotIdx).isEmpty();

            if (mode == FilterMode.WHITELIST && present) {
                int current = items ? menu.getCurrentItemAmount(slotIdx) : menu.getCurrentFluidAmount(slotIdx);
                int max = items ? 999_999 : 1_000_000_000;
                int step = amountStep(items);
                int next = (int) Math.max(0, Math.min(max, current + (scrollY > 0 ? step : -step)));
                if (next != current) {
                    if (items) {
                        menu.setCurrentItemAmountLocal(slotIdx, next);
                    } else {
                        menu.setCurrentFluidAmountLocal(slotIdx, next);
                    }
                    ClientPacketDistributor.sendToServer(new RoutingNodeSetAmountPayload(
                            menu.tile.getBlockPos(), !items, menu.absoluteSlot(slotIdx), next));
                }
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}
