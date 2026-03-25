package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class CraftingArrayEntry extends EntryProvider {

    public CraftingArrayEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crafting with Arrays");
        this.pageText("**Alchemy Arrays of Crafting** are one of the simplest forms of array. These arrays "
                + "inscribe the **base item** onto the **catalyst**, transforming them both into a useful item "
                + "(after a small, pretty animation).");

        this.page("details", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("While some of these resulting items can be crafted using very simple ingredients, "
                + "others require additional steps to create useful items.");
    }

    @Override
    protected String entryName() {
        return "Crafting with Arrays";
    }

    @Override
    protected String entryDescription() {
        return "Using Alchemy Arrays to transform items.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_DIVINATION.get());
    }

    @Override
    protected String entryId() {
        return "crafting_array";
    }
}
