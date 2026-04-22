package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;

public class BookAlchemyArrayRecipePage extends BookRecipePage<AlchemyArrayRecipe> {

    public BookAlchemyArrayRecipePage(JsonDataHolder data) {
        super(data);
    }

    public BookAlchemyArrayRecipePage(NetworkDataHolder data) {
        super(data);
    }

    public static BookAlchemyArrayRecipePage fromJson(Identifier id, JsonObject json, HolderLookup.Provider provider) {
        return new BookAlchemyArrayRecipePage(BookRecipePage.commonFromJson(id, json, provider));
    }

    public static BookAlchemyArrayRecipePage fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new BookAlchemyArrayRecipePage(BookRecipePage.commonFromNetwork(buffer));
    }

    @Override
    public Identifier getType() {
        return NVPageTypes.ALCHEMY_ARRAY;
    }
}
