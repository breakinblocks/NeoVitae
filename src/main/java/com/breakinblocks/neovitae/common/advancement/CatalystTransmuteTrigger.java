package com.breakinblocks.neovitae.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

import java.util.Optional;

public class CatalystTransmuteTrigger extends SimpleCriterionTrigger<CatalystTransmuteTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, SpiritusType resultAspect) {
        this.trigger(player, instance -> instance.matches(resultAspect));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player,
                                  Optional<SpiritusType> aspect) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                SpiritusType.CODEC.optionalFieldOf("aspect").forGetter(TriggerInstance::aspect)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(SpiritusType resultAspect) {
            return aspect.isEmpty() || aspect.get() == resultAspect;
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return player;
        }
    }

    public static TriggerInstance any() {
        return new TriggerInstance(Optional.empty(), Optional.empty());
    }

    public static TriggerInstance forAspect(SpiritusType aspect) {
        return new TriggerInstance(Optional.empty(), Optional.of(aspect));
    }
}
