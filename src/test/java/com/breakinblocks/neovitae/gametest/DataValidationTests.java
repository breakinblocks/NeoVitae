package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.datamap.RoutingNodeStats;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.ritual.RitualRegistry;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class DataValidationTests {

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void allRitualsHaveComponents(GameTestHelper helper) {
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
                    ResourceLocation id = RitualRegistry.getId(ritual);
                    helper.fail("Ritual " + id + " has no components");
                    return;
                }
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void allRitualsHaveTranslationKeys(GameTestHelper helper) {
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
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void masterNodeHasRoutingStats(GameTestHelper helper) {
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
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void bloodAltarRecipesExist(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            var recipes = helper.getLevel().getRecipeManager()
                    .getAllRecipesFor(com.breakinblocks.neovitae.common.recipe.NVRecipes.ARA_VITAE_TYPE.get());
            if (recipes.isEmpty()) {
                helper.fail("No blood altar recipes found");
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void hellfireForgeRecipesExist(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            var recipes = helper.getLevel().getRecipeManager()
                    .getAllRecipesFor(com.breakinblocks.neovitae.common.recipe.NVRecipes.HELLFIRE_FORGE_TYPE.get());
            if (recipes.isEmpty()) {
                helper.fail("No hellfire forge recipes found");
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void imperfectRitualsRegistered(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            Collection<ResourceLocation> rituals = RitualRegistry.getRegisteredImperfectRituals();
            if (rituals.isEmpty()) {
                helper.fail("No imperfect rituals registered");
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void neovitaeBlocksRegistered(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            String[] expectedBlocks = {
                "ara_vitae", "hellfire_forge", "tabula_vitae", "athanor",
                "master_routing_node", "input_routing_node", "output_routing_node",
                "master_ritual_stone", "imperfect_ritual_stone",
                "vas_maleficum", "spira_infernalis", "blood_tank", "teleposer",
                "incense_altar"
            };

            for (String name : expectedBlocks) {
                ResourceLocation rl = NeoVitae.rl(name);
                if (!BuiltInRegistries.BLOCK.containsKey(rl)) {
                    helper.fail("Expected block " + rl + " not registered");
                    return;
                }
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void neovitaeItemsRegistered(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            String[] expectedItems = {
                "sacrificial_dagger", "dagger_of_sacrifice",
                "orb_weak", "orb_apprentice",
                "slate_blank", "slate_reinforced",
                "divination_sigil", "alchemy_flask",
                "ritual_diviner_dawn"
            };

            for (String name : expectedItems) {
                ResourceLocation rl = NeoVitae.rl(name);
                if (!BuiltInRegistries.ITEM.containsKey(rl)) {
                    helper.fail("Expected item " + rl + " not registered");
                    return;
                }
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void streamPayloadTypeRegistered(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            ResourceLocation id = com.breakinblocks.neovitae.common.network.StreamPayload.TYPE.id();
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
    }
}
