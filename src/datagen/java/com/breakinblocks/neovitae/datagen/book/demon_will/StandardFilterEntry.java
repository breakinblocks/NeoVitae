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
        this.pageText("The Standard Item Filter lets you select up to 9 items to withdraw from, or insert into, "
                + "the adjacent inventory when inserted into a **Routing Node**.\n\n"
                + "Each item has a quantity - leaving this blank will default to 'all'.\n\n"
                + "The Filter also has an Allow and Deny function. In Deny mode, quantities are ignored.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Standard Item Filter");
        this.pageText("Craft the Standard Item Filter in the Alchemy Table.\n\n"
                + "When used in an **Input Routing Node**, the quantity tells the node how many of that item "
                + "to leave in the selected inventory. Anything above this amount will be imported into the "
                + "network.");

        this.page("output_usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("When used in an **Output Routing Node**, the quantity tells the node how many of that "
                + "item to fill in the selected inventory with. Anything above this amount will be left in "
                + "the network - either passed into another valid inventory, or left where it is.");

        this.page("gui_image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/routing/standard_item_filter_gui.png"))
                .withTitle("Standard Item Filter GUI")
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("The GUI and the mouseover text of a configured filter.");
    }

    @Override
    protected String entryName() {
        return "Standard Item Filter";
    }

    @Override
    protected String entryDescription() {
        return "A basic filter for selecting specific items in routing nodes.";
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
