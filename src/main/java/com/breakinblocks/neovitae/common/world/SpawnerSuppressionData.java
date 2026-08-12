package com.breakinblocks.neovitae.common.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.LinkedHashMap;
import java.util.Map;

public class SpawnerSuppressionData extends SavedData {
    public static final String ID = "neovitae_spawner_suppression";

    private final Map<BlockPos, BlockPos> suppressed = new LinkedHashMap<>();

    public Map<BlockPos, BlockPos> entries() {
        return suppressed;
    }

    public void put(BlockPos spawner, BlockPos master) {
        if (!master.equals(suppressed.put(spawner.immutable(), master.immutable()))) {
            setDirty();
        }
    }

    public void drop(BlockPos spawner) {
        if (suppressed.remove(spawner) != null) {
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (Map.Entry<BlockPos, BlockPos> entry : suppressed.entrySet()) {
            CompoundTag element = new CompoundTag();
            element.put("spawner", NbtUtils.writeBlockPos(entry.getKey()));
            element.put("master", NbtUtils.writeBlockPos(entry.getValue()));
            list.add(element);
        }
        tag.put("suppressed", list);
        return tag;
    }

    public static SpawnerSuppressionData load(CompoundTag tag, HolderLookup.Provider registries) {
        SpawnerSuppressionData data = new SpawnerSuppressionData();
        ListTag list = tag.getList("suppressed", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag element = list.getCompound(i);
            BlockPos spawner = NbtUtils.readBlockPos(element, "spawner").orElse(null);
            BlockPos master = NbtUtils.readBlockPos(element, "master").orElse(null);
            if (spawner != null && master != null) {
                data.suppressed.put(spawner, master);
            }
        }
        return data;
    }
}
