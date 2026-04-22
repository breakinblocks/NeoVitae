package com.breakinblocks.neovitae.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class ImperfectRitualActivatedTrigger extends SimpleCriterionTrigger<ImperfectRitualActivatedTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, String ritualId) {
        this.trigger(player, instance -> instance.matches(ritualId));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player,
                                  Optional<String> ritualId) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                Codec.STRING.optionalFieldOf("ritual_id").forGetter(TriggerInstance::ritualId)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(String activatedRitualId) {
            return ritualId.isEmpty() || ritualId.get().equals(activatedRitualId);
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return player;
        }
    }

    public static TriggerInstance any() {
        return new TriggerInstance(Optional.empty(), Optional.empty());
    }

    public static TriggerInstance forRitual(String ritualId) {
        return new TriggerInstance(Optional.empty(), Optional.of(ritualId));
    }
}
