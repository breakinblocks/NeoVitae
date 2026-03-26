package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class SoulNetworkEntry extends EntryProvider {

    public SoulNetworkEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Soul Network");
        this.pageText("The [#](8B0000)Soul Network[#]() is the network that connects your [#](8B0000)Soul[#]() to all of your bound "
                + "items, rituals and blocks. Functionally, it is a global storage of [#](8B0000)LP[#]() unique to each "
                + "player that can be added to and extracted from, using the player's bound items as an "
                + "intermediary. When you first right-click with an item that can be bound to a [#](8B0000)Soul Network[#](), "
                + "it will bind to you and will be labeled as");

        this.page("binding", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("being \"owned\" by you. As such, any action that the item does that has an [#](8B0000)LP cost[#]() "
                + "will drain from your [#](8B0000)Soul Network[#](). In some cases, if the item cannot get its [#](8B0000)LP[#]() from "
                + "the [#](8B0000)Soul Network[#](), it will instead directly take the [#](8B0000)LP[#]() cost from your health.\\\n\\\n"
                + "In other cases (such as when an ongoing [#](8B0000)Ritual[#]() drains your network completely), they will "
                + "merely cause unceasing nausea until either the Ritual is deactivated or your [#](8B0000)Soul Network[#]() "
                + "is re-filled.");

        this.page("filling", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("In order to fill your [#](8B0000)Soul Network[#](), you will need to construct a [#](8B0000)Blood Orb[#]()."
                + "\\\n\\\nBlood Orbs can be charged with [#](8B0000)LP[#]() in one of two ways."
                + "\n\n- A player can sacrifice 1 heart of health by right-clicking with the [#](8B0000)Blood Orb[#](), "
                + "providing the bound [#](8B0000)Soul Network[#]() with [#](8B0000)200 LP[#]()."
                + "\n\n- The [#](8B0000)Blood Orb[#]() can be placed inside a [#](8B0000)Ara Vitae[#]() with some [#](8B0000)Life Essence[#]() in "
                + "it. The Orb will absorb it as fast as it can, limited by your Altar's [#](8B0000)Speed Runes[#]().");

        this.page("orb_tiers", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("There is a separate [#](8B0000)Blood Orb[#]() that can be created for each Tier of the [#](8B0000)Ara Vitae[#]()."
                + "\n\n- [#](8B0000)Weak Blood Orb[#]() - Max capacity: [#](8B0000)5k LP[#]()."
                + "\n\n- [#](8B0000)Apprentice Blood Orb[#]() - Max capacity: [#](8B0000)25k LP[#]()."
                + "\n\n- [#](8B0000)Magician Blood Orb[#]() - Max capacity: [#](8B0000)150k LP[#]()."
                + "\n\n- [#](8B0000)Master Blood Orb[#]() - Max capacity: [#](8B0000)1M LP[#]()."
                + "\n\n- [#](8B0000)Archmage Blood Orb[#]() - Max capacity: [#](8B0000)10M LP[#]().");

        this.page("weak_apprentice", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Blood Orb Recipes");
        this.pageText("Craft the [#](8B0000)Weak Blood Orb[#]() in the Ara Vitae (Tier 1, cost: 2,000 LP).\\\n\\\n"
                + "Craft the [#](8B0000)Apprentice Blood Orb[#]() in the Ara Vitae (Tier 2, cost: 5,000 LP).");

        this.page("magician_master", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Craft the [#](8B0000)Magician Blood Orb[#]() in the Ara Vitae (Tier 3, cost: 25,000 LP).\\\n\\\n"
                + "Craft the [#](8B0000)Master Blood Orb[#]() in the Ara Vitae (Tier 4, cost: 50,000 LP).");

        this.page("archmage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Craft the [#](8B0000)Archmage Blood Orb[#]() in the Ara Vitae (Tier 5, cost: 80,000 LP).\\\n\\\n"
                + "If that's still not enough [#](8B0000)LP storage[#]() for you, consider using [#](8B0000)Runes of the Orb[#]().");
    }

    @Override
    protected String entryName() {
        return "Soul Network";
    }

    @Override
    protected String entryDescription() {
        return "The global LP storage that powers your bound items and rituals.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.ORB_WEAK.get());
    }

    @Override
    protected String entryId() {
        return "soul_network";
    }
}
