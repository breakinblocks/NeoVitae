package com.breakinblocks.neovitae.spiritus;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

import java.util.EnumMap;
import java.util.Map;

public class SpiritusHolder {
    private final EnumMap<SpiritusType, Double> spiritusMap = new EnumMap<>(SpiritusType.class);

    public double addSpiritus(SpiritusType type, double amount, double max) {
        double current = spiritusMap.getOrDefault(type, 0.0);
        double added = Math.min(max - current, amount);
        addSpiritus(type, added);
        return added;
    }

    public void addSpiritus(SpiritusType type, double amount) {
        double current = spiritusMap.getOrDefault(type, 0.0);
        spiritusMap.put(type, current + amount);
    }

    public double drainSpiritus(SpiritusType type, double amount) {
        double current = spiritusMap.getOrDefault(type, 0.0);
        if (current <= 0) {
            return 0;
        }

        double reduced = Math.min(current, amount);
        double remaining = current - reduced;

        if (remaining <= 0) {
            spiritusMap.remove(type);
        } else {
            spiritusMap.put(type, remaining);
        }

        return reduced;
    }

    public double getSpiritus(SpiritusType type) {
        return spiritusMap.getOrDefault(type, 0.0);
    }

    public void readFromNBT(CompoundTag tag, String key) {
        CompoundTag spiritusTag = tag.getCompound(key);
        spiritusMap.clear();

        for (SpiritusType type : SpiritusType.values()) {
            String nbtKey = "EnumWill" + type.name();
            if (spiritusTag.contains(nbtKey)) {
                double amount = spiritusTag.getDouble(nbtKey);
                if (amount > 0) {
                    spiritusMap.put(type, amount);
                }
            }
        }
    }

    public void writeToNBT(CompoundTag tag, String key) {
        CompoundTag spiritusTag = new CompoundTag();
        for (Map.Entry<SpiritusType, Double> entry : spiritusMap.entrySet()) {
            spiritusTag.putDouble("EnumWill" + entry.getKey().name(), entry.getValue());
        }
        tag.put(key, spiritusTag);
    }

    public void clearSpiritus() {
        spiritusMap.clear();
    }
}
