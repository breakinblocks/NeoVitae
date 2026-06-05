package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class PhantomBridgeSigilEntry extends EntryProvider {

    public PhantomBridgeSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sigil of the Phantom Bridge");
        this.pageText("The [#](8B0000)Sigil of the Phantom Bridge[#]() crystallizes [#](4A0080)Essentia Vitae[#]() into "
                + "temporary platforms beneath your feet as you walk. These spectral surfaces allow you to "
                + "traverse chasms, voids, and open sky with the confidence of solid ground.\\\n\\\n"
                + "The phantom blocks dissolve shortly after you leave them behind. "
                + "[#](2E8B57)Keep moving; hesitation over the abyss is unwise.[#]()");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Forge the [#](8B0000)Phantom Bridge Reagent[#]() in the [#](8B0000)Tabula Vitae[#](), then inscribe "
                + "an [#](8B0000)Alchemy Array[#]() with the reagent as base and a slate as catalyst.\\\n\\\n"
                + "[#](4A0080)Between you and the void, blood is the only bridge you need.[#]()");
    }

    @Override
    protected String entryName() {
        return "Sigil of the Phantom Bridge";
    }

    @Override
    protected String entryDescription() {
        return "Walk upon crystallized EV, a bridge born of blood beneath your feet.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_PHANTOM_BRIDGE.get());
    }

    @Override
    protected String entryId() {
        return "sigil_phantom_bridge";
    }
}
