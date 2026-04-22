package com.breakinblocks.neovitae.datagen.builder.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeUpgradeRecipe;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

public class ForgeUpgradeRecipeBuilder extends BaseRecipeBuilder {

    private double minWill;
    private double drainedWill;
    private final List<Ingredient> catalysts = new ArrayList<>();

    private ForgeUpgradeRecipeBuilder() {
        super(Items.IRON_SWORD);
    }

    public static ForgeUpgradeRecipeBuilder build() {
        return new ForgeUpgradeRecipeBuilder();
    }

    public ForgeUpgradeRecipeBuilder catalyst(ItemLike item) {
        this.catalysts.add(Ingredient.of(item));
        return this;
    }

    public ForgeUpgradeRecipeBuilder catalyst(TagKey<Item> tag) {
        this.catalysts.add(Ingredient.of() /* TODO(phase15): tag-based ingredient � needs HolderGetter plumb-through */);
        return this;
    }

    public ForgeUpgradeRecipeBuilder minWill(double minWill) {
        this.minWill = minWill;
        return this;
    }

    public ForgeUpgradeRecipeBuilder drain(double drain) {
        this.drainedWill = drain;
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        Advancement.Builder advBuilder = getBuilder(output, id);
        ForgeUpgradeRecipe recipe = new ForgeUpgradeRecipe(minWill, drainedWill, catalysts);
        output.accept(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, id.identifier().withPrefix("hellfire_forge/")), recipe, advBuilder.build(advancementId(id, "hellfire_forge")));
    }
}
