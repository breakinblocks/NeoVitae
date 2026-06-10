package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.widgets.SideToggleButton;
import com.breakinblocks.neovitae.common.blockentity.TabulaVitaeBlockEntity;
import com.breakinblocks.neovitae.common.menu.TabulaVitaeMenu;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import com.breakinblocks.neovitae.common.network.SetSideConfigPayload;
import com.breakinblocks.neovitae.common.sideconfig.SlotSideConfig;
import com.breakinblocks.neovitae.compat.jei.NeoVitaeJEIPlugin;
import com.breakinblocks.neovitae.compat.jei.tabulavitae.TabulaVitaeRecipeCategory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TabulaVitaeScreen extends AbstractContainerScreen<TabulaVitaeMenu> {
    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(NeoVitae.MODID, "textures/gui/alchemytable.png");

    private final Map<Direction, SideToggleButton> sideButtons = new EnumMap<>(Direction.class);

    public TabulaVitaeScreen(TabulaVitaeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 205);
        this.titleLabelX = 38;
        this.inventoryLabelY = 111;
    }

    @Override
    protected void init() {
        super.init();
        sideButtons.clear();

        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;

        addSideButton(left + 133, top + 50, Direction.DOWN, "D");
        addSideButton(left + 151, top + 50, Direction.UP, "U");
        addSideButton(left + 133, top + 68, Direction.NORTH, "N");
        addSideButton(left + 151, top + 68, Direction.SOUTH, "S");
        addSideButton(left + 133, top + 86, Direction.WEST, "W");
        addSideButton(left + 151, top + 86, Direction.EAST, "E");

        refreshSideButtons();
    }

    private void addSideButton(int x, int y, Direction direction, String label) {
        SideToggleButton button = new SideToggleButton(
                x, y, direction, Component.literal(label),
                () -> sideAllowed(direction),
                btn -> onDirectionButton(direction));
        button.visible = false;
        sideButtons.put(direction, button);
        this.addRenderableWidget(button);
    }

    private boolean sideAllowed(Direction direction) {
        int slot = menu.tile.activeSlot;
        return slot >= 0 && menu.tile.getSideConfig().isAllowed(slot, direction);
    }

    private void onDirectionButton(Direction direction) {
        TabulaVitaeBlockEntity tile = menu.tile;
        int slot = tile.activeSlot;
        if (slot < 0) return;
        SlotSideConfig config = tile.getSideConfig();
        boolean newState = !config.isAllowed(slot, direction);
        config.setAllowed(slot, direction, newState);
        ClientPacketDistributor.sendToServer(new SetSideConfigPayload(tile.getBlockPos(), slot, direction.get3DDataValue(), newState));
        refreshSideButtonTooltips();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        refreshSideButtons();
    }

    private void refreshSideButtons() {
        boolean active = menu.tile.activeSlot >= 0;
        for (SideToggleButton button : sideButtons.values()) {
            button.visible = active;
        }
        if (active) refreshSideButtonTooltips();
    }

    private void refreshSideButtonTooltips() {
        int slot = menu.tile.activeSlot;
        if (slot < 0) return;
        Component slotName = slotName(slot);
        for (Map.Entry<Direction, SideToggleButton> entry : sideButtons.entrySet()) {
            Direction dir = entry.getKey();
            boolean allowed = menu.tile.getSideConfig().isAllowed(slot, dir);
            MutableComponent header = Component.translatable(
                    "gui.neovitae.tabula_vitae.side_button",
                    Component.translatable("gui.neovitae.side." + dir.getName()).withStyle(ChatFormatting.WHITE),
                    slotName);
            Component state = allowed
                    ? Component.translatable("gui.neovitae.tabula_vitae.side_allowed").withStyle(ChatFormatting.GREEN)
                    : Component.translatable("gui.neovitae.tabula_vitae.side_blocked").withStyle(ChatFormatting.RED);
            Component hint = Component.translatable("gui.neovitae.tabula_vitae.side_hint").withStyle(ChatFormatting.GRAY);
            entry.getValue().setTooltip(Tooltip.create(joinLines(header, state, hint)));
        }
    }

    private static Component joinLines(Component... lines) {
        MutableComponent root = Component.empty();
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) root.append("\n");
            root.append(lines[i]);
        }
        return root;
    }

    private static Component slotName(int slot) {
        String key = switch (slot) {
            case TabulaVitaeBlockEntity.ORB_SLOT -> "gui.neovitae.tabula_vitae.slot.orb";
            case TabulaVitaeBlockEntity.OUTPUT_SLOT -> "gui.neovitae.tabula_vitae.slot.output";
            default -> "gui.neovitae.tabula_vitae.slot.input";
        };
        if (slot < TabulaVitaeBlockEntity.ORB_SLOT) {
            return Component.translatable(key, slot + 1).withStyle(ChatFormatting.YELLOW);
        }
        return Component.translatable(key).withStyle(ChatFormatting.YELLOW);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractTooltip(guiGraphics, mouseX, mouseY);
        if (isOverProgress(mouseX, mouseY)) {
            guiGraphics.setTooltipForNextFrame(this.font, Component.translatable("gui.neovitae.show_recipes").withStyle(ChatFormatting.YELLOW), mouseX, mouseY);
            return;
        }
        if (isOverActiveSlot(mouseX, mouseY)) {
            int slot = menu.tile.activeSlot;
            Component line1 = Component.translatable("gui.neovitae.tabula_vitae.active_slot", slotName(slot)).withStyle(ChatFormatting.WHITE);
            Component line2 = Component.translatable("gui.neovitae.tabula_vitae.active_hint").withStyle(ChatFormatting.GRAY);
            List<Component> lines = new ArrayList<>();
            lines.add(line1);
            lines.add(line2);
            guiGraphics.setTooltipForNextFrame(this.font, lines, Optional.empty(), mouseX, mouseY);
        }
    }

    private boolean isOverActiveSlot(double mouseX, double mouseY) {
        int slot = menu.tile.activeSlot;
        if (slot < 0) return false;
        Slot s = this.getMenu().getSlot(slot);
        int x = leftPos + s.x;
        int y = topPos + s.y;
        return mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16;
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
        double progress = menu.getProgress();
        return (int) (progress * scale);
    }
}
