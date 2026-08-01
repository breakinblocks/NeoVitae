package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.klikli_dev.modonomicon.book.entries.BookContentEntry;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public abstract class BookNVRecipePage<T extends Recipe<?>> extends BookRecipePage<T> {

    protected BookNVRecipePage(JsonDataHolder data) {
        super(data);
    }

    protected BookNVRecipePage(NetworkDataHolder data) {
        super(data);
    }

    @Override
    public void build(Level level, BookContentEntry parentEntry, int pageNumber) {
        this.parentEntry = parentEntry;
        this.pageNumber = pageNumber;
        this.book = parentEntry.getBook();
    }
}
