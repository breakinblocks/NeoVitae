package com.breakinblocks.neovitae.common.sentient.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import com.breakinblocks.neovitae.common.sentient.SentientEntityEffect;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;

public record EntityBasedExp(Holder<SentientUpgrade> upgrade) implements SentientEntityEffect {
    public static final MapCodec<EntityBasedExp> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            SentientUpgrade.HOLDER_CODEC.fieldOf("upgrade").forGetter(EntityBasedExp::upgrade)
    ).apply(builder, EntityBasedExp::new));

    @Override
    public void apply(int upgradeLevel, Entity entity) {
        SentientHelper.applyExp((Player) entity, upgrade, 1);
    }

    @Override
    public MapCodec<? extends SentientEntityEffect> codec() {
        return CODEC;
    }
}
