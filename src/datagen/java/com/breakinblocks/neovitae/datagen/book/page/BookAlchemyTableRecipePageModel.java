package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel;

public class BookAlchemyTableRecipePageModel extends BookRecipePageModel<BookAlchemyTableRecipePageModel> {

    protected BookAlchemyTableRecipePageModel() {
        super(NVPageTypes.ALCHEMY_TABLE);
    }

    public static BookAlchemyTableRecipePageModel create() {
        return new BookAlchemyTableRecipePageModel();
    }
}
