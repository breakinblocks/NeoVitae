package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.common.recipe.livingdowngrade.LivingDowngradeRecipe;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;

public class BookLivingDowngradeRecipePage extends BookRecipePage<LivingDowngradeRecipe> {

    public BookLivingDowngradeRecipePage(JsonDataHolder data) {
        super(data);
    }

    public BookLivingDowngradeRecipePage(NetworkDataHolder data) {
        super(data);
    }

    public static BookLivingDowngradeRecipePage fromJson(Identifier id, JsonObject json, HolderLookup.Provider provider) {
        return new BookLivingDowngradeRecipePage(BookRecipePage.commonFromJson(id, json, provider));
    }

    public static BookLivingDowngradeRecipePage fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new BookLivingDowngradeRecipePage(BookRecipePage.commonFromNetwork(buffer));
    }

    @Override
    public Identifier getType() {
        return NVPageTypes.LIVING_DOWNGRADE;
    }
}
