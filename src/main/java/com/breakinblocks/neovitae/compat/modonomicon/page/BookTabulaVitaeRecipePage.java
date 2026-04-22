package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.common.recipe.tabulavitae.TabulaVitaeRecipe;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;

public class BookTabulaVitaeRecipePage extends BookRecipePage<TabulaVitaeRecipe> {

    public BookTabulaVitaeRecipePage(JsonDataHolder data) {
        super(data);
    }

    public BookTabulaVitaeRecipePage(NetworkDataHolder data) {
        super(data);
    }

    public static BookTabulaVitaeRecipePage fromJson(Identifier id, JsonObject json, HolderLookup.Provider provider) {
        return new BookTabulaVitaeRecipePage(BookRecipePage.commonFromJson(id, json, provider));
    }

    public static BookTabulaVitaeRecipePage fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new BookTabulaVitaeRecipePage(BookRecipePage.commonFromNetwork(buffer));
    }

    @Override
    public Identifier getType() {
        return NVPageTypes.TABULA_VITAE;
    }
}
