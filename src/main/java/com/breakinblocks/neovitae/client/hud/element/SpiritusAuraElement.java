package com.breakinblocks.neovitae.client.hud.element;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.hud.HUDElement;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.spiritus.PlayerSpiritusHandler;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

import java.util.List;

public class SpiritusAuraElement extends HUDElement {

    private static final Identifier BAR_LOCATION = NeoVitae.rl("textures/hud/bars.png");

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
        for (ItemStack stack : PlayerSpiritusHandler.getAllInventories(player)) {
            if (stack.is(NVItems.SPIRITUS_GAUGE.get())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(GuiGraphicsExtractor guiGraphics, float partialTicks, int drawX, int drawY) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) {
            return;
        }

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BAR_LOCATION, drawX, drawY, 0, 210, WIDTH, HEIGHT, 256, 256);

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
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BAR_LOCATION, x, y, textureX, textureY, width, height, 256, 256);
            }

            if (player.isShiftKeyDown()) {
                double amount = WorldSpiritusHandler.getCurrentSpiritus(mc.level, player.blockPosition(), type);
                var poseStack = guiGraphics.pose();
                poseStack.pushMatrix();
                poseStack.translate(x - 2 * textureXOffset + 70, y - 2);
                poseStack.scale(0.5f, 0.5f);
                guiGraphics.text(mc.font, String.valueOf((int) amount), 0, 2, 0xFFFFFFFF);
                poseStack.popMatrix();
            }
        }
    }
}
