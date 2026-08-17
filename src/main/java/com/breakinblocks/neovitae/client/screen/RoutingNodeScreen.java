package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
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
import com.breakinblocks.neovitae.api.routing.ISpiritusExportNode;
import com.breakinblocks.neovitae.common.routing.FaceDirection;
import com.breakinblocks.neovitae.common.routing.FilterMode;
import com.breakinblocks.neovitae.util.helper.KeyboardHelper;
import com.breakinblocks.neovitae.util.helper.RenderHelper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.lwjgl.glfw.GLFW;

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
    private enum Tab { ITEMS, FLUIDS, ENERGY, SPIRITUS }

    private Tab activeTab = Tab.ITEMS;

    private Button[] directionButtons = new Button[6];
    private Button priorityUpButton;
    private Button priorityDownButton;
    private Button enableButton;
    private Button directionButton;
    private Button energyButton;
    private Button modeButton;
    private Button tabButton;
    private Button pagePrevButton;
    private Button pageNextButton;
    private Button spiritusTypeButton;
    private Button stockDownButton;
    private Button stockUpButton;
    private EditBox energyRateBox;
    private Button energyRateSetButton;
    private int lastSyncedEnergyRate = -1;
    private int pendingEnergyRate = -1;
    private int lastEnergySide = -1;

    private static final int FILTER_GRID_X = 7;
    private static final int FILTER_GRID_Y = 75;
    private static final int FILTER_GRID_WIDTH = 163;
    private static final int FILTER_GRID_HEIGHT = 55;
    private static final int FILTER_GRID_BLANK_V = 20;

    private static final int ENERGY_BOX_X = 74;
    private static final int ENERGY_BOX_Y = 102;
    private static final int ENERGY_BOX_W = 56;
    private static final int ENERGY_BOX_H = 14;

    private static final int PICKER_WIDTH = 174;
    private int componentPickerSlot = -1;
    private final List<Identifier> pickerTypes = new ArrayList<>();

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

        directionButton = Button.builder(Component.translatable("gui.neovitae.routing.direction_off"), btn ->
                ClientPacketDistributor.sendToServer(new RoutingNodePayload(
                        menu.tile.getBlockPos(), RoutingNodePayload.ACTION_CYCLE_SIDE_DIRECTION,
                        KeyboardHelper.isShiftDown() ? -1 : 1)))
                .bounds(leftPos + 8, topPos + 18, 50, 16).build();
        directionButton.visible = false;
        this.addRenderableWidget(directionButton);

        tabButton = Button.builder(Component.translatable("gui.neovitae.routing.items"), btn -> {
            activeTab = switch (activeTab) {
                case ITEMS -> Tab.FLUIDS;
                case FLUIDS -> Tab.ENERGY;
                case ENERGY -> isSpiritusCapable() ? Tab.SPIRITUS : Tab.ITEMS;
                case SPIRITUS -> Tab.ITEMS;
            };
            menu.setShowItemGhosts(activeTab == Tab.ITEMS);
        }).bounds(leftPos + 8, topPos + 36, 50, 16).build();
        this.addRenderableWidget(tabButton);

        energyButton = Button.builder(Component.translatable("gui.neovitae.routing.energy_on"), btn ->
                ClientPacketDistributor.sendToServer(new RoutingNodePayload(
                        menu.tile.getBlockPos(), RoutingNodePayload.ACTION_TOGGLE_SIDE_ENERGY, 0)))
                .bounds(leftPos + 8, topPos + 80, 160, 16).build();
        energyButton.visible = false;
        this.addRenderableWidget(energyButton);

        energyRateBox = new EditBox(this.font, leftPos + ENERGY_BOX_X, topPos + ENERGY_BOX_Y,
                ENERGY_BOX_W, ENERGY_BOX_H, Component.translatable("gui.neovitae.routing.rate"));
        energyRateBox.setMaxLength(7);
        energyRateBox.setFilter(str -> str.isEmpty() || str.chars().allMatch(Character::isDigit));
        lastSyncedEnergyRate = menu.getEnergyRate();
        energyRateBox.setValue(String.valueOf(lastSyncedEnergyRate));
        energyRateBox.visible = false;
        this.addRenderableWidget(energyRateBox);

        energyRateSetButton = Button.builder(Component.translatable("gui.neovitae.routing.set"), btn -> commitEnergyRate())
                .bounds(leftPos + ENERGY_BOX_X + ENERGY_BOX_W + 4, topPos + ENERGY_BOX_Y, 26, ENERGY_BOX_H)
                .build();
        energyRateSetButton.visible = false;
        this.addRenderableWidget(energyRateSetButton);

        spiritusTypeButton = Button.builder(Component.translatable("gui.neovitae.routing.spiritus_off"), btn ->
                ClientPacketDistributor.sendToServer(new RoutingNodePayload(
                        menu.tile.getBlockPos(), RoutingNodePayload.ACTION_CYCLE_SPIRITUS_TYPE, 1)))
                .bounds(leftPos + 8, topPos + 80, 160, 16).build();
        spiritusTypeButton.visible = false;
        this.addRenderableWidget(spiritusTypeButton);

        stockDownButton = Button.builder(Component.literal("-"), btn ->
                ClientPacketDistributor.sendToServer(new RoutingNodePayload(
                        menu.tile.getBlockPos(), RoutingNodePayload.ACTION_ADJUST_SPIRITUS_STOCK,
                        KeyboardHelper.isShiftDown() ? -100 : -10)))
                .bounds(leftPos + 8, topPos + 102, 14, 16).build();
        stockDownButton.visible = false;
        this.addRenderableWidget(stockDownButton);

        stockUpButton = Button.builder(Component.literal("+"), btn ->
                ClientPacketDistributor.sendToServer(new RoutingNodePayload(
                        menu.tile.getBlockPos(), RoutingNodePayload.ACTION_ADJUST_SPIRITUS_STOCK,
                        KeyboardHelper.isShiftDown() ? 100 : 10)))
                .bounds(leftPos + 154, topPos + 102, 14, 16).build();
        stockUpButton.visible = false;
        this.addRenderableWidget(stockUpButton);

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

    private void commitEnergyRate() {
        if (energyRateBox == null || menu.tile == null) return;
        String value = energyRateBox.getValue();
        if (value.isEmpty()) return;
        int parsed;
        try {
            parsed = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return;
        }
        int ceiling = menu.getEnergyCeiling();
        int applied = (parsed <= 0 || parsed >= ceiling) ? ceiling : parsed;
        energyRateBox.setValue(String.valueOf(applied));
        energyRateBox.setFocused(false);
        pendingEnergyRate = applied;
        lastSyncedEnergyRate = applied;
        ClientPacketDistributor.sendToServer(new RoutingNodePayload(
                menu.tile.getBlockPos(), RoutingNodePayload.ACTION_SET_SIDE_ENERGY_RATE, applied));
    }

    private boolean energyBoxShowsSyncedValue() {
        if (energyRateBox == null) return false;
        String value = energyRateBox.getValue();
        try {
            return !value.isEmpty() && Integer.parseInt(value) == lastSyncedEnergyRate;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isFilterTab() {
        return activeTab == Tab.ITEMS || activeTab == Tab.FLUIDS;
    }

    private boolean isSpiritusCapable() {
        return menu.tile instanceof ISpiritusExportNode;
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
        boolean omni = menu.isOmniNode();
        boolean enabled = menu.isSideEnabled(currentSlot);
        enableButton.setMessage(Component.translatable(enabled ? "gui.neovitae.routing.enabled" : "gui.neovitae.routing.disabled")
                .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED));

        if (omni) {
            FaceDirection facing = menu.getSideDirection(currentSlot);
            directionButton.setMessage(Component.translatable(switch (facing) {
                case OFF -> "gui.neovitae.routing.direction_off";
                case INPUT -> "gui.neovitae.routing.direction_input";
                case OUTPUT -> "gui.neovitae.routing.direction_output";
                case BOTH -> "gui.neovitae.routing.direction_both";
            }).withStyle(switch (facing) {
                case OFF -> ChatFormatting.RED;
                case INPUT -> ChatFormatting.AQUA;
                case OUTPUT -> ChatFormatting.GOLD;
                case BOTH -> ChatFormatting.GREEN;
            }));
        }

        tabButton.setMessage(Component.translatable(switch (activeTab) {
            case ITEMS -> "gui.neovitae.routing.items";
            case FLUIDS -> "gui.neovitae.routing.fluids";
            case ENERGY -> "gui.neovitae.routing.energy";
            case SPIRITUS -> "gui.neovitae.routing.spiritus";
        }));
        tabButton.active = true;

        boolean spiritusTab = activeTab == Tab.SPIRITUS;
        boolean energyTab = activeTab == Tab.ENERGY;
        boolean filterTab = isFilterTab();
        menu.setGhostSlotsVisible(filterTab);
        for (Button button : directionButtons) {
            button.visible = !spiritusTab;
        }
        modeButton.visible = filterTab;
        pagePrevButton.visible = filterTab;
        pageNextButton.visible = filterTab;
        priorityUpButton.visible = !spiritusTab;
        priorityDownButton.visible = !spiritusTab;
        enableButton.visible = !spiritusTab && !omni;
        directionButton.visible = !spiritusTab && omni;
        energyButton.visible = energyTab;
        spiritusTypeButton.visible = spiritusTab;
        stockDownButton.visible = spiritusTab;
        stockUpButton.visible = spiritusTab;

        energyRateBox.visible = energyTab;
        energyRateSetButton.visible = energyTab;

        if (energyTab) {
            boolean energyOn = menu.isSideEnergyEnabled(currentSlot);
            energyButton.setMessage(Component.translatable(energyOn
                    ? "gui.neovitae.routing.energy_on" : "gui.neovitae.routing.energy_off")
                    .withStyle(energyOn ? ChatFormatting.GREEN : ChatFormatting.RED));
            energyButton.active = enabled;

            int serverRate = menu.getEnergyRate();
            if (currentSlot != lastEnergySide) {
                lastEnergySide = currentSlot;
                pendingEnergyRate = -1;
                lastSyncedEnergyRate = serverRate;
                energyRateBox.setValue(String.valueOf(serverRate));
                energyRateBox.setFocused(false);
            } else if (pendingEnergyRate >= 0) {
                if (serverRate == pendingEnergyRate) pendingEnergyRate = -1;
            } else if (serverRate != lastSyncedEnergyRate && energyBoxShowsSyncedValue()) {
                lastSyncedEnergyRate = serverRate;
                energyRateBox.setValue(String.valueOf(serverRate));
            }
            energyRateBox.setEditable(enabled);
            energyRateSetButton.active = enabled;
        } else if (energyRateBox.isFocused()) {
            energyRateBox.setFocused(false);
        }

        if (spiritusTab) {
            int typeOrdinal = menu.getSpiritusTypeOrdinal();
            if (typeOrdinal <= 0) {
                spiritusTypeButton.setMessage(Component.translatable("gui.neovitae.routing.spiritus_off")
                        .withStyle(ChatFormatting.GRAY));
            } else {
                SpiritusType type = SpiritusType.values()[Math.min(typeOrdinal - 1, SpiritusType.values().length - 1)];
                spiritusTypeButton.setMessage(Component.translatable("tooltip.neovitae.spiritus." + type.getSerializedName())
                        .withColor(SpiritusTooltipHelper.spiritusColor(type)));
            }
        }

        modeButton.active = enabled && filterTab;
        if (activeTab == Tab.ITEMS) {
            FilterMode mode = menu.getSideItemMode(currentSlot);
            modeButton.setMessage(Component.translatable(mode == FilterMode.WHITELIST ? "gui.neovitae.routing.whitelist" : "gui.neovitae.routing.blacklist")
                    .withStyle(mode == FilterMode.WHITELIST ? ChatFormatting.GREEN : ChatFormatting.RED));
        } else if (activeTab == Tab.FLUIDS) {
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
        if (filterTab) {
            renderGhostAmounts(guiGraphics);
        }

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
            Identifier id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(entry.getKey());
            if (id != null) {
                pickerTypes.add(id);
            }
        }
        componentPickerSlot = pickerTypes.isEmpty() ? -1 : ghostSlot;
    }

    private void renderComponentPicker(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        int x = pickerLeft();
        int y = pickerTop();
        int w = PICKER_WIDTH;
        int h = pickerHeight();
        guiGraphics.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF000000);
        guiGraphics.fill(x, y, x + w, y + h, 0xFF202024);
        guiGraphics.text(font, Component.translatable("gui.neovitae.routing.match_components"), x + 4, y + 4, 0xFFFFFFFF, false);

        Set<Identifier> selected = menu.getCurrentItemComponents(componentPickerSlot);
        for (int i = 0; i < pickerTypes.size(); i++) {
            Identifier id = pickerTypes.get(i);
            int rowY = y + 16 + i * 12;
            boolean on = selected.contains(id);
            boolean hovered = mouseX >= x && mouseX < x + w && mouseY >= rowY && mouseY < rowY + 12;
            if (hovered) {
                guiGraphics.fill(x + 1, rowY, x + w - 1, rowY + 12, 0x40FFFFFF);
            }
            guiGraphics.fill(x + 4, rowY + 2, x + 12, rowY + 10, 0xFF000000);
            guiGraphics.fill(x + 5, rowY + 3, x + 11, rowY + 9, on ? 0xFF55FF55 : 0xFF555555);
            String label = this.font.plainSubstrByWidth(id.toString(), w - 20);
            guiGraphics.text(font, label, x + 16, rowY + 2, on ? 0xFFFFFFFF : 0xFFAAAAAA, false);
        }
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
                Identifier id = pickerTypes.get(row);
                Set<Identifier> current = new LinkedHashSet<>(menu.getCurrentItemComponents(componentPickerSlot));
                if (!current.add(id)) {
                    current.remove(id);
                }
                menu.setCurrentItemComponentsLocal(componentPickerSlot, current);
                ClientPacketDistributor.sendToServer(new RoutingNodeSetComponentsPayload(
                        menu.tile.getBlockPos(), menu.absoluteSlot(componentPickerSlot), new ArrayList<>(current)));
            }
        }
        return true;
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
            guiGraphics.text(font, str, x + 1, y + 1, 0xFF3F3F3F, false);
            guiGraphics.text(font, str, x, y, 0xFFFFFFFF, false);
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
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF404040, false);
        guiGraphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF404040, false);

        if (activeTab == Tab.SPIRITUS) {
            Component keep = Component.translatable("gui.neovitae.routing.spiritus_keep", menu.getSpiritusStock());
            guiGraphics.text(font, keep, 63 - font.width(keep) / 2, 100, 0xFFFFFFFF, false);
            return;
        }

        if (isFilterTab()) {
            Component pageStr = Component.literal((menu.getCurrentPage() + 1) + "/" + menu.getPageCount());
            guiGraphics.text(font, pageStr, 87 - font.width(pageStr) / 2, 22, 0xFF404040, false);
        }

        int currentSlot = menu.getCurrentSlot();
        if (currentSlot < 0 || currentSlot >= 6) return;

        int priority = menu.getCurrentPriority();
        Component priorityStr = Component.translatable("gui.neovitae.routing.priority_short", priority);
        guiGraphics.text(font, priorityStr, 87 - font.width(priorityStr) / 2, 44, 0xFF404040, false);

        if (activeTab == Tab.ENERGY) {
            int ceiling = menu.getEnergyCeiling();
            guiGraphics.text(font, Component.translatable("gui.neovitae.routing.rate_label"),
                    8, ENERGY_BOX_Y + 3, 0xFF808080, false);
            int color = menu.getEnergyRate() > ceiling ? 0xFFC00000 : 0xFF808080;
            guiGraphics.text(font, Component.translatable("gui.neovitae.routing.rate_max", ceiling),
                    ENERGY_BOX_X, ENERGY_BOX_Y + ENERGY_BOX_H + 4, color, false);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256);
        if (!isFilterTab()) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos + FILTER_GRID_X, topPos + FILTER_GRID_Y,
                    FILTER_GRID_X, FILTER_GRID_BLANK_V, FILTER_GRID_WIDTH, FILTER_GRID_HEIGHT, 256, 256);
        }
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
            Set<Identifier> matched = menu.getCurrentItemComponents(this.hoveredSlot.index);
            if (!matched.isEmpty()) {
                tooltip.add(Component.translatable("gui.neovitae.routing.matching_components").withStyle(ChatFormatting.GREEN));
                for (Identifier id : matched) {
                    tooltip.add(Component.literal(" - " + id).withStyle(ChatFormatting.DARK_GREEN));
                }
            }
            tooltip.add(Component.translatable("gui.neovitae.routing.shift_match").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
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
            if (directionButtons[i].visible && directionButtons[i].isHovered()) {
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

        if (enableButton.visible && enableButton.isHovered()) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("gui.neovitae.routing.side.toggle"));
            tooltip.add(Component.translatable("gui.neovitae.routing.side.disabled_blocks").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            return;
        }
        if (directionButton.visible && directionButton.isHovered()) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("gui.neovitae.routing.face_mode"));
            tooltip.add(Component.translatable("gui.neovitae.routing.face_mode.input").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("gui.neovitae.routing.face_mode.output").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("gui.neovitae.routing.face_mode.both").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("gui.neovitae.routing.face_mode.cycle").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            return;
        }
        if (energyButton.visible && energyButton.isHovered()) {
            guiGraphics.setTooltipForNextFrame(this.font, Component.translatable("gui.neovitae.routing.energy_toggle"), mouseX, mouseY);
            return;
        }
        if (energyRateBox.visible && energyRateBox.isMouseOver(mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("gui.neovitae.routing.rate.title"));
            tooltip.add(Component.translatable("gui.neovitae.routing.rate.capped").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("gui.neovitae.routing.rate.tracks").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("gui.neovitae.routing.rate.apply").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            return;
        }
        if (spiritusTypeButton.visible && spiritusTypeButton.isHovered()) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("gui.neovitae.routing.spiritus_aspect"));
            tooltip.add(Component.translatable("gui.neovitae.routing.spiritus_aspect.desc").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            return;
        }
        if ((stockDownButton.visible && stockDownButton.isHovered()) || (stockUpButton.visible && stockUpButton.isHovered())) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("gui.neovitae.routing.spiritus_reserve"));
            tooltip.add(Component.translatable("gui.neovitae.routing.spiritus_reserve.hint").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            return;
        }
        if (tabButton.isHovered()) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("gui.neovitae.routing.filter.switch"));
            tooltip.add(Component.translatable("gui.neovitae.routing.filter.items_desc").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("gui.neovitae.routing.filter.fluids_desc").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("gui.neovitae.routing.filter.energy_desc").withStyle(ChatFormatting.GRAY));
            if (isSpiritusCapable()) {
                tooltip.add(Component.translatable("gui.neovitae.routing.filter.spiritus_desc").withStyle(ChatFormatting.GRAY));
            }
            guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            return;
        }
        if (modeButton.visible && modeButton.isHovered()) {
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
        if ((pagePrevButton.visible && pagePrevButton.isHovered()) || (pageNextButton.visible && pageNextButton.isHovered())) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("gui.neovitae.routing.page", menu.getCurrentPage() + 1, menu.getPageCount()));
            tooltip.add(Component.translatable("gui.neovitae.routing.page_hint").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            guiGraphics.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
            return;
        }
        if (priorityUpButton.visible && priorityUpButton.isHovered()) {
            guiGraphics.setTooltipForNextFrame(this.font, Component.translatable("gui.neovitae.routing.priority.increase"), mouseX, mouseY);
        }
        if (priorityDownButton.visible && priorityDownButton.isHovered()) {
            guiGraphics.setTooltipForNextFrame(this.font, Component.translatable("gui.neovitae.routing.priority.decrease"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
        int button = event.button();
        if (componentPickerSlot >= 0) {
            return handleComponentPickerClick(event.x(), event.y());
        }
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
                    ItemStack ghost = this.hoveredSlot.getItem();
                    if (KeyboardHelper.isShiftDown() && !ghost.isEmpty()) {
                        openComponentPicker(slotIdx, ghost);
                        return true;
                    }
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
    public boolean keyPressed(KeyEvent event) {
        if (componentPickerSlot >= 0 && event.key() == GLFW.GLFW_KEY_ESCAPE) {
            componentPickerSlot = -1;
            return true;
        }
        if ((event.key() == GLFW.GLFW_KEY_ENTER || event.key() == GLFW.GLFW_KEY_KP_ENTER)
                && energyRateBox != null && energyRateBox.isFocused()) {
            commitEnergyRate();
            return true;
        }
        return super.keyPressed(event);
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
