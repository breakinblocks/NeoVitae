package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientUpgradeTablePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class DwarvenMightUpgradeEntry extends EntryProvider {

    public DwarvenMightUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookSentientUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Dwarven Might");
        this.pageText("The armor recognizes the rhythm of the pick. Mining identical blocks in succession "
                + "accelerates your speed, and at higher levels a surge of [#](4A0080)Haste[#]() follows each "
                + "broken block.\\\n\\\n"
                + "[#](B8860B)Trained by[#](): Mining blocks.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 10");
    }

    @Override
    protected String entryName() {
        return "Dwarven Might";
    }

    @Override
    protected String entryDescription() {
        return "The deeper you dig, the faster the armor drives your arms.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.DIAMOND_PICKAXE);
    }

    @Override
    protected String entryId() {
        return "upgrade_dwarven_might";
    }
}
