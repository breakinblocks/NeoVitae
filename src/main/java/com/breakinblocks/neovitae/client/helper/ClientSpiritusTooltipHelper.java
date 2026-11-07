package com.breakinblocks.neovitae.client.helper;

import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.soul.HellforgedHelper;
import com.breakinblocks.neovitae.common.item.soul.SentientToolHelper;
import com.breakinblocks.neovitae.spiritus.PlayerSpiritusHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;

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

    public static int appendLevelLine(ItemStack stack, SpiritusType type, List<Component> tooltip) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return -1;
        double pool = PlayerSpiritusHandler.getTotalSpiritus(type, localPlayer);
        int level = SentientToolHelper.getLevel(pool);
        int displayLevel = Math.max(0, level + 1);
        tooltip.add(Component.translatable("tooltip.neovitae.spiritus.level",
                displayLevel, (int) pool).withStyle(ChatFormatting.GRAY));
        double overcharge = HellforgedHelper.overchargeFactor(stack, pool) - 1.0;
        if (overcharge > 0) {
            tooltip.add(Component.translatable("tooltip.neovitae.spiritus.overcharge",
                    String.format(Locale.ROOT, "%.0f", overcharge * 100)).withStyle(ChatFormatting.LIGHT_PURPLE));
        }
        return level;
    }

    public static void appendAreaRadius(ItemStack stack, SpiritusType type, double[] areaRange, List<Component> tooltip) {
        int level = resolveLevel(type);
        if (level < 0) return;
        double radius = areaRange[level] * HellforgedHelper.overchargeFactor(stack, resolvePool(type));
        tooltip.add(Component.translatable("tooltip.neovitae.spiritus.aoe_radius",
                String.format(Locale.ROOT, "%.1f", radius))
                .withStyle(ChatFormatting.GRAY));
    }
}
