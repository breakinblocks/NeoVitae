package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.common.recipe.sentientdowngrade.SentientDowngradeRecipe;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;

public class BookSentientDowngradeRecipePage extends BookRecipePage<SentientDowngradeRecipe> {

    public BookSentientDowngradeRecipePage(JsonDataHolder data) {
        super(data);
    }

    public BookSentientDowngradeRecipePage(NetworkDataHolder data) {
        super(data);
    }

    public static BookSentientDowngradeRecipePage fromJson(Identifier id, JsonObject json, HolderLookup.Provider provider) {
        return new BookSentientDowngradeRecipePage(BookRecipePage.commonFromJson(id, json, provider));
    }

    public static BookSentientDowngradeRecipePage fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new BookSentientDowngradeRecipePage(BookRecipePage.commonFromNetwork(buffer));
    }

    @Override
    public Identifier getType() {
        return NVPageTypes.SENTIENT_DOWNGRADE;
    }
}
