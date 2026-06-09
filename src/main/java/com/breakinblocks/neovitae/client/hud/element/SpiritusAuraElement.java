package com.breakinblocks.neovitae.client.hud.element;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.hud.HUDElement;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

import java.util.List;

public class SpiritusAuraElement extends HUDElement {

    private static final ResourceLocation BAR_LOCATION = NeoVitae.rl("textures/hud/bars.png");

    private static final List<SpiritusType> ORDERED_TYPES = Lists.newArrayList(
            SpiritusType.RAW,
            SpiritusType.RUINA,
            SpiritusType.INVICTUS,
            SpiritusType.NIHILUM,
            SpiritusType.VINDICTA
    );

    private static final int WIDTH = 80;
    private static final int HEIGHT = 46;

    public SpiritusAuraElement() {
        super(WIDTH, HEIGHT);
    }

    @Override
    public boolean shouldRender(Minecraft mc) {
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) {
            return false;
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(NVItems.SPIRITUS_GAUGE.get())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, float partialTicks, int drawX, int drawY) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) {
            return;
        }

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
                double amount = WorldSpiritusHandler.getCurrentSpiritus(mc.level, player.blockPosition(), type);
                PoseStack poseStack = guiGraphics.pose();
                poseStack.pushPose();
                poseStack.translate(x - 2 * textureXOffset + 70, y - 2, 0);
                poseStack.scale(0.5f, 0.5f, 1f);
                guiGraphics.drawString(mc.font, String.valueOf((int) amount), 0, 2, 0xFFFFFFFF, true);
                poseStack.popPose();
            }
        }
    }
}
