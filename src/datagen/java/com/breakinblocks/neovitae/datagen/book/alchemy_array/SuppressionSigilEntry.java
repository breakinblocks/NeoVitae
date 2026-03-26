package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class SuppressionSigilEntry extends EntryProvider {

    public SuppressionSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sigil of Suppression");
        this.pageText("The [#](8B0000)Sigil of Suppression[#](), when activated via pressing [Use], will temporarily "
                + "remove all fluids in a roughly 6-block radius of its holder. A short while after moving "
                + "away from the liquid in question, it will return as though it never left. While this will "
                + "make exploring the lava oceans of the Nether much easier, beware of jumping into any deep "
                + "pools of water with this active!");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Step 1: Craft the [#](8B0000)Suppression Reagent[#]() in the Alchemy Table.\\\n\\\n"
                + "Step 2: Create the [#](8B0000)Sigil of Suppression[#]() in an Alchemy Array using the "
                + "Suppression Reagent as the base and a slate as the catalyst.\\\n\\\n"
                + "*Better than a Void Sigil!*");
    }

    @Override
    protected String entryName() {
        return "Sigil of Suppression";
    }

    @Override
    protected String entryDescription() {
        return "A sigil that temporarily removes nearby fluids.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_SUPPRESSION.get());
    }

    @Override
    protected String entryId() {
        return "sigil_suppression";
    }
}
