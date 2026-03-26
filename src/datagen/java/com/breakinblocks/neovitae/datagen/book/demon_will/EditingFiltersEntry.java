package com.breakinblocks.neovitae.datagen.book.demon_will;

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
        this.pageText("If you are configuring a large number of [#](8B0000)filters[#](), or want to expand your existing "
                + "filter setups, simply place between two and 9 filters into a crafting table to copy the "
                + "filter in slot one onto all the other filters.");

        this.page("copying", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Copying Filters");
        this.pageText("Place a configured filter in slot 1 of a crafting table, then place empty filters of "
                + "the same type in any other slots. The crafting result will be copies of the configured "
                + "filter with all settings duplicated.");

        this.page("clearing", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Similarly, if you want to clear a filter, place it in the crafting table on its own.");

        this.page("clearing_details", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Clearing Filters");
        this.pageText("Place a single configured filter in a crafting table by itself. The result will be a "
                + "blank filter with all settings reset to default.");
    }

    @Override
    protected String entryName() {
        return "Editing Filters";
    }

    @Override
    protected String entryDescription() {
        return "Copying and clearing filter configurations.";
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
