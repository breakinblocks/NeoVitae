package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

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
        this.pageText("The [#](8B0000)Sigil of the Phantom Bridge[#](), when activated via pressing [Use], will create "
                + "temporary phantom blocks beneath your feet as you walk. These ethereal platforms allow you "
                + "to traverse chasms and voids with ease.\\\n\\\n"
                + "The phantom blocks disappear shortly after you move "
                + "away from them, so keep moving! Perfect for building in dangerous areas or crossing large gaps.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Step 1: Craft the [#](8B0000)Phantom Bridge Reagent[#]() in the Tabula Vitae.\\\n\\\n"
                + "Step 2: Create the [#](8B0000)Sigil of the Phantom Bridge[#]() in an Alchemy Array using the "
                + "Phantom Bridge Reagent as the base and a slate as the catalyst.\\\n\\\n*Walk on air!*");
    }

    @Override
    protected String entryName() {
        return "Sigil of the Phantom Bridge";
    }

    @Override
    protected String entryDescription() {
        return "A sigil that creates temporary blocks beneath your feet.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
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
