package com.breakinblocks.neovitae.common.world;

import com.breakinblocks.neovitae.NeoVitae;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpawnerSuppressionData extends SavedData {
    public static final String ID = "spawner_suppression";

    public record Claim(BlockPos spawner, BlockPos master) {
        public static final Codec<Claim> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                BlockPos.CODEC.fieldOf("spawner").forGetter(Claim::spawner),
                BlockPos.CODEC.fieldOf("master").forGetter(Claim::master)
        ).apply(builder, Claim::new));
    }

    public static final Codec<SpawnerSuppressionData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Claim.CODEC.listOf().optionalFieldOf("suppressed", List.of()).forGetter(SpawnerSuppressionData::claims)
    ).apply(builder, SpawnerSuppressionData::new));

    public static final SavedDataType<SpawnerSuppressionData> TYPE =
            new SavedDataType<>(NeoVitae.rl(ID), SpawnerSuppressionData::new, CODEC, DataFixTypes.LEVEL);

    private final Map<BlockPos, BlockPos> suppressed = new LinkedHashMap<>();

    public SpawnerSuppressionData() {
    }

    public SpawnerSuppressionData(List<Claim> claims) {
        for (Claim claim : claims) {
            suppressed.put(claim.spawner(), claim.master());
        }
    }

    public List<Claim> claims() {
        List<Claim> out = new ArrayList<>(suppressed.size());
        for (Map.Entry<BlockPos, BlockPos> entry : suppressed.entrySet()) {
            out.add(new Claim(entry.getKey(), entry.getValue()));
        }
        return out;
    }

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
}
