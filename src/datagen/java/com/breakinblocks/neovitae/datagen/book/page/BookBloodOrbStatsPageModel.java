package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

public class BookBloodOrbStatsPageModel extends BookPageModel<BookBloodOrbStatsPageModel> {

    protected BookTextHolderModel title = new BookTextHolderModel("");

    protected BookBloodOrbStatsPageModel() {
        super(NVPageTypes.BLOOD_ORB_STATS);
    }

    public static BookBloodOrbStatsPageModel create() {
        return new BookBloodOrbStatsPageModel();
    }

    public BookBloodOrbStatsPageModel withTitle(String title) {
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
