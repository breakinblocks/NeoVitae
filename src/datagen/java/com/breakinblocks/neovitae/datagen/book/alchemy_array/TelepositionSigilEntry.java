package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class TelepositionSigilEntry extends EntryProvider {

    public TelepositionSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Teleposition Sigil");
        this.pageText("Teleports the user to a linked Teleposer at a cost of 1000 LP per use. By "
                + "shift-clicking on a Teleposer with the sigil in hand, the location and dimension of "
                + "the Teleposer can be recorded, allowing for a quick escape back home.\\\n\\\n"
                + "Just don't move the Teleposer by accident...");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Step 1: Craft the [#](8B0000)Teleposition Reagent[#]() in the Tabula Vitae.\\\n\\\n"
                + "Step 2: Create the [#](8B0000)Teleposition Sigil[#]() in an Alchemy Array using the "
                + "Teleposition Reagent as the base and a slate as the catalyst.\\\n\\\n*Now you see me...!*");
    }

    @Override
    protected String entryName() {
        return "Teleposition Sigil";
    }

    @Override
    protected String entryDescription() {
        return "A sigil that teleports you to a linked Teleposer.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_TELEPOSITION.get());
    }

    @Override
    protected String entryId() {
        return "sigil_teleposition";
    }
}
