package com.breakinblocks.neovitae.common.item.soul;

import com.breakinblocks.neovitae.client.helper.ClientSpiritusTooltipHelper;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.Locale;
import java.util.function.Consumer;

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

    public static void appendSpiritusInfo(ItemStack stack, String tooltipKey, Consumer<Component> tooltip, TooltipFlag flag) {
        SpiritusType type = stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW);
        Style typeStyle = Style.EMPTY.withColor(spiritusColor(type));
        String typeKey = type.name().toLowerCase(Locale.ROOT);

        Component coloredName = Component.translatable("tooltip.neovitae.spiritus." + typeKey).withStyle(typeStyle);
        tooltip.accept(Component.translatable("tooltip.neovitae.spiritus.type", coloredName).withStyle(ChatFormatting.GRAY));

        if (!flag.hasShiftDown()) {
            tooltip.accept(Component.translatable("tooltip.neovitae.spiritus.hold_shift").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        int level = ClientSpiritusTooltipHelper.appendLevelLine(type, tooltip);

        double damageBonus = SentientToolHelper.getDamageBonus(stack);
        if (damageBonus > 0) {
            tooltip.accept(Component.translatable("tooltip.neovitae.spiritus.damage_bonus",
                    String.format(Locale.ROOT, "%.1f", damageBonus)).withStyle(ChatFormatting.GRAY));
        }

        tooltip.accept(buildRider(tooltipKey, type, level).withStyle(ChatFormatting.GRAY));
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
