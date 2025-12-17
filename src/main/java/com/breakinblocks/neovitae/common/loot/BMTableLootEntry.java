package com.breakinblocks.neovitae.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Consumer;

/**
 * Loot pool entry that references another loot table.
 * Allows composing loot tables together (e.g., including vanilla village loot in dungeon chests).
 */
public class BMTableLootEntry extends LootPoolSingletonContainer {
    public static final MapCodec<BMTableLootEntry> CODEC = RecordCodecBuilder.mapCodec(instance ->
            singletonFields(instance).and(
                    ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("name").forGetter(e -> e.table)
            ).apply(instance, BMTableLootEntry::new)
    );

    private final ResourceKey<LootTable> table;

    private BMTableLootEntry(int weight, int quality, List<LootItemCondition> conditions, List<LootItemFunction> functions, ResourceKey<LootTable> table) {
        super(weight, quality, conditions, functions);
        this.table = table;
    }

    @Override
    public LootPoolEntryType getType() {
        return BMLootEntries.LOOT_TABLE.get();
    }

    @Override
    protected void createItemStack(Consumer<ItemStack> stackConsumer, LootContext context) {
        context.addDynamicDrops(this.table.location(), stackConsumer);
    }

    public static Builder<?> builder(ResourceKey<LootTable> table) {
        return simpleBuilder((weight, quality, conditions, functions) ->
                new BMTableLootEntry(weight, quality, conditions, functions, table));
    }
}
