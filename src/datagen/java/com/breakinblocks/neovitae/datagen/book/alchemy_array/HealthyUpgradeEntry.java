package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class HealthyUpgradeEntry extends EntryProvider {

    public HealthyUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Healthy");
        this.pageText("Effect: Grants additional health, up to 50 half-hearts.\n\n"
                + "Trained by: Restoring health (ordinary healing, or via **Potions of Healing** "
                + "or **Potions of Regeneration**).\n\nMaximum level: 10");
    }

    @Override
    protected String entryName() {
        return "Healthy";
    }

    @Override
    protected String entryDescription() {
        return "Additional health from healing.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.GOLDEN_APPLE);
    }

    @Override
    protected String entryId() {
        return "upgrade_healthy";
    }
}
