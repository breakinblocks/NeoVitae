package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.widgets.FlatButton;
import com.breakinblocks.neovitae.client.widgets.SideConfigPanel;
import com.breakinblocks.neovitae.common.blockentity.SanguineCrafterBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.SanguineCrafterBlockEntity.ChunkLoading;
import com.breakinblocks.neovitae.common.blockentity.SanguineCrafterBlockEntity.Mode;
import com.breakinblocks.neovitae.common.blockentity.SanguineCrafterBlockEntity.RedstoneMode;
import com.breakinblocks.neovitae.common.blockentity.SanguineCrafterBlockEntity.Status;
import com.breakinblocks.neovitae.common.menu.SanguineCrafterMenu;
import com.breakinblocks.neovitae.common.network.SanguineCrafterSettingPayload;
import com.breakinblocks.neovitae.common.network.SanguineCrafterTemplatePayload;
import com.breakinblocks.neovitae.util.helper.KeyboardHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class SanguineCrafterScreen extends AbstractContainerScreen<SanguineCrafterMenu> {
    private static final Identifier BACKGROUND = NeoVitae.rl("textures/gui/sanguine_crafter.png");
    private static final int ARROW_X = 66;
    private static final int ARROW_Y = 37;
    private static final int ARROW_W = 24;
    private static final int ARROW_H = 16;
    private static final int MAX_LIMIT = 64 * SanguineCrafterBlockEntity.OUTPUT_COUNT;

    private SideConfigPanel sidePanel;
    private FlatButton modeButton;
    private FlatButton redstoneButton;
    private FlatButton limitButton;

    public SanguineCrafterScreen(SanguineCrafterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 212, 244);
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 152;
    }

    @Override
    protected void init() {
        super.init();
        SanguineCrafterBlockEntity tile = menu.tile;
        sidePanel = new SideConfigPanel(tile, tile.getSideConfig(), () -> tile.activeGroup,
                SanguineCrafterScreen::groupName, SanguineCrafterBlockEntity.GROUP_OUTPUT, tile);
        sidePanel.create(this::addRenderableWidget, leftPos + 157, topPos + 56);

        modeButton = addRenderableWidget(new FlatButton(leftPos + 8, topPos + 78, 54, 14,
                () -> Component.translatable(modeKey(tile.getMode()) + ".short"),
                b -> {
                    Mode next = tile.getMode().next();
                    tile.setMode(next);
                    sendSetting(SanguineCrafterSettingPayload.MODE, next.ordinal());
                }));
        redstoneButton = addRenderableWidget(new FlatButton(leftPos + 66, topPos + 77, 16, 16,
                Component::empty, () -> redstoneIcon(tile.getRedstoneMode()),
                b -> {
                    RedstoneMode next = tile.getRedstoneMode().next();
                    tile.setRedstoneMode(next);
                    sendSetting(SanguineCrafterSettingPayload.REDSTONE, next.ordinal());
                }));
        limitButton = addRenderableWidget(new FlatButton(leftPos + 86, topPos + 78, 62, 14,
                () -> tile.getOutputLimit() <= 0
                        ? Component.translatable("gui.neovitae.sanguine_crafter.limit.none")
                        : Component.translatable("gui.neovitae.sanguine_crafter.limit.value", tile.getOutputLimit()),
                b -> { }));
        refreshTooltips();
    }

    private void sendSetting(int setting, int value) {
        ClientPacketDistributor.sendToServer(new SanguineCrafterSettingPayload(menu.tile.getBlockPos(), setting, value));
        refreshTooltips();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        sidePanel.tick();
        refreshTooltips();
    }

    private void refreshTooltips() {
        SanguineCrafterBlockEntity tile = menu.tile;
        modeButton.setTooltip(Tooltip.create(lines(
                Component.translatable("gui.neovitae.sanguine_crafter.mode", Component.translatable(modeKey(tile.getMode())).withStyle(ChatFormatting.YELLOW)),
                Component.translatable(modeKey(tile.getMode()) + ".desc").withStyle(ChatFormatting.GRAY),
                Component.translatable("gui.neovitae.sanguine_crafter.click_to_change").withStyle(ChatFormatting.DARK_GRAY))));
        redstoneButton.setTooltip(Tooltip.create(lines(
                Component.translatable("gui.neovitae.sanguine_crafter.redstone",
                        Component.translatable("gui.neovitae.sanguine_crafter.redstone." + tile.getRedstoneMode().name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.YELLOW)),
                Component.translatable("gui.neovitae.sanguine_crafter.click_to_change").withStyle(ChatFormatting.DARK_GRAY))));
        limitButton.setTooltip(Tooltip.create(lines(
                Component.translatable("gui.neovitae.sanguine_crafter.limit.desc"),
                Component.translatable("gui.neovitae.sanguine_crafter.limit.scroll").withStyle(ChatFormatting.GRAY),
                Component.translatable("gui.neovitae.sanguine_crafter.limit.reset").withStyle(ChatFormatting.GRAY))));
    }

    private static Component lines(Component... lines) {
        MutableComponent root = Component.empty();
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) root.append("\n");
            root.append(lines[i]);
        }
        return root;
    }

    private static String modeKey(Mode mode) {
        return "gui.neovitae.sanguine_crafter.mode." + mode.name().toLowerCase(Locale.ROOT);
    }

    private static ItemStack redstoneIcon(RedstoneMode mode) {
        return switch (mode) {
            case IGNORED -> new ItemStack(Items.GUNPOWDER);
            case HIGH -> new ItemStack(Items.REDSTONE);
            case LOW -> new ItemStack(Items.REDSTONE_TORCH);
        };
    }

    private static Component groupName(int group) {
        String key = switch (group) {
            case SanguineCrafterBlockEntity.GROUP_INPUT -> "input";
            case SanguineCrafterBlockEntity.GROUP_OUTPUT -> "output";
            case SanguineCrafterBlockEntity.GROUP_ORB -> "orb";
            default -> "gem";
        };
        return Component.translatable("gui.neovitae.sanguine_crafter.group." + key).withStyle(ChatFormatting.YELLOW);
    }

    public void setTemplateSlotFromJei(int index, ItemStack stack) {
        if (stack.isEmpty()) return;
        SanguineCrafterBlockEntity tile = menu.tile;
        List<ItemStack> stacks = new ArrayList<>(SanguineCrafterBlockEntity.TEMPLATE_SIZE);
        for (int i = 0; i < SanguineCrafterBlockEntity.TEMPLATE_SIZE; i++) {
            stacks.add(i == index ? stack.copyWithCount(1) : tile.template.getStackInSlot(i).copy());
        }
        Mode mode = tile.getMode() == Mode.AUTO ? Mode.CRAFTING : tile.getMode();
        tile.setTemplate(mode, stacks);
        ClientPacketDistributor.sendToServer(new SanguineCrafterTemplatePayload(tile.getBlockPos(), mode.ordinal(), stacks));
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (sidePanel.keyPressed(event, getFocused())) {
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
        if (sidePanel.mouseClicked(event)) {
            return true;
        }
        if (event.button() == 1 && limitButton.isMouseOver(event.x(), event.y())) {
            menu.tile.setOutputLimit(0);
            sendSetting(SanguineCrafterSettingPayload.LIMIT, 0);
            return true;
        }
        return super.mouseClicked(event, dragging);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY != 0 && limitButton.isMouseOver(mouseX, mouseY)) {
            int step = KeyboardHelper.isControlDown() ? 64 : KeyboardHelper.isShiftDown() ? 10 : 1;
            int current = menu.tile.getOutputLimit();
            int next = Math.max(0, Math.min(MAX_LIMIT, current + (scrollY > 0 ? step : -step)));
            if (next != current) {
                menu.tile.setOutputLimit(next);
                sendSetting(SanguineCrafterSettingPayload.LIMIT, next);
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private Status status() {
        return Status.byIndex(menu.getData(SanguineCrafterMenu.DATA_STATUS));
    }

    private static int statusColor(Status status) {
        return switch (status) {
            case CRAFTING -> 0xFF60C060;
            case IDLE, DISABLED, LIMIT_REACHED -> 0xFFA0A0A0;
            default -> 0xFFE05050;
        };
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFFB0A0A0, false);
        guiGraphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFFB0A0A0, false);
        Status status = status();
        Component text = Component.translatable("gui.neovitae.sanguine_crafter.status." + status.name().toLowerCase(Locale.ROOT));
        guiGraphics.text(this.font, this.font.substrByWidth(text, 146).getString(), 8, 98, statusColor(status), false);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractTooltip(guiGraphics, mouseX, mouseY);
        if (isOverArrow(mouseX, mouseY) || isOverStatus(mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            Status status = status();
            tooltip.add(Component.translatable("gui.neovitae.sanguine_crafter.status." + status.name().toLowerCase(Locale.ROOT))
                    .withColor(statusColor(status) & 0xFFFFFF));
            tooltip.add(Component.translatable("gui.neovitae.sanguine_crafter.parallel", menu.getData(SanguineCrafterMenu.DATA_PARALLEL)).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("gui.neovitae.sanguine_crafter.speed", menu.getData(SanguineCrafterMenu.DATA_SPEED)).withStyle(ChatFormatting.GRAY));
            ChunkLoading chunkLoading = ChunkLoading.byIndex(menu.getData(SanguineCrafterMenu.DATA_CHUNK_LOADING));
            tooltip.add(Component.translatable("gui.neovitae.sanguine_crafter.chunk_loading." + chunkLoading.name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.GRAY));
            if (!menu.tile.getOwnerName().isEmpty()) {
                tooltip.add(Component.translatable("gui.neovitae.sanguine_crafter.owner", menu.tile.getOwnerName()).withStyle(ChatFormatting.DARK_GRAY));
            }
            guiGraphics.setTooltipForNextFrame(this.font, tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    private boolean isOverArrow(double mouseX, double mouseY) {
        int x = leftPos + ARROW_X;
        int y = topPos + ARROW_Y;
        return mouseX >= x && mouseX < x + ARROW_W && mouseY >= y && mouseY < y + ARROW_H;
    }

    private boolean isOverStatus(double mouseX, double mouseY) {
        int x = leftPos + 8;
        int y = topPos + 96;
        return mouseX >= x && mouseX < x + 146 && mouseY >= y && mouseY < y + 11;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256);

        int progress = Math.max(0, Math.min(1000, menu.getData(SanguineCrafterMenu.DATA_PROGRESS)));
        int width = progress * ARROW_W / 1000;
        if (width > 0) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos + ARROW_X, topPos + ARROW_Y, 212f, 0f, width, ARROW_H, 256, 256);
        }

        int group = menu.tile.activeGroup;
        if (group >= 0) {
            float v = group == SanguineCrafterBlockEntity.GROUP_OUTPUT ? 16f : 0f;
            for (int i = 0; i < SanguineCrafterBlockEntity.SLOT_COUNT; i++) {
                if (SanguineCrafterBlockEntity.groupOf(i) != group) continue;
                Slot slot = menu.getSlot(i);
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos + slot.x, topPos + slot.y, 236f, v, 16, 16, 256, 256);
            }
        }
    }
}
