package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;

public class BookHellfireForgeRecipePage extends BookRecipePage<ForgeRecipe> {

    public BookHellfireForgeRecipePage(JsonDataHolder data) {
        super(data);
    }

    public BookHellfireForgeRecipePage(NetworkDataHolder data) {
        super(data);
    }

    public static BookHellfireForgeRecipePage fromJson(Identifier id, JsonObject json, HolderLookup.Provider provider) {
        return new BookHellfireForgeRecipePage(BookRecipePage.commonFromJson(id, json, provider));
    }

    public static BookHellfireForgeRecipePage fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new BookHellfireForgeRecipePage(BookRecipePage.commonFromNetwork(buffer));
    }

    @Override
    public Identifier getType() {
        return NVPageTypes.HELLFIRE_FORGE;
    }
}
