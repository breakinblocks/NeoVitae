package com.breakinblocks.neovitae.compat.emi;

import com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;
import com.breakinblocks.neovitae.common.recipe.flask.FlaskRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;
import com.breakinblocks.neovitae.common.recipe.tabulavitae.TabulaVitaeRecipe;
import com.breakinblocks.neovitae.compat.emi.recipe.AlchemyArrayCraftingEmiRecipe;
import com.breakinblocks.neovitae.compat.emi.recipe.AlchemyArrayEffectEmiRecipe;
import com.breakinblocks.neovitae.compat.emi.recipe.AraVitaeEmiRecipe;
import com.breakinblocks.neovitae.compat.emi.recipe.AthanorEmiRecipe;
import com.breakinblocks.neovitae.compat.emi.recipe.BloodTankUpgradeEmiRecipe;
import com.breakinblocks.neovitae.compat.emi.recipe.DisenchantEmiRecipe;
import com.breakinblocks.neovitae.compat.emi.recipe.FlaskEmiRecipe;
import com.breakinblocks.neovitae.compat.emi.recipe.HellfireForgeEmiRecipe;
import com.breakinblocks.neovitae.compat.emi.recipe.MeteorEmiRecipe;
import com.breakinblocks.neovitae.compat.emi.recipe.TabulaVitaeEmiRecipe;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;

@EmiEntrypoint
public class NeoVitaeEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        addCategories(registry);
        addWorkstations(registry);
        addComparisons(registry);

        addAltarRecipes(registry);
        addForgeRecipes(registry);
        addArrayRecipes(registry);
        addTabulaVitaeRecipes(registry);
        addMeteorRecipes(registry);
        addAthanorRecipes(registry);
        addFlaskRecipes(registry);
        addRitualRecipes(registry);
        addBloodTankUpgrades(registry);
        addDisenchant(registry);
    }

    private void addCategories(EmiRegistry registry) {
        registry.addCategory(NVEmiCategories.ARA_VITAE);
        registry.addCategory(NVEmiCategories.HELLFIRE_FORGE);
        registry.addCategory(NVEmiCategories.FORGE_UPGRADE);
        registry.addCategory(NVEmiCategories.ALCHEMY_ARRAY_CRAFTING);
        registry.addCategory(NVEmiCategories.ALCHEMY_ARRAY_EFFECT);
        registry.addCategory(NVEmiCategories.TABULA_VITAE);
        registry.addCategory(NVEmiCategories.METEOR);
        registry.addCategory(NVEmiCategories.ATHANOR);
        registry.addCategory(NVEmiCategories.FLASK);
        registry.addCategory(NVEmiCategories.FLASK_COMBINATION);
        registry.addCategory(NVEmiCategories.IMPERFECT_RITUAL);
        registry.addCategory(NVEmiCategories.RITUAL);
        registry.addCategory(NVEmiCategories.BLOOD_TANK_UPGRADE);
        registry.addCategory(NVEmiCategories.DISENCHANT);
    }

    private void addWorkstations(EmiRegistry registry) {
        EmiStack forge = EmiStack.of(NVBlocks.HELLFIRE_FORGE.block().get());
        EmiStack scribe = EmiStack.of(NVItems.ARCANE_SCRIBE_TOOL.get());
        EmiStack tabula = EmiStack.of(NVBlocks.TABULA_VITAE.block().get());
        EmiStack flask = EmiStack.of(NVItems.ALCHEMY_FLASK.get());
        EmiStack masterStone = EmiStack.of(NVBlocks.MASTER_RITUAL_STONE.block().get());

        registry.addWorkstation(NVEmiCategories.ARA_VITAE, EmiStack.of(NVBlocks.ARA_VITAE.block().get()));
        registry.addWorkstation(NVEmiCategories.HELLFIRE_FORGE, forge);
        registry.addWorkstation(NVEmiCategories.FORGE_UPGRADE, forge);
        registry.addWorkstation(NVEmiCategories.ALCHEMY_ARRAY_CRAFTING, scribe);
        registry.addWorkstation(NVEmiCategories.ALCHEMY_ARRAY_EFFECT, scribe);
        registry.addWorkstation(NVEmiCategories.TABULA_VITAE, tabula);
        registry.addWorkstation(NVEmiCategories.ATHANOR, EmiStack.of(NVBlocks.ATHANOR_BLOCK.block().get()));
        registry.addWorkstation(NVEmiCategories.FLASK, flask);
        registry.addWorkstation(NVEmiCategories.FLASK, tabula);
        registry.addWorkstation(NVEmiCategories.FLASK_COMBINATION, flask);
        registry.addWorkstation(NVEmiCategories.FLASK_COMBINATION, tabula);
        registry.addWorkstation(NVEmiCategories.IMPERFECT_RITUAL, EmiStack.of(NVBlocks.IMPERFECT_RITUAL_STONE.block().get()));
        registry.addWorkstation(NVEmiCategories.RITUAL, masterStone);
        registry.addWorkstation(NVEmiCategories.METEOR, masterStone);
        registry.addWorkstation(NVEmiCategories.BLOOD_TANK_UPGRADE, EmiStack.of(Items.CRAFTING_TABLE));
        registry.addWorkstation(NVEmiCategories.DISENCHANT, EmiStack.of(NVItems.SANGUINE_REVERTER.get()));
    }

    private void addComparisons(EmiRegistry registry) {
        Comparison byComponents = Comparison.compareComponents();
        registry.setDefaultComparison(NVItems.ALCHEMY_FLASK.get(), byComponents);
        registry.setDefaultComparison(NVItems.ALCHEMY_FLASK_THROWABLE.get(), byComponents);
        registry.setDefaultComparison(NVItems.ALCHEMY_FLASK_LINGERING.get(), byComponents);
        registry.setDefaultComparison(NVBlocks.BLOOD_TANK.item().get(), byComponents);
        registry.setDefaultComparison(NVItems.UPGRADE_TOME.get(), byComponents);
    }

    private void addAltarRecipes(EmiRegistry registry) {
        for (RecipeHolder<AraVitaeRecipe> holder : registry.getRecipeManager().getAllRecipesFor(NVRecipes.ARA_VITAE_TYPE.get())) {
            registry.addRecipe(new AraVitaeEmiRecipe(holder.value(), holder.id()));
        }
    }

    private void addForgeRecipes(EmiRegistry registry) {
        for (RecipeHolder<ForgeRecipe> holder : registry.getRecipeManager().getAllRecipesFor(NVRecipes.HELLFIRE_FORGE_TYPE.get())) {
            EmiRecipeCategory category = NVEmiCategoryRouting.forgeCategory(holder.value());
            registry.addRecipe(new HellfireForgeEmiRecipe(holder.value(), holder.id(), category));
        }
    }

    private void addArrayRecipes(EmiRegistry registry) {
        for (RecipeHolder<AlchemyArrayRecipe> holder : registry.getRecipeManager().getAllRecipesFor(NVRecipes.ALCHEMY_ARRAY_TYPE.get())) {
            AlchemyArrayRecipe recipe = holder.value();
            if (recipe.getOutput().isEmpty()) {
                registry.addRecipe(new AlchemyArrayEffectEmiRecipe(recipe, holder.id()));
            } else {
                registry.addRecipe(new AlchemyArrayCraftingEmiRecipe(recipe, holder.id()));
            }
        }
    }

    private void addTabulaVitaeRecipes(EmiRegistry registry) {
        for (RecipeHolder<TabulaVitaeRecipe> holder : registry.getRecipeManager().getAllRecipesFor(NVRecipes.TABULA_VITAE_TYPE.get())) {
            registry.addRecipe(new TabulaVitaeEmiRecipe(holder.value(), holder.id()));
        }
    }

    private void addMeteorRecipes(EmiRegistry registry) {
        for (RecipeHolder<MeteorRecipe> holder : registry.getRecipeManager().getAllRecipesFor(NVRecipes.METEOR_TYPE.get())) {
            registry.addRecipe(new MeteorEmiRecipe(holder.value(), holder.id()));
        }
    }

    private void addAthanorRecipes(EmiRegistry registry) {
        for (RecipeHolder<AthanorRecipe> holder : registry.getRecipeManager().getAllRecipesFor(NVRecipes.ATHANOR_TYPE.get())) {
            registry.addRecipe(new AthanorEmiRecipe(holder.value(), holder.id()));
        }
    }

    private void addFlaskRecipes(EmiRegistry registry) {
        List<FlaskRecipe> flaskRecipes = new ArrayList<>();
        for (RecipeHolder<FlaskRecipe> holder : registry.getRecipeManager().getAllRecipesFor(NVRecipes.FLASK_TYPE.get())) {
            flaskRecipes.add(holder.value());
            registry.addRecipe(new FlaskEmiRecipe(holder.value(), holder.id()));
        }
        NVEmiRecipeSources.flaskCombinations(flaskRecipes).forEach(registry::addRecipe);
    }

    private void addRitualRecipes(EmiRegistry registry) {
        NVEmiRecipeSources.rituals().forEach(registry::addRecipe);
        NVEmiRecipeSources.imperfectRituals().forEach(registry::addRecipe);
    }

    private void addBloodTankUpgrades(EmiRegistry registry) {
        for (int tier = 2; tier <= 16; tier++) {
            registry.addRecipe(new BloodTankUpgradeEmiRecipe(bloodTankStack(tier - 1), bloodTankStack(tier),
                    NVEmiCategories.recipeId("blood_tank_upgrade", tier)));
        }
    }

    private void addDisenchant(EmiRegistry registry) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        List<ItemStack> books = new ArrayList<>();
        level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).listElements().forEach(holder -> {
            Enchantment enchantment = holder.value();
            for (int lvl = enchantment.getMinLevel(); lvl <= enchantment.getMaxLevel(); lvl++) {
                ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                mutable.set(holder, lvl);
                book.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
                books.add(book);
            }
        });
        registry.addRecipe(new DisenchantEmiRecipe(books, NVEmiCategories.synthetic("disenchant")));
    }

    private static ItemStack bloodTankStack(int tier) {
        ItemStack stack = new ItemStack(NVBlocks.BLOOD_TANK.block().get());
        stack.set(NVDataComponents.CONTAINER_TIER, tier);
        return stack;
    }
}
