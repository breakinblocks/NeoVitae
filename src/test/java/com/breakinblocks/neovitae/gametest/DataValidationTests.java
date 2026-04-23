package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.datamap.RoutingNodeStats;
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
                        "sacrificial_dagger",
                        "blood_orb_weak", "blood_orb_apprentice",
                        "blank_slate", "reinforced_slate",
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
