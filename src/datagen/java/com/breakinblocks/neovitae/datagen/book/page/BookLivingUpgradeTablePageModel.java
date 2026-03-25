package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

public class BookLivingUpgradeTablePageModel extends BookPageModel<BookLivingUpgradeTablePageModel> {

    protected BookTextHolderModel title = new BookTextHolderModel("");
    protected BookTextHolderModel text = new BookTextHolderModel("");

    protected BookLivingUpgradeTablePageModel() {
        super(NVPageTypes.LIVING_UPGRADE_TABLE);
    }

    public static BookLivingUpgradeTablePageModel create() {
        return new BookLivingUpgradeTablePageModel();
    }

    public BookLivingUpgradeTablePageModel withTitle(String title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    public BookLivingUpgradeTablePageModel withText(String text) {
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
