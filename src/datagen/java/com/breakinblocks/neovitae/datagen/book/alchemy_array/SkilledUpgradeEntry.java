package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientUpgradeTablePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class SkilledUpgradeEntry extends EntryProvider {

    public SkilledUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookSentientUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Skilled");
        this.pageText("The armor sharpens your fortune, tilting fate itself in your favor. Grants "
                + "+2 [#](4A0080)Luck[#]() per level, up to +10, improving the loot you pull from "
                + "chests and the fish you draw from the water.\\\n\\\n"
                + "[#](B8860B)Trained by[#](): Never taught through deeds. Apply a [#](8B0000)Skilled "
                + "Tome[#]() directly to your worn Sentient set; find them in the loot of "
                + "[#](8B0000)The Mines[#]() or extract them from another chestplate.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 5");
    }

    @Override
    protected String entryName() {
        return "Skilled";
    }

    @Override
    protected String entryDescription() {
        return "Fortune bends toward the armor's chosen bearer.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.RABBIT_FOOT);
    }

    @Override
    protected String entryId() {
        return "upgrade_skilled";
    }
}
