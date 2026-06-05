package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.compat.modonomicon.page.BookSentientDowngradeRecipePage;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;

public class BookSentientDowngradeRecipePageModel extends BookRecipePageModel<BookSentientDowngradeRecipePageModel> {

    protected BookSentientDowngradeRecipePageModel() {
        super(NVPageTypes.SENTIENT_DOWNGRADE);
    }

    public static BookSentientDowngradeRecipePageModel create() {
        return new BookSentientDowngradeRecipePageModel();
    }

    @Override
    protected BookPage createPage(BookRecipePage.JsonDataHolder data) {
        return new BookSentientDowngradeRecipePage(data);
    }
}
