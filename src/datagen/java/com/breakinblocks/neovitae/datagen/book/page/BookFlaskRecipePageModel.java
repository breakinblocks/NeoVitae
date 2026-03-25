package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel;

public class BookFlaskRecipePageModel extends BookRecipePageModel<BookFlaskRecipePageModel> {

    protected BookFlaskRecipePageModel() {
        super(NVPageTypes.FLASK);
    }

    public static BookFlaskRecipePageModel create() {
        return new BookFlaskRecipePageModel();
    }
}
