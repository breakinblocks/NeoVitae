package com.breakinblocks.neovitae.datagen.book.spiritus;

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
        this.pageText("The [#](8B0000)Composite Item Filter[#]() is inert on its own -- a blank canvas awaiting "
                + "instruction. Combine it with another filter type in the [#](8B0000)Tabula Vitae[#]() to layer "
                + "that filter's rules onto this one.\\\n\\\n"
                + "This allows you to weave complex conditions: use the Enchantment Filter's 'any enchantment' "
                + "rule alongside the Tag Filter's 'forge:swords' to permit only enchanted blades through.");

        this.page("details", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Quantity limits and Allow/Deny modes function as with the Standard Filter. Additional "
                + "controls appear in the interface as you layer more filter types onto the composite.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Composite Item Filter");
        this.pageText("Combine a Tag, Enchantment, or Mod Filter with the Composite in the [#](8B0000)Tabula Vitae[#]() "
                + "to merge their logic together.");
    }

    @Override
    protected String entryName() {
        return "Composite Item Filter";
    }

    @Override
    protected String entryDescription() {
        return "A layered filter that weaves multiple sorting rules into one.";
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
