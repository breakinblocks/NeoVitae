package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class MimicsEntry extends EntryProvider {

    public MimicsEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ethereal Mimics");
        this.pageText("[#](8B0000)Ethereal Mimics[#]() are blocks of a curious [#](4A0080)liminal[#]() nature. "
                + "They adopt the appearance of any block presented to them, yet remain completely "
                + "intangible, as though they exist only partially in our reality. This makes them "
                + "ideal for concealing pitfall traps, hidden passages, and secret chambers.\\\n\\\n"
                + "You may encounter them within the [#](8B0000)Endless Realm[#](), where they were "
                + "clearly put to devious use.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Right-click a placed Ethereal Mimic with any block to assign it that "
                + "block's appearance. It will look solid to the eye but offer no resistance to passage.[#]()\\\n\\\n"
                + "Consult JEI for the crafting recipe.");
    }

    @Override
    protected String entryName() {
        return "Ethereal Mimics";
    }

    @Override
    protected String entryDescription() {
        return "Phantom blocks that wear the faces of others.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.ETHEREAL_MIMIC.asItem());
    }

    @Override
    protected String entryId() {
        return "mimics";
    }
}
