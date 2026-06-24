package com.breakinblocks.neovitae.common.loot;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;

public class NVLootConditions {
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<MapCodec<? extends LootItemCondition>> LOOT_CONDITIONS =
            (DeferredRegister<MapCodec<? extends LootItemCondition>>) (DeferredRegister<?>)
                    DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, NeoVitae.MODID);

    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<SentientCooldownCondition>> SENTIENT_COOLDOWN_READY =
            LOOT_CONDITIONS.register("sentient_cooldown_ready", () -> SentientCooldownCondition.CODEC);

    public static void register(IEventBus modEventBus) {
        LOOT_CONDITIONS.register(modEventBus);
    }
}
