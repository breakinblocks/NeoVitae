package com.breakinblocks.neovitae.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SentientStats;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeLimits;
import com.breakinblocks.neovitae.common.menu.TrainerMenu;
import com.breakinblocks.neovitae.common.network.SetTrainingCapPayload;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;

import java.util.Comparator;
import java.util.List;

public class TrainerScreen extends AbstractContainerScreen<TrainerMenu> {

    private static final int PANEL_WIDTH = 232;
    private static final int TITLE_HEIGHT = 22;
    private static final int HINT_HEIGHT = 12;
    private static final int ROW_HEIGHT = 16;
    private static final int VISIBLE_ROWS = 10;
    private static final int LIST_X = 6;
    private static final int PAD_BOTTOM = 8;
    private static final int STEP_W = 12;
    private static final int VALUE_W = 26;
    private static final int SCROLL_W = 4;

    private static final int BORDER = 0xFF6B1A1A;
    private static final int BG_OUTER = 0xCC1A0A0A;
    private static final int BG_INNER = 0xCC2A1520;

    private List<Holder<SentientUpgrade>> upgrades = List.of();
    private int scroll;

    public TrainerScreen(TrainerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = PANEL_WIDTH;
        this.imageHeight = TITLE_HEIGHT + HINT_HEIGHT + VISIBLE_ROWS * ROW_HEIGHT + PAD_BOTTOM;
    }

    @Override
    protected void init() {
        super.init();
        if (minecraft != null && minecraft.level != null) {
            upgrades = TrainerMenu.trainable(minecraft.level.registryAccess()).stream()
                    .sorted(Comparator.comparing(holder -> Component.translatable(SentientUpgrade.descriptionId(holder.getKey())).getString()))
                    .toList();
        }
        scroll = Mth.clamp(scroll, 0, maxScroll());
    }

    private int maxScroll() {
        return Math.max(0, upgrades.size() - VISIBLE_ROWS);
    }

    private ItemStack chest() {
        return SentientHelper.getChest(menu.getPlayer());
    }

    private int listTop() {
        return topPos + TITLE_HEIGHT + HINT_HEIGHT;
    }

    private int stepperX() {
        return leftPos + imageWidth - LIST_X - SCROLL_W - 2 - (STEP_W * 2 + VALUE_W);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, BG_OUTER);
        g.fill(leftPos + 1, topPos + 1, leftPos + imageWidth - 1, topPos + imageHeight - 1, BG_INNER);
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + 1, BORDER);
        g.fill(leftPos, topPos + imageHeight - 1, leftPos + imageWidth, topPos + imageHeight, BORDER);
        g.fill(leftPos, topPos, leftPos + 1, topPos + imageHeight, BORDER);
        g.fill(leftPos + imageWidth - 1, topPos, leftPos + imageWidth, topPos + imageHeight, BORDER);

        ItemStack chest = chest();
        UpgradeLimits limits = chest.getOrDefault(NVDataComponents.LIMITS, UpgradeLimits.EMPTY);
        SentientStats stats = chest.getOrDefault(NVDataComponents.UPGRADES, SentientStats.EMPTY);
        int sx = stepperX();

        for (int row = 0; row < VISIBLE_ROWS && scroll + row < upgrades.size(); row++) {
            Holder<SentientUpgrade> upgrade = upgrades.get(scroll + row);
            int y = listTop() + row * ROW_HEIGHT;
            int textY = y + (ROW_HEIGHT - 8) / 2;
            boolean rowHover = mouseX >= leftPos + LIST_X && mouseX < sx - 2 && mouseY >= y && mouseY < y + ROW_HEIGHT;
            g.fill(leftPos + LIST_X, y, leftPos + imageWidth - LIST_X - SCROLL_W - 2, y + ROW_HEIGHT - 1,
                    rowHover ? 0x553A1520 : (row % 2 == 0 ? 0x22000000 : 0x11000000));

            int current = SentientHelper.getLevelFromXp(upgrade, stats.upgrades().getOrDefault(upgrade, 0f));
            int max = SentientHelper.getMaxLevel(upgrade);
            int cap = limits.capLevel(upgrade);
            g.drawString(font, Component.translatable(SentientUpgrade.descriptionId(upgrade.getKey())),
                    leftPos + LIST_X + 3, textY, current > 0 ? 0xFFE0C8CF : 0xFF9A8088, false);
            String have = current + "/" + max;
            g.drawString(font, have, sx - 6 - font.width(have), textY, 0xFF9A8088, false);

            boolean minusHover = inBox(mouseX, mouseY, sx, y, STEP_W);
            boolean plusHover = inBox(mouseX, mouseY, sx + STEP_W + VALUE_W, y, STEP_W);
            g.fill(sx, y + 1, sx + STEP_W, y + ROW_HEIGHT - 2, minusHover ? 0x55FFFFFF : 0x33000000);
            g.fill(sx + STEP_W + VALUE_W, y + 1, sx + STEP_W * 2 + VALUE_W, y + ROW_HEIGHT - 2, plusHover ? 0x55FFFFFF : 0x33000000);
            g.drawString(font, "-", sx + (STEP_W - font.width("-")) / 2, textY, 0xFFE0C0C8, false);
            g.drawString(font, "+", sx + STEP_W + VALUE_W + (STEP_W - font.width("+")) / 2, textY, 0xFFE0C0C8, false);

            Component value;
            int color;
            if (cap <= 0) {
                value = Component.translatable("gui.neovitae.trainer.off");
                color = 0xFFE05050;
            } else if (cap >= max) {
                value = Component.translatable("gui.neovitae.trainer.max");
                color = 0xFF7FD47F;
            } else {
                value = Component.literal(String.valueOf(cap));
                color = 0xFFFFFFFF;
            }
            g.drawString(font, value, sx + STEP_W + (VALUE_W - font.width(value)) / 2, textY, color, false);
        }

        if (maxScroll() > 0) {
            int trackX = leftPos + imageWidth - LIST_X - SCROLL_W;
            int trackH = VISIBLE_ROWS * ROW_HEIGHT;
            g.fill(trackX, listTop(), trackX + SCROLL_W, listTop() + trackH, 0x33000000);
            int thumbH = Math.max(12, trackH * VISIBLE_ROWS / upgrades.size());
            int thumbY = listTop() + (trackH - thumbH) * scroll / maxScroll();
            g.fill(trackX, thumbY, trackX + SCROLL_W, thumbY + thumbH, 0xFF8A3A4A);
        }
    }

    private boolean inBox(double mouseX, double mouseY, int x, int y, int w) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + ROW_HEIGHT;
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(font, title, (imageWidth - font.width(title)) / 2, 8, 0xFFE7B0C0, false);
        Component hint = Component.translatable("gui.neovitae.trainer.hint");
        g.drawString(font, hint, (imageWidth - font.width(hint)) / 2, TITLE_HEIGHT, 0xFF9A8088, false);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        renderTooltip(g, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int sx = stepperX();
            for (int row = 0; row < VISIBLE_ROWS && scroll + row < upgrades.size(); row++) {
                int y = listTop() + row * ROW_HEIGHT;
                Holder<SentientUpgrade> upgrade = upgrades.get(scroll + row);
                if (inBox(mouseX, mouseY, sx, y, STEP_W)) {
                    step(upgrade, -1);
                    return true;
                }
                if (inBox(mouseX, mouseY, sx + STEP_W + VALUE_W, y, STEP_W)) {
                    step(upgrade, 1);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void step(Holder<SentientUpgrade> upgrade, int direction) {
        ItemStack chest = chest();
        UpgradeLimits limits = chest.getOrDefault(NVDataComponents.LIMITS, UpgradeLimits.EMPTY);
        int max = SentientHelper.getMaxLevel(upgrade);
        int requested = Screen.hasShiftDown() ? (direction < 0 ? 0 : max) : limits.capLevel(upgrade) + direction;
        int target = Mth.clamp(requested, 0, max);
        if (target == limits.capLevel(upgrade)) return;
        chest.set(NVDataComponents.LIMITS, limits.withCap(upgrade, target, upgrades));
        upgrade.unwrapKey().ifPresent(key -> PacketDistributor.sendToServer(new SetTrainingCapPayload(key.location(), target)));
        if (minecraft != null) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (maxScroll() > 0 && scrollY != 0) {
            scroll = Mth.clamp(scroll - (int) Math.signum(scrollY), 0, maxScroll());
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}
