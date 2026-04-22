package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.common.recipe.flask.FlaskRecipe;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;

public class BookFlaskRecipePage extends BookRecipePage<FlaskRecipe> {

    public BookFlaskRecipePage(JsonDataHolder data) {
        super(data);
    }

    public BookFlaskRecipePage(NetworkDataHolder data) {
        super(data);
    }

    public static BookFlaskRecipePage fromJson(Identifier id, JsonObject json, HolderLookup.Provider provider) {
        return new BookFlaskRecipePage(BookRecipePage.commonFromJson(id, json, provider));
    }

    public static BookFlaskRecipePage fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new BookFlaskRecipePage(BookRecipePage.commonFromNetwork(buffer));
    }

    @Override
    public Identifier getType() {
        return NVPageTypes.FLASK;
    }
}
