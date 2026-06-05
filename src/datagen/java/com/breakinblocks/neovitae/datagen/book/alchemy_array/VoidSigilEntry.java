package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class VoidSigilEntry extends EntryProvider {

    public VoidSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Void Sigil");
        this.pageText("The [#](8B0000)Void Sigil[#]() annihilates fluid on contact, consuming 50 "
                + "[#](4A0080)Essentia Vitae[#]() per block erased. Aim at any liquid and press [Use] to watch it "
                + "vanish without a trace.\\\n\\\n"
                + "Invaluable for clearing treacherous lava flows or unwanted floods without the tedium "
                + "of conventional methods.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Forge the [#](8B0000)Void Reagent[#]() in the [#](8B0000)Tabula Vitae[#](), then inscribe "
                + "an [#](8B0000)Alchemy Array[#]() with the reagent as base and a slate as catalyst.\\\n\\\n"
                + "[#](4A0080)What the void takes, it does not return.[#]()");
    }

    @Override
    protected String entryName() {
        return "Void Sigil";
    }

    @Override
    protected String entryDescription() {
        return "Erase fluid from existence with a single gesture.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_VOID.get());
    }

    @Override
    protected String entryId() {
        return "sigil_void";
    }
}
