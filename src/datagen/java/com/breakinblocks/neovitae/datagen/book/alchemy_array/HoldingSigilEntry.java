package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class HoldingSigilEntry extends EntryProvider {

    public HoldingSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sigil of Holding");
        this.pageText("This Sigil can hold up to 5 other [#](8B0000)Sigils[#]() at a time, providing you with their "
                + "passive effects and allowing you to activate them on a whim.\\\n\\\n"
                + "Press the Open Holding keybind while holding the Sigil to open its inventory. "
                + "Use the cycle keybinds to cycle forward or backward through held sigils. "
                + "Holding sneak and using your mousewheel also works.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Step 1: Craft the [#](8B0000)Holding Reagent[#]() in the Alchemy Table.\\\n\\\n"
                + "Step 2: Create the [#](8B0000)Sigil of Holding[#]() in an Alchemy Array using the "
                + "Holding Reagent as the base and a slate as the catalyst.\\\n\\\n*Sigil-ception*");
    }

    @Override
    protected String entryName() {
        return "Sigil of Holding";
    }

    @Override
    protected String entryDescription() {
        return "A sigil that holds up to 5 other sigils.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_HOLDING.get());
    }

    @Override
    protected String entryId() {
        return "sigil_holding";
    }
}
