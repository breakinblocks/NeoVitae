package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import com.breakinblocks.neovitae.common.item.NVItems;

public class SpiritSiphonArrayEntry extends EntryProvider {

    public SpiritSiphonArrayEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spirit Siphon Array");
        this.pageText("This array combines the cruelty of the Spike Array with a deeper purpose. "
                + "When a non-player creature steps onto it, the array inflicts a full heart of damage "
                + "and extracts a fragment of raw [#](8B0000)Spiritus[#]() from the act of suffering, "
                + "releasing it into the surrounding chunk.\\\n\\\n"
                + "The array has a brief cooldown between activations to prevent overwhelming the local "
                + "aura with too much spiritus at once.");

        this.page("details", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Requires a [#](8B0000)Tabula Animata[#]() to inscribe, reflecting the deeper understanding "
                + "of spiritual energy needed to harness it.\\\n\\\n"
                + "Each activation releases 0.5 units of raw (default) spiritus into the chunk. Players are "
                + "immune to its effects.\\\n\\\n"
                + "[#](4A0080)Every creature carries a spark of will. This array simply... liberates it.[#]()");
    }

    @Override
    protected String entryName() {
        return "Spirit Siphon Array";
    }

    @Override
    protected String entryDescription() {
        return "A trap that damages mobs and harvests raw spiritus.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.TABULA_ANIMATA.get());
    }

    @Override
    protected String entryId() {
        return "spirit_siphon_array";
    }
}
