package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.compat.modonomicon.page.BookAthanorRecipePage;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;

public class BookAthanorRecipePageModel extends BookRecipePageModel<BookAthanorRecipePageModel> {

    protected BookAthanorRecipePageModel() {
        super(NVPageTypes.ATHANOR);
    }

    public static BookAthanorRecipePageModel create() {
        return new BookAthanorRecipePageModel();
    }

    @Override
    protected BookPage createPage(BookRecipePage.JsonDataHolder data) {
        return new BookAthanorRecipePage(data);
    }
}
