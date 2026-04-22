package com.breakinblocks.neovitae.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.Anima;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NVSavedData extends SavedData {
    public static final String ID = "anima";

    public static final Codec<NVSavedData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Anima.CODEC.listOf().fieldOf("animaData").forGetter(NVSavedData::animaList)
    ).apply(builder, NVSavedData::fromList));

    public static final SavedDataType<NVSavedData> TYPE = new SavedDataType<>(NeoVitae.rl(ID), NVSavedData::new, CODEC, DataFixTypes.LEVEL);

    private Map<UUID, Anima> animaMap = new HashMap<>();

    public NVSavedData() {
    }

    private static NVSavedData fromList(List<Anima> entries) {
        NVSavedData data = new NVSavedData();
        for (Anima anima : entries) {
            anima.rebind(data);
            data.animaMap.put(anima.getPlayerId(), anima);
        }
        return data;
    }

    private List<Anima> animaList() {
        return List.copyOf(animaMap.values());
    }

    public Anima getNetwork(UUID playerId) {
        if (!animaMap.containsKey(playerId))
            animaMap.put(playerId, Anima.newEmpty(playerId, this));

        return animaMap.get(playerId);
    }
}
