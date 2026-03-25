package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class BloodLampSigilEntry extends EntryProvider {

    public BloodLampSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sigil of the Blood Lamp");
        this.pageText("The **Sigil of the Blood Lamp** is a handy tool for any miner, dungeon delver, or "
                + "simply any Sanguimancer that doesn't like dark patches and feels that torches and glowstone "
                + "blocks get in the way. When used, this sigil launches a Blood Light in the direction you "
                + "are facing. When it hits a block, it spawns a nearly-invisible light source at a cost of 10 LP.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Step 1: Craft the **Blood Lamp Reagent** in the Alchemy Table.\\\n\\\n"
                + "Step 2: Create the **Sigil of the Blood Lamp** in an Alchemy Array using the "
                + "Blood Lamp Reagent as the base and a slate as the catalyst.\\\n\\\n*I see a light!*");
    }

    @Override
    protected String entryName() {
        return "Sigil of the Blood Lamp";
    }

    @Override
    protected String entryDescription() {
        return "A sigil that shoots invisible light sources.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_BLOOD_LIGHT.get());
    }

    @Override
    protected String entryId() {
        return "sigil_blood_lamp";
    }
}
