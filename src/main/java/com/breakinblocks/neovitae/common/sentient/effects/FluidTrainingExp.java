package com.breakinblocks.neovitae.common.sentient.effects;

import com.breakinblocks.neovitae.common.sentient.SentientEntityEffect;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public record FluidTrainingExp(Holder<SentientUpgrade> upgrade, Activity activity) implements SentientEntityEffect {
    public static final MapCodec<FluidTrainingExp> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            SentientUpgrade.HOLDER_CODEC.fieldOf("upgrade").forGetter(FluidTrainingExp::upgrade),
            Activity.CODEC.fieldOf("activity").forGetter(FluidTrainingExp::activity)
    ).apply(builder, FluidTrainingExp::new));

    @Override
    public void apply(int upgradeLevel, Entity entity) {
        if (!(entity instanceof Player player)) return;
        boolean training = switch (activity) {
            case UNDERWATER -> player.isUnderWater();
            case SWIMMING -> player.isSwimming() && player.isInWater();
            case LAVA -> player.hasEffect(MobEffects.FIRE_RESISTANCE)
                    && player.level().getFluidState(player.blockPosition()).is(FluidTags.LAVA);
        };
        if (training) SentientHelper.applyExp(player, upgrade, 1);
    }

    @Override
    public MapCodec<? extends SentientEntityEffect> codec() {
        return CODEC;
    }

    public enum Activity {
        UNDERWATER, SWIMMING, LAVA;
        public static final Codec<Activity> CODEC = Codec.stringResolver(Activity::name, Activity::valueOf);
    }
}
