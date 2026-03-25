package com.breakinblocks.neovitae.datagen.book.demon_will;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class CompositeFilterEntry extends EntryProvider {

    public CompositeFilterEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Composite Item Filter");
        this.pageText("The **Composite Item Filter** does nothing on its own, but when combined with another type "
                + "of **Filter**, it allows you to apply that filter's rules to it.\\\n\\\n"
                + "This means you can, for example, use the Enchantment Filter's 'Any Enchantments' and the "
                + "Tag Filter's 'forge:swords' to only allow enchanted swords to pass through.");

        this.page("details", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Similarly to the **Standard Item Filter**, it has a quantity selector and an allow/deny "
                + "function. Leaving the quantity blank defaults to 'all'. Other buttons will appear on the GUI "
                + "as you combine it with other filters.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Composite Item Filter");
        this.pageText("Craft the Composite Item Filter in the Alchemy Table.\\\n\\\n"
                + "You can add a Tag Filter, Enchantment Filter, or Mod Filter to a Composite Filter by "
                + "combining them in the Alchemy Table.");
    }

    @Override
    protected String entryName() {
        return "Composite Item Filter";
    }

    @Override
    protected String entryDescription() {
        return "A filter that combines rules from multiple filter types.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.ITEM_COMPOSITE_FILTER.get());
    }

    @Override
    protected String entryId() {
        return "composite_filter";
    }
}
