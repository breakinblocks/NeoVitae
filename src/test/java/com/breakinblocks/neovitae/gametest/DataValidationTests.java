package com.breakinblocks.neovitae.gametest;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.datamap.RitualStats;
import com.breakinblocks.neovitae.common.datamap.RoutingNodeStats;
import com.breakinblocks.neovitae.common.item.soul.SpiritusEssenceItem;
import com.breakinblocks.neovitae.common.network.StreamPayload;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DataValidationTests {

    private DataValidationTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("data/shipped_ritual_stats_do_not_consume_books", 30, helper -> {
            helper.runAfterDelay(1, () -> {
                Ritual ritual = RitualRegistry.getRitual("enchanted_vitae");
                if (ritual == null) {
                    helper.fail("Ritual of Enchanted Vitae is not registered");
                    return;
                }
                RitualStats stats = RitualRegistry.getStats(ritual);
                if (stats == null) {
                    helper.fail("Ritual of Enchanted Vitae has no stats in the datamap");
                    return;
                }
                if (stats.consumeBooks()) {
                    helper.fail("consume_books must ship disabled; packs opt in, we do not ship it on");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("data/ritual_stats_json_without_per_operation_still_loads", 30, helper -> {
            String legacy = "{\"activation_cost\":20000,\"refresh_cost\":10000,\"refresh_time\":40,\"crystal_level\":1}";
            RitualStats decoded = RitualStats.CODEC
                    .parse(JsonOps.INSTANCE, JsonParser.parseString(legacy))
                    .resultOrPartial(err -> helper.fail("Legacy ritual stats JSON failed to decode: " + err))
                    .orElse(null);
            if (decoded == null) {
                return;
            }
            if (decoded.perOperation()) {
                helper.fail("A datapack written before per_operation existed must default to false");
                return;
            }
            if (decoded.consumeBooks()) {
                helper.fail("A datapack written before consume_books existed must default to false");
                return;
            }
            if (decoded.activationCost() != 20000 || decoded.refreshCost() != 10000
                    || decoded.refreshTime() != 40 || decoded.crystalLevel() != 1) {
                helper.fail("Legacy fields were not preserved: " + decoded);
                return;
            }

            RitualStats flagged = RitualStats.CODEC
                    .parse(JsonOps.INSTANCE, JsonParser.parseString(
                            "{\"activation_cost\":1,\"refresh_cost\":2,\"per_operation\":true}"))
                    .resultOrPartial(err -> helper.fail("per_operation JSON failed to decode: " + err))
                    .orElse(null);
            if (flagged == null) {
                return;
            }
            if (!flagged.perOperation()) {
                helper.fail("per_operation:true should decode as true");
                return;
            }

            RitualStats consuming = RitualStats.CODEC
                    .parse(JsonOps.INSTANCE, JsonParser.parseString(
                            "{\"activation_cost\":1,\"refresh_cost\":2,\"consume_books\":true}"))
                    .resultOrPartial(err -> helper.fail("consume_books JSON failed to decode: " + err))
                    .orElse(null);
            if (consuming == null) {
                return;
            }
            if (!consuming.consumeBooks()) {
                helper.fail("consume_books:true should decode as true");
                return;
            }
            if (consuming.perOperation()) {
                helper.fail("consume_books should not disturb per_operation");
                return;
            }

            RitualStats viaOldConstructor = new RitualStats(1, 2, 20, 0, java.util.Map.of(), true,
                    java.util.Optional.empty());
            if (viaOldConstructor.perOperation()) {
                helper.fail("The pre-existing constructor shape should default per_operation to false");
                return;
            }
            helper.succeed();
        });

        r.add("data/all_rituals_have_components", 30, helper -> {
            helper.runAfterDelay(1, () -> {
                Collection<Ritual> rituals = RitualRegistry.getAllRituals();
                if (rituals.isEmpty()) {
                    helper.fail("No rituals registered");
                    return;
                }

                for (Ritual ritual : rituals) {
                    List<RitualComponent> components = new ArrayList<>();
                    ritual.gatherComponents(components::add);
                    if (components.isEmpty()) {
                        helper.fail("Ritual " + RitualRegistry.getId(ritual) + " has no components");
                        return;
                    }
                }
                helper.succeed();
            });
        });

        r.add("data/all_rituals_have_translation_keys", 30, helper -> {
            helper.runAfterDelay(1, () -> {
                for (Ritual ritual : RitualRegistry.getAllRituals()) {
                    String key = ritual.getTranslationKey();
                    if (key == null || key.isEmpty()) {
                        helper.fail("Ritual " + RitualRegistry.getId(ritual) + " has no translation key");
                        return;
                    }
                }
                helper.succeed();
            });
        });

        r.add("data/master_node_has_routing_stats", 30, helper -> {
            helper.runAfterDelay(1, () -> {
                RoutingNodeStats stats = BuiltInRegistries.BLOCK
                        .wrapAsHolder(NVBlocks.MASTER_ROUTING_NODE.block().get())
                        .getData(NVDataMaps.ROUTING_NODE_STATS);

                if (stats == null) {
                    helper.fail("Master routing node should have routing_node_stats datamap entry");
                    return;
                }
                if (stats.getBaseItemTransfer() <= 0) {
                    helper.fail("Master node base_item_transfer should be > 0");
                    return;
                }
                if (stats.getBaseFluidTransfer() <= 0) {
                    helper.fail("Master node base_fluid_transfer should be > 0");
                    return;
                }
                if (stats.getBaseEnergyTransfer() <= 0) {
                    helper.fail("Master node base_energy_transfer should be > 0");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("data/ara_vitae_recipes_exist", 30, helper -> {
            helper.runAfterDelay(1, () -> {
                var recipes = helper.getLevel().recipeAccess()
                        .recipeMap().byType(NVRecipes.ARA_VITAE_TYPE.get());
                if (recipes.isEmpty()) {
                    helper.fail("No blood altar recipes found");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("data/hellfire_forge_recipes_exist", 30, helper -> {
            helper.runAfterDelay(1, () -> {
                var recipes = helper.getLevel().recipeAccess()
                        .recipeMap().byType(NVRecipes.HELLFIRE_FORGE_TYPE.get());
                if (recipes.isEmpty()) {
                    helper.fail("No hellfire forge recipes found");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("data/imperfect_rituals_registered", 30, helper -> {
            helper.runAfterDelay(1, () -> {
                Collection<Identifier> rituals = RitualRegistry.getRegisteredImperfectRituals();
                if (rituals.isEmpty()) {
                    helper.fail("No imperfect rituals registered");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("data/blocks_registered", 30, helper -> {
            helper.runAfterDelay(1, () -> {
                String[] expected = {
                        "ara_vitae", "hellfire_forge", "tabula_vitae", "athanor",
                        "master_routing_node", "input_routing_node", "output_routing_node",
                        "master_ritual_stone", "imperfect_ritual_stone",
                        "vas_maleficum", "spira_infernalis", "blood_tank", "teleposer",
                        "incense_altar"
                };
                for (String name : expected) {
                    Identifier rl = NeoVitae.rl(name);
                    if (!BuiltInRegistries.BLOCK.containsKey(rl)) {
                        helper.fail("Expected block " + rl + " not registered");
                        return;
                    }
                }
                helper.succeed();
            });
        });

        r.add("data/items_registered", 30, helper -> {
            helper.runAfterDelay(1, () -> {
                String[] expected = {
                        "blood_orb_weak", "blood_orb_apprentice",
                        "tabula_rasa", "tabula_robur",
                        "sigil_divination", "alchemy_flask",
                        "ritual_diviner"
                };
                for (String name : expected) {
                    Identifier rl = NeoVitae.rl(name);
                    if (!BuiltInRegistries.ITEM.containsKey(rl)) {
                        helper.fail("Expected item " + rl + " not registered");
                        return;
                    }
                }
                helper.succeed();
            });
        });

        r.add("data/chest_loot_never_contains_spiritus_souls", 100, helper -> {
            helper.runAfterDelay(1, () -> {
                ServerLevel level = helper.getLevel();
                LootParams params = new LootParams.Builder(level)
                        .withParameter(LootContextParams.ORIGIN, Vec3.ZERO)
                        .create(LootContextParamSets.CHEST);

                List<ResourceKey<LootTable>> tables = level.getServer().reloadableRegistries().lookup()
                        .lookupOrThrow(Registries.LOOT_TABLE)
                        .listElementIds()
                        .filter(key -> key.identifier().getNamespace().equals(NeoVitae.MODID))
                        .filter(key -> key.identifier().getPath().startsWith("chests/"))
                        .toList();

                ResourceKey<LootTable> greatLoot = ResourceKey.create(Registries.LOOT_TABLE,
                        NeoVitae.rl("chests/standard_dungeon/great_loot"));
                if (!tables.contains(greatLoot)) {
                    helper.fail("Chest loot table lookup found " + tables.size() + " tables and missed " + greatLoot.identifier());
                    return;
                }

                for (ResourceKey<LootTable> key : tables) {
                    LootTable table = level.getServer().reloadableRegistries().getLootTable(key);
                    if (table == LootTable.EMPTY) {
                        helper.fail(key.identifier() + " did not load");
                        return;
                    }
                    for (int i = 0; i < 200; i++) {
                        for (ItemStack drop : table.getRandomItems(params)) {
                            if (drop.getItem() instanceof SpiritusEssenceItem) {
                                helper.fail("Spiritus soul " + drop + " rolled from " + key.identifier());
                                return;
                            }
                        }
                    }
                }
                helper.succeed();
            });
        });

        r.add("data/stream_payload_type_registered", 30, helper -> {
            helper.runAfterDelay(1, () -> {
                Identifier id = StreamPayload.TYPE.id();
                if (id == null) {
                    helper.fail("StreamPayload TYPE should have a valid id");
                    return;
                }
                if (!id.getNamespace().equals("neovitae")) {
                    helper.fail("StreamPayload TYPE namespace should be neovitae, got " + id.getNamespace());
                    return;
                }
                helper.succeed();
            });
        });
    }
}
