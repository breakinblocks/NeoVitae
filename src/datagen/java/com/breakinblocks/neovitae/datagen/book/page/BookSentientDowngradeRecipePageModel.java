package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel;

public class BookSentientDowngradeRecipePageModel extends BookRecipePageModel<BookSentientDowngradeRecipePageModel> {

    protected BookSentientDowngradeRecipePageModel() {
        super(NVPageTypes.SENTIENT_DOWNGRADE);
    }

    public static BookSentientDowngradeRecipePageModel create() {
        return new BookSentientDowngradeRecipePageModel();
    }
}
