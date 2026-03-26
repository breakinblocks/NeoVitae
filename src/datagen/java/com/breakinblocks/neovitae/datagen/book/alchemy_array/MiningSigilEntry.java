package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class MiningSigilEntry extends EntryProvider {

    public MiningSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sigil of the Fast Miner");
        this.pageText("The [#](8B0000)Sigil of the Fast Miner[#]() is a Sigil that, when activated using sneak and [Use], "
                + "will consume 100 LP every 5 seconds and apply the Haste potion effect. Thus, it increases "
                + "your mining, digging, and cutting speeds.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Step 1: Craft the [#](8B0000)Mining Reagent[#]() in the Tabula Vitae.\\\n\\\n"
                + "Step 2: Create the [#](8B0000)Sigil of the Fast Miner[#]() in an Alchemy Array using the "
                + "Mining Reagent as the base and a [#](8B0000)Reinforced Slate[#]() as the catalyst.");
    }

    @Override
    protected String entryName() {
        return "Sigil of the Fast Miner";
    }

    @Override
    protected String entryDescription() {
        return "A sigil that grants Haste when active.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_FAST_MINER.get());
    }

    @Override
    protected String entryId() {
        return "sigil_mining";
    }
}
