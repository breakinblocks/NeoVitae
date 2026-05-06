package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientUpgradeTablePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class QuickFeetUpgradeEntry extends EntryProvider {

    public QuickFeetUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookSentientUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Quick Feet");
        this.pageText("The armour attunes itself to the cadence of your stride, propelling you ever faster. "
                + "Increases movement speed up to 150%%.\\\n\\\n"
                + "[#](B8860B)Trained by[#](): Running.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 10");
    }

    @Override
    protected String entryName() {
        return "Quick Feet";
    }

    @Override
    protected String entryDescription() {
        return "The armour quickens your step with every mile traveled.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SUGAR);
    }

    @Override
    protected String entryId() {
        return "upgrade_quick_feet";
    }
}
