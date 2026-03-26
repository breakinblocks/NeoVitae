package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class AirSigilEntry extends EntryProvider {

    public AirSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Air Sigil");
        this.pageText("Throws you in the direction you're facing, at a cost of 50 LP per use. Note that this "
                + "does not provide any sort of Feather Falling effect, so be careful when landing!\\\n\\\n"
                + "A good way to get around quickly, albeit with some risk. Many an unwary mage has met their end by "
                + "running out of LP in their [#](8B0000)Soul Network[#]() while flying miles above the countryside.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Step 1: Craft the [#](8B0000)Air Reagent[#]() in the Tabula Vitae.\\\n\\\n"
                + "Step 2: Create the [#](8B0000)Air Sigil[#]() in an Alchemy Array using the Air Reagent as "
                + "the base and a slate as the catalyst.\\\n\\\n*I feel lighter already...*");
    }

    @Override
    protected String entryName() {
        return "Air Sigil";
    }

    @Override
    protected String entryDescription() {
        return "A sigil that launches you in the direction you face.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_AIR.get());
    }

    @Override
    protected String entryId() {
        return "sigil_air";
    }
}
