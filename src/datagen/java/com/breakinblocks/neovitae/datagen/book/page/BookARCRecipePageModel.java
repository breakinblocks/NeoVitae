package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel;

public class BookARCRecipePageModel extends BookRecipePageModel<BookARCRecipePageModel> {

    protected BookARCRecipePageModel() {
        super(NVPageTypes.ARC);
    }

    public static BookARCRecipePageModel create() {
        return new BookARCRecipePageModel();
    }
}
