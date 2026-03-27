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
        this.pageText("The [#](8B0000)Sigil of Magnetism[#]() bends the subtle forces between you and nearby objects, "
                + "extending your item pickup range to 7 blocks while active. Hold the sigil, then hold "
                + "sneak and press [Use] to toggle the effect.\\\n\\\n"
                + "The sigil drains 50 [#](4A0080)Essentia Vitae[#]() every 5 seconds while its pull persists.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Forge the [#](8B0000)Magnetism Reagent[#]() in the [#](8B0000)Tabula Vitae[#](), then inscribe "
                + "an [#](8B0000)Alchemy Array[#]() with the reagent as base and a slate as catalyst.\\\n\\\n"
                + "[#](4A0080)The world draws near to those who command it.[#]()");
    }

    @Override
    protected String entryName() {
        return "Sigil of Magnetism";
    }

    @Override
    protected String entryDescription() {
        return "Extend your reach; items drift toward you unbidden.";
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
