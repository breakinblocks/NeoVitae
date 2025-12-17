package com.breakinblocks.neovitae.common.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Registry for Blood Magic loot functions.
 */
public class BMLootFunctions {
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTIONS =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, NeoVitae.MODID);

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SetWillRange>> SET_WILL_RANGE =
            LOOT_FUNCTIONS.register("set_will_range", () -> new LootItemFunctionType<>(SetWillRange.CODEC));

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SetWillFraction>> SET_WILL_FRACTION =
            LOOT_FUNCTIONS.register("set_will_fraction", () -> new LootItemFunctionType<>(SetWillFraction.CODEC));

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SetLivingUpgrade>> SET_LIVING_UPGRADE =
            LOOT_FUNCTIONS.register("set_living_upgrade", () -> new LootItemFunctionType<>(SetLivingUpgrade.CODEC));

    public static void register(IEventBus modEventBus) {
        LOOT_FUNCTIONS.register(modEventBus);
        BMLootEntries.ENTRY_TYPES.register(modEventBus);
        GlobalLootModifiers.register(modEventBus);
    }
}
