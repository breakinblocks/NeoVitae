package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel;

public class BookLivingDowngradeRecipePageModel extends BookRecipePageModel<BookLivingDowngradeRecipePageModel> {

    protected BookLivingDowngradeRecipePageModel() {
        super(NVPageTypes.LIVING_DOWNGRADE);
    }

    public static BookLivingDowngradeRecipePageModel create() {
        return new BookLivingDowngradeRecipePageModel();
    }
}
