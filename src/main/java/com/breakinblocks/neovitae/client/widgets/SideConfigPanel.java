package com.breakinblocks.neovitae.client.widgets;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.network.PacketDistributor;
import com.breakinblocks.neovitae.common.network.PasteSideConfigPayload;
import com.breakinblocks.neovitae.common.network.SetOutputFacePayload;
import com.breakinblocks.neovitae.common.network.SetSideConfigPayload;
import com.breakinblocks.neovitae.common.sideconfig.OutputFaceConfigurable;
import com.breakinblocks.neovitae.common.sideconfig.SlotSideConfig;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;

public class SideConfigPanel {

    private static final SystemToast.SystemToastId CLIPBOARD_TOAST = new SystemToast.SystemToastId(2000L);

    private record Clipboard(BlockEntityType<?> type, byte[] mask, int face) {}

    @Nullable
    private static Clipboard clipboard;

    private final Map<Direction, SideToggleButton> buttons = new EnumMap<>(Direction.class);
    private final BlockEntity tile;
    private final BlockPos pos;
    private final SlotSideConfig config;
    private final IntSupplier activeSlot;
    private final IntFunction<Component> slotName;
    private final int pushSlot;
    @Nullable
    private final OutputFaceConfigurable pushTarget;

    public SideConfigPanel(BlockEntity tile, SlotSideConfig config, IntSupplier activeSlot, IntFunction<Component> slotName,
                           int pushSlot, @Nullable OutputFaceConfigurable pushTarget) {
        this.tile = tile;
        this.pos = tile.getBlockPos();
        this.config = config;
        this.activeSlot = activeSlot;
        this.slotName = slotName;
        this.pushSlot = pushSlot;
        this.pushTarget = pushTarget;
    }

    public void create(Consumer<SideToggleButton> add, int x, int y) {
        buttons.clear();
        addButton(add, x, y, Direction.DOWN, "D");
        addButton(add, x + 18, y, Direction.UP, "U");
        addButton(add, x, y + 18, Direction.NORTH, "N");
        addButton(add, x + 18, y + 18, Direction.SOUTH, "S");
        addButton(add, x, y + 36, Direction.WEST, "W");
        addButton(add, x + 18, y + 36, Direction.EAST, "E");
        tick();
    }

    private void addButton(Consumer<SideToggleButton> add, int x, int y, Direction direction, String label) {
        SideToggleButton button = new SideToggleButton(
                x, y, direction, Component.literal(label),
                () -> isAllowed(direction),
                () -> isPushing(direction),
                btn -> toggleAllowed(direction));
        button.visible = false;
        buttons.put(direction, button);
        add.accept(button);
    }

    private boolean isAllowed(Direction direction) {
        int slot = activeSlot.getAsInt();
        return slot >= 0 && config.isAllowed(slot, direction);
    }

    private boolean isPushing(Direction direction) {
        return pushTarget != null && activeSlot.getAsInt() == pushSlot && pushTarget.getOutputFace() == direction;
    }

    private void toggleAllowed(Direction direction) {
        int slot = activeSlot.getAsInt();
        if (slot < 0) return;
        boolean newState = !config.isAllowed(slot, direction);
        config.setAllowed(slot, direction, newState);
        PacketDistributor.sendToServer(new SetSideConfigPayload(pos, slot, direction.get3DDataValue(), newState));
        refreshTooltips();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 1 || pushTarget == null || activeSlot.getAsInt() != pushSlot) return false;
        for (SideToggleButton toggle : buttons.values()) {
            if (toggle.visible && toggle.isMouseOver(mouseX, mouseY)) {
                Direction face = pushTarget.getOutputFace() == toggle.getDirection() ? null : toggle.getDirection();
                pushTarget.setOutputFace(face);
                PacketDistributor.sendToServer(new SetOutputFacePayload(pos, face == null ? -1 : face.get3DDataValue()));
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.player != null) {
                    minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.4f, 1.0f);
                }
                refreshTooltips();
                return true;
            }
        }
        return false;
    }

    public boolean keyPressed(int keyCode, @Nullable GuiEventListener focused) {
        if (focused instanceof EditBox) return false;
        if (Screen.isCopy(keyCode)) {
            copy();
            return true;
        }
        if (Screen.isPaste(keyCode)) {
            paste();
            return true;
        }
        return false;
    }

    private void copy() {
        Direction[] directions = Direction.values();
        byte[] mask = new byte[config.slotCount() * directions.length];
        for (int slot = 0; slot < config.slotCount(); slot++) {
            for (Direction dir : directions) {
                mask[slot * directions.length + dir.get3DDataValue()] = (byte) (config.isAllowed(slot, dir) ? 1 : 0);
            }
        }
        Direction face = pushTarget == null ? null : pushTarget.getOutputFace();
        clipboard = new Clipboard(tile.getType(), mask, face == null ? -1 : face.get3DDataValue());
        notify(Component.translatable("gui.neovitae.side_config.copied", machineName()));
    }

    private void paste() {
        Clipboard copied = clipboard;
        if (copied == null) {
            notify(Component.translatable("gui.neovitae.side_config.empty"));
            return;
        }
        if (copied.type() != tile.getType() || copied.mask().length != config.slotCount() * Direction.values().length) {
            notify(Component.translatable("gui.neovitae.side_config.wrong_machine"));
            return;
        }
        Direction[] directions = Direction.values();
        for (int slot = 0; slot < config.slotCount(); slot++) {
            for (Direction dir : directions) {
                config.setAllowed(slot, dir, copied.mask()[slot * directions.length + dir.get3DDataValue()] != 0);
            }
        }
        if (pushTarget != null) {
            pushTarget.setOutputFace(copied.face() < 0 ? null : Direction.from3DDataValue(copied.face()));
        }
        PacketDistributor.sendToServer(new PasteSideConfigPayload(pos, copied.mask(), copied.face()));
        notify(Component.translatable("gui.neovitae.side_config.pasted", machineName()));
        refreshTooltips();
    }

    private Component machineName() {
        return tile.getBlockState().getBlock().getName();
    }

    private static void notify(Component message) {
        Minecraft minecraft = Minecraft.getInstance();
        SystemToast.addOrUpdate(minecraft.getToasts(), CLIPBOARD_TOAST, Component.translatable("gui.neovitae.side_config.title"), message);
        if (minecraft.player != null) {
            minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.4f, 1.0f);
        }
    }

    public void tick() {
        boolean active = activeSlot.getAsInt() >= 0;
        for (SideToggleButton button : buttons.values()) {
            button.visible = active;
        }
        if (active) refreshTooltips();
    }

    private void refreshTooltips() {
        int slot = activeSlot.getAsInt();
        if (slot < 0) return;
        Component name = slotName.apply(slot);
        for (Map.Entry<Direction, SideToggleButton> entry : buttons.entrySet()) {
            Direction dir = entry.getKey();
            List<Component> lines = new ArrayList<>();
            lines.add(Component.translatable(
                    "gui.neovitae.tabula_vitae.side_button",
                    Component.translatable("gui.neovitae.side." + dir.getName()).withStyle(ChatFormatting.WHITE),
                    name));
            lines.add(config.isAllowed(slot, dir)
                    ? Component.translatable("gui.neovitae.tabula_vitae.side_allowed").withStyle(ChatFormatting.GREEN)
                    : Component.translatable("gui.neovitae.tabula_vitae.side_blocked").withStyle(ChatFormatting.RED));
            lines.add(Component.translatable("gui.neovitae.tabula_vitae.side_hint").withStyle(ChatFormatting.GRAY));
            lines.add(Component.translatable("gui.neovitae.side_config.hint").withStyle(ChatFormatting.DARK_GRAY));
            if (pushTarget != null && slot == pushSlot) {
                lines.add(isPushing(dir)
                        ? Component.translatable("gui.neovitae.side_pushing").withStyle(ChatFormatting.GOLD)
                        : Component.translatable("gui.neovitae.side_push_hint").withStyle(ChatFormatting.GRAY));
            }
            entry.getValue().setTooltip(Tooltip.create(joinLines(lines)));
        }
    }

    private static Component joinLines(List<Component> lines) {
        MutableComponent root = Component.empty();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) root.append("\n");
            root.append(lines.get(i));
        }
        return root;
    }
}
