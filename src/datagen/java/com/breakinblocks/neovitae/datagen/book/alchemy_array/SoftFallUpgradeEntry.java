package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientUpgradeTablePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class SoftFallUpgradeEntry extends EntryProvider {

    public SoftFallUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookSentientUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Soft Fall");
        this.pageText("The armour absorbs the shock of descent, cushioning your landing with increasing "
                + "skill. At its peak, fall damage is negated entirely.\\\n\\\n"
                + "[#](B8860B)Trained by[#](): Suffering fall damage.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 5");
    }

    @Override
    protected String entryName() {
        return "Soft Fall";
    }

    @Override
    protected String entryDescription() {
        return "The armour learns to absorb impact; the ground softens beneath you.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.FEATHER);
    }

    @Override
    protected String entryId() {
        return "upgrade_soft_fall";
    }
}
