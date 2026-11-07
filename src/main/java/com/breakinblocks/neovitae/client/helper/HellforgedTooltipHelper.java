package com.breakinblocks.neovitae.client.helper;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.event.ClientModEventHandler;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.soul.HellforgedHelper;
import com.breakinblocks.neovitae.common.item.soul.ISentientTool;
import com.breakinblocks.neovitae.common.item.soul.LexVitaeItem;

import java.util.List;
import java.util.Locale;

public final class HellforgedTooltipHelper {

    public static final int HELLFORGED_COLOR = 0xFF9A6A;

    private HellforgedTooltipHelper() {}

    public static void append(ItemStack stack, List<Component> tooltip) {
        if (!HellforgedHelper.isHellforged(stack)) return;
        tooltip.add(Component.translatable("tooltip.neovitae.hellforged")
                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(HELLFORGED_COLOR))));

        if (stack.is(NVItems.SENTIENT_PLATE.get())) {
            tooltip.add(detail(Component.translatable("tooltip.neovitae.hellforged.training",
                    format(NeoVitae.SERVER_CONFIG.HELLFORGED_TRAINING_MULTIPLIER.get()))));
        }
        if (HellforgedHelper.hasNetheriteFloor(stack)) {
            tooltip.add(detail(Component.translatable("tooltip.neovitae.hellforged.netherite_floor")));
        }
        if (stack.getItem() instanceof ISentientTool) {
            double drain = NeoVitae.SERVER_CONFIG.HELLFORGED_DRAIN_MULTIPLIER.get();
            if (drain < 1.0) {
                tooltip.add(detail(Component.translatable("tooltip.neovitae.hellforged.drain",
                        String.format(Locale.ROOT, "%.0f", (1.0 - drain) * 100))));
            }
        }
        if (!(stack.getItem() instanceof LexVitaeItem) && stack.has(DataComponents.DAMAGE_RESISTANT)) {
            tooltip.add(detail(Component.translatable("tooltip.neovitae.hellforged.fire_resistant")));
        }
        if (LexVitaeItem.canToggleSilkTouch(stack)) {
            tooltip.add(detail(Component.translatable("tooltip.neovitae.hellforged.silk_toggle",
                    ClientModEventHandler.LEX_MODE.getTranslatedKeyMessage())));
        }
    }

    private static Component detail(Component line) {
        return Component.literal(" ").append(line).withStyle(ChatFormatting.GRAY);
    }

    private static String format(double value) {
        return value == Math.rint(value) ? String.format(Locale.ROOT, "%.0f", value) : String.format(Locale.ROOT, "%s", value);
    }
}
