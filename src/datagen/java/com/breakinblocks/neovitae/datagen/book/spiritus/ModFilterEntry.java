package com.breakinblocks.neovitae.datagen.book.spiritus;

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
        this.pageText("The [#](8B0000)Mod Item Filter[#]() accepts up to 9 representative items. For each item you "
                + "place within it, [#](B8860B)every item from the same origin[#]() is matched, allowing you to "
                + "permit or deny entire collections at once.\\\n\\\n"
                + "Quantity limits and Allow/Deny modes function as with the Standard Filter.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Mod Item Filter");
        this.pageText("Particularly useful for directing all artifacts of a given discipline into their own "
                + "dedicated vault, ensuring your [#](4A0080)Vitaemantic[#]() instruments never mingle with "
                + "lesser materials.");
    }

    @Override
    protected String entryName() {
        return "Mod Item Filter";
    }

    @Override
    protected String entryDescription() {
        return "A broad filter that matches all items sharing a common origin.";
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
