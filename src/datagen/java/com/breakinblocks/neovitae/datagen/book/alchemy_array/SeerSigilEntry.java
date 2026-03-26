package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class SeerSigilEntry extends EntryProvider {

    public SeerSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Seer's Sigil");
        this.pageText("The [#](8B0000)Seer's Sigil[#]() is a more advanced form of the [#](8B0000)Divination Sigil[#](). Alongside "
                + "showing the amount of LP in the bound player's [#](8B0000)Soul Network[#](), it also shows more "
                + "information when looking at a [#](8B0000)Ara Vitae[#]().\\\n\\\n"
                + "Like the Divination Sigil, it can also be used to edit your GUI.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Step 1: Craft the [#](8B0000)Sight Reagent[#]() in the Alchemy Table.\\\n\\\n"
                + "Step 2: Create the [#](8B0000)Seer's Sigil[#]() in an Alchemy Array using the "
                + "Sight Reagent as the base and a slate as the catalyst.\\\n\\\n*When seeing all is not enough*");

        this.page("hud", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("From top to bottom, the Seer's Sigil HUD shows:\n"
                + "- The current Tier of the Ara Vitae\n"
                + "- The amount of blood currently inside the Altar, and the current total capacity\n"
                + "- The current crafting progress, if any\n"
                + "- LP Consumption/Tick - how much LP the Altar will use per tick when crafting\n"
                + "- Current LP Storage of any Charging Runes you may have");
    }

    @Override
    protected String entryName() {
        return "Seer's Sigil";
    }

    @Override
    protected String entryDescription() {
        return "An advanced divination sigil with more detailed altar information.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_SEER.get());
    }

    @Override
    protected String entryId() {
        return "sigil_seer";
    }
}
