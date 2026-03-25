package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel;

public class BookBloodAltarRecipePageModel extends BookRecipePageModel<BookBloodAltarRecipePageModel> {

    protected BookBloodAltarRecipePageModel() {
        super(NVPageTypes.BLOOD_ALTAR);
    }

    public static BookBloodAltarRecipePageModel create() {
        return new BookBloodAltarRecipePageModel();
    }
}
