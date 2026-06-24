package com.breakinblocks.neovitae.common.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;

public class NVLootConditions {
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS =
            DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, NeoVitae.MODID);

    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> SENTIENT_COOLDOWN_READY =
            LOOT_CONDITIONS.register("sentient_cooldown_ready", () -> new LootItemConditionType(SentientCooldownCondition.CODEC));

    public static void register(IEventBus modEventBus) {
        LOOT_CONDITIONS.register(modEventBus);
    }
}
