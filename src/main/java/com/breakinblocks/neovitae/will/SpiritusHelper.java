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
        return resolveMaxWill(stack) > 0;
    }

    public static SpiritusType getCurrentType(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.DEFAULT);
    }

    public static double getWill(ItemStack stack, SpiritusType type) {
        if (!hasSpiritus(stack)) return 0;
        SpiritusType current = getCurrentType(stack);
        if (!type.equals(current)) return 0;
        return stack.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0.0);
    }

    public static double resolveMaxWill(ItemStack stack) {
        Double componentMax = stack.get(NVDataComponents.SPIRITUS_MAX);
        if (componentMax != null) return componentMax;

        Double dataMapMax = stack.typeHolder().getData(NVDataMaps.SPIRITUS_GEM_MAX_AMOUNTS);
        if (dataMapMax != null) return dataMapMax;

        return 0;
    }

    public static double resolveMaxWill(ItemStack stack, SpiritusType type) {
        double currentWill = stack.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0.0);
        if (currentWill > 0 && !type.equals(getCurrentType(stack))) return 0;
        return resolveMaxWill(stack);
    }

    public static void setWill(ItemStack stack, SpiritusType type, double amount) {
        stack.set(NVDataComponents.SPIRITUS_TYPE, type);
        stack.set(NVDataComponents.SPIRITUS_AMOUNT, amount);
    }

    public static double drainWill(ItemStack stack, SpiritusType type, double amount, boolean doDrain) {
        double current = getWill(stack, type);
        double drained = Math.min(amount, current);
        if (doDrain && drained > 0) {
            stack.set(NVDataComponents.SPIRITUS_AMOUNT, current - drained);
        }
        return drained;
    }

    public static double fillWill(ItemStack stack, SpiritusType type, double amount, boolean doFill) {
        double maxWill = resolveMaxWill(stack, type);
        if (maxWill <= 0) return 0;

        double currentWill = stack.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0.0);
        double filled = Math.min(amount, maxWill - currentWill);
        if (filled <= 0) return 0;

        if (doFill) {
            SpiritusType typeToSet = currentWill > 0 ? getCurrentType(stack) : type;
            setWill(stack, typeToSet, currentWill + filled);
        }
        return filled;
    }

    public static double getFillRatio(ItemStack stack) {
        double max = resolveMaxWill(stack);
        if (max <= 0) return 0;
        double current = stack.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0.0);
        return Math.min(1.0, current / max);
    }
}
