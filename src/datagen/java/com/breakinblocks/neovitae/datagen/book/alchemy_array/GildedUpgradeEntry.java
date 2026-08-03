package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientUpgradeTablePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class GildedUpgradeEntry extends EntryProvider {

    public GildedUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookSentientUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Gilded");
        this.pageText("The armor takes on a subtle golden sheen, marking you as an ally to the Piglins "
                + "of the Nether. They regard you as they would any creature wearing gold.\\\n\\\n"
                + "[#](B8860B)Trained by[#](): Handing a [#](8B0000)Gold Ingot[#]() directly to a Piglin. "
                + "It must be given by hand, not dropped.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 1");
    }

    @Override
    protected String entryName() {
        return "Gilded";
    }

    @Override
    protected String entryDescription() {
        return "A golden sheen earns the Piglins' grudging respect.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.GOLDEN_CHESTPLATE);
    }

    @Override
    protected String entryId() {
        return "upgrade_gilded";
    }
}
