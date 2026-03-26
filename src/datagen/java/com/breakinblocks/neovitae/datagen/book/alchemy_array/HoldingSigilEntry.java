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
        this.pageText("The [#](8B0000)Sigil of Holding[#]() is a vessel within a vessel -- capable of containing "
                + "up to five other [#](8B0000)Sigils[#]() simultaneously. While held, it channels their passive "
                + "effects and allows you to activate any of them at will.\\\n\\\n"
                + "Press the Open Holding keybind to access its inventory. Use the cycle keybinds or hold "
                + "sneak and scroll to switch between stored sigils.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Forge the [#](8B0000)Holding Reagent[#]() in the [#](8B0000)Tabula Vitae[#](), then inscribe "
                + "an [#](8B0000)Alchemy Array[#]() with the reagent as base and a slate as catalyst.\\\n\\\n"
                + "[#](4A0080)One hand to hold the world's power.[#]()");
    }

    @Override
    protected String entryName() {
        return "Sigil of Holding";
    }

    @Override
    protected String entryDescription() {
        return "Carry five sigils in one -- their power at your fingertips.";
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
