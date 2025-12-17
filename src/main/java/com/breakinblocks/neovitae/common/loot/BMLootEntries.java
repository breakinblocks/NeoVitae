package com.breakinblocks.neovitae.common.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Registry for custom loot pool entry types.
 */
public class BMLootEntries {
    public static final DeferredRegister<LootPoolEntryType> ENTRY_TYPES =
            DeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE, NeoVitae.MODID);

    public static final DeferredHolder<LootPoolEntryType, LootPoolEntryType> LOOT_TABLE =
            ENTRY_TYPES.register("loot_table", () -> new LootPoolEntryType(BMTableLootEntry.CODEC));
}
