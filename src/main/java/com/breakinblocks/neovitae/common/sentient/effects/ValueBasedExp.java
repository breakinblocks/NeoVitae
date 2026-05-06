package com.breakinblocks.neovitae.common.sentient.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.common.sentient.SentientValueEffect;

public record ValueBasedExp(Holder<SentientUpgrade> upgrade, boolean victim) implements SentientValueEffect {
    public static final MapCodec<ValueBasedExp> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            SentientUpgrade.HOLDER_CODEC.fieldOf("upgrade").forGetter(ValueBasedExp::upgrade),
            Codec.BOOL.fieldOf("victim").forGetter(ValueBasedExp::victim)
    ).apply(builder, ValueBasedExp::new));

    public static final boolean THIS_ENTITY = true;
    public static final boolean ATTACKER = false;

    @Override
    public float process(int level, LootContext lootContext, float value) {
        Player player = (Player) lootContext.getOptionalParameter(victim ? LootContextParams.THIS_ENTITY : LootContextParams.ATTACKING_ENTITY);
        SentientHelper.applyExp(player, upgrade, value);
        return value;
    }

    @Override
    public MapCodec<? extends SentientValueEffect> codec() {
        return CODEC;
    }
}
