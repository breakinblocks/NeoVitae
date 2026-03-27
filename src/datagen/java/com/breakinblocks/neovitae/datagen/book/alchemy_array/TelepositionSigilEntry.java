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
        this.pageText("The [#](8B0000)Teleposition Sigil[#]() tears a momentary rift between you and a linked "
                + "[#](8B0000)Teleposer[#](), pulling you across any distance, even between dimensions, for "
                + "1,000 [#](4A0080)Essentia Vitae[#]().\\\n\\\n"
                + "Sneak-click on a [#](8B0000)Teleposer[#]() to bind its location to the sigil. "
                + "[#](2E8B57)Do not move the Teleposer afterward, or you may find yourself arriving "
                + "somewhere... unexpected.[#]()");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Forge the [#](8B0000)Teleposition Reagent[#]() in the [#](8B0000)Tabula Vitae[#](), then inscribe "
                + "an [#](8B0000)Alchemy Array[#]() with the reagent as base and a slate as catalyst.\\\n\\\n"
                + "[#](4A0080)The distance between here and there is merely a matter of blood.[#]()");
    }

    @Override
    protected String entryName() {
        return "Teleposition Sigil";
    }

    @Override
    protected String entryDescription() {
        return "Tear through space itself to reach a bound Teleposer.";
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
