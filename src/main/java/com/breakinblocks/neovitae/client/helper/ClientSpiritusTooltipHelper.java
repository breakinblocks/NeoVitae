package com.breakinblocks.neovitae.client.helper;

import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.soul.SentientToolHelper;
import com.breakinblocks.neovitae.will.PlayerSpiritusHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

import java.util.Locale;
import java.util.function.Consumer;

public final class ClientSpiritusTooltipHelper {

    private ClientSpiritusTooltipHelper() {}

    public static int resolveLevel(SpiritusType type) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return -1;
        return SentientToolHelper.getLevel(PlayerSpiritusHandler.getTotalSpiritus(type, localPlayer));
    }

    public static double resolvePool(SpiritusType type) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return 0.0;
        return PlayerSpiritusHandler.getTotalSpiritus(type, localPlayer);
    }

    public static int appendLevelLine(SpiritusType type, Consumer<Component> tooltip) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return -1;
        double pool = PlayerSpiritusHandler.getTotalSpiritus(type, localPlayer);
        int level = SentientToolHelper.getLevel(pool);
        int displayLevel = Math.max(0, level + 1);
        tooltip.accept(Component.translatable("tooltip.neovitae.spiritus.level",
                displayLevel, (int) pool).withStyle(ChatFormatting.GRAY));
        return level;
    }

    public static void appendAreaRadius(SpiritusType type, double[] areaRange, Consumer<Component> tooltip) {
        int level = resolveLevel(type);
        if (level < 0) return;
        double radius = areaRange[level];
        tooltip.accept(Component.translatable("tooltip.neovitae.spiritus.aoe_radius",
                String.format(Locale.ROOT, "%.1f", radius))
                .withStyle(ChatFormatting.GRAY));
    }
}
