package com.breakinblocks.neovitae.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import com.breakinblocks.neovitae.NeoVitae;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AlternatorLinkData extends SavedData {
    public static final String ID = "alternator_links";

    public record Link(BlockPos receiver, BlockPos source) {
        public static final Codec<Link> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                BlockPos.CODEC.fieldOf("receiver").forGetter(Link::receiver),
                BlockPos.CODEC.fieldOf("source").forGetter(Link::source)
        ).apply(builder, Link::new));
    }

    public static final Codec<AlternatorLinkData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Link.CODEC.listOf().optionalFieldOf("links", List.of()).forGetter(AlternatorLinkData::links)
    ).apply(builder, AlternatorLinkData::new));

    public static final SavedDataType<AlternatorLinkData> TYPE =
            new SavedDataType<>(NeoVitae.rl(ID), AlternatorLinkData::new, CODEC, DataFixTypes.LEVEL);

    private final Map<BlockPos, BlockPos> receiverToSource = new LinkedHashMap<>();

    public AlternatorLinkData() {
    }

    public AlternatorLinkData(List<Link> links) {
        for (Link link : links) {
            receiverToSource.put(link.receiver(), link.source());
        }
    }

    public List<Link> links() {
        List<Link> out = new ArrayList<>(receiverToSource.size());
        for (Map.Entry<BlockPos, BlockPos> entry : receiverToSource.entrySet()) {
            out.add(new Link(entry.getKey(), entry.getValue()));
        }
        return out;
    }

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
}
