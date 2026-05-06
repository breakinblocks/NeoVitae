package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

public class BookSentientUpgradeTablePageModel extends BookPageModel<BookSentientUpgradeTablePageModel> {

    protected BookTextHolderModel title = new BookTextHolderModel("");
    protected BookTextHolderModel text = new BookTextHolderModel("");

    protected BookSentientUpgradeTablePageModel() {
        super(NVPageTypes.SENTIENT_UPGRADE_TABLE);
    }

    public static BookSentientUpgradeTablePageModel create() {
        return new BookSentientUpgradeTablePageModel();
    }

    public BookSentientUpgradeTablePageModel withTitle(String title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    public BookSentientUpgradeTablePageModel withText(String text) {
        this.text = new BookTextHolderModel(text);
        return this;
    }

    @Override
    public JsonObject toJson(Identifier entryId, HolderLookup.Provider provider) {
        var json = super.toJson(entryId, provider);
        json.add("title", this.title.toJson(provider));
        json.add("text", this.text.toJson(provider));
        return json;
    }
}
