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
import com.breakinblocks.neovitae.common.recipe.forge.ForgeUpgradeRecipe;

import java.util.ArrayList;
import java.util.List;

public class ForgeUpgradeRecipeBuilder extends BaseRecipeBuilder {

    private double minWill;
    private double drainedWill;
    private final List<Ingredient> catalysts = new ArrayList<>();

    private ForgeUpgradeRecipeBuilder() {
        super(new ItemStack(Items.IRON_SWORD));
    }

    public static ForgeUpgradeRecipeBuilder build() {
        return new ForgeUpgradeRecipeBuilder();
    }

    public ForgeUpgradeRecipeBuilder catalyst(ItemLike item) {
        this.catalysts.add(Ingredient.of(item));
        return this;
    }

    public ForgeUpgradeRecipeBuilder catalyst(TagKey<Item> tag) {
        this.catalysts.add(Ingredient.of(tag));
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
    public void save(RecipeOutput output, ResourceLocation id) {
        Advancement.Builder advBuilder = getBuilder(output, id);
        ForgeUpgradeRecipe recipe = new ForgeUpgradeRecipe(minWill, drainedWill, catalysts);
        output.accept(id.withPrefix("hellfire_forge/"), recipe, advBuilder.build(advancementId(id, "hellfire_forge")));
    }
}
