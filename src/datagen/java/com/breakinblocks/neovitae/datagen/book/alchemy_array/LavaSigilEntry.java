package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class LavaSigilEntry extends EntryProvider {

    public LavaSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Lava Sigil");
        this.pageText("The sister sigil to the **Water Sigil**. Use the **Lava Sigil** to create a source "
                + "block of lava on the ground, for the cost of 1000 LP. It'll drain 5 hearts from you if "
                + "you don't have enough LP in your **Soul Network**.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Step 1: Craft the **Lava Reagent** in the Alchemy Table.\n\n"
                + "Step 2: Create the **Lava Sigil** in an Alchemy Array using the Lava Reagent "
                + "as the base and a **Blank Slate** as the catalyst.\n\n*HOT! DO NOT EAT*");

        this.page("alchemy_table", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The Lava Sigil can also be used in the **Alchemy Table** to automate the production "
                + "of **Lava Buckets**. The Sigil is not consumed in this recipe.");
    }

    @Override
    protected String entryName() {
        return "Lava Sigil";
    }

    @Override
    protected String entryDescription() {
        return "A sigil that places lava source blocks.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_LAVA.get());
    }

    @Override
    protected String entryId() {
        return "sigil_lava";
    }
}
