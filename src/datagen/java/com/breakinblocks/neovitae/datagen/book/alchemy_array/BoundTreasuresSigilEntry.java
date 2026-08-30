package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class BoundTreasuresSigilEntry extends EntryProvider {

    public BoundTreasuresSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sigil of Bound Treasures");
        this.pageText("The [#](8B0000)Sigil of Bound Treasures[#]() links to a container and allows "
                + "you to access its contents from anywhere. [#](8B0000)Shift right-click[#]() a chest, barrel, "
                + "or any other container to bind it to the sigil. Then [#](8B0000)right-click[#]() in the air "
                + "to open the linked container remotely at a cost of [#](4A0080)200 EV[#]().\\\n\\\n"
                + "The linked container must be in a loaded chunk to be accessed. If the container is "
                + "destroyed, the link is broken.");

        this.page("crafting", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crafting");
        this.pageText("Inscribe an [#](8B0000)Alchemy Array[#]() with an [#](4A0080)Eye of Ender[#]() as the "
                + "base and a [#](4A0080)Tabula Animata[#]() as the catalyst.");
    }

    @Override
    protected String entryName() {
        return "Sigil of Bound Treasures";
    }

    @Override
    protected String entryDescription() {
        return "Access a linked container from anywhere in the world.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_BOUND_TREASURES.get());
    }

    @Override
    protected String entryId() {
        return "sigil_bound_treasures";
    }
}
