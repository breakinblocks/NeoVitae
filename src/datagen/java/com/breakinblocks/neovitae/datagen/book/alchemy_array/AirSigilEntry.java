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
        this.pageText("The [#](8B0000)Air Sigil[#]() hurls you through the sky in whatever direction you face, "
                + "trading 50 [#](4A0080)Essentia Vitae[#]() for each burst of momentum. The wind obeys, but gravity does "
                + "not -- this sigil offers no protection from the landing.\\\n\\\n"
                + "[#](2E8B57)More than one overconfident apprentice has drained their [#](8B0000)Anima[#]() mid-flight. "
                + "Know your reserves before you leap.[#]()");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Forge the [#](8B0000)Air Reagent[#]() in the [#](8B0000)Tabula Vitae[#](), then inscribe "
                + "an [#](8B0000)Alchemy Array[#]() with the reagent as base and a slate as catalyst.\\\n\\\n"
                + "[#](4A0080)The sky remembers every mage who forgot to look down.[#]()");
    }

    @Override
    protected String entryName() {
        return "Air Sigil";
    }

    @Override
    protected String entryDescription() {
        return "Command the wind to hurl you skyward -- at your own peril.";
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
