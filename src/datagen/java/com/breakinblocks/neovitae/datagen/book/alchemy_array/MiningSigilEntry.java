package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

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
        this.pageText("The [#](8B0000)Sigil of the Fast Miner[#]() infuses your muscles with vitaemantic vigor, "
                + "granting the Haste effect while active. Stone yields faster, timber falls quicker, earth "
                + "parts more readily.\\\n\\\n"
                + "Toggle with sneak and [Use]. Drains 100 [#](4A0080)Essentia Vitae[#]() every 5 seconds while "
                + "the effect persists.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Forge the [#](8B0000)Mining Reagent[#]() in the [#](8B0000)Tabula Vitae[#](), then inscribe "
                + "an [#](8B0000)Alchemy Array[#]() with the reagent as base and a [#](8B0000)Tabula Robur[#]() "
                + "as catalyst.\\\n\\\n"
                + "[#](4A0080)The earth parts before the blood-quickened hand.[#]()");
    }

    @Override
    protected String entryName() {
        return "Sigil of the Fast Miner";
    }

    @Override
    protected String entryDescription() {
        return "Blood-fueled haste for those who delve deep.";
    }

    @Override
    protected GuiSprite entryBackground() {
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
