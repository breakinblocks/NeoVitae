package com.breakinblocks.neovitae.common.loot;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Registry for custom loot pool entry types.
 *
 * <p>26.1 removed the {@code LootPoolEntryType} wrapper — the
 * {@code Registries.LOOT_POOL_ENTRY_TYPE} registry now stores
 * {@code MapCodec<? extends LootPoolEntryContainer>} directly.
 */
public class NVLootEntries {
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<MapCodec<? extends LootPoolEntryContainer>> ENTRY_TYPES =
            (DeferredRegister<MapCodec<? extends LootPoolEntryContainer>>) (DeferredRegister<?>)
                    DeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE, NeoVitae.MODID);

    public static final DeferredHolder<MapCodec<? extends LootPoolEntryContainer>, MapCodec<NVTableLootEntry>> LOOT_TABLE =
            ENTRY_TYPES.register("loot_table", () -> NVTableLootEntry.CODEC);
}
