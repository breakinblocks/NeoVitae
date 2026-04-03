package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class ArcaneAshEntry extends EntryProvider {

    public ArcaneAshEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Alchemy Array");
        this.pageText("Before sigils, before rituals, before the great workings of [#](4A0080)Vitaemancy[#]() "
                + "were ever conceived, there was the [#](8B0000)Alchemy Array[#](). Drawn in [#](8B0000)Arcane Ashes[#]() "
                + "upon bare stone or earth, the array is the most fundamental expression of the art "
                + ", a circle of intent, waiting to be given purpose.");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("To inscribe an array, hold [#](8B0000)Arcane Ashes[#]() and press [Use] upon a solid surface. "
                + "Each inscription consumes one charge from the ashes, which hold twenty uses before crumbling "
                + "to dust.\\\n\\\n"
                + "The empty array is inert. Click it with an item to place the [#](8B0000)base[#]() component. "
                + "The design shifts if the base is recognized.");

        this.page("inputs", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("A second item placed becomes the [#](8B0000)catalyst[#](). If both are valid, "
                + "the array awakens and its working begins. Both components are [#](8B0000)consumed[#]() "
                + "as the array activates; nothing remains but the result of the working.\\\n\\\n"
                + "[#](2E8B57)Watch the pattern carefully when you place the base. If the glyph changes form, "
                + "you are on the right path.[#]()");

        this.page("arrays", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The arrays catalogued in this chapter range from simple [#](8B0000)crafting arrays[#]() that reshape "
                + "matter, to complex inscriptions that forge [#](4A0080)sigils[#](), bind [#](4A0080)living armour[#](), "
                + "and even bend the passage of time. Master the fundamentals, and the rest shall follow.");
    }

    @Override
    protected String entryName() {
        return "The Alchemy Array";
    }

    @Override
    protected String entryDescription() {
        return "The foundation of all vitaemantic craft, circles drawn in ash, awaiting purpose.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.ARCANE_ASHES.get());
    }

    @Override
    protected String entryId() {
        return "arcane_ash";
    }
}
