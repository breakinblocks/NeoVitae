package com.breakinblocks.neovitae.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
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
import com.breakinblocks.neovitae.common.blockentity.routing.OutputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.soul.SpiritusTooltipHelper;
import com.breakinblocks.neovitae.common.menu.RoutingNodeMenu;
import com.breakinblocks.neovitae.common.network.RoutingNodePayload;
import com.breakinblocks.neovitae.common.network.RoutingNodeSetAmountPayload;
import com.breakinblocks.neovitae.common.network.RoutingNodeSetFluidGhostPayload;
import com.breakinblocks.neovitae.common.network.RoutingNodeSetComponentsPayload;
import com.breakinblocks.neovitae.common.network.RoutingNodeSetGhostPayload;
import com.breakinblocks.neovitae.common.routing.FilterMode;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RoutingNodeScreen extends AbstractContainerScreen<RoutingNodeMenu> {
    private static final ResourceLocation BACKGROUND = NeoVitae.rl("textures/gui/routingnode.png");

    private static final String[] DIRECTION_NAMES = {"Down", "Up", "North", "South", "West", "East"};

    /** Client-side UI tab - server never needs to know which tab the player is viewing. */
    private enum Tab { ITEMS, FLUIDS, SPIRITUS }

    private Tab activeTab = Tab.ITEMS;

    private Button[] directionButtons = new Button[6];
    private Button priorityUpButton;
    private Button priorityDownButton;
    private Button enableButton;
    private Button modeButton;
    private Button tabButton;
    private Button pagePrevButton;
    private Button pageNextButton;
    private Button spiritusTypeButton;
    private Button stockDownButton;
    private Button stockUpButton;

    private static final int PICKER_WIDTH = 174;
    private int componentPickerSlot = -1;
    private final List<ResourceLocation> pickerTypes = new ArrayList<>();

    public RoutingNodeScreen(RoutingNodeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = RoutingNodeMenu.IMAGE_WIDTH;
        this.imageHeight = RoutingNodeMenu.IMAGE_HEIGHT;
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

        enableButton = Button.builder(Component.literal("Disabled"), btn -> {
            PacketDistributor.sendToServer(new RoutingNodePayload(
                    menu.tile.getBlockPos(),
                    RoutingNodePayload.ACTION_TOGGLE_SIDE_ENABLED,
                    0));
        }).bounds(leftPos + 8, topPos + 18, 50, 16).build();
        this.addRenderableWidget(enableButton);

        tabButton = Button.builder(Component.literal("Items"), btn -> {
            activeTab = switch (activeTab) {
                case ITEMS -> Tab.FLUIDS;
                case FLUIDS -> isSpiritusCapable() ? Tab.SPIRITUS : Tab.ITEMS;
                case SPIRITUS -> Tab.ITEMS;
            };
            menu.setShowItemGhosts(activeTab == Tab.ITEMS);
        }).bounds(leftPos + 8, topPos + 36, 50, 16).build();
        this.addRenderableWidget(tabButton);

        spiritusTypeButton = Button.builder(Component.literal("Off"), btn -> {
            PacketDistributor.sendToServer(new RoutingNodePayload(
                    menu.tile.getBlockPos(),
                    RoutingNodePayload.ACTION_CYCLE_SPIRITUS_TYPE,
                    1));
        }).bounds(leftPos + 8, topPos + 76, 110, 16).build();
        spiritusTypeButton.visible = false;
        this.addRenderableWidget(spiritusTypeButton);

        stockDownButton = Button.builder(Component.literal("-"), btn -> {
            PacketDistributor.sendToServer(new RoutingNodePayload(
                    menu.tile.getBlockPos(),
                    RoutingNodePayload.ACTION_ADJUST_SPIRITUS_STOCK,
                    Screen.hasShiftDown() ? -100 : -10));
        }).bounds(leftPos + 8, topPos + 96, 14, 16).build();
        stockDownButton.visible = false;
        this.addRenderableWidget(stockDownButton);

        stockUpButton = Button.builder(Component.literal("+"), btn -> {
            PacketDistributor.sendToServer(new RoutingNodePayload(
                    menu.tile.getBlockPos(),
                    RoutingNodePayload.ACTION_ADJUST_SPIRITUS_STOCK,
                    Screen.hasShiftDown() ? 100 : 10));
        }).bounds(leftPos + 104, topPos + 96, 14, 16).build();
        stockUpButton.visible = false;
        this.addRenderableWidget(stockUpButton);

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

        pagePrevButton = Button.builder(Component.literal("<"), btn -> menu.setPage(menu.getCurrentPage() - 1))
                .bounds(leftPos + 62, topPos + 18, 14, 16).build();
        this.addRenderableWidget(pagePrevButton);

        pageNextButton = Button.builder(Component.literal(">"), btn -> menu.setPage(menu.getCurrentPage() + 1))
                .bounds(leftPos + 98, topPos + 18, 14, 16).build();
        this.addRenderableWidget(pageNextButton);

        priorityDownButton = Button.builder(Component.literal("-"), btn -> {
            menu.decrementPriority();
            PacketDistributor.sendToServer(new RoutingNodePayload(menu.tile.getBlockPos(), RoutingNodePayload.ACTION_DECREMENT_PRIORITY, 0));
        }).bounds(leftPos + 62, topPos + 40, 14, 16).build();
        this.addRenderableWidget(priorityDownButton);

        priorityUpButton = Button.builder(Component.literal("+"), btn -> {
            menu.incrementPriority();
            PacketDistributor.sendToServer(new RoutingNodePayload(menu.tile.getBlockPos(), RoutingNodePayload.ACTION_INCREMENT_PRIORITY, 0));
        }).bounds(leftPos + 98, topPos + 40, 14, 16).build();
        this.addRenderableWidget(priorityUpButton);
    }

    private boolean isSpiritusCapable() {
        return menu.tile instanceof OutputRoutingNodeBlockEntity;
    }

    public boolean isItemsTab() {
        return activeTab == Tab.ITEMS;
    }

    public boolean isFluidsTab() {
        return activeTab == Tab.FLUIDS;
    }

    public void setItemGhostFromJei(int ghostSlot, ItemStack stack) {
        if (stack.isEmpty() || menu.tile == null) return;
        PacketDistributor.sendToServer(new RoutingNodeSetGhostPayload(
                menu.tile.getBlockPos(), menu.absoluteSlot(ghostSlot), stack.copyWithCount(1)));
        menu.setVisibleItemGhostLocal(ghostSlot, stack);
    }

    public void setFluidGhostFromJei(int ghostSlot, FluidStack stack) {
        if (stack.isEmpty() || menu.tile == null) return;
        FluidStack ghost = stack.copy();
        ghost.setAmount(1);
        PacketDistributor.sendToServer(new RoutingNodeSetFluidGhostPayload(
                menu.tile.getBlockPos(), menu.absoluteSlot(ghostSlot), ghost));
        menu.setVisibleFluidGhostLocal(ghostSlot, ghost);
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

        tabButton.setMessage(Component.literal(switch (activeTab) {
            case ITEMS -> "Items";
            case FLUIDS -> "Fluids";
            case SPIRITUS -> "Spiritus";
        }));
        tabButton.active = enabled || activeTab == Tab.SPIRITUS;

        boolean spiritusTab = activeTab == Tab.SPIRITUS;
        modeButton.visible = !spiritusTab;
        pagePrevButton.visible = !spiritusTab;
        pageNextButton.visible = !spiritusTab;
        priorityUpButton.visible = !spiritusTab;
        priorityDownButton.visible = !spiritusTab;
        enableButton.visible = !spiritusTab;
        spiritusTypeButton.visible = spiritusTab;
        stockDownButton.visible = spiritusTab;
        stockUpButton.visible = spiritusTab;

        if (spiritusTab) {
            int typeOrdinal = menu.getSpiritusTypeOrdinal();
            if (typeOrdinal <= 0) {
                spiritusTypeButton.setMessage(Component.literal("Off").withStyle(ChatFormatting.GRAY));
            } else {
                SpiritusType type = SpiritusType.values()[Math.min(typeOrdinal - 1, SpiritusType.values().length - 1)];
                spiritusTypeButton.setMessage(Component.translatable("tooltip.neovitae.spiritus." + type.getSerializedName())
                        .withColor(SpiritusTooltipHelper.spiritusColor(type)));
            }
            guiGraphics.drawCenteredString(this.font,
                    Component.literal("Keep: " + menu.getSpiritusStock()),
                    leftPos + 63, topPos + 100, 0xFFFFFF);
        }

        modeButton.active = enabled && !spiritusTab;
        if (activeTab == Tab.ITEMS) {
            FilterMode mode = menu.getSideItemMode(currentSlot);
            modeButton.setMessage(Component.literal(mode == FilterMode.WHITELIST ? "Whitelist" : "Blacklist")
                    .withStyle(mode == FilterMode.WHITELIST ? ChatFormatting.GREEN : ChatFormatting.RED));
        } else if (activeTab == Tab.FLUIDS) {
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

        int pageCount = menu.getPageCount();
        if (menu.getCurrentPage() > pageCount - 1) {
            menu.setPage(pageCount - 1);
        }
        pagePrevButton.active = menu.getCurrentPage() > 0;
        pageNextButton.active = menu.getCurrentPage() < pageCount - 1;

        if (activeTab == Tab.FLUIDS) {
            renderFluidGhosts(guiGraphics);
        }

        if (!spiritusTab) {
            renderGhostAmounts(guiGraphics);
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY);

        if (componentPickerSlot >= 0) {
            renderComponentPicker(guiGraphics, mouseX, mouseY);
        }
    }

    private int pickerHeight() {
        return 16 + pickerTypes.size() * 12 + 4;
    }

    private int pickerLeft() {
        return this.width / 2 - PICKER_WIDTH / 2;
    }

    private int pickerTop() {
        return this.height / 2 - pickerHeight() / 2;
    }

    private void openComponentPicker(int ghostSlot, ItemStack ghost) {
        pickerTypes.clear();
        for (var entry : ghost.getComponentsPatch().entrySet()) {
            ResourceLocation id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(entry.getKey());
            if (id != null) {
                pickerTypes.add(id);
            }
        }
        componentPickerSlot = pickerTypes.isEmpty() ? -1 : ghostSlot;
    }

    private void renderComponentPicker(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = pickerLeft();
        int y = pickerTop();
        int w = PICKER_WIDTH;
        int h = pickerHeight();
        guiGraphics.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF000000);
        guiGraphics.fill(x, y, x + w, y + h, 0xFF202024);
        guiGraphics.drawString(this.font,
                Component.translatable("gui.neovitae.routing.match_components"), x + 4, y + 4, 0xFFFFFF, false);

        Set<ResourceLocation> selected = menu.getCurrentItemComponents(componentPickerSlot);
        for (int i = 0; i < pickerTypes.size(); i++) {
            ResourceLocation id = pickerTypes.get(i);
            int rowY = y + 16 + i * 12;
            boolean on = selected.contains(id);
            boolean hovered = mouseX >= x && mouseX < x + w && mouseY >= rowY && mouseY < rowY + 12;
            if (hovered) {
                guiGraphics.fill(x + 1, rowY, x + w - 1, rowY + 12, 0x40FFFFFF);
            }
            guiGraphics.fill(x + 4, rowY + 2, x + 12, rowY + 10, 0xFF000000);
            guiGraphics.fill(x + 5, rowY + 3, x + 11, rowY + 9, on ? 0xFF55FF55 : 0xFF555555);
            String label = this.font.plainSubstrByWidth(id.toString(), w - 20);
            guiGraphics.drawString(this.font, label, x + 16, rowY + 2, on ? 0xFFFFFFFF : 0xFFAAAAAA, false);
        }
    }

    /** Draws the per-slot keep amount in the corner of each whitelisted ghost cell. */
    private void renderGhostAmounts(GuiGraphics guiGraphics) {
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
            String str = formatAmount(amount);
            int x = leftPos + slot.x + 17 - font.width(str);
            int y = topPos + slot.y + 9;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 300.0F);
            guiGraphics.drawString(font, str, x, y, 0xFFFFFF, true);
            guiGraphics.pose().popPose();
        }
    }

    private static String formatAmount(int amount) {
        if (amount >= 1_000_000) return (amount / 1_000_000) + "M";
        if (amount >= 1000) return (amount / 1000) + "k";
        return Integer.toString(amount);
    }

    private int amountStep(boolean items) {
        if (Screen.hasControlDown()) return items ? 64 : 250;
        if (Screen.hasShiftDown()) return items ? 10 : 10_000;
        return items ? 1 : 1000;
    }

    /** Draws fluid sprites into the (item-free) ghost cells on the Fluids tab. */
    private void renderFluidGhosts(GuiGraphics guiGraphics) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 250.0F);
        for (int i = 0; i < RoutingNodeMenu.GHOST_SLOT_COUNT; i++) {
            FluidStack fluid = menu.getCurrentFluidGhost(i);
            if (fluid.isEmpty()) continue;

            Slot slot = menu.slots.get(i);
            drawFluidSprite(guiGraphics, leftPos + slot.x, topPos + slot.y, fluid);
        }
        guiGraphics.pose().popPose();
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

        String pageStr = (menu.getCurrentPage() + 1) + "/" + menu.getPageCount();
        guiGraphics.drawString(font, pageStr, 87 - font.width(pageStr) / 2, 22, 0x404040, false);

        int currentSlot = menu.getCurrentSlot();
        if (currentSlot < 0 || currentSlot >= 6) return;

        int priority = menu.getCurrentPriority();
        String priorityStr = "P:" + priority;
        guiGraphics.drawString(font, priorityStr, 87 - font.width(priorityStr) / 2, 44, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (activeTab == Tab.ITEMS && this.hoveredSlot != null
                && this.hoveredSlot.index < RoutingNodeMenu.GHOST_SLOT_COUNT
                && !this.hoveredSlot.getItem().isEmpty()) {
            ItemStack ghost = this.hoveredSlot.getItem();
            List<Component> tooltip = new ArrayList<>(Screen.getTooltipFromItem(this.minecraft, ghost));
            if (menu.getSideItemMode(menu.getCurrentSlot()) == FilterMode.WHITELIST) {
                int amount = menu.getCurrentItemAmount(this.hoveredSlot.index);
                tooltip.add(Component.literal(amount > 0 ? "Keep: " + amount : "Keep: Unlimited").withStyle(ChatFormatting.AQUA));
                tooltip.add(Component.literal("Scroll to set (Shift x10, Ctrl x64)").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }
            Set<ResourceLocation> matched = menu.getCurrentItemComponents(this.hoveredSlot.index);
            if (!matched.isEmpty()) {
                tooltip.add(Component.literal("Matching components:").withStyle(ChatFormatting.GREEN));
                for (ResourceLocation id : matched) {
                    tooltip.add(Component.literal(" - " + id).withStyle(ChatFormatting.DARK_GREEN));
                }
            }
            tooltip.add(Component.literal("Shift-Right-Click: match components").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            guiGraphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
            return;
        }

        if (activeTab == Tab.FLUIDS && this.hoveredSlot != null
                && this.hoveredSlot.index < RoutingNodeMenu.GHOST_SLOT_COUNT) {
            FluidStack fluid = menu.getCurrentFluidGhost(this.hoveredSlot.index);
            List<Component> tooltip = new ArrayList<>();
            if (fluid.isEmpty()) {
                tooltip.add(Component.literal("Empty").withStyle(ChatFormatting.DARK_GRAY));
                tooltip.add(Component.literal("Left-click with a bucket to set").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            } else {
                tooltip.add(fluid.getHoverName());
                if (menu.getSideFluidMode(menu.getCurrentSlot()) == FilterMode.WHITELIST) {
                    int amount = menu.getCurrentFluidAmount(this.hoveredSlot.index);
                    tooltip.add(Component.literal(amount > 0 ? "Keep: " + amount + " mB" : "Keep: Unlimited").withStyle(ChatFormatting.AQUA));
                    tooltip.add(Component.literal("Scroll to set (Shift x10k, Ctrl x250)").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
                }
                tooltip.add(Component.literal("Right-click to clear").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }
            guiGraphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
            return;
        }

        super.renderTooltip(guiGraphics, mouseX, mouseY);

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
        if (pagePrevButton.isHovered() || pageNextButton.isHovered()) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal("Filter Page " + (menu.getCurrentPage() + 1) + " of " + menu.getPageCount()));
            tooltip.add(Component.literal("A new page opens as you fill the last").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            guiGraphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (componentPickerSlot >= 0) {
            return handleComponentPickerClick(mouseX, mouseY);
        }

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
                                menu.absoluteSlot(slotIdx),
                                carried.copyWithCount(1)
                        ));
                        menu.clicked(slotIdx, 0, ClickType.PICKUP, this.minecraft.player);
                    }
                    return true;
                } else if (button == 1) {
                    ItemStack ghost = this.hoveredSlot.getItem();
                    if (hasShiftDown() && !ghost.isEmpty()) {
                        openComponentPicker(slotIdx, ghost);
                        return true;
                    }
                    PacketDistributor.sendToServer(new RoutingNodeSetGhostPayload(
                            menu.tile.getBlockPos(),
                            menu.absoluteSlot(slotIdx),
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
                                    menu.absoluteSlot(slotIdx),
                                    ghost
                            ));
                        }
                    }
                    return true;
                } else if (button == 1) {
                    PacketDistributor.sendToServer(new RoutingNodeSetFluidGhostPayload(
                            menu.tile.getBlockPos(),
                            menu.absoluteSlot(slotIdx),
                            FluidStack.EMPTY
                    ));
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleComponentPickerClick(double mouseX, double mouseY) {
        int x = pickerLeft();
        int y = pickerTop();
        boolean inside = mouseX >= x && mouseX < x + PICKER_WIDTH && mouseY >= y && mouseY < y + pickerHeight();
        if (!inside) {
            componentPickerSlot = -1;
            return true;
        }
        int rowsTop = y + 16;
        if (mouseY >= rowsTop) {
            int row = (int) ((mouseY - rowsTop) / 12);
            if (row >= 0 && row < pickerTypes.size()) {
                ResourceLocation id = pickerTypes.get(row);
                Set<ResourceLocation> current = new LinkedHashSet<>(menu.getCurrentItemComponents(componentPickerSlot));
                if (!current.add(id)) {
                    current.remove(id);
                }
                menu.setCurrentItemComponentsLocal(componentPickerSlot, current);
                PacketDistributor.sendToServer(new RoutingNodeSetComponentsPayload(
                        menu.tile.getBlockPos(), menu.absoluteSlot(componentPickerSlot), new ArrayList<>(current)));
            }
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (componentPickerSlot >= 0 && keyCode == 256) {
            componentPickerSlot = -1;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
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
                    PacketDistributor.sendToServer(new RoutingNodeSetAmountPayload(
                            menu.tile.getBlockPos(), !items, menu.absoluteSlot(slotIdx), next));
                }
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}
