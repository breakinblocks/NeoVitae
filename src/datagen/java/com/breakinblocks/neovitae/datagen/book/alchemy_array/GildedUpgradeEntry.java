package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class GildedUpgradeEntry extends EntryProvider {

    public GildedUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Gilded");
        this.pageText("Effect: Passivises Piglins as if you were wearing Golden Armor.\\\n\\\n"
                + "Trained by: Giving a Piglin a **Gold Ingot**. You must give it to them directly, "
                + "it cannot be dropped on the ground.\\\n\\\nMaximum level: 1");
    }

    @Override
    protected String entryName() {
        return "Gilded";
    }

    @Override
    protected String entryDescription() {
        return "Piglins treat you as friendly.";
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
