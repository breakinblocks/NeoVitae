package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class ExperiencedUpgradeEntry extends EntryProvider {

    public ExperiencedUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Experienced");
        this.pageText("Effect: Increases XP drops from killing mobs, up to 150%.\\\n\\\n"
                + "Trained by: Collecting XP.\\\n\\\nMaximum level: 10");
    }

    @Override
    protected String entryName() {
        return "Experienced";
    }

    @Override
    protected String entryDescription() {
        return "Increased XP drops from collecting experience.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.EXPERIENCE_BOTTLE);
    }

    @Override
    protected String entryId() {
        return "upgrade_experienced";
    }
}
