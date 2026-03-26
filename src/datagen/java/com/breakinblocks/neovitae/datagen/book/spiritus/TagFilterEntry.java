package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookImagePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class TagFilterEntry extends EntryProvider {

    public TagFilterEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tag Item Filter");
        this.pageText("The [#](8B0000)Tag Item Filter[#]() sorts items by their associated [#](B8860B)Tags[#]() -- the invisible "
                + "categories that bind similar materials together. As with the Standard Filter, it supports "
                + "up to 9 entries, quantity limits, and Allow/Deny modes.\\\n\\\n"
                + "For each item placed into this filter, you may choose to match [#](8B0000)one specific tag[#]() "
                + "or [#](8B0000)any of its tags[#]().");

        this.page("recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tag Item Filter");
        this.pageText("This enables broad categorical sorting. Specify that all items tagged as "
                + "[#](8B0000)forge:ores[#]() flow to your furnace, for instance, and never manually sort another "
                + "pebble again.");

        this.page("gui_image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/routing/tag_item_filter_gui.png"))
                .withTitle("Tag Item Filter")
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("The filter interface, showing tag selection and tooltip.");
    }

    @Override
    protected String entryName() {
        return "Tag Item Filter";
    }

    @Override
    protected String entryDescription() {
        return "Sorting items by their hidden categorical bindings.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.ITEM_TAG_FILTER.get());
    }

    @Override
    protected String entryId() {
        return "tag_filter";
    }
}
