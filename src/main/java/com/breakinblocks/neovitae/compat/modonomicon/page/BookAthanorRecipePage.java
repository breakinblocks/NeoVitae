package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;

public class BookAthanorRecipePage extends BookRecipePage<AthanorRecipe> {

    public BookAthanorRecipePage(JsonDataHolder data) {
        super(data);
    }

    public BookAthanorRecipePage(NetworkDataHolder data) {
        super(data);
    }

    public static BookAthanorRecipePage fromJson(Identifier id, JsonObject json, HolderLookup.Provider provider) {
        return new BookAthanorRecipePage(BookRecipePage.commonFromJson(id, json, provider));
    }

    public static BookAthanorRecipePage fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new BookAthanorRecipePage(BookRecipePage.commonFromNetwork(buffer));
    }

    @Override
    public Identifier getType() {
        return NVPageTypes.ATHANOR;
    }
}
