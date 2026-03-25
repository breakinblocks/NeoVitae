package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class BodyBuilderUpgradeEntry extends EntryProvider {

    public BodyBuilderUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Body Builder");
        this.pageText("Effect: Grants Knockback Resistance and bonus Health. Caps out at 100% Resistance "
                + "and 10 half-hearts of health.\n\nTrained by: Eating food.\n\nMaximum level: 5");
    }

    @Override
    protected String entryName() {
        return "Body Builder";
    }

    @Override
    protected String entryDescription() {
        return "Knockback resistance and bonus health from eating.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.COOKED_BEEF);
    }

    @Override
    protected String entryId() {
        return "upgrade_body_builder";
    }
}
