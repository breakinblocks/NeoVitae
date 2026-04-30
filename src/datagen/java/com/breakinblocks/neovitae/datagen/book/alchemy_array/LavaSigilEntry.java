package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class LavaSigilEntry extends EntryProvider {

    public LavaSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Lava Sigil");
        this.pageText("Sister to the [#](8B0000)Water Sigil[#](), the [#](8B0000)Lava Sigil[#]() draws molten stone "
                + "from the depths of your [#](8B0000)Anima[#]() at a cost of 1,000 [#](4A0080)Essentia Vitae[#](). "
                + "The price reflects the violence of the element, and if your reserves cannot bear it, "
                + "five hearts are torn from your flesh instead.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Forge the [#](8B0000)Lava Reagent[#]() in the [#](8B0000)Tabula Vitae[#](), then inscribe "
                + "an [#](8B0000)Alchemy Array[#]() with the reagent as base and a [#](8B0000)Tabula Rasa[#]() as "
                + "catalyst.\\\n\\\n[#](4A0080)Handle with the reverence due to hellfire.[#]()");

        this.page("tabula_vitae", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Like its cooler counterpart, the [#](8B0000)Lava Sigil[#]() may serve as a component in the "
                + "[#](8B0000)Tabula Vitae[#]() to automate the production of [#](8B0000)Lava Buckets[#](). "
                + "The sigil is not consumed.");
    }

    @Override
    protected String entryName() {
        return "Lava Sigil";
    }

    @Override
    protected String entryDescription() {
        return "Summon molten stone at a steep cost to your Anima.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_LAVA.get());
    }

    @Override
    protected String entryId() {
        return "sigil_lava";
    }
}
