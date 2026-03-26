package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class BookExperienceEntry extends EntryProvider {

    public BookExperienceEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tome of Peritia");
        this.pageText("The [#](8B0000)Tome of Peritia[#]() allows you to safely store your experience.\\\n\\\n"
                + "Pressing Sneak and Use with the Tome in hand stores one level of XP. "
                + "Pressing Use retrieves a level. Hold Use to store/retrieve multiple levels.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("View the Tome of Peritia recipe in JEI.");

        this.page("curios", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Curios Integration");
        this.pageText("If you have the Curios API installed, you can equip the Tome of Peritia as a charm. "
                + "If you want to wear more curios at once, consider using a Sigil of Holding, "
                + "or the Socketed Upgrade for your Living Armour.");
    }

    @Override
    protected String entryName() {
        return "Tome of Peritia";
    }

    @Override
    protected String entryDescription() {
        return "Store and retrieve experience levels.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.EXPERIENCE_TOME.get());
    }

    @Override
    protected String entryId() {
        return "book_experience";
    }
}
