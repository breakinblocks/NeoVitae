package com.breakinblocks.neovitae.will;

import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;

public final class SpiritusHelper {

    private SpiritusHelper() {}

    public static boolean hasSpiritus(ItemStack stack) {
        return stack.has(NVDataComponents.SPIRITUS_AMOUNT);
    }

    public static boolean isRechargeable(ItemStack stack) {
        return resolveMaxSpiritus(stack) > 0;
    }

    public static SpiritusType getCurrentType(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW);
    }

    public static double getSpiritus(ItemStack stack, SpiritusType type) {
        if (!hasSpiritus(stack)) return 0;
        SpiritusType current = getCurrentType(stack);
        if (!type.equals(current)) return 0;
        return stack.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0.0);
    }

    public static double resolveMaxSpiritus(ItemStack stack) {
        Double componentMax = stack.get(NVDataComponents.SPIRITUS_MAX);
        if (componentMax != null) return componentMax;

        Double dataMapMax = stack.typeHolder().getData(NVDataMaps.SPIRITUS_GEM_MAX_AMOUNTS);
        if (dataMapMax != null) return dataMapMax;

        return 0;
    }

    public static double resolveMaxSpiritus(ItemStack stack, SpiritusType type) {
        double currentSpiritus = stack.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0.0);
        if (currentSpiritus > 0 && !type.equals(getCurrentType(stack))) return 0;
        return resolveMaxSpiritus(stack);
    }

    public static void setSpiritus(ItemStack stack, SpiritusType type, double amount) {
        stack.set(NVDataComponents.SPIRITUS_TYPE, type);
        stack.set(NVDataComponents.SPIRITUS_AMOUNT, amount);
    }

    public static double drainSpiritus(ItemStack stack, SpiritusType type, double amount, boolean doDrain) {
        double current = getSpiritus(stack, type);
        double drained = Math.min(amount, current);
        if (doDrain && drained > 0) {
            stack.set(NVDataComponents.SPIRITUS_AMOUNT, current - drained);
        }
        return drained;
    }

    public static double fillSpiritus(ItemStack stack, SpiritusType type, double amount, boolean doFill) {
        double maxSpiritus = resolveMaxSpiritus(stack, type);
        if (maxSpiritus <= 0) return 0;

        double currentSpiritus = stack.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0.0);
        double filled = Math.min(amount, maxSpiritus - currentSpiritus);
        if (filled <= 0) return 0;

        if (doFill) {
            SpiritusType typeToSet = currentSpiritus > 0 ? getCurrentType(stack) : type;
            setSpiritus(stack, typeToSet, currentSpiritus + filled);
        }
        return filled;
    }

    public static double getFillRatio(ItemStack stack) {
        double max = resolveMaxSpiritus(stack);
        if (max <= 0) return 0;
        double current = stack.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0.0);
        return Math.min(1.0, current / max);
    }
}
