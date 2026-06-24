package com.breakinblocks.neovitae.common.loot;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Registry for NeoVitae loot functions.
 *
 * <p>26.1 removed the {@code LootItemFunctionType<T>} wrapper — the
 * {@code Registries.LOOT_FUNCTION_TYPE} registry now stores
 * {@code MapCodec<? extends LootItemFunction>} directly.
 */
public class NVLootFunctions {
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<MapCodec<? extends LootItemFunction>> LOOT_FUNCTIONS =
            (DeferredRegister<MapCodec<? extends LootItemFunction>>) (DeferredRegister<?>)
                    DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, NeoVitae.MODID);

    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<SetSpiritusRange>> SET_SPIRITUS_RANGE =
            LOOT_FUNCTIONS.register("set_spiritus_range", () -> SetSpiritusRange.CODEC);

    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<SetSpiritusFraction>> SET_SPIRITUS_FRACTION =
            LOOT_FUNCTIONS.register("set_spiritus_fraction", () -> SetSpiritusFraction.CODEC);

    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<SetSentientUpgrade>> SET_SENTIENT_UPGRADE =
            LOOT_FUNCTIONS.register("set_sentient_upgrade", () -> SetSentientUpgrade.CODEC);

    public static void register(IEventBus modEventBus) {
        LOOT_FUNCTIONS.register(modEventBus);
        NVLootEntries.ENTRY_TYPES.register(modEventBus);
        GlobalLootModifiers.register(modEventBus);
        NVLootConditions.register(modEventBus);
    }
}
