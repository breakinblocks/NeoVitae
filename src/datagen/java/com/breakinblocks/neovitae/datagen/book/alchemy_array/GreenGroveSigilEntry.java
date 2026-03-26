package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class GreenGroveSigilEntry extends EntryProvider {

    public GreenGroveSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sigil of the Green Grove");
        this.pageText("The [#](8B0000)Sigil of the Green Grove[#]() is an item that has multiple uses. Crafted in an "
                + "array with a [#](8B0000)Growth Reagent[#]() and a [#](8B0000)Reinforced Slate[#](), the sigil can use your "
                + "[#](8B0000)Soul Network[#]()'s LP to nourish and grow nearby plants.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Step 1: Craft the [#](8B0000)Growth Reagent[#]() in the Alchemy Table.\\\n\\\n"
                + "Step 2: Create the [#](8B0000)Sigil of the Green Grove[#]() in an Alchemy Array using the "
                + "Growth Reagent as the base and a Reinforced Slate as the catalyst.");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("If you use the sigil on a block that is growable, it will apply the bonemeal effect "
                + "while consuming 150 LP.\\\n\\\n"
                + "However, if you hold sneak and use while aiming at the air, it will activate and consume "
                + "150 LP every 5 seconds until deactivated. Every block in a 7x7x5 high volume centered "
                + "on the player will have a growth tick applied to it.");
    }

    @Override
    protected String entryName() {
        return "Sigil of the Green Grove";
    }

    @Override
    protected String entryDescription() {
        return "A sigil that accelerates plant growth.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_GREEN_GROVE.get());
    }

    @Override
    protected String entryId() {
        return "sigil_green_grove";
    }
}
