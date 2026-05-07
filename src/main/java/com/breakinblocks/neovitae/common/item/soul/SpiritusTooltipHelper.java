package com.breakinblocks.neovitae.common.item.soul;

import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.will.PlayerSpiritusHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;

public final class SpiritusTooltipHelper {

    private SpiritusTooltipHelper() {}

    public static int spiritusColor(SpiritusType type) {
        return switch (type) {
            case RAW -> 0x66E6E6;
            case RUINA -> 0x55EE55;
            case NIHILUM -> 0xFF8844;
            case VINDICTA -> 0xBB55EE;
            case INVICTUS -> 0xEEEEEE;
        };
    }

    public static void appendSpiritusInfo(ItemStack stack, String tooltipKey, List<Component> tooltip) {
        SpiritusType type = stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW);
        Style typeStyle = Style.EMPTY.withColor(spiritusColor(type));
        String typeKey = type.name().toLowerCase(Locale.ROOT);

        Component coloredName = Component.translatable("tooltip.neovitae.spiritus." + typeKey).withStyle(typeStyle);
        tooltip.add(Component.translatable("tooltip.neovitae.spiritus.type", coloredName).withStyle(ChatFormatting.GRAY));

        LocalPlayer localPlayer = Minecraft.getInstance().player;
        int level = -1;
        if (localPlayer != null) {
            double pool = PlayerSpiritusHandler.getTotalSpiritus(type, localPlayer);
            level = SentientToolHelper.getLevel(pool);
            int displayLevel = Math.max(0, level + 1);
            tooltip.add(Component.translatable("tooltip.neovitae.spiritus.level",
                    displayLevel, (int) pool).withStyle(ChatFormatting.GRAY));
        }

        double damageBonus = SentientToolHelper.getDamageBonus(stack);
        if (damageBonus > 0) {
            tooltip.add(Component.translatable("tooltip.neovitae.spiritus.damage_bonus",
                    String.format(Locale.ROOT, "%.1f", damageBonus)).withStyle(ChatFormatting.GRAY));
        }

        if (Screen.hasShiftDown()) {
            tooltip.add(buildRider(tooltipKey, type, level).withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("tooltip.neovitae.spiritus.hold_shift").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static MutableComponent buildRider(String tooltipKey, SpiritusType type, int level) {
        String typeKey = type.name().toLowerCase(Locale.ROOT);

        return switch (type) {
            case RUINA -> {
                if (level < 0) {
                    yield Component.translatable("tooltip.neovitae.spiritus.rider.ruina.inactive");
                }
                int amplifier = SentientToolHelper.POISON_LEVEL[level] + 1;
                double seconds = SentientToolHelper.POISON_TIME[level] / 20.0;
                yield Component.translatable("tooltip.neovitae.spiritus.rider.ruina",
                        amplifier, String.format(Locale.ROOT, "%.1f", seconds));
            }
            case INVICTUS -> {
                if (level < 0) {
                    yield Component.translatable("tooltip.neovitae.spiritus.rider.invictus.inactive");
                }
                double seconds = SentientToolHelper.ABSORPTION_TIME[level] / 20.0;
                yield Component.translatable("tooltip.neovitae.spiritus.rider.invictus",
                        String.format(Locale.ROOT, "%.1f", seconds));
            }
            default -> Component.translatable("tooltip.neovitae." + tooltipKey + ".rider." + typeKey);
        };
    }
}
