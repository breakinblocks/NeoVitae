package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class MagnetismSigilEntry extends EntryProvider {

    public MagnetismSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sigil of Magnetism");
        this.pageText("Increases the player's item pickup range to 7 blocks when active. Toggle on/off by "
                + "holding the Sigil in your hand, and then holding sneak and pressing [Use].\\\n\\\n"
                + "Consumes 50 LP every 5 seconds while active.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Step 1: Craft the [#](8B0000)Magnetism Reagent[#]() in the Tabula Vitae.\\\n\\\n"
                + "Step 2: Create the [#](8B0000)Sigil of Magnetism[#]() in an Alchemy Array using the "
                + "Magnetism Reagent as the base and a slate as the catalyst.");
    }

    @Override
    protected String entryName() {
        return "Sigil of Magnetism";
    }

    @Override
    protected String entryDescription() {
        return "A sigil that increases item pickup range.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_MAGNETISM.get());
    }

    @Override
    protected String entryId() {
        return "sigil_magnetism";
    }
}
