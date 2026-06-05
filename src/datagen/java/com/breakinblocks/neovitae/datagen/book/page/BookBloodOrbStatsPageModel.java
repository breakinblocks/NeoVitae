package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.compat.modonomicon.page.BookBloodOrbStatsPage;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import com.klikli_dev.modonomicon.book.page.BookPage;
import net.minecraft.core.HolderLookup;

public class BookBloodOrbStatsPageModel extends BookPageModel<BookBloodOrbStatsPageModel> {

    protected BookTextHolderModel title = new BookTextHolderModel("");

    protected BookBloodOrbStatsPageModel() {
        super(NVPageTypes.BLOOD_ORB_STATS);
    }

    public static BookBloodOrbStatsPageModel create() {
        return new BookBloodOrbStatsPageModel();
    }

    public BookBloodOrbStatsPageModel withTitle(String title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    @Override
    public BookPage toBookPage(HolderLookup.Provider provider) {
        return new BookBloodOrbStatsPage(this.title.toBookTextHolder(), this.id, this.condition(provider));
    }
}
