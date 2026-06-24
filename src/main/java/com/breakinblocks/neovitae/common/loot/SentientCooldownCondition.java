package com.breakinblocks.neovitae.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;

import java.util.Map;
import java.util.Set;

public record SentientCooldownCondition(ResourceLocation id) implements LootItemCondition {
    public static final MapCodec<SentientCooldownCondition> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(SentientCooldownCondition::id)
    ).apply(builder, SentientCooldownCondition::new));

    @Override
    public LootItemConditionType getType() {
        return NVLootConditions.SENTIENT_COOLDOWN_READY.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.THIS_ENTITY);
    }

    @Override
    public boolean test(LootContext context) {
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity == null) return false;
        Map<ResourceLocation, Double> data = entity.getData(NVDataAttachments.SENTIENT_ADDITIONAL.get());
        Double cooldown = data.get(id);
        return cooldown != null && cooldown == 0.0;
    }

    public static LootItemCondition.Builder ready(ResourceLocation id) {
        return () -> new SentientCooldownCondition(id);
    }
}
