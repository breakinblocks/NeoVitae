package com.breakinblocks.neovitae.datagen.book.demon_will;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class FilterPartsEntry extends EntryProvider {

    public FilterPartsEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Filter Parts");
        this.pageText("Craft Filter Parts in the Alchemy Table.\n\n"
                + "**Filter Parts** are an integral component in all **Filters**. They have no use on their own.");
    }

    @Override
    protected String entryName() {
        return "Filter Parts";
    }

    @Override
    protected String entryDescription() {
        return "A crafting component used in all routing filters.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.FRAME_PARTS.get());
    }

    @Override
    protected String entryId() {
        return "filter_parts";
    }
}
