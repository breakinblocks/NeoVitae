package com.breakinblocks.neovitae.datagen.book.demon_will;

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
        this.pageText("The [#](8B0000)Tag Item Filter[#]() lets you select up to 9 items and filter by their associated "
                + "[#](8B0000)Tags[#](). Similarly to the [#](8B0000)Standard Item Filter[#](), it has a quantity selector and an "
                + "allow/deny function. Leaving the quantity blank defaults to 'all'.\\\n\\\n"
                + "For each item that you put into this filter, you can select whether to match items based on "
                + "[#](8B0000)one specific tag[#](), or [#](8B0000)any of its tags[#]().");

        this.page("recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tag Item Filter");
        this.pageText("Craft the Tag Item Filter in the Alchemy Table.\\\n\\\n"
                + "This allows you to deny/permit categories of items, so you can specify that all items with "
                + "the tag [#](8B0000)forge:ores[#]() get sent to your furnace, for example.");

        this.page("gui_image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/routing/tag_item_filter_gui.png"))
                .withTitle("Tag Item Filter GUI")
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("The GUI and the mouseover text of a configured filter.");
    }

    @Override
    protected String entryName() {
        return "Tag Item Filter";
    }

    @Override
    protected String entryDescription() {
        return "A filter that matches items by their data tags.";
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
