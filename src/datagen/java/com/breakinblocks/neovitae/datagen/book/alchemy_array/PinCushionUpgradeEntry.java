package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookLivingUpgradeTablePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class PinCushionUpgradeEntry extends EntryProvider {

    public PinCushionUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookLivingUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Pin Cushion");
        this.pageText("Effect: Offers protection from arrows.\\\n\\\n"
                + "Trained by: Being shot.\\\n\\\nMaximum level: 10");
    }

    @Override
    protected String entryName() {
        return "Pin Cushion";
    }

    @Override
    protected String entryDescription() {
        return "Arrow protection from being shot.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.ARROW);
    }

    @Override
    protected String entryId() {
        return "upgrade_pin_cushion";
    }
}
