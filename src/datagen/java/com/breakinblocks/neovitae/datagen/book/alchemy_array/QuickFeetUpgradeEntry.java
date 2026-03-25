package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class QuickFeetUpgradeEntry extends EntryProvider {

    public QuickFeetUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Quick Feet");
        this.pageText("Effect: Increases player movement speed up to 150%.\\\n\\\n"
                + "Trained by: Running around.\\\n\\\nMaximum level: 10");
    }

    @Override
    protected String entryName() {
        return "Quick Feet";
    }

    @Override
    protected String entryDescription() {
        return "Increased movement speed from running.";
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
