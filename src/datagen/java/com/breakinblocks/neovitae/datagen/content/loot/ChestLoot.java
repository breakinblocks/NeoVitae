package com.breakinblocks.neovitae.datagen.content.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.loot.NVTableLootEntry;
import com.breakinblocks.neovitae.common.loot.SetSentientUpgrade;
import com.breakinblocks.neovitae.common.loot.SetSpiritusRange;
import com.breakinblocks.neovitae.datagen.content.SentientUpgrades;

import java.util.function.BiConsumer;

/**
 * Generates simple_dungeon and standard_dungeon chest loot tables.
 *
 * Note: mines/ loot tables are kept as manual JSON files in
 * src/main/resources/data/neovitae/loot_table/chests/mines/
 */
public class ChestLoot implements LootTableSubProvider {

    private final HolderLookup.Provider registries;

    public ChestLoot(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        // Simple dungeon loot tables
        generateSimpleDungeonBastion(output);
        generateSimpleDungeonCrypt(output);
        generateSimpleDungeonEntranceChest(output);
        generateSimpleDungeonFarmParts(output);
        generateSimpleDungeonFarmTools(output);
        generateSimpleDungeonFood(output);
        generateSimpleDungeonLibrary(output);
        generateSimpleDungeonNether(output);
        generateSimpleDungeonPotionIngredients(output);
        generateSimpleDungeonSimpleArmoury(output);
        generateSimpleDungeonSimpleBlacksmith(output);
        generateSimpleDungeonTestGems(output);

        // Standard dungeon loot tables
        generateStandardDungeonEntranceChest(output);
        generateStandardDungeonDecentAlchemy(output);
        generateStandardDungeonDecentLoot(output);
        generateStandardDungeonDecentSmithy(output);
        generateStandardDungeonEnchantingLoot(output);
        generateStandardDungeonGreatLoot(output);
        generateStandardDungeonMinesKey(output);
        generateStandardDungeonPoorLoot(output);
        generateStandardDungeonStrongAlchemy(output);

        // Foreman boss treasure (mine_entrance trial spawner)
        generateForemanTreasure(output);
    }

    private ResourceKey<LootTable> chestKey(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "chests/" + path));
    }

    private ResourceKey<LootTable> vanillaChestKey(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace("chests/" + path));
    }

    private LootPool.Builder standardKeyPool() {
        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(NVItems.STANDARD_KEY.get()))
                .when(LootItemRandomChanceCondition.randomChance(0.04f));
    }

    // ==================== SIMPLE DUNGEON LOOT TABLES ====================

    private void generateSimpleDungeonBastion(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("simple_dungeon/bastion"), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(NVTableLootEntry.builder(vanillaChestKey("bastion_treasure")))
            )
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(2.0f, 3.0f))
                .add(LootItem.lootTableItem(NVBlocks.WEAK_TAU.item().get()).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(NVItems.TAU_OIL.get()).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 7.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
            )
        );
    }

    private void generateSimpleDungeonCrypt(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("simple_dungeon/crypt"), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(NVTableLootEntry.builder(vanillaChestKey("simple_dungeon")))
            )
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(2.0f, 3.0f))
                .add(LootItem.lootTableItem(Items.BONE).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 8.0f))))
                .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 8.0f))))
                .add(LootItem.lootTableItem(NVBlocks.WEAK_TAU.item().get()).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(NVItems.TAU_OIL.get()).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 7.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
            )
        );
    }

    private void generateSimpleDungeonEntranceChest(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("simple_dungeon/entrance_chest"), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .name("keys")
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(NVItems.SIMPLE_KEY.get())
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
            )
            .withPool(LootPool.lootPool()
                .name("vanilla_dungeon")
                .setRolls(ConstantValue.exactly(1))
                .add(NVTableLootEntry.builder(vanillaChestKey("simple_dungeon")))
            )
        );
    }

    private void generateSimpleDungeonFarmParts(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("simple_dungeon/farm_parts"), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(NVTableLootEntry.builder(vanillaChestKey("village/village_plains_house")))
            )
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(2.0f, 3.0f))
                .add(LootItem.lootTableItem(Items.WHEAT_SEEDS).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 8.0f))))
                .add(LootItem.lootTableItem(Items.BEETROOT_SEEDS).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 8.0f))))
                .add(LootItem.lootTableItem(Items.PUMPKIN_SEEDS).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 8.0f))))
                .add(LootItem.lootTableItem(Items.MELON_SEEDS).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 8.0f))))
                .add(LootItem.lootTableItem(NVBlocks.WEAK_TAU.item().get()).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
            )
        );
    }

    private void generateSimpleDungeonFarmTools(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("simple_dungeon/farm_tools"), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(NVTableLootEntry.builder(vanillaChestKey("village/village_toolsmith")))
            )
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(2.0f, 3.0f))
                .add(LootItem.lootTableItem(Items.IRON_HOE).setWeight(10)
                    .apply(EnchantWithLevelsFunction.enchantWithLevels(registries, UniformGenerator.between(10.0f, 20.0f))))
                .add(LootItem.lootTableItem(Items.IRON_SHOVEL).setWeight(10)
                    .apply(EnchantWithLevelsFunction.enchantWithLevels(registries, UniformGenerator.between(10.0f, 20.0f))))
                .add(LootItem.lootTableItem(NVBlocks.WEAK_TAU.item().get()).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
            )
        );
    }

    private void generateSimpleDungeonFood(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("simple_dungeon/food"), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(NVTableLootEntry.builder(vanillaChestKey("shipwreck_supply")))
            )
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(2.0f, 3.0f))
                .add(LootItem.lootTableItem(NVBlocks.WEAK_TAU.item().get()).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(NVItems.TAU_OIL.get()).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 7.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
            )
        );
    }

    private void generateSimpleDungeonLibrary(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("simple_dungeon/library"), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(NVTableLootEntry.builder(vanillaChestKey("stronghold_library")))
            )
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(2.0f, 3.0f))
                .add(LootItem.lootTableItem(Items.BOOK).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 6.0f))))
                .add(LootItem.lootTableItem(Items.PAPER).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0f, 10.0f))))
                .add(LootItem.lootTableItem(NVBlocks.WEAK_TAU.item().get()).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
            )
        );
    }

    private void generateSimpleDungeonNether(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("simple_dungeon/nether"), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(NVTableLootEntry.builder(vanillaChestKey("nether_bridge")))
            )
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(2.0f, 3.0f))
                .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(Items.NETHER_WART).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 6.0f))))
                .add(LootItem.lootTableItem(NVBlocks.WEAK_TAU.item().get()).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
            )
        );
    }

    private void generateSimpleDungeonPotionIngredients(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("simple_dungeon/potion_ingredients"), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(NVTableLootEntry.builder(vanillaChestKey("igloo_chest")))
            )
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(2.0f, 4.0f))
                .add(LootItem.lootTableItem(Items.BLAZE_POWDER).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(Items.NETHER_WART).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 6.0f))))
                .add(LootItem.lootTableItem(Items.REDSTONE).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 8.0f))))
                .add(LootItem.lootTableItem(Items.GLOWSTONE_DUST).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 8.0f))))
                .add(LootItem.lootTableItem(Items.SPIDER_EYE).setWeight(8)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 4.0f))))
                .add(LootItem.lootTableItem(Items.FERMENTED_SPIDER_EYE).setWeight(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
                .add(LootItem.lootTableItem(NVBlocks.WEAK_TAU.item().get()).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
            )
        );
    }

    private void generateSimpleDungeonSimpleArmoury(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("simple_dungeon/simple_armoury"), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(NVTableLootEntry.builder(vanillaChestKey("village/village_armorer")))
            )
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(2.0f, 3.0f))
                .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(Items.LEATHER).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(NVBlocks.WEAK_TAU.item().get()).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
            )
        );
    }

    private void generateSimpleDungeonSimpleBlacksmith(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("simple_dungeon/simple_blacksmith"), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(NVTableLootEntry.builder(vanillaChestKey("village/village_weaponsmith")))
            )
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(2.0f, 3.0f))
                .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(Items.COAL).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0f, 10.0f))))
                .add(LootItem.lootTableItem(NVBlocks.WEAK_TAU.item().get()).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
            )
        );
    }

    private void generateSimpleDungeonTestGems(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("simple_dungeon/test_gems"), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(NVTableLootEntry.builder(vanillaChestKey("buried_treasure")))
            )
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(2.0f, 3.0f))
                .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
                .add(LootItem.lootTableItem(Items.EMERALD).setWeight(8)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(Items.LAPIS_LAZULI).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0f, 10.0f))))
                .add(LootItem.lootTableItem(NVBlocks.WEAK_TAU.item().get()).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
            )
        );
    }

    // ==================== STANDARD DUNGEON LOOT TABLES ====================

    private void generateStandardDungeonEntranceChest(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("standard_dungeon/entrance_chest"), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .name("keys")
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(NVItems.STANDARD_KEY.get())
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
            )
            .withPool(LootPool.lootPool()
                .name("vanilla_dungeon")
                .setRolls(ConstantValue.exactly(1))
                .add(NVTableLootEntry.builder(vanillaChestKey("simple_dungeon")))
            )
            .withPool(LootPool.lootPool()
                .name("bonus_tau")
                .setRolls(UniformGenerator.between(1.0f, 2.0f))
                .add(LootItem.lootTableItem(NVBlocks.WEAK_TAU.item().get()).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 7.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
            )
        );
    }

    private void generateStandardDungeonDecentAlchemy(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("standard_dungeon/decent_alchemy"), LootTable.lootTable()
            .withPool(standardKeyPool())
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(2.0f, 4.0f))
                .add(LootItem.lootTableItem(Items.BLAZE_POWDER).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 8.0f))))
                .add(LootItem.lootTableItem(Items.NETHER_WART).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0f, 10.0f))))
                .add(LootItem.lootTableItem(Items.REDSTONE).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(6.0f, 14.0f))))
                .add(LootItem.lootTableItem(Items.GLOWSTONE_DUST).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(6.0f, 14.0f))))
                .add(LootItem.lootTableItem(Items.GUNPOWDER).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0f, 10.0f))))
                .add(LootItem.lootTableItem(Items.FERMENTED_SPIDER_EYE).setWeight(8)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(Items.GHAST_TEAR).setWeight(3)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f))))
                .add(LootItem.lootTableItem(Items.MAGMA_CREAM).setWeight(8)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(NVBlocks.WEAK_TAU.item().get()).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 7.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(8)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 6.0f))))
                .add(LootItem.lootTableItem(NVItems.STANDARD_KEY.get()).setWeight(3).setQuality(2)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f))))
            )
        );
    }

    private void generateStandardDungeonDecentLoot(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("standard_dungeon/decent_loot"), LootTable.lootTable()
            .withPool(standardKeyPool())
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(NVTableLootEntry.builder(vanillaChestKey("stronghold_corridor")))
            )
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(2.0f, 4.0f))
                .add(LootItem.lootTableItem(Items.RAW_COPPER).setWeight(20).setQuality(-4)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(6.0f, 14.0f))))
                .add(LootItem.lootTableItem(Items.RAW_IRON).setWeight(18).setQuality(1)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0f, 10.0f))))
                .add(LootItem.lootTableItem(Items.RAW_GOLD).setWeight(14).setQuality(2)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 8.0f))))
                .add(LootItem.lootTableItem(NVItems.DEMONITE_RAW.get()).setWeight(10).setQuality(3)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 6.0f))))
                .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(5).setQuality(3)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 4.0f))))
                .add(LootItem.lootTableItem(Items.EMERALD).setWeight(4).setQuality(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 6.0f))))
                .add(LootItem.lootTableItem(NVItems.SULFUR.get()).setWeight(8).setQuality(1)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 8.0f))))
                .add(LootItem.lootTableItem(NVItems.STANDARD_KEY.get()).setWeight(4).setQuality(2)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f))))
                .add(LootItem.lootTableItem(NVBlocks.WEAK_TAU.item().get()).setWeight(12)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0f, 9.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(6).setQuality(3)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 6.0f))))
                .add(LootItem.lootTableItem(NVItems.ANIMUS_MOTE.get()).setWeight(3).setQuality(3)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 1.0f))))
            )
            .withPool(LootPool.lootPool()
                .name("mine_entrance_key")
                .setRolls(ConstantValue.exactly(1))
                .when(LootItemRandomChanceCondition.randomChance(0.07f))
                .add(LootItem.lootTableItem(NVItems.MINE_ENTRANCE_KEY.get()))
            )
        );
    }

    private void generateStandardDungeonDecentSmithy(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("standard_dungeon/decent_smithy"), LootTable.lootTable()
            .withPool(standardKeyPool())
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(2.0f, 4.0f))
                .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 8.0f))))
                .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(Items.COAL).setWeight(20)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(6.0f, 14.0f))))
                .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(4).setQuality(4)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
                .add(LootItem.lootTableItem(Items.IRON_PICKAXE).setWeight(6)
                    .apply(EnchantWithLevelsFunction.enchantWithLevels(registries, UniformGenerator.between(15.0f, 30.0f))))
                .add(LootItem.lootTableItem(Items.IRON_SWORD).setWeight(6)
                    .apply(EnchantWithLevelsFunction.enchantWithLevels(registries, UniformGenerator.between(15.0f, 30.0f))))
                .add(LootItem.lootTableItem(NVItems.STANDARD_KEY.get()).setWeight(3).setQuality(2)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f))))
                .add(LootItem.lootTableItem(NVBlocks.WEAK_TAU.item().get()).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 7.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(8)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
            )
        );
    }

    private void generateStandardDungeonEnchantingLoot(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("standard_dungeon/enchanting_loot"), LootTable.lootTable()
            .withPool(standardKeyPool())
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(2.0f, 4.0f))
                .add(LootItem.lootTableItem(Items.BOOK).setWeight(10)
                    .apply(EnchantWithLevelsFunction.enchantWithLevels(registries, UniformGenerator.between(20.0f, 35.0f))))
                .add(LootItem.lootTableItem(Items.LAPIS_LAZULI).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(8.0f, 20.0f))))
                .add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE).setWeight(8)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 8.0f))))
                .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(4).setQuality(4)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
                .add(LootItem.lootTableItem(NVItems.STANDARD_KEY.get()).setWeight(3).setQuality(2)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f))))
                .add(LootItem.lootTableItem(NVBlocks.WEAK_TAU.item().get()).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 7.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(8)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
            )
        );
    }

    private void generateStandardDungeonGreatLoot(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("standard_dungeon/great_loot"), LootTable.lootTable()
            .withPool(standardKeyPool())
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(NVTableLootEntry.builder(vanillaChestKey("end_city_treasure")).setWeight(1))
                .add(NVTableLootEntry.builder(vanillaChestKey("stronghold_crossing")).setWeight(2))
                .add(NVTableLootEntry.builder(vanillaChestKey("buried_treasure")).setWeight(2))
            )
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(3.0f, 5.0f))
                .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(10).setQuality(3)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 6.0f))))
                .add(LootItem.lootTableItem(Items.EMERALD).setWeight(8).setQuality(4)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 8.0f))))
                .add(LootItem.lootTableItem(Items.NETHERITE_SCRAP).setWeight(3).setQuality(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
                .add(LootItem.lootTableItem(NVItems.DEMONITE_RAW.get()).setWeight(8).setQuality(3)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 8.0f))))
                .add(LootItem.lootTableItem(NVItems.HELLFORGED_INGOT.get()).setWeight(5).setQuality(4)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 4.0f))))
                .add(LootItem.lootTableItem(Items.DIAMOND_PICKAXE).setWeight(4).setQuality(4)
                    .apply(EnchantWithLevelsFunction.enchantWithLevels(registries, UniformGenerator.between(25.0f, 40.0f))))
                .add(LootItem.lootTableItem(Items.DIAMOND_SWORD).setWeight(4).setQuality(4)
                    .apply(EnchantWithLevelsFunction.enchantWithLevels(registries, UniformGenerator.between(25.0f, 40.0f))))
                .add(LootItem.lootTableItem(Items.DIAMOND_CHESTPLATE).setWeight(3).setQuality(4)
                    .apply(EnchantWithLevelsFunction.enchantWithLevels(registries, UniformGenerator.between(25.0f, 35.0f))))
                .add(LootItem.lootTableItem(NVItems.STANDARD_KEY.get()).setWeight(3).setQuality(3)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(12)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0f, 9.0f))))
                .add(LootItem.lootTableItem(NVItems.MONSTER_SOUL_RAW.get()).setWeight(8)
                    .apply(SetSpiritusRange.builder(UniformGenerator.between(30.0f, 60.0f))))
                .add(LootItem.lootTableItem(NVItems.ANIMUS_MOTE.get()).setWeight(5).setQuality(4)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f))))
            )
            .withPool(LootPool.lootPool()
                .name("mine_entrance_key")
                .setRolls(ConstantValue.exactly(1))
                .when(LootItemRandomChanceCondition.randomChance(0.07f))
                .add(LootItem.lootTableItem(NVItems.MINE_ENTRANCE_KEY.get()))
            )
        );
    }

    private void generateStandardDungeonMinesKey(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("standard_dungeon/mines_key"), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(2.0f, 4.0f))
                .add(LootItem.lootTableItem(Items.RAW_IRON).setWeight(15)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(5.0f, 12.0f))))
                .add(LootItem.lootTableItem(Items.RAW_GOLD).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 8.0f))))
                .add(LootItem.lootTableItem(Items.RAW_COPPER).setWeight(18)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(8.0f, 18.0f))))
                .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(5).setQuality(3)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
                .add(LootItem.lootTableItem(NVItems.MINE_KEY.get()).setWeight(4).setQuality(3)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f))))
                .add(LootItem.lootTableItem(NVBlocks.WEAK_TAU.item().get()).setWeight(12)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0f, 9.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(6).setQuality(3)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 6.0f))))
            )
        );
    }

    private void generateStandardDungeonPoorLoot(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("standard_dungeon/poor_loot"), LootTable.lootTable()
            .withPool(standardKeyPool())
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(2.0f, 4.0f))
                .add(LootItem.lootTableItem(Items.RAW_COPPER).setWeight(22).setQuality(-4)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0f, 10.0f))))
                .add(LootItem.lootTableItem(Items.RAW_IRON).setWeight(18).setQuality(1)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 7.0f))))
                .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(10).setQuality(1)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 4.0f))))
                .add(LootItem.lootTableItem(Items.COPPER_INGOT).setWeight(14).setQuality(-2)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 8.0f))))
                .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(3).setQuality(3)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
                .add(LootItem.lootTableItem(Items.COAL).setWeight(15).setQuality(-2)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0f, 10.0f))))
                .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(8).setQuality(-2)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0f, 9.0f))))
                .add(LootItem.lootTableItem(Items.GUNPOWDER).setWeight(8).setQuality(-2)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0f, 8.0f))))
                .add(LootItem.lootTableItem(NVItems.SULFUR.get()).setWeight(6).setQuality(1)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 7.0f))))
                .add(LootItem.lootTableItem(NVItems.STANDARD_KEY.get()).setWeight(3).setQuality(2)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(3).setQuality(3)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 6.0f))))
                .add(LootItem.lootTableItem(NVBlocks.WEAK_TAU.item().get()).setWeight(5).setQuality(3)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 6.0f))))
            )
            .withPool(LootPool.lootPool()
                .name("mine_entrance_key")
                .setRolls(ConstantValue.exactly(1))
                .when(LootItemRandomChanceCondition.randomChance(0.07f))
                .add(LootItem.lootTableItem(NVItems.MINE_ENTRANCE_KEY.get()))
            )
        );
    }

    private void generateStandardDungeonStrongAlchemy(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("standard_dungeon/strong_alchemy"), LootTable.lootTable()
            .withPool(standardKeyPool())
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(3.0f, 5.0f))
                .add(LootItem.lootTableItem(Items.BLAZE_POWDER).setWeight(12)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(5.0f, 12.0f))))
                .add(LootItem.lootTableItem(Items.NETHER_WART).setWeight(12)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(6.0f, 14.0f))))
                .add(LootItem.lootTableItem(Items.GHAST_TEAR).setWeight(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f))))
                .add(LootItem.lootTableItem(Items.PHANTOM_MEMBRANE).setWeight(6)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(Items.DRAGON_BREATH).setWeight(3).setQuality(4)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f))))
                .add(LootItem.lootTableItem(NVItems.REAGENT_BINDING.get()).setWeight(8)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(NVItems.REAGENT_WATER.get()).setWeight(8)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(NVItems.REAGENT_LAVA.get()).setWeight(8)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))))
                .add(LootItem.lootTableItem(NVItems.STANDARD_KEY.get()).setWeight(3).setQuality(3)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f))))
                .add(LootItem.lootTableItem(NVBlocks.STRONG_TAU.item().get()).setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0f, 9.0f))))
            )
        );
    }

    private void generateForemanTreasure(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(chestKey("foreman/treasure"), LootTable.lootTable()
            .withPool(standardKeyPool())
            // End-city-tier base loot via the vanilla end_city_treasure table.
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(2.0f, 4.0f))
                .add(NVTableLootEntry.builder(vanillaChestKey("end_city_treasure")).setWeight(1))
            )
            // 10% nether star: weight 1 against weight-9 empty entry over a single roll.
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(EmptyLootItem.emptyItem().setWeight(9))
                .add(LootItem.lootTableItem(Items.NETHER_STAR).setWeight(1).setQuality(10)
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
            )
            // Spiritus + neovitae trophies for the boss kill.
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(1.0f, 2.0f))
                .add(LootItem.lootTableItem(NVItems.MONSTER_SOUL_INVICTUS.get()).setWeight(8)
                    .apply(SetSpiritusRange.builder(UniformGenerator.between(120.0f, 240.0f))))
                .add(LootItem.lootTableItem(NVItems.MONSTER_SOUL_RUINA.get()).setWeight(8)
                    .apply(SetSpiritusRange.builder(UniformGenerator.between(120.0f, 240.0f))))
                .add(LootItem.lootTableItem(NVItems.HELLFORGED_INGOT.get()).setWeight(6).setQuality(5)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 4.0f))))
                .add(LootItem.lootTableItem(NVItems.UPGRADE_TOME.get()).setWeight(4).setQuality(6)
                    .apply(SetSentientUpgrade.withKeysRange(registries, 1f, 3f,
                        SentientUpgrades.CURIOS_SOCKET, SentientUpgrades.ELYTRA, SentientUpgrades.GILDED)))
            )
        );
    }
}
