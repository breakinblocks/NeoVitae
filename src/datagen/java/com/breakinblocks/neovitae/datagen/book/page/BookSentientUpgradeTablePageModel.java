package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.compat.modonomicon.page.BookSentientUpgradeTablePage;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import com.klikli_dev.modonomicon.book.page.BookPage;
import net.minecraft.core.HolderLookup;

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
    public BookPage toBookPage(HolderLookup.Provider provider) {
        return new BookSentientUpgradeTablePage(this.title.toBookTextHolder(), this.text.toBookTextHolder(), this.id, this.condition(provider));
    }
}
