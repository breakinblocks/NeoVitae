package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class EditingFiltersEntry extends EntryProvider {

    public EditingFiltersEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Editing Filters");
        this.pageText("When configuring many [#](8B0000)Filters[#]() to identical specifications, the tedium of "
                + "hand-setting each one grows tiresome. Instead, place between two and nine filters into a "
                + "crafting table -- the filter in slot one imprints its configuration onto all the rest.");

        this.page("copying", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Duplicating Configurations");
        this.pageText("Place one configured filter in the first slot, and blank filters of the same type in any "
                + "remaining slots. The result: perfect copies with all settings faithfully duplicated.");

        this.page("clearing", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Should you wish to wipe a filter clean and start anew, place it alone in a crafting table.");

        this.page("clearing_details", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Clearing Filters");
        this.pageText("A single configured filter placed alone in a crafting table yields a blank filter with all "
                + "settings restored to their defaults.");
    }

    @Override
    protected String entryName() {
        return "Editing Filters";
    }

    @Override
    protected String entryDescription() {
        return "Duplicating and clearing filter configurations with ease.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.WRITABLE_BOOK);
    }

    @Override
    protected String entryId() {
        return "editing_filters";
    }
}
