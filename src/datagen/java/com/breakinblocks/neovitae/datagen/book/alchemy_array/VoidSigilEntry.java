package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class VoidSigilEntry extends EntryProvider {

    public VoidSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Void Sigil");
        this.pageText("The [#](8B0000)Void Sigil[#](), when you use it while looking at any fluid, will destroy it at "
                + "a cost of 50 LP per block. Good for clearing out irksome lava flows without all that "
                + "tedious placing and breaking of individual blocks.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Step 1: Craft the [#](8B0000)Void Reagent[#]() in the Alchemy Table.\\\n\\\n"
                + "Step 2: Create the [#](8B0000)Void Sigil[#]() in an Alchemy Array using the Void Reagent "
                + "as the base and a slate as the catalyst.\\\n\\\n*Better than a Swiffer!*");
    }

    @Override
    protected String entryName() {
        return "Void Sigil";
    }

    @Override
    protected String entryDescription() {
        return "A sigil that destroys fluid blocks.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_VOID.get());
    }

    @Override
    protected String entryId() {
        return "sigil_void";
    }
}
