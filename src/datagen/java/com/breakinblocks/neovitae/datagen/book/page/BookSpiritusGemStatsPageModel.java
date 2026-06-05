package com.breakinblocks.neovitae.datagen.book.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.compat.modonomicon.page.BookSpiritusGemStatsPage;
import com.klikli_dev.modonomicon.api.datagen.book.BookTextHolderModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookPageModel;
import com.klikli_dev.modonomicon.book.page.BookPage;
import net.minecraft.core.HolderLookup;

public class BookSpiritusGemStatsPageModel extends BookPageModel<BookSpiritusGemStatsPageModel> {

    protected BookTextHolderModel title = new BookTextHolderModel("");

    protected BookSpiritusGemStatsPageModel() {
        super(NVPageTypes.SPIRITUS_GEM_STATS);
    }

    public static BookSpiritusGemStatsPageModel create() {
        return new BookSpiritusGemStatsPageModel();
    }

    public BookSpiritusGemStatsPageModel withTitle(String title) {
        this.title = new BookTextHolderModel(title);
        return this;
    }

    @Override
    public BookPage toBookPage(HolderLookup.Provider provider) {
        return new BookSpiritusGemStatsPage(this.title.toBookTextHolder(), this.id, this.condition(provider));
    }
}
