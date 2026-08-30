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

public class AlternatorLinkData extends SavedData {
    public static final String ID = "neovitae_alternator_links";

    private final Map<BlockPos, BlockPos> receiverToSource = new LinkedHashMap<>();

    public Map<BlockPos, BlockPos> entries() {
        return receiverToSource;
    }

    public void put(BlockPos receiver, BlockPos source) {
        if (!source.equals(receiverToSource.put(receiver.immutable(), source.immutable()))) {
            setDirty();
        }
    }

    public void drop(BlockPos receiver) {
        if (receiverToSource.remove(receiver) != null) {
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (Map.Entry<BlockPos, BlockPos> entry : receiverToSource.entrySet()) {
            CompoundTag element = new CompoundTag();
            element.put("receiver", NbtUtils.writeBlockPos(entry.getKey()));
            element.put("source", NbtUtils.writeBlockPos(entry.getValue()));
            list.add(element);
        }
        tag.put("links", list);
        return tag;
    }

    public static AlternatorLinkData load(CompoundTag tag, HolderLookup.Provider registries) {
        AlternatorLinkData data = new AlternatorLinkData();
        ListTag list = tag.getList("links", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag element = list.getCompound(i);
            BlockPos receiver = NbtUtils.readBlockPos(element, "receiver").orElse(null);
            BlockPos source = NbtUtils.readBlockPos(element, "source").orElse(null);
            if (receiver != null && source != null) {
                data.receiverToSource.put(receiver, source);
            }
        }
        return data;
    }
}
