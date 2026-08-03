package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class DemonDropsEntry extends EntryProvider {

    public DemonDropsEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Daemonium Trophies");
        this.pageText("Each species of [#](8B0000)Daemonium[#]() yields a unique trophy material when slain. "
                + "These materials cannot be obtained any other way and serve as key ingredients in recipes "
                + "that require a tier 3 altar or higher to even begin approaching.");

        this.page("fodder_drops", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fodder Trophies");
        this.pageText("[#](8B0000)Gore-Clotted Fang[#]() (Cruoris): A bloodied claw fragment, used in "
                + "blood-themed recipes and infusions.\\\n\\\n"
                + "[#](8B0000)Venomgland Sac[#]() (Pestis): An intact poison sac, potent in flask and "
                + "potion crafting.\\\n\\\n"
                + "[#](8B0000)Ectoplasmic Residue[#]() (Rancoris): Shimmering spectral goo, vital for "
                + "ghostly and combat brews.\\\n\\\n"
                + "[#](8B0000)Animus Mote[#]() (Animaris): A flickering soul fragment; the rarest fodder "
                + "drop, essential for soul-binding recipes.");

        this.page("standard_drops", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Standard and Elite Trophies");
        this.pageText("[#](8B0000)Hollow Gut[#]() (Voraxis): A distended organ that never fills, used in "
                + "hunger and drain recipes.\\\n\\\n"
                + "[#](8B0000)Blight Marrow[#]() (Corrodis): Corroded bone seeping dark mist, key to wither "
                + "and Ruina crafting.\\\n\\\n"
                + "[#](8B0000)Revenant Plate[#]() (Fervidis): A chunk of undying chitin, prized for defensive "
                + "and armor crafting.\\\n\\\n"
                + "[#](8B0000)Frozen Marrow Shard[#]() (Doloris): Ice-veined bone, combining cold and anguish "
                + "in a single reagent.");

        this.page("boss_drops", () -> BookSpotlightPageModel.create()
                .withItem(NVItems.PERMAFROST_CORE.get())
                .withTitle("Apex Trophies")
                .withText(this.context().pageText()));
        this.pageText("[#](8B0000)Cinder Heart Fragment[#]() (Ignis): A smoldering ember core that never "
                + "fully cools. Combined with the [#](8B0000)Permafrost Core[#]() from Glaciaris, these two "
                + "materials form the foundation of the most powerful demon-forged items, including the "
                + "[#](4A0080)Sigil of the Damned[#]().");
    }

    @Override
    protected String entryName() {
        return "Daemonium Trophies";
    }

    @Override
    protected String entryDescription() {
        return "Unique materials harvested from slain demons, essential for advanced crafting.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.CINDER_HEART_FRAGMENT.get());
    }

    @Override
    protected String entryId() {
        return "demon_drops";
    }
}
