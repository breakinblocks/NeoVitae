package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class CraftingArrayEntry extends EntryProvider {

    public CraftingArrayEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crafting with Arrays");
        this.pageText("[#](8B0000)Crafting Arrays[#]() are among the simplest expressions of the art. The array "
                + "inscribes the essence of the [#](8B0000)base[#]() onto the [#](8B0000)catalyst[#](), transmuting "
                + "both into something new. A brief, elegant animation accompanies the transformation.");

        this.page("details", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Some crafting arrays require only common materials. Others demand reagents forged "
                + "through more advanced means; the complexity of the product mirrors the complexity of "
                + "the path to reach it.\\\n\\\n"
                + "[#](2E8B57)Most sigils, reagents, and bindings begin their existence within a crafting "
                + "array. Master this foundation well.[#]()");
    }

    @Override
    protected String entryName() {
        return "Crafting with Arrays";
    }

    @Override
    protected String entryDescription() {
        return "The simplest array, inscribe essence onto matter and reshape it.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_DIVINATION.get());
    }

    @Override
    protected String entryId() {
        return "crafting_array";
    }
}
