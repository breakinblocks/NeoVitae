package com.breakinblocks.neovitae.datagen.book.demon_will;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class NodeRouterEntry extends EntryProvider {

    public NodeRouterEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Node Router");
        this.pageText("The **Node Router** is used to link Nodes together into a Network. Its functionality "
                + "is straightforward - Simply hold sneak and right-click while aiming at a node, then do "
                + "the same to another node within 16 blocks. These two nodes are now linked. If you want "
                + "to de-select a node, right-click on any other block. See the entry on **Routing Nodes** "
                + "for more information.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Node Router");
        this.pageText("Craft the Node Router in the Hellfire Forge.\n\n*A slightly magical stick.*");
    }

    @Override
    protected String entryName() {
        return "Node Router";
    }

    @Override
    protected String entryDescription() {
        return "A tool for linking routing nodes into networks.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.NODE_ROUTER.get());
    }

    @Override
    protected String entryId() {
        return "node_router";
    }
}
