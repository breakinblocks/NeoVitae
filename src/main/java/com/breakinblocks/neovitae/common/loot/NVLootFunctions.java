package com.breakinblocks.neovitae.common.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Registry for NeoVitae loot functions.
 */
public class NVLootFunctions {
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTIONS =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, NeoVitae.MODID);

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SetSpiritusRange>> SET_SPIRITUS_RANGE =
            LOOT_FUNCTIONS.register("set_spiritus_range", () -> new LootItemFunctionType<>(SetSpiritusRange.CODEC));

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SetSpiritusFraction>> SET_SPIRITUS_FRACTION =
            LOOT_FUNCTIONS.register("set_spiritus_fraction", () -> new LootItemFunctionType<>(SetSpiritusFraction.CODEC));

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SetSentientUpgrade>> SET_SENTIENT_UPGRADE =
            LOOT_FUNCTIONS.register("set_sentient_upgrade", () -> new LootItemFunctionType<>(SetSentientUpgrade.CODEC));

    public static void register(IEventBus modEventBus) {
        LOOT_FUNCTIONS.register(modEventBus);
        NVLootEntries.ENTRY_TYPES.register(modEventBus);
        GlobalLootModifiers.register(modEventBus);
    }
}
