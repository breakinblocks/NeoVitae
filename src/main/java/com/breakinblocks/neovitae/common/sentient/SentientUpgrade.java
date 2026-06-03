package com.breakinblocks.neovitae.common.sentient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.util.TriConsumer;
import com.breakinblocks.neovitae.common.sentient.effects.AttributeEffect;
import com.breakinblocks.neovitae.common.sentient.effects.ConditionalEffect;
import com.breakinblocks.neovitae.common.registry.NVRegistries;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public record SentientUpgrade(Levels levels, DataComponentMap effects) {
    public static final Codec<SentientUpgrade> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Levels.CODEC.fieldOf("levels").forGetter(SentientUpgrade::levels),
            SentientEffectComponents.CODEC.fieldOf("effects").forGetter(SentientUpgrade::effects)
    ).apply(builder, SentientUpgrade::new));

    public static final Codec<SentientUpgrade> CLIENT_CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Levels.CODEC.fieldOf("levels").forGetter(SentientUpgrade::levels),
            MapCodec.unitCodec(DataComponentMap.EMPTY).fieldOf("effects").forGetter(SentientUpgrade::effects)
    ).apply(builder, SentientUpgrade::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<SentientUpgrade>> HOLDER_STREAM_CODEC = ByteBufCodecs.holderRegistry(NVRegistries.Keys.SENTIENT_UPGRADES);

    public static String descriptionId(ResourceKey<SentientUpgrade> key) {
        return Util.makeDescriptionId("sentient_upgrade", key.identifier());
    }

    public static final Codec<Holder<SentientUpgrade>> HOLDER_CODEC = RegistryFixedCodec.create(NVRegistries.Keys.SENTIENT_UPGRADES);
	
	private float applyDamageFloatEffects(DataComponentType<List<ConditionalEffect<SentientValueEffect>>> type, int level, LivingEntity victim, DamageSource source, float initialValue) {
		LootContext context = SentientContextParamSets.damageBased(victim, source, level);
		float currentValue = initialValue;
		List<ConditionalEffect<SentientValueEffect>> effects = getEffects(type);
		for (ConditionalEffect<SentientValueEffect> conditionalEffect : effects) {
			if (conditionalEffect.matches(context)) {
				currentValue = conditionalEffect.effect().process(level, context, currentValue);
			}
		}
		return currentValue;
	}

	private float applyFloatEffects(DataComponentType<List<ConditionalEffect<SentientValueEffect>>> type, int level, Player player, float initialValue) {
		LootContext context = SentientContextParamSets.tick(player, level);
		float currentValue = initialValue;
		List<ConditionalEffect<SentientValueEffect>> effects = getEffects(type);
		for (ConditionalEffect<SentientValueEffect> conditionalEffect : effects) {
			if (conditionalEffect.matches(context)) {
				currentValue = conditionalEffect.effect().process(level, context, currentValue);
			}
		}
		return currentValue;
	}

	private void applyDamageReaction(DataComponentType<List<ConditionalEffect<SentientValueEffect>>> type, int level, LivingEntity victim, DamageSource source, float damage) {
		LootContext context = SentientContextParamSets.damageBased(victim, source, level);
		List<ConditionalEffect<SentientValueEffect>> effects = getEffects(type);
		for (ConditionalEffect<SentientValueEffect> conditionalEffect : effects) {
			if (conditionalEffect.matches(context)) {
				// We don't care about the return value, just that it processes.
				conditionalEffect.effect().process(level, context, damage);
			}
		}
	}

	public float modifyKnockback(Integer level, LivingEntity victim, DamageSource source, float initialValue) {
		return applyDamageFloatEffects(SentientEffectComponents.KNOCKBACK.get(), level, victim, source, initialValue);
	}

	public float modifyExperience(Integer level, Player player, float initialValue) {
		return applyFloatEffects(SentientEffectComponents.EXP_PICKUP.get(), level, player, initialValue);
	}

    public static <T> void applyEffects(List<ConditionalEffect<T>> effects, LootContext context, Consumer<T> applier) {
        for (ConditionalEffect<T> conditionaleffect : effects) {
            if (conditionaleffect.matches(context)) {
                applier.accept(conditionaleffect.effect());
            }
        }
    }

	public float modifyHealing(Integer level, Player player, float initialValue) {
		return applyFloatEffects(SentientEffectComponents.HEALING.get(), level, player, initialValue);
	}

	public float modifyDamageDealt(Integer level, LivingEntity victim, DamageSource source, float initialValue) {
		return applyDamageFloatEffects(SentientEffectComponents.DEALING_DAMAGE.get(), level, victim, source, initialValue);
	}

	public float modifyDamageTaken(Integer level, LivingEntity victim, DamageSource source, float initialValue) {
		return applyDamageFloatEffects(SentientEffectComponents.TAKING_DAMAGE.get(), level, victim, source, initialValue);
	}

    public void reactToDamageDealt(Integer level, LivingEntity victim, DamageSource source, float damage) {
		applyDamageReaction(SentientEffectComponents.DAMAGE_DEALT_EXP.get(), level, victim, source, damage);
    }

    public void reactToDamageTaken(Integer level, Player victim, DamageSource source, float damage) {
		applyDamageReaction(SentientEffectComponents.DAMAGE_TAKEN_EXP.get(), level, victim, source, damage);
    }

    public void blockBroken(Integer level, Player player, BlockState state) {
        applyEffects(
                getEffects(SentientEffectComponents.BREAK_BLOCK.get()),
                SentientContextParamSets.breakBlock(player, state, player.getItemInHand(InteractionHand.MAIN_HAND), level),
                effect -> effect.apply(level, player)
        );
    }

    public void tick(Integer level, Player player) {
        applyEffects(
                getEffects(SentientEffectComponents.TICK.get()),
                SentientContextParamSets.tick(player, level),
                effect -> effect.apply(level, player)
        );
    }

    public void modifyProjectile(Integer level, Player player, Projectile projectile) {
        applyEffects(
                getEffects(SentientEffectComponents.PROJECTILE_SHOT.get()),
                SentientContextParamSets.projectile(player, projectile, level),
                effect -> effect.apply(level, projectile)
        );
    }

    public void collectAttributes(Integer level, TriConsumer<Holder<Attribute>, AttributeModifier, EquipmentSlotGroup> consumer) {
        for (AttributeEffect effect : getEffects(SentientEffectComponents.ATTRIBUTES.get())) {
            effect.getModifier(level, consumer);
        }
    }

    public record Levels(TreeMap<Integer, Integer> expToLevel, Map<Integer, Integer> levelToCost) {
        private static final Codec<Integer> INT_STRING = Codec.STRING.comapFlatMap(str -> {
           try {
               return DataResult.success(Integer.valueOf(str));
           } catch (NumberFormatException ex) {
               return DataResult.error(() -> str + "is not an integer");
           }
        }, i -> i.toString());
        public static final Codec<Levels> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                Codec.unboundedMap(INT_STRING, Codec.INT).xmap(TreeMap::new, Function.identity()).fieldOf("exp_to_level").forGetter(Levels::expToLevel),
                Codec.unboundedMap(INT_STRING, Codec.INT).fieldOf("level_to_cost").forGetter(Levels::levelToCost)
        ).apply(builder, Levels::new));
    }

    public <T> List<T> getEffects(DataComponentType<List<T>> component) {
        return this.effects.getOrDefault(component, List.of());
    }

    public static class Builder {
        private final Map<DataComponentType<?>, List<?>> effectLists = new HashMap<>();
        private final DataComponentMap.Builder effectMapBuilder = DataComponentMap.builder();
        private final TreeMap<Integer, Integer> expToLevel = new TreeMap<>();
        private final Map<Integer, Integer> levelToCost = new HashMap<>();
        private int level = 1;

        public Builder level(int xpNeeded, int cost) {
            expToLevel.put(xpNeeded, level);
            levelToCost.put(level, cost);
            level++;
            return this;
        }

        public Builder withEffect(DataComponentType<List<AttributeEffect>> type, AttributeEffect effect) {
            getEffectsList(type).add(effect);
            return this;
        }

        public Builder withEffect(DataComponentType<Unit> type) {
            effectMapBuilder.set(type, Unit.INSTANCE);
            return this;
        }

        public <E> Builder withEffect(DataComponentType<List<ConditionalEffect<E>>> type, E effect, LootItemCondition.Builder condition) {
            getEffectsList(type).add(new ConditionalEffect<>(effect, Optional.of(condition.build())));
            return this;
        }

        public <E> Builder withEffect(DataComponentType<List<ConditionalEffect<E>>> type, E effect) {
            getEffectsList(type).add(new ConditionalEffect<>(effect, Optional.empty()));
            return this;
        }

        public SentientUpgrade build() {
            return new SentientUpgrade(new Levels(expToLevel, levelToCost), effectMapBuilder.build());
        }

        private <E> List<E> getEffectsList(DataComponentType<List<E>> componentType) {
            return (List<E>) this.effectLists.computeIfAbsent(componentType, p_346247_ -> {
                ArrayList<E> arraylist = new ArrayList<>();
                this.effectMapBuilder.set(componentType, arraylist);
                return arraylist;
            });
        }
    }
}
