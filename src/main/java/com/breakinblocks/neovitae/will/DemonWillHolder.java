package com.breakinblocks.neovitae.will;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Holds demon will amounts for each type.
 */
public class DemonWillHolder {
    private final EnumMap<EnumWillType, Double> willMap = new EnumMap<>(EnumWillType.class);

    /**
     * Adds will up to a maximum amount.
     *
     * @param type   - The type of will
     * @param amount - The amount to add
     * @param max    - The maximum allowed
     * @return The amount actually added
     */
    public double addWill(EnumWillType type, double amount, double max) {
        double current = willMap.getOrDefault(type, 0.0);
        double added = Math.min(max - current, amount);
        addWill(type, added);
        return added;
    }

    /**
     * Adds will without a maximum check.
     *
     * @param type   - The type of will
     * @param amount - The amount to add
     */
    public void addWill(EnumWillType type, double amount) {
        double current = willMap.getOrDefault(type, 0.0);
        willMap.put(type, current + amount);
    }

    /**
     * Drains will of a specific type.
     *
     * @param type   - The type of will
     * @param amount - The amount to drain
     * @return The amount actually drained
     */
    public double drainWill(EnumWillType type, double amount) {
        double current = willMap.getOrDefault(type, 0.0);
        if (current <= 0) {
            return 0;
        }

        double reduced = Math.min(current, amount);
        double remaining = current - reduced;

        if (remaining <= 0) {
            willMap.remove(type);
        } else {
            willMap.put(type, remaining);
        }

        return reduced;
    }

    /**
     * Gets the will of a specific type.
     *
     * @param type - The type of will
     * @return The amount of will
     */
    public double getWill(EnumWillType type) {
        return willMap.getOrDefault(type, 0.0);
    }

    /**
     * Reads the will holder from NBT.
     *
     * @param tag - The tag to read from
     * @param key - The key to read under
     */
    public void readFromNBT(CompoundTag tag, String key) {
        CompoundTag willTag = tag.getCompound(key);
        willMap.clear();

        for (EnumWillType type : EnumWillType.values()) {
            String nbtKey = "EnumWill" + type.name();
            if (willTag.contains(nbtKey)) {
                double amount = willTag.getDouble(nbtKey);
                if (amount > 0) {
                    willMap.put(type, amount);
                }
            }
        }
    }

    /**
     * Writes the will holder to NBT.
     *
     * @param tag - The tag to write to
     * @param key - The key to write under
     */
    public void writeToNBT(CompoundTag tag, String key) {
        CompoundTag willTag = new CompoundTag();
        for (Map.Entry<EnumWillType, Double> entry : willMap.entrySet()) {
            willTag.putDouble("EnumWill" + entry.getKey().name(), entry.getValue());
        }
        tag.put(key, willTag);
    }

    /**
     * Clears all will from this holder.
     */
    public void clearWill() {
        willMap.clear();
    }
}
