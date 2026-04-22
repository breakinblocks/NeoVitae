package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;

public class BookAraVitaeRecipePage extends BookRecipePage<AraVitaeRecipe> {

    public BookAraVitaeRecipePage(JsonDataHolder data) {
        super(data);
    }

    public BookAraVitaeRecipePage(NetworkDataHolder data) {
        super(data);
    }

    public static BookAraVitaeRecipePage fromJson(Identifier id, JsonObject json, HolderLookup.Provider provider) {
        return new BookAraVitaeRecipePage(BookRecipePage.commonFromJson(id, json, provider));
    }

    public static BookAraVitaeRecipePage fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new BookAraVitaeRecipePage(BookRecipePage.commonFromNetwork(buffer));
    }

    @Override
    public Identifier getType() {
        return NVPageTypes.ARA_VITAE;
    }
}
