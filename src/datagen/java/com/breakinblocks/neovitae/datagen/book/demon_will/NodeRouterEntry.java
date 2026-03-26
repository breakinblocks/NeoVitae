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
        this.pageText("The [#](8B0000)Node Router[#]() is the weaver's needle that stitches your [#](8B0000)Routing Nodes[#]() into "
                + "a coherent network. Its use is straightforward -- hold sneak and right-click on one node, "
                + "then do the same to another within 16 blocks. The two are now linked.\\\n\\\n"
                + "[#](2E8B57)Right-click any other block to deselect. See the Routing Nodes entry for the full "
                + "picture.[#]()");

        this.page("recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Node Router");
        this.pageText("A slender wand of bound Will. Unremarkable in appearance, indispensable in practice.");
    }

    @Override
    protected String entryName() {
        return "Node Router";
    }

    @Override
    protected String entryDescription() {
        return "The binding wand that weaves Routing Nodes into networks.";
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
