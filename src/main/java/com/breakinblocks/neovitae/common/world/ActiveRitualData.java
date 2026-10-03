package com.breakinblocks.neovitae.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import com.breakinblocks.neovitae.NeoVitae;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ActiveRitualData extends SavedData {
    public static final String ID = "active_rituals";

    public record Entry(UUID owner, Identifier ritual) {}

    private record Tracked(GlobalPos pos, UUID owner, Identifier ritual) {
        static final Codec<Tracked> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                GlobalPos.CODEC.fieldOf("pos").forGetter(Tracked::pos),
                UUIDUtil.CODEC.fieldOf("owner").forGetter(Tracked::owner),
                Identifier.CODEC.fieldOf("ritual").forGetter(Tracked::ritual)
        ).apply(builder, Tracked::new));
    }

    public static final Codec<ActiveRitualData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Tracked.CODEC.listOf().optionalFieldOf("rituals", List.of()).forGetter(ActiveRitualData::tracked)
    ).apply(builder, ActiveRitualData::new));

    public static final SavedDataType<ActiveRitualData> TYPE =
            new SavedDataType<>(NeoVitae.rl(ID), ActiveRitualData::new, CODEC, DataFixTypes.LEVEL);

    private final Map<GlobalPos, Entry> entries = new LinkedHashMap<>();

    public ActiveRitualData() {
    }

    private ActiveRitualData(List<Tracked> tracked) {
        for (Tracked t : tracked) {
            entries.put(t.pos(), new Entry(t.owner(), t.ritual()));
        }
    }

    private List<Tracked> tracked() {
        List<Tracked> out = new ArrayList<>(entries.size());
        for (Map.Entry<GlobalPos, Entry> e : entries.entrySet()) {
            out.add(new Tracked(e.getKey(), e.getValue().owner(), e.getValue().ritual()));
        }
        return out;
    }

    public Map<GlobalPos, Entry> entries() {
        return entries;
    }

    public void put(GlobalPos pos, Entry entry) {
        if (!entry.equals(entries.put(pos, entry))) {
            setDirty();
        }
    }

    public void remove(GlobalPos pos) {
        if (entries.remove(pos) != null) {
            setDirty();
        }
    }
}
