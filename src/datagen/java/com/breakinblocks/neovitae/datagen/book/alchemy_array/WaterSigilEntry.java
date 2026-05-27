package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class WaterSigilEntry extends EntryProvider {

    public WaterSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Water Sigil");
        this.pageText("The [#](8B0000)Water Sigil[#]() conjures a source of water from nothing, drawn from "
                + "the currents of your [#](8B0000)Anima[#]() at a cost of 100 [#](4A0080)Essentia Vitae[#]() per use. "
                + "Aim at any solid surface and press [Use].\\\n\\\n"
                + "Should your reserves prove insufficient, the sigil draws the toll from your own blood "
                + "instead.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Forge the [#](8B0000)Water Reagent[#]() in the [#](8B0000)Tabula Vitae[#](), then inscribe "
                + "an [#](8B0000)Alchemy Array[#]() with the reagent as base and a [#](8B0000)Tabula Rasa[#]() as "
                + "catalyst.\\\n\\\n[#](4A0080)An endless spring, born of sacrifice.[#]()");

        this.page("tabula_vitae", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Water Sigil[#]() may also serve as a component in the "
                + "[#](8B0000)Tabula Vitae[#](), automating the production of [#](8B0000)Water Buckets[#](). "
                + "The sigil itself is not consumed in this process.");
    }

    @Override
    protected String entryName() {
        return "Water Sigil";
    }

    @Override
    protected String entryDescription() {
        return "Conjure water from the currents of your Anima.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_WATER.get());
    }

    @Override
    protected String entryId() {
        return "sigil_water";
    }
}
