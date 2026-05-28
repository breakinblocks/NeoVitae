package com.breakinblocks.neovitae.datagen.builder.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeSpiritusInfusionRecipe;

public class ForgeSpiritusInfusionRecipeBuilder extends BaseRecipeBuilder {

    private double minSpiritus;
    private double drainedSpiritus;
    private Ingredient gemInput;

    private ForgeSpiritusInfusionRecipeBuilder() {
        super(new ItemStack(Items.DIAMOND_SWORD));
    }

    public static ForgeSpiritusInfusionRecipeBuilder build() {
        return new ForgeSpiritusInfusionRecipeBuilder();
    }

    public ForgeSpiritusInfusionRecipeBuilder gemInput(ItemLike item) {
        this.gemInput = Ingredient.of(item);
        return this;
    }

    public ForgeSpiritusInfusionRecipeBuilder gemInput(TagKey<Item> tag) {
        this.gemInput = Ingredient.of(tag);
        return this;
    }

    public ForgeSpiritusInfusionRecipeBuilder minSpiritus(double minSpiritus) {
        this.minSpiritus = minSpiritus;
        return this;
    }

    public ForgeSpiritusInfusionRecipeBuilder drain(double drain) {
        this.drainedSpiritus = drain;
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        Advancement.Builder advBuilder = getBuilder(output, id);
        ForgeSpiritusInfusionRecipe recipe = new ForgeSpiritusInfusionRecipe(minSpiritus, drainedSpiritus, gemInput);
        output.accept(id.withPrefix("hellfire_forge/"), recipe, advBuilder.build(advancementId(id, "hellfire_forge")));
    }
}
