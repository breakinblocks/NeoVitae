package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.compat.modonomicon.page.BookFlaskRecipePage;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;

public class BookFlaskRecipePageModel extends BookRecipePageModel<BookFlaskRecipePageModel> {

    protected BookFlaskRecipePageModel() {
        super(NVPageTypes.FLASK);
    }

    public static BookFlaskRecipePageModel create() {
        return new BookFlaskRecipePageModel();
    }

    @Override
    protected BookPage createPage(BookRecipePage.JsonDataHolder data) {
        return new BookFlaskRecipePage(data);
    }
}
