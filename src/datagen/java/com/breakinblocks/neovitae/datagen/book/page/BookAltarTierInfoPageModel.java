package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.compat.modonomicon.page.BookAltarTierInfoPage;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import com.klikli_dev.modonomicon.book.page.BookPage;
import net.minecraft.core.HolderLookup;

public class BookAltarTierInfoPageModel extends BookPageModel<BookAltarTierInfoPageModel> {

    protected BookTextHolderModel title = new BookTextHolderModel("");

    protected BookAltarTierInfoPageModel() {
        super(NVPageTypes.ALTAR_TIER_INFO);
    }

    public static BookAltarTierInfoPageModel create() {
        return new BookAltarTierInfoPageModel();
    }

    public BookAltarTierInfoPageModel withTitle(String title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    @Override
    public BookPage toBookPage(HolderLookup.Provider provider) {
        return new BookAltarTierInfoPage(this.title.toBookTextHolder(), this.id, this.condition(provider));
    }
}
