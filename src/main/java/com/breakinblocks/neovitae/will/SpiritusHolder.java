package com.breakinblocks.neovitae.will;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

import java.util.EnumMap;
import java.util.Map;

public class SpiritusHolder {
    private final EnumMap<SpiritusType, Double> willMap = new EnumMap<>(SpiritusType.class);

    public double addWill(SpiritusType type, double amount, double max) {
        double current = willMap.getOrDefault(type, 0.0);
        double added = Math.min(max - current, amount);
        addWill(type, added);
        return added;
    }

    public void addWill(SpiritusType type, double amount) {
        double current = willMap.getOrDefault(type, 0.0);
        willMap.put(type, current + amount);
    }

    public double drainWill(SpiritusType type, double amount) {
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

    public double getWill(SpiritusType type) {
        return willMap.getOrDefault(type, 0.0);
    }

    public void readFromNBT(CompoundTag tag, String key) {
        CompoundTag willTag = tag.getCompound(key);
        willMap.clear();

        for (SpiritusType type : SpiritusType.values()) {
            String nbtKey = "EnumWill" + type.name();
            if (willTag.contains(nbtKey)) {
                double amount = willTag.getDouble(nbtKey);
                if (amount > 0) {
                    willMap.put(type, amount);
                }
            }
        }
    }

    public void writeToNBT(CompoundTag tag, String key) {
        CompoundTag willTag = new CompoundTag();
        for (Map.Entry<SpiritusType, Double> entry : willMap.entrySet()) {
            willTag.putDouble("EnumWill" + entry.getKey().name(), entry.getValue());
        }
        tag.put(key, willTag);
    }

    public void clearWill() {
        willMap.clear();
    }
}
