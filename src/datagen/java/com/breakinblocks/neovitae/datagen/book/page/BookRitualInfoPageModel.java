package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.compat.modonomicon.page.BookRitualInfoPage;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import com.klikli_dev.modonomicon.book.page.BookPage;
import net.minecraft.core.HolderLookup;

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
    public BookPage toBookPage(HolderLookup.Provider provider) {
        return new BookRitualInfoPage(this.title.toBookTextHolder(), this.text.toBookTextHolder(), this.id, this.condition(provider));
    }
}
