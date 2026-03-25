package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class PoisonResistanceUpgradeEntry extends EntryProvider {

    public PoisonResistanceUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Poison Resistance");
        this.pageText("Effect: Cures Poison. Has a cooldown which shortens with additional levels.\\\n\\\n"
                + "Trained by: Being Poisoned.\\\n\\\nMaximum level: 5");
    }

    @Override
    protected String entryName() {
        return "Poison Resistance";
    }

    @Override
    protected String entryDescription() {
        return "Poison cure from being poisoned.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.MILK_BUCKET);
    }

    @Override
    protected String entryId() {
        return "upgrade_poison_resistance";
    }
}
