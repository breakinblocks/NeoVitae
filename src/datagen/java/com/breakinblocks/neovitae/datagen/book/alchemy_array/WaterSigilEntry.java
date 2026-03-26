package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class WaterSigilEntry extends EntryProvider {

    public WaterSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Water Sigil");
        this.pageText("The [#](8B0000)Water Sigil[#]() is a rather simple sigil. When you use it while looking at a "
                + "block, you can drain 100 LP from your [#](8B0000)Soul Network[#]() to place a source block of water "
                + "in the world. If there's not enough LP, it will instead drain the toll from your health.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Step 1: Craft the [#](8B0000)Water Reagent[#]() in the Alchemy Table.\\\n\\\n"
                + "Step 2: Create the [#](8B0000)Water Sigil[#]() in an Alchemy Array using the Water Reagent "
                + "as the base and a [#](8B0000)Blank Slate[#]() as the catalyst.\\\n\\\n*Infinite water, anyone?*");

        this.page("alchemy_table", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The Water Sigil can also be used in the [#](8B0000)Alchemy Table[#]() to automate the production "
                + "of [#](8B0000)Water Buckets[#](). The Sigil is not consumed in this recipe.");
    }

    @Override
    protected String entryName() {
        return "Water Sigil";
    }

    @Override
    protected String entryDescription() {
        return "A sigil that places water source blocks.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_WATER.get());
    }

    @Override
    protected String entryId() {
        return "sigil_water";
    }
}
