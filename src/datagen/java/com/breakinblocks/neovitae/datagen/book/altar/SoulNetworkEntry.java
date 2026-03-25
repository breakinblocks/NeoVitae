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
        this.pageText("The **Soul Network** is the network that connects your **Soul** to all of your bound "
                + "items, rituals and blocks. Functionally, it is a global storage of **LP** unique to each "
                + "player that can be added to and extracted from, using the player's bound items as an "
                + "intermediary. When you first right-click with an item that can be bound to a **Soul Network**, "
                + "it will bind to you and will be labeled as");

        this.page("binding", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("being \"owned\" by you. As such, any action that the item does that has an **LP cost** "
                + "will drain from your **Soul Network**. In some cases, if the item cannot get its **LP** from "
                + "the **Soul Network**, it will instead directly take the **LP** cost from your health.\\\n\\\n"
                + "In other cases (such as when an ongoing **Ritual** drains your network completely), they will "
                + "merely cause unceasing nausea until either the Ritual is deactivated or your **Soul Network** "
                + "is re-filled.");

        this.page("filling", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("In order to fill your **Soul Network**, you will need to construct a **Blood Orb**."
                + "\\\n\\\nBlood Orbs can be charged with **LP** in one of two ways."
                + "\n- A player can sacrifice 1 heart of health by right-clicking with the **Blood Orb**, "
                + "providing the bound **Soul Network** with **200 LP**."
                + "\n- The **Blood Orb** can be placed inside a **Blood Altar** with some **Life Essence** in "
                + "it. The Orb will absorb it as fast as it can, limited by your Altar's **Speed Runes**.");

        this.page("orb_tiers", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("There is a separate **Blood Orb** that can be created for each Tier of the **Blood Altar**."
                + "\n- **Weak Blood Orb** - Max capacity: **5k LP**."
                + "\n- **Apprentice Blood Orb** - Max capacity: **25k LP**."
                + "\n- **Magician Blood Orb** - Max capacity: **150k LP**."
                + "\n- **Master Blood Orb** - Max capacity: **1M LP**."
                + "\n- **Archmage Blood Orb** - Max capacity: **10M LP**.");

        this.page("weak_apprentice", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Blood Orb Recipes");
        this.pageText("Craft the **Weak Blood Orb** in the Blood Altar (Tier 1, cost: 2,000 LP).\\\n\\\n"
                + "Craft the **Apprentice Blood Orb** in the Blood Altar (Tier 2, cost: 5,000 LP).");

        this.page("magician_master", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Craft the **Magician Blood Orb** in the Blood Altar (Tier 3, cost: 25,000 LP).\\\n\\\n"
                + "Craft the **Master Blood Orb** in the Blood Altar (Tier 4, cost: 50,000 LP).");

        this.page("archmage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Craft the **Archmage Blood Orb** in the Blood Altar (Tier 5, cost: 80,000 LP).\\\n\\\n"
                + "If that's still not enough **LP storage** for you, consider using **Runes of the Orb**.");
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
