package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.widgets.SideToggleButton;
import com.breakinblocks.neovitae.common.blockentity.TabulaVitaeBlockEntity;
import com.breakinblocks.neovitae.common.menu.TabulaVitaeMenu;
import com.breakinblocks.neovitae.common.network.NVPayloads;
import com.breakinblocks.neovitae.common.network.SetSideConfigPayload;
import com.breakinblocks.neovitae.common.sideconfig.SlotSideConfig;
import com.breakinblocks.neovitae.compat.jei.NeoVitaeJEIPlugin;
import com.breakinblocks.neovitae.compat.jei.tabulavitae.TabulaVitaeRecipeCategory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TabulaVitaeScreen extends AbstractContainerScreen<TabulaVitaeMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "textures/gui/alchemytable.png");

    private final Map<Direction, SideToggleButton> sideButtons = new EnumMap<>(Direction.class);

    public TabulaVitaeScreen(TabulaVitaeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 205;
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
        NVPayloads.sendToServer(new SetSideConfigPayload(tile.getBlockPos(), slot, direction.get3DDataValue(), newState));
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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        if (isOverProgress(mouseX, mouseY)) {
            List<Component> lines = new ArrayList<>();
            TabulaVitaeBlockEntity.IdleReason reason = menu.getIdleReason();
            if (reason != TabulaVitaeBlockEntity.IdleReason.NONE) {
                lines.add(Component.translatable("gui.neovitae.tabula_vitae.idle." + reason.name().toLowerCase(Locale.ROOT))
                        .withStyle(ChatFormatting.RED));
            }
            lines.add(Component.translatable("gui.neovitae.show_recipes").withStyle(ChatFormatting.YELLOW));
            guiGraphics.renderComponentTooltip(this.font, lines, mouseX, mouseY);
            return;
        }
        if (isOverActiveSlot(mouseX, mouseY)) {
            int slot = menu.tile.activeSlot;
            List<Component> lines = new ArrayList<>();
            lines.add(Component.translatable("gui.neovitae.tabula_vitae.active_slot", slotName(slot)).withStyle(ChatFormatting.WHITE));
            lines.add(Component.translatable("gui.neovitae.tabula_vitae.active_hint").withStyle(ChatFormatting.GRAY));
            guiGraphics.renderComponentTooltip(this.font, lines, mouseX, mouseY);
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
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);

        int progress = getCookProgressScaled(90);
        guiGraphics.blit(BACKGROUND, i + 106, j + 14 + 90 - progress, 176, 90 - progress, 18, progress);

        int activeSlot = menu.tile.activeSlot;
        if (activeSlot != -1) {
            Slot slot = this.getMenu().getSlot(activeSlot);
            int highlightV = (activeSlot == TabulaVitaeBlockEntity.OUTPUT_SLOT) ? 37 : 19;
            guiGraphics.blit(BACKGROUND, i + slot.x, j + slot.y, 195, highlightV, 16, 16);
        }
    }

    private boolean isOverProgress(double mouseX, double mouseY) {
        int px = leftPos + 106;
        int py = topPos + 14;
        return mouseX >= px && mouseX < px + 18 && mouseY >= py && mouseY < py + 90;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isOverProgress(mouseX, mouseY)) {
            var runtime = NeoVitaeJEIPlugin.jeiRuntime;
            if (runtime != null) {
                runtime.getRecipesGui().showTypes(List.of(TabulaVitaeRecipeCategory.RECIPE_TYPE));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private int getCookProgressScaled(int scale) {
        double progress = menu.getProgress();
        return (int) (progress * scale);
    }
}
