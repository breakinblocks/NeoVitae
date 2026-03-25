package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

public class BookRitualInfoPageModel extends BookPageModel<BookRitualInfoPageModel> {

    protected BookTextHolderModel title = new BookTextHolderModel("");
    protected BookTextHolderModel text = new BookTextHolderModel("");

    protected BookRitualInfoPageModel() {
        super(NVPageTypes.RITUAL_INFO);
    }

    public static BookRitualInfoPageModel create() {
        return new BookRitualInfoPageModel();
    }

    public BookRitualInfoPageModel withTitle(String title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    public BookRitualInfoPageModel withText(String text) {
        this.text = new BookTextHolderModel(text);
        return this;
    }

    @Override
    public JsonObject toJson(ResourceLocation entryId, HolderLookup.Provider provider) {
        var json = super.toJson(entryId, provider);
        json.add("title", this.title.toJson(provider));
        json.add("text", this.text.toJson(provider));
        return json;
    }
}
