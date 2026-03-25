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
        this.pageTitle("Alchemy Array Basics");
        this.pageText("**Arcane Ashes** are an item that is pivotal in the creation of Alchemy Arrays. "
                + "**Arcane Ashes** can be crafted in the **Alchemy Table** using some early game items.");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("In order to create an Alchemy Array, press [Use] while looking at a block with the "
                + "**Arcane Ashes** in hand. This will consume 1 durability out of 20 from the **Arcane Ashes** "
                + "and draw a simple **Alchemy Array**, that by itself has no effects.\\\n\\\n"
                + "When you click on the **Alchemy Array**, it will consume a single **item** from the stack "
                + "in your hand and hold it in the array. These items are then used to determine the "
                + "**Alchemy Array**'s effect.");

        this.page("inputs", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Each effect requires two items: a **base** and a **catalyst**. The **base** is the "
                + "first item that you click the array with after it is drawn, and the **catalyst** is the "
                + "second item. When you apply the base item, the design of the array will change if it is "
                + "valid, and the array will activate once you apply the catalyst.");

        this.page("arrays", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Although only a few arrays are currently implemented, eventually you will have arrays "
                + "that range from simple **crafting arrays** to even teleportation arrays.");
    }

    @Override
    protected String entryName() {
        return "Alchemy Array Basics";
    }

    @Override
    protected String entryDescription() {
        return "The fundamentals of creating and using Alchemy Arrays.";
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
