package com.breakinblocks.neovitae.common.world;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import com.breakinblocks.neovitae.common.datacomponent.Anima;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NVSavedData extends SavedData {
    public static final String ID = "neovitae_anima";

    private Map<UUID, Anima> animaMap = new HashMap<>();

    public Anima getNetwork(UUID playerId) {
        if (!animaMap.containsKey(playerId))
            animaMap.put(playerId, Anima.newEmpty(playerId, this));

        return animaMap.get(playerId);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag animaData = new ListTag();
        for (Anima anima : animaMap.values()) {
            animaData.add(anima.toNBT());
        }

        tag.put("animaData", animaData);

        return tag;
    }

    public static NVSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        NVSavedData savedData = new NVSavedData();
        ListTag animaData = tag.getList("animaData", Tag.TAG_COMPOUND);

        for (int i = 0; i < animaData.size(); i++) {
            CompoundTag data = animaData.getCompound(i);
            Anima anima = Anima.fromNBT(data, savedData);
            savedData.animaMap.put(anima.getPlayerId(), anima);
        }

        return savedData;
    }
}
