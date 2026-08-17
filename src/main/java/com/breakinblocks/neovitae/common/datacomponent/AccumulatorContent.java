package com.breakinblocks.neovitae.common.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record AccumulatorContent(Optional<SpiritusType> type, double stored, boolean locked) {

    public static final AccumulatorContent EMPTY = new AccumulatorContent(Optional.empty(), 0, false);

    public static final Codec<AccumulatorContent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SpiritusType.CODEC.optionalFieldOf("type").forGetter(AccumulatorContent::type),
            Codec.DOUBLE.optionalFieldOf("stored", 0d).forGetter(AccumulatorContent::stored),
            Codec.BOOL.optionalFieldOf("locked", false).forGetter(AccumulatorContent::locked)
    ).apply(instance, AccumulatorContent::new));

    public static final StreamCodec<ByteBuf, AccumulatorContent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(SpiritusType.STREAM_CODEC), AccumulatorContent::type,
            ByteBufCodecs.DOUBLE, AccumulatorContent::stored,
            ByteBufCodecs.BOOL, AccumulatorContent::locked,
            AccumulatorContent::new);

    public static AccumulatorContent of(@Nullable SpiritusType type, double stored, boolean locked) {
        return new AccumulatorContent(Optional.ofNullable(type), stored, locked);
    }

    @Nullable
    public SpiritusType typeOrNull() {
        return type.orElse(null);
    }

    public boolean isEmpty() {
        return type.isEmpty() && stored <= 0 && !locked;
    }
}
