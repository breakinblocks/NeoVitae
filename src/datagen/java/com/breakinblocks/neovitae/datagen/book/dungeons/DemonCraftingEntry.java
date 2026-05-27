package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class DemonCraftingEntry extends EntryProvider {

    public DemonCraftingEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Forged from Damnation");
        this.pageText("The [#](8B0000)Hellfire Forge[#]() can transmute daemonium trophies into items of "
                + "terrible power. All recipes in this section require at minimum a tier 3 altar and "
                + "blood orb to have reached the dungeons in the first place.");

        this.page("sigil_damned", () -> BookSpotlightPageModel.create()
                .withItem(NVItems.SIGIL_DAMNED.get())
                .withTitle("Sigil of the Damned")
                .withText(this.context().pageText()));
        this.pageText("A passive sigil that empowers the bearer when held. Grants "
                + "[#](4A0080)+50 Bonus Spiritus Collection[#](), [#](8B0000)+25 Bonus Sacrifice[#](), "
                + "and [#](8B0000)+10 Blood Siphon[#]() (healing a portion of the target's health on kill, "
                + "capped at 4 hearts).\\\n\\\n"
                + "Forged from a [#](8B0000)Cinder Heart Fragment[#](), [#](8B0000)Permafrost Core[#](), "
                + "[#](4A0080)Greater Spiritus Gem[#](), and [#](8B0000)Tabula Spiritus[#]().");

        this.page("blight_whetstone", () -> BookSpotlightPageModel.create()
                .withItem(NVItems.BLIGHT_WHETSTONE.get())
                .withTitle("Blight Whetstone")
                .withText(this.context().pageText()));
        this.pageText("A single-use anointment applicator that coats a weapon in Spiritus Ruina, "
                + "inflicting [#](8B0000)Wither[#]() on struck targets for 60 seconds.\\\n\\\n"
                + "Requires [#](4A0080)Spiritus Ruina[#]() in the forge. Crafted from "
                + "[#](8B0000)Blight Marrow[#](), a [#](4A0080)Ruina Crystal Catalyst[#](), "
                + "and a [#](8B0000)Tabula Spiritus[#]().");

        this.page("trim_ingot", () -> BookSpotlightPageModel.create()
                .withItem(NVItems.DEMONITE_TRIM_INGOT.get())
                .withTitle("Demonite Trim Ingot")
                .withText(this.context().pageText()));
        this.pageText("A deep blood-red trim material for armour decoration. Apply it at a smithing "
                + "table like any other trim material to give your gear a [#](8B0000)demonic[#]() accent "
                + "unique to the Demon Realm.\\\n\\\n"
                + "Forged from [#](8B0000)Demonite Fragments[#](), an [#](4A0080)Animus Mote[#](), "
                + "and a [#](8B0000)Weak Blood Shard[#]().");

        this.page("grand_gem", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Grand Spiritus Gem");
        this.pageText("The [#](4A0080)Grand Spiritus Gem[#]() can now be forged using materials from "
                + "the Demon Realm. It requires a [#](4A0080)Greater Spiritus Gem[#](), an "
                + "[#](8B0000)Animus Mote[#](), an [#](8B0000)Tabula Aetherea[#](), and a "
                + "[#](8B0000)Permafrost Core[#]().\\\n\\\n"
                + "With 4000 minimum will and 400 drain, this is among the most demanding recipes "
                + "in the forge.");
    }

    @Override
    protected String entryName() {
        return "Forged from Damnation";
    }

    @Override
    protected String entryDescription() {
        return "Powerful items crafted from demon trophies in the Hellfire Forge.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_DAMNED.get());
    }

    @Override
    protected String entryId() {
        return "demon_crafting";
    }
}
