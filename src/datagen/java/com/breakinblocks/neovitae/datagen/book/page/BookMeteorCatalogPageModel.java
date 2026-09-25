package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.compat.modonomicon.page.BookMeteorCatalogPage;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import com.klikli_dev.modonomicon.book.page.BookPage;
import net.minecraft.core.HolderLookup;

public class BookMeteorCatalogPageModel extends BookPageModel<BookMeteorCatalogPageModel> {

    protected BookTextHolderModel title = new BookTextHolderModel("");

    protected BookMeteorCatalogPageModel() {
        super(NVPageTypes.METEOR_CATALOG);
    }

    public static BookMeteorCatalogPageModel create() {
        return new BookMeteorCatalogPageModel();
    }

    public BookMeteorCatalogPageModel withTitle(String title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    @Override
    public BookPage toBookPage(HolderLookup.Provider provider) {
        return new BookMeteorCatalogPage(this.title.toBookTextHolder(), this.id, this.condition(provider));
    }
}
