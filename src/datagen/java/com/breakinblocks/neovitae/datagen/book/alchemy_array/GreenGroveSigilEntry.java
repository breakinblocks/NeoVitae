package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class GreenGroveSigilEntry extends EntryProvider {

    public GreenGroveSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sigil of the Green Grove");
        this.pageText("The [#](8B0000)Sigil of the Green Grove[#]() channels [#](4A0080)Essentia Vitae[#]() into the "
                + "living earth, coaxing plants to grow with unnatural speed. A marriage of blood and botany "
                + "that any practitioner with a garden will find indispensable.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Forge the [#](8B0000)Growth Reagent[#]() in the [#](8B0000)Tabula Vitae[#](), then inscribe "
                + "an [#](8B0000)Alchemy Array[#]() with the reagent as base and a [#](8B0000)Tabula Robur[#]() "
                + "as catalyst.");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Aim at a growable block and press [Use] to apply a burst of growth for 150 "
                + "[#](4A0080)Essentia Vitae[#]().\\\n\\\n"
                + "Alternatively, hold sneak and press [Use] to toggle a persistent aura. While active, "
                + "every block within a 7x7x5 volume centered on you receives a growth pulse every 5 seconds, "
                + "at a cost of 150 [#](4A0080)Essentia Vitae[#]() per cycle.");
    }

    @Override
    protected String entryName() {
        return "Sigil of the Green Grove";
    }

    @Override
    protected String entryDescription() {
        return "Feed life into the soil and watch the world bloom around you.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_GREEN_GROVE.get());
    }

    @Override
    protected String entryId() {
        return "sigil_green_grove";
    }
}
