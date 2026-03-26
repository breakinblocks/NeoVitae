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

public class StandardFilterEntry extends EntryProvider {

    public StandardFilterEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Standard Item Filter");
        this.pageText("The [#](8B0000)Standard Item Filter[#]() allows you to designate up to 9 specific items for a "
                + "[#](8B0000)Routing Node[#]() to interact with. Each slot accepts a quantity -- leave it blank to "
                + "default to 'all.'\\\n\\\n"
                + "The filter also toggles between [#](B8860B)Allow[#]() and [#](B8860B)Deny[#]() modes. In Deny mode, quantities "
                + "are ignored.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Standard Item Filter");
        this.pageText("When installed in an [#](8B0000)Input Routing Node[#](), the quantity determines how many of each "
                + "item to leave behind in the adjacent inventory. Anything above that threshold is drawn into "
                + "the network.");

        this.page("output_usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("When installed in an [#](8B0000)Output Routing Node[#](), the quantity sets how many of each item "
                + "to fill into the adjacent inventory. Surplus remains in the network -- passed onward to "
                + "another valid destination, or held in place.");

        this.page("gui_image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/routing/standard_item_filter_gui.png"))
                .withTitle("Standard Item Filter")
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("The filter interface, showing a configured layout and its tooltip.");
    }

    @Override
    protected String entryName() {
        return "Standard Item Filter";
    }

    @Override
    protected String entryDescription() {
        return "Precise control over which items flow through your network.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.ITEM_ROUTER_FILTER.get());
    }

    @Override
    protected String entryId() {
        return "standard_filter";
    }
}
