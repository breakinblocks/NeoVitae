package com.breakinblocks.neovitae.datagen.book.demon_will;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class ModFilterEntry extends EntryProvider {

    public ModFilterEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Mod Item Filter");
        this.pageText("The [#](8B0000)Mod Item Filter[#]() lets you select up to 9 items from different mods. Similarly to "
                + "the [#](8B0000)Standard Item Filter[#](), it has a quantity selector and an allow/deny function. Leaving "
                + "the quantity blank defaults to 'all'.\\\n\\\n"
                + "For each item that you put into this filter, [#](8B0000)any item from the same mod[#]() will be matched.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Mod Item Filter");
        this.pageText("Craft the Mod Item Filter in the Tabula Vitae.\\\n\\\n"
                + "This allows you to deny/permit entire swathes of items. Handy for sorting all of your "
                + "Neo Vitae items into their own super-special chest, to name an example at random.");
    }

    @Override
    protected String entryName() {
        return "Mod Item Filter";
    }

    @Override
    protected String entryDescription() {
        return "A filter that matches all items from a specific mod.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.ITEM_MOD_FILTER.get());
    }

    @Override
    protected String entryId() {
        return "mod_filter";
    }
}
