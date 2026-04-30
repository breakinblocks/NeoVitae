package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class CrystallizedSpiritusEntry extends EntryProvider {

    public CrystallizedSpiritusEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crystallized Spiritus");
        this.pageText("Now that your [#](8B0000)Spiritus Gem[#]() brims with [#](8B0000)Spiritus[#](), you may wonder what happens "
                + "when that malice is unleashed upon the world itself. The answer begins with saturating "
                + "the [#](8B0000)Aura[#](), and continues with the [#](8B0000)Crystallarium Maleficum[#]().");

        this.page("crystallizer", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crystallarium Maleficum");
        this.pageText("This device slowly draws [#](8B0000)Spiritus[#]() from the [#](8B0000)Aura[#]() and condenses it into physical "
                + "[#](8B0000)Spiritus Crystals[#](). The first spire demands 100 Spiritus to form; each subsequent growth costs "
                + "45, yet yields 50 when burned in a [#](8B0000)Vas Maleficum[#](), a net gain of 5 per spire. "
                + "A cluster may grow up to 7 spires tall.");

        this.page("harvesting", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("If you carry more than [#](B8860B)512 total Spiritus[#]() in your inventory (across any number of "
                + "[#](8B0000)Spiritus Gems[#](), of any single type), you may harvest these crystals by right-clicking "
                + "the cluster with an empty hand. This strips all but the central spire.\\\n\\\n"
                + "[#](2E8B57)In a hurry, or desperate for that last crystal? A pickaxe will shatter the entire "
                + "cluster at once.[#]()");

        this.page("related", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Manual harvest yields a modest five Spiritus per cluster. To industrialise crystal "
                + "production, build the [#](8B0000)Crystallum Fractura[#]() ritual: it auto-harvests every fully-grown "
                + "cluster in range, doubles crystal growth speed, and amplifies any Spiritus injection by +25%%. "
                + "To bootstrap aspected lineages, apply a [#](8B0000)Spiritus Catalyst[#]() (one per aspect) to a "
                + "fully-grown Raw cluster; the catalyst consumes one [#](8B0000)Animus Mote[#]() and transmutes the "
                + "cluster into its target Aspect. See the Spiritus Catalysts entry for the full loop.");
    }

    @Override
    protected String entryName() {
        return "Crystallized Spiritus";
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
        return BookIconModel.create(NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get());
    }

    @Override
    protected String entryId() {
        return "crystallized_spiritus";
    }
}
