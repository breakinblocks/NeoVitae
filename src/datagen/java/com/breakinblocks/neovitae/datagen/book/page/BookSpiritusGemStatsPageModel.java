package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

public class BookSpiritusGemStatsPageModel extends BookPageModel<BookSpiritusGemStatsPageModel> {

    protected BookTextHolderModel title = new BookTextHolderModel("");

    protected BookSpiritusGemStatsPageModel() {
        super(NVPageTypes.SPIRITUS_GEM_STATS);
    }

    public static BookSpiritusGemStatsPageModel create() {
        return new BookSpiritusGemStatsPageModel();
    }

    public BookSpiritusGemStatsPageModel withTitle(String title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    @Override
    public JsonObject toJson(Identifier entryId, HolderLookup.Provider provider) {
        var json = super.toJson(entryId, provider);
        json.add("title", this.title.toJson(provider));
        return json;
    }
}
