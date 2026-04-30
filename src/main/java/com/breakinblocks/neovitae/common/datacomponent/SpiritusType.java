package com.breakinblocks.neovitae.common.datacomponent;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.function.IntFunction;

public enum SpiritusType implements StringRepresentable {

    RAW,
    RUINA,
    NIHILUM,
    INVICTUS,
    VINDICTA;

    public static final IntFunction<SpiritusType> BY_ID = ByIdMap.continuous(
            SpiritusType::ordinal,
            SpiritusType.values(),
            ByIdMap.OutOfBoundsStrategy.ZERO
    );

    public static final Codec<SpiritusType> CODEC = StringRepresentable.fromEnum(SpiritusType::values);
    public static final StreamCodec<ByteBuf, SpiritusType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, SpiritusType::ordinal);

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String toCapitalized() {
        return StringUtils.capitalize(this.getSerializedName());
    }
}
