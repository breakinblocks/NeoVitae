package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.arc.ARCRecipe;
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

public class BookARCRecipePage extends BookRecipePage<ARCRecipe> {

    public BookARCRecipePage(BookTextHolder title1, ResourceLocation recipeId1,
                             BookTextHolder title2, ResourceLocation recipeId2,
                             BookTextHolder text, String anchor, BookCondition condition) {
        super(NVRecipes.ARC_TYPE.get(), title1, recipeId1, title2, recipeId2, text, anchor, condition);
    }

    public static BookARCRecipePage fromJson(JsonObject json, HolderLookup.Provider provider) {
        var common = BookRecipePage.commonFromJson(json, provider);
        var anchor = GsonHelper.getAsString(json, "anchor", "");
        var condition = json.has("condition")
                ? BookCondition.fromJson(json.getAsJsonObject("condition"), provider)
                : new BookNoneCondition();
        return new BookARCRecipePage(common.title1(), common.recipeId1(),
                common.title2(), common.recipeId2(), common.text(), anchor, condition);
    }

    public static BookARCRecipePage fromNetwork(RegistryFriendlyByteBuf buffer) {
        var common = BookRecipePage.commonFromNetwork(buffer);
        var anchor = buffer.readUtf();
        var condition = BookCondition.fromNetwork(buffer);
        return new BookARCRecipePage(common.title1(), common.recipeId1(),
                common.title2(), common.recipeId2(), common.text(), anchor, condition);
    }

    @Override
    protected ItemStack getRecipeOutput(Level level, RecipeHolder<ARCRecipe> recipeHolder) {
        var outputs = recipeHolder.value().getGuaranteedOutput();
        return outputs.isEmpty() ? ItemStack.EMPTY : outputs.getFirst();
    }

    @Override
    public ResourceLocation getType() {
        return NVPageTypes.ARC;
    }
}
