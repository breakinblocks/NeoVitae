package com.breakinblocks.neovitae.datagen.book.demon_will;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class CrystallizedWillEntry extends EntryProvider {

    public CrystallizedWillEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crystallized Will");
        this.pageText("Now that your [#](8B0000)Tartaric Gem[#]() brims with [#](8B0000)Demon Will[#](), you may wonder what happens "
                + "when that malice is unleashed upon the world itself. The answer begins with saturating "
                + "the [#](8B0000)Aura[#](), and continues with the [#](8B0000)Crystallarium Maleficum[#]().");

        this.page("crystallizer", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crystallarium Maleficum");
        this.pageText("This device slowly draws [#](8B0000)Demon Will[#]() from the [#](8B0000)Aura[#]() and condenses it into physical "
                + "[#](8B0000)Will Crystals[#](). The first spire demands 100 Will to form; each subsequent growth costs "
                + "45, yet yields 50 when burned in a [#](8B0000)Vas Maleficum[#]() -- a net gain of 5 per spire. "
                + "A cluster may grow up to 7 spires tall.");

        this.page("harvesting", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("If you carry more than [#](B8860B)512 total Will[#]() in your inventory (across any number of "
                + "[#](8B0000)Tartaric Gems[#](), of any single type), you may harvest these crystals by right-clicking "
                + "the cluster with an empty hand. This strips all but the central spire.\\\n\\\n"
                + "[#](2E8B57)In a hurry, or desperate for that last crystal? A pickaxe will shatter the entire "
                + "cluster at once.[#]()");

        this.page("related", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Five Will per harvest is a modest return, but rituals such as the "
                + "[#](8B0000)Resonance of the Faceted Crystal[#](), the [#](8B0000)Catalyst of the Forsaken Souls[#](), and the "
                + "[#](8B0000)Crack of the Fractured Crystal[#]() -- alongside [#](8B0000)Will Catalysts[#]() -- can accelerate and "
                + "automate crystal production to impressive effect.");
    }

    @Override
    protected String entryName() {
        return "Crystallized Will";
    }

    @Override
    protected String entryDescription() {
        return "Condensing raw malice from the Aura into physical crystal.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.RAW_CRYSTAL.get());
    }

    @Override
    protected String entryId() {
        return "crystallized_will";
    }
}
