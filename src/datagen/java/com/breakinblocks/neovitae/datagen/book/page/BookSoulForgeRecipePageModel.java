package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel;

public class BookSoulForgeRecipePageModel extends BookRecipePageModel<BookSoulForgeRecipePageModel> {

    protected BookSoulForgeRecipePageModel() {
        super(NVPageTypes.HELLFIRE_FORGE);
    }

    public static BookSoulForgeRecipePageModel create() {
        return new BookSoulForgeRecipePageModel();
    }
}
