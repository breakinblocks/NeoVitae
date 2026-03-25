package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.flask.FlaskRecipe;
import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public class BookFlaskRecipePage extends BookRecipePage<FlaskRecipe> {

    public BookFlaskRecipePage(BookTextHolder title1, ResourceLocation recipeId1,
                               BookTextHolder title2, ResourceLocation recipeId2,
                               BookTextHolder text, String anchor, BookCondition condition) {
        super(NVRecipes.FLASK_TYPE.get(), title1, recipeId1, title2, recipeId2, text, anchor, condition);
    }

    public static BookFlaskRecipePage fromJson(JsonObject json, HolderLookup.Provider provider) {
        var common = BookRecipePage.commonFromJson(json, provider);
        var anchor = GsonHelper.getAsString(json, "anchor", "");
        var condition = json.has("condition")
                ? BookCondition.fromJson(json.getAsJsonObject("condition"), provider)
                : new BookNoneCondition();
        return new BookFlaskRecipePage(common.title1(), common.recipeId1(),
                common.title2(), common.recipeId2(), common.text(), anchor, condition);
    }

    public static BookFlaskRecipePage fromNetwork(RegistryFriendlyByteBuf buffer) {
        var common = BookRecipePage.commonFromNetwork(buffer);
        var anchor = buffer.readUtf();
        var condition = BookCondition.fromNetwork(buffer);
        return new BookFlaskRecipePage(common.title1(), common.recipeId1(),
                common.title2(), common.recipeId2(), common.text(), anchor, condition);
    }

    @Override
    protected ItemStack getRecipeOutput(Level level, RecipeHolder<FlaskRecipe> recipeHolder) {
        var recipe = recipeHolder.value();
        return recipe.getOutput(recipe.getExampleFlask(), recipe.getExampleEffects());
    }

    @Override
    public ResourceLocation getType() {
        return NVPageTypes.FLASK;
    }
}
