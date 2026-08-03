package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

public class BookAltarTierInfoPageModel extends BookPageModel<BookAltarTierInfoPageModel> {

    protected BookTextHolderModel title = new BookTextHolderModel("");

    protected BookAltarTierInfoPageModel() {
        super(NVPageTypes.ALTAR_TIER_INFO);
    }

    public static BookAltarTierInfoPageModel create() {
        return new BookAltarTierInfoPageModel();
    }

    public BookAltarTierInfoPageModel withTitle(String title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    @Override
    public JsonObject toJson(ResourceLocation entryId, HolderLookup.Provider provider) {
        var json = super.toJson(entryId, provider);
        json.add("title", this.title.toJson(provider));
        return json;
    }
}
