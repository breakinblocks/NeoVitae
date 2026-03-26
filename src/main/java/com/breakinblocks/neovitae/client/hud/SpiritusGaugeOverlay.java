package com.breakinblocks.neovitae.client.hud;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.will.WorldSpiritusHandler;

import java.util.List;

public class SpiritusGaugeOverlay implements LayeredDraw.Layer {

    private static final ResourceLocation BAR_LOCATION = NeoVitae.rl("textures/hud/bars.png");

    private static final List<SpiritusType> ORDERED_TYPES = Lists.newArrayList(
            SpiritusType.DEFAULT,
            SpiritusType.CORROSIVE,
            SpiritusType.STEADFAST,
            SpiritusType.DESTRUCTIVE,
            SpiritusType.VENGEFUL
    );

    private static final int WIDTH = 80;
    private static final int HEIGHT = 46;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null || mc.level == null) {
            return;
        }

        if (!hasGaugeInInventory(player)) {
            return;
        }

        if (mc.getDebugOverlay().showDebugScreen()) {
            return;
        }

        int drawX = 2;
        int drawY = 2;

        guiGraphics.blit(BAR_LOCATION, drawX, drawY, 0, 210, WIDTH, HEIGHT);

        int i = 0;
        for (SpiritusType type : ORDERED_TYPES) {
            i++;
            int textureXOffset = (i > 3) ? (i - 3) : (3 - i);
            int maxBarSize = 30 - 2 * textureXOffset;

            double ratio = WorldSpiritusHandler.getSpiritusChunk(mc.level, player.blockPosition()).getFillRatio(type);
            ratio = Math.max(Math.min(ratio, 1), 0);

            int width = (int) (maxBarSize * ratio * 2);
            int height = 2;
            int x = drawX + 2 * textureXOffset + 10;
            int y = drawY + 4 * i + 10;

            int textureX = 2 * textureXOffset + 2 * 42;
            int textureY = 4 * i + 220;

            if (width > 0) {
                guiGraphics.blit(BAR_LOCATION, x, y, textureX, textureY, width, height);
            }

            if (player.isShiftKeyDown()) {
                double amount = WorldSpiritusHandler.getCurrentWill(mc.level, player.blockPosition(), type);
                PoseStack poseStack = guiGraphics.pose();
                poseStack.pushPose();
                poseStack.translate(x - 2 * textureXOffset + 70, y - 2, 0);
                poseStack.scale(0.5f, 0.5f, 1f);
                guiGraphics.drawString(mc.font, String.valueOf((int) amount), 0, 2, 0xFFFFFFFF, true);
                poseStack.popPose();
            }
        }
    }

    private boolean hasGaugeInInventory(LocalPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(NVItems.SPIRITUS_GAUGE.get())) {
                return true;
            }
        }
        return false;
    }
}
