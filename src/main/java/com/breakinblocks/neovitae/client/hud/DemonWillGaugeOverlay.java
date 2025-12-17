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
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.item.BMItems;
import com.breakinblocks.neovitae.will.WorldDemonWillHandler;

import java.util.List;

/**
 * HUD overlay that displays demon will levels in the current chunk.
 * Only shown when the player has a Demon Will Gauge in their inventory.
 */
public class DemonWillGaugeOverlay implements LayeredDraw.Layer {

    private static final ResourceLocation BAR_LOCATION = NeoVitae.rl("textures/hud/bars.png");

    // Order of will types for display (matches 1.20.1)
    private static final List<EnumWillType> ORDERED_TYPES = Lists.newArrayList(
            EnumWillType.DEFAULT,
            EnumWillType.CORROSIVE,
            EnumWillType.STEADFAST,
            EnumWillType.DESTRUCTIVE,
            EnumWillType.VENGEFUL
    );

    // HUD element dimensions
    private static final int WIDTH = 80;
    private static final int HEIGHT = 46;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null || mc.level == null) {
            return;
        }

        // Check if player has the gauge in their inventory
        if (!hasGaugeInInventory(player)) {
            return;
        }

        // Don't show in F3 debug screen
        if (mc.getDebugOverlay().showDebugScreen()) {
            return;
        }

        // Position in top-left corner (matching 1.20.1 positioning)
        int drawX = 2;
        int drawY = 2;

        // Draw background frame from texture
        guiGraphics.blit(BAR_LOCATION, drawX, drawY, 0, 210, WIDTH, HEIGHT);

        int i = 0;
        for (EnumWillType type : ORDERED_TYPES) {
            i++;
            // Calculate texture offset for each bar row
            int textureXOffset = (i > 3) ? (i - 3) : (3 - i);
            int maxBarSize = 30 - 2 * textureXOffset;

            // Get fill ratio (0.0 to 1.0) - respects configurable max per type
            double ratio = WorldDemonWillHandler.getWillChunk(mc.level, player.blockPosition()).getFillRatio(type);
            ratio = Math.max(Math.min(ratio, 1), 0);

            // Calculate bar dimensions and position
            int width = (int) (maxBarSize * ratio * 2);
            int height = 2;
            int x = drawX + 2 * textureXOffset + 10;
            int y = drawY + 4 * i + 10;

            // Calculate texture coordinates for the filled bar
            int textureX = 2 * textureXOffset + 2 * 42;
            int textureY = 4 * i + 220;

            // Draw the filled portion of the bar
            if (width > 0) {
                guiGraphics.blit(BAR_LOCATION, x, y, textureX, textureY, width, height);
            }

            // Show numerical amount and max when shift is held
            if (player.isShiftKeyDown()) {
                double amount = WorldDemonWillHandler.getCurrentWill(mc.level, player.blockPosition(), type);
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
            if (stack.is(BMItems.DEMON_WILL_GAUGE.get())) {
                return true;
            }
        }
        return false;
    }
}
