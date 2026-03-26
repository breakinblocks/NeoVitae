package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel;

public class BookAthanorRecipePageModel extends BookRecipePageModel<BookAthanorRecipePageModel> {

    protected BookAthanorRecipePageModel() {
        super(NVPageTypes.ATHANOR);
    }

    public static BookAthanorRecipePageModel create() {
        return new BookAthanorRecipePageModel();
    }
}
